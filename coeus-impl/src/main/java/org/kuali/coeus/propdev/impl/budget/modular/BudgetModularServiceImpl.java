/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.budget.modular;

import org.kuali.coeus.common.budget.api.rate.RateClassType;
import org.kuali.coeus.common.budget.framework.calculator.BudgetCalculationService;
import org.kuali.coeus.common.budget.framework.core.Budget;
import org.kuali.coeus.common.budget.framework.nonpersonnel.BudgetLineItem;
import org.kuali.coeus.common.budget.framework.nonpersonnel.BudgetLineItemCalculatedAmount;
import org.kuali.coeus.common.budget.framework.nonpersonnel.BudgetRateAndBase;
import org.kuali.coeus.common.budget.framework.period.BudgetPeriod;
import org.kuali.coeus.propdev.impl.budget.ProposalDevelopmentBudgetExt;
import org.kuali.coeus.sys.api.model.AbstractDecimal;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component("budgetModularService")
public class BudgetModularServiceImpl implements BudgetModularService {
    
    private static final String RATE_CLASS_PROPERTY_NAME = "rateClass";
    private static final String RATE_NUMBER_PROPERTY_NAME = "rateNumber";
    private static final ScaleTwoDecimal TDC_NEXT_INCREMENT = new ScaleTwoDecimal(25000);

    @Autowired
    @Qualifier("budgetCalculationService")
    private BudgetCalculationService budgetCalculationService;
    @Autowired
    @Qualifier("parameterService")
    private ParameterService parameterService;
    
    @Override
    public void generateModularPeriod(BudgetPeriod budgetPeriod) {

        if (ObjectUtils.isNull(budgetPeriod.getBudgetModular())) {
            BudgetModular budgetModular =
                new BudgetModular(budgetPeriod);
            budgetModular.setBudgetPeriodObj(budgetPeriod);
            budgetPeriod.setBudgetModular(budgetModular);
        }
        
        budgetPeriod.getBudgetModular().calculateAllTotals();
    }
    
    @Override 
    public BudgetModularSummary processModularSummary(Budget budget, boolean synchModular) {
        BudgetModularSummary modularSummary = new BudgetModularSummary();
        ScaleTwoDecimal directCostLessConsortiumFna = ScaleTwoDecimal.ZERO;
        ScaleTwoDecimal consortiumFna = ScaleTwoDecimal.ZERO;
        ScaleTwoDecimal totalDirectCost = ScaleTwoDecimal.ZERO;
        ScaleTwoDecimal totalFnaRequested = ScaleTwoDecimal.ZERO;
        ScaleTwoDecimal totalRequestedCost = ScaleTwoDecimal.ZERO;
        List<BudgetModularIdc> budgetModularIdcs = new ArrayList();
        
        for (BudgetPeriod budgetPeriod: budget.getBudgetPeriods()) {
            BudgetModular budgetModular = budgetPeriod.getBudgetModular();
            if (!ObjectUtils.isNull(budgetModular)) {
				if (synchModular) {
					calculateAllTotalsAfterSync(budgetModular);
				} else {
					budgetModular.calculateAllTotals();
				}
                directCostLessConsortiumFna = directCostLessConsortiumFna.add(budgetModular.getDirectCostLessConsortiumFna());
                consortiumFna = consortiumFna.add(budgetModular.getConsortiumFna());
                totalDirectCost = totalDirectCost.add(budgetModular.getTotalDirectCost());
                totalFnaRequested = totalFnaRequested.add(budgetModular.getTotalFnaRequested());
                totalRequestedCost = totalRequestedCost.add(budgetModular.getTotalRequestedCost());
                budgetModularIdcs.addAll(budgetModular.getBudgetModularIdcs());
            }
        }
        
        modularSummary.setDirectCostLessConsortiumFna(directCostLessConsortiumFna);
        modularSummary.setConsortiumFna(consortiumFna);
        modularSummary.setTotalDirectCost(totalDirectCost);
        modularSummary.setTotalFnaRequested(totalFnaRequested);
        modularSummary.setTotalRequestedCost(totalRequestedCost);
        modularSummary.setBudgetModularIdcs(budgetModularIdcs);
        
        return modularSummary;
    }

    private void calculateAllTotalsAfterSync(BudgetModular budgetModular) {
    	budgetModular.calculateTotalDirectCost();
               
        final ScaleTwoDecimal fnaRequested = budgetModular.getBudgetModularIdcs().stream()
                .map(BudgetModularIdc::getFundsRequested)
                .filter(Objects::nonNull)
                .reduce(ScaleTwoDecimal.ZERO, AbstractDecimal::add);

        budgetModular.setTotalFnaRequested(fnaRequested);
        budgetModular.calculateTotalRequestedCost();
    }

    @Override
    public void synchModularBudget(Budget budget, boolean recalculateFromModifiedDirectCost) {
        if (!roundFandAbase()) {
            synchModularBudgetWithoutRoundingFandABase(budget);
        } else {
            synchModularBudgetAndRoundFandABase(budget, recalculateFromModifiedDirectCost);
        }
    }
    
    //Allow for the modular values to be recalculated from a manually-entered direct cost
    public void synchModularBudgetAndRoundFandABase(Budget budget, boolean recalculateFromModifiedDirectCost) {
        Collection<String> consortiumFnaCostElements = getConsortiumFnaOnlyCostElements();
        String fnaRateClassType = getFnaRateClassType();

        for (BudgetPeriod budgetPeriod: budget.getBudgetPeriods()) {
            BudgetModular budgetModular = initializeModularBudget(budgetPeriod);
            ScaleTwoDecimal directCostLessConsortiumFna = calculateConsortiumAmountsFandABaseRounded(budget, consortiumFnaCostElements, fnaRateClassType,
                    budgetPeriod, budgetModular, recalculateFromModifiedDirectCost);

            if (budgetModular.getBudgetModularIdcs().size() > 0) {
                calculateFandAbase(recalculateFromModifiedDirectCost, budgetPeriod, budgetModular, budgetModular.getBudgetModularIdcs().size());
            }
            // If this is a recalc, these numbers have been manually overridden)
            if (!recalculateFromModifiedDirectCost) {
                ScaleTwoDecimal modularTdc;
                modularTdc = roundToNextSubcontractModule(directCostLessConsortiumFna);
                budgetModular.setDirectCostLessConsortiumFna(modularTdc);
            }
            calculateAllTotalsAfterSync(budgetModular);
        }
    }

    public ScaleTwoDecimal calculateConsortiumAmountsFandABaseRounded(Budget budget, Collection<String> consortiumFnaCostElements, String fnaRateClassType, BudgetPeriod budgetPeriod,
                                                                        BudgetModular budgetModular, boolean recalculateFromModifiedDirectCost) {
        ScaleTwoDecimal directCostLessConsortiumFna = new ScaleTwoDecimal(0);
        ScaleTwoDecimal consortiumFna = new ScaleTwoDecimal(0);
        for (BudgetLineItem budgetLineItem: budgetPeriod.getBudgetLineItems()) {
            getBudgetCalculationService().calculateBudgetLineItem(budget, budgetLineItem);
            if (consortiumFnaCostElements.contains(budgetLineItem.getCostElement())) {
                consortiumFna = consortiumFna.add(budgetLineItem.getLineItemCost());
                directCostLessConsortiumFna = directCostLessConsortiumFna.add(calculateDirectCostWithoutFna(budgetLineItem));
            } else {
                directCostLessConsortiumFna = directCostLessConsortiumFna.add(budgetLineItem.getDirectCost());
            }
            createModularBudgetIndirectCost(budget, budgetModular, fnaRateClassType, budgetLineItem, true);
        }
        if (!recalculateFromModifiedDirectCost) {
            budgetModular.setConsortiumFna(consortiumFna);
        }
        return directCostLessConsortiumFna;
    }

    public void synchModularBudgetWithoutRoundingFandABase(Budget budget) {
       final Collection<String> consortiumFnaOnlyCostElements = getConsortiumFnaOnlyCostElements();
       final String fnaRateClassType = getFnaRateClassType();

        for (BudgetPeriod budgetPeriod: budget.getBudgetPeriods()) {
            BudgetModular budgetModular = initializeModularBudget(budgetPeriod);

            ScaleTwoDecimal directCostWithoutConsortiumFna = calculateConsortiumAmounts(budget, budgetPeriod, budgetModular, fnaRateClassType,
                                                                                        consortiumFnaOnlyCostElements);
            budgetModular.setDirectCostLessConsortiumFna(roundToNextSubcontractModule(directCostWithoutConsortiumFna));
            calculateAllTotalsAfterSync(budgetModular);
        }
    }

    public ScaleTwoDecimal calculateConsortiumAmounts(Budget budget, BudgetPeriod budgetPeriod, BudgetModular budgetModular, String fnaRateClassType,
                                                      Collection<String> consortiumFnaOnlyCostElements) {
        ScaleTwoDecimal directCostWithoutConsortiumFna = new ScaleTwoDecimal(0);
        ScaleTwoDecimal consortiumFna = new ScaleTwoDecimal(0);

        ArrayList<BudgetLineItem> budgetLineItems = new ArrayList<>(budgetPeriod.getBudgetLineItems());
        for (BudgetLineItem budgetLineItem: budgetLineItems) {
            getBudgetCalculationService().calculateBudgetLineItem(budget, budgetLineItem);
            if (consortiumFnaOnlyCostElements.contains(budgetLineItem.getCostElement())) {
                // this line item is ALL F&A so add everything to consortium FnA
                consortiumFna = consortiumFna.add(budgetLineItem.getLineItemCost());
                //this is only direct cost, so neither has Our Fna nor consortiumFnaA
                directCostWithoutConsortiumFna = directCostWithoutConsortiumFna.add(calculateDirectCostWithoutFna(budgetLineItem));
            } else {
                // if it is not mapped as a consortium F&A element, then just add the direct cost
                // (without F&A) to the directCostWithoutF&A
                directCostWithoutConsortiumFna = directCostWithoutConsortiumFna.add(budgetLineItem.getDirectCost());
            }
            createModularBudgetIndirectCost(budget, budgetModular, fnaRateClassType, budgetLineItem, false);
        }
        budgetModular.setConsortiumFna(consortiumFna);
        return directCostWithoutConsortiumFna;
    }

    public void createModularBudgetIndirectCost(Budget budget, BudgetModular budgetModular, String fnaRateClassType, BudgetLineItem budgetLineItem, boolean roundFandABase) {
        for (BudgetRateAndBase budgetRateAndBase: budgetLineItem.getBudgetRateAndBaseList()) {
            budgetRateAndBase.refreshReferenceObject(RATE_CLASS_PROPERTY_NAME);

            if (budgetRateAndBase.getRateClass().getRateClassTypeCode().equals(fnaRateClassType)) {
                BudgetModularIdc budgetModularIdc = setBudgetModularIdcInfo(budget, budgetModular, budgetRateAndBase);

                if (getApplyRateFlag(budgetLineItem, budgetRateAndBase)) {
                    if (roundFandABase) {
                        setModularIdcInfoForFandABaseRoundedCalculation((ProposalDevelopmentBudgetExt) budget, budgetLineItem, budgetRateAndBase, budgetModularIdc);
                    } else {
                        budgetModularIdc.setIdcBase(budgetRateAndBase.getBaseCost());
                    }
                }
                budgetModularIdc.setBudgetModular(budgetModular);
                if (roundFandABase) {
                    budgetModular.addNewBudgetModularIdcBaseRounded(budgetModularIdc);
                } else {
                    budgetModular.addNewBudgetModularIdcBaseUnrounded(budgetModularIdc);
                }
            }
        }
    }

    public void setModularIdcInfoForFandABaseRoundedCalculation(ProposalDevelopmentBudgetExt budget, BudgetLineItem budgetLineItem, BudgetRateAndBase budgetRateAndBase, BudgetModularIdc budgetModularIdc) {
        budgetModularIdc.setStartDate(budgetRateAndBase.getStartDate());
        budgetModularIdc.setEndDate(budgetRateAndBase.getEndDate());
        budgetModularIdc.setIdcBaseUnrounded(budgetRateAndBase.getBaseCost());
        // The true IdcBase is going to be set later once we calculate the MTDC exclusions and do the rounding
        budgetModularIdc.setIdcBase(null);
        setProposalNumber(budget, budgetLineItem, budgetModularIdc);
    }

    public BudgetModularIdc setBudgetModularIdcInfo(Budget budget, BudgetModular budgetModular, BudgetRateAndBase budgetRateAndBase) {
        BudgetModularIdc budgetModularIdc = new BudgetModularIdc();
        if (budgetModularIdc.getBudgetPeriod() == null)
            budgetModularIdc.setBudgetPeriod(budgetModular.getBudgetPeriod());
        if (budgetModularIdc.getBudgetId() == null)
            budgetModularIdc.setBudgetId(budgetModular.getBudgetId());
        if (budgetModularIdc.getBudgetPeriodId() == null)
            budgetModularIdc.setBudgetPeriodId(budgetModular.getBudgetPeriodId());
        budgetModularIdc.setRateNumber(budget.getNextValue(RATE_NUMBER_PROPERTY_NAME));
        budgetModularIdc.setDescription(budgetRateAndBase.getRateClassCode());
        budgetModularIdc.setIdcRate(budgetRateAndBase.getAppliedRate());
        budgetModularIdc.setFundsRequested(budgetRateAndBase.getCalculatedCost());
        return budgetModularIdc;
    }

    /*
    *
    * FnaBase
    * subtract subcon F&A
    * add direct costs of items without MTDC
    * round to the next $25k increment,
    * subtract the direct costs and
    * add back the subcon F&A to the base
    */
    public void calculateFandAbase(boolean recalculateFromModifiedDirectCost, BudgetPeriod budgetPeriod, BudgetModular budgetModular, int numberOfIdcs) {
        // Calculate the total excluded costs for this period
        //excluded costs includes costs that do not have mtdc and cost element under 25K which are subject to mtdc
        ScaleTwoDecimal mtdcExcludedCosts = getMtdcExcludedCosts(budgetPeriod.getBudgetLineItems());

        ScaleTwoDecimal subcontractFna = getSubcontractFnaCosts(budgetPeriod.getBudgetLineItems());
        ScaleTwoDecimal startingFnaBase = getStartingFandAbase(budgetModular);
        ScaleTwoDecimal finalCostBase = ScaleTwoDecimal.ZERO;

        ScaleTwoDecimal fnaBaseLessSubcontractCosts;
        ScaleTwoDecimal roundedFnaBaseWithExcludedCosts;

        if (startingFnaBase.isGreaterThan(ScaleTwoDecimal.ZERO)) {
            //Just start at the (manually-entered) rounded F&A base and continue from there
            if (recalculateFromModifiedDirectCost) {
                roundedFnaBaseWithExcludedCosts = budgetModular.getDirectCostLessConsortiumFna();
            }
            else {
                // Above 25K does not have mtdc on it, so we do not care about it
                fnaBaseLessSubcontractCosts = startingFnaBase.subtract(subcontractFna)
                                                                .add(mtdcExcludedCosts);
                roundedFnaBaseWithExcludedCosts = roundToNextSubcontractModule(fnaBaseLessSubcontractCosts);

            }
            finalCostBase = roundedFnaBaseWithExcludedCosts.add(subcontractFna)
                                                            .subtract(mtdcExcludedCosts);
        }
        //Distribute the FNA base across the period portions
        //Also need to account for cases where a parent proposal has children with differing rates
        if (numberOfIdcs == 1) {
            BudgetModularIdc idcEntry = budgetModular.getBudgetModularIdcs().get(0);
            idcEntry.setIdcBase(finalCostBase);
            idcEntry.calculateFundsRequested();
        }
        else {
            handleHierarchyBaseCalculations(budgetPeriod, budgetModular, startingFnaBase, finalCostBase);
        }
    }

    public ScaleTwoDecimal roundToNextSubcontractModule(ScaleTwoDecimal amountToBeRounded) {
        ScaleTwoDecimal amountRoundedToNextHighestModule = ScaleTwoDecimal.ZERO;
        // Round the new total to the next increment
        while (amountToBeRounded.isGreaterThan(amountRoundedToNextHighestModule)) {
            amountRoundedToNextHighestModule = amountRoundedToNextHighestModule.add(TDC_NEXT_INCREMENT);
        }
        return amountRoundedToNextHighestModule;
    }

    public ScaleTwoDecimal getStartingFandAbase(BudgetModular budgetModular) {
        ScaleTwoDecimal startingFnaBase = ScaleTwoDecimal.ZERO;
        for (BudgetModularIdc idcEntry : budgetModular.getBudgetModularIdcs()) {
            startingFnaBase = startingFnaBase.add(idcEntry.getIdcBaseUnrounded());
        }
        return startingFnaBase;
    }

    public void handleHierarchyBaseCalculations(BudgetPeriod budgetPeriod, BudgetModular budgetModular, ScaleTwoDecimal startingFnaBase, ScaleTwoDecimal finalCostBase) {
        // Now process each set of IDCs with the same start date, splitting the final cost base up between children and then by period portion
        // Calculate the total direct cost (minus exclusions) for all children
        ScaleTwoDecimal totalDirectFNACosts = startingFnaBase;

        long daysInPeriod = getDaysInPeriod(budgetPeriod.getStartDate(), budgetPeriod.getEndDate());
        for (BudgetModularIdc idcEntry : budgetModular.getBudgetModularIdcs()) {
            // CHILD SPLIT
            // Calculate the total starting FNA base for this child
            ScaleTwoDecimal thisChildFnaBase = idcEntry.getIdcBaseUnrounded();
            for (BudgetModularIdc anotherIdc : budgetModular.getBudgetModularIdcs()) {
                if (idcEntry.getHierarchyProposalNumber() != null
                        && idcEntry.getHierarchyProposalNumber().equals(anotherIdc.getHierarchyProposalNumber())
                        && !(idcEntry.getRateNumber().equals(anotherIdc.getRateNumber()))) {
                    thisChildFnaBase = thisChildFnaBase.add(anotherIdc.getIdcBaseUnrounded());
                }
            }

            double childPercentOfTotal = thisChildFnaBase.doubleValue() / totalDirectFNACosts.doubleValue();
            long daysInIdcEntry = getDaysInPeriod(idcEntry.getStartDate(), idcEntry.getEndDate());
            // Calculate the percent of the overall period that this line represents
            double percentOfPeriod = (double)daysInIdcEntry / (double)daysInPeriod;
            // Multiply the full FNA cost base by the percentage of the period to find the cost base for this line item
            // Doing this math in doubles because of rounding complications with ScaleTwoDecimal
            double finalCostBaseDouble = finalCostBase.doubleValue();
            double partialCostBaseDouble = finalCostBaseDouble * percentOfPeriod * childPercentOfTotal;

            ScaleTwoDecimal partialCostBase = new ScaleTwoDecimal(partialCostBaseDouble);
            idcEntry.setIdcBase(partialCostBase);
            idcEntry.calculateFundsRequested();
        }
    }

    public void setProposalNumber(ProposalDevelopmentBudgetExt budget, BudgetLineItem budgetLineItem, BudgetModularIdc budgetModularIdc) {
        ProposalDevelopmentBudgetExt pdBudget = budget;
        if (budgetLineItem.getHierarchyProposalNumber() != null) {
            budgetModularIdc.setHierarchyProposalNumber(budgetLineItem.getHierarchyProposalNumber());
        }
        else {
            budgetModularIdc.setHierarchyProposalNumber(pdBudget.getDevelopmentProposal().getProposalNumber());
        }
    }

    public BudgetModular initializeModularBudget(BudgetPeriod budgetPeriod) {
        if (ObjectUtils.isNull(budgetPeriod.getBudgetModular())) {
            BudgetModular tmpBudgetModular = new BudgetModular(budgetPeriod);
            tmpBudgetModular.setBudgetPeriodObj(budgetPeriod);
            budgetPeriod.setBudgetModular(tmpBudgetModular);
        }
        BudgetModular budgetModular = budgetPeriod.getBudgetModular();
        budgetModular.setBudgetModularIdcs(new ArrayList<>());
        return budgetModular;
    }

    private ScaleTwoDecimal calculateDirectCostWithoutFna(BudgetLineItem budgetLineItem) {
		return budgetLineItem.getBudgetCalculatedAmounts().stream().filter(budgetLineItemCalculatedAmount ->
                !budgetLineItemCalculatedAmount.getRateClass().getRateClassTypeCode().equals(RateClassType.OVERHEAD.getRateClassType())).
                map(BudgetLineItemCalculatedAmount::getCalculatedCost)
                .reduce(ScaleTwoDecimal.ZERO, AbstractDecimal::add);
	}

    protected ScaleTwoDecimal getMtdcExcludedCosts(List<BudgetLineItem> budgetLineItems) {
        ScaleTwoDecimal excludedCostTotal = ScaleTwoDecimal.ZERO;
        for (BudgetLineItem budgetLineItem : budgetLineItems) {
            if (budgetLineItem.getBudgetRateAndBaseList().isEmpty()) {
                excludedCostTotal = excludedCostTotal.add(budgetLineItem.getDirectCost());
            }
        }
        return excludedCostTotal;
    }

    protected ScaleTwoDecimal getSubcontractFnaCosts(List<BudgetLineItem> budgetLineItems) {
        String COST_ELEMENT_SUB_UNDER_25K = getCostelementSubconFandAUnder25K();
        String COST_ELEMENT_SUB_OVER_25K = getCostelementSubconFandAOver25K();
        ScaleTwoDecimal subcontractFna = ScaleTwoDecimal.ZERO;
        for (BudgetLineItem budgetLineItem : budgetLineItems) {
            if (COST_ELEMENT_SUB_UNDER_25K.equals(budgetLineItem.getCostElement()) || COST_ELEMENT_SUB_OVER_25K.equals(budgetLineItem.getCostElement())) {
                subcontractFna = subcontractFna.add(budgetLineItem.getLineItemCost());
            }
        }
        return subcontractFna;
    }

    protected long getDaysInPeriod(Date startDate, Date endDate) {
        long diffMillis = endDate.getTime() - startDate.getTime();
        long diffDays = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);
        // Need to count both the start and end days, so add one to the difference
        diffDays++;
        return diffDays;
    }

    private boolean getApplyRateFlag(BudgetLineItem budgetLineItem, BudgetRateAndBase budgetRateAndBase) {
        return budgetLineItem.getBudgetCalculatedAmounts().stream().anyMatch(budgetCalculatedAmounts -> budgetCalculatedAmounts.getRateClassCode().
                equals(budgetRateAndBase.getRateClassCode()) && budgetCalculatedAmounts.getApplyRateFlag());
    }

    public boolean roundFandAbase() {
        return parameterService.getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT,
                ParameterConstants.DOCUMENT_COMPONENT,
                Constants.ROUND_F_AND_A_BASE);
    }

    public Collection<String> getConsortiumFnaOnlyCostElements() {
        return parameterService.getParameterValuesAsString(Budget.class, Constants.PARAMETER_FNA_COST_ELEMENTS);
    }

    public String getFnaRateClassType() {
        return parameterService.getParameterValueAsString(Budget.class, Constants.PARAMETER_FNA_RATE_CLASS_TYPE);
    }

    public String getCostelementSubconFandAUnder25K() {
        return parameterService.getParameterValueAsString(Budget.class, Constants.SUBCONTRACTOR_F_AND_A_LT_25K_PARAM);
    }

    public String getCostelementSubconFandAOver25K() {
        return parameterService.getParameterValueAsString(Budget.class, Constants.SUBCONTRACTOR_F_AND_A_GT_25K_PARAM);
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public BudgetCalculationService getBudgetCalculationService() {
        return budgetCalculationService;
    }

    public void setBudgetCalculationService(BudgetCalculationService budgetCalculationService) {
        this.budgetCalculationService = budgetCalculationService;
    }
}
