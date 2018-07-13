/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.irb.actions.noreview;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.irb.ProtocolDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;


public class ProtocolReviewNotRequiredEvent extends KcDocumentEventBase {
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(ProtocolReviewNotRequiredEvent.class);
    
    private ProtocolReviewNotRequiredBean actionBean;
    
    /**
     * 
     * Constructs a ProtocolReviewNotRequiredEvent.java.
     * @param document
     * @param actionBean
     */
    public ProtocolReviewNotRequiredEvent(ProtocolDocument document, ProtocolReviewNotRequiredBean actionBean) {
        super("review not required for document " + getDocumentId(document), "", document);
        this.actionBean = actionBean;
        logEvent();
    }

    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        if (this.actionBean == null) {
            logMessage.append("null actionBean");
        } else {
            logMessage.append(actionBean.toString());
        }
        LOG.debug(logMessage);
    }

    @Override
    public Class getRuleInterfaceClass() {
        return ExecuteProtocolReviewNotRequiredRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((ExecuteProtocolReviewNotRequiredRule) rule).processReviewNotRequiredRule((ProtocolDocument) getDocument(), actionBean);
    }

}
