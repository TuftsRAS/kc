/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.actions.submit;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.protocol.ProtocolDocumentBase;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public abstract class ProtocolSubmitActionEventBase  extends KcDocumentEventBase {
    
    private ProtocolSubmitAction submitAction;
    
    public ProtocolSubmitActionEventBase(ProtocolDocumentBase document, ProtocolSubmitAction submitAction) {
        super("Submitting for review for document " + getDocumentId(document), "", document);
        this.submitAction = submitAction;
        logEvent();
    }

    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (this.submitAction == null) {
            logMessage.append("null submitAction");
        }
        else {
            logMessage.append(this.submitAction.toString());
        }

        getLOGHook().debug(logMessage);
    }

    protected abstract org.apache.logging.log4j.Logger getLOGHook();

    
    
    @Override
    public Class getRuleInterfaceClass() {
        return ExecuteProtocolSubmitActionRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((ExecuteProtocolSubmitActionRule) rule).processSubmitAction((ProtocolDocumentBase) getDocument(), submitAction);
    }
}
