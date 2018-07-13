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
 * Event created when delete an existing {@link ProtocolNotepadBase ProtocolNotepadBase}.
 */
public class DeleteProtocolNotepadEvent extends KcDocumentEventBase {

    private static final Logger LOG = LogManager.getLogger(DeleteProtocolNotepadEvent.class);
    private final ProtocolNotepadBase protocolNotepad;
    
    /**
     * Creates a new "delete" event.
     * @param document the document.
     * @param deleteProtocolNotepad the new attachment to be deleted.
     */
    public DeleteProtocolNotepadEvent(final ProtocolDocumentBase document,
        final ProtocolNotepadBase protocolNotepad) {
        super("deleting existing protocol notepad", "notesAttachmentsHelper", document);
        
        if (document == null) {
            throw new IllegalArgumentException("the document is null");
        }
        
        if (protocolNotepad == null) {
            throw new IllegalArgumentException("the protocolNotepad is null");
        }
        
        this.protocolNotepad = protocolNotepad;
    }

    @Override
    protected void logEvent() {
        LOG.debug("adding new: " + this.protocolNotepad + " on doc # " + this.getDocument().getDocumentNumber());
    }

    @Override
    public Class<DeleteProtocolNotepadRule> getRuleInterfaceClass() {
        return DeleteProtocolNotepadRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return this.getRuleInterfaceClass().cast(rule).processDeleteProtocolNotepadRules(this);
    }

    /**
     * Gets new {@link ProtocolNotepadBase ProtocolNotepadBase}.
     * @return new {@link ProtocolNotepadBase ProtocolNotepadBase}.
     */
    public ProtocolNotepadBase getProtocolNotepad() {
        return this.protocolNotepad;
    }
}
