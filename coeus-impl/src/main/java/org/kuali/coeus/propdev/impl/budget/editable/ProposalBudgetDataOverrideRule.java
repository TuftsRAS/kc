/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.budget.editable;


import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.persistence.KcPersistenceStructureService;
import org.kuali.coeus.sys.framework.rule.KcTransactionalDocumentRuleBase;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.coeus.common.budget.framework.core.Budget;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kns.datadictionary.validation.charlevel.AnyCharacterValidationPattern;
import org.kuali.rice.kns.datadictionary.validation.charlevel.NumericValidationPattern;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.datadictionary.validation.ValidationPattern;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ProposalBudgetDataOverrideRule extends KcTransactionalDocumentRuleBase implements BudgetDataOverrideRule {

    private static Map<String, String> validationClasses = new HashMap<>();
    private static final String DATE = "DATE";
    static {
        validationClasses.put("STRING", AnyCharacterValidationPattern.class.getName());
        validationClasses.put("NUMBER", NumericValidationPattern.class.getName());
    }
    private KcPersistenceStructureService kcPersistenceStructureService;
    private DataDictionaryService dataDictionaryService;
    private DateTimeService dateTimeService;

    protected KcPersistenceStructureService getKcPersistenceStructureService () {
        if (kcPersistenceStructureService == null)
            kcPersistenceStructureService = KcServiceLocator.getService(KcPersistenceStructureService.class);
        return kcPersistenceStructureService;
    }
    @Override
    protected DataDictionaryService getDataDictionaryService(){
        if (dataDictionaryService == null)
            dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
        return dataDictionaryService;
    }

    protected DateTimeService getDateTimeService (){
        if(dateTimeService == null)
            dateTimeService = KcServiceLocator.getService(DateTimeService.class);
        return dateTimeService;
    }
    @Override
    public boolean processBudgetDataOverrideRules(BudgetDataOverrideEvent budgetDataOverrideEvent) {
        DevelopmentProposal developmentProposal = ((ProposalDevelopmentDocument) budgetDataOverrideEvent.getDocument()).getDevelopmentProposal();
        Budget finalBudget = developmentProposal.getFinalBudget();

        BudgetChangedData budgetOverriddenData = budgetDataOverrideEvent.getBudgetChangedData();
        boolean valid = true;
        DataDictionaryService dataDictionaryService = getDataDictionaryService();
        String overriddenValue = budgetOverriddenData.getChangedValue();
        KcPersistenceStructureService kraPersistenceStructureService = getKcPersistenceStructureService();
        Map<String, String> columnToAttributesMap = kraPersistenceStructureService.getDBColumnToObjectAttributeMap(Budget.class);
        String overriddenName = dataDictionaryService.getAttributeErrorLabel(Budget.class, columnToAttributesMap.get(budgetOverriddenData.getColumnName()));
        Boolean isRequiredField = dataDictionaryService.isAttributeRequired(Budget.class, columnToAttributesMap.get(budgetOverriddenData.getColumnName()));
        boolean isPrimitiveField = getDataObjectService().wrap(finalBudget).getPropertyType(budgetOverriddenData.getAttributeName()).isPrimitive();

        if (StringUtils.isEmpty(budgetOverriddenData.getColumnName())) {
            valid = false;
            GlobalVariables.getMessageMap().putError("newBudgetChangedData.columnName", KeyConstants.ERROR_NO_FIELD_TO_EDIT);
        }
        
        if(StringUtils.isNotEmpty(budgetOverriddenData.getChangedValue())) {
            valid &= validateAttributeFormat(budgetDataOverrideEvent, columnToAttributesMap);
        }
        
        if ((isRequiredField || isPrimitiveField) && StringUtils.isEmpty(overriddenValue)){
            valid = false;
            GlobalVariables.getMessageMap().putError("newBudgetChangedData.changedValue", RiceKeyConstants.ERROR_REQUIRED, overriddenName);
        }
        
        if(StringUtils.isNotEmpty(budgetOverriddenData.getComments())) {
            int commentsMaxLength = dataDictionaryService.getAttributeMaxLength(BudgetChangedData.class, "comments");
            String commentsLabel = dataDictionaryService.getAttributeLabel(BudgetChangedData.class, "comments");
            if (commentsMaxLength < budgetOverriddenData.getComments().length()) {
                GlobalVariables.getMessageMap().putError(Constants.BUDGETDATA_COMMENTS_KEY, RiceKeyConstants.ERROR_MAX_LENGTH,
                        commentsLabel, commentsMaxLength+"");
                return false;
            }
        }
        return valid;
    }

    private boolean validateAttributeFormat(BudgetDataOverrideEvent budgetDataOverrideEvent, Map<String, String> columnToAttributesMap) {

        BudgetChangedData budgetOverriddenData = budgetDataOverrideEvent.getBudgetChangedData();


        DateTimeService dateTimeService = getDateTimeService();

        String overriddenValue = budgetOverriddenData.getChangedValue();
        String changedValueLabel = dataDictionaryService.getAttributeLabel(BudgetChangedData.class, "changedValue");

        String dataType = null;
        Integer maxLength = -1;
        
        if(budgetOverriddenData.getEditableColumn() != null) {
            dataType = budgetOverriddenData.getEditableColumn().getDataType();
            maxLength = budgetOverriddenData.getEditableColumn().getDataLength();
        }
        
        ValidationPattern validationPattern = null;

        
        if( DATE.equalsIgnoreCase(dataType) ) {
            try {
                dateTimeService.convertToDate(overriddenValue);
            }
            catch (ParseException e) {
                GlobalVariables.getMessageMap().putError(Constants.BUDGETDATA_CHANGED_VAL_KEY, RiceKeyConstants.ERROR_INVALID_FORMAT,
                        changedValueLabel, overriddenValue);
                return false;
            }
        } else {
            String validationClassName = validationClasses.get(dataType);
            if(StringUtils.isNotEmpty(validationClassName)) {
                try {
                    validationPattern = (ValidationPattern) Class.forName(validationClasses.get(dataType)).newInstance();
                    if (dataType.equalsIgnoreCase("STRING")) {
                        ((org.kuali.rice.kns.datadictionary.validation.charlevel.AnyCharacterValidationPattern) validationPattern)
                        .setAllowWhitespace(true);
                    }
                }
                catch (Exception e) {  
                    throw new RuntimeException("Error in instantiating a ValidationPatternClass for Budget Data Overriding", e);
                }
            } else {
                //throw error
            }
            
            if(validationPattern != null) {
                Pattern validationExpression = validationPattern.getRegexPattern();
                if (validationExpression != null && !validationExpression.pattern().equals(".*")) {
                    if (!validationExpression.matcher(overriddenValue).matches()) {
                        GlobalVariables.getMessageMap().putError(Constants.BUDGETDATA_CHANGED_VAL_KEY, RiceKeyConstants.ERROR_INVALID_FORMAT,
                                changedValueLabel, overriddenValue);
                        return false;
                    }
                }
            }
        }

        DevelopmentProposal developmentProposal = ((ProposalDevelopmentDocument) budgetDataOverrideEvent.getDocument()).getDevelopmentProposal();

        Budget finalBudget = developmentProposal.getFinalBudget();
        
        Object currentValue = finalBudget != null ? ObjectUtils.getPropertyValue(finalBudget, columnToAttributesMap.get(budgetOverriddenData.getColumnName())) : null;
        if (currentValue instanceof ScaleTwoDecimal) {
            try {
                Double overriddenValueToInt = Double.parseDouble(overriddenValue); 
            } catch (Exception e) {
                GlobalVariables.getMessageMap().putError(Constants.BUDGETDATA_CHANGED_VAL_KEY, RiceKeyConstants.ERROR_NUMBER,
                        changedValueLabel, overriddenValue);
                return false;
            } 
        }
        
        if ((maxLength != null) && (maxLength < overriddenValue.length())) {
            if (!(currentValue instanceof Boolean)) {
                GlobalVariables.getMessageMap().putError(Constants.BUDGETDATA_CHANGED_VAL_KEY, RiceKeyConstants.ERROR_MAX_LENGTH,
                        changedValueLabel, maxLength.toString());
                return false;
            }
        }
        
        String currentValueStr = (currentValue != null) ? currentValue.toString() : "";
        
        if(DATE.equalsIgnoreCase( budgetOverriddenData.getEditableColumn().getDataType()) && currentValue != null) {
                currentValueStr = dateTimeService.toString((Date) currentValue, "MM/dd/yyyy");
        }
        
        if (StringUtils.isNotEmpty(currentValueStr) && currentValueStr.equalsIgnoreCase(overriddenValue)) {
            if (!(currentValue instanceof Boolean)) {
                GlobalVariables.getMessageMap().putError(
                        Constants.BUDGETDATA_CHANGED_VAL_KEY,
                        KeyConstants.BUDGET_DATA_OVERRIDE_SAME_VALUE,
                        budgetOverriddenData.getEditableColumn().getColumnLabel(),
                        (budgetOverriddenData.getDisplayValue() != null) ? budgetOverriddenData.getDisplayValue()
                                : overriddenValue);
                return false;
            }

        }
        
        return true;
    }
}
