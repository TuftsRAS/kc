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
import org.kuali.coeus.common.framework.module.CoeusModule;
import org.kuali.coeus.common.framework.version.VersionStatus;
import org.kuali.coeus.common.notification.impl.bo.NotificationType;
import org.kuali.coeus.common.notification.impl.service.KcNotificationService;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.AwardConstants;
import org.kuali.kra.award.home.AwardService;
import org.kuali.kra.award.notification.AwardNotificationContext;
import org.kuali.kra.award.notification.AwardReportTrackingDigestNotificationRenderer;
import org.kuali.kra.award.notification.AwardReportTrackingNotificationRenderer;
import org.kuali.kra.award.notification.AwardUnitHierarchyDescendingNotificationContext;
import org.kuali.kra.award.paymentreports.ReportStatus;
import org.kuali.kra.award.paymentreports.awardreports.reporting.ReportTracking;
import org.kuali.kra.award.paymentreports.awardreports.reporting.ReportTrackingConstants;
import org.kuali.kra.award.version.service.AwardVersionService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.ken.api.notification.NotificationRecipient;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.*;

/**
 * Service to handle sending report tracking notifications.
 */
public class ReportTrackingNotificationServiceImpl implements ReportTrackingNotificationService {

    private static final Logger LOG = LogManager.getLogger(ReportTrackingNotificationServiceImpl.class);
    private static final String ASCEND_UNIT_HIERARCHY_FOR_DIGEST_NOTIFICATIONS_PARAM = "Ascend_Unit_Hierarchy_For_Report_Tracking_Digest_Unit_Admins";

    private BusinessObjectService businessObjectService;
    private AwardService awardService;
    private AwardVersionService awardVersionService;
    private KcNotificationService notificationService;
    private ParameterService parameterService;
    private ReportTrackingDao reportTrackingDao;
    private ReportTrackingService reportTrackingService;
    
    private List<ReportTrackingNotification> notifications;

    public ReportTrackingNotificationServiceImpl() {
        notifications = new ArrayList<>();
    }
    
    @Override
    public List<ReportTrackingNotificationDetails> runReportTrackingNotifications() {
        List<ReportTrackingNotificationDetails> resultDetails = new ArrayList<>();
        AwardReportTrackingNotificationRenderer renderer = new AwardReportTrackingNotificationRenderer();
        ReportStatus pendingStatus = reportTrackingService.getPendingReportStatus();

        for (ReportTrackingNotification notification : notifications) {
            ReportTrackingNotificationDetails details = new ReportTrackingNotificationDetails();
            details.setActionCode(notification.getActionCode());
            details.setNotificationName(notification.getName());
            resultDetails.add(details);
            try {
                NotificationType notificationType = notificationService.getNotificationType("1", notification.getActionCode());
                if (canSendNotifications(notificationType)) {
                    details.setNotificationActive(true);
                    details.setNotificationRecipients(notificationType.getNotificationTypeRecipients().size());
                    int recordsFound = 0;
                    int recordsMatched = 0;
                    int notificationsSent = 0;
    
                    // either add or subtract, based on the overdue flag the number of days specified in the notification from today
                    Calendar checkFor = Calendar.getInstance();
                    Calendar until;
                    if (notification.isOverdue()) {
                        checkFor.add(Calendar.DAY_OF_MONTH, (notification.getDays() + notification.getScope()) * -1);
                    } else {
                        checkFor.add(Calendar.DAY_OF_MONTH, notification.getDays() - notification.getScope());
                    }
                    until = (Calendar) checkFor.clone();
                    until.add(Calendar.DAY_OF_MONTH, notification.getScope());
                    clearTimeFields(checkFor);
                    clearTimeFields(until);
                    Map<Award, List<ReportTracking>> matchedReports = new HashMap<>();
                    Map<NotificationRecipient.Builder, List<ReportTracking>> recipients = 
                        new TreeMap<>(Comparator.comparing(NotificationRecipient.Builder::getRecipientId));
                    for (ReportTrackingNotificationTask task : notification.getTasks()) {
                        List<ReportTracking> reports = getReportTrackingDao().getDetailResults(task.getReportTrackingValueMap(), Collections.emptyList());
                        recordsFound += reports.size();
                        for (ReportTracking report : reports) {
                            //if the report's due date is between the checked for date and the scoped date, and a notification
                            //hasn't previously been sent for it, add it to the matched reports.
                            if (report.getDueDate() != null && doDatesMatch(report.getDueDate(), checkFor, until)
                                    && !hasSentNotification(report, notification)
                                    && report.getStatusCode() != null && StringUtils.equalsIgnoreCase(report.getStatusCode(), pendingStatus.getReportStatusCode())) {
                                Award curAward = awardService.getAward(report.getAwardId());
                                if (isAwardActive(curAward)) {
                                    recordsMatched++;
                                    report.setAward(curAward);
                                    List<ReportTracking> curReports = matchedReports.get(curAward);
                                    if (curReports == null) {
                                        curReports = new ArrayList<>();
                                        matchedReports.put(curAward, curReports);
                                    }
                                    curReports.add(report);
                                }
                            }
                        }
                        //rehash the reports per recipient found in the notification
                        for (Award award : matchedReports.keySet()) {
                            Set<NotificationRecipient.Builder> recips = notificationService.getNotificationRecipients(
                                            new AwardNotificationContext(award, notification.getActionCode(), notification.getName()));
                            for (NotificationRecipient.Builder recip : recips) {
                                List<ReportTracking> curReports = recipients.get(recip);
                                if (curReports == null) {
                                    curReports = new ArrayList<>();
                                    recipients.put(recip, curReports);
                                }
                                curReports.addAll(matchedReports.get(award));
                            }
                        }
                        for (Map.Entry<NotificationRecipient.Builder, List<ReportTracking>> entry : recipients.entrySet()) {
                            renderer.setReports(entry.getValue());
                            String message = renderer.render(notificationType.getMessage());
                            notificationService.sendNotification(notification.getName(),
                                    notificationType.getSubject(), message, Collections.singletonList(entry.getKey().getRecipientId()));
                            notificationsSent++;
                        }
                        //for each matched report, create a sent report record to make sure we don't attempt
                        //to send this notification for this report again.
                        List<SentReportNotification> sentReports = new ArrayList<>();
                        for (Map.Entry<Award, List<ReportTracking>> entry : matchedReports.entrySet()) {
                            for (ReportTracking report : entry.getValue()) {
                                sentReports.add(new SentReportNotification(notification.getActionCode(), report));
                            }
                        }
                        businessObjectService.save(sentReports);
                    }
                    details.setTrackingRecordsFound(recordsFound);
                    details.setTrackingRecordsMatched(recordsMatched);
                    details.setNotificationsSent(notificationsSent);
                }
            } catch (Exception e) {
                LOG.error("Error sending report tracking notifications for " + notification.getActionCode(), e);
                details.setErrorMessage(e.getMessage());
            }
        }
        
        return resultDetails;
    }

    @Override
    public ReportTrackingNotificationDetails runReportDigestNotification(String actionCode, String notificationName, boolean includeDue, boolean includeOverdue) {
        AwardReportTrackingDigestNotificationRenderer renderer = new AwardReportTrackingDigestNotificationRenderer();
        renderer.setIncludeDue(includeDue);
        renderer.setIncludeOverdue(includeOverdue);
        ReportStatus dueStatus = reportTrackingService.getDueReportStatus();
        ReportTrackingNotificationDetails details = new ReportTrackingNotificationDetails();
        details.setNotificationName(notificationName);
        details.setActionCode(actionCode);

        NotificationType notificationType = notificationService.getNotificationType(CoeusModule.AWARD_MODULE_CODE, actionCode);
        if (canSendNotifications(notificationType)) {
            details.setNotificationActive(true);
            try {
                Map<String, String> dueCriteria = Collections.singletonMap(ReportTrackingConstants.STATUS_CODE, dueStatus.getReportStatusCode());
                List<ReportTracking> dueReports = getReportTrackingDao().getDetailResults(dueCriteria, Collections.emptyList());
                Map<String, Set<ReportTracking>> reportsByRecipient = groupReportsByRecipient(dueReports, actionCode, notificationName);
                
                reportsByRecipient.forEach((recipientId, reports) -> {
                    renderer.setDigestReports(reports);
                    String message = renderer.render(notificationType.getMessage());
                    String subject = renderer.render(notificationType.getSubject());
                    notificationService.sendNotification(notificationName, subject, message, Collections.singletonList(recipientId));
                });
                populateReportDetails(reportsByRecipient, dueReports, details);
            } catch (Exception e) {
                LOG.error("Error sending report tracking digest notification for " + actionCode, e);
                details.setErrorMessage(e.getMessage());
            }
        }

        return details;
    }

    /**
     * Is date1 after from and before until?
     * @param date1
     * @param from
     * @param until
     * @return
     */
    protected boolean doDatesMatch(Date date1, Calendar from, Calendar until) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        clearTimeFields(cal1);
        return cal1.after(from) && (cal1.equals(until) || cal1.before(until));
    }
    
    /**
     * Check to see if this report tracking record has already had a notification sent.
     * @param report
     * @param notification
     * @return
     */
    protected boolean hasSentNotification(ReportTracking report, ReportTrackingNotification notification) {
        Map<String, Object> values = new HashMap<>();
        values.put(AwardConstants.AWARD_NUMBER, report.getAwardNumber());
        values.put(ReportTrackingConstants.REPORT_CLASS_CODE, report.getReportClassCode());
        values.put(ReportTrackingConstants.REPORT_CODE, report.getReportCode());
        values.put("dueDate", report.getDueDate());
        values.put("actionCode", notification.getActionCode());
        List<SentReportNotification> notifications = (List<SentReportNotification>) getBusinessObjectService().findMatching(SentReportNotification.class, values);
        return notifications != null && !notifications.isEmpty();
    }

    protected boolean isAwardActive(Award award) {
        return award != null
                && award.getStatusCode() != AwardConstants.AWARD_STATUS_PENDING_CODE
                && award.getStatusCode() != AwardConstants.AWARD_STATUS_INACTIVE_CODE
                && award.getAwardSequenceStatus().equals(VersionStatus.ACTIVE.name());
    }

    protected void clearTimeFields(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }

    protected boolean canSendNotifications(NotificationType notificationType) {
        return notificationType != null && notificationType.isActive()
                && notificationType.getNotificationTypeRecipients() != null
                && !notificationType.getNotificationTypeRecipients().isEmpty();
    }

    protected Map<String, Set<ReportTracking>> groupReportsByRecipient(Collection<ReportTracking> reports, String actionCode, String notificationName) {
        Map<String, Set<ReportTracking>> reportsByRecipient = new HashMap<>();
        for (ReportTracking report : reports) {
            report.setAward(awardService.getAward(report.getAwardId()));
            if (isAwardActive(report.getAward())) {
                AwardUnitHierarchyDescendingNotificationContext notificationContext =
                        new AwardUnitHierarchyDescendingNotificationContext(report.getAward(), actionCode, notificationName);
                notificationContext.setDescendsHierarchy(shouldNotificationContextDescendHierarchy());
                notificationService.getNotificationRecipients(notificationContext)
                        .forEach(recipient -> {
                            Set<ReportTracking> recipientReports = reportsByRecipient.getOrDefault(recipient.getRecipientId(), new HashSet<>());
                            recipientReports.add(report);
                            reportsByRecipient.put(recipient.getRecipientId(), recipientReports);
                });
            }
        }
        return reportsByRecipient;
    }

    protected void populateReportDetails(Map<String, Set<ReportTracking>> reportsByRecipient, List<ReportTracking> foundReports, ReportTrackingNotificationDetails details) {
        details.setNotificationRecipients(reportsByRecipient.size());
        details.setNotificationsSent(details.getNotificationRecipients());
        details.setTrackingRecordsMatched(foundReports.size());
        details.setTrackingRecordsFound(details.getTrackingRecordsMatched());
    }

    protected boolean shouldNotificationContextDescendHierarchy() {
        return getParameterService().getParameterValueAsBoolean(AwardDocument.class, ASCEND_UNIT_HIERARCHY_FOR_DIGEST_NOTIFICATIONS_PARAM, true);
    }

    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    protected KcNotificationService getNotificationService() {
        return notificationService;
    }

    public void setNotificationService(KcNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public List<ReportTrackingNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<ReportTrackingNotification> notifications) {
        this.notifications = notifications;
    }

    public AwardService getAwardService() {
        return awardService;
    }

    public void setAwardService(AwardService awardService) {
        this.awardService = awardService;
    }

    public AwardVersionService getAwardVersionService() {
        return awardVersionService;
    }

    public void setAwardVersionService(AwardVersionService awardVersionService) {
        this.awardVersionService = awardVersionService;
    }

    public ReportTrackingDao getReportTrackingDao() {
        return reportTrackingDao;
    }

    public void setReportTrackingDao(ReportTrackingDao reportTrackingDao) {
        this.reportTrackingDao = reportTrackingDao;
    }

    public ReportTrackingService getReportTrackingService() {
        return reportTrackingService;
    }

    public void setReportTrackingService(ReportTrackingService reportTrackingService) {
        this.reportTrackingService = reportTrackingService;
    }
}
