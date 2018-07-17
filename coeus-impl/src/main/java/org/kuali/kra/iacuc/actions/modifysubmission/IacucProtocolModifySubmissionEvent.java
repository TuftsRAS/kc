/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.iacuc.actions.modifysubmission;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.protocol.ProtocolDocumentBase;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class IacucProtocolModifySubmissionEvent extends KcDocumentEventBase {
    private  IacucProtocolModifySubmissionBean actionBean;
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(IacucProtocolModifySubmissionEvent.class);

    public IacucProtocolModifySubmissionEvent(ProtocolDocumentBase document, IacucProtocolModifySubmissionBean actionBean) {
        super("Modifying submission " + getDocumentId(document), "", document);
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
    public Class<? extends BusinessRule> getRuleInterfaceClass() {
        return IacucProtocolModifySubmissionRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((IacucProtocolModifySubmissionRule) rule).processModifySubmissionRule((ProtocolDocumentBase) getDocument(), actionBean);
    }


}
