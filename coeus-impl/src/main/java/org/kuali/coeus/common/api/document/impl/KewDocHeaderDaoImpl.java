/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.common.api.document.impl;

import org.kuali.coeus.common.api.document.DocumentWorkflowUserDetails;
import org.kuali.coeus.common.api.document.service.KewDocHeaderDao;
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

    private static final String PROPOSAL_DEVELOPMENT_DOCUMENT = "ProposalDevelopmentDocument";
    private static final String PRINCIPAL_ID = "principalId";
    private static final String DOCUMENT_NUMBER = "documentNumber";

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
        builder.setDocumentTypeName(PROPOSAL_DEVELOPMENT_DOCUMENT);
        return documentSearchService.lookupDocuments(user, builder.build()).getSearchResults();
    }

    public List<DocumentSearchResult> getSavedDocuments(String user, Integer limit, Integer skip) {
        DocumentSearchCriteria.Builder builder = DocumentSearchCriteria.Builder.create();
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

    public List<DocumentWorkflowUserDetails> getWorkflowDetailsForEnrouteDocuments(String user, Integer limit, Integer skip) {

        Map<String,String> queryMap = new HashMap<>();
        queryMap.put(PRINCIPAL_ID, user);
        QueryByCriteria.Builder query = QueryByCriteria.Builder.andAttributes(queryMap);
        List<OrderByField> orderByFields = new ArrayList<>();
        orderByFields.add(OrderByField.Builder.create(DOCUMENT_NUMBER, OrderDirection.DESCENDING ).build());
        query.setOrderByFields(orderByFields);

        if (limit != null) {
            query.setMaxResults(limit);
        }
        if (skip != null && skip > 0) {
            query.setStartAtIndex(skip);
        }
        return dataObjectService.
                findMatching(DocumentWorkflowUserDetails.class, query.build()).getResults();
    }
}
