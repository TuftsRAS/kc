/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.framework.custom.attr;

import org.apache.commons.lang3.StringUtils;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.lookup.keyvalue.LookupReturnValuesFinder;
import org.kuali.rice.kns.lookup.KualiLookupableImpl;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.lookup.LookupUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

@Component("customLookupable")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CustomLookupableImpl extends KualiLookupableImpl {

    @Autowired
    @Qualifier("lookupableHelperService")
    @Override
    public void setLookupableHelperService(LookupableHelperService lookupableHelperService) {
        super.setLookupableHelperService(lookupableHelperService);
    }

    /**
     * This is to force to reload the lookupreturn dropdown list for the lookupform. It's not pretty. The
     * GlovalVaribles.getKualiForm() is not helping because the 'fields' on lookupForm is not set until all fields are set. So, the
     * lookupreturnvaluesfinder can't take advantage of that.
     * 
     */
    @Override
    public boolean checkForAdditionalFields(Map fieldValues) {
        String lookupReturnFieldName = (String) fieldValues.get("lookupReturn");
        String lookupClassName = (String) fieldValues.get("lookupClass");
        if (StringUtils.isNotBlank(lookupClassName)) {
            for (Iterator iter = getRows().iterator(); iter.hasNext();) {
                Row row = (Row) iter.next();
                for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                    Field field = (Field) iterator.next();
                    if (field.getPropertyName().equals("lookupReturn")) {
                        GlobalVariables.getUserSession().addObject(Constants.LOOKUP_CLASS_NAME, (Object) lookupClassName);
                        LookupReturnValuesFinder finder = new LookupReturnValuesFinder();
                        field.setFieldValidValues(finder.getKeyValues());
                        GlobalVariables.getUserSession().removeObject(Constants.LOOKUP_RETURN_FIELDS);
                        GlobalVariables.getUserSession().removeObject(Constants.LOOKUP_CLASS_NAME);
                        if (StringUtils.isNotBlank(lookupReturnFieldName)) {
                            field.setPropertyValue(lookupReturnFieldName);
                            field.setPropertyValue(LookupUtils.forceUppercase(this.getBusinessObjectClass(), lookupReturnFieldName, field.getPropertyValue()));
                            fieldValues.put(lookupReturnFieldName, field.getPropertyValue());
                        }
                    }
                }
            }

        }
        return super.checkForAdditionalFields(fieldValues);
    }
}
