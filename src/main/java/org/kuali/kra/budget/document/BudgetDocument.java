/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.budget.document;

import static org.kuali.kra.infrastructure.KraServiceLocator.getService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.Copyable;
import org.kuali.core.document.SessionDocument;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kra.budget.BudgetDecimal;
import org.kuali.kra.budget.bo.BudgetLineItem;
import org.kuali.kra.budget.bo.BudgetPeriod;
import org.kuali.kra.budget.bo.BudgetPersonnelDetails;
import org.kuali.kra.budget.bo.RateClass;
import org.kuali.kra.budget.service.BudgetSummaryService;
import org.kuali.kra.document.ResearchDocumentBase;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;

import edu.iu.uis.eden.exception.WorkflowException;

public class BudgetDocument extends ResearchDocumentBase implements Copyable, SessionDocument {

    private Integer proposalNumber;
    private Integer budgetVersionNumber;
    private String comments;
    private BudgetDecimal costSharingAmount; // = new BudgetDecimal(0);
    private Date endDate;
    private boolean finalVersionFlag;
    private String modularBudgetFlag;
    private Integer ohRateClassCode;
    private Integer ohRateTypeCode;
    private BudgetDecimal residualFunds;
    private Date startDate;
    private BudgetDecimal totalCost;
    private BudgetDecimal totalCostLimit; // = new BudgetDecimal(0);
    private BudgetDecimal totalDirectCost;
    private BudgetDecimal totalIndirectCost; // = new BudgetDecimal(0);
    private BudgetDecimal underrecoveryAmount; // = new BudgetDecimal(0);
    private Integer urRateClassCode;
    private ProposalDevelopmentDocument proposal;
    private RateClass rateClass;
    private List<BudgetPeriod> budgetPeriods;
    private List<BudgetLineItem> budgetLineItems;
    private List<BudgetPersonnelDetails> budgetPersonnelDetailsList;

    private Date summaryPeriodStartDate;
    private Date summaryPeriodEndDate;
    
    public BudgetDocument(){
        super();
        budgetPeriods = new ArrayList<BudgetPeriod>();
        budgetLineItems = new ArrayList<BudgetLineItem>();
        budgetPersonnelDetailsList = new ArrayList<BudgetPersonnelDetails>();
    }

    public void initialize() {
    }
    
    @Override
    public void toCopy() throws WorkflowException, IllegalStateException {
        super.toCopy();
        setBudgetVersionNumber(proposal.getNextBudgetVersionNumber());
    }

    public Integer getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(Integer proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public BudgetDecimal getCostSharingAmount() {
        return costSharingAmount == null ?  new BudgetDecimal(0) : costSharingAmount;
    }

    public void setCostSharingAmount(BudgetDecimal costSharingAmount) {
        this.costSharingAmount = costSharingAmount;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean getFinalVersionFlag() {
        return finalVersionFlag;
    }

    public void setFinalVersionFlag(boolean finalVersionFlag) {
        this.finalVersionFlag = finalVersionFlag;
    }

    public String getModularBudgetFlag() {
        return modularBudgetFlag;
    }

    public void setModularBudgetFlag(String modularBudgetFlag) {
        this.modularBudgetFlag = modularBudgetFlag;
    }

    public Integer getOhRateClassCode() {
        return ohRateClassCode;
    }

    public void setOhRateClassCode(Integer ohRateClassCode) {
        this.ohRateClassCode = ohRateClassCode;
    }

    public Integer getOhRateTypeCode() {
        return ohRateTypeCode;
    }

    public void setOhRateTypeCode(Integer ohRateTypeCode) {
        this.ohRateTypeCode = ohRateTypeCode;
    }

    public BudgetDecimal getResidualFunds() {
        return residualFunds; // == null ?  new BudgetDecimal(0) : residualFunds;
    }

    public void setResidualFunds(BudgetDecimal residualFunds) {
        this.residualFunds = residualFunds;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public BudgetDecimal getTotalCost() {
        return totalCost == null ?  new BudgetDecimal(0) : totalCost;
    }

    public void setTotalCost(BudgetDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BudgetDecimal getTotalCostLimit() {
        return totalCostLimit; // == null ?  new BudgetDecimal(0) : totalCostLimit;
    }

    public void setTotalCostLimit(BudgetDecimal totalCostLimit) {
        this.totalCostLimit = totalCostLimit;
    }

    public BudgetDecimal getTotalDirectCost() {
        return totalDirectCost == null ?  new BudgetDecimal(0) : totalDirectCost;
    }

    public void setTotalDirectCost(BudgetDecimal totalDirectCost) {
        this.totalDirectCost = totalDirectCost;
    }

    public BudgetDecimal getTotalIndirectCost() {
        return totalIndirectCost == null ?  new BudgetDecimal(0) : totalIndirectCost;
    }

    public void setTotalIndirectCost(BudgetDecimal totalIndirectCost) {
        this.totalIndirectCost = totalIndirectCost;
    }

    public BudgetDecimal getUnderrecoveryAmount() {
        return underrecoveryAmount == null ?  new BudgetDecimal(0) : underrecoveryAmount;
    }

    public void setUnderrecoveryAmount(BudgetDecimal underrecoveryAmount) {
        this.underrecoveryAmount = underrecoveryAmount;
    }

    public Integer getUrRateClassCode() {
        return urRateClassCode;
    }

    public void setUrRateClassCode(Integer urRateClassCode) {
        this.urRateClassCode = urRateClassCode;
    }

    public Integer getBudgetVersionNumber() {
        return budgetVersionNumber;
    }

    public void setBudgetVersionNumber(Integer budgetVersionNumber) {
        this.budgetVersionNumber = budgetVersionNumber;
    }

    public ProposalDevelopmentDocument getProposal() {
        return proposal;
    }

    public void setProposal(ProposalDevelopmentDocument proposal) {
        this.proposal = proposal;
    }

    public RateClass getRateClass() {
        return rateClass;
    }

    public void setRateClass(RateClass rateClass) {
        this.rateClass = rateClass;
    }

    public List<BudgetPeriod> getBudgetPeriods() {
        /* check for new budget version - if new, generate budget periods */
        if(budgetPeriods.isEmpty() && getStartDate() != null) {
            getBudgetSummaryService().generateBudgetPeriods(budgetPeriods, getStartDate(), getEndDate());
        }
        return budgetPeriods;
    }

    /**
     * Gets the BudgetSummary attribute. 
     * @return Returns the BudgetSummary.
     */
    public BudgetSummaryService getBudgetSummaryService() {
        return getService(BudgetSummaryService.class);
    }
    
    public void setBudgetPeriods(List<BudgetPeriod> budgetPeriods) {
        this.budgetPeriods = budgetPeriods;
    }

    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getBudgetPeriods());
        return managedLists;
    }


    /**
     * Gets index i from the budgetPeriods list.
     * 
     * @param index
     * @return Budget Period at index i
     */
    public BudgetPeriod getBudgetPeriod(int index) {
        while (getBudgetPeriods().size() <= index) {
            getBudgetPeriods().add(new BudgetPeriod());
        }
        return (BudgetPeriod) getBudgetPeriods().get(index);
    }

    public List<BudgetLineItem> getBudgetLineItems() {
        return budgetLineItems;
    }

    public void setBudgetLineItems(List<BudgetLineItem> budgetLineItems) {
        this.budgetLineItems = budgetLineItems;
    }

    public List<BudgetPersonnelDetails> getBudgetPersonnelDetailsList() {
        return budgetPersonnelDetailsList;
    }

    public void setBudgetPersonnelDetailsList(List<BudgetPersonnelDetails> budgetPersonnelDetailsList) {
        this.budgetPersonnelDetailsList = budgetPersonnelDetailsList;
    }

    public Date getSummaryPeriodStartDate() {
        summaryPeriodStartDate = getBudgetPeriods().get(0).getStartDate();
        if(summaryPeriodStartDate == null) {
            summaryPeriodStartDate = startDate;
        }
        return summaryPeriodStartDate;
    }

    public Date getSummaryPeriodEndDate() {
        summaryPeriodEndDate = getBudgetPeriods().get(budgetPeriods.size()-1).getEndDate();
        if(summaryPeriodEndDate == null) {
            summaryPeriodEndDate = endDate;
        }
        return summaryPeriodEndDate;
    }

}
