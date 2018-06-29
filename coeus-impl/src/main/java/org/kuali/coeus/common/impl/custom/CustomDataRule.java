/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.custom;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.custom.AuditCustomDataEvent;
import org.kuali.coeus.common.framework.custom.DocumentCustomData;
import org.kuali.coeus.common.framework.custom.SaveCustomDataEvent;
import org.kuali.coeus.common.framework.custom.arg.ArgValueLookup;
import org.kuali.coeus.common.framework.custom.attr.CustomAttribute;
import org.kuali.coeus.common.framework.custom.attr.CustomAttributeDataType;
import org.kuali.coeus.common.framework.custom.attr.CustomAttributeDocument;
import org.kuali.coeus.common.framework.custom.attr.CustomAttributeService;
import org.kuali.coeus.common.framework.person.KcPerson;
import org.kuali.coeus.common.framework.person.KcPersonService;
import org.kuali.coeus.propdev.impl.custom.AuditProposalCustomDataEvent;
import org.kuali.coeus.sys.framework.rule.KcBusinessRule;
import org.kuali.coeus.sys.framework.rule.KcTransactionalDocumentRuleBase;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.datadictionary.validation.charlevel.NumericValidationPattern;
import org.kuali.rice.kns.datadictionary.validation.charlevel.UTF8AnyCharacterValidationPattern;
import org.kuali.rice.krad.datadictionary.validation.ValidationPattern;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Validates the rules for a Custom Attribute save action.
 */
public class CustomDataRule extends KcTransactionalDocumentRuleBase implements KcBusinessRule<SaveCustomDataEvent> {

    private static final String STRING = "String";
    private static final String DATE = "Date";
    private static final String NUMBER = "Number";
    public static final String ARGUMENT_NAME = "argumentName";
    public static final String USER_NAME = "userName";
    private static String CUSTOMATTRIBUTE_ERROR_START = "[";
    private static String CUSTOMATTRIBUTE_ERROR_END = "].value";   
    private static DateFormat dateFormat;
    private static final String DATE_REGEX = "(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])/(19|2[0-9])[0-9]{2}";
    private static Map<String, ValidationPattern> VALIDATION_CLASSES = new HashMap<String, ValidationPattern>();

    @Autowired
    @Qualifier("legacyDataAdapter")
    private LegacyDataAdapter legacyDataAdapter;

    private CustomAttributeService customAttributeService;
    
    static {
        final Map<String, ValidationPattern> tempPatterns = new HashMap<String, ValidationPattern>();
        
        final UTF8AnyCharacterValidationPattern anyCharVal = new UTF8AnyCharacterValidationPattern();
        anyCharVal.setAllowWhitespace(true);
        
        tempPatterns.put(STRING, anyCharVal);
        tempPatterns.put(NUMBER, new NumericValidationPattern());
        
        VALIDATION_CLASSES = Collections.unmodifiableMap(tempPatterns);
        
        dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        dateFormat.setLenient(false);
    }
    
    @Override
    public boolean processRules(SaveCustomDataEvent event) {
        boolean rulePassed = true;
        Map<String, CustomAttributeDocument> customAttributeDocuments = event.getCustomAttributeDocuments();
        for (Map.Entry<String, CustomAttributeDocument> customAttributeDocumentEntry : customAttributeDocuments.entrySet()) {

            CustomAttributeDocument customAttributeDocument = customAttributeDocumentEntry.getValue();

            CustomAttribute customAttribute = customAttributeDocument.getCustomAttribute();

            List<? extends DocumentCustomData> customDataList = event.getCustomDataList();

            if (isDocEnrouteAndDoesNotContainCustomAttribute(customAttribute.getId(), customDataList,
                    event.getDocument().getDocumentHeader().getWorkflowDocument())) {
                continue;
            }
            int index = 0;
            for (DocumentCustomData data : customDataList) {
                if (data.getCustomAttributeId() == customAttribute.getId().longValue()) {
                    customAttribute.setValue(data.getValue());
                    break;
                }
                index++;
            }
            String errorKey = event.getErrorPathPrefix() + CUSTOMATTRIBUTE_ERROR_START + index + CUSTOMATTRIBUTE_ERROR_END;
            if (StringUtils.isNotBlank(customAttribute.getValue())) {
                CustomAttributeDataType customAttributeDataType = customAttribute.getCustomAttributeDataType();
                if (customAttributeDataType == null && customAttribute.getDataTypeCode() != null) {
                    customAttributeDataType = getCustomAttributeService().getCustomAttributeDataType(customAttribute.getDataTypeCode());
                }

                if (customAttributeDataType != null) {
                    rulePassed &= validateAttributeFormat(customAttribute, errorKey, event);
                }
            }
            if (event.isValidateRequiredFields() && customAttributeDocument.isRequired() && StringUtils.isBlank(customAttribute.getValue())) {
                event.reportError(customAttribute, errorKey, RiceKeyConstants.ERROR_REQUIRED, 
                        customAttribute.getLabel(),customAttribute.getValue(), getValidFormat(customAttribute.getCustomAttributeDataType().getDescription()));
                 rulePassed = false;
            }
            
        }

        return rulePassed;
    }

    protected boolean isDocEnrouteAndDoesNotContainCustomAttribute(Long customAttributeId, List<? extends DocumentCustomData> customDataList, WorkflowDocument workflowDocument) {
        if (!workflowDocument.isSaved() && !workflowDocument.isInitiated()) {
            for (DocumentCustomData data : customDataList) {
                if (data.getCustomAttributeId().equals(customAttributeId)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    /**
     * 
     * This method is to validate the format/length of custom attribute
     * @param customAttribute
     * @param errorKey
     * @return
     */
    @SuppressWarnings("unchecked")
    protected boolean validateAttributeFormat(CustomAttribute customAttribute, String errorKey, SaveCustomDataEvent event) {

        boolean isValid = true;
        CustomAttributeDataType customAttributeDataType = customAttribute.getCustomAttributeDataType();
        if (customAttributeDataType == null && customAttribute.getDataTypeCode() != null) {
            customAttributeDataType = getCustomAttributeService().getCustomAttributeDataType(
                    customAttribute.getDataTypeCode());
        }
        if (customAttributeDataType != null) {
            Integer maxLength = customAttribute.getDataLength();
            if ((maxLength != null) && (maxLength.intValue() < customAttribute.getValue().length())) {
                event.reportError(customAttribute, errorKey, RiceKeyConstants.ERROR_MAX_LENGTH, customAttribute.getLabel(), maxLength.toString());
                isValid = false;
            }
                
            ValidationPattern validationPattern = VALIDATION_CLASSES.get(customAttributeDataType.getDescription());
            String validFormat = getValidFormat(customAttributeDataType.getDescription());
            if (validationPattern != null) {
                if (!validationPattern.matches(customAttribute.getValue())) {
                    event.reportError(customAttribute, errorKey, KeyConstants.ERROR_INVALID_FORMAT_WITH_FORMAT, customAttribute.getLabel(), customAttribute.getValue(), validFormat);
                    isValid = false;
                }
            } else if (DATE.equals(customAttributeDataType.getDescription())) {
                if (customAttribute.getValue() != null && !customAttribute.getValue().matches(DATE_REGEX)) {
                    event.reportError(customAttribute, errorKey, RiceKeyConstants.ERROR_INVALID_FORMAT, customAttribute.getLabel(), customAttribute.getValue(), validFormat);
                    isValid = false;
                } else {
                    try {
                        dateFormat.parse(customAttribute.getValue());
                    }
                    catch (ParseException e) {
                        event.reportError(customAttribute, errorKey, KeyConstants.ERROR_DATE, customAttribute.getValue(), customAttribute.getLabel());
                        isValid = false;
                    }
                }
            }

            if (customAttribute.getLookupClass() != null && customAttribute.getLookupClass().equals(KcPerson.class.getName())) {
                if (customAttribute.getDataTypeCode().equals("1") && customAttribute.getLookupReturn().equals(USER_NAME)) {
                    validFormat = getValidFormat(customAttributeDataType.getDescription());
                    KcPersonService kps = getKcPersonService();
                    KcPerson customPerson = kps.getKcPersonByUserName(customAttribute.getValue());
                    if (customPerson == null) {
                        addError(customAttribute, errorKey, event, customAttribute.getValue(), validFormat);
                        isValid = false;
                    }
                }
            } else if (customAttribute.getLookupClass() != null && customAttribute.getLookupClass().equals(ArgValueLookup.class.getName())) {
                isValid &= isValidArgValue(customAttribute, errorKey, event, customAttributeDataType);
            }
            else if (customAttribute.getLookupClass() != null) {
                Class boClass = null;
                try {
                    boClass = Class.forName(customAttribute.getLookupClass());
                } catch (ClassNotFoundException cnfE) {/* Do nothing, checked on input */ }

                if (isInvalid(boClass, keyValue(customAttribute.getLookupReturn(), customAttribute.getValue()))) {
                    validFormat = getValidFormat(customAttributeDataType.getDescription());
                    addError(customAttribute, errorKey, event, customAttribute.getValue(), validFormat);
                    return false;
                }
            }
        }
        return isValid;
    }

    public boolean isValidArgValue(CustomAttribute customAttribute, String errorKey, SaveCustomDataEvent event, CustomAttributeDataType customAttributeDataType) {
        Collection<ArgValueLookup> searchResults = getMatchingArgValues(customAttribute);
        String validFormat;
        Optional<ArgValueLookup> matchedArgValue = searchResults.stream()
                .filter(argValueLookup1 -> customAttribute.getValue().equals(argValueLookup1.getValue()))
                .findFirst();

        if (!matchedArgValue.isPresent()) {
            validFormat = getValidFormat(customAttributeDataType.getDescription());
            addError(customAttribute, errorKey, event, customAttribute.getValue(), validFormat);
            return false;
        }

        if (event instanceof AuditCustomDataEvent || event instanceof AuditProposalCustomDataEvent) {
            ArgValueLookup argValue = matchedArgValue.get();
            if (!argValue.isActive() && StringUtils.equals(customAttribute.getValue(), argValue.getValue())) {
                event.reportWarning(customAttribute, errorKey, KeyConstants.ERROR_INACTIVE_ARGUMENT_VALUE, customAttribute.getLabel());
                return false;
            }
        }

        return true;
    }

    public Collection<ArgValueLookup> getMatchingArgValues(CustomAttribute customAttribute) {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put(ARGUMENT_NAME, customAttribute.getLookupReturn());
        return getLegacyDataAdapter().findMatching(ArgValueLookup.class, criteria);
    }

    public void addError(CustomAttribute customAttribute, String errorKey, SaveCustomDataEvent event, String attributeValue, String validFormat) {
        event.reportError(customAttribute, errorKey, RiceKeyConstants.ERROR_EXISTENCE,
                customAttribute.getLabel(), attributeValue, validFormat);
    }

    private String getValidFormat(String dataType) {
        String validFormat = Constants.DATA_TYPE_STRING;
        if (dataType.equalsIgnoreCase(NUMBER)) {
            validFormat = Constants.DATA_TYPE_NUMBER;
        } else if (dataType.equalsIgnoreCase(DATE)) {
            validFormat = Constants.DATA_TYPE_DATE;
        }
        return validFormat;
    }

    /**
     * Gets the Custom Attribute Service.
     * @return the Custom Attribute Service
     */
    public CustomAttributeService getCustomAttributeService() {
        if (customAttributeService == null) {
            customAttributeService = KcServiceLocator.getService(CustomAttributeService.class);
        }
        return customAttributeService;
    }
    
    /**
     * Sets the Custom Attribute Service.
     * @param customAttributeService the Custom Attribute Service
     */
    public void setCustomAttributeService(CustomAttributeService customAttributeService) {
        this.customAttributeService = customAttributeService;
    }

    /**
     * 
     * This method is a helper method to retrieve KcPersonService.
     * @return
     */
    protected KcPersonService getKcPersonService() {
        return KcServiceLocator.getService(KcPersonService.class);
    }

    public LegacyDataAdapter getLegacyDataAdapter() {
        if (legacyDataAdapter == null) {
            legacyDataAdapter = KcServiceLocator.getService(LegacyDataAdapter.class);
        }
        return legacyDataAdapter;
    }

    public void setLegacyDataAdapter(LegacyDataAdapter legacyDataAdapter) {
        this.legacyDataAdapter = legacyDataAdapter;
    }
}
