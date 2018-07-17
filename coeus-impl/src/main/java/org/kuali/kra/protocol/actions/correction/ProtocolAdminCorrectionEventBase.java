/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.actions.correction;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.rule.KcBusinessRule;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBaseExtension;
import org.kuali.kra.protocol.ProtocolDocumentBase;

/**
 * The event that occurs when the ProtocolBase Administrator assigns reviewers to a protocol.
 */
public abstract class ProtocolAdminCorrectionEventBase extends KcDocumentEventBaseExtension {
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(ProtocolAdminCorrectionEventBase.class);
    
    private AdminCorrectionBean actionBean;
    private String propertyName;
    
    public ProtocolAdminCorrectionEventBase(ProtocolDocumentBase document, String propertyName, AdminCorrectionBean actionBean) {
        super("Administrative Correction for document " + getDocumentId(document), "", document);
        this.actionBean = actionBean;
        this.propertyName = propertyName;
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

    public ProtocolDocumentBase getProtocolDocument() {
        return (ProtocolDocumentBase) getDocument();
    }
    
    public String getPropertyName() {
        return propertyName;
    }
    
    public AdminCorrectionBean getAdminCorrectionBean() {
        return actionBean;
    }

    @Override
    public abstract KcBusinessRule getRule();

}
