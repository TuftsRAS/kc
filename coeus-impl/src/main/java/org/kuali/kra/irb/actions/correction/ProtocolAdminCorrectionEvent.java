/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.irb.actions.correction;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.irb.ProtocolDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * The event that occurs when the IRB Administrator assigns reviewers to a protocol.
 */
public class ProtocolAdminCorrectionEvent extends KcDocumentEventBase {
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(ProtocolAdminCorrectionEvent.class);
    
    private AdminCorrectionBean actionBean;
    
    public ProtocolAdminCorrectionEvent(ProtocolDocument document, AdminCorrectionBean actionBean) {
        super("Administrative Correction for document " + getDocumentId(document), "", document);
        this.actionBean = actionBean;
        logEvent();
    }

    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        if (this.actionBean == null) {
            logMessage.append("null actionBean");
        }
        else {
            logMessage.append(actionBean.toString());
        }

        LOG.debug(logMessage);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return ExecuteProtocolAdminCorrectionRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((ExecuteProtocolAdminCorrectionRule) rule).processAdminCorrectionRule((ProtocolDocument) getDocument(), actionBean);
    }
}
