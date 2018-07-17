/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.subcontracting.reporting.dao;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kra.award.subcontracting.reporting.SubcontractingExpenditureCategoryDetails;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.service.util.OjbCollectionAware;

import java.sql.Date;
import java.util.List;

public class SubcontractingExpenditureCategoryDetailsDaoOjb extends PlatformAwareDaoBaseOjb implements OjbCollectionAware, SubcontractingExpenditureCategoryDetailsDao {

    private static final Object FISCAL_PERIOD = "fiscalPeriod";
    private static final Logger LOG = LogManager.getLogger(SubcontractingExpenditureCategoryDetailsDaoOjb.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<SubcontractingExpenditureCategoryDetails> findCategoryDetailsByFiscalPeriodRange(Date startDate, Date endDate) {
        List<SubcontractingExpenditureCategoryDetails> retVal = null;
        
        if((startDate != null ) && (endDate != null)) {
            //set the less-than and greater-than criteria and run the query to get the BOs
            Criteria criteria = new Criteria();
            criteria.addGreaterOrEqualThan(FISCAL_PERIOD, startDate);
            criteria.addLessOrEqualThan(FISCAL_PERIOD, endDate);
            Query q = QueryFactory.newQuery(SubcontractingExpenditureCategoryDetails.class, criteria, true);
            logQuery(q);
            retVal = (List<SubcontractingExpenditureCategoryDetails>) getPersistenceBrokerTemplate().getCollectionByQuery(q);
        }
        else {
            throw new IllegalArgumentException("Both start and end dates have to be non-null");
        }
        return retVal;
    }
    

    /**
     * Logs the Query
     * @param q the query
     */
    private static void logQuery(Query q) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(q.toString());
        }
    }

}
