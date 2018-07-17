/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.rule.event;

import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.notesandattachments.attachments.AwardAttachment;
import org.kuali.kra.award.rule.AddAwardAttachmentRule;
import org.kuali.rice.krad.rules.rule.BusinessRule;


public class AwardAttachmentEventBase extends KcDocumentEventBase implements AwardAttachmentEvent  {

    private AwardAttachment attachment;
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager
    .getLogger(AwardAttachmentEventBase.class);
    
    protected AwardAttachmentEventBase(String description, String errorPathPrefix, AwardDocument document, AwardAttachment attachment) {
        super(description, errorPathPrefix, document);
        this.attachment = attachment;
        logEvent();
    }
    
    protected AwardAttachmentEventBase( String description, String errorPathPrefix, AwardDocument document ) {
        super( description, errorPathPrefix, document );
        logEvent();
    }

    @Override
    protected void logEvent() {
        if( LOG.isDebugEnabled() )
            LOG.debug(getDescription());
    }

    @Override
    public AwardAttachment getAwardAttachment() {
       return attachment;
    }

    public void setAwardAttachment(AwardAttachment attachment) {
        this.attachment = attachment;
    }

    
    @Override
    public Class getRuleInterfaceClass() {
       return AddAwardAttachmentRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return false;
    }
    
}
