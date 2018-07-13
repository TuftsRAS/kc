/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.krms;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.impl.workflow.KcPeopleFlowTypeServiceImpl;
import org.kuali.coeus.sys.framework.model.KcTransactionalDocumentBase;
import org.kuali.kra.kim.bo.KcKimAttributes;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContent;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("advancedPeopleFlowTypeService")
public class AdvancedPeopleFlowTypeServiceImpl extends KcPeopleFlowTypeServiceImpl {
    private static final Logger LOG = LogManager.getLogger(AdvancedPeopleFlowTypeServiceImpl.class);

    @Autowired
    @Qualifier("documentService")
    private DocumentService documentService;
    
    @Override
    public Map<String, String> resolveRoleQualifiers(String kewTypeId, String roleId, Document document, DocumentContent documentContent) {
        Map<String, String> qualifiers = new HashMap<>();
        org.kuali.rice.krad.document.Document kradDocument = null;

        try {
            kradDocument = getDocumentService().getByDocumentHeaderId(document.getDocumentId());
        } catch (WorkflowException e) {
            LOG.error(e.getMessage(), e);
        }

        if (kradDocument != null && kradDocument instanceof KcTransactionalDocumentBase) {
            qualifiers = ((KcTransactionalDocumentBase) kradDocument).getKrmsRoleQualifiers();
        }

        qualifiers.put(KcKimAttributes.DOCUMENT_NUMBER, document.getDocumentId());
        return qualifiers;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
