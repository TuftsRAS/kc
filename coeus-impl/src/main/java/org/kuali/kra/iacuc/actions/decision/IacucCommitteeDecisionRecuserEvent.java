/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.iacuc.actions.decision;

import org.apache.logging.log4j.Logger;
import org.kuali.kra.iacuc.IacucProtocolDocument;
import org.kuali.kra.protocol.actions.decision.CommitteeDecisionRecuserEventBase;


public class IacucCommitteeDecisionRecuserEvent extends CommitteeDecisionRecuserEventBase<IacucCommitteeDecision> {
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(IacucCommitteeDecisionRecuserEvent.class);

    public IacucCommitteeDecisionRecuserEvent(IacucProtocolDocument document, IacucCommitteeDecision decision) {
        super(document, decision);
    }
    
    @Override
    protected Logger getLOGHook() {
        return LOG;
    }
    
}
