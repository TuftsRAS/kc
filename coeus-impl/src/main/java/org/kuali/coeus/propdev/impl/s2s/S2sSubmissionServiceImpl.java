/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s;

import gov.grants.apply.services.applicantwebservices_v2.GetApplicationListResponse;
import gov.grants.apply.services.applicantwebservices_v2.GetApplicationStatusDetailResponse;
import gov.grants.apply.services.applicantwebservices_v2.GetOpportunitiesResponse;
import gov.grants.apply.services.applicantwebservices_v2.SubmitApplicationResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.instprop.api.admin.ProposalAdminDetailsContract;
import org.kuali.coeus.instprop.api.admin.ProposalAdminDetailsService;
import org.kuali.coeus.instprop.api.sponsor.InstPropSponsorService;
import org.kuali.coeus.propdev.api.s2s.*;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.s2s.connect.OpportunitySchemaParserService;
import org.kuali.coeus.propdev.impl.s2s.connect.S2SConnectorService;
import org.kuali.coeus.propdev.impl.s2s.connect.S2sCommunicationException;
import org.kuali.coeus.s2sgen.api.core.ConfigurationConstants;
import org.kuali.coeus.s2sgen.api.generate.AttachmentData;
import org.kuali.coeus.s2sgen.api.generate.FormGenerationResult;
import org.kuali.coeus.s2sgen.api.generate.FormGeneratorService;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.kuali.coeus.sys.framework.util.CollectionUtils.entriesToMap;
import static org.kuali.coeus.sys.framework.util.CollectionUtils.entry;
import static org.kuali.coeus.sys.framework.util.CollectionUtils.distinctByKey;
import static org.kuali.rice.core.api.criteria.PredicateFactory.*;


@Component("s2sSubmissionService")
public class S2sSubmissionServiceImpl implements S2sSubmissionService {

    private static final Logger LOG = LogManager.getLogger(S2sSubmissionServiceImpl.class);
    private static final String GRANTS_GOV_STATUS_ERROR = "ERROR";
    private static final String ERROR_MESSAGE = "Exception Occurred";
    private static final String PREVENT_MULTIPLE_S2S_SUBMISSIONS = "PREVENT_MULTIPLE_S2S_SUBMISSIONS";

    @Autowired
    @Qualifier("proposalAdminDetailsService")
    private ProposalAdminDetailsService proposalAdminDetailsService;

    @Autowired
    @Qualifier("instPropSponsorService")
    private InstPropSponsorService instPropSponsorService;

    @Autowired
    @Qualifier("s2sOpportunityService")
    private S2sOpportunityService s2sOpportunityService;

    @Autowired
    @Qualifier("formGeneratorService")
    private FormGeneratorService formGeneratorService;

    @Autowired
    @Qualifier("s2sProviderService")
    private S2sProviderService s2sProviderService;

    @Autowired
    @Qualifier("s2SConfigurationService")
    private S2SConfigurationService s2SConfigurationService;

    @Autowired
    @Qualifier("opportunitySchemaParserService")
    private OpportunitySchemaParserService opportunitySchemaParserService;

    @Autowired
    @Qualifier("s2sFormConfigurationService")
    private S2sFormConfigurationService s2sFormConfigurationService;
    
    @Autowired
    @Qualifier("dataObjectService")
    private DataObjectService dataObjectService;

    @Autowired
    @Qualifier("parameterService")
    private ParameterService parameterService;

    /**
     *
     * This method is used to get the application status details.
     */
    @Override
    public String getStatusDetails(String ggTrackingId, String proposalNumber)
            throws S2sCommunicationException {
        if (StringUtils.isNotBlank(proposalNumber) && proposalNumber.contains(Constants.COLON)) {
            proposalNumber = StringUtils.split(proposalNumber, Constants.COLON)[0];
        }

        S2sOpportunityContract s2sOpportunity = s2sOpportunityService.findS2SOpportunityByProposalNumber(proposalNumber);

        Object statusDetail = null;
        GetApplicationStatusDetailResponse applicationStatusDetailResponse;
        applicationStatusDetailResponse = getS2sConnectorService(s2sOpportunity)
                .getApplicationStatusDetail(ggTrackingId, proposalNumber);
        if (applicationStatusDetailResponse != null) {
            statusDetail = applicationStatusDetailResponse.getDetailedStatus();
        }
        return statusDetail != null ? statusDetail.toString() : StringUtils.EMPTY;
    }

    /**
     * This method checks if status on grants.gov has changed since last check
     * and returns the status.
     */
    @Override
    public boolean checkForSubmissionStatusChange(
            ProposalDevelopmentDocument pdDoc, S2sAppSubmission appSubmission)
            throws S2sCommunicationException {
        boolean statusChanged = false;
        GetApplicationStatusDetailResponse applicationStatusDetailResponse = getS2sConnectorService(pdDoc.getDevelopmentProposal().getS2sOpportunity())
                .getApplicationStatusDetail(appSubmission.getGgTrackingId(),
                        pdDoc.getDevelopmentProposal().getProposalNumber());
        if (applicationStatusDetailResponse != null
                && applicationStatusDetailResponse.getDetailedStatus() != null) {
            String statusDetail = applicationStatusDetailResponse.getDetailedStatus();
            String statusStr = !statusDetail.toUpperCase().contains(GRANTS_GOV_STATUS_ERROR) ? statusDetail
                    : S2sAppSubmissionConstants.STATUS_GRANTS_GOV_SUBMISSION_ERROR;
            statusChanged = !appSubmission.getStatus().equalsIgnoreCase(
                    statusStr);
            appSubmission.setComments(statusDetail);
            appSubmission.setStatus(statusStr);
        } else {
            appSubmission
                    .setComments(S2sAppSubmissionConstants.STATUS_NO_RESPONSE_FROM_GRANTS_GOV);
        }
        return statusChanged;
    }

    /**
     * This method checks for the status of submission for the given
     * {@link ProposalDevelopmentDocument} on Grants.gov
     *
     * @param pdDoc for which status has to be checked
     */
    @Override
    public void refreshGrantsGov(ProposalDevelopmentDocument pdDoc)
            throws S2sCommunicationException {
        GetApplicationListResponse applicationListResponse = fetchApplicationListResponse(pdDoc.getDevelopmentProposal().getProposalNumber());
        if (applicationListResponse != null) {
            saveGrantsGovStatus(pdDoc, applicationListResponse);
        }
    }

    /**
     *
     * This method fetches the status from Grants Gov and saves in case status
     * is modified
     *
     * @param pdDoc
     *            {@link ProposalDevelopmentDocument}
     * @param applicationListResponse
     *            {@link GetApplicationListResponse} response from Grants Gov
     */
    protected void saveGrantsGovStatus(ProposalDevelopmentDocument pdDoc,
                                       GetApplicationListResponse applicationListResponse)
            throws S2sCommunicationException {
        S2sAppSubmission appSubmission = null;
        List<S2sAppSubmission> appSubmissionList = pdDoc
                .getDevelopmentProposal().getS2sAppSubmission();
        int submissionNo = 0;
        for (S2sAppSubmission s2AppSubmission : appSubmissionList) {
            if (s2AppSubmission.getSubmissionNumber() > submissionNo) {
                appSubmission = s2AppSubmission;
                submissionNo = s2AppSubmission.getSubmissionNumber();
            }
        }

        if (appSubmission != null) {
            boolean statusChanged = false;
            if (applicationListResponse.getApplicationInfo() == null
                    || applicationListResponse.getApplicationInfo()
                    .size() == 0) {
                statusChanged = checkForSubmissionStatusChange(pdDoc,
                        appSubmission);
            } else {
                int appSize = applicationListResponse
                        .getApplicationInfo().size();
                GetApplicationListResponse.ApplicationInfo ggApplication = applicationListResponse
                        .getApplicationInfo().get(appSize - 1);
                if (ggApplication != null) {
                    statusChanged = !appSubmission.getStatus()
                            .equalsIgnoreCase(
                                    ggApplication
                                            .getGrantsGovApplicationStatus()
                                            .value());
                    populateAppSubmission(pdDoc, appSubmission, ggApplication);
                }
            }
            if (statusChanged) {
               getDataObjectService().save(appSubmission);
            }
        }

    }

    /**
     * This method populates the {@link S2sAppSubmission} BO with details from
     * {@link ProposalDevelopmentDocument}
     */
    @Override
    public void populateAppSubmission(ProposalDevelopmentDocument pdDoc, S2sAppSubmission appSubmission,
                                      GetApplicationListResponse.ApplicationInfo ggApplication) {
        appSubmission.setGgTrackingId(ggApplication
                .getGrantsGovTrackingNumber());
        appSubmission
                .setReceivedDate(new Timestamp(ggApplication
                        .getReceivedDateTime().toGregorianCalendar()
                        .getTimeInMillis()));
        appSubmission.setStatus(ggApplication.getGrantsGovApplicationStatus()
                .toString());
        appSubmission.setLastModifiedDate(new Timestamp(ggApplication
                .getStatusDateTime().toGregorianCalendar().getTimeInMillis()));
        appSubmission.setAgencyTrackingId(ggApplication
                .getAgencyTrackingNumber());
        if (ggApplication.getAgencyTrackingNumber() != null
                && ggApplication.getAgencyTrackingNumber().length() > 0) {
            appSubmission
                    .setComments(S2sAppSubmissionConstants.STATUS_AGENCY_TRACKING_NUMBER_ASSIGNED);
            populateSponsorProposalId(pdDoc.getDevelopmentProposal(), appSubmission.getAgencyTrackingId());
        } else {
            appSubmission.setComments(ggApplication
                    .getGrantsGovApplicationStatus().toString());
        }
    }

    /**
     * This method fetches the application list from Grants.gov for a given
     * proposal.
     */
    @Override
    public GetApplicationListResponse fetchApplicationListResponse(
            String proposalNumber) throws S2sCommunicationException {
        S2sOpportunityContract s2sOpportunity = s2sOpportunityService.findS2SOpportunityByProposalNumber(proposalNumber);

        final String firstCfdaNumber = s2sOpportunity.getS2sOpportunityCfdas().stream().findFirst().get().getCfdaNumber();

        return getS2sConnectorService(s2sOpportunity).getApplicationList(s2sOpportunity.getOpportunityId(), firstCfdaNumber, s2sOpportunity.getProposalNumber());
    }

    /**
     *
     * Takes the appSubmission and proposal and if a federal tracking id has been specified, will
     * set on both the proposal development doc and the related institutional proposal doc
     * if there is not a sponsor proposal id already.
     */
    protected void populateSponsorProposalId(DevelopmentProposal developmentProposal, String agencyTrackingId) {
       if (StringUtils.isNotBlank(agencyTrackingId)) {
            if (StringUtils.isBlank(developmentProposal.getSponsorProposalNumber())) {
                developmentProposal.setSponsorProposalNumber(agencyTrackingId);
                developmentProposal = getDataObjectService().save(developmentProposal);
            }

            //find and populate the inst proposal sponsor proposal id as well
            Collection<? extends ProposalAdminDetailsContract> proposalAdminDetails = proposalAdminDetailsService.findProposalAdminDetailsByPropDevNumber(developmentProposal.getProposalNumber());

            for(ProposalAdminDetailsContract pad : proposalAdminDetails){
            	if (pad.getInstProposalId() != null) {
            		instPropSponsorService.updateSponsorProposalNumber(pad.getInstProposalId(), agencyTrackingId);
            	}
            }

        }
    }

    private boolean doubleSubmissionViolation(String proposalNumber) {
        if (getParameterService().getParameterValueAsBoolean(Constants.KC_S2S_PARAMETER_NAMESPACE, Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, PREVENT_MULTIPLE_S2S_SUBMISSIONS)) {
            final QueryResults<S2sAppSubmission> submissions = getDataObjectService().findMatching(S2sAppSubmission.class,
                    QueryByCriteria.Builder.create().setPredicates(and(equal("proposalNumber", proposalNumber),
                            isNotNull("ggTrackingId"))).build());

            if (submissions.getResults().stream().anyMatch(submission -> StringUtils.isNotBlank(submission.getGgTrackingId())) ) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Proposal Number: " + proposalNumber + " is already submitted.");
                }
                return true;
            }
        }
        return false;
    }

    /**
     * This method is used to submit forms to Grants.gov. It generates forms for
     * a given {@link ProposalDevelopmentDocument}, validates and then submits
     * the forms
     *
     * @param pdDoc Proposal Development Document.
     */
    @Override
    public boolean submitApplication(ProposalDevelopmentDocument pdDoc) throws S2sCommunicationException {
        final DevelopmentProposal proposal = pdDoc.getDevelopmentProposal();
        final String proposalNumber = proposal.getProposalNumber();

        if (doubleSubmissionViolation(proposalNumber)) {
            return true;
        }

        //form generation validation along with nih integration should have been done by ProposalDevelopmentGrantsGovAuditRule which is required in
        //order to submit.  Therefore, the specific failures are ignored here as this should not happen. If this step returns a not valid result,
        //then this indicates a bug.
        final FormGenerationResult result = formGeneratorService.generateAndValidateForms(pdDoc);
        if (result.isValid()) {
            final String xmlForSubmission = result.getApplicationXml();
            final List<AttachmentData> attachmentsForSubmission = result.getAttachments()
                    .stream()
                    .filter(distinctByKey(AttachmentData::getContentId))
                    .collect(Collectors.toList());
            logDuplicateContentIds(proposalNumber, result.getAttachments(), attachmentsForSubmission);

            final Map<String, DataHandler> attachments = attachmentsForSubmission
                    .stream()
                    .map(attachment -> entry(attachment.getContentId(), new DataHandler(new ByteArrayDataSource(attachment.getContent(), attachment.getContentType()))))
                    .collect(entriesToMap());

            final S2SConnectorService connectorService = getS2sConnectorService(proposal.getS2sOpportunity());
            final SubmitApplicationResponse response = connectorService.submitApplication(xmlForSubmission, attachments, proposalNumber);

            saveSubmissionDetails(pdDoc, response, xmlForSubmission, attachmentsForSubmission);
            return true;
        }

        LOG.error("Submission errors exist that were not prevented by ProposalDevelopmentGrantsGovAuditRule.  Proposal Number: " + proposalNumber + " Errors: " + result.getErrors());
        return false;
    }

    private void logDuplicateContentIds(String proposalNumber, List<AttachmentData> resultAttachments, List<AttachmentData> attachmentsForSubmission) {
        if (LOG.isWarnEnabled()) {
            if (resultAttachments.size() != attachmentsForSubmission.size()) {
                LOG.warn("Proposal " + proposalNumber + " has duplicate contentIds " + resultAttachments.stream().map(AttachmentData::getContentId).collect(Collectors.toList()));
            }
        }
    }

    /**
     * This method convert GetOpportunityListResponse to ArrayList&lt;OpportunityInfo&gt;
     *
     * @param resList
     *            {GetOpportunityListResponse}
     * @return ArrayList&lt;OpportunityInfo&gt; containing all form information
     */

    protected ArrayList<S2sOpportunity> convertToArrayList(String source,
                                                           GetOpportunitiesResponse resList) {
        ArrayList<S2sOpportunity> convList = new ArrayList<>();
        if (resList == null || resList.getOpportunityInfo() == null) {
            return convList;
        }
        for (GetOpportunitiesResponse.OpportunityInfo opportunityInfo : resList.getOpportunityInfo()) {
            convList.add(convert2S2sOpportunity(source, opportunityInfo));
        }
        return convList;
    }

    /**
     * This method convert OpportunityInformationType to OpportunityInfo
     *
     * @return OpportunityInfo containing Opportunity information corresponding
     *         to the OpportunityInformationType object.
     */
    protected S2sOpportunity convert2S2sOpportunity(
            String providerCode, GetOpportunitiesResponse.OpportunityInfo oppInfo) {

        S2sOpportunity s2Opportunity = new S2sOpportunity();

        s2Opportunity
                .setClosingDate(oppInfo.getClosingDate() == null ? null
                        : endOfDay(oppInfo.getClosingDate()
                        .toGregorianCalendar()));

        s2Opportunity.setCompetetionId(oppInfo.getCompetitionID());
        s2Opportunity.setInstructionUrl(oppInfo.getInstructionsURL());
        s2Opportunity
                .setOpeningDate(oppInfo.getOpeningDate() == null ? null
                        : endOfDay(oppInfo.getOpeningDate()
                        .toGregorianCalendar()));

        s2Opportunity.setOpportunityId(oppInfo.getFundingOpportunityNumber());
        s2Opportunity.setOpportunityTitle(oppInfo.getFundingOpportunityTitle());
        s2Opportunity.setSchemaUrl(oppInfo.getSchemaURL());
        s2Opportunity.setProviderCode(providerCode);
        s2Opportunity.setOfferingAgency(oppInfo.getOfferingAgency());
        s2Opportunity.setAgencyContactInfo(oppInfo.getAgencyContactInfo());

        s2Opportunity.setMultiProject(oppInfo.isIsMultiProject());

        final S2sOpportunityCfda cfda = new S2sOpportunityCfda();
        cfda.setCfdaDescription(oppInfo.getCFDADescription());
        cfda.setCfdaNumber(oppInfo.getCFDANumber());

        s2Opportunity.setS2sOpportunityCfdas(Stream.of(cfda).collect(Collectors.toList()));

        return s2Opportunity;
    }

    /**
     * Returns the given date at the last second of the day.
     * @return GregorianCalendar with the time set to the last second of the day.
     */
    private GregorianCalendar endOfDay(GregorianCalendar date) {
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        return date;
    }

    /**
     * This method is to find the list of available opportunities for a given
     * cfda number, opportunity ID and competition ID.
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
    @Override
    public List<S2sOpportunity> searchOpportunity(String providerCode, String cfdaNumber,
                                                  String opportunityId, String competitionId) throws S2sCommunicationException {

        //The OpportunityID and CompetitionID element definitions were changed from a string with
        //a length between 1 and 100 (StringMin1Max100Type) to an uppercase alphanumeric value with a maximum length
        //of 40 characters ([A-Z0-9\-]{1,40}).
        opportunityId = StringUtils.upperCase(opportunityId);
        opportunityId = StringUtils.isBlank(opportunityId) ? null : opportunityId;

        cfdaNumber = StringUtils.isBlank(cfdaNumber) ? null : cfdaNumber;

        competitionId = StringUtils.upperCase(competitionId);
        competitionId = StringUtils.isBlank(competitionId) ? null : competitionId;

        S2sProviderContract provider = s2sProviderService.findS2SProviderByCode(providerCode);
        if (provider == null) {
            throw new S2sCommunicationException("An invalid provider code was provided when attempting to search for opportunities.");
        }
        S2SConnectorService connectorService = KcServiceLocator.getService(provider.getConnectorServiceName());
        if (connectorService == null) {
            throw new S2sCommunicationException("The connector service '" + provider.getConnectorServiceName() + "' required by '" + provider.getDescription() + "' S2S provider is not configured.");
        }
        return convertToArrayList(provider.getCode(), connectorService.getOpportunityList(cfdaNumber, opportunityId, competitionId));
    }

    protected List<S2sOppForms> parseOpportunityForms(S2sOpportunity opportunity) throws S2sCommunicationException{
        setOpportunityContent(opportunity);
        return opportunitySchemaParserService.getForms(opportunity.getProposalNumber(),opportunity.getSchemaUrl());
    }

    @Override
    public List<String> setMandatoryForms(DevelopmentProposal proposal, S2sOpportunity s2sOpportunity) {
        List<String> missingMandatoryForms = new ArrayList<>();
        s2sOpportunity.setS2sProvider(getDataObjectService().find(S2sProvider.class, s2sOpportunity.getProviderCode()));
        List<S2sOppForms> s2sOppForms = parseOpportunityForms(s2sOpportunity);
        if (s2sOppForms != null) {
            for (S2sOppForms s2sOppForm : s2sOppForms) {
                final S2sFormConfigurationContract cfg = getS2sFormConfigurationService().findS2sFormConfigurationByFormName(s2sOppForm.getFormName());
                if (cfg != null) {
                    proposal.getS2sUserAttachedForms().stream()
                            .filter(form -> StringUtils.equals(form.getNamespace(), s2sOppForm.getOppNameSpace()))
                            .findFirst()
                            .ifPresent(form -> s2sOppForm.setUserAttachedForm(true));
                    s2sOppForm.setAvailable(s2sOppForm.getAvailable() && (cfg.isActive() || s2sOppForm.getUserAttachedForm()));
                }
                if (s2sOppForm.getMandatory() && !s2sOppForm.getAvailable()) {
                    missingMandatoryForms.add(s2sOppForm.getFormName());
                }
            }
        }
        if (CollectionUtils.isEmpty(missingMandatoryForms)) {
            if (s2sOppForms != null) {
                s2sOppForms.sort((arg0, arg1) -> {
                    int result = arg0.getMandatory().compareTo(arg1.getMandatory()) * -1;
                    if (result == 0) {
                        result = arg0.getFormName().compareTo(arg1.getFormName());
                    }
                    return result;
                });
            }
            s2sOpportunity.setS2sOppForms(s2sOppForms);
        }
        return missingMandatoryForms;
    }

    @Override
    public void setOpportunityContent(S2sOpportunity opportunity) {
        String opportunityContent = getOpportunityContent(opportunity.getSchemaUrl());
        opportunity.setOpportunity(opportunityContent);
    }

    private String getOpportunityContent(String schemaUrl) throws S2sCommunicationException{
        try (InputStream is = new URL(schemaUrl).openStream();
             BufferedInputStream br = new BufferedInputStream(is)) {
            byte bufContent[] = new byte[is.available()];
            br.read(bufContent);
            return new String(bufContent);
        }catch (IOException e) {
            LOG.error(ERROR_MESSAGE, e);
            throw new S2sCommunicationException(KeyConstants.ERROR_GRANTSGOV_FORM_SCHEMA_NOT_FOUND, e.getMessage(), schemaUrl);
        }
    }

    /**
     *
     * This method saves the submission details after successful submission of
     * proposal
     *
     * @param pdDoc {@link ProposalDevelopmentDocument} that was submitted
     * @param response {@link SubmitApplicationResponse} submission response from grants gov
     */
    protected void saveSubmissionDetails(ProposalDevelopmentDocument pdDoc, SubmitApplicationResponse response, String xmlForSubmission, List<AttachmentData> attachmentsForSubmission) {
        if (response != null) {
            String proposalNumber = pdDoc.getDevelopmentProposal().getProposalNumber();

            final S2sAppSubmission appSubmission = new S2sAppSubmission();
            appSubmission.setStatus(S2sAppSubmissionConstants.GRANTS_GOV_SUBMISSION_MESSAGE);
            appSubmission.setComments(S2sAppSubmissionConstants.GRANTS_GOV_COMMENTS_MESSAGE);



            final List<S2sAppAttachments> s2sAppAttachmentList = attachmentsForSubmission
                    .stream()
                    .map(attachment -> {
                        final S2sAppAttachments appAttachments = new S2sAppAttachments();
                        appAttachments.setContentId(attachment.getContentId());
                        appAttachments.setProposalNumber(proposalNumber);
                        appAttachments.setContentType(attachment.getContentType());
                        appAttachments.setHashCode(attachment.getHashValue());
                        return appAttachments;
                    }).collect(Collectors.toList());


            S2sApplication application = new S2sApplication();
            application.setApplication(xmlForSubmission);
            application.setProposalNumber(proposalNumber);
            application.setS2sAppAttachmentList(s2sAppAttachmentList);
            List<S2sApplication> s2sApplicationList = new ArrayList<>();
            s2sApplicationList.add(application);

            appSubmission
                    .setGgTrackingId(response.getGrantsGovTrackingNumber());
            appSubmission.setReceivedDate(new Timestamp(response
                    .getReceivedDateTime().toGregorianCalendar()
                    .getTimeInMillis()));
            appSubmission.setS2sApplication(s2sApplicationList.get(0));
            appSubmission.setProposalNumber(proposalNumber);

            List<S2sAppSubmission> appList = pdDoc.getDevelopmentProposal()
                    .getS2sAppSubmission();
            int submissionNumber = 1;
            for (S2sAppSubmission submittedApplication : appList) {
                if (submittedApplication.getSubmissionNumber() >= submissionNumber) {
                    ++submissionNumber;
                }
            }

            appSubmission.setSubmissionNumber(submissionNumber);
            getDataObjectService().save(appSubmission);
            pdDoc.getDevelopmentProposal().refreshReferenceObject("s2sAppSubmission");
        }
    }

    @Override
	public File getGrantsGovSavedFile(ProposalDevelopmentDocument pdDoc)
            throws IOException {

        String loggingDirectory = s2SConfigurationService.getValueAsString(ConfigurationConstants.PRINT_XML_DIRECTORY);
        String opportunityId = pdDoc.getDevelopmentProposal().getS2sOpportunity().getOpportunityId();
        String proposalnumber = pdDoc.getDevelopmentProposal().getProposalNumber();
        String exportDate = StringUtils.replaceChars((pdDoc.getDevelopmentProposal().getUpdateTimestamp().toString()), ":", "_");
        exportDate = StringUtils.replaceChars(exportDate, " ", ".");

        if (StringUtils.isNotBlank(loggingDirectory)) {
            File directory = new File(loggingDirectory);
            if(!directory.exists()){
                directory.createNewFile();
            }
            return new File(loggingDirectory + opportunityId + "." + proposalnumber + "." + exportDate + ".zip");
        } else {
            return null;
        }

    }

    protected S2SConnectorService getS2sConnectorService(S2sOpportunityContract s2sOpportunity) {
        return KcServiceLocator.getService(s2sOpportunity.getS2sProvider().getConnectorServiceName());
    }

    public S2sOpportunityService getS2sOpportunityService() {
        return s2sOpportunityService;
    }

    public void setS2sOpportunityService(S2sOpportunityService s2sOpportunityService) {
        this.s2sOpportunityService = s2sOpportunityService;
    }

    public ProposalAdminDetailsService getProposalAdminDetailsService() {
        return proposalAdminDetailsService;
    }

    public void setProposalAdminDetailsService(ProposalAdminDetailsService proposalAdminDetailsService) {
        this.proposalAdminDetailsService = proposalAdminDetailsService;
    }

    public InstPropSponsorService getInstPropSponsorService() {
        return instPropSponsorService;
    }

    public void setInstPropSponsorService(InstPropSponsorService instPropSponsorService) {
        this.instPropSponsorService = instPropSponsorService;
    }

    public FormGeneratorService getFormGeneratorService() {
        return formGeneratorService;
    }

    public void setFormGeneratorService(FormGeneratorService formGeneratorService) {
        this.formGeneratorService = formGeneratorService;
    }

    public S2sProviderService getS2sProviderService() {
        return s2sProviderService;
    }

    public void setS2sProviderService(S2sProviderService s2sProviderService) {
        this.s2sProviderService = s2sProviderService;
    }

    public S2SConfigurationService getS2SConfigurationService() {
        return s2SConfigurationService;
    }

    public void setS2SConfigurationService(S2SConfigurationService s2SConfigurationService) {
        this.s2SConfigurationService = s2SConfigurationService;
    }

    public OpportunitySchemaParserService getOpportunitySchemaParserService() {
        return opportunitySchemaParserService;
    }

    public void setOpportunitySchemaParserService(OpportunitySchemaParserService opportunitySchemaParserService) {
        this.opportunitySchemaParserService = opportunitySchemaParserService;
    }

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    public S2sFormConfigurationService getS2sFormConfigurationService() {
        return s2sFormConfigurationService;
    }

    public void setS2sFormConfigurationService(S2sFormConfigurationService s2sFormConfigurationService) {
        this.s2sFormConfigurationService = s2sFormConfigurationService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
