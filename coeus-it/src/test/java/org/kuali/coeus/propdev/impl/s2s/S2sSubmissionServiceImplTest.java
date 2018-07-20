package org.kuali.coeus.propdev.impl.s2s;

import gov.grants.apply.services.applicantwebservices_v2.GetApplicationListResponse;
import gov.grants.apply.system.applicantcommonelements_v1.SubmissionDetails;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentService;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.proposaldevelopment.rules.ProposalDevelopmentRuleTestBase;
import gov.grants.apply.system.grantscommontypes_v1.GrantsGovApplicationStatusType;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.service.DocumentService;

import javax.xml.datatype.DatatypeFactory;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

public class S2sSubmissionServiceImplTest extends ProposalDevelopmentRuleTestBase {
    private ProposalDevelopmentService proposalDevelopmentService;
    private S2sSubmissionService s2sSubmissionService;
    private DocumentService documentService;
    private DataObjectService dataObjectService;

    private String agencyTrackingNumber = "9999";

    protected ProposalDevelopmentService getProposalDevelopmentService(){
        if (proposalDevelopmentService == null)
            proposalDevelopmentService = KcServiceLocator.getService(ProposalDevelopmentService.class);
        return proposalDevelopmentService;
    }

    protected S2sSubmissionService getS2sSubmissionService(){
        if (s2sSubmissionService == null)
            s2sSubmissionService = KcServiceLocator.getService(S2sSubmissionService.class);
        return s2sSubmissionService;
    }

    protected DocumentService getDocumentService(){
        if (documentService == null)
            documentService = KcServiceLocator.getService(DocumentService.class);
        return documentService;
    }

    protected DataObjectService getDataObjectService(){
        if (dataObjectService == null)
            dataObjectService = KcServiceLocator.getService(DataObjectService.class);
        return dataObjectService;
    }

    @Test
    public void testPopulateAppSubmissionWithAgencyTrackingNumber() throws Exception {
        ProposalDevelopmentDocument proposalDevelopmentDocument = (ProposalDevelopmentDocument)getDocumentService().saveDocument(createProposal());

        S2sAppSubmission appSubmission = new S2sAppSubmission();
        SubmissionDetails ggApplication = getGgApplication(true);
        S2sSubmissionService s2sSubmissionService = getS2sSubmissionService();

        s2sSubmissionService.populateAppSubmission(proposalDevelopmentDocument, appSubmission, ggApplication);

        QueryByCriteria query = QueryByCriteria.Builder.create().setPredicates(equal("proposalNumber", proposalDevelopmentDocument.getDevelopmentProposal().getProposalNumber())).build();
        List<DevelopmentProposal> proposals = getDataObjectService().findMatching(DevelopmentProposal.class, query).getResults().stream().collect(Collectors.toList());

        Assert.assertEquals(agencyTrackingNumber, proposals.get(0).getSponsorProposalNumber());
    }

    @Test
    public void testPopulateAppSubmissionWithoutAgencyTrackingNumber() throws Exception {
        ProposalDevelopmentDocument proposalDevelopmentDocument = (ProposalDevelopmentDocument)getDocumentService().saveDocument(createProposal());

        S2sAppSubmission appSubmission = new S2sAppSubmission();
        SubmissionDetails ggApplication = getGgApplication(false);
        S2sSubmissionService s2sSubmissionService = getS2sSubmissionService();

        s2sSubmissionService.populateAppSubmission(proposalDevelopmentDocument, appSubmission, ggApplication);

        QueryByCriteria query = QueryByCriteria.Builder.create().setPredicates(equal("proposalNumber", proposalDevelopmentDocument.getDevelopmentProposal().getProposalNumber())).build();
        List<DevelopmentProposal> proposals = getDataObjectService().findMatching(DevelopmentProposal.class, query).getResults().stream().collect(Collectors.toList());

        Assert.assertNotEquals(agencyTrackingNumber, proposals.get(0).getSponsorProposalNumber());
    }

    private ProposalDevelopmentDocument createProposal() throws Exception {
        String SPONSOR_CODE = "000162";
        String ACTIVITY_TYPE_CODE = "1";
        String PROPOSAL_TYPE_CODE = "1";
        String PRIME_SPONSOR_CODE = "000120";
        String ORIGINAL_LEAD_UNIT = "000001";

        ProposalDevelopmentDocument document = getNewProposalDevelopmentDocument();
        DevelopmentProposal developmentProposal = document.getDevelopmentProposal();
        Date requestedStartDateInitial = new Date(System.currentTimeMillis());
        Date requestedEndDateInitial = new Date(System.currentTimeMillis());

        document.getDocumentHeader().setDocumentDescription("Original document");
        developmentProposal.setSponsorCode(SPONSOR_CODE);
        developmentProposal.setTitle("Project Title");
        developmentProposal.setRequestedStartDateInitial(requestedStartDateInitial);
        developmentProposal.setRequestedEndDateInitial(requestedEndDateInitial);
        developmentProposal.setActivityTypeCode(ACTIVITY_TYPE_CODE);
        developmentProposal.setProposalTypeCode(PROPOSAL_TYPE_CODE);
        developmentProposal.setOwnedByUnitNumber(ORIGINAL_LEAD_UNIT);
        developmentProposal.setPrimeSponsorCode(PRIME_SPONSOR_CODE);

        getProposalDevelopmentService().initializeUnitOrganizationLocation(document);
        getProposalDevelopmentService().initializeProposalSiteNumbers(document);

        return document;
    }

    private SubmissionDetails getGgApplication(boolean withAgencyTrackingNumber) throws Exception {
        SubmissionDetails ggApplication = new SubmissionDetails();

        ggApplication.setGrantsGovTrackingNumber("9999");
        ggApplication.setReceivedDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar());
        ggApplication.setGrantsGovApplicationStatus(GrantsGovApplicationStatusType.AGENCY_TRACKING_NUMBER_ASSIGNED);
        ggApplication.setStatusDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar());
        if (withAgencyTrackingNumber) {
            ggApplication.setAgencyTrackingNumber(agencyTrackingNumber);
        }

        return ggApplication;
    }

}
