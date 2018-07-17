/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.editable;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class ProposalDataOverrideEvent extends KcDocumentEventBase {
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(ProposalDataOverrideEvent.class);
    
    private ProposalChangedData proposalChangedData;
    private DevelopmentProposal developmentProposal;
    /**
     * Constructs an CopyProposalEvent.
     * 
     * @param document proposal development document
     * @param criteria the proposal copy criteria
     */
    public ProposalDataOverrideEvent(ProposalDevelopmentDocument document, ProposalChangedData proposalChangedData) {
        super("Override Proposal Data " + getDocumentId(document), "", document);
        this.proposalChangedData = proposalChangedData;
        this.developmentProposal = document.getDevelopmentProposal();
        logEvent();
    }
    
    @Override
    public void validate() {
        super.validate();
    }
    
    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        LOG.debug(logMessage);
    }

    @Override
    public Class getRuleInterfaceClass() {
        return ProposalDataOverrideRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((ProposalDataOverrideRule) rule).processProposalDataOverrideRules(this);
    }

    public ProposalChangedData getProposalChangedData() {
        return proposalChangedData;
    }

    public void setProposalChangedData(ProposalChangedData proposalChangedData) {
        this.proposalChangedData = proposalChangedData;
    }

    public DevelopmentProposal getDevelopmentProposal() {
        return developmentProposal;
    }

    public void setDevelopmentProposal(DevelopmentProposal developmentProposal) {
        this.developmentProposal = developmentProposal;
    }
}
