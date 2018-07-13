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
import org.kuali.kra.institutionalproposal.document.InstitutionalProposalDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;

import java.util.List;


public class InstitutionalProposalPersonSaveRuleEvent extends KcDocumentEventBase {

private static final Logger LOG = LogManager.getLogger(InstitutionalProposalPersonSaveRuleEvent.class);
    
    private List<InstitutionalProposalPerson> projectPersons;
    
    public InstitutionalProposalPersonSaveRuleEvent(String description, String errorPathPrefix, Document document) {
        super(description, errorPathPrefix, document);
        this.projectPersons = ((InstitutionalProposalDocument) document).getInstitutionalProposal().getProjectPersons();
    }

    /**
     * Gets the InstitutionalProposal project persons. 
     * @return Returns the InstitutionalProposal project persons
     */
    public List<InstitutionalProposalPerson> getProjectPersons() {
        return projectPersons;
    }

    @Override
    public Class<InstitutionalProposalProjectPersonAddRule> getRuleInterfaceClass() {
        return InstitutionalProposalProjectPersonAddRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((InstitutionalProposalPersonSaveRule) rule).processInstitutionalProposalPersonSaveBusinessRules(this);
    }

    @Override
    protected void logEvent() {
        LOG.info("Logging InstitutionalProposalPersonSaveRuleEvent");
    }
}
