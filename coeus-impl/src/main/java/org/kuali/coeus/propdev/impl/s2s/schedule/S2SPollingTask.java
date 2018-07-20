/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s.schedule;

import gov.grants.apply.services.applicantwebservices_v2.GetSubmissionListResponse;
import gov.grants.apply.system.applicantcommonelements_v1.SubmissionDetails;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.s2s.S2sAppSubmission;
import org.kuali.coeus.propdev.impl.s2s.S2sAppSubmissionConstants;
import org.kuali.coeus.propdev.impl.s2s.S2sSubmissionService;
import org.kuali.coeus.propdev.impl.s2s.connect.S2sCommunicationException;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.exception.InvalidAddressException;
import org.kuali.rice.krad.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * This class is run by the S2S Scheduler. ON every trigger, it polls data from Grants.gov for status of submitted proposals. On
 * receiving status, if it has changed from what exists in database, it updates the status in database and also sends emails
 * regarding status. All the required parameter configurations are injected from spring-beans.xml
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class S2SPollingTask {

    private static final Logger LOG = LogManager.getLogger(S2SPollingTask.class);
    private final List<String> lstStatus = new ArrayList<>();
    private final Map<String, String> sortMsgKeyMap = new Hashtable<>();

    @Autowired
    @Qualifier("dataObjectService")
    private DataObjectService dataObjectService;

    @Autowired
    @Qualifier("s2sSubmissionService")
    private S2sSubmissionService s2sSubmissionService;

    @Autowired
    @Qualifier("mailService")
    private MailService mailService;

    private String stopPollInterval;
    private String mailInterval;
    private Map<String, String> statusMap = new HashMap<>();
    private List<MailInfo> mailInfoList = new ArrayList<>();


    private static final String KEY_STATUS = "status";
    private static final String DATE_FORMAT = "dd-MMM-yyyy hh:mm a";

    private static final String SORT_ID_A = "A";
    private static final String SORT_ID_B = "B";
    private static final String SORT_ID_C = "C";
    private static final String SORT_ID_D = "D";
    private static final String SORT_ID_E = "E";
    private static final String SORT_ID_F = "F";
    private static final String SORT_ID_Z = "Z";


    public S2SPollingTask() {
        lstStatus.add(S2sAppSubmissionConstants.GRANTS_GOV_SUBMISSION_MESSAGE.toUpperCase());
        lstStatus.add(S2sAppSubmissionConstants.STATUS_RECEIVING.toUpperCase());
        lstStatus.add(S2sAppSubmissionConstants.STATUS_RECEIVED.toUpperCase());
        lstStatus.add(S2sAppSubmissionConstants.STATUS_PROCESSING.toUpperCase());
        lstStatus.add(S2sAppSubmissionConstants.STATUS_VALIDATED.toUpperCase());
        lstStatus.add(S2sAppSubmissionConstants.STATUS_RECEIVED_BY_AGENCY.toUpperCase());
        lstStatus.add(S2sAppSubmissionConstants.STATUS_AGENCY_TRACKING_NUMBER_ASSIGNED.toUpperCase());
        lstStatus.add(S2sAppSubmissionConstants.STATUS_REJECTED_WITH_ERRORS.toUpperCase());
        lstStatus.add(S2sAppSubmissionConstants.STATUS_GRANTS_GOV_SUBMISSION_ERROR.toUpperCase());

        sortMsgKeyMap.put("A", "Following proposals DID NOT validate at Grants.Gov");
        sortMsgKeyMap.put("B", "Following proposals are at an Unknown status at Grants.Gov");
        sortMsgKeyMap.put("C", "Following proposals will be dropped from the notification emails in next 24 hours");
        sortMsgKeyMap.put("D", "Following submissions status has not changed");
        sortMsgKeyMap.put("E", "Following submissions status has changed");
        sortMsgKeyMap.put("F", "Error occurred while retrieving submissions");
        sortMsgKeyMap.put("Z", "");
    }

    /**
     * 
     * This method determines whether the particular submission record received as parameter must be polled or not based on its last
     * modified date.
     */
    private boolean getSubmissionDateValidity(S2sAppSubmission appSubmission) {
        final Calendar lastModifiedDate = Calendar.getInstance();

        if (appSubmission.getLastModifiedDate() != null) {
            lastModifiedDate.setTimeInMillis(appSubmission.getLastModifiedDate().getTime());
        } else {
            lastModifiedDate.setTimeInMillis(appSubmission.getReceivedDate().getTime());
        }

        final long stopPollingIntervalMillis = Integer.parseInt(this.getStopPollInterval()) * 60 * 60 * 1000L;
        return (new Date().getTime() - lastModifiedDate.getTimeInMillis() < stopPollingIntervalMillis);
    }

    /**
     * 
     * This method filters out the latest submission record for each proposal and returns it in a map.
     * 
     * @return map of one submission record for each proposal
     */
    private Map<String, SubmissionData> populatePollingList() {
        final Collection<S2sAppSubmission> submissionList = dataObjectService
                .findMatching(S2sAppSubmission.class, QueryByCriteria.Builder.fromPredicates(PredicateFactory.in(KEY_STATUS, statusMap.values().toArray(new String[] {}))))
                .getResults();

        final Map<String, SubmissionData> pollingList = new HashMap<>();
        for (S2sAppSubmission appSubmission : submissionList) {
            if (pollingList.get(appSubmission.getProposalNumber()) != null) {
                if (appSubmission.getSubmissionNumber() > pollingList.get(appSubmission.getProposalNumber()).getS2sAppSubmission().getSubmissionNumber()) {
                    // Greater the submission number, more the latest
                    // Check if the record is not more than 6 months old
                    putIfValid(pollingList, appSubmission);
                }
            } else {
                putIfValid(pollingList, appSubmission);
            }
        }
        return pollingList;
    }

    private void putIfValid(Map<String, SubmissionData> pollingList, S2sAppSubmission appSubmission) {
        if (getSubmissionDateValidity(appSubmission)) {
            final SubmissionData submissionData = new SubmissionData();
            submissionData.setS2sAppSubmission(appSubmission);
            pollingList.put(appSubmission.getProposalNumber(), submissionData);
        }
    }

    /**
     * This method is the starting point of execution for the thread that is scheduled by the scheduler service
     */
    public void execute() {
        LOG.info("Executing polling schedule for status -" + statusMap.values() + ":" + stopPollInterval);
        final Map<String, SubmissionData> pollingList = populatePollingList();
        final int appListSize = pollingList.size();
        final HashMap<String, List<SubmissionData>> htMails = new LinkedHashMap<>();
        final List<SubmissionData> submList = new ArrayList<>();
        final Timestamp[] lastNotiDateArr = new Timestamp[appListSize];

        pollingList.values().forEach(localSubInfo -> {
            final S2sAppSubmission appSubmission = localSubInfo.getS2sAppSubmission();
            final ProposalDevelopmentDocument pdDoc = getProposalDevelopmentDocument(appSubmission.getProposalNumber());
            final Timestamp oldLastNotiDate = appSubmission.getLastNotifiedDate();
            final Timestamp today = new Timestamp(new Date().getTime());
            boolean updateFlag = false;
            boolean sendEmailFlag = false;
            boolean statusChanged = false;

            try {
                final GetSubmissionListResponse submissionListResponse = s2sSubmissionService.fetchSubmissionList(appSubmission);
                if (submissionListResponse == null || submissionListResponse.getSubmissionDetails() == null || submissionListResponse.getSubmissionDetails().isEmpty()) {
                    statusChanged = s2sSubmissionService.checkForSubmissionStatusChange(pdDoc, appSubmission);
                    if (!statusChanged && appSubmission.getComments().equals(S2sAppSubmissionConstants.STATUS_NO_RESPONSE_FROM_GRANTS_GOV)) {
                        localSubInfo.setSortId(SORT_ID_F);
                        sendEmailFlag = true;
                    }
                } else {
                    final SubmissionDetails ggApplication = submissionListResponse.getSubmissionDetails().get(0);
                    if (ggApplication != null) {
                        localSubInfo.setAcType('U');
                        statusChanged = !appSubmission.getStatus().equalsIgnoreCase(ggApplication.getGrantsGovApplicationStatus().value());
                        s2sSubmissionService.populateAppSubmission(pdDoc, appSubmission, ggApplication);
                    }
                }
            } catch (S2sCommunicationException e) {
                LOG.error(e.getMessage(), e);
                appSubmission.setComments(e.getMessage());
                localSubInfo.setSortId(SORT_ID_F);
                sendEmailFlag = true;
            }

            String sortId = SORT_ID_Z;
            final Timestamp lastNotifiedDate = appSubmission.getLastNotifiedDate();
            final Timestamp statusChangedDate = appSubmission.getLastModifiedDate();
            final Calendar lastNotifiedDateCal = Calendar.getInstance();
            if (lastNotifiedDate != null) {
                lastNotifiedDateCal.setTimeInMillis(lastNotifiedDate.getTime());
            }

            final Calendar statusChangedDateCal = Calendar.getInstance();
            if (statusChangedDate != null) {
                statusChangedDateCal.setTimeInMillis(statusChangedDate.getTime());
            }

            final Calendar recDateCal = Calendar.getInstance();
            recDateCal.setTimeInMillis(appSubmission.getReceivedDate().getTime());

            if (statusChanged) {
                if (appSubmission.getStatus().contains(S2sAppSubmissionConstants.STATUS_GRANTS_GOV_SUBMISSION_ERROR)) {
                    updateFlag = true;
                    sendEmailFlag = true;
                    sortId = SORT_ID_A;
                } else if (!lstStatus.contains(appSubmission.getStatus().trim().toUpperCase())) {
                    updateFlag = false;
                    sendEmailFlag = true;
                    sortId = SORT_ID_B;
                } else {
                    updateFlag = true;
                    sendEmailFlag = true;
                    sortId = SORT_ID_E;
                }
            } else {
                final long lastModifiedTime = statusChangedDate == null ? appSubmission.getReceivedDate().getTime() : statusChangedDate.getTime();
                final long lastNotifiedTime = lastNotifiedDate == null ? lastModifiedTime : lastNotifiedDate.getTime();
                final long mailDelta = today.getTime() - lastNotifiedTime;
                final long delta = today.getTime() - lastModifiedTime;

                final long stopPollDiff = ((Integer.parseInt(getStopPollInterval()) == 0 ? 4320L : Integer.parseInt(getStopPollInterval())) - (delta / (60 * 60 * 1000)));
                if ((mailDelta / (1000 * 60)) >= (Integer.parseInt(getMailInterval()))) {
                    if (localSubInfo.getSortId() == null) {
                        if (stopPollDiff <= 24) {
                            sortId = SORT_ID_C;
                        } else {
                            sortId = SORT_ID_D;
                            sortMsgKeyMap.put(SORT_ID_D, "Following submissions status has not been changed in " + getMailInterval() + " minutes");
                        }
                    }
                    updateFlag = true;
                    sendEmailFlag = true;
                }
            }

            if (sendEmailFlag) {

                final String dunsNum;
                if (pdDoc.getDevelopmentProposal().getApplicantOrganization().getOrganization().getDunsNumber() != null) {
                    dunsNum = pdDoc.getDevelopmentProposal().getApplicantOrganization().getOrganization().getDunsNumber();
                } else {
                    dunsNum = pdDoc.getDevelopmentProposal().getApplicantOrganization().getOrganizationId();
                }

                final List<SubmissionData> mailGrpForDunNum = new ArrayList<>();
                mailGrpForDunNum.add(localSubInfo);
                htMails.put(dunsNum, mailGrpForDunNum);
                appSubmission.setLastNotifiedDate(today);
            }

            if (localSubInfo.getSortId() == null) {
                localSubInfo.setSortId(sortId);
            }

            if (updateFlag) {
                submList.add(localSubInfo);
                lastNotiDateArr[submList.size() - 1] = oldLastNotiDate;
            }

        });

        try {
            sendMail(htMails);
        } catch (InvalidAddressException | MessagingException ex) {
            LOG.error(ex.getMessage(), ex);
            for (int i = 0; i < submList.size(); i++) {
                SubmissionData localSubInfo = submList.get(i);
                localSubInfo.getS2sAppSubmission().setLastNotifiedDate(lastNotiDateArr[i]);
            }
        }
        saveSubmissionDetails(submList);
    }

    private ProposalDevelopmentDocument getProposalDevelopmentDocument(String proposalNumber) {
        final DevelopmentProposal developmentProposal = dataObjectService.find(DevelopmentProposal.class, proposalNumber);
        return developmentProposal.getProposalDocument();
    }

    /**
     * This method saves submission data and status to database
     */
    private void saveSubmissionDetails(List<SubmissionData> submList) {
        if (submList != null) {
            submList.forEach(submissionData -> {
                S2sAppSubmission s2sAppSubmission = submissionData.getS2sAppSubmission();
                s2sAppSubmission.setUpdateUserSet(true);
                if(!s2sAppSubmission.getStatus().equalsIgnoreCase(S2sAppSubmissionConstants.STATUS_PUREGED))
                    dataObjectService.save(s2sAppSubmission);
            });
        }
    }

    /**
     * This method sends mail for all submission status records that have changed relative to database
     */
    private void sendMail(Map<String, List<SubmissionData>> htMails) throws InvalidAddressException , MessagingException {
        if (htMails.isEmpty()) {
            return;
        }
        final List<MailInfo> mailList = getMailInfoList();
        for (MailInfo mailInfo : mailList) {
            final String dunsNum = mailInfo.getDunsNumber();
            final List<SubmissionData> propList = htMails.get(dunsNum);
            if (propList == null) {
                continue;
            }

            htMails.remove(dunsNum);
            final MailMessage mailMessage = parseNGetMailAttr(propList, mailInfo);
            if (mailMessage != null) {
                mailService.sendMessage(mailMessage);
            }
        }

        if (mailList.size() > 0 && !htMails.isEmpty()) {
            for (String s : htMails.keySet()) {
                final List<SubmissionData> nonDunsPropList = htMails.get(s);
                final MailInfo mailInfo = mailList.get(0);
                final MailMessage mailMessage = parseNGetMailAttr(nonDunsPropList, mailInfo);
                if (mailMessage != null) {
                    mailService.sendMessage(mailMessage);
                    LOG.debug("Sent mail with default duns to " + mailMessage.getToAddresses() + " Subject as " + mailMessage.getSubject() + " Message as " + mailMessage.getMessage());
                }
            }
        }
    }

    /**
     * 
     * This method processes data that is to be sent by mail.
     */
    private MailMessage parseNGetMailAttr(List<SubmissionData> propList, MailInfo mailInfo) {
        if (propList == null || propList.isEmpty()) {
            return null;
        }

        final MailMessage mailMessage = mailInfo.getMailMessage();
        final StringBuilder message = new StringBuilder(mailMessage.getMessage());

        for (SubmissionData submissionData : propList) {
            final S2sAppSubmission appSubmission = submissionData.getS2sAppSubmission();
            final Timestamp lastNotifiedDate = appSubmission.getLastNotifiedDate();
            final Timestamp statusChangedDate = appSubmission.getLastModifiedDate();
            final Calendar lastNotifiedDateCal = Calendar.getInstance();

            if (lastNotifiedDate != null) {
                lastNotifiedDateCal.setTimeInMillis(lastNotifiedDate.getTime());
            }

            final Calendar statusChangedDateCal = Calendar.getInstance();
            if (statusChangedDate != null) {
                statusChangedDateCal.setTimeInMillis(statusChangedDate.getTime());
            }

            final Calendar recDateCal = Calendar.getInstance();
            recDateCal.setTimeInMillis(appSubmission.getReceivedDate().getTime());

            final long lastModifiedTime = statusChangedDate == null ? appSubmission.getReceivedDate().getTime() : statusChangedDate.getTime();
            final Timestamp today = new Timestamp(new Date().getTime());
            final long delta = today.getTime() - lastModifiedTime;
            final double deltaHrs = ((double) Math.round((delta / (1000.0d * 60.0d * 60.0d)) * Math.pow(10.0, 2))) / 100;

            int days = 0;
            int hrs = 0;
            if (deltaHrs > 0) {
                days = (int) deltaHrs / 24;
                hrs = (int) (((double) Math.round((deltaHrs % 24) * Math.pow(10.0, 2))) / 100);

            }

            if (propList.size() > 0) {
                final SubmissionData prevSubmissionData = propList.get(propList.size() - 1);
                if (!prevSubmissionData.getSortId().equals(submissionData.getSortId())) {
                    message.append("\n\n");
                    message.append(sortMsgKeyMap.get(submissionData.getSortId()));
                    message.append("\n____________________________________________________");
                }
            } else {
                message.append("\n\n");
                message.append(sortMsgKeyMap.get(submissionData.getSortId()));
                message.append("\n____________________________________________________");
            }

            final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            message.append('\n');
            message.append("Proposal Number : " + appSubmission.getProposalNumber() + "\n");
            message.append("Received Date : ");
            message.append(dateFormat.format(appSubmission.getReceivedDate()));
            message.append('\n');
            message.append("Grants.Gov Tracking Id : ");
            message.append(appSubmission.getGgTrackingId());
            message.append('\n');
            final String agTrackId = appSubmission.getAgencyTrackingId() == null ? "Not updated yet" : appSubmission.getAgencyTrackingId();
            message.append("Agency Tracking Id : ");
            message.append(agTrackId);
            message.append('\n');
            message.append("Current Status : ");
            message.append(appSubmission.getStatus());
            message.append('\n');
            final String stChnageDate = appSubmission.getLastModifiedDate() == null ? "Not updated yet" : dateFormat.format(appSubmission.getLastModifiedDate());
            message.append("Last Status Change : " + stChnageDate + "\t *** " + days + " day(s) and " + hrs + " hour(s) ***\n");
            message.append('\n');
        }
        message.append('\n');
        message.append(mailInfo.getFooter());
        mailMessage.setMessage(message.toString());

        return mailMessage;
    }
    
    public String getStopPollInterval() {
        return stopPollInterval;
    }
    
    public void setStopPollInterval(String stopPollInterval) {
        this.stopPollInterval = stopPollInterval;
    }
    
    public Map<String, String> getStatusMap() {
        return statusMap;
    }
    
    public void setStatusMap(Map<String, String> statusMap) {
        this.statusMap = statusMap;
    }
    
    public List<MailInfo> getMailInfoList() {
        return mailInfoList;
    }
    
    public void setMailInfoList(List<MailInfo> mailInfoList) {
        this.mailInfoList = mailInfoList;
    }
    
    public String getMailInterval() {
        return mailInterval;
    }
    
    public void setMailInterval(String mailInterval) {
        this.mailInterval = mailInterval;
    }


    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    public S2sSubmissionService getS2sSubmissionService() {
        return s2sSubmissionService;
    }

    public void setS2sSubmissionService(S2sSubmissionService s2sSubmissionService) {
        this.s2sSubmissionService = s2sSubmissionService;
    }

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }
}
