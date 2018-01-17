/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.irb.auth;

import org.kuali.coeus.common.committee.impl.bo.CommitteeDecisionMotionType;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.irb.actions.ProtocolAction;
import org.kuali.kra.irb.actions.ProtocolActionType;
import org.kuali.kra.irb.actions.submit.ProtocolSubmission;

/**
 * Is the user allowed to return protocols for substantive revisions?
 */
public class ReturnForSRRAuthorizer extends ProtocolAuthorizer {

    @Override
    public boolean isAuthorized(String userId, ProtocolTask task) {
        ProtocolAction lastAction = task.getProtocol().getLastProtocolAction();
        ProtocolSubmission lastSubmission = task.getProtocol().getProtocolSubmission();
        
        return canPerform(lastAction, lastSubmission) && hasPermission(userId, task.getProtocol(), PermissionConstants.MAINTAIN_PROTOCOL_SUBMISSIONS);
    }
    
    private boolean canPerform(ProtocolAction lastAction, ProtocolSubmission lastSubmission) {
        boolean canPerform = false;
        
        if (lastAction != null && lastSubmission != null) {
            
            boolean normalCanPerform = ProtocolActionType.RECORD_COMMITTEE_DECISION.equals(lastAction.getProtocolActionTypeCode()) 
            && CommitteeDecisionMotionType.SUBSTANTIVE_REVISIONS_REQUIRED.equals(lastSubmission.getCommitteeDecisionMotionTypeCode());
            
            boolean exemptExpeditePerform = false;
            if (lastSubmission.getProtocolReviewType() != null){
                exemptExpeditePerform =  canPerformActionOnExpeditedOrExempt(lastSubmission, lastAction);
            }
            
            canPerform = normalCanPerform || exemptExpeditePerform;
        }
        
        return canPerform;
    }
    
    
}
