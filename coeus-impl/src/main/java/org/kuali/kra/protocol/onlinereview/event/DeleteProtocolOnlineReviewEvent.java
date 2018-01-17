/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.onlinereview.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.protocol.ProtocolOnlineReviewDocumentBase;
import org.kuali.kra.protocol.onlinereview.rules.DeleteOnlineReviewRule;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class DeleteProtocolOnlineReviewEvent extends KcDocumentEventBase {
    
    private static final Log LOG = LogFactory.getLog(DeleteProtocolOnlineReviewEvent.class);
    private String reason = null;
    private String noteText = null;
    private int maxLength;
       
    public DeleteProtocolOnlineReviewEvent(final ProtocolOnlineReviewDocumentBase document,
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
