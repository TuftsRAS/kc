/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.home;

import org.kuali.coeus.common.framework.version.sequence.associate.SequenceAssociate;
import org.kuali.kra.award.home.ValuableItem;
import org.kuali.kra.institutionalproposal.IndirectcostRateType;
import org.kuali.kra.institutionalproposal.InstitutionalProposalAssociate;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;

public class InstitutionalProposalUnrecoveredFandA extends InstitutionalProposalAssociate implements ValuableItem, SequenceAssociate<InstitutionalProposal> {

    private static final long serialVersionUID = 1L;

    private Long proposalUnrecoveredFandAId;

    private ScaleTwoDecimal applicableIndirectcostRate;

    private Integer indirectcostRateTypeCode;

    private String fiscalYear;

    private boolean onCampusFlag;

    private ScaleTwoDecimal underrecoveryOfIndirectcost;

    private String sourceAccount;

    private IndirectcostRateType indirectcostRateType;

    public InstitutionalProposalUnrecoveredFandA() {
        onCampusFlag = true;
    }

    public Long getProposalUnrecoveredFandAId() {
        return proposalUnrecoveredFandAId;
    }

    public void setProposalUnrecoveredFandAId(Long proposalUnrecoveredFandAId) {
        this.proposalUnrecoveredFandAId = proposalUnrecoveredFandAId;
    }

    public ScaleTwoDecimal getApplicableIndirectcostRate() {
        return applicableIndirectcostRate;
    }

    public void setApplicableIndirectcostRate(ScaleTwoDecimal applicableIndirectcostRate) {
        this.applicableIndirectcostRate = applicableIndirectcostRate;
    }

    public Integer getIndirectcostRateTypeCode() {
        return indirectcostRateTypeCode;
    }

    public void setIndirectcostRateTypeCode(Integer indirectcostRateTypeCode) {
        this.indirectcostRateTypeCode = indirectcostRateTypeCode;
    }

    public String getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public boolean getOnCampusFlag() {
        return onCampusFlag;
    }

    public void setOnCampusFlag(boolean onCampusFlag) {
        this.onCampusFlag = onCampusFlag;
    }

    public ScaleTwoDecimal getUnderrecoveryOfIndirectcost() {
        return underrecoveryOfIndirectcost;
    }

    public void setUnderrecoveryOfIndirectcost(ScaleTwoDecimal underrecoveryOfIndirectcost) {
        this.underrecoveryOfIndirectcost = underrecoveryOfIndirectcost;
    }

    public String getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(String sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public IndirectcostRateType getIndirectcostRateType() {
        return indirectcostRateType;
    }

    public void setIndirectcostRateType(IndirectcostRateType idcRateType) {
        this.indirectcostRateType = idcRateType;
    }

    @Override
    public InstitutionalProposal getSequenceOwner() {
        return getInstitutionalProposal();
    }

    @Override
    public void setSequenceOwner(InstitutionalProposal newlyVersionedOwner) {
        setInstitutionalProposal(newlyVersionedOwner);
    }

    @Override
    public void resetPersistenceState() {
        this.proposalUnrecoveredFandAId = null;
    }

    @Override
    public ScaleTwoDecimal getAmount() {
        return underrecoveryOfIndirectcost;
    }
}
