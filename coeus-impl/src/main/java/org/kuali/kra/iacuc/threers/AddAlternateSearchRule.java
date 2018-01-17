/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.iacuc.threers;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.rule.KcBusinessRule;
import org.kuali.coeus.sys.framework.rule.KcTransactionalDocumentRuleBase;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.Date;
import java.util.List;

public class AddAlternateSearchRule extends KcTransactionalDocumentRuleBase implements KcBusinessRule<AddAlternateSearchEvent> {

    private static final String ERROR_PATH_PREFIX = "iacucAlternateSearchHelper.newAlternateSearch.";
    private static final String DD_ENTRY_NAME = "IacucAlternateSearch";
        
    @Override
    public boolean processRules(AddAlternateSearchEvent event) {
        return validateRequiredFields(event);
    }

    private boolean validateRequiredFields(AddAlternateSearchEvent event) {
        boolean valid = true;
        
        valid &= validateSearchDate(event);
        valid &= validateDatabases(event);
        valid &= validateYears(event);
        valid &= validateKeywords(event);
        
        return valid;
    }
    
    private boolean validateSearchDate(AddAlternateSearchEvent event) {
        Date searchDate = event.getAlternateSearch().getSearchDate();
        
        if (searchDate != null) {
            DictionaryValidationResult result = getDictionaryValidationService().validate(searchDate, DD_ENTRY_NAME, "searchDate", false); 
            if (result.getNumberOfErrors() == 0){
                Date currentDate = new Date();
                if (!searchDate.after(currentDate)) {
                    return true;
                } else {
                    GlobalVariables.getMessageMap().putError(ERROR_PATH_PREFIX + "searchDate", 
                            KeyConstants.ERROR_IACUC_VALIDATION_SEARCHDATE_AFTER_CURRENTDATE, new String[] { "Search Date" });                
                    return false;                    
                }
            } else {
                GlobalVariables.getMessageMap().putError(ERROR_PATH_PREFIX + "searchDate", 
                        KeyConstants.ERROR_IACUC_VALIDATION_ALTERNATE_SEARCH, new String[] { "Search Date" });                
                return false;
            }            
        } else {
            GlobalVariables.getMessageMap().putError(ERROR_PATH_PREFIX + "searchDate", 
                    KeyConstants.ERROR_IACUC_VALIDATION_ALTERNATE_SEARCH, new String[] { "Search Date" });                
            return false;
        }
    }
    
    private boolean validateDatabases(AddAlternateSearchEvent event) {
        List<String> newDatabases = event.getSelectedDatabases();
        
        if (CollectionUtils.isNotEmpty(newDatabases)) {
            return true;
        } else {
            GlobalVariables.getMessageMap().putError(ERROR_PATH_PREFIX + "databases", 
                    KeyConstants.ERROR_REQUIRED, new String[] { "Databases" }); 
            return false;
        }
    }
    
    private boolean validateYears(AddAlternateSearchEvent event) {
        String years = event.getAlternateSearch().getYearsSearched();
        
        if (StringUtils.isNotBlank(years)) {
            DictionaryValidationResult result = getDictionaryValidationService().validate((Object)years, DD_ENTRY_NAME, "yearsSearched", false); 
            if (result.getNumberOfErrors() == 0){
                return true;
            } else {
                GlobalVariables.getMessageMap().putError(ERROR_PATH_PREFIX + "yearsSearched", 
                        KeyConstants.ERROR_IACUC_VALIDATION_ALTERNATE_SEARCH, new String[] { "Years" });                
                return false;
            }
        } else {
            GlobalVariables.getMessageMap().putError(ERROR_PATH_PREFIX, 
                    KeyConstants.ERROR_REQUIRED, new String[] { "Years" }); 
            return false;            
        }
    }

    private boolean validateKeywords(AddAlternateSearchEvent event) {
        String keywords = event.getAlternateSearch().getKeywords();
        
        if (StringUtils.isNotBlank(keywords)) {
            DictionaryValidationResult result = getDictionaryValidationService().validate((Object)keywords, DD_ENTRY_NAME, "keywords", false); 
            if (result.getNumberOfErrors() == 0){
                return true;
            } else {
                GlobalVariables.getMessageMap().putError(ERROR_PATH_PREFIX + "keywords", 
                        KeyConstants.ERROR_IACUC_VALIDATION_ALTERNATE_SEARCH, new String[] { "Keywords" });                
                return false;
            }
        } else {
            GlobalVariables.getMessageMap().putError(ERROR_PATH_PREFIX, 
                    KeyConstants.ERROR_REQUIRED, new String[] { "Keywords" }); 
            return false;            
        }
    }    

}
