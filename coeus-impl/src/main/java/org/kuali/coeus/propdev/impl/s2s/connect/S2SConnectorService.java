/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s.connect;

import gov.grants.apply.services.applicantwebservices_v2.*;

import javax.activation.DataHandler;
import java.util.Map;

/**
 * 
 * This class will be used to call web services.
 */
public interface S2SConnectorService {

    GetOpportunityListResponse getOpportunityList(String cfdaNumber, String opportunityId, String competitionId, String packageId) throws S2sCommunicationException;
    /**
     * 
     * This method is to get status of the submitted application.
     * 
     * @param ggTrackingId grants gov tracking id for the proposal.
     * @param proposalNumber Proposal number.
     * @return GetApplicationStatusDetailResponse status of the submitted application.
     * @throws S2sCommunicationException
     */
    GetApplicationInfoResponse getApplicationInfo(String ggTrackingId, String proposalNumber) throws S2sCommunicationException;

    /**
     * 
     * This method is to get Application List from grants.gov.
     */
    GetSubmissionListResponse getSubmissionList(String opportunityId, String ggTrackingId, String packageId, String submissionTitle, String status, String proposalNumber) throws S2sCommunicationException;

    /**
     * 
     * This method is to submit a proposal to grants.gov
     * 
     * @param xmlText xml format of the form object.
     * @param attachments attachments of the proposal.
     * @param proposalNumber proposal number.
     * @return SubmitApplicationResponse corresponding to the input parameters passed.
     * @throws S2sCommunicationException
     */
    SubmitApplicationResponse submitApplication(String xmlText, Map<String, DataHandler> attachments, String proposalNumber) throws S2sCommunicationException;

}
