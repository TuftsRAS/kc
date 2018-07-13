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
 * Event created when adding a new {@link ProtocolNotepadBase ProtocolNotepadBase}.
 */
public class AddProtocolNotepadEvent extends KcDocumentEventBase {

    private static final Logger LOG = LogManager.getLogger(AddProtocolNotepadEvent.class);
    private final ProtocolNotepadBase newProtocolNotepad;
    
    /**
     * Creates a new event.
     * @param document the document.
     * @param newProtocolNotepad the new attachment to be added.
     */
    public AddProtocolNotepadEvent(final ProtocolDocumentBase document,
        final ProtocolNotepadBase newProtocolNotepad) {
        super("adding new protocol notepad", "notesAttachmentsHelper", document);
        
        if (document == null) {
            throw new IllegalArgumentException("the document is null");
        }
        
        if (newProtocolNotepad == null) {
            throw new IllegalArgumentException("the newProtocolNotepad is null");
        }
        
        this.newProtocolNotepad = newProtocolNotepad;
    }

    @Override
    protected void logEvent() {
        LOG.debug("adding new: " + this.newProtocolNotepad + " on doc # " + this.getDocument().getDocumentNumber());
    }

    @Override
    public Class<AddProtocolNotepadRule> getRuleInterfaceClass() {
        return AddProtocolNotepadRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return this.getRuleInterfaceClass().cast(rule).processAddProtocolNotepadRules(this);
    }

    /**
     * Gets new {@link ProtocolNotepadBase ProtocolNotepadBase}.
     * @return new {@link ProtocolNotepadBase ProtocolNotepadBase}.
     */
    public ProtocolNotepadBase getNewProtocolNotepad() {
        return this.newProtocolNotepad;
    }
}
