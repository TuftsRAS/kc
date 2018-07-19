/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.common.api.document.impl;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.api.document.DocumentWorkflowUserDetails;
import org.kuali.coeus.common.api.document.DocumentWorkloadDetails;
import org.kuali.coeus.common.api.document.service.KewDocHeaderDao;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentConstants;
import org.kuali.rice.core.api.criteria.OrderByField;
import org.kuali.rice.core.api.criteria.OrderDirection;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.docsearch.service.DocumentSearchService;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository("kewDocHeaderDao")
public class KewDocHeaderDaoImpl extends LookupDaoOjb implements KewDocHeaderDao {

    private static final String PRINCIPAL_ID = "principalId";
    private static final String DOCUMENT_NUMBER = "documentNumber";
    private static final String STEPS = "steps";

    private static final String AWARD_DOCUMENT = "AwardDocument";
    private static final String NEGOTIATION_DOCUMENT = "NegotiationDocument";
    private static final String SUBAWARD_DOCUMENT = "SubawardDocument";
    private static final String INSTITUTIONAL_PROPOSAL_DOCUMENT = "InstitutionalProposalDocument";
    private static final String AWARD_BUDGET_DOCUMENT = "AwardBudgetDocument";
    private static final String CURRENT_PEOPLE_FLOW_STOP = "currentPeopleFlowStop";

    @Autowired
    @Qualifier("documentSearchService")
    private DocumentSearchService documentSearchService;

    @Autowired
    @Qualifier("dataObjectService")
    private DataObjectService dataObjectService;

    @Override
    public List<DocumentSearchResult> getEnrouteProposalDocs(String user, Integer limit, Integer skip) {
        DocumentSearchCriteria.Builder builder = DocumentSearchCriteria.Builder.create();
        builder.setDocumentStatuses(Arrays.asList(DocumentStatus.ENROUTE));
        if (!Objects.isNull(limit)) {
            builder.setMaxResults(limit);
        }
        if (!Objects.isNull(skip)) {
            builder.setStartAtIndex(skip);
        }
        builder.setDocumentTypeName(ProposalDevelopmentConstants.KewConstants.PROPOSAL_DEVELOPMENT_DOCUMENT);
        return documentSearchService.lookupDocuments(user, builder.build()).getSearchResults();
    }

    public List<DocumentSearchResult> getSavedDocuments(String user, String documentTypeFilter, Integer limit, Integer skip) {
        DocumentSearchCriteria.Builder builder = DocumentSearchCriteria.Builder.create();
        if (StringUtils.isNotEmpty(documentTypeFilter)) {
            builder.setDocumentTypeName(documentTypeFilter);
        } else {
            builder.setDocumentTypeName(AWARD_DOCUMENT);
            List<String> additionalDocTypes = new ArrayList<>();
            additionalDocTypes.add(NEGOTIATION_DOCUMENT);
            additionalDocTypes.add(SUBAWARD_DOCUMENT);
            additionalDocTypes.add(INSTITUTIONAL_PROPOSAL_DOCUMENT);
            additionalDocTypes.add(AWARD_BUDGET_DOCUMENT);
            builder.setAdditionalDocumentTypeNames(additionalDocTypes);
        }
        builder.setDocumentStatuses(Arrays.asList(DocumentStatus.SAVED));
        builder.setViewerPrincipalId(user);
        if (!Objects.isNull(limit)) {
            builder.setMaxResults(limit);
        }
        if (!Objects.isNull(skip)) {
            builder.setStartAtIndex(skip);
        }
        return documentSearchService.lookupDocuments(user, builder.build()).getSearchResults();
    }


    public List<DocumentWorkflowUserDetails> getWorkflowDetailsOfEnrouteProposalsForUser(String user, Integer limit, Integer skip) {
        Map<String,String> queryMap = new HashMap<>();
        queryMap.put(PRINCIPAL_ID, user);
        QueryByCriteria.Builder query = QueryByCriteria.Builder.andAttributes(queryMap);
        List<OrderByField> orderByFields = new ArrayList<>();
        orderByFields.add(OrderByField.Builder.create(STEPS, OrderDirection.ASCENDING).build());
        query.setOrderByFields(orderByFields);
        return getDocumentWorkflowUserDetails(limit, skip, query);
    }

    public List<DocumentWorkloadDetails> getProposalsInWorkloadStop(String stopNumber, Integer limit, Integer skip) {
        Map<String,String> queryMap = new HashMap<>();
        queryMap.put(CURRENT_PEOPLE_FLOW_STOP, stopNumber);
        QueryByCriteria.Builder query = QueryByCriteria.Builder.andAttributes(queryMap);
        List<OrderByField> orderByFields = new ArrayList<>();
        orderByFields.add(OrderByField.Builder.create(DOCUMENT_NUMBER, OrderDirection.DESCENDING ).build());
        query.setOrderByFields(orderByFields);
        query = addOrderByAndPagingFields(limit, skip, query);
        return dataObjectService.findMatching(DocumentWorkloadDetails.class, query.build()).getResults();
    }

    public List<DocumentWorkflowUserDetails> getDocumentWorkflowUserDetails(Integer limit, Integer skip, QueryByCriteria.Builder  query) {
        query = addOrderByAndPagingFields(limit, skip, query);
        return dataObjectService.findMatching(DocumentWorkflowUserDetails.class, query.build()).getResults();
    }

    public QueryByCriteria.Builder addOrderByAndPagingFields(Integer limit, Integer skip, QueryByCriteria.Builder  query) {
        if (limit != null) {
            query.setMaxResults(limit);
        }
        if (skip != null && skip > 0) {
            query.setStartAtIndex(skip);
        }
        return query;
    }

}
