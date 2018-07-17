/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.lookup.keyvalue;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.framework.custom.arg.ArgValueLookup;
import org.kuali.coeus.common.framework.custom.attr.CustomAttributeService;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.coeus.sys.framework.util.ValuesFinderUtils;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This class get a list of lookup fileds for the selected lookup class
 */
public class LookupReturnValuesFinder extends UifKeyValuesFinderBase {
    private static final Logger LOG = LogManager.getLogger(LookupReturnValuesFinder.class);
    private static final String ARGVALUELOOKUPE_CLASS = ArgValueLookup.class.getName();

    @Override
    public List<KeyValue> getKeyValues() {
        // this will be called twice for each maintenancedocument page load
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(ValuesFinderUtils.getSelectOption());
        String lookupClass = (String) GlobalVariables.getUserSession().retrieveObject(Constants.LOOKUP_CLASS_NAME);

        List lookupReturnFields = (List) GlobalVariables.getUserSession().retrieveObject(Constants.LOOKUP_RETURN_FIELDS);
        try {
            if (lookupReturnFields != null) {
                GlobalVariables.getUserSession().removeObject(Constants.LOOKUP_RETURN_FIELDS);
                GlobalVariables.getUserSession().removeObject(Constants.LOOKUP_CLASS_NAME);
            }
            else {
                if (lookupClass != null) {
                    lookupReturnFields = KcServiceLocator.getService(CustomAttributeService.class).getLookupReturns(lookupClass);
                    GlobalVariables.getUserSession().addObject(Constants.LOOKUP_RETURN_FIELDS, lookupReturnFields);
                }
            }
        }
        catch (Exception e) {
            LOG.info(e.getMessage(), e);
        }
        
        if (lookupReturnFields != null) {
            for (Object fieldName : lookupReturnFields) {
                keyValues.add(new ConcreteKeyValue(fieldName.toString(), (ARGVALUELOOKUPE_CLASS.equals(lookupClass) ? fieldName.toString() : KcServiceLocator.getService(DataDictionaryService.class).getAttributeLabel(lookupClass,fieldName.toString()))));
            }
        }

        return keyValues;
    }


}
