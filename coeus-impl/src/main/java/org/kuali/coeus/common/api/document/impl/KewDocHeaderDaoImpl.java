/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.common.api.document.impl;

import org.kuali.coeus.common.api.document.service.KewDocHeaderDao;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.docsearch.service.DocumentSearchService;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@Repository("kewDocHeaderDao")
public class KewDocHeaderDaoImpl extends LookupDaoOjb implements KewDocHeaderDao {

    private static final String PROPOSAL_DEVELOPMENT_DOCUMENT = "ProposalDevelopmentDocument";

    @Autowired
    @Qualifier("documentSearchService")
    private DocumentSearchService documentSearchService;

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
        if (!Objects.isNull(limit)) {
            builder.setMaxResults(limit);
        }
        if (!Objects.isNull(skip)) {
            builder.setStartAtIndex(skip);
        }
        return documentSearchService.lookupDocuments(user, builder.build()).getSearchResults();
    }


}
