/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.onlinereview.event;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.committee.impl.meeting.CommitteeScheduleMinuteBase;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.protocol.ProtocolOnlineReviewDocumentBase;
import org.kuali.kra.protocol.onlinereview.ProtocolReviewAttachmentBase;
import org.kuali.kra.protocol.onlinereview.rules.SaveProtocolOnlineReviewRule;
import org.kuali.rice.krad.rules.rule.BusinessRule;

import java.util.List;

public class SaveProtocolOnlineReviewEvent extends KcDocumentEventBase {

    
    
    private static final Logger LOG = LogManager.getLogger(SaveProtocolOnlineReviewEvent.class);
    private final List<CommitteeScheduleMinuteBase> minutes;
    private List<ProtocolReviewAttachmentBase> reviewAttachments;
    private final long onlineReviewIndex;
  
    /**
     * Creates a new event.
     * @param document the document.
     * @param newProtocolNotepad the new attachment to be added.
     */
    public SaveProtocolOnlineReviewEvent(final ProtocolOnlineReviewDocumentBase document,
        final List<CommitteeScheduleMinuteBase> minutes, final long onlineReviewIndex ) {
        super("adding new protocol notepad", "notesAttachmentsHelper", document);
        this.onlineReviewIndex = onlineReviewIndex;
        if (document == null) {
            throw new IllegalArgumentException("the document is null");
        }
        
        if (minutes == null) {
            throw new IllegalArgumentException("the newCommitteeScheduleMinute is null");
        }
        
        this.minutes = minutes;
        this.reviewAttachments =document.getProtocolOnlineReview().getReviewAttachments();
    }
  
    
    
    
    @Override
    protected void logEvent() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("save #" + this.getDocument().getDocumentNumber());
        }
    }

    @Override
    public Class<SaveProtocolOnlineReviewRule> getRuleInterfaceClass() {
        return SaveProtocolOnlineReviewRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return this.getRuleInterfaceClass().cast(rule).processSaveProtocolOnlineReview(this);
    }

   
    public ProtocolOnlineReviewDocumentBase getProtocolOnlineReviewDocument() {
        return (ProtocolOnlineReviewDocumentBase)getDocument();
    }

    /**
     * Gets the onlineReviewIndex attribute. 
     * @return Returns the onlineReviewIndex.
     */
    public long getOnlineReviewIndex() {
        return onlineReviewIndex;
    }

    /**
     * Gets the minutes attribute. 
     * @return Returns the minutes.
     */
    public List<CommitteeScheduleMinuteBase> getMinutes() {
        return minutes;
    }


    public List<ProtocolReviewAttachmentBase> getReviewAttachments() {
        return reviewAttachments;
    }
    
}
