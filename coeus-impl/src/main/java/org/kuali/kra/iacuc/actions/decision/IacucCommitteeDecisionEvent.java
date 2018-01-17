/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.iacuc.actions.decision;

import org.apache.commons.logging.Log;
import org.kuali.kra.iacuc.IacucProtocolDocument;
import org.kuali.kra.protocol.actions.decision.CommitteeDecisionEventBase;


public class IacucCommitteeDecisionEvent extends CommitteeDecisionEventBase<IacucCommitteeDecision> {
    
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(IacucCommitteeDecisionEvent.class);

    public IacucCommitteeDecisionEvent(IacucProtocolDocument document, IacucCommitteeDecision decision) {
        super(document, decision);
    }
    
    @Override
    protected Log getLOGHook() {
        return LOG;
    }
    
}
