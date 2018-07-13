/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.impl.workflow;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.workflow.KcDocumentRejectionService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.doctype.RoutePath;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kew.api.exception.InvalidActionTakenException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.service.RouteHeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("kcDocumentRejectionService")
public class KcDocumentRejectionServiceImpl implements KcDocumentRejectionService {

    private static final Logger LOG = LogManager.getLogger(KcDocumentRejectionServiceImpl.class);

    @Autowired
    @Qualifier("routeHeaderService")
    private RouteHeaderService routeHeaderService;


    @Autowired
    @Qualifier("documentTypeService")
    private DocumentTypeService documentTypeService;

    @Autowired
    @Qualifier("kewWorkflowDocumentService")
    private WorkflowDocumentService workflowDocumentService;

    @Override
    public void reject(WorkflowDocument document, String reason, String principalId, String appDocStatus) {
        reject(document, reason, principalId, appDocStatus, getWorkflowInitialNodeName(document.getDocumentTypeName()));
    }

    @Override
    public void reject(WorkflowDocument document, String reason, String principalId,  String appDocStatus, String nodeName) {
        if( LOG.isDebugEnabled() ) {
            LOG.debug( String.format( "Returning document %s to node %s as %s with reason '%s'", document, nodeName, principalId, reason ));
        }

        document.returnToPreviousNode(reason, nodeName);
        try {
            DocumentRouteHeaderValue routeHeader = routeHeaderService.getRouteHeader(document.getDocumentId());
            routeHeader.markDocumentSaved();
            routeHeaderService.saveRouteHeader(routeHeader);
        } catch (InvalidActionTakenException e) {
           throw new RuntimeException(e);
        }
        if (appDocStatus != null) {
            if( LOG.isDebugEnabled() ) {
                LOG.debug( String.format( "Setting application document status of document %s to %s", document.getDocumentId(), appDocStatus));
            }
            document.setApplicationDocumentStatus(appDocStatus);
        }
    }

    @Override
    public boolean isDocumentOnInitialNode(WorkflowDocument document)  {
        boolean ret = false;
        if (document != null) {
            ret = isDocumentOnNode(document.getDocumentId(), getWorkflowInitialNodeName(document.getDocumentTypeName()));
        }
        return ret;
    }

    protected boolean isDocumentOnNode(String documentNumber,String nodeName) {
        if (StringUtils.isNotBlank(documentNumber) && StringUtils.isNotBlank(nodeName)) {
            final Collection<String> currentRouteNodeNames = workflowDocumentService.getActiveRouteNodeNames(documentNumber);
            return currentRouteNodeNames.contains(nodeName);
        }

        return false;
    }

    @Override
    public String getWorkflowInitialNodeName(String docTypeName) {
        final RoutePath path = documentTypeService.getRoutePathForDocumentTypeName(docTypeName);
        return path.getPrimaryProcess().getInitialRouteNode().getName();
    }

    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    public DocumentTypeService getDocumentTypeService() {
        return documentTypeService;
    }

    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }
}
