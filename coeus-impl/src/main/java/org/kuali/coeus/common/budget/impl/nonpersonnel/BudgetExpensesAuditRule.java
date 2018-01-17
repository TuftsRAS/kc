/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.budget.impl.nonpersonnel;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.coeus.common.budget.framework.core.Budget;
import org.kuali.coeus.common.budget.framework.core.BudgetAuditEvent;
import org.kuali.coeus.common.budget.framework.core.BudgetAuditRuleBase;
import org.kuali.coeus.common.budget.framework.core.BudgetAuditRuleEvent;
import org.kuali.coeus.common.budget.framework.core.BudgetConstants;
import org.kuali.coeus.common.budget.framework.nonpersonnel.BudgetExpenseService;
import org.kuali.coeus.common.budget.framework.nonpersonnel.BudgetLineItem;
import org.kuali.coeus.common.budget.framework.period.BudgetPeriod;
import org.kuali.coeus.common.budget.framework.personnel.BudgetPerson;
import org.kuali.coeus.common.budget.framework.personnel.BudgetPersonnelDetails;
import org.kuali.coeus.common.framework.ruleengine.KcBusinessRule;
import org.kuali.coeus.common.framework.ruleengine.KcEventMethod;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.AuditCluster;
import org.kuali.rice.krad.util.AuditError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

@KcBusinessRule("budgetExpensesAuditRule")
public class BudgetExpensesAuditRule extends BudgetAuditRuleBase {

    private static final String WARN_NEGATIVE_UNRECOVERED_F_AND_A_PARM = "WARN_NEGATIVE_UNRECOVERED_F_AND_A";
    public static final String BUDGET_MODULAR_KEY = "budget.modular";
    @Autowired
	@Qualifier("budgetExpenseService")
	private BudgetExpenseService budgetExpenseService;

    @Autowired
    @Qualifier("parameterService")
    private ParameterService parameterService;

    /**
     * 
     * This method is to validate budget expense business rules
     * 
     */
	@KcEventMethod
	@Deprecated
    public boolean processRunAuditBusinessRules(BudgetAuditEvent event) {
		Budget budget = event.getBudget();
        boolean retval = true;
        
        if ( budget.getTotalCostLimit().isGreaterThan(ScaleTwoDecimal.ZERO) &&
        		budget.getTotalCost().isGreaterThan(budget.getTotalCostLimit()) ) {
            String key = "budgetParametersOverviewWarnings";
            AuditCluster auditCluster = getGlobalVariableService().getAuditErrorMap().get(key);
            if (auditCluster == null) {
                List<AuditError> auditErrors = new ArrayList<>();
                auditCluster = new AuditCluster(Constants.BUDGET_PARAMETERS_OVERVIEW_PANEL_NAME, auditErrors, Constants.AUDIT_WARNINGS);
                getGlobalVariableService().getAuditErrorMap().put(key, auditCluster);
            }
            @SuppressWarnings("unchecked")
            List<AuditError> auditErrors = auditCluster.getAuditErrorList();
            auditErrors.add(new AuditError("document.budget.totalCostLimit", 
                    KeyConstants.WARNING_TOTAL_COST_LIMIT_EXCEEDED, 
                    Constants.BUDGET_PARAMETERS_PAGE_METHOD + "." + Constants.BUDGET_PARAMETERS_OVERVIEW_PANEL_ANCHOR));
            retval=false;
            
        }
       for (BudgetPeriod budgetPeriod : budget.getBudgetPeriods()) {
            if(budgetPeriod.getTotalCostLimit().isGreaterThan(ScaleTwoDecimal.ZERO) && budgetPeriod.getTotalCost().isGreaterThan(budgetPeriod.getTotalCostLimit())){
                String key = "budgetPeriodProjectDateAuditWarnings";
                AuditCluster auditCluster = getGlobalVariableService().getAuditErrorMap().get(key);
                if (auditCluster == null) {
                    List<AuditError> auditErrors = new ArrayList<>();
                    auditCluster = new AuditCluster(Constants.BUDGET_PARAMETERS_TOTALS_PANEL_NAME, auditErrors, Constants.AUDIT_WARNINGS);
                    getGlobalVariableService().getAuditErrorMap().put(key, auditCluster);
                }

                @SuppressWarnings("unchecked")
                List<AuditError> auditErrors = auditCluster.getAuditErrorList();
                auditErrors.add(new AuditError("document.budget.budgetPeriods[" + (budgetPeriod.getBudgetPeriod() - 1) + "].totalCostLimit", 
                        KeyConstants.WARNING_PERIOD_COST_LIMIT_EXCEEDED, 
                        Constants.BUDGET_PARAMETERS_PAGE_METHOD + "." + Constants.BUDGET_PARAMETERS_TOTALS_PANEL_ANCHOR));
                retval=false;
            } 
            
            // budget personnel budget effective warning 
            int j = 0;
            for (BudgetLineItem budgetLineItem : budgetPeriod.getBudgetLineItems()) {
                final String panelName = budgetExpenseService.getBudgetExpensePanelName(budgetPeriod, budgetLineItem);
                
                if (budgetLineItem.getUnderrecoveryAmount() != null && budgetLineItem.getUnderrecoveryAmount().isNegative() &&
                        parameterService.getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_BUDGET, Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, WARN_NEGATIVE_UNRECOVERED_F_AND_A_PARM) ) {
                    final String key = "budgetNonPersonnelAuditWarnings" + budgetPeriod.getBudgetPeriod() + panelName;
                    AuditCluster auditCluster = getGlobalVariableService().getAuditErrorMap().get(key);
                    if (auditCluster == null) {
                        final List<AuditError> auditErrors = new ArrayList<>();
                        auditCluster = new AuditCluster(panelName + " Budget Period "+budgetPeriod.getBudgetPeriod(), auditErrors, Constants.AUDIT_WARNINGS);
                        getGlobalVariableService().getAuditErrorMap().put(key, auditCluster);
                    }

                    @SuppressWarnings("unchecked")
                    final List<AuditError> auditErrors = auditCluster.getAuditErrorList();
                    auditErrors.add(new AuditError("document.budgetPeriod[" + (budgetPeriod.getBudgetPeriod() - 1) + "].budgetLineItem[" + j + "].underrecoveryAmount", KeyConstants.WARNING_UNRECOVERED_FA_NEGATIVE, Constants.BUDGET_EXPENSES_PAGE_METHOD + "." + budgetLineItem.getBudgetCategory().getBudgetCategoryType().getDescription() + "&viewBudgetPeriod=" + budgetPeriod.getBudgetPeriod() + "&selectedBudgetLineItemIndex=" + j + "&activePanelName=" + panelName));
                    retval = false;
                }
                    
                int k = 0;
                for (BudgetPersonnelDetails budgetPersonnelDetails : budgetLineItem.getBudgetPersonnelDetailsList()) {
                    if (StringUtils.isNotEmpty(budgetPersonnelDetails.getEffdtAfterStartdtMsg())) {
                        String key = "budgetPersonnelBudgetAuditWarnings"+budgetPeriod.getBudgetPeriod();
                        AuditCluster auditCluster = getGlobalVariableService().getAuditErrorMap().get(key);
                        if (auditCluster == null) {
                            List<AuditError> auditErrors = new ArrayList<>();
                            auditCluster = new AuditCluster(Constants.PERSONNEL_BUDGET_PANEL_NAME + " (Period " +budgetPeriod.getBudgetPeriod()+")", auditErrors, Constants.AUDIT_WARNINGS);
                            getGlobalVariableService().getAuditErrorMap().put(key, auditCluster);
                        }
                        @SuppressWarnings("unchecked")
                        List<AuditError> auditErrors = auditCluster.getAuditErrorList();
                        auditErrors.add(new AuditError("document.budgetPeriod[" + (budgetPeriod.getBudgetPeriod() - 1) + "].budgetLineItem["+j+"].budgetPersonnelDetailsList["+k+"].salaryRequested", KeyConstants.WARNING_EFFDT_AFTER_PERIOD_START_DATE, Constants.BUDGET_EXPENSES_PAGE_METHOD + "." + Constants.BUDGET_EXPENSES_OVERVIEW_PANEL_ANCHOR + "&viewBudgetPeriod=" + budgetPeriod.getBudgetPeriod() + "&selectedBudgetLineItemIndex=" + j + "&personnelDetailLine="+k, new String[]{budgetPersonnelDetails.getBudgetPerson().getPersonName()}));
                        retval=false;

                    }
                    if (budgetPersonnelDetails.getBudgetPerson().getCalculationBase().equals(ScaleTwoDecimal.ZERO)) {
                        String key = "budgetPersonnelBudgetAuditWarnings"+budgetPeriod.getBudgetPeriod();
                        AuditCluster auditCluster = getGlobalVariableService().getAuditErrorMap().get(key);
                        if (auditCluster == null) {
                            List<AuditError> auditErrors = new ArrayList<>();
                            auditCluster = new AuditCluster(Constants.PERSONNEL_BUDGET_PANEL_NAME+ " (Period " +budgetPeriod.getBudgetPeriod() +")", auditErrors, Constants.AUDIT_WARNINGS);
                            getGlobalVariableService().getAuditErrorMap().put(key, auditCluster);
                        }

                        @SuppressWarnings("unchecked")
                        List<AuditError> auditErrors = auditCluster.getAuditErrorList();
                        auditErrors.add(new AuditError("document.budgetPeriod[" + (budgetPeriod.getBudgetPeriod() - 1) + "].budgetLineItem["+j+"].budgetPersonnelDetailsList["+k+"].salaryRequested", KeyConstants.WARNING_BASE_SALARY_ZERO, Constants.BUDGET_EXPENSES_PAGE_METHOD + "." + Constants.BUDGET_EXPENSES_OVERVIEW_PANEL_ANCHOR + "&viewBudgetPeriod=" + budgetPeriod.getBudgetPeriod() + "&selectedBudgetLineItemIndex=" + j + "&personnelDetailLine="+k, new String[]{budgetPersonnelDetails.getBudgetPerson().getPersonName()}));
                        retval=false;

                    }

                    k++;
                }
                j++;
            }
        }
        
        return retval;

    }

	@KcEventMethod
    public boolean processRunAuditBusinessRules(BudgetAuditRuleEvent event) {
		Budget budget = event.getBudget();
		boolean auditRulePassed = verifyTotalCostLimit(budget);
        for (BudgetPeriod budgetPeriod : budget.getBudgetPeriods()) {
        	auditRulePassed &= verifyPeriodCostLimit(budgetPeriod);
        }

        for (BudgetLineItem budgetLineItem : budget.getBudgetLineItems()) {
        	auditRulePassed &= verifyUnderRecoveryAmount(budgetLineItem);
        }
       
        for (BudgetPersonnelDetails budgetPersonnelDetails : budget.getBudgetPersonnelDetails()) {
        	auditRulePassed &= verifyPersonnelDetails(budgetPersonnelDetails);
        }
        
        auditRulePassed &= verifyModularBudgetStatus(budget);
        
        return auditRulePassed;
    }

	protected boolean verifyTotalCostLimit(Budget budget) {
        if ( budget.getTotalCostLimit().isGreaterThan(ScaleTwoDecimal.ZERO) &&
        		budget.getTotalCost().isGreaterThan(budget.getTotalCostLimit()) ) {
            BudgetConstants.BudgetAuditRules budgetSettingsRule = BudgetConstants.BudgetAuditRules.BUDGET_SETTINGS;
			List<AuditError> auditErrors = getAuditErrors(budgetSettingsRule, false);
            auditErrors.add(new AuditError("budget.totalCostLimit", KeyConstants.WARNING_TOTAL_COST_LIMIT_EXCEEDED, budgetSettingsRule.getPageId()));
            return false;
        }
        return true;
	}

	protected boolean verifyPeriodCostLimit(BudgetPeriod budgetPeriod) {
        if(budgetPeriod.getTotalCostLimit().isGreaterThan(ScaleTwoDecimal.ZERO) && budgetPeriod.getTotalCost().isGreaterThan(budgetPeriod.getTotalCostLimit())){
            BudgetConstants.BudgetAuditRules budgetPeriodAndTotalRule = BudgetConstants.BudgetAuditRules.PERIODS_AND_TOTALS;
			List<AuditError> auditErrors = getAuditErrors(budgetPeriodAndTotalRule, false);
            auditErrors.add(new AuditError(budgetPeriodAndTotalRule.getPageId(), 
                    KeyConstants.WARNING_PERIOD_COST_LIMIT_EXCEEDED, budgetPeriodAndTotalRule.getPageId(), new String[]{budgetPeriod.getBudgetPeriod().toString()}));
            return false;
        } 
        return true;
	}
	
	protected boolean verifyUnderRecoveryAmount(BudgetLineItem budgetLineItem) {
        if (budgetLineItem.getUnderrecoveryAmount() != null && budgetLineItem.getUnderrecoveryAmount().isNegative()  &&
                parameterService.getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_BUDGET, Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, WARN_NEGATIVE_UNRECOVERED_F_AND_A_PARM)) {
            final BudgetConstants.BudgetAuditRules budgetNonPersonnelRule = BudgetConstants.BudgetAuditRules.NON_PERSONNEL_COSTS;
            final String additionalMessage = " Budget Period "+ budgetLineItem.getBudgetPeriodBO().getBudgetPeriod() + "Line item " + budgetLineItem.getCostElementBO().getDescription();
			final List<AuditError> auditErrors = getAuditErrors(budgetNonPersonnelRule, additionalMessage, false);
            auditErrors.add(new AuditError(budgetNonPersonnelRule.getPageId(), 
            		KeyConstants.WARNING_UNRECOVERED_FA_NEGATIVE, budgetNonPersonnelRule.getPageId(), 
            		new String[]{additionalMessage}));
       }
        return true;
	}
	
	protected boolean verifyPersonnelDetails(BudgetPersonnelDetails budgetPersonnelDetails) {
        boolean passed = true;
		boolean salaryEffectiveAfterStartDate = budgetPersonnelDetails.isPersonSalaryEffectiveDateAfterStartDate();
		boolean personSalaryNotDefined = budgetPersonnelDetails.isPersonBaseSalaryZero();
        if (salaryEffectiveAfterStartDate || personSalaryNotDefined) {
        	BudgetPeriod budgetPeriod = budgetPersonnelDetails.getBudgetLineItem().getBudgetPeriodBO();
        	BudgetPerson budgetPerson = budgetPersonnelDetails.getBudgetPerson();
            BudgetConstants.BudgetAuditRules budgetProjectPersonnelRule = BudgetConstants.BudgetAuditRules.PROJECT_PERSONNEL;
			List<AuditError> auditErrors = getAuditErrors(budgetProjectPersonnelRule, " Budget Period "+ budgetPeriod.getBudgetPeriod(), false);
			String errorKey = budgetProjectPersonnelRule.getPageId();
			if (salaryEffectiveAfterStartDate) {
	             auditErrors.add(new AuditError(errorKey, KeyConstants.WARNING_EFFDT_AFTER_PERIOD_START_DATE,
	            		 budgetProjectPersonnelRule.getPageId(), new String[]{budgetPerson.getPersonName()}));
                passed = false;
			} else {
	            auditErrors.add(new AuditError(errorKey, KeyConstants.WARNING_BASE_SALARY_ZERO, budgetProjectPersonnelRule.getPageId(), 
	            		new String[]{budgetPerson.getPersonName()}));
			}
        }
        return passed;
	}
	

    protected boolean verifyModularBudgetStatus(Budget budget) {
        boolean validBudgetCompletion = true;
        if (budget.getModularBudgetFlag() == true) {
            for (BudgetPeriod budgetPeriod : budget.getBudgetPeriods()) {
                if (budgetPeriod != null) {
                    if (budgetPeriod.getBudgetModular() == null || budgetPeriod.getBudgetModular().getTotalDirectCost() == null) {
                        validBudgetCompletion = false;
                    }
                    else if ((budgetPeriod.getBudgetModular().getTotalDirectCost().equals(ScaleTwoDecimal.ZERO))
                            && budgetPeriod.getTotalDirectCost().isGreaterThan(ScaleTwoDecimal.ZERO)) {
                        validBudgetCompletion = false;
                    }
                    if (!validBudgetCompletion) {
                        final BudgetConstants.BudgetAuditRules budgetModularRule = BudgetConstants.BudgetAuditRules.MODULAR_BUDGET;
                        List<AuditError> auditErrors = getAuditErrors(budgetModularRule, true);
                        auditErrors.add(new AuditError(BUDGET_MODULAR_KEY, KeyConstants.ERROR_MODULARBUDGET_NOT_SYNCED,
                                budgetModularRule.getPageId()));
                        break;
                    }
                }
            }
        }
        return validBudgetCompletion;
    }	
	
	protected BudgetExpenseService getBudgetExpenseService() {
		return budgetExpenseService;
	}

	public void setBudgetExpenseService(BudgetExpenseService budgetExpenseService) {
		this.budgetExpenseService = budgetExpenseService;
	}

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}


