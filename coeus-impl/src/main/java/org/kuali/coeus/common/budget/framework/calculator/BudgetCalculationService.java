/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.budget.framework.calculator;

import org.kuali.coeus.common.budget.framework.core.Budget;
import org.kuali.coeus.common.budget.framework.nonpersonnel.BudgetLineItem;
import org.kuali.coeus.common.budget.framework.nonpersonnel.BudgetLineItemCalculatedAmount;
import org.kuali.coeus.common.budget.framework.period.BudgetPeriod;
import org.kuali.coeus.common.budget.framework.personnel.BudgetPersonnelCalculatedAmount;
import org.kuali.coeus.common.budget.framework.personnel.BudgetPersonnelDetails;
import org.kuali.coeus.common.framework.impl.Period;

import java.util.List;


public interface BudgetCalculationService {
    /**
     * 
     * This method is for calculating the entire budget version and populate the appropriate values 
     * to session (Budget).
     */
    void calculateBudget(Budget budget) ;
    /**
     * 
     * This method is for calculating the entire budget version and populate the appropriate values 
     * to session (Budget).
     */
    void calculateBudgetPeriod(Budget budget,BudgetPeriod budgetPeriod) ;
    /**
     * 
     * This method for calculating non-personnel budget line item. This calculates all calculated amounts
     * and sum it up to cost of the the line item. It populates the appropriate values to session as well.
     * (BudgetLineItemCalculatedAmount, BudgetLineItem)
     */
    void calculateBudgetLineItem(Budget budget,BudgetPersonnelDetails budgetPersonnelDetails);
    /**
     * 
     * This method for calculating non-personnel budget line item. This calculates all calculated amounts
     * and sum it up to cost of the the line item. It populates the appropriate values to session as well.
     * (BudgetLineItemCalculatedAmount, BudgetLineItem)
     */
    void calculateBudgetLineItem(Budget budget,BudgetLineItem budgetLineItem);
    /**
     * 
     * This method is for calculating calculated amounts for each cost element. It looks at all
     * applicable rates for a cost element and calculate all direct and indirect costs.
     */
    void populateCalculatedAmount(Budget budget,BudgetLineItem budgetLineItem);
    /**
     * 
     * This method is for calculating calculated amounts for each cost element. It looks at all
     * applicable rates for a cost element and calculate all direct and indirect costs.
     */
    void rePopulateCalculatedAmount(Budget budget,BudgetLineItem budgetLineItem);

    /**
     * 
     * This method is to calculate the budget totals for budget total page.
     */
    void calculateBudgetSummaryTotals(Budget budget);

    /**
     * This method is to apply budgetlineitem details to all later periods.
     */
    void applyToLaterPeriods(Budget budget, BudgetPeriod budgetPeriod, BudgetLineItem budgetLineItem);
    /**
     * This method is to adjust the line item cost to total cost limit of a period.
     */
    boolean syncToPeriodCostLimit(Budget budget, BudgetPeriod budgetPeriod, BudgetLineItem budgetLineItem);
    /**
     * 
     * This method is for calculating calculated amounts for each cost element. It looks at all
     * applicable rates for a cost element and calculate all direct and indirect costs.
     */
    void populateCalculatedAmount(Budget budget, BudgetPersonnelDetails newBudgetPersonnelDetails);
    /**
     * 
     * This method is for calculating calculated amounts for each cost element. It looks at all
     * applicable rates for a cost element and calculate all direct and indirect costs.
     */
    void rePopulateCalculatedAmount(Budget budget, BudgetPersonnelDetails newBudgetPersonnelDetails);

    /**
     * Synchronize rates between {@link BudgetLineItemCalculatedAmount} instances and {@link BudgetPersonnelCalculatedAmount} instances
     * in a {@link Budget}.
     * 
     *
     * @param budgetLineItem {@link BudgetLineItem} instance to examine amounts for synchronization
     */
    void updatePersonnelBudgetRate(BudgetLineItem budgetLineItem);    
    /**
     * This method is to adjust the line item cost to total direct cost limit of a period.
     */
    boolean syncToPeriodDirectCostLimit(Budget budget, BudgetPeriod budgetPeriod, BudgetLineItem budgetLineItem);
    
    List<Period> retrieveBudgetSummaryTotals(Budget budget);
    
    void updateBudgetTotalCost(Budget budget);
    
    String getPersonnelBudgetCategoryTypeCode();
    
    void resetBudgetLineItemCalculatedAmounts(Budget budget);
    
    void calculateAndUpdateFormulatedCost(BudgetLineItem budgetLineItem);

    void populateBudgetPeriodSummaryCalcAmounts(Budget budget);

}
