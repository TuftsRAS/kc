/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.negotiations.rules;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.negotiations.bo.NegotiationActivity;
import org.kuali.kra.negotiations.bo.NegotiationActivityAttachment;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * 
 * Event class to use when validating new negotiation activity attachments.
 */
public class NegotiationActivityAttachmentAddRuleEvent extends KcDocumentEventBase {

    private static final Logger LOG = LogManager.getLogger(NegotiationActivityAttachmentAddRuleEvent.class);
    
    private NegotiationActivity activity;
    private NegotiationActivityAttachment newAttachment;
    
    /**
     * 
     * Constructs a NegotiationActivityAttachmentAddRuleEvent.java.
     * @param description
     * @param errorPathPrefix
     * @param document
     * @param activity
     * @param newAttachment
     */
    public NegotiationActivityAttachmentAddRuleEvent(String description, String errorPathPrefix, Document document, NegotiationActivity activity, NegotiationActivityAttachment newAttachment) {
        super(description, errorPathPrefix, document);
        this.activity = activity;
        this.newAttachment = newAttachment;
    }
    
    @Override
    public Class<NegotiationActivityAddRule> getRuleInterfaceClass() {
        return NegotiationActivityAddRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((NegotiationActivityAttachmentAddRule) rule).processAddAttachmentRule(this);
    }

    @Override
    protected void logEvent() {
        LOG.info("Logging NegotiationActivityAttachmentAddRuleEvent");
    }

    public NegotiationActivity getActivity() {
        return activity;
    }

    public void setActivity(NegotiationActivity activity) {
        this.activity = activity;
    }

    public NegotiationActivityAttachment getNewAttachment() {
        return newAttachment;
    }

    public void setNewAttachment(NegotiationActivityAttachment newAttachment) {
        this.newAttachment = newAttachment;
    }
}
