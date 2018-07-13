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
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.protocol.ProtocolOnlineReviewDocumentBase;
import org.kuali.kra.protocol.onlinereview.rules.DisapproveOnlineReviewCommentRule;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.util.KRADConstants;

public class DisapproveProtocolOnlineReviewCommentEvent extends KcDocumentEventBase {
    
    private static final Logger LOG = LogManager.getLogger(DisapproveProtocolOnlineReviewCommentEvent.class);
    private String reason = null;
    private String noteText = null;
    private int maxLength;
       
    public DisapproveProtocolOnlineReviewCommentEvent(final ProtocolOnlineReviewDocumentBase document,
                                                         final String disapprovalReason,
                                                         final String disapprovalNoteText,
                                                         final int reasonMaxLength) {
        super("disapprove protocol online review comment", KRADConstants.DOCUMENT_DISAPPROVE_QUESTION, document);
        this.reason = disapprovalReason;
        this.noteText = disapprovalNoteText;
        this.maxLength = reasonMaxLength;
    }
 
    @Override
    protected void logEvent() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("disapprove protocol online review comment event reason=" + reason);
        }
    }

    @Override
    public Class<DisapproveOnlineReviewCommentRule> getRuleInterfaceClass() {
        return DisapproveOnlineReviewCommentRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return this.getRuleInterfaceClass().cast(rule).processDisapproveOnlineReviewComment(this);

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
