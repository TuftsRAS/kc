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
import org.kuali.kra.protocol.onlinereview.rules.AddOnlineReviewCommentRule;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class AddProtocolOnlineReviewCommentEvent extends KcDocumentEventBase {

    
    
    private static final Logger LOG = LogManager.getLogger(AddProtocolOnlineReviewCommentEvent.class);
    private final CommitteeScheduleMinuteBase committeeScheduleMinute;
    private final long onlineReviewIndex;
  
    /**
     * Creates a new event.
     * @param document the document.
     * @param newProtocolNotepad the new attachment to be added.
     */
    public AddProtocolOnlineReviewCommentEvent(final ProtocolOnlineReviewDocumentBase document,
        final CommitteeScheduleMinuteBase newCommitteeScheduleMinute, final long onlineReviewIndex ) {
        super("adding new protocol notepad", "notesAttachmentsHelper", document);
        this.onlineReviewIndex = onlineReviewIndex;
        if (document == null) {
            throw new IllegalArgumentException("the document is null");
        }
        
        if (newCommitteeScheduleMinute == null) {
            throw new IllegalArgumentException("the newCommitteeScheduleMinute is null");
        }
        
        this.committeeScheduleMinute = newCommitteeScheduleMinute;
    }
  
    
    
    
    @Override
    protected void logEvent() {
        LOG.debug("adding new: " + this.committeeScheduleMinute + " on doc # " + this.getDocument().getDocumentNumber());
    }

    @Override
    public Class<AddOnlineReviewCommentRule> getRuleInterfaceClass() {
        return AddOnlineReviewCommentRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return this.getRuleInterfaceClass().cast(rule).processAddProtocolOnlineReviewComment(this);
    }

    /**
     * Gets the committeeScheduleMinute attribute. 
     * @return Returns the committeeScheduleMinute.
     */
    public CommitteeScheduleMinuteBase getCommitteeScheduleMinute() {
        return committeeScheduleMinute;
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
    
}
