/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.actions.decision;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.protocol.ProtocolDocumentBase;
import org.kuali.rice.krad.rules.rule.BusinessRule;


public abstract class CommitteeDecisionEventBase<CD extends CommitteeDecision<?>> extends KcDocumentEventBase {
    
    private CD actionBean;
    
    /**
     * 
     * Constructs a CommitteeDecisionEventBase.java.
     * @param document
     * @param decision
     */
    public CommitteeDecisionEventBase(ProtocolDocumentBase document, CD decision) {
        super("Recording Committee Decision " + getDocumentId(document), "", document);
        this.actionBean = decision;
    }
    
    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (this.actionBean == null) {
            logMessage.append("null actionBean");
        } else {
            logMessage.append(actionBean.toString());
        }

        getLOGHook().debug(logMessage);
    }
    
    protected abstract Logger getLOGHook();

    
    @Override
    public Class<ExecuteCommitteeDecisionRule> getRuleInterfaceClass() {
        return ExecuteCommitteeDecisionRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((ExecuteCommitteeDecisionRule<CD>) rule).proccessCommitteeDecisionRule((ProtocolDocumentBase) this.getDocument(), this.actionBean);
    }

}
