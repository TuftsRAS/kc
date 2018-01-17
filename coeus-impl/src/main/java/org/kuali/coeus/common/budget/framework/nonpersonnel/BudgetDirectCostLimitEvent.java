/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.budget.framework.nonpersonnel;

import org.kuali.coeus.common.budget.framework.core.Budget;
import org.kuali.coeus.common.budget.framework.period.BudgetPeriod;

public class BudgetDirectCostLimitEvent extends BudgetCostLimitEvent {
	
	public BudgetDirectCostLimitEvent(Budget budget, BudgetPeriod budgetPeriod, BudgetLineItem budgetLineItem, String errorPath) {
		super(budget, budgetPeriod, budgetLineItem, errorPath);
	}
}
