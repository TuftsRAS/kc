/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.subaward.subawardrule.events;



import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.subaward.subawardrule.AddSubAwardAttachmentRule;
import org.kuali.kra.subaward.bo.SubAwardAttachments;
import org.kuali.kra.subaward.document.SubAwardDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class SubAwardAttachmentEventBase extends KcDocumentEventBase implements SubAwardAttachmentEvent {
    
    private SubAwardAttachments attachment;
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager
    .getLogger(SubAwardAttachmentEventBase.class);
    
    protected SubAwardAttachmentEventBase(String description, String errorPathPrefix, SubAwardDocument document, SubAwardAttachments attachment) {
        super(description, errorPathPrefix, document);
        this.attachment = attachment;
        logEvent();
    }
    
    protected SubAwardAttachmentEventBase( String description, String errorPathPrefix, SubAwardDocument document ) {
        super( description, errorPathPrefix, document );
        logEvent();
    }

    @Override
    protected void logEvent() {
        if( LOG.isDebugEnabled() )
            LOG.debug(getDescription());
    }

    @Override
    public SubAwardAttachments getSubAwardAttachments() {
       return attachment;
    }

    public void setSubAwardAttachments(SubAwardAttachments attachment) {
        this.attachment = attachment;
    }

    
    @Override
    public Class getRuleInterfaceClass() {
       return AddSubAwardAttachmentRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return false;
    }

}
