/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s;

import gov.grants.apply.services.applicantwebservices_v2.GetApplicationListResponse;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.s2s.connect.S2sCommunicationException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface S2sSubmissionService {

    /**
     * This method is used to get the application status details.
     *
     * @param ggTrackingId
     *            grants gov tracking id for the application.
     * @param proposalNumber
     *            Proposal number.
     */
    String getStatusDetails(String ggTrackingId, String proposalNumber)
            throws S2sCommunicationException;

    /**
     * This method checks if status on grants.gov has changed since last check
     * and returns the status.
     */
    boolean checkForSubmissionStatusChange(
            ProposalDevelopmentDocument pdDoc, S2sAppSubmission appSubmission)
            throws S2sCommunicationException;

    /**
     * This method checks for the status of submission for the given
     * {@link ProposalDevelopmentDocument} on Grants.gov
     *
     * @param pdDoc for which status has to be checked
     */
    void refreshGrantsGov(ProposalDevelopmentDocument pdDoc)
            throws S2sCommunicationException;

    /**
     *
     * This method is used to submit forms to the grants.guv
     *
     */
    boolean submitApplication(ProposalDevelopmentDocument pdDoc) throws S2sCommunicationException;

    /**
     * This method is to find the list of available opportunities
     *
     * @param cfdaNumber
     *            of the opportunity.
     * @param opportunityId
     *            parameter for the opportunity.
     * @param competitionId
     *            parameter for the opportunity.
     * @return List&lt;S2sOpportunity&gt; a list containing the available
     *         opportunities for the corresponding parameters.
     */
    List<S2sOpportunity> searchOpportunity(String providerCode, String cfdaNumber,
                                                  String opportunityId, String competitionId) throws S2sCommunicationException;

    /**
     * Return the file saved to the local filesystem.
     */
    File getGrantsGovSavedFile(ProposalDevelopmentDocument pdDoc)
            throws IOException;

    GetApplicationListResponse fetchApplicationListResponse(
            ProposalDevelopmentDocument pdDoc) throws S2sCommunicationException;

    void populateAppSubmission(ProposalDevelopmentDocument pdDoc, S2sAppSubmission appSubmission,
                          GetApplicationListResponse.ApplicationInfo ggApplication);

    void setOpportunityContent(S2sOpportunity opportunity);


    List<String> setMandatoryForms(DevelopmentProposal proposal, S2sOpportunity s2sOpportunity);
}
