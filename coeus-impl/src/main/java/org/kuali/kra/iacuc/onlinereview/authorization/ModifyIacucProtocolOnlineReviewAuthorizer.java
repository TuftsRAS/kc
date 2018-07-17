/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.iacuc.onlinereview.authorization;

import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.coeus.sys.framework.workflow.KcWorkflowService;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.protocol.ProtocolOnlineReviewDocumentBase;
import org.kuali.kra.protocol.onlinereview.ProtocolOnlineReviewBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

public class ModifyIacucProtocolOnlineReviewAuthorizer extends IacucProtocolOnlineReviewAuthorizer {

    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(ModifyIacucProtocolOnlineReviewAuthorizer.class);

    private KcWorkflowService kraWorkflowService;

    @Override
    public boolean isAuthorized(String userId, IacucProtocolOnlineReviewTask task) {
        boolean hasPermission = true;
        ProtocolOnlineReviewBase protocolOnlineReview = task.getProtocolOnlineReview();
        ProtocolOnlineReviewDocumentBase protocolDoc = null;
        try {
            protocolDoc = (ProtocolOnlineReviewDocumentBase) KcServiceLocator.getService(DocumentService.class).getByDocumentHeaderId(protocolOnlineReview.getProtocolOnlineReviewDocument().getDocumentNumber());
        }
        catch (WorkflowException e) {
            LOG.error(String.format("Could not find ProtocolOnlineReviewBase, document number %s",protocolOnlineReview.getProtocolOnlineReviewDocument().getDocumentNumber()));
            return false;
        }
        
        if ( protocolOnlineReview.getProtocolOnlineReviewId() == null ) {
            //we never authorize edits on a review, the reviews are created
            //by the system on behalf of the users.
            return false;
        } else {
            
            hasPermission = !protocolOnlineReview.getProtocolOnlineReviewDocument().isViewOnly() 
                            && ((hasPermission(userId, protocolOnlineReview, PermissionConstants.MAINTAIN_IACUC_ONLINE_REVIEWS)
                                    && kraWorkflowService.isEnRoute(protocolDoc))
                                || (hasPermission(userId, protocolOnlineReview, PermissionConstants.MAINTAIN_IACUC_PROTOCOL_ONLINE_REVIEW_COMMENTS)
                                    && kraWorkflowService.isUserApprovalRequested(protocolDoc, userId) )
                                   );
                          
        }
        return hasPermission;
    }

    public KcWorkflowService getKraWorkflowService() {
        return kraWorkflowService;
    }

    public void setKraWorkflowService(KcWorkflowService kraWorkflowService) {
        this.kraWorkflowService = kraWorkflowService;
    }
}
