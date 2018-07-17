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
import org.kuali.coeus.common.committee.impl.bo.CommitteeBase;
import org.kuali.coeus.common.committee.impl.bo.CommitteeMembershipBase;
import org.kuali.coeus.common.committee.impl.document.CommitteeDocumentBase;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;

/**
 * 
 * This abstract class is used for specific <code>{@link CommitteeBase}</code> events.
 * 
 * @author Kuali Research Administration Team (kc.dev@kuali.org)
 */
public abstract class CommitteeMembershipEventBase extends KcDocumentEventBase
                                                   implements CommitteeMembershipEvent {
    private static final Logger LOG = LogManager.getLogger(CommitteeMembershipEventBase.class);
    
    private CommitteeMembershipBase committeeMembership;
    
    /**
     * 
     * Constructs a <code>{@link CommitteeMembershipEventBase}</code>
     * 
     * 
     * @param description
     * @param errorPathPrefix
     * @param document
     * @param committeeMembership
     */
    protected CommitteeMembershipEventBase(String description, String errorPathPrefix, 
            CommitteeDocumentBase comitteeDocument, CommitteeMembershipBase committeeMembership) {
        super(description, errorPathPrefix, comitteeDocument);

        this.committeeMembership = committeeMembership;

        logEvent();
    }

    /**
     * 
     * Constructs a <code>{@link CommitteeMembershipEventBase}</code>
     * 
     * 
     * @param description
     * @param errorPathPrefix
     * @param document
     */
    protected CommitteeMembershipEventBase(String description, String errorPathPrefix, 
            CommitteeDocumentBase comitteeDocument) {
        super(description, errorPathPrefix, comitteeDocument);
        logEvent();
    }

    /**
     * 
     * Get the <code>{@link CommitteeMembershipBase}</code> of this event.
     * 
     * @return <code>CommitteeMembershipBase</code>
     */
    @Override
    public CommitteeMembershipBase getCommitteeMembership() {
        return this.committeeMembership;
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
        if (getCommitteeMembership() == null) {
            logMessage.append("null committeeMembership");
        } else {
            logMessage.append(getCommitteeMembership().toString());
        }

        LOG.debug(logMessage);
    }
}
