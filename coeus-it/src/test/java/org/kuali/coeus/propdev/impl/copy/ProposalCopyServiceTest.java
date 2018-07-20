/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.copy;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.coeus.common.framework.custom.attr.CustomAttributeDocValue;
import org.kuali.coeus.common.framework.org.Organization;
import org.kuali.coeus.common.framework.unit.Unit;
import org.kuali.coeus.common.framework.unit.UnitService;
import org.kuali.coeus.propdev.impl.attachment.Narrative;
import org.kuali.coeus.propdev.impl.attachment.NarrativeStatus;
import org.kuali.coeus.propdev.impl.attachment.NarrativeType;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentService;
import org.kuali.coeus.propdev.impl.location.ProposalSite;
import org.kuali.coeus.propdev.impl.person.ProposalPerson;
import org.kuali.coeus.propdev.impl.s2s.*;
import org.kuali.coeus.s2sgen.impl.generate.support.S2STestConstants;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.proposaldevelopment.rules.ProposalDevelopmentRuleTestBase;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import static org.junit.Assert.*;

public class ProposalCopyServiceTest extends ProposalDevelopmentRuleTestBase {
	ProposalCopyService proposalCopyService;
	ProposalDevelopmentDocument proposalDocument;

    private static final String SPONSOR_CODE = "000162";
    private static final String ACTIVITY_TYPE_CODE = "1";
    private static final String PROPOSAL_TYPE_CODE = "1";
    private static final String PRIME_SPONSOR_CODE = "000120";
    private static final String ORIGINAL_LEAD_UNIT = "000001";
    private static final String NEW_LEAD_UNIT = "BL-IIDC";
    private static final String OPP_ID = "PA-C-R03";
    private static final String COMP_ID = "FORMS-C";
    private static final String INS_URL = "insUrl";
    private static final String SCHEMA_URL = "http://at07apply.grants.gov/apply/opportunities/schemas/applicant/oppPA-C-R03-cfda93.838-cidFORMS-C.xsd";
    private static final String CFDA = "93.838";
    private static final String PROVIDER_CODE = "1";
    private static final String SUB_TYPE_CODE = "2";
    private static final String AGENCY_CONTACT_INFO= "contactInfo";

    private ProposalDevelopmentDocument oldDocument;

    @Before
    @Override
	public void setUp() throws Exception {
        documentService = KRADServiceLocatorWeb.getDocumentService();
        saveDoc();
    }

    private ProposalDevelopmentDocument createProposal() throws Exception {
		ProposalDevelopmentDocument document = getNewProposalDevelopmentDocument();
		Date requestedStartDateInitial = new Date(System.currentTimeMillis());
		Date requestedEndDateInitial = new Date(System.currentTimeMillis());


		document.getDocumentHeader()
				.setDocumentDescription("Original document");
		document.getDevelopmentProposal().setSponsorCode(SPONSOR_CODE);
		document.getDevelopmentProposal().setTitle("project title");
		document.getDevelopmentProposal().setRequestedStartDateInitial(
                requestedStartDateInitial);
		document.getDevelopmentProposal().setRequestedEndDateInitial(
                requestedEndDateInitial);
		document.getDevelopmentProposal().setActivityTypeCode(ACTIVITY_TYPE_CODE);
		document.getDevelopmentProposal().setProposalTypeCode(PROPOSAL_TYPE_CODE);
		document.getDevelopmentProposal().setOwnedByUnitNumber(ORIGINAL_LEAD_UNIT);
		document.getDevelopmentProposal().setPrimeSponsorCode(PRIME_SPONSOR_CODE);

        getProposalDevelopmentService().initializeUnitOrganizationLocation(document);
        getProposalDevelopmentService().initializeProposalSiteNumbers(document);

        document.getDevelopmentProposal().getProposalYnqs();
        createCustomDocument(document);
        List<Narrative> narratives = new ArrayList<>();
        narratives.add(getNewNarrative(document.getDevelopmentProposal()));
        document.getDevelopmentProposal().setNarratives(narratives);
        addNote(document);

		return document;
	}

	protected S2sUserAttachedForm getS2sUserAttachedForm(DevelopmentProposal proposal) throws IOException {
        S2sUserAttachedForm form = new S2sUserAttachedForm();
        form.setDevelopmentProposal(proposal);
        form.setProposalNumber(proposal.getProposalNumber());

        form.setDescription("A test UAF");
        form.setFormFileName("a_file.pdf");
        form.setFormName("Form_Name");
        form.setNamespace("A_Namespace");

        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
        Resource uafResource = resourceLoader.getResource(S2STestConstants.ATT_PACKAGE + "/Project_Abstract-V1.1_exercise1.pdf");
        InputStream uafStream = uafResource.getInputStream();
        BufferedInputStream uafBis = new BufferedInputStream(uafStream);

        S2sUserAttachedFormFile formFile = new S2sUserAttachedFormFile();
        formFile.setFormFile(IOUtils.toByteArray(uafBis));
        formFile.setXmlFile("<xml></xml>");
        formFile.setS2sUserAttachedForm(form);

        List<S2sUserAttachedFormFile> formFiles = new ArrayList<>();
        formFiles.add(formFile);

        form.setS2sUserAttachedFormFileList(formFiles);

        S2sUserAttachedFormAtt att = new S2sUserAttachedFormAtt();
        att.setProposalNumber(proposal.getProposalNumber());
        att.setName("exercise1.pdf");
        att.setType("application/pdf");
        att.setContentId("exercise1.pdf");
        att.setS2sUserAttachedForm(form);


        S2sUserAttachedFormAttFile file = new S2sUserAttachedFormAttFile();

        Resource attResource = resourceLoader.getResource(S2STestConstants.ATT_PACKAGE + "/Project_Abstract-V1.1_exercise1.pdf");
        InputStream attStream = attResource.getInputStream();
        BufferedInputStream attBis = new BufferedInputStream(attStream);

        file.setAttachment(IOUtils.toByteArray(attBis));
        file.setS2sUserAttachedFormAtt(att);

        List<S2sUserAttachedFormAttFile> files = new ArrayList<>();
        files.add(file);

        att.setS2sUserAttachedFormAttFiles(files);

        List<S2sUserAttachedFormAtt> attachments = new ArrayList<>();
        attachments.add(att);

        form.setS2sUserAttachedFormAtts(attachments);

        return form;
    }

    protected S2sOpportunity getS2s(DevelopmentProposal proposal) throws DatatypeConfigurationException {
        S2sOpportunity opportunity = new S2sOpportunity();
        opportunity.setAgencyContactInfo(AGENCY_CONTACT_INFO);
        opportunity.setSchemaUrl(SCHEMA_URL);
        opportunity.setCompetitionId(COMP_ID);
        opportunity.setInstructionUrl(INS_URL);
        opportunity.setOpportunityId(OPP_ID);
        opportunity.setOpportunity("bogus opportunity character data");

        S2sOpportunityCfda s2sCfda = new S2sOpportunityCfda();
        s2sCfda.setCfdaNumber(CFDA);
        s2sCfda.setProposalNumber(proposal.getProposalNumber());
        opportunity.getS2sOpportunityCfdas().add(s2sCfda);

        opportunity.setS2sSubmissionTypeCode(SUB_TYPE_CODE);
        opportunity.setProviderCode(PROVIDER_CODE);
        opportunity.setMultiProject(false);
        opportunity.setDevelopmentProposal(proposal);

        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date(System.currentTimeMillis()));
        XMLGregorianCalendar date1 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);


        opportunity.setClosingDate(date1.toGregorianCalendar());
        opportunity.setOpeningDate(date1.toGregorianCalendar());
        Boolean mandatoryFormNotAvailable = false;
        opportunity.setS2sProvider(getDataObjectService().find(S2sProvider.class, opportunity.getProviderCode()));
        List<S2sOppForms> s2sOppForms = getForms(proposal);
        if(s2sOppForms!=null){
            for(S2sOppForms s2sOppForm:s2sOppForms){
                if(s2sOppForm.getMandatory() && !s2sOppForm.getAvailable()){
                    mandatoryFormNotAvailable = true;
                    break;
                }
            }
        }
        if(!mandatoryFormNotAvailable) {
            Collections.sort(s2sOppForms, (arg0, arg1) -> {
                int result = arg0.getMandatory().compareTo(arg1.getMandatory()) * -1;
                if (result == 0) {
                    result = arg0.getFormName().compareTo(arg1.getFormName());
                }
                return result;
            });
            opportunity.setS2sOppForms(s2sOppForms);
        }
        return opportunity;
    }

    protected List<S2sOppForms> getForms(DevelopmentProposal proposal) {
        List<S2sOppForms> forms = new ArrayList<>();
        S2sOppForms rrSF424 = new S2sOppForms();
        rrSF424.setFormName("rrSF424-V2.0");
        rrSF424.setInclude(true);
        rrSF424.setMandatory(true);
        rrSF424.setAvailable(true);
        rrSF424.setSchemaUrl("https://trainingapply.grants.gov/apply/forms/schemas/rrSF424-V2.0.xsd");
        rrSF424.setSelectToPrint(null);
        S2sOppForms.S2sOppFormsId oppFormId = new S2sOppForms.S2sOppFormsId();
        oppFormId.setProposalNumber(proposal.getProposalNumber());
        oppFormId.setOppNameSpace("https://trainingapply.grants.gov/apply/forms/schemas/rrSF424-V2.0");
        rrSF424.setS2sOppFormsId(oppFormId);
        rrSF424.setUserAttachedForm(false);
        forms.add(rrSF424);
        S2sOppForms phsResearchPlan = new S2sOppForms();
        phsResearchPlan.setFormName("rrSF424-V2.0");
        phsResearchPlan.setInclude(true);
        phsResearchPlan.setAvailable(true);
        phsResearchPlan.setMandatory(true);
        phsResearchPlan.setSchemaUrl("https://trainingapply.grants.gov/apply/forms/schemas/phsResearchPlan-V2.0.xsd");
        oppFormId = new S2sOppForms.S2sOppFormsId();
        oppFormId.setProposalNumber(proposal.getProposalNumber());
        oppFormId.setOppNameSpace("https://trainingapply.grants.gov/apply/forms/schemas/phsResearchPlan-V2.0");
        phsResearchPlan.setS2sOppFormsId(oppFormId);
        phsResearchPlan.setSelectToPrint(null);
        phsResearchPlan.setUserAttachedForm(false);
        forms.add(phsResearchPlan);
        return forms;
    }
    protected void addNote(ProposalDevelopmentDocument proposalDocument) {
        Note note = new Note();
        note.setNoteText("testNote");
        note.setNoteTypeCode("BO");
        note.setNoteTopicText("topic");
        List<Note> notes = new ArrayList<>();
        notes.add(note);

        proposalDocument.setNotes(notes);
    }

    protected ProposalPerson getPersonnel() {
        ProposalPerson newProposalPerson = new ProposalPerson();
        newProposalPerson.setPersonId("10000000008");
        newProposalPerson.setFullName("Allyson Cate");
        newProposalPerson.setUserName("cate");
        newProposalPerson.setProposalPersonRoleId("PI");
        return newProposalPerson;
    }

    protected void createCustomDocument(ProposalDevelopmentDocument document) {
        CustomAttributeDocValue newDocValue = new CustomAttributeDocValue();
        newDocValue.setDocumentNumber(document.getDocumentNumber());
        newDocValue.setId(3L);
        newDocValue.setValue("34");
        document.getCustomDataList().add(newDocValue);
    }

    protected BusinessObjectService getBusinessObjectService() {
        return KcServiceLocator.getService(BusinessObjectService.class);
    }

    protected void setOrg(ProposalDevelopmentDocument document) {

        Unit ownedByUnit = document.getDevelopmentProposal().getOwnedByUnit();
        if (ownedByUnit != null) {
            String unitOrganizationId = ownedByUnit.getOrganizationId();
            for (ProposalSite proposalSite: document.getDevelopmentProposal().getProposalSites()) {
                // set location name to default from Organization
                proposalSite.setOrganizationId(unitOrganizationId);
                proposalSite.refreshReferenceObject("organization");
                proposalSite.setLocationName(proposalSite.getOrganization().getOrganizationName());
                proposalSite.setRolodexId(proposalSite.getOrganization().getContactAddressId());
                proposalSite.refreshReferenceObject("rolodex");
                proposalSite.initializeDefaultCongressionalDistrict();
            }
        }
    }

	@After
    @Override
	public void tearDown() throws Exception {
		proposalDocument = null;
		proposalCopyService = null;
	}

    protected void saveDoc() throws Exception {
        oldDocument = (ProposalDevelopmentDocument) getDocumentService().saveDocument(createProposal());
        oldDocument.getDevelopmentProposal().setS2sOpportunity(getS2s(oldDocument.getDevelopmentProposal()));

        List<S2sUserAttachedForm> uaf = new ArrayList<>();
        uaf.add(getS2sUserAttachedForm(oldDocument.getDevelopmentProposal()));

        oldDocument.getDevelopmentProposal().setS2sUserAttachedForms(uaf);
        getDocumentService().saveDocument(oldDocument);
    }

	@Test
	public void testCopySameUnitNoAttachment() throws Exception {

        ProposalCopyCriteria criteria = new ProposalCopyCriteria();
        criteria.setLeadUnitNumber(ORIGINAL_LEAD_UNIT);
	    ProposalDevelopmentDocument copiedDocument = getProposalCopyService().copyProposal(oldDocument, criteria);

        assertEquals(copiedDocument.getDevelopmentProposal().getOwnedByUnitNumber(), ORIGINAL_LEAD_UNIT);
        assertEquals(copiedDocument.getDevelopmentProposal().getActivityTypeCode(), ACTIVITY_TYPE_CODE);
        assertEquals(copiedDocument.getDevelopmentProposal().getSponsorCode(), SPONSOR_CODE);
        assertEquals(copiedDocument.getDevelopmentProposal().getProposalTypeCode(), PROPOSAL_TYPE_CODE);
        assertEquals(copiedDocument.getDevelopmentProposal().getPrimeSponsorCode(), PRIME_SPONSOR_CODE);

        // test org
        List<ProposalSite> copiedSites = copiedDocument.getDevelopmentProposal().getProposalSites();
        assertEquals(copiedSites.get(0).getOrganization().getOrganizationId(), "000001");
        assertEquals(copiedSites.get(1).getOrganization().getOrganizationId(), "000001");

        Organization organization = getDataObjectService().find(Organization.class, copiedSites.get(0).getOrganization().getOrganizationId());

        // test cong district
        assertEquals(copiedSites.get(0).getDefaultCongressionalDistrict().getCongressionalDistrict(), organization.getCongressionalDistrict());
        assertTrue(copiedDocument.getDevelopmentProposal().getNarratives().size() == 0);

        assertTrue(copiedDocument.getDevelopmentProposal().getS2sOpportunity() != null);
        assertEquals(copiedDocument.getDevelopmentProposal().getS2sOpportunity().getOpportunityId(), OPP_ID);
        assertTrue(copiedDocument.getDevelopmentProposal().getS2sOpportunity().getS2sOppForms().size() == 2);

        assertFalse(copiedDocument.getDevelopmentProposal().getS2sUserAttachedForms().isEmpty());
        assertFalse(copiedDocument.getDevelopmentProposal().getS2sUserAttachedForms().get(0).getS2sUserAttachedFormFileList().isEmpty());
        assertTrue(ArrayUtils.isNotEmpty(copiedDocument.getDevelopmentProposal().getS2sUserAttachedForms().get(0).getS2sUserAttachedFormFileList().get(0).getFormFile()));

        assertFalse(copiedDocument.getDevelopmentProposal().getS2sUserAttachedForms().get(0).getS2sUserAttachedFormAtts().isEmpty());
        assertTrue(ArrayUtils.isNotEmpty(copiedDocument.getDevelopmentProposal().getS2sUserAttachedForms().get(0).getS2sUserAttachedFormAtts().get(0).getData()));
    }

    @Test
    public void testCopyDifferentUnit() throws Exception {
        ProposalCopyCriteria criteria = new ProposalCopyCriteria();
        criteria.setLeadUnitNumber(NEW_LEAD_UNIT);
        ProposalDevelopmentDocument copiedDocument = getProposalCopyService().copyProposal(oldDocument, criteria);

        assertEquals(copiedDocument.getDevelopmentProposal().getOwnedByUnitNumber(), NEW_LEAD_UNIT);

        List<ProposalSite> copiedSites = copiedDocument.getDevelopmentProposal().getProposalSites();
        assertEquals(copiedSites.get(0).getOrganization().getOrganizationId(), getOrgForUnit(NEW_LEAD_UNIT).getOrganizationId());
        assertEquals(copiedSites.get(1).getOrganization().getOrganizationId(), getOrgForUnit(NEW_LEAD_UNIT).getOrganizationId());
        assertNotNull(copiedSites.get(0).getDefaultCongressionalDistrict().getCongressionalDistrict(), getOrgForUnit(NEW_LEAD_UNIT).getCongressionalDistrict());
    }

    @Test
    public void testCopyNarrative() throws Exception {
        ProposalCopyCriteria criteria = new ProposalCopyCriteria();
        criteria.setLeadUnitNumber(ORIGINAL_LEAD_UNIT);
        criteria.setIncludeAttachments(true);
        ProposalDevelopmentDocument copiedDocument = getProposalCopyService().copyProposal(oldDocument, criteria);
        assertTrue(copiedDocument.getDevelopmentProposal().getNarratives().size() == 1);

    }

    @Test
    public void testDeleteNarrative() throws Exception{
        ProposalCopyCriteria criteria = new ProposalCopyCriteria();
        criteria.setLeadUnitNumber(ORIGINAL_LEAD_UNIT);
        criteria.setIncludeAttachments(true);
        ProposalDevelopmentDocument copiedDocument = getProposalCopyService().copyProposal(oldDocument, criteria);
        copiedDocument.getDevelopmentProposal().setNarratives(new ArrayList<>());
        getDocumentService().saveDocument(copiedDocument);
        assertTrue(copiedDocument.getDevelopmentProposal().getNarratives().size() == 0);
        assertTrue(oldDocument.getDevelopmentProposal().getNarratives().size() == 1);
    }

    protected Narrative getNewNarrative(DevelopmentProposal proposal) throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "test.txt",                //filename
                "Hello World".getBytes()); //content
        Narrative narrative = new Narrative();
        narrative.setName("test");
        narrative.setModuleStatusCode(Constants.NARRATIVE_MODULE_STATUS_INCOMPLETE);
        Map<String, Object> map = new HashMap<>();
        map.put("code", "1");
        List<NarrativeType> types = (List<NarrativeType>) getBusinessObjectService().findMatching(NarrativeType.class, map);
        narrative.setNarrativeType(types.get(0));
        narrative.setNarrativeTypeCode(types.get(0).getCode());
        map = new HashMap<>();
        map.put("code", "C");
        List<NarrativeStatus> status = (List<NarrativeStatus>) getBusinessObjectService().findMatching(NarrativeStatus.class, map);
        narrative.setNarrativeStatus(status.get(0));
        narrative.setModuleNumber(1);
        narrative.setModuleSequenceNumber(1);
        narrative.init(mockMultipartFile);
        narrative.getNarrativeAttachment().setName("test");
        narrative.setInstitutionalAttachmentTypeCode(types.get(0).getCode());
        narrative.setDevelopmentProposal(proposal);

        return narrative;
    }

    protected Organization getOrgForUnit(String unitNumber) {
       UnitService unitService = KcServiceLocator.getService(UnitService.class);
       Unit unit = unitService.getUnit(unitNumber);
       return unit.getOrganization();
    }


    protected DocumentService getDocumentService() {
        return KcServiceLocator.getService(DocumentService.class);
    }

    protected ProposalCopyService getProposalCopyService() {
        return KcServiceLocator.getService(ProposalCopyService.class);
    }

    protected ProposalDevelopmentService getProposalDevelopmentService() {
        return KcServiceLocator.getService(ProposalDevelopmentService.class);
    }

    protected DataObjectService getDataObjectService() {
        return KcServiceLocator.getService(DataObjectService.class);
    }


    public S2sSubmissionService getS2sSubmissionService() {
        return KcServiceLocator.getService(S2sSubmissionService.class);
    }
}
