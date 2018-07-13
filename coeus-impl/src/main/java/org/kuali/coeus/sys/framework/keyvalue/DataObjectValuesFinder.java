/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.framework.keyvalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.rice.core.api.criteria.OrderByField;
import org.kuali.rice.core.api.criteria.OrderDirection;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
@Component("dataObjectValuesFinder")
public class DataObjectValuesFinder extends UifKeyValuesFinderBase {

    private static final Logger LOG = LogManager.getLogger(DataObjectValuesFinder.class);
    private static final long serialVersionUID = 1L;

    protected Class<?> dataObjectClass;
    protected String keyAttributeName;
    protected String labelAttributeName;
    protected boolean includeKeyInDescription = false;
    protected String blankRowValue = "select";
    protected Map<String,String> matchingCriteria = new HashMap<String,String>();
    protected String orderByField;
    protected boolean orderDescending;

    @Autowired
    @Qualifier("dataObjectService")
    private DataObjectService dataObjectService;
    
    /**
     * Build the list of KeyValues using the key (keyAttributeName) and
     * label (labelAttributeName) of the list of all data objects found
     * for the BO class specified.
     */
    @Override
	public List<KeyValue> getKeyValues() {
        List<KeyValue> labels = new ArrayList<KeyValue>();
    	try {
           List<OrderByField> orderByFields = new ArrayList<>();
            if (StringUtils.isNotEmpty(orderByField)) {
               orderByFields.add(OrderByField.Builder.create(orderByField,orderDescending ? OrderDirection.DESCENDING : OrderDirection.ASCENDING).build());
            }
            Collection<?> objects;
            if(matchingCriteria == null || matchingCriteria.isEmpty()) {
            	objects = getDataObjectService().findMatching(dataObjectClass,QueryByCriteria.Builder.create().setOrderByFields(orderByFields).build()).getResults();
            }else {
            	objects = getDataObjectService().findMatching(dataObjectClass,QueryByCriteria.Builder.andAttributes(matchingCriteria).setOrderByFields(orderByFields).build()).getResults();
            }
            if(isAddBlankOption()) {
            	labels.add(new ConcreteKeyValue("", blankRowValue));
            }
            for (Object object : objects) {
            	Object key = PropertyUtils.getProperty(object, keyAttributeName);
            	String label = (String)PropertyUtils.getProperty(object, labelAttributeName);
            	if (includeKeyInDescription) {
            	    label = key + " - " + label;
            	}
            	labels.add(new ConcreteKeyValue(key.toString(), label));
    	    }
    	} catch (Exception e) {
            LOG.error("Exception occurred while trying to build keyValues List: " + this, e);
    	}
        return labels;
    }

    public void setDataObjectClass(Class<?> dataObjectClass) {
        this.dataObjectClass = dataObjectClass;
    }

    public void setIncludeKeyInDescription(boolean includeKeyInDescription) {
        this.includeKeyInDescription = includeKeyInDescription;
    }

    public void setKeyAttributeName(String keyAttributeName) {
        this.keyAttributeName = keyAttributeName;
    }

    public void setLabelAttributeName(String labelAttributeName) {
        this.labelAttributeName = labelAttributeName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PersistableDataObjectValuesFinder [dataObjectClass=").append(this.dataObjectClass)
                .append(", keyAttributeName=").append(this.keyAttributeName).append(", labelAttributeName=")
                .append(this.labelAttributeName).append(", includeKeyInDescription=")
                .append(this.includeKeyInDescription).append(", includeBlankRow=").append(isAddBlankOption())
                .append("]");
        return builder.toString();
    }

	public DataObjectService getDataObjectService() {
		return dataObjectService;
	}

	public void setBlankRowValue(String blankRowValue) {
		this.blankRowValue = blankRowValue;
	}

	public Map<String, String> getMatchingCriteria() {
		return matchingCriteria;
	}

	public void setMatchingCriteria(Map<String, String> matchingCriteria) {
		this.matchingCriteria = matchingCriteria;
	}

	public void setDataObjectService(DataObjectService dataObjectService) {
		this.dataObjectService = dataObjectService;
	}

    public String getOrderByField() {
        return orderByField;
    }

    public void setOrderByField(String orderByField) {
        this.orderByField = orderByField;
    }

    public boolean isOrderDescending() {
        return orderDescending;
    }

    public void setOrderDescending(boolean orderDescending) {
        this.orderDescending = orderDescending;
    }
}
