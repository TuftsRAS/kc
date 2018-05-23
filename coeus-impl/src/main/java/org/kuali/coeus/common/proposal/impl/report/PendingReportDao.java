/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.proposal.impl.report;

import org.kuali.coeus.common.framework.print.PendingReportBean;
import org.kuali.rice.kew.api.exception.WorkflowException;

import java.util.Collection;
import java.util.List;

/**
 * Contract for pending support DAO
 */
public interface PendingReportDao {
    /**
     * Loads the Pending Report data into a list of PendingReportBeans
     * @param personId - The person for whom pending support obligations are being queried
     * @return
     */
    List<PendingReportBean> queryForPendingSupport(String personId, Collection<String> excludedProposalTypes) throws WorkflowException;
}
