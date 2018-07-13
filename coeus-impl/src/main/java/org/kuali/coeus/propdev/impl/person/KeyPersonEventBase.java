/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.person;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * Base implementation for events triggered when a Key Person state is modified on a 
 * <code>{@link ProposalDevelopmentDocument}</code>
 *
 * @author $Author: gmcgrego $
 * @version $Revision: 1.6 $
 */
public abstract class KeyPersonEventBase extends KcDocumentEventBase implements KeyPersonEvent {
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(KeyPersonEventBase.class);
    
    private ProposalPerson person;
    
    protected KeyPersonEventBase(String description, ProposalDevelopmentDocument document, ProposalPerson person) {
        this(description, "", document, person);
    }

    protected KeyPersonEventBase(String description, String errorPathPrefix, ProposalDevelopmentDocument document, ProposalPerson person) {
        super(description, errorPathPrefix, document);

        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        this.person = (ProposalPerson) ObjectUtils.deepCopy(person);
        
        logEvent();
    }

    /**
     * @return <code>{@link ProposalPerson}</code> that triggered this event.
     */
    @Override
    public ProposalPerson getProposalPerson() {
        return person;
    }

    @Override
    public void validate() {
        super.validate();
        if (getProposalPerson() == null) {
            throw new IllegalArgumentException("invalid (null) proposal person");
        }
    }

    /**
     * Logs the event type and some information about the associated accountingLine
     */
    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (getProposalPerson() == null) {
            logMessage.append("null proposalPerson");
        }
        else {
            logMessage.append(getProposalPerson().toString());
        }

        LOG.debug(logMessage);
    }
}
