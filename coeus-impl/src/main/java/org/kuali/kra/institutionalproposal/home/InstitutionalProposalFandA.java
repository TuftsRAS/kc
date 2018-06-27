/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.home;


import org.kuali.coeus.common.budget.framework.rate.RateClass;
import org.kuali.coeus.common.budget.framework.rate.RateType;
import org.kuali.coeus.common.framework.version.sequence.associate.SequenceAssociate;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.kra.award.home.ValuableItem;
import org.kuali.kra.institutionalproposal.InstitutionalProposalAssociate;

import java.sql.Date;


public class InstitutionalProposalFandA extends InstitutionalProposalAssociate implements ValuableItem, SequenceAssociate<InstitutionalProposal> {

    private static final long serialVersionUID = 1L;

    private Long proposalId;
    private Long proposalFandARateId;
    private String fiscalYear;
    private boolean onOffCampusFlag = true;
    private String rateClassCode;
    private String rateTypeCode;
    private Date startDate;
    private ScaleTwoDecimal instituteRate;
    private RateClass rateClass;
    private RateType rateType;
    private ScaleTwoDecimal applicableRate;
    private String activityTypeCode;
    private ScaleTwoDecimal amount;

    public ScaleTwoDecimal getApplicableRate() {
        return applicableRate;
    }

    public void setApplicableRate(ScaleTwoDecimal applicableRate) {
        this.applicableRate = applicableRate;
    }

    public String getActivityTypeCode() {
        return activityTypeCode;
    }

    public void setActivityTypeCode(String activityTypeCode) {
        this.activityTypeCode = activityTypeCode;
    }



    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public Long getProposalFandARateId() {
        return proposalFandARateId;
    }

    public void setProposalFandARateId(Long proposalFandARateId) {
        this.proposalFandARateId = proposalFandARateId;
    }

    public String getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public boolean getOnOffCampusFlag() {
        return onOffCampusFlag;
    }

    public void setOnOffCampusFlag(boolean onOffCampusFlag) {
        this.onOffCampusFlag = onOffCampusFlag;
    }

    public String getRateClassCode() {
        return rateClassCode;
    }

    public void setRateClassCode(String rateClassCode) {
        this.rateClassCode = rateClassCode;
    }

    public String getRateTypeCode() {
        return rateTypeCode;
    }

    public void setRateTypeCode(String rateTypeCode) {
        this.rateTypeCode = rateTypeCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public ScaleTwoDecimal getInstituteRate() {
        return instituteRate;
    }

    public void setInstituteRate(ScaleTwoDecimal instituteRate) {
        this.instituteRate = instituteRate;
    }

    public RateClass getRateClass() {
        return rateClass;
    }

    public void setRateClass(RateClass rateClass) {
        this.rateClass = rateClass;
    }

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;
    }

    @Override
    public ScaleTwoDecimal getAmount() {
        return amount;
    }

    public void setAmount(ScaleTwoDecimal amount) {
        this.amount = amount;
    }

    @Override
    public void setSequenceOwner(InstitutionalProposal newlyVersionedOwner) {
        setInstitutionalProposal(newlyVersionedOwner);
    }

    @Override
    public InstitutionalProposal getSequenceOwner() {
        return getInstitutionalProposal();
    }

    @Override
    public void resetPersistenceState() {
        setProposalFandARateId(null);
    }
}
