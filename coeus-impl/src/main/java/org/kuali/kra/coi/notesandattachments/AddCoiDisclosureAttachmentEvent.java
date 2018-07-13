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
import org.kuali.kra.coi.notesandattachments.attachments.CoiDisclosureAttachment;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class AddCoiDisclosureAttachmentEvent extends KcDocumentEventBase {
    private static final Logger LOG = LogManager.getLogger(AddCoiDisclosureAttachmentEvent.class);
    private final CoiDisclosureAttachment newCoiDisclosureAttachment;

    public AddCoiDisclosureAttachmentEvent(Document document, CoiDisclosureAttachment newCoiDisclosureAttachment) {
        super("adding new coi disclosure attachment", "coiNoteAndAttachmentHelper", document);
        
        if (document == null) {
            throw new IllegalArgumentException("the document is null");
        }
        
        if (newCoiDisclosureAttachment == null) {
            throw new IllegalArgumentException("the newCoiDisclosureAttachment is null");
        }
        
        this.newCoiDisclosureAttachment = newCoiDisclosureAttachment;    }

    @Override
    protected void logEvent() {
        LOG.debug("adding new: " + this.newCoiDisclosureAttachment + " on doc # " + this.getDocument().getDocumentNumber());
        
    }

    @Override
    public Class<AddCoiDisclosureAttachmentRule> getRuleInterfaceClass() {
        return AddCoiDisclosureAttachmentRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return this.getRuleInterfaceClass().cast(rule).processAddCoiDisclosureAttachmentRules(this);

    }
    
    public CoiDisclosureAttachment getNewCoiDisclosureAttachment() {
        return this.newCoiDisclosureAttachment;
    }

}
