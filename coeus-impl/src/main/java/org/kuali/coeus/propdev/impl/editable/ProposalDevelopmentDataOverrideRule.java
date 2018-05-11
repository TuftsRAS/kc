/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.editable;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.persistence.KcPersistenceStructureService;
import org.kuali.coeus.sys.framework.rule.KcTransactionalDocumentRuleBase;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kns.datadictionary.validation.charlevel.AnyCharacterValidationPattern;
import org.kuali.rice.kns.datadictionary.validation.charlevel.NumericValidationPattern;
import org.kuali.rice.krad.datadictionary.validation.ValidationPattern;
import org.kuali.rice.krad.util.GlobalVariables;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Business Rule to determine if it valid for the user to override the
 * given Proposal Development Document data.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class ProposalDevelopmentDataOverrideRule extends KcTransactionalDocumentRuleBase implements ProposalDataOverrideRule {

    private static Map<String, String> validationClasses = new HashMap<>();
    private static final String DATE="DATE";
    static {
        validationClasses.put("STRING", AnyCharacterValidationPattern.class.getName());
        validationClasses.put("NUMBER", NumericValidationPattern.class.getName());
    }

    private KcPersistenceStructureService kcPersistenceStructureService;
    private DateTimeService dateTimeService;

    @Override
    public boolean processProposalDataOverrideRules(ProposalDataOverrideEvent proposalDataOverrideEvent) {
        ProposalChangedData proposalOverriddenData = proposalDataOverrideEvent.getProposalChangedData();
        DevelopmentProposal developmentProposal = proposalDataOverrideEvent.getDevelopmentProposal();
        boolean valid = true;

        
        String overriddenValue = proposalOverriddenData.getChangedValue();

        Map<String, String> columnToAttributesMap = getKcPersistenceStructureService().getDBColumnToObjectAttributeMap(DevelopmentProposal.class);

        
        if (StringUtils.isEmpty(proposalOverriddenData.getColumnName())) {
            GlobalVariables.getMessageMap().putError("newProposalChangedData.columnName", KeyConstants.ERROR_NO_FIELD_TO_EDIT);
            return false;
        }

        String overriddenName = getDataDictionaryService().getAttributeErrorLabel(DevelopmentProposal.class, columnToAttributesMap.get(proposalOverriddenData.getColumnName()));
        Boolean isRequiredField = getDataDictionaryService().isAttributeRequired(DevelopmentProposal.class, columnToAttributesMap.get(proposalOverriddenData.getColumnName()));
        boolean isPrimitiveField = getDataObjectService().wrap(developmentProposal).getPropertyType(proposalOverriddenData.getAttributeName()).isPrimitive();

        if(StringUtils.isNotEmpty(proposalOverriddenData.getChangedValue())) {
            valid &= validateAttributeFormat(proposalOverriddenData, developmentProposal);
        }
        
        if ((isRequiredField || isPrimitiveField) && StringUtils.isEmpty(overriddenValue)){
            valid = false;
            GlobalVariables.getMessageMap().putError("newProposalChangedData.changedValue", RiceKeyConstants.ERROR_REQUIRED, overriddenName);
        }
        
        
        if(StringUtils.isNotEmpty(proposalOverriddenData.getComments())) {
            int commentsMaxLength = getDataDictionaryService().getAttributeMaxLength(ProposalChangedData.class, "comments");
            String commentsLabel = getDataDictionaryService().getAttributeLabel(ProposalChangedData.class, "comments");
            if (commentsMaxLength < proposalOverriddenData.getComments().length()) {
                GlobalVariables.getMessageMap().putError(Constants.PROPOSALDATA_COMMENTS_KEY, RiceKeyConstants.ERROR_MAX_LENGTH,
                        commentsLabel, commentsMaxLength + "");
                return false;
            }
        }
        
        return valid;
    }
    
    /**
     * This method is to validate the format/length of custom attribute.
     */
    private boolean validateAttributeFormat(ProposalChangedData proposalOverriddenData, DevelopmentProposal developmentProposal) {
        
        String overriddenValue = proposalOverriddenData.getChangedValue();
        String changedValueLabel = getDataDictionaryService().getAttributeLabel(ProposalChangedData.class, "changedValue");
        
        String dataType = null;
        Integer maxLength = -1;
        
        if(proposalOverriddenData.getEditableColumn() != null) {
            dataType = proposalOverriddenData.getEditableColumn().getDataType();
            maxLength = proposalOverriddenData.getEditableColumn().getDataLength();
        }
        
        ValidationPattern validationPattern = null;

        
        if( DATE.equalsIgnoreCase(dataType) ) {
            try {
                getDateTimeService().convertToDate(overriddenValue);
            }
            catch (ParseException e) {
                GlobalVariables.getMessageMap().putError(Constants.PROPOSALDATA_CHANGED_VAL_KEY, RiceKeyConstants.ERROR_INVALID_FORMAT,
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
                    throw new RuntimeException("Error in instantiating a ValidationPatternClass for Proposal Data Overriding", e);
                }
            }

            if(validationPattern != null) {
                Pattern validationExpression = validationPattern.getRegexPattern();
                if (validationExpression != null && !validationExpression.pattern().equals(".*")) {
                    if (!validationExpression.matcher(overriddenValue).matches()) {
                        GlobalVariables.getMessageMap().putError(Constants.PROPOSALDATA_CHANGED_VAL_KEY, RiceKeyConstants.ERROR_INVALID_FORMAT,
                                changedValueLabel, overriddenValue);
                        return false;
                    }
                }
            }
        }
        
        if ((maxLength != null) && (maxLength < overriddenValue.length())) {
            GlobalVariables.getMessageMap().putError(Constants.PROPOSALDATA_CHANGED_VAL_KEY, RiceKeyConstants.ERROR_MAX_LENGTH,
                    changedValueLabel, maxLength.toString());
            return false;
        }

        try {
            Object currentValue = PropertyUtils.getNestedProperty(developmentProposal,proposalOverriddenData.getAttributeName());
            String currentValueStr = (currentValue != null) ? currentValue.toString() : "";
            if(DATE.equalsIgnoreCase( proposalOverriddenData.getEditableColumn().getDataType()) && currentValue != null) {
                    currentValueStr = getDateTimeService().toString((Date) currentValue, "MM/dd/yyyy");
            }

            if(StringUtils.isNotEmpty(currentValueStr) && currentValueStr.equalsIgnoreCase(overriddenValue)) {
                GlobalVariables.getMessageMap().putError(Constants.PROPOSALDATA_CHANGED_VAL_KEY, KeyConstants.PROPOSAL_DATA_OVERRIDE_SAME_VALUE,
                        proposalOverriddenData.getEditableColumn().getColumnLabel(), (proposalOverriddenData.getDisplayValue() != null) ? proposalOverriddenData.getDisplayValue() : overriddenValue);
                return false;
            }
        }catch (Exception e) {
            throw new RuntimeException("Error retrieving " + proposalOverriddenData.getAttributeName() + " from the proposal", e);
        }
        
        return true;
    }

    protected KcPersistenceStructureService getKcPersistenceStructureService () {
        if (kcPersistenceStructureService == null) {
            kcPersistenceStructureService = KcServiceLocator.getService(KcPersistenceStructureService.class);
        }
        return kcPersistenceStructureService;
    }

    protected DateTimeService getDateTimeService (){
        if (dateTimeService == null) {
            dateTimeService = KcServiceLocator.getService(DateTimeService.class);
        }
        return dateTimeService;
    }
}
