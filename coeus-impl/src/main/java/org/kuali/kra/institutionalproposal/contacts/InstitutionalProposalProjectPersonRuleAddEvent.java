/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.contacts;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.award.contacts.AwardProjectPersonRuleAddEvent;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;


public class InstitutionalProposalProjectPersonRuleAddEvent extends KcDocumentEventBase {

private static final Logger LOG = LogManager.getLogger(AwardProjectPersonRuleAddEvent.class);
    
    private InstitutionalProposalPerson newProjectPerson;
    
    public InstitutionalProposalProjectPersonRuleAddEvent(String description, String errorPathPrefix, Document document, InstitutionalProposalPerson newProjectPerson) {
        super(description, errorPathPrefix, document);
        this.newProjectPerson = newProjectPerson;
    }

    /**
     * Gets the newProjectPerson attribute. 
     * @return Returns the newProjectPerson.
     */
    public InstitutionalProposalPerson getNewProjectPerson() {
        return newProjectPerson;
    }

    @Override
    public Class<InstitutionalProposalProjectPersonAddRule> getRuleInterfaceClass() {
        return InstitutionalProposalProjectPersonAddRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((InstitutionalProposalProjectPersonAddRule) rule).processAddInstitutionalProposalProjectPersonBusinessRules(this);
    }

    @Override
    protected void logEvent() {
        LOG.info("Logging AddInstitutionalProposalProjectPersonRuleEvent");
    }

}
