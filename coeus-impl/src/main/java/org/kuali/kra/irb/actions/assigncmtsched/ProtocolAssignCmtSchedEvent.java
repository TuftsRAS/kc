/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.irb.actions.assigncmtsched;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.irb.ProtocolDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * The event that occurs when the IRB Administrator assigns a protocol
 * to a committee/schedule.
 */
public class ProtocolAssignCmtSchedEvent extends KcDocumentEventBase {
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(ProtocolAssignCmtSchedEvent.class);
    
    private ProtocolAssignCmtSchedBean actionBean;
    
    public ProtocolAssignCmtSchedEvent(ProtocolDocument document, ProtocolAssignCmtSchedBean actionBean) {
        super("Submitting for review for document " + getDocumentId(document), "", document);
        this.actionBean = actionBean;
        logEvent();
    }

    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (this.actionBean == null) {
            logMessage.append("null actionBean");
        }
        else {
            logMessage.append(actionBean.toString());
        }

        LOG.debug(logMessage);
    }

    @Override
    public Class getRuleInterfaceClass() {
        return ExecuteProtocolAssignCmtSchedRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((ExecuteProtocolAssignCmtSchedRule) rule).processAssignToCommitteeSchedule((ProtocolDocument) getDocument(), actionBean);
    }
}
