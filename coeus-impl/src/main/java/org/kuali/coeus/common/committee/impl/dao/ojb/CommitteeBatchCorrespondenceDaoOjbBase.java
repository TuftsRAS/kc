/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.committee.impl.dao.ojb;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.coeus.common.committee.impl.bo.CommitteeBatchCorrespondenceBase;
import org.kuali.coeus.common.committee.impl.dao.CommitteeBatchCorrespondenceDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.service.util.OjbCollectionAware;

import java.sql.Date;
import java.util.List;

/**
 * 
 * This class is the OJB implementation of CommitteeBatchCorrespondenceDao 
 * which provides enhanced database access functionality.
 */
public abstract class CommitteeBatchCorrespondenceDaoOjbBase<CBC extends CommitteeBatchCorrespondenceBase> extends PlatformAwareDaoBaseOjb implements OjbCollectionAware, CommitteeBatchCorrespondenceDao<CBC> {

    private static final Logger LOG = LogManager.getLogger(CommitteeBatchCorrespondenceDaoOjbBase.class);

    private static final String BATCH_CORRESPONDENCE_TYPE_CODE = "batchCorrespondenceTypeCode";
    private static final String BATCH_RUN_DATE = "batchRunDate";

    @Override
    @SuppressWarnings("unchecked")
    public List<CBC> getCommitteeBatchCorrespondence(String batchCorrespondenceTypeCode, Date startDate, Date endDate) {
        Criteria crit = new Criteria();
        crit.addEqualTo(BATCH_CORRESPONDENCE_TYPE_CODE, batchCorrespondenceTypeCode);
        if (startDate != null) {
            crit.addGreaterOrEqualThan(BATCH_RUN_DATE, startDate);
        }
        if (endDate != null) {
            crit.addLessOrEqualThan(BATCH_RUN_DATE, endDate);
        }

        Query q = QueryFactory.newQuery(getCommitteeBatchCorrespondenceBOClassHook(), crit, true);
        logQuery(q);
        return (List<CBC>) getPersistenceBrokerTemplate().getCollectionByQuery(q);
    }

    protected abstract Class<CBC> getCommitteeBatchCorrespondenceBOClassHook();
    
    
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
