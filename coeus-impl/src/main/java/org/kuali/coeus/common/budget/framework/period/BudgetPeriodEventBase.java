/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.budget.framework.period;

import org.kuali.coeus.common.budget.framework.core.Budget;
import org.kuali.coeus.common.budget.framework.core.BudgetEventBase;

public abstract class BudgetPeriodEventBase extends BudgetEventBase {

    private BudgetPeriod budgetPeriod;
    private int budgetPeriodNumber;

    protected BudgetPeriodEventBase(Budget budget, BudgetPeriod budgetPeriod) {
    	super(budget);
        this.budgetPeriod = budgetPeriod;
    }

    protected BudgetPeriodEventBase(Budget budget, BudgetPeriod budgetPeriod, String errorPath) {
    	super(budget, errorPath);
        this.budgetPeriod = budgetPeriod;
    }
    
    /**
     * @return <code>{@link BudgetPeriod}</code> that triggered this event.
     */
    public BudgetPeriod getBudgetPeriod() {
        return budgetPeriod;
    }

    /**
     * @return <code>{@link BudgetPeriod}</code> that triggered this event.
     */
    public int getBudgetPeriodNumber() {
        return budgetPeriodNumber;
    }
}

