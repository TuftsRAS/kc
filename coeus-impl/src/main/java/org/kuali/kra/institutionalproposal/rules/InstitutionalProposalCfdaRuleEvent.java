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


public class InstitutionalProposalCfdaRuleEvent extends KcDocumentEventBase {

private static final Logger LOG = LogManager.getLogger(InstitutionalProposalAddCostShareRuleEvent.class);

    public InstitutionalProposalCfdaRuleEvent(String errorPathPrefix,
                                              InstitutionalProposalDocument institutionalProposalDocument) {
        super("Institutional Proposal", errorPathPrefix, institutionalProposalDocument);
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
        return getInstitutionalProposalDocument().getInstitutionalProposal();
    }
    
    
    @Override
    protected void logEvent() {
        LOG.info("Logging InstitutionalProposalCfdaRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return InstitutionalProposalCfdaRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((InstitutionalProposalCfdaRule)rule).processCfdaRules(this);
    }
}
