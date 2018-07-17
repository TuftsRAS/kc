/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.kra.award.paymentreports.awardreports.reporting.service;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.rice.krad.UserSession;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

public class ReportTrackingDigestNotificationJobDetail extends ReportTrackingNotificationJobDetail {

    private static final Logger LOG = LogManager.getLogger(ReportTrackingNotificationJobDetail.class);

    private static final String ACTION_CODE_JOB_MAP_KEY = "actionCode";
    private static final String NOTIFICATION_NAME_JOB_MAP_KEY = "notificationName";
    private static final String INCLUDE_DUE_JOB_MAP_KEY = "includeDue";
    private static final String INCLUDE_OVERDUE_JOB_MAP_KEY = "includeOverdue";

    private GlobalVariableService globalVariableService;
    private String user;

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) {
        String actionCode = getActionCode(context);
        String notificationName = getNotificationName(context);
        boolean includeDue = isIncludeDue(context);
        boolean includeOverdue = isIncludeOverdue(context);
        globalVariableService.doInNewGlobalVariables(new UserSession(user), () -> {
            StringBuilder builder = new StringBuilder();
            try {
                ReportTrackingNotificationDetails results = getReportTrackingNotificationService().runReportDigestNotification(actionCode, notificationName, includeDue, includeOverdue);
                buildMessage(builder, Collections.singletonList(results));
            } catch (Exception e) {
                LOG.error("Error running report tracking digest notifications", e);
                builder.append("Message: Error running report tracking digest notifications. See log for more details. " + e.getMessage());
            }

            logAndSendResults(builder.toString());
            return null;
        });
    }

    protected GlobalVariableService getGlobalVariableService() {
        return globalVariableService;
    }

    public void setGlobalVariableService(GlobalVariableService globalVariableService) {
        this.globalVariableService = globalVariableService;
    }

    public void setUser(String user) {
        this.user = user;
    }

    protected String getActionCode(JobExecutionContext context) {
        return (String) context.getJobDetail().getJobDataMap().get(ACTION_CODE_JOB_MAP_KEY);
    }

    protected String getNotificationName(JobExecutionContext context) {
        return (String) context.getJobDetail().getJobDataMap().get(NOTIFICATION_NAME_JOB_MAP_KEY);
    }

    protected boolean isIncludeDue(JobExecutionContext context) {
        String includeDue = (String) context.getJobDetail().getJobDataMap().get(INCLUDE_DUE_JOB_MAP_KEY);
        return includeDue == null || Boolean.valueOf(includeDue);
    }

    protected boolean isIncludeOverdue(JobExecutionContext context) {
        String includeOverdue = (String) context.getJobDetail().getJobDataMap().get(INCLUDE_OVERDUE_JOB_MAP_KEY);
        return includeOverdue == null || Boolean.valueOf(includeOverdue);
    }

}
