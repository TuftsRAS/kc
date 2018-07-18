/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdevrest.Dto;

import com.codiform.moo.annotation.CollectionProperty;
import com.codiform.moo.annotation.Property;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.kuali.coeus.common.api.BudgetPeriodDto;
import org.kuali.coeus.common.api.RateClassDto;

import java.sql.Timestamp;
import java.util.List;

public class ModularBudgetDto {

    private Long budgetId;
    private Timestamp updateTimestamp;
    @Property(translate = true, update = true)
    private RateClassDto rateClass;
    @JsonProperty(value="budgetPeriods")
    @CollectionProperty(source="budgetPeriods", itemClass=BudgetPeriodDto.class)
    private List<BudgetPeriodDto> budgetPeriods;
    @Property(source = "mvel:?developmentProposal.?proposalNumber")
    private String proposalNumber;
    @Property(source = "mvel:?developmentProposal.?principalInvestigatorName")
    private String principalInvestigatorName;
    @Property(source = "mvel:?developmentProposal.?principalInvestigator.?personId")
    private String principalInvestigatorId;

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public Timestamp getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Timestamp updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public RateClassDto getRateClass() {
        return rateClass;
    }

    public void setRateClass(RateClassDto rateClass) {
        this.rateClass = rateClass;
    }

    public List<BudgetPeriodDto> getBudgetPeriods() {
        return budgetPeriods;
    }

    public void setBudgetPeriods(List<BudgetPeriodDto> budgetPeriods) {
        this.budgetPeriods = budgetPeriods;
    }

    public String getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public String getPrincipalInvestigatorName() {
        return principalInvestigatorName;
    }

    public void setPrincipalInvestigatorName(String principalInvestigatorName) {
        this.principalInvestigatorName = principalInvestigatorName;
    }

    public String getPrincipalInvestigatorId() {
        return principalInvestigatorId;
    }

    public void setPrincipalInvestigatorId(String principalInvestigatorId) {
        this.principalInvestigatorId = principalInvestigatorId;
    }
}
