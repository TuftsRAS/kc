/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.rules;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.institutionalproposal.document.InstitutionalProposalDocument;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.rice.krad.rules.rule.BusinessRule;


public class InstitutionalProposalRuleEvent extends KcDocumentEventBase {

private static final Logger LOG = LogManager.getLogger(InstitutionalProposalAddCostShareRuleEvent.class);
    
    private InstitutionalProposal institutionalProposal;

    public InstitutionalProposalRuleEvent(String errorPathPrefix, 
            InstitutionalProposalDocument institutionalProposalDocument,
            InstitutionalProposal institutionalProposal) {
        super("Institutional Proposal", errorPathPrefix, institutionalProposalDocument);
        this.institutionalProposal = institutionalProposal;
    }
    
    /**
     * Convenience method to return an InstitutionalProposalDocument
     * @return
     */
    public InstitutionalProposalDocument getInstitutionalProposalDocument() {
        return (InstitutionalProposalDocument) getDocument();
    }
    
    /**
     * This method returns the equipment item for validation
     * @return
     */
    public InstitutionalProposal getInstitutionalProposalForValidation() {
        return institutionalProposal;
    }
    
    
    @Override
    protected void logEvent() {
        LOG.info("Logging InstitutionalProposalSponsorAndProgramRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return InstitutionalProposalRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((InstitutionalProposalRule)rule).processInstitutionalProposalRules(this);
    }
}

