/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.actions.abandon;

import org.kuali.coeus.common.framework.print.PrintingException;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.actions.ProtocolActionBase;
import org.kuali.kra.protocol.actions.correspondence.ProtocolActionCorrespondenceGenerationService;
import org.kuali.kra.protocol.actions.correspondence.ProtocolActionsCorrespondenceBase;
import org.kuali.kra.protocol.actions.genericactions.ProtocolGenericActionBean;
import org.kuali.kra.protocol.actions.submit.ProtocolActionService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

public abstract class ProtocolAbandonServiceImplBase implements ProtocolAbandonService {

    private DocumentService documentService;
    private ProtocolActionService protocolActionService;
    private ProtocolActionCorrespondenceGenerationService protocolActionCorrespondenceGenerationService;


    @Override
    public void abandonProtocol(ProtocolBase protocol, ProtocolGenericActionBean protocolAbandonBean) throws WorkflowException {

        ProtocolActionBase protocolAction = getNewActionHook(protocol); 
        protocolAction.setComments(protocolAbandonBean.getComments());
        protocol.getProtocolActions().add(protocolAction);
        protocolActionService.updateProtocolStatus(protocolAction, protocol);
        protocol.setActive(false);
        documentService.cancelDocument(protocol.getProtocolDocument(), null);
        
    }

    protected abstract String getActionType();
    
    public abstract ProtocolActionBase getNewActionHook(ProtocolBase protocol);
    
    protected abstract ProtocolActionsCorrespondenceBase getNewProtocolCorrespondenceHook(String actionType);
    
    private void createCorrespondenceAndAttach(ProtocolBase protocol, String protocolActionType) throws PrintingException {
        ProtocolActionsCorrespondenceBase correspondence = getNewProtocolCorrespondenceHook(protocolActionType);
        correspondence.setPrintableBusinessObject(protocol);
        correspondence.setProtocol(protocol);
        protocolActionCorrespondenceGenerationService.generateCorrespondenceDocumentAndAttach(correspondence);
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    
    protected DocumentService getDocumentService() {
        return this.documentService;
    }

    public void setProtocolActionService(ProtocolActionService protocolActionService) {
        this.protocolActionService = protocolActionService;
    }
    
    protected ProtocolActionService getProtocolActionService() {
        return this.protocolActionService;
    }

    public void setProtocolActionCorrespondenceGenerationService(
            ProtocolActionCorrespondenceGenerationService protocolActionCorrespondenceGenerationService) {
        this.protocolActionCorrespondenceGenerationService = protocolActionCorrespondenceGenerationService;
    }


}
