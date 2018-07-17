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
import org.kuali.kra.institutionalproposal.home.InstitutionalProposalUnrecoveredFandA;
import org.kuali.rice.krad.rules.rule.BusinessRule;

import java.util.List;


public class InstitutionalProposalAddUnrecoveredFandARuleEvent extends KcDocumentEventBase {

    private static final Logger LOG = LogManager.getLogger(InstitutionalProposalAddCostShareRuleEvent.class);
    
    private InstitutionalProposalUnrecoveredFandA institutionalProposalUnrecoveredFandA;

    public InstitutionalProposalAddUnrecoveredFandARuleEvent(String errorPathPrefix, 
            InstitutionalProposalDocument institutionalProposalDocument,
            InstitutionalProposalUnrecoveredFandA institutionalProposalUnrecoveredFandA) {
        super("Cost Share", errorPathPrefix, institutionalProposalDocument);
        this.institutionalProposalUnrecoveredFandA = institutionalProposalUnrecoveredFandA;
    }
    
    /**
     * Convenience method to return an InstitutionalProposalDocument
     * @return
     */
    public InstitutionalProposalDocument getInstitutionalProposalDocument() {
        return (InstitutionalProposalDocument) getDocument();
    }
    
    /**
     * This method returns all unrecovered F&amp;A distributions
     * @return
     */
    public List<InstitutionalProposalUnrecoveredFandA> getInstitutionalProposalUnrecoveredFandAs() {
        return getInstitutionalProposalDocument().getInstitutionalProposal().getInstitutionalProposalUnrecoveredFandAs();
    }

    /**
     * This method returns the equipment item for validation
     * @return
     */
    public InstitutionalProposalUnrecoveredFandA getUnrecoveredFandAForValidation() {
        return institutionalProposalUnrecoveredFandA;
    }
    
    
    @Override
    protected void logEvent() {
        LOG.info("Logging InstitutionalProposalUnrecoveredRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return InstitutionalProposalUnrecoveredFandARule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((InstitutionalProposalUnrecoveredFandARule)rule).processAddInstitutionalProposalUnrecoveredFandABusinessRules(this);
    }

}
