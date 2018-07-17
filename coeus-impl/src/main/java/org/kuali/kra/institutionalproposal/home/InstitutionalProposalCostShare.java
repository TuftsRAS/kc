/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.home;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.budget.framework.core.CostShare;
import org.kuali.coeus.common.framework.unit.Unit;
import org.kuali.coeus.common.framework.version.sequence.associate.SequenceAssociate;
import org.kuali.kra.award.home.ValuableItem;
import org.kuali.kra.bo.CostShareType;
import org.kuali.kra.institutionalproposal.InstitutionalProposalAssociate;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;

public class InstitutionalProposalCostShare extends InstitutionalProposalAssociate implements ValuableItem, SequenceAssociate<InstitutionalProposal>, CostShare {

    private static final long serialVersionUID = 1L;

    private Long proposalCostShareId;

    private String projectPeriod;

    private ScaleTwoDecimal costSharePercentage;

    private Integer costShareTypeCode;

    private String sourceAccount;

    private ScaleTwoDecimal amount;

    private String unitNumber;

    private Unit unit;

    private CostShareType costShareType;

    public InstitutionalProposalCostShare() {
    }

    public Long getProposalCostShareId() {
        return proposalCostShareId;
    }

    public void setProposalCostShareId(Long proposalCostShareId) {
        this.proposalCostShareId = proposalCostShareId;
    }

    public String getProjectPeriod() {
        return projectPeriod;
    }

    public void setProjectPeriod(String projectPeriod) {
        this.projectPeriod = projectPeriod;
    }

    public ScaleTwoDecimal getCostSharePercentage() {
        return costSharePercentage;
    }

    public void setCostSharePercentage(ScaleTwoDecimal costSharePercentage) {
        this.costSharePercentage = costSharePercentage;
    }

    @Override
    public Integer getCostShareTypeCode() {
        return costShareTypeCode;
    }

    public void setCostShareTypeCode(Integer costShareTypeCode) {
        this.costShareTypeCode = costShareTypeCode;
    }

    @Override
    public String getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(String sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    @Override
    public ScaleTwoDecimal getAmount() {
        return amount;
    }

    public void setAmount(ScaleTwoDecimal amount) {
        this.amount = amount;
    }

    @Override
    public CostShareType getCostShareType() {
        return costShareType;
    }

    public void setCostShareType(CostShareType costShareType) {
        this.costShareType = costShareType;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public Unit getUnit() {
        if (unit == null && StringUtils.isNotBlank(getUnitNumber()) || (unit != null && !unit.getUnitNumber().equals(getUnitNumber()))) {
            refreshReferenceObject("unit");
        }

        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getUnitName() {
        Unit unit = getUnit();
        return unit != null ? unit.getUnitName() : null;
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
        this.proposalCostShareId = null;
    }
}
