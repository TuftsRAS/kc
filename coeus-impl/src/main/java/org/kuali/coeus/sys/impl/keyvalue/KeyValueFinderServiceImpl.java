/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.impl.keyvalue;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.keyvalue.KeyValueFinderService;
import org.kuali.coeus.sys.framework.util.ValuesFinderUtils;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Component("keyValueFinderService")
public class KeyValueFinderServiceImpl implements KeyValueFinderService {

    private static final Logger LOG = LogManager.getLogger(KeyValueFinderServiceImpl.class);

    @Autowired
    @Qualifier("businessObjectService")
    private BusinessObjectService businessObjectService;

    @Override
    public List<KeyValue> getKeyValues(Class<? extends BusinessObject> keyValClass,String codePropName,String valPropName) {
        Collection keyVals = businessObjectService.findAll(keyValClass);
        List<KeyValue> keyValueList = new ArrayList<KeyValue>(keyVals.size());
        keyValueList.add(ValuesFinderUtils.getSelectOption());
        for (Iterator iterator = keyVals.iterator(); iterator.hasNext();) {
            Object keyValObj = iterator.next();
            Method getCodeMeth;
            try {
                getCodeMeth = keyValObj.getClass().getMethod("get"+StringUtils.capitalize(codePropName), null);
                Method getValMeth = keyValObj.getClass().getMethod("get"+StringUtils.capitalize(valPropName), null);
                Object code = getCodeMeth.invoke(keyValObj, null);
                Object value = getValMeth.invoke(keyValObj, null);
                if(code!=null && value!=null){
                    keyValueList.add(new ConcreteKeyValue(code.toString(), value.toString()));
                }
            }
            catch (SecurityException|NoSuchMethodException|IllegalArgumentException|IllegalAccessException|InvocationTargetException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return keyValueList;
    }

    @Override
    public List<KeyValue> getKeyValues(Class<? extends BusinessObject> keyValClass, String codePropName, String valPropName, Map<String, ?> queryMap) {
        
        Collection keyVals = businessObjectService.findMatching(keyValClass,queryMap);
        List<KeyValue> keyValueList = new ArrayList<KeyValue>(keyVals.size());
        keyValueList.add(ValuesFinderUtils.getSelectOption());
        for (Iterator iterator = keyVals.iterator(); iterator.hasNext();) {
            Object keyValObj = iterator.next();
            Method getCodeMeth;
            try {
                getCodeMeth = keyValObj.getClass().getMethod("get"+StringUtils.capitalize(codePropName), null);
                Method getValMeth = keyValObj.getClass().getMethod("get"+StringUtils.capitalize(valPropName), null);
                String code = (String)getCodeMeth.invoke(keyValObj, null);
                String value = (String)getValMeth.invoke(keyValObj, null);
                keyValueList.add(new ConcreteKeyValue(code, value));
            } catch (SecurityException|NoSuchMethodException|IllegalArgumentException|IllegalAccessException|InvocationTargetException e) {
                    LOG.error(e.getMessage(), e);
            }
        }
        return keyValueList;
    }

    /**
     * Gets the businessObjectService attribute.
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
