/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.paymentreports.awardreports.reporting.service;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.ojb.broker.query.*;
import org.kuali.coeus.common.framework.version.VersionStatus;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.AwardConstants;
import org.kuali.kra.award.paymentreports.awardreports.reporting.ReportTracking;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

/**
 * Report Tracking Dao for OJB.
 */
public class ReportTrackingDaoOjb extends LookupDaoOjb implements ReportTrackingDao {

    private static final Logger LOG = LogManager.getLogger(ReportTrackingDaoOjb.class);

    private PersistenceStructureService persistenceStructureServiceLocal;

    @SuppressWarnings("unchecked")
    @Override
    public List<ReportTracking> getResultsGroupedBy(Map<String, String> searchValues, List<String> groupedByAttrs, List<String> displayByAttrs) throws IllegalAccessException, InvocationTargetException {
        Criteria criteria = getCollectionCriteriaFromMap(new ReportTracking(), searchValues);
        criteria.addIn(AwardConstants.AWARD_ID, getActiveAwardsSubQuery());
        List<String> columns = new ArrayList<String>(groupedByAttrs);
        columns.add("count(*)");
        ReportQueryByCriteria query = QueryFactory.newReportQuery(ReportTracking.class, columns.toArray(new String[1]), criteria, true);
        query.addGroupBy(groupedByAttrs.toArray(new String[1]));
        for (String attr : groupedByAttrs) {
            query.addOrderByAscending(attr);
        }
        Iterator iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        List<ReportTracking> searchResults = new ArrayList<ReportTracking>();
        while (iter.hasNext()) {
            Object[] curLine = (Object[]) iter.next();
            ReportTracking curItem = new ReportTracking();
            int i = 0;
            for (; i < groupedByAttrs.size(); i++) {
                String column = groupedByAttrs.get(i);
                if (curLine[i] != null) {
                    if (ObjectUtils.getPropertyType(curItem, column, persistenceStructureServiceLocal).isAssignableFrom(Timestamp.class)
                            && curLine[i] instanceof Date) {
                        BeanUtils.setProperty(curItem, column, new Timestamp(((Date) curLine[i]).getTime()));
                    } else {
                        BeanUtils.setProperty(curItem, column, curLine[i]);
                    }
                }
            }
            //when on mysql count(*) column is returned as long, but on oracle BigDecimal.
            //handle here instead of looking at OJB fix.
            if (curLine[i] instanceof Long) {
                curItem.setItemCount(((Long) curLine[i]).intValue());
            } else {
                curItem.setItemCount(((BigDecimal) curLine[i]).intValue());
            }
            curItem.refreshNonUpdateableReferences();
            searchResults.add(curItem);
        }
        Collections.sort(searchResults, new MultiColumnComparator(displayByAttrs));
        return searchResults;
    }
    
    @Override
    public List<ReportTracking> getDetailResults(Map<String, String> searchValues, List<String> detailAttrs) throws IllegalAccessException, InvocationTargetException {
        Criteria criteria = getCollectionCriteriaFromMap(new ReportTracking(), searchValues);
        criteria.addIn(AwardConstants.AWARD_ID, getActiveAwardsSubQuery());
        QueryByCriteria query = QueryFactory.newQuery(ReportTracking.class, criteria, false);
        List<ReportTracking> result = new ArrayList<ReportTracking>(getPersistenceBrokerTemplate().getCollectionByQuery(query));
        Collections.sort(result, new MultiColumnComparator(detailAttrs));
        return result;
    }

    private Query getActiveAwardsSubQuery() {
        Criteria activeAwardSubCriteria = new Criteria();
        activeAwardSubCriteria.addIn(AwardConstants.AWARD_SEQUENCE_STATUS,
                Arrays.asList(VersionStatus.ACTIVE.toString(), VersionStatus.PENDING.toString()));
        ReportQueryByCriteria activeAwardIdsQuery = QueryFactory.newReportQuery(Award.class, activeAwardSubCriteria);
        activeAwardIdsQuery.setAttributes(new String[]{ AwardConstants.MAX_AWARD_ID });
        activeAwardIdsQuery.addGroupBy(AwardConstants.AWARD_NUMBER);
        return activeAwardIdsQuery;
    }

    /**
     * Comparator that supports sorting a list of report tracking BOs by a list of
     * columns. It will first sort by the first column, if that column is the same
     * then by the second column, etc.
     */
    protected class MultiColumnComparator implements Comparator<ReportTracking> {

        private List<String> columnsToSortBy;
        public MultiColumnComparator(List<String> columnsToSortBy) {
            this.columnsToSortBy = columnsToSortBy;
        }
        @Override
        public int compare(ReportTracking o1, ReportTracking o2) {
            int result = 0;
            for (String column : columnsToSortBy) {
                String v1 = null;
                String v2 = null;
                try {
                    v1 = BeanUtils.getProperty(o1, column);
                    v2 = BeanUtils.getProperty(o2, column);
                } catch (Exception e) {
                    LOG.warn("Exception while trying to sort report tracking records.", e);
                }
                if (v1 == null && v2 == null) {
                    return 0;
                } else if (v1 == null && v2 != null) {
                    return -1;
                } else if (v2 == null && v1 != null) {
                    return 1;
                }
                result = v1.compareTo(v2);
                if (result != 0) { 
                    break;
                }
            }
            return result;
        }
        
    }

    protected PersistenceStructureService getPersistenceStructureServiceLocal() {
        return persistenceStructureServiceLocal;
    }

    public void setPersistenceStructureServiceLocal(PersistenceStructureService persistenceStructureServiceLocal) {
        this.persistenceStructureServiceLocal = persistenceStructureServiceLocal;
    }

}
