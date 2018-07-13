/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.irb.noteattachment;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.irb.ProtocolDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * Event created when adding a new {@link ProtocolAttachmentProtocol ProtocolAttachmentProtocol}.
 */
class AddProtocolAttachmentProtocolEvent extends KcDocumentEventBase {

    private static final Logger LOG = LogManager.getLogger(AddProtocolAttachmentProtocolEvent.class);
    private final ProtocolAttachmentProtocol newAttachmentProtocol;
    
    /**
     * Creates a new event.
     * @param document the document.
     * @param newAttachmentProtocol the new attachment to be added.
     */
    public AddProtocolAttachmentProtocolEvent(final ProtocolDocument document,
        final ProtocolAttachmentProtocol newAttachmentProtocol) {
        super("adding new protocol attachment", "notesAttachmentsHelper", document);
        
        if (document == null) {
            throw new IllegalArgumentException("the document is null");
        }
        
        if (newAttachmentProtocol == null) {
            throw new IllegalArgumentException("the newAttachmentProtocol is null");
        }
        
        this.newAttachmentProtocol = newAttachmentProtocol;
    }

    @Override
    protected void logEvent() {
        LOG.debug("adding new: " + this.newAttachmentProtocol + " on doc # " + this.getDocument().getDocumentNumber());
    }

    @Override
    public Class<AddProtocolAttachmentProtocolRule> getRuleInterfaceClass() {
        return AddProtocolAttachmentProtocolRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return this.getRuleInterfaceClass().cast(rule).processAddProtocolAttachmentProtocolRules(this);
    }

    /**
     * Gets new {@link ProtocolAttachmentProtocol ProtocolAttachmentProtocol}.
     * @return new {@link ProtocolAttachmentProtocol ProtocolAttachmentProtocol}.
     */
    public ProtocolAttachmentProtocol getNewAttachmentProtocol() {
        return this.newAttachmentProtocol;
    }
}
