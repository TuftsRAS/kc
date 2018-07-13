/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.paymentreports.awardreports.reporting.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.notification.impl.service.KcNotificationService;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * This job is triggered by the quartz scheduler to kick off the CFDA table update.
 * Extends QuartzJobBean for Persistence.
 */
public class ReportTrackingNotificationJobDetail extends QuartzJobBean {

    private static final String BREAK = "<BR>";
    private static final String CONTEXT_NAME = "Report Tracking Notification Job";
    private static final String SUBJECT = "Report Tracking Notification batch job result";
    
    private static final Logger LOG = LogManager.getLogger(ReportTrackingNotificationJobDetail.class);
    
    private String user;

    private ReportTrackingNotificationService reportTrackingNotificationService;
    private ParameterService parameterService;
    private KcNotificationService kcNotificationService;

    /*
     * This is the method that is called by the Quartz job scheduler.
     */
    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) {
        StringBuilder builder = new StringBuilder();
        // The cron job runs as this user
        UserSession userSession = new UserSession(user);
        GlobalVariables.setUserSession(userSession);
        try {
            List<ReportTrackingNotificationDetails> results = reportTrackingNotificationService.runReportTrackingNotifications();
            buildMessage(builder, results);
        } catch (Exception e) {
            LOG.error("Error running report tracking notification service.", e);
            builder.append("Message: Error running report tracking notification service. See log for more details. " + e.getMessage());
        }

        logAndSendResults(builder.toString());
    }
    
    protected void buildMessage(StringBuilder builder, List<ReportTrackingNotificationDetails> details) {
        builder.append("Report Tracking Notifications : " + details.size() + BREAK);
        int i = 1;
        for (ReportTrackingNotificationDetails detail : details) {
            builder.append("Report Tracking Notification " + i++ + BREAK);
            builder.append("Notification Name : " + detail.getNotificationName() + BREAK);
            builder.append("Action Code : " + detail.getActionCode() + BREAK);
            if (StringUtils.isNotBlank(detail.getErrorMessage())) {
                builder.append("Error Occurred : " + detail.getErrorMessage());
            }
            if (!detail.isNotificationActive()) {
                builder.append("Was not found, was inactive or had no recipients defined." + BREAK);
            } else {
                builder.append("Notification Type Recipients : " + detail.getNotificationRecipients() + BREAK);
                builder.append("Report Tracking Records found using task parameters : " + detail.getTrackingRecordsFound() + BREAK);
                builder.append("Report Tracking Records whose due date matched : " + detail.getTrackingRecordsMatched() + BREAK);
                builder.append("Report Tracking Notifications sent : " + detail.getNotificationsSent() + BREAK);
            }
            builder.append(BREAK);
        }
    }

    protected void logAndSendResults(String message) {
        LOG.info(message);

        String recipient = getRecipient();
        // Send notification only if recipient has been set in the param
        if (StringUtils.isNotEmpty(recipient)) {
            kcNotificationService.sendNotification(CONTEXT_NAME, SUBJECT, message, Collections.singletonList(recipient));
        }
    }
    
    /**
     * This method gets the recipient specified in the parameter.
     * 
     * @return
     */
    protected String getRecipient() {
        return parameterService.getParameterValueAsString(AwardDocument.class,
                Constants.REPORT_TRACKING_NOTIFICATIONS_BATCH_RECIPIENT);  
    }
    
    /**
     * Injected by spring.
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }
    
    protected String getUser() {
        return user;
    }
    
    /**
     * This is injected into the scheduler context by spring.
     * @param kcNotificationService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    /**
     * This is injected into the scheduler context by spring.
     * @param kcNotificationService
     */
    public void setKcNotificationService(KcNotificationService kcNotificationService) {
        this.kcNotificationService = kcNotificationService;
    }

    protected ReportTrackingNotificationService getReportTrackingNotificationService() {
        return reportTrackingNotificationService;
    }

    public void setReportTrackingNotificationService(ReportTrackingNotificationService reportTrackingNotificationService) {
        this.reportTrackingNotificationService = reportTrackingNotificationService;
    }

    protected ParameterService getParameterService() {
        return parameterService;
    }

    protected KcNotificationService getKcNotificationService() {
        return kcNotificationService;
    }

}
