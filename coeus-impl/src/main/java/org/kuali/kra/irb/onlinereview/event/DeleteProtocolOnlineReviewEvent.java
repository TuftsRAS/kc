/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.irb.onlinereview.event;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.irb.ProtocolOnlineReviewDocument;
import org.kuali.kra.irb.onlinereview.rules.DeleteOnlineReviewRule;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class DeleteProtocolOnlineReviewEvent extends KcDocumentEventBase {
    
    private static final Logger LOG = LogManager.getLogger(DeleteProtocolOnlineReviewEvent.class);
    private String reason = null;
    private String noteText = null;
    private int maxLength;
       
    public DeleteProtocolOnlineReviewEvent(final ProtocolOnlineReviewDocument document,
                                                         final String deleteReason,
                                                         final String deleteNoteText,
                                                         final int reasonMaxLength) {
        super("delete protocol online review", "Are you sure you want to delete this document?", document);
        this.reason = deleteReason;
        this.noteText = deleteNoteText;
        this.maxLength = reasonMaxLength;
    }
 
    @Override
    protected void logEvent() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("disapprove protocol online review comment event reason=" + reason);
        }
    }

    @Override
    public Class<DeleteOnlineReviewRule> getRuleInterfaceClass() {
        return DeleteOnlineReviewRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return this.getRuleInterfaceClass().cast(rule).processDeleteOnlineReview(this);

    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
    
    
}
