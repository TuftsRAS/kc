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
 * Event created when editing an existing {@link ProtocolNotepadBase ProtocolNotepadBase}.
 */
public class ModifyProtocolNotepadEvent extends KcDocumentEventBase {

    private static final Logger LOG = LogManager.getLogger(ModifyProtocolNotepadEvent.class);
    private final ProtocolNotepadBase existingProtocolNotepad;
    
    /**
     * Creates a new event.
     * @param document the document.
     * @param existingProtocolNotepad the new attachment to be added.
     */
    public ModifyProtocolNotepadEvent(final ProtocolDocumentBase document,
        final ProtocolNotepadBase existingProtocolNotepad) {
        super("modifying existing protocol notepad", "notesAttachmentsHelper", document);
        
        if (document == null) {
            throw new IllegalArgumentException("the document is null");
        }
        
        if (existingProtocolNotepad == null) {
            throw new IllegalArgumentException("the existingProtocolNotepad is null");
        }
        
        this.existingProtocolNotepad = existingProtocolNotepad;
    }

    @Override
    protected void logEvent() {
        LOG.debug("adding new: " + this.existingProtocolNotepad + " on doc # " + this.getDocument().getDocumentNumber());
    }

    @Override
    public Class<ModifyProtocolNotepadRule> getRuleInterfaceClass() {
        return ModifyProtocolNotepadRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return this.getRuleInterfaceClass().cast(rule).processModifyProtocolNotepadRules(this);
    }

    /**
     * Gets new {@link ProtocolNotepadBase ProtocolNotepadBase}.
     * @return new {@link ProtocolNotepadBase ProtocolNotepadBase}.
     */
    public ProtocolNotepadBase getExistingProtocolNotepad() {
        return this.existingProtocolNotepad;
    }
}
