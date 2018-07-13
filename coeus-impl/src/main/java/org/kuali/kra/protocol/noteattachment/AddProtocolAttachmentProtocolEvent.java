/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.noteattachment;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.protocol.ProtocolDocumentBase;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * Event created when adding a new {@link ProtocolAttachmentProtocolBase ProtocolAttachmentProtocolBase}.
 */
class AddProtocolAttachmentProtocolEvent extends KcDocumentEventBase {

    private static final Logger LOG = LogManager.getLogger(AddProtocolAttachmentProtocolEvent.class);
    private final ProtocolAttachmentProtocolBase newAttachmentProtocol;
    
    /**
     * Creates a new event.
     * @param document the document.
     * @param newAttachmentProtocol the new attachment to be added.
     */
    public AddProtocolAttachmentProtocolEvent(final ProtocolDocumentBase document,
        final ProtocolAttachmentProtocolBase newAttachmentProtocol) {
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
     * Gets new {@link ProtocolAttachmentProtocolBase ProtocolAttachmentProtocolBase}.
     * @return new {@link ProtocolAttachmentProtocolBase ProtocolAttachmentProtocolBase}.
     */
    public ProtocolAttachmentProtocolBase getNewAttachmentProtocol() {
        return this.newAttachmentProtocol;
    }
}
