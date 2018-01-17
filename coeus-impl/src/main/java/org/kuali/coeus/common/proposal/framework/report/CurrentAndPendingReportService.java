/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.proposal.framework.report;

import org.kuali.coeus.common.framework.print.PrintingException;
import org.kuali.coeus.common.framework.print.CurrentReportBean;
import org.kuali.coeus.common.framework.print.PendingReportBean;
import org.kuali.coeus.common.framework.print.AttachmentDataSource;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface CurrentAndPendingReportService {

    /**
     * Loads the Current Report data into a list of CurrentReportBeans
     *
     * @param personId - The person for whom current support obligations are being queried
     * @return The list of beans or an empty list if no data found.
     * @throws RuntimeException if an exception is thrown during the call
     */
    List<CurrentReportBean> loadCurrentReportData(String personId);

    /**
     * Loads the Pending Report data into a list of PendingReportBeans
     *
     * @param personId - The person for whom pending support obligations are being queried
     * @return The list of beans or an empty list if no data found.
     * @throws RuntimeException if an exception is thrown during the call
     */
    List<PendingReportBean> loadPendingReportData(String personId);

    /**
     * This method generates the current report and returns the PDF stream as
     * {@link AttachmentDataSource}
     *
     * @param reportParameters
     *            {@link Map} of parameters required for report generation
     * @return {@link AttachmentDataSource} which contains the byte array of the
     *         generated PDF
     * @throws PrintingException
     *             if any errors occur during report generation
     */
    public AttachmentDataSource printCurrentReport(Map<String, Object> reportParameters) throws PrintingException;

    /**
     * This method generates the pending report and returns the PDF stream as
     * {@link AttachmentDataSource}
     *
     * @param reportParameters
     *            {@link Map} of parameters required for report generation
     * @return {@link AttachmentDataSource} which contains the byte array of the
     *         generated PDF
     * @throws PrintingException
     *             if any errors occur during report generation
     */
    public AttachmentDataSource printPendingReport(Map<String, Object> reportParameters) throws PrintingException;
}
