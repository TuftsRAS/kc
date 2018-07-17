/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.irb.actions.followup;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.kra.irb.actions.ProtocolActionType;
import org.kuali.kra.irb.actions.submit.ValidProtocolActionAction;
import org.kuali.kra.protocol.actions.followup.FollowupActionServiceImplBase;


public class FollowupActionServiceImpl extends FollowupActionServiceImplBase<ValidProtocolActionAction> implements FollowupActionService {
    
    private static final Logger LOG = LogManager.getLogger(FollowupActionServiceImpl.class);

    @Override
    protected Class<ValidProtocolActionAction> getValidProtocolActionActionClassHook() {
        return ValidProtocolActionAction.class;
    }

    @Override
    protected String getProtocolActionTypeCodeForRecordCommitteeDecisionHook() {
        return ProtocolActionType.RECORD_COMMITTEE_DECISION;
    }

    @Override
    protected Logger getLogHook() {
        return FollowupActionServiceImpl.LOG;
    }
}
