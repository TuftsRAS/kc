/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.framework.custom;

import org.kuali.coeus.common.framework.custom.attr.CustomAttribute;
import org.kuali.coeus.common.framework.custom.attr.CustomAttributeDocument;
import org.kuali.coeus.common.impl.custom.CustomDataRule;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.model.KcTransactionalDocumentBase;
import org.kuali.coeus.sys.framework.rule.KcBusinessRule;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBaseExtension;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.coeus.sys.framework.validation.ErrorReporter;
import org.kuali.rice.krad.document.Document;

import java.util.List;
import java.util.Map;

/**
 * Encapsulates a validation event for a Custom Attribute add action.
 */
public class SaveCustomDataEvent extends KcDocumentEventBaseExtension {

    private boolean validateRequiredFields = false;
    private List<? extends DocumentCustomData> customDataList;
    private Map<String, CustomAttributeDocument> customAttributeDocuments;
    protected ErrorReporter errorReporter = KcServiceLocator.getService(ErrorReporter.class);
    protected GlobalVariableService globalVariableService;
    
    /**
     * Constructs a SaveCustomAttributeEvent.
     * @param errorPathPrefix The error path prefix
     * @param document The document to validate
     */
    public SaveCustomDataEvent(String errorPrefix, Document document, List<? extends DocumentCustomData> customDataList, Map<String, CustomAttributeDocument> customAttributeDocuments) {
        super("Saving custom attribute to document " + getDocumentId(document), errorPrefix, document);
        this.customDataList = customDataList;
        this.customAttributeDocuments = customAttributeDocuments;
    }
    
    public SaveCustomDataEvent(String errorPrefix, Document document, List<? extends DocumentCustomData> customDataList, Map<String, CustomAttributeDocument> customAttributeDocuments, boolean validateRequiredFields) {
        this(errorPrefix, document, customDataList, customAttributeDocuments);
        this.validateRequiredFields = validateRequiredFields;
    }
    public SaveCustomDataEvent(KcTransactionalDocumentBase document) {
        this("customDataHelper.customDataList", document, document.getDocumentCustomData(), document.getCustomAttributeDocuments());
    }
    public SaveCustomDataEvent(KcTransactionalDocumentBase document, boolean validateRequiredFields) {
        this("customDataHelper.customDataList", document, document.getDocumentCustomData(), document.getCustomAttributeDocuments(), validateRequiredFields);
    }
    
    

    @Override
    @SuppressWarnings("unchecked")
    public KcBusinessRule getRule() {
        return new CustomDataRule();
    }

    public boolean isValidateRequiredFields() {
        return validateRequiredFields;
    }

    public void setValidateRequiredFields(boolean validateRequiredFields) {
        this.validateRequiredFields = validateRequiredFields;
    }

    public List<? extends DocumentCustomData> getCustomDataList() {
        return customDataList;
    }

    public void setCustomDataList(List<? extends DocumentCustomData> customDataList) {
        this.customDataList = customDataList;
    }

    public Map<String, CustomAttributeDocument> getCustomAttributeDocuments() {
        return customAttributeDocuments;
    }

    public void setCustomAttributeDocuments(Map<String, CustomAttributeDocument> customAttributeDocuments) {
        this.customAttributeDocuments = customAttributeDocuments;
    }
    
    public void reportError(CustomAttribute customAttribute, String propertyName, String errorKey, String... errorParams) {
        errorReporter.reportError(propertyName, errorKey, errorParams);
    }

    public void reportWarning(CustomAttribute customAttribute, String propertyName, String warningKey, String... warningParams) {
        errorReporter.reportWarning(propertyName, warningKey, warningParams);
    }

    protected GlobalVariableService getGlobalVariableService() {
        if (this.globalVariableService == null) {
            this.globalVariableService = KcServiceLocator.getService(GlobalVariableService.class);
        }
        return this.globalVariableService;
    }

}
