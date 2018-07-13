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
import org.kuali.kra.institutionalproposal.attachments.InstitutionalProposalAttachment;
import org.kuali.kra.institutionalproposal.document.InstitutionalProposalDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;


public class InstitutionalProposalAddAttachmentRuleEvent extends KcDocumentEventBase {

    private static final Logger LOG = LogManager.getLogger(InstitutionalProposalAddCostShareRuleEvent.class);
    private static final String INSTITUTIONAL_PROPOSAL = "Institutional Proposal";

    private InstitutionalProposalAttachment institutionalProposalAttachment;

    public InstitutionalProposalAddAttachmentRuleEvent(String errorPathPrefix, 
            InstitutionalProposalDocument institutionalProposalDocument,
            InstitutionalProposalAttachment institutionalProposalAttachment) {
        super(INSTITUTIONAL_PROPOSAL, errorPathPrefix, institutionalProposalDocument);
        this.institutionalProposalAttachment = institutionalProposalAttachment;
    }

    public InstitutionalProposalDocument getInstitutionalProposalDocument() {
        return (InstitutionalProposalDocument) getDocument();
    }

    public InstitutionalProposalAttachment getInstitutionalProposalAttachmentForValidation() {
        return institutionalProposalAttachment;
    }
    
    
    @Override
    protected void logEvent() {
        LOG.info("Logging InstitutionalProposalAttachmentRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return InstitutionalProposalAddAttachmentRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((InstitutionalProposalAddAttachmentRule)rule).processAddInstitutionalProposalAttachmentBusinessRules(this);
    }

}
