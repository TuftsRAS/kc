/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.rolodex;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.coeus.common.framework.rolodex.Rolodex;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.springframework.dao.DataIntegrityViolationException;
import org.springmodules.orm.ojb.OjbOperationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * OJB Implementation of <code>{@link RolodexDao}</code>
 * 
 */
public class RolodexDaoOjb extends LookupDaoOjb implements RolodexDao {
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(RolodexDaoOjb.class);
    
    @Override
    public List<? extends BusinessObject> getNonOrganizationalRolodexResults(Map fieldValues, boolean usePrimaryKeys) {
        Collection searchResults = new ArrayList();
        Long matchingResultsCount = null;
        Criteria criteria = getNonOrganizationalRolodexCriteria(Rolodex.class, fieldValues, usePrimaryKeys);
        
         try {
            Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(Rolodex.class);
            if (searchResultsLimit != null) {
                matchingResultsCount = new Long(getPersistenceBrokerTemplate().getCount(QueryFactory.newQuery(Rolodex.class, criteria)));
                LookupUtils.applySearchResultsLimit(Rolodex.class, criteria, getDbPlatform());
            }
            if ((matchingResultsCount == null) || (matchingResultsCount.intValue() <= searchResultsLimit.intValue())) {
                matchingResultsCount = new Long(0);
            }
            searchResults = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Rolodex.class, criteria));
            
            LOG.info("Got results " + searchResults);
        }
        catch (OjbOperationException e) {
            throw new RuntimeException("RolodexDaoOjb encountered exception during executeSearch", e);
        }
        catch (DataIntegrityViolationException e) {
            throw new RuntimeException("RolodexDaoOjb encountered exception during executeSearch", e);
        }
        return new CollectionIncomplete(searchResults, matchingResultsCount);    
    }

    protected Criteria getNonOrganizationalRolodexCriteria(Class businessObjectClass, Map fieldValues, boolean usePrimaryKeys) {
        Criteria retval;
        
        if (usePrimaryKeys) {
            retval = getCollectionCriteriaFromMapUsingPrimaryKeysOnly(businessObjectClass, fieldValues);
        }
        else {
            retval = getCollectionCriteriaFromMap(checkBusinessObjectClass(businessObjectClass), fieldValues);   
        }  
        
        retval.addNotNull("firstName");
        retval.addNotNull("lastName");
        //retval.addNotEqualToField("active", "No");\
        retval.addNotEqualTo("active", Boolean.FALSE);
        
        LOG.info("NonOrganizationalQuery" + retval);
        
        return retval;
    }

    
    private PersistableBusinessObject checkBusinessObjectClass(Class businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("BusinessObject class passed to RolodexDaoOjb findCollectionBySearchHelper... method was null");
        }
        PersistableBusinessObject businessObject = null;
        try {
            businessObject = (PersistableBusinessObject) businessObjectClass.newInstance();
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("RolodexDaoOjb could not get instance of " + businessObjectClass.getName(), e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException("RolodexDaoOjb could not get instance of " + businessObjectClass.getName(), e);
        }
        return businessObject;
    }
}
