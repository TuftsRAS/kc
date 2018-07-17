/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.budget.impl.core;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.budget.framework.core.*;
import org.kuali.coeus.common.budget.framework.query.QueryList;
import org.kuali.coeus.common.budget.api.rate.RateClassType;
import org.kuali.coeus.common.budget.framework.query.operator.Equals;
import org.kuali.coeus.common.budget.framework.nonpersonnel.BudgetLineItem;
import org.kuali.coeus.common.budget.framework.nonpersonnel.BudgetLineItemBase;
import org.kuali.coeus.common.budget.framework.period.BudgetPeriod;
import org.kuali.coeus.common.budget.framework.personnel.BudgetPersonnelDetails;
import org.kuali.coeus.common.budget.framework.rate.BudgetRate;
import org.kuali.coeus.common.budget.framework.rate.ValidCeRateType;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

public abstract class AbstractBudgetService<T extends BudgetParent> implements BudgetService<T> {

    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(AbstractBudgetService.class);
    public static final String BUDGET_ID = "budgetId";
    public static final String VALID_CE_RATE_TYPES = "validCeRateTypes";
    public static final String RATE_CLASS_TYPE = "rateClassType";
    public static final String ACTIVITY_TYPE_CODE = "activityTypeCode";
    public static final String ACTIVE = "active";
    public static final String COST_SHARE_TYPE = "costShareType";

    @Autowired
    @Qualifier("businessObjectService")
    private BusinessObjectService businessObjectService;

    @Autowired
    @Qualifier("parameterService")
    private ParameterService parameterService;

    @Autowired
    @Qualifier("legacyDataAdapter")
    private LegacyDataAdapter legacyDataAdapter;

    @Override
    public Budget addBudgetVersion(BudgetParentDocument<T> budgetParentDocument, String versionName, Map<String,Object> options) {
        if (!isBudgetVersionNameValid(budgetParentDocument.getBudgetParent(), versionName)) {
            LOG.debug("Buffered Version not Valid");
            return null;
        }

        return getNewBudgetVersion(budgetParentDocument, versionName, options);
    }

    protected abstract Budget getNewBudgetVersion(BudgetParentDocument<T> parent, String versionName, Map<String, Object> options);

    @Override
    public boolean validInflationCeRate(BudgetLineItemBase budgetLineItem) {
        final CostElement costElementBO = getCostElement(budgetLineItem.getCostElement());

        budgetLineItem.setCostElementBO(costElementBO);
        final List<ValidCeRateType> validCeRateTypes = costElementBO.getValidCeRateTypes();
        final QueryList<ValidCeRateType> qValidCeRateTypes = validCeRateTypes == null ? new QueryList<>() : new QueryList<>(validCeRateTypes);

        // Check whether it contains Inflation Rate
        final Equals eqInflation = new Equals(RATE_CLASS_TYPE, RateClassType.INFLATION.getRateClassType());
        final QueryList<ValidCeRateType> inflationValidCeRates = qValidCeRateTypes.filter(eqInflation);
        return !inflationValidCeRates.isEmpty();
    }

    protected CostElement getCostElement(String costElement) {
        CostElement costElementBO = businessObjectService.findBySinglePrimaryKey(CostElement.class, costElement);
        costElementBO.refreshReferenceObject(VALID_CE_RATE_TYPES);
        return costElementBO;
    }

    @Override
    public String getActivityTypeForBudget(Budget budget) {
        BudgetParent budgetParent = budget.getBudgetParent().getDocument().getBudgetParent();
        Map<String,Object> qMap = new HashMap<>();
        qMap.put(BUDGET_ID, budget.getBudgetId());
        List<BudgetRate> allPropRates = (List<BudgetRate>)businessObjectService.findMatching(BudgetRate.class, qMap);
        if (CollectionUtils.isNotEmpty(allPropRates)) {
            qMap.put(ACTIVITY_TYPE_CODE, budgetParent.getActivityTypeCode());
            Collection<BudgetRate> matchActivityTypePropRates =businessObjectService.findMatching(
                BudgetRate.class, qMap);
            if (CollectionUtils.isNotEmpty(matchActivityTypePropRates)) {
                for (BudgetRate budgetRate : allPropRates) { 
                    if (!budgetRate.getActivityTypeCode().equals(budgetParent.getActivityTypeCode())) {
                        return budgetRate.getActivityTypeCode();
                    }
                }
                return budgetParent.getActivityTypeCode();                
            } else {
                return allPropRates.get(0).getActivityTypeCode();
            }
        }
                
        return "x";
        
    }

    
    @Override
    public Collection<BudgetRate> getSavedProposalRates(Budget budgetToOpen) {
        Map<String,Long> qMap = new HashMap<>();
        qMap.put(BUDGET_ID,budgetToOpen.getBudgetId());
        return businessObjectService.findMatching(BudgetRate.class, qMap);
    }

    /**
     * 
     * Do this so that new personnel details(or copied ones) can be calculated
     */
    protected void copyLineItemToPersonnelDetails(Budget budget) {
        for (BudgetPeriod budgetPeriod : budget.getBudgetPeriods()) {
            if (budgetPeriod.getBudgetLineItems() != null && !budgetPeriod.getBudgetLineItems().isEmpty()) {
                for (BudgetLineItem budgetLineItem : budgetPeriod.getBudgetLineItems()) {        
                    if (budgetLineItem.getBudgetPersonnelDetailsList() != null && !budgetLineItem.getBudgetPersonnelDetailsList().isEmpty()) {
                        for (BudgetPersonnelDetails budgetPersonnelDetails : budgetLineItem.getBudgetPersonnelDetailsList()) {
                        	budgetPersonnelDetails.setBudget(budgetLineItem.getBudget());
                            budgetPersonnelDetails.setBudgetId(budgetLineItem.getBudgetId());
                            budgetPersonnelDetails.setBudgetPeriod(budgetLineItem.getBudgetPeriod());
                            budgetPersonnelDetails.setLineItemNumber(budgetLineItem.getLineItemNumber());
                            budgetPersonnelDetails.setCostElement(budgetLineItem.getCostElement());
                            budgetPersonnelDetails.setCostElementBO(budgetLineItem.getCostElementBO());
                       }
                    }
                }
            }
        }
    }

    @Override
    public boolean isValidSourceAccountCostShareType(String validationMessageType, CostShare budgetCostShare, String costShareField) {
        boolean valid = Boolean.TRUE;
        if (isCostShareTypeSourceAccountValidationEnabled()) {
            Collection<ValidSourceAccountsCostShareType> activeValidSourceAccountCostSharetypes = getMatchingValidSourceAccountsCostShareTypes();
            if (activeValidSourceAccountCostSharetypes.size() != 0) {
                final boolean validMatches = activeValidSourceAccountCostSharetypes.stream().anyMatch(validSourceAccountsCostShareType ->
                        validSourceAccountsCostShareType.getCostShareTypeCode().equals(budgetCostShare.getCostShareTypeCode())
                                && budgetCostShare.getSourceAccount().equalsIgnoreCase(validSourceAccountsCostShareType.getAccount().getAccountNumber()));
                if (!validMatches) {
                    refreshReference(budgetCostShare);
                    valid = addValidationMessage(validationMessageType, costShareField, KeyConstants.INVALID_SOURCE_ACCOUNT_COST_SHARE_TYPE,
                            budgetCostShare.getCostShareTypeCode() != null ? budgetCostShare.getCostShareType().getDescription() : "", budgetCostShare.getSourceAccount());
                }
            }
        }
        return valid;
    }

    public void refreshReference(CostShare budgetCostShare) {
        legacyDataAdapter.refreshReferenceObject(budgetCostShare, COST_SHARE_TYPE);
    }

    public boolean isCostShareTypeSourceAccountValidationEnabled() {
        return getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_SYSTEM, Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, Constants.ENABLE_COST_SHARE_TYPE_SOURCE_ACCOUNT_VALIDATION);
    }

    public Collection<ValidSourceAccountsCostShareType> getMatchingValidSourceAccountsCostShareTypes() {
        Map<String, Object> fields = new HashMap<>();
        fields.put(ACTIVE, Boolean.TRUE);
        return getBusinessObjectService().findMatching(ValidSourceAccountsCostShareType.class, fields);
    }

    public boolean addValidationMessage(String validationMessageType, String field, String errorMessageKey, String... errorParameters) {
        if (StringUtils.equalsIgnoreCase(Constants.VALIDATION_MESSAGE_ERROR, validationMessageType)) {
            getGlobalVariableService().getMessageMap().putError(field, errorMessageKey, errorParameters);
            return Boolean.FALSE;
        } else if (StringUtils.equalsIgnoreCase(Constants.VALIDATION_MESSAGE_WARNING, validationMessageType)) {
            getGlobalVariableService().getMessageMap().putWarning(field, errorMessageKey, errorParameters);
            return Boolean.TRUE;
        }
        return Boolean.TRUE;
    }

    protected boolean isBudgetFormulatedCostEnabled() {
        String formulatedCostEnabled = getParameterService().getParameterValueAsString(Budget.class, Constants.FORMULATED_COST_ENABLED);
        return (formulatedCostEnabled!=null && formulatedCostEnabled.equalsIgnoreCase("Y"));
    }
    protected List<String> getFormulatedCostElements() {
        String formulatedCEsValue = getParameterService().getParameterValueAsString(Budget.class, Constants.FORMULATED_COST_ELEMENTS);
        String[] formulatedCEs = formulatedCEsValue==null?new String[0]:formulatedCEsValue.split(",");
        return Arrays.asList(formulatedCEs);
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public abstract GlobalVariableService getGlobalVariableService();

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
