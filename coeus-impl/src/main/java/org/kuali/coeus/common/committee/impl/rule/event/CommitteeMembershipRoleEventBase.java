/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.committee.impl.rule.event;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.committee.impl.bo.CommitteeMembershipRole;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.rice.krad.document.Document;

public abstract class CommitteeMembershipRoleEventBase extends KcDocumentEventBase
                                                       implements CommitteeMembershipRoleEvent {


    private static final Logger LOG = LogManager.getLogger(CommitteeMembershipRoleEventBase.class);

    private CommitteeMembershipRole committeeMembershipRole;
    private int membershipIndex;

    protected CommitteeMembershipRoleEventBase(String description, String errorPathPrefix, Document document,
            CommitteeMembershipRole committeeMembershipRole, int membershipIndex) {
        super(description, errorPathPrefix, document);

        this.committeeMembershipRole = committeeMembershipRole;
        this.membershipIndex = membershipIndex;

        logEvent();
    }

    /**
     * Get the <code>{@link CommitteeMembershipRole}</code> of this event.
     * 
     * @return <code>CommitteeMembershipRole</code>
     */
    @Override
    public CommitteeMembershipRole getCommitteeMembershipRole() {
        return this.committeeMembershipRole;
    }

    /**
     * Get the index of the <code>CommitteeMembershipBase</code> of this event.
     * 
     * @return <code>membershipIndex</code>
     */
    public int getMembershipIndex() {
        return this.membershipIndex;
    }

    /**
     * 
     * Logs the event type and some information about the associated location.
     */
    @Override
    protected void logEvent() {
        String className = StringUtils.substringAfterLast(this.getClass().getName(), ".");
        StringBuffer logMessage = new StringBuffer(className);
        logMessage.append(" with ");

        // vary logging detail as needed
        if (getCommitteeMembershipRole() == null) {
            logMessage.append("null committeeMembershipRole");
        }
        else {
            logMessage.append(getCommitteeMembershipRole().toString());
        }

        LOG.debug(logMessage);
    }
}
