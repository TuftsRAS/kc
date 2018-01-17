/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.editable;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.person.attr.PersonEditableField;
import org.kuali.coeus.sys.framework.rule.KcMaintenanceDocumentRuleBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.Collection;

import static org.kuali.coeus.sys.framework.service.KcServiceLocator.getService;
import static org.kuali.kra.infrastructure.Constants.PERSON_EDITABLE_FIELD_NAME_PROPERTY_KEY;
import static org.kuali.kra.infrastructure.KeyConstants.ERROR_PERSON_EDITABLE_FIELD_EXISTS;

/**
 * Business rules class for the <code>{@link PersonEditableFieldMaintenanceDocument}</code>. When a <code>{@link PersonEditableField}</code> BO is created,
 * we need to check to see if it already exists or not. If it already exists, don't create a new one. Only change an existing one.
 * 
 */
public class PersonEditableFieldRule extends KcMaintenanceDocumentRuleBase {
    
        /**
         * Constructs a PersonEditableFieldRule.java.
         */
        public PersonEditableFieldRule() {
            super();
        }
        
        /**
         * Looks at existing persisted <code>{@link PersonEditableField}</code> BO's to see if this one already exists.
         * 
         * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
         */ 
        @Override
        public boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
            boolean retval = true;
            if (document.isNew()) {
                PersonEditableField newField = (PersonEditableField) getNewBo();
                
                for (PersonEditableField existingField : (Collection<PersonEditableField>) getBusinessObjectService().findAll(PersonEditableField.class)) {
                    if(StringUtils.equals(existingField.getFieldName(), newField.getFieldName()) && StringUtils.equals(existingField.getModuleCode(), newField.getModuleCode())) {
                        GlobalVariables.getMessageMap().putError(PERSON_EDITABLE_FIELD_NAME_PROPERTY_KEY, ERROR_PERSON_EDITABLE_FIELD_EXISTS, existingField.getFieldName(), existingField.getCoeusModule().getDescription());
                        retval = false;
                        break;
                    }
                }
            } else if (document.isEdit()) {
                PersonEditableField newField = (PersonEditableField) getNewBo();
                
                for (PersonEditableField existingField : (Collection<PersonEditableField>) getBusinessObjectService().findAll(PersonEditableField.class)) {
                    if(!ObjectUtils.equalByKeys(newField, existingField) && StringUtils.equals(existingField.getFieldName(), newField.getFieldName()) && StringUtils.equals(existingField.getModuleCode(), newField.getModuleCode())) {
                        GlobalVariables.getMessageMap().putError(PERSON_EDITABLE_FIELD_NAME_PROPERTY_KEY, ERROR_PERSON_EDITABLE_FIELD_EXISTS, existingField.getFieldName(), existingField.getCoeusModule().getDescription());
                        retval = false;
                        break;
                    }
                }
            } 
            
            return retval;
        }
        
        /**
         * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
         */
        @Override
        protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
            return true;
        }
        
        /**
         * Read Only Access to <code>{@link BusinessObjectService}</code>
         * 
         * @return BusinessObjectService instance
         */
        public BusinessObjectService getBusinessObjectService() {return getService(BusinessObjectService.class);}
}
