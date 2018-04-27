/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.framework.controller.rest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kuali.coeus.sys.framework.controller.rest.editor.CustomSqlDateEditor;
import org.kuali.coeus.sys.framework.controller.rest.editor.CustomSqlTimestampEditor;
import org.kuali.coeus.sys.framework.rest.UnprocessableEntityException;
import org.kuali.coeus.sys.framework.util.CollectionUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.TypeMismatchException;

public class SimpleCrudMapBasedRestController<T> extends SimpleCrudRestControllerBase<T, Map<String, Object>> {

	private static final Collection<String> IGNORED_FIELDS = Stream.of("versionNumber", "objectId", "updateUser", "updateTimestamp").collect(Collectors.toList());

	private List<String> exposedProperties;

	@Override
	protected Object getPropertyValueFromDto(String propertyName, Map<String, Object> dataObject) {
		return dataObject.get(propertyName);
	}

	@Override
	protected Map<String, Object> convertDataObjectToDto(T dataObject) {
		BeanWrapper beanWrapper = getRestBeanWrapperFactory().newInstance(dataObject);
		return createMapFromPropsOnBean(beanWrapper);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T convertDtoToDataObject(Map<String, Object> input) {
		T newDataObject = this.getNewDataObject();
		BeanWrapper beanWrapper = getRestBeanWrapperFactory().newInstance(newDataObject);
		beanWrapper.setAutoGrowNestedPaths(true);
        beanWrapper.registerCustomEditor(java.sql.Timestamp.class, new CustomSqlTimestampEditor());
        beanWrapper.registerCustomEditor(java.sql.Date.class, new CustomSqlDateEditor());

		try {
			getExposedProperties().forEach(name -> {
                final Object val = input.get(name);
                if (val != null || !isPrimitive(name)) {
                    beanWrapper.setPropertyValue(name, translateValue(name, val != null ? val.toString() : null));
                }
            });
		} catch (IllegalArgumentException e) {
			getExposedProperties().forEach(name -> beanWrapper.setPropertyValue(name, input.get(name)));
		} catch (TypeMismatchException e) {
			throw new UnprocessableEntityException(e.getMessage(), e);
		}
		return (T) beanWrapper.getWrappedInstance();
	}

	@Override
	protected void updateDataObjectFromDto(T existingDataObject,
			Map<String, Object> input) {
		BeanWrapper beanWrapper = getRestBeanWrapperFactory().newInstance(existingDataObject);
		beanWrapper.setAutoGrowNestedPaths(true);
		try {
			getExposedProperties().forEach(name -> beanWrapper.setPropertyValue(name, input.get(name)));
		} catch (TypeMismatchException e) {
			throw new UnprocessableEntityException(e.getMessage(), e);
		}
	}

	@Override
	protected void mergeDataObjectFromDto(T existingDataObject, Map<String, Object> input) {
		BeanWrapper beanWrapper = getRestBeanWrapperFactory().newInstance(existingDataObject);
		beanWrapper.setAutoGrowNestedPaths(true);
		try {
			input.forEach(beanWrapper::setPropertyValue);
		} catch (TypeMismatchException e) {
			throw new UnprocessableEntityException(e.getMessage(), e);
		}
	}

	@Override
	protected List<String> getListOfTrackedProperties() {
		return getExposedProperties().stream().filter(p -> !SYNTHETIC_FIELD_PK.equals(p)).collect(Collectors.toList());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Map<String, Object> convertObjectToDto(Object o) {
		return (Map<String, Object>) o;
	}

	protected Map<String, Object> createMapFromPropsOnBean(BeanWrapper beanWrapper) {
		final Map<String, Object> map = getExposedProperties().stream()
				.map(name -> CollectionUtils.entry(name, beanWrapper.getPropertyValue(name)))
				.collect(CollectionUtils.nullSafeEntriesToMap());
		map.put(SYNTHETIC_FIELD_PK, primaryKeyToString(getPrimaryKeyIncomingObject(map)));
		return map;
	}

	@Override
	public List<String> getExposedProperties() {
		if (org.apache.commons.collections4.CollectionUtils.isEmpty(exposedProperties)) {
			exposedProperties = getDefaultProperties();
		}

		return exposedProperties;
	}

	public void setExposedProperties(List<String> exposedProperties) {
		this.exposedProperties = exposedProperties;
	}

	public List<String> getDefaultProperties() {
		final List<String> fields = getPersistenceVerificationService().persistableFields(getDataObjectClazz());

		return fields.stream().filter(field -> !IGNORED_FIELDS.contains(field)).collect(Collectors.toList());
	}
}
