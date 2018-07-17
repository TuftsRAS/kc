/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.iacuc.actions.followup;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.kra.iacuc.actions.IacucProtocolActionType;
import org.kuali.kra.iacuc.actions.submit.IacucValidProtocolActionAction;
import org.kuali.kra.protocol.actions.followup.FollowupActionServiceImplBase;


public class IacucFollowupActionServiceImpl extends FollowupActionServiceImplBase<IacucValidProtocolActionAction> implements IacucFollowupActionService {
 
    
    private static final Logger LOG = LogManager.getLogger(IacucFollowupActionServiceImpl.class);

    @Override
    protected Class<IacucValidProtocolActionAction> getValidProtocolActionActionClassHook() {
        return IacucValidProtocolActionAction.class;
    }

    @Override
    protected String getProtocolActionTypeCodeForRecordCommitteeDecisionHook() {
        return IacucProtocolActionType.RECORD_COMMITTEE_DECISION;
    }

    @Override
    protected Logger getLogHook() {
        return IacucFollowupActionServiceImpl.LOG;
    }
    

}
