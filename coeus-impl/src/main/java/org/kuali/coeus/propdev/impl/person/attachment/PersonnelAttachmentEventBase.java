/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.person.attachment;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;

public abstract class PersonnelAttachmentEventBase extends KcDocumentEventBase implements PersonnelAttachmentEvent {
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager
            .getLogger(PersonnelAttachmentEventBase.class);

    private ProposalPersonBiography proposalPersonBiography;

    protected PersonnelAttachmentEventBase(String description, String errorPathPrefix, ProposalDevelopmentDocument document,
            ProposalPersonBiography proposalPersonBiography) {
        super(description, errorPathPrefix, document);


        if ( proposalPersonBiography != null ) {
        	//Deep copy is not needed here
        	// No business validations should alter the BO itslef.
            this.proposalPersonBiography = proposalPersonBiography;
        } else {
            //due to this rule requiring A proposal person biography, create one if null
            this.proposalPersonBiography = new ProposalPersonBiography();
        }
        logEvent();

    }

    /**
     * @return <code>{@link ProposalPersonBiography}</code> that triggered this event.
     */
    @Override
    public ProposalPersonBiography getProposalPersonBiography() {
        return proposalPersonBiography;
    }

    @Override
    public void validate() {
        super.validate();
        if (getProposalPersonBiography() == null) {
            throw new IllegalArgumentException("invalid (null) proposal person biography");
        }
    }

    /**
     * Logs the event type and some information about the associated proposal person  bio
     */
    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (getProposalPersonBiography() == null) {
            logMessage.append("null proposalPersonBiography");
        }
        else {
            logMessage.append(getProposalPersonBiography().toString());
        }

        LOG.debug(logMessage);
    }

}
