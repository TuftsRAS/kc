/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.person;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * Event triggered when a Key Person is added to a
 * <code>{@link ProposalDevelopmentDocument}</code>
 *
 * @author $Author: gmcgrego $
 * @version $Revision: 1.4 $
 */
public class SaveKeyPersonEvent extends KcDocumentEventBase {

    private static final Logger LOG = LogManager.getLogger(SaveKeyPersonEvent.class);

    /**
     * Constructs an SaveKeyPersonEvent with the given errorPathPrefix, document, and proposalPerson.
     * 
     * @param errorPathPrefix
     * @param document
     * @see org.kuali.coeus.sys.framework.rule.KcDocumentEventBase#KcDocumentEventBase(String, String, Document)
     */
    public SaveKeyPersonEvent(String errorPathPrefix, ProposalDevelopmentDocument document) {
        super("Saving personnel on document " + getDocumentId(document), errorPathPrefix, document);
        logEvent();
    }

    /**
     * Logs the event type and some information about the associated accountingLine
     */
    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");
        
        for (ProposalPerson person : ((ProposalDevelopmentDocument) getDocument()).getDevelopmentProposal().getProposalPersons()) {
            logMessage.append(person.toString());
            logMessage.append(", ");
        }
        
        if (logMessage.substring(logMessage.length() - 2).equals(", ")) {
            logMessage.delete(logMessage.length() - 2, logMessage.length());
        }

        LOG.debug(logMessage);
    }

    /**
     * Constructs an SaveKeyPersonEvent with the given errorPathPrefix, document, and proposalPerson.
     * 
     * @param errorPathPrefix
     * @param document
     * @param person
     */
    public SaveKeyPersonEvent(String errorPathPrefix, Document document) {
        this(errorPathPrefix, (ProposalDevelopmentDocument) document);
    }

    @Override
    public Class getRuleInterfaceClass() {
        return SaveKeyPersonRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        LOG.info("Calling processSaveKeyPersonBusinessRules on " + rule.getClass().getSimpleName());
        return ((SaveKeyPersonRule) rule).processSaveKeyPersonBusinessRules((ProposalDevelopmentDocument) getDocument());
    }
}
