/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.irb.onlinereview.authorization;

import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.coeus.sys.framework.workflow.KcWorkflowService;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.irb.ProtocolOnlineReviewDocument;
import org.kuali.kra.irb.onlinereview.ProtocolOnlineReview;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

/**
 * The Modify Protocol Online Review Authorizer checks to see if the user has 
 * permission to modify a protocol online review. 
 * 
 * Authorization depends on the users role.  If the user is a member of the IRB Administrator
 * role for the related protocol's unit, then that user is always authorized to alter the protocol online review.
 * If the user is the online reviewer, then they are allowed to edit the document provided that
 * they have an outstanding approve request on the document.
 *  
 */
public class ModifyProtocolOnlineReviewAuthorizer extends ProtocolOnlineReviewAuthorizer {

    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(ModifyProtocolOnlineReviewAuthorizer.class);

    private KcWorkflowService kraWorkflowService;

    @Override
    public boolean isAuthorized(String userId, ProtocolOnlineReviewTask task) {
        boolean hasPermission = true;
        ProtocolOnlineReview protocolOnlineReview = task.getProtocolOnlineReview();
        ProtocolOnlineReviewDocument protocolDoc = null;
        try {
            protocolDoc = (ProtocolOnlineReviewDocument) KcServiceLocator.getService(DocumentService.class).getByDocumentHeaderId(protocolOnlineReview.getProtocolOnlineReviewDocument().getDocumentNumber());
        }
        catch (WorkflowException e) {
            LOG.error(String.format("Could not find ProtocolOnlineReview, document number %s",protocolOnlineReview.getProtocolOnlineReviewDocument().getDocumentNumber()));
            return false;
        }
        
        if ( protocolOnlineReview.getProtocolOnlineReviewId() == null ) {
            //we never authorize edits on a review, the reviews are created
            //by the system on behalf of the users.
            return false;
        } else {
            
            hasPermission = !protocolOnlineReview.getProtocolOnlineReviewDocument().isViewOnly() 
                            && ((hasPermission(userId, protocolOnlineReview, PermissionConstants.MAINTAIN_ONLINE_REVIEWS)
                                    && kraWorkflowService.isEnRoute(protocolDoc))
                                || (hasPermission(userId, protocolOnlineReview, PermissionConstants.MAINTAIN_PROTOCOL_ONLINE_REVIEW_COMMENTS)
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
