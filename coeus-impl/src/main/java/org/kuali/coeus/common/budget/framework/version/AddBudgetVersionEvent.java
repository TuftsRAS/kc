/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.budget.framework.version;

import org.kuali.coeus.common.budget.framework.core.BudgetParent;

public class AddBudgetVersionEvent {

    private String versionName;
    private BudgetParent budgetParent;
    private String errorPath;

    public AddBudgetVersionEvent(String errorPath, BudgetParent budgetParent, String versionName) {
    	this.budgetParent = budgetParent;
    	this.versionName = versionName;
    	this.errorPath = errorPath;
    }

	public BudgetParent getBudgetParent() {
		return budgetParent;
	}

	public void setBudgetParent(BudgetParent budgetParent) {
		this.budgetParent = budgetParent;
	}

	public String getErrorPath() {
		return errorPath;
	}

	public void setErrorPath(String errorPath) {
		this.errorPath = errorPath;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
}

