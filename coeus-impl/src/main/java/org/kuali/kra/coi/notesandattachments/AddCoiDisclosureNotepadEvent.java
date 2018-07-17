/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.coi.notesandattachments;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.coi.CoiDisclosureDocument;
import org.kuali.kra.coi.notesandattachments.notes.CoiDisclosureNotepad;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class AddCoiDisclosureNotepadEvent extends KcDocumentEventBase {

    private static final Logger LOG = LogManager.getLogger(AddCoiDisclosureNotepadEvent.class);
    private final CoiDisclosureNotepad newCoiDisclosureNotepad;
    
    /**
     * Creates a new event.
     * @param document the document.
     * @param newProtocolNotepad the new attachment to be added.
     */
    public AddCoiDisclosureNotepadEvent(final CoiDisclosureDocument document,
        final CoiDisclosureNotepad newCoiDisclosureNotepad) {
        super("adding new coi disclosure notepad", "coiNotesAndAttachmentsHelper", document);
        
        if (document == null) {
            throw new IllegalArgumentException("the document is null");
        }
        
        if (newCoiDisclosureNotepad == null) {
            throw new IllegalArgumentException("the newCoiDisclosureNotepad is null");
        }
        
        this.newCoiDisclosureNotepad = newCoiDisclosureNotepad;
    }

    @Override
    protected void logEvent() {
        LOG.debug("adding new: " + this.newCoiDisclosureNotepad + " on doc # " + this.getDocument().getDocumentNumber());
    }

    @Override
    public Class<AddCoiDisclosureNotepadRule> getRuleInterfaceClass() {
        return AddCoiDisclosureNotepadRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return this.getRuleInterfaceClass().cast(rule).processAddCoiDisclosureNotepadRules(this);
    }

    
    public CoiDisclosureNotepad getNewCoiDisclosureNotepad() {
        return this.newCoiDisclosureNotepad;
    }
}
