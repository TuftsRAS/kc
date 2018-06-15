/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.common.api.document;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.coeus.common.api.document.dto.DocumentDetailsDto;
import org.kuali.coeus.common.api.document.service.WorkflowDetailsService;
import org.kuali.coeus.common.framework.custom.attr.CustomAttributeDocValue;
import org.kuali.coeus.common.framework.org.Organization;
import org.kuali.coeus.common.framework.unit.Unit;
import org.kuali.coeus.common.framework.unit.UnitService;
import org.kuali.coeus.common.questionnaire.framework.core.Questionnaire;
import org.kuali.coeus.propdev.impl.copy.ProposalCopyCriteria;
import org.kuali.coeus.propdev.impl.copy.ProposalCopyService;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentService;
import org.kuali.coeus.propdev.impl.location.ProposalSite;
import org.kuali.coeus.propdev.impl.person.ProposalPerson;
import org.kuali.coeus.propdev.impl.s2s.S2sSubmissionService;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.proposaldevelopment.rules.ProposalDevelopmentRuleTestBase;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.document.DocumentDetail;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

import java.sql.Date;
import java.util.*;

public class DocumentControllerTest extends ProposalDevelopmentRuleTestBase {

    private static final String QUICKSTART = "10000000001";
    private static final String PERSON1 = "10000000059";
    private static final String PERSON2 = "10000000049";
    String SPONSOR_CODE = "000340";
    String ACTIVITY_TYPE_CODE = "1";
    String PROPOSAL_TYPE_CODE = "1";
    String PRIME_SPONSOR_CODE = "000120";
    String ORIGINAL_LEAD_UNIT = "000001";
    public static final String FIRST_NAME = "althea";
    public static final String LAST_NAME = "burd";
    public static final String ALTHEA_BURD = "10000000045";


    @Before
    @Override
    public void setUp() throws Exception {
        documentService = KRADServiceLocatorWeb.getDocumentService();
        updateParameterForTesting("KC-PD", "Document", "proposaldevelopment.creditsplit.enabled", "N");
        updateParameterForTesting("KC-PD", "Document", "KEY_PERSON_CERTIFICATION_DEFERRAL", "BA");
        Map<String, Object> questionnaireCriteria = new HashMap<>();
        questionnaireCriteria.put("questionnaireSeqId", "1009");
        List<Questionnaire> questionnaire = (List<Questionnaire>) getBusinessObjectService().findMatching(Questionnaire.class, questionnaireCriteria);
        questionnaire.get(0).setActive(Boolean.FALSE);
        getBusinessObjectService().save(questionnaire);
        saveDoc();
    }


    @Test
    public void testWorkflowDetailsService() throws Exception {

        ProposalDevelopmentDocument proposalDocument = saveDoc();
        ProposalCopyCriteria criteria = new ProposalCopyCriteria();
        criteria.setLeadUnitNumber(ORIGINAL_LEAD_UNIT);
        ProposalDevelopmentDocument copiedDocument = getProposalCopyService().copyProposal(proposalDocument, criteria);
        proposalDocument = (ProposalDevelopmentDocument) getDocumentService().routeDocument(copiedDocument, "", new ArrayList<>());

        DocumentDetail detail = getWorkflowDetailsService().getDocumentDetail(proposalDocument.getDocumentNumber());
        getWorkflowDetailsService().calculateAndPersistDetailsForUsersInRouteLog(proposalDocument.getDocumentNumber(), detail);
        List<DocumentWorkflowUserDetails> matches = getMatches(proposalDocument.getDocumentNumber());
        Assert.assertTrue(matches.size() == 4);
        Assert.assertTrue(matches.stream().filter(match -> match.getPrincipalId().equalsIgnoreCase(PERSON2)).findFirst().get().getSteps() == 0);
        Assert.assertTrue(matches.stream().filter(match -> match.getPrincipalId().equalsIgnoreCase(PERSON1)).findFirst().get().getSteps() == 0);
        Assert.assertTrue(matches.stream().filter(match -> match.getPrincipalId().equalsIgnoreCase(QUICKSTART)).findFirst().get().getSteps() == 2);
        Assert.assertTrue(matches.stream().filter(match -> match.getPrincipalId().equalsIgnoreCase(ALTHEA_BURD)).findFirst().get().getSteps() == 1);

        ProposalDevelopmentDocument copiedDocument1 = getProposalCopyService().copyProposal(proposalDocument, criteria);
        ProposalDevelopmentDocument proposalDocument1 = (ProposalDevelopmentDocument) getDocumentService().routeDocument(copiedDocument1, "", new ArrayList<>());
        detail = getWorkflowDetailsService().getDocumentDetail(proposalDocument1.getDocumentNumber());
        getWorkflowDetailsService().calculateAndPersistDetailsForUsersInRouteLog(proposalDocument1.getDocumentNumber(), detail);

        ProposalDevelopmentDocument copiedDocument2 = getProposalCopyService().copyProposal(proposalDocument, criteria);
        ProposalDevelopmentDocument proposalDocument2 = (ProposalDevelopmentDocument) getDocumentService().routeDocument(copiedDocument2, "", new ArrayList<>());
        detail = getWorkflowDetailsService().getDocumentDetail(proposalDocument2.getDocumentNumber());
        getWorkflowDetailsService().calculateAndPersistDetailsForUsersInRouteLog(proposalDocument2.getDocumentNumber(), detail);

        ProposalDevelopmentDocument copiedDocument3 = getProposalCopyService().copyProposal(proposalDocument, criteria);
        ProposalDevelopmentDocument proposalDocument3 = (ProposalDevelopmentDocument) getDocumentService().routeDocument(copiedDocument3, "", new ArrayList<>());
        detail = getWorkflowDetailsService().getDocumentDetail(proposalDocument3.getDocumentNumber());
        getWorkflowDetailsService().calculateAndPersistDetailsForUsersInRouteLog(proposalDocument3.getDocumentNumber(), detail);

        ProposalDevelopmentDocument copiedDocument4 = getProposalCopyService().copyProposal(proposalDocument, criteria);
        ProposalDevelopmentDocument proposalDocument4 = (ProposalDevelopmentDocument) getDocumentService().routeDocument(copiedDocument4, "", new ArrayList<>());
        detail = getWorkflowDetailsService().getDocumentDetail(proposalDocument4.getDocumentNumber());
        getWorkflowDetailsService().calculateAndPersistDetailsForUsersInRouteLog(proposalDocument4.getDocumentNumber(), detail);

        ProposalDevelopmentDocument copiedDocument5 = getProposalCopyService().copyProposal(proposalDocument, criteria);
        ProposalDevelopmentDocument proposalDocument5 = (ProposalDevelopmentDocument) getDocumentService().routeDocument(copiedDocument5, "", new ArrayList<>());
        detail = getWorkflowDetailsService().getDocumentDetail(proposalDocument5.getDocumentNumber());
        getWorkflowDetailsService().calculateAndPersistDetailsForUsersInRouteLog(proposalDocument5.getDocumentNumber(), detail);

        ProposalDevelopmentDocument copiedDocument6 = getProposalCopyService().copyProposal(proposalDocument, criteria);
        ProposalDevelopmentDocument proposalDocument6 = (ProposalDevelopmentDocument) getDocumentService().routeDocument(copiedDocument6, "", new ArrayList<>());
        detail = getWorkflowDetailsService().getDocumentDetail(proposalDocument6.getDocumentNumber());
        getWorkflowDetailsService().calculateAndPersistDetailsForUsersInRouteLog(proposalDocument6.getDocumentNumber(), detail);

        List<DocumentDetailsDto> enrouteDocuments = getDocumentController().documentsRoutingToUser(QUICKSTART, null, null);
        Assert.assertTrue(enrouteDocuments.size() == 7);
        enrouteDocuments = getDocumentController().documentsRoutingToUser(QUICKSTART, 5, 0);
        Assert.assertTrue(enrouteDocuments.size() == 5);
        enrouteDocuments = getDocumentController().documentsRoutingToUser(QUICKSTART, 5, 2);
        Assert.assertTrue(enrouteDocuments.size() == 5);
        enrouteDocuments = getDocumentController().documentsRoutingToUser(QUICKSTART, 5, 3);
        Assert.assertTrue(enrouteDocuments.size() == 4);
    }

    public DocumentController getDocumentController() {
        return KcServiceLocator.getService(DocumentController.class);
    }

    private List<DocumentWorkflowUserDetails> getMatches(String documentNumber) {
        final List<DocumentWorkflowUserDetails> matches = getDataObjectService().findMatching(DocumentWorkflowUserDetails.class,
                QueryByCriteria.Builder
                        .andAttributes(Collections.singletonMap("documentNumber", documentNumber))
                        .build()).getResults();
        return matches;
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
        createEmpProposalPerson(document.getDevelopmentProposal(), Constants.PRINCIPAL_INVESTIGATOR_ROLE, FIRST_NAME, LAST_NAME, 1, ALTHEA_BURD);
        return document;
    }

    private void createEmpProposalPerson(DevelopmentProposal developmentProposal, String role, String firstName, String lastName, int proposalPersonNumber, String personId) {
        ProposalPerson person = new ProposalPerson();
        person.setProposalPersonNumber(proposalPersonNumber);
        person.setProposalPersonRoleId(role);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setMiddleName("middleName");
        setPersonData(person);
        person.setRolodexId(null);
        person.setDevelopmentProposal(developmentProposal);
        developmentProposal.getProposalPersons().add(person);
        person.setPersonId(personId);
    }

    public void setPersonData(ProposalPerson person) {
        person.setOfficePhone("321-321-1228");
        person.setEmailAddress("kcnotification@gmail.com");
        person.setFaxNumber("321-321-1289");
        person.setAddressLine1("addressLine1");
        person.setAddressLine2("addressLine2");
        person.setCity("Coeus");
        person.setPostalCode("53421");
        person.setCounty("UNITED STATES");
        person.setCountryCode("USA");
        person.setState("MA");
        person.setDirectoryTitle("directoryTitle");
        person.setDivision("division");
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
        newDocValue.setId(1L);
        newDocValue.setValue("34");
        document.getCustomDataList().add(newDocValue);
        newDocValue = new CustomAttributeDocValue();
        newDocValue.setDocumentNumber(document.getDocumentNumber());
        newDocValue.setId(4L);
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

    protected ProposalDevelopmentDocument saveDoc() throws Exception {
        ProposalDevelopmentDocument proposalDocument = createProposal();
        proposalDocument = (ProposalDevelopmentDocument) getDocumentService().saveDocument(proposalDocument);
        return proposalDocument;
    }

    public WorkflowDocumentActionsService getWorkflowDocumentActionsService() {
        return KcServiceLocator.getService(WorkflowDocumentActionsService.class);
    }

    protected Organization getOrgForUnit(String unitNumber) {
        UnitService unitService = KcServiceLocator.getService(UnitService.class);
        Unit unit = unitService.getUnit(unitNumber);
        return unit.getOrganization();
    }

    protected WorkflowDetailsService getWorkflowDetailsService() {
        return KcServiceLocator.getService(WorkflowDetailsService.class);
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
