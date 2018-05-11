/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.dataovveride.common;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.api.model.ScaleThreeDecimal;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;

@Component("commonDataOverrideService")
public class CommonDataOverrideServiceImpl implements CommonDataOverrideService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CommonDataOverrideServiceImpl.class);

    private static final String ON = "on";
    private static final String YES = "Yes";
    private static final String NO = "No";
    private static final String TRUE = "true";

    @Autowired
    @Qualifier("dataObjectService")
    private DataObjectService dataObjectService;

    @Autowired
    @Qualifier("dateTimeService")
    private DateTimeService dateTimeService;

    @Override
    public void setChangedValue(KcPersistableBusinessObjectBase bo, String propertyName, String propertyValue) {
        final Class<?> type = dataObjectService.wrap(bo).getPropertyType(propertyName);

        try {
            if (propertyValue == null) {
                PropertyUtils.setNestedProperty(bo, propertyName, null);
            } else if (java.sql.Date.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, getDateTimeService().convertToSqlDate(propertyValue));
            } else if (java.sql.Timestamp.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, getDateTimeService().convertToSqlTimestamp(propertyValue));
            } else if (java.sql.Time.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, getDateTimeService().convertToSqlTime(propertyValue));
            } else if (java.util.Date.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, getDateTimeService().convertToDateTime(propertyValue));
            } else if (int.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, Integer.valueOf(propertyValue).intValue());
            } else if (byte.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, Byte.valueOf(propertyValue).byteValue());
            } else if (short.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, Short.valueOf(propertyValue).shortValue());
            } else if (Integer.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, Integer.valueOf(propertyValue));
            } else if (Byte.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, Byte.valueOf(propertyValue));
            } else if (Short.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, Short.valueOf(propertyValue));
            } else if (long.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, Long.valueOf(propertyValue).longValue());
            } else if (Long.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, Long.valueOf(propertyValue));
            } else if (float.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, Float.valueOf(propertyValue).floatValue());
            } else if (Float.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, Float.valueOf(propertyValue));
            } else if (double.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, Double.valueOf(propertyValue).doubleValue());
            } else if (Double.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, Double.valueOf(propertyValue));
            } else if (BigInteger.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, new BigInteger(propertyValue));
            } else if (BigDecimal.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, new BigDecimal(propertyValue));
            } else if (String.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, propertyValue);
            } else if (boolean.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, ON.equals(propertyValue));
            } else if (Boolean.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, Boolean.valueOf(ON.equals(propertyValue)));
            } else if (ScaleTwoDecimal.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, new ScaleTwoDecimal(propertyValue));
            } else if (ScaleThreeDecimal.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, new ScaleThreeDecimal(propertyValue));
            } else if (KualiDecimal.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, new KualiDecimal(propertyValue));
            } else if (KualiInteger.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, new KualiInteger(propertyValue));
            } else if (KualiPercent.class.isAssignableFrom(type)) {
                PropertyUtils.setNestedProperty(bo, propertyName, new KualiPercent(propertyValue));
            } else {
                throw new RuntimeException("Data override does not work for this class");
            }
        } catch (ParseException|IllegalAccessException|InvocationTargetException|NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getChangedValue(KcPersistableBusinessObjectBase bo, String propertyName) {
        Object propertyObject = getPropertyValue(bo, propertyName);

        DataType dataType = DataType.getDataTypeFromClass(dataObjectService.wrap(bo).getPropertyType(propertyName));
        String propertyValue = null;
        if (propertyObject != null) {
            if (dataType == null) {
                propertyValue = propertyObject.toString();
            } else if (dataType.isTemporal()) {
                propertyValue = getDateTimeService().toDateString((Date)propertyObject);
            } else if (dataType.equals(DataType.STRING)){
                propertyValue = (String) propertyObject;
            } else if (dataType.equals(DataType.BOOLEAN)) {
                propertyValue = TRUE.equals(propertyObject.toString()) ? YES : NO;
            } else {
                propertyValue = propertyObject.toString();
            }
        }
        return propertyValue;
    }

    @Override
    public <T extends KcPersistableBusinessObjectBase> String getDisplayReferenceValue(T bo, String propertyName, Class<T> clazz) {
        String refName = "";

        DataObjectRelationship relationship = getDataObjectService().getMetadataRepository().getMetadata(clazz).getRelationshipByLastAttributeInRelationship(propertyName);
        if (relationship != null) {
            refName = relationship.getName();
        }

        if (StringUtils.isNotEmpty(refName)){
            getDataObjectService().wrap(bo).fetchRelationship(refName);
            try {
                Object refObject = PropertyUtils.getNestedProperty(bo,refName);
                return (String) PropertyUtils.getNestedProperty(refObject,"description");
            } catch (Exception e) {
                LOG.warn("no description field found on ref object",e);
            }
        }
        return null;
    }

    protected Object getPropertyValue(KcPersistableBusinessObjectBase bo, String propertyName) {
        try{
            return PropertyUtils.getNestedProperty(bo,propertyName);
        } catch (Exception e) {
            throw new RiceRuntimeException("propertyName [" + propertyName + "] can not be found on bo", e);
        }
    }

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
