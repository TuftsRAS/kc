/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.s2sgen.impl.generate.support;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.coeus.common.framework.auth.perm.KcAuthorizationService;
import org.kuali.coeus.propdev.impl.attachment.Narrative;
import org.kuali.coeus.propdev.impl.attachment.NarrativeAttachment;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentService;
import org.kuali.coeus.propdev.impl.location.ProposalSite;
import org.kuali.coeus.propdev.impl.s2s.S2sOppForms;
import org.kuali.coeus.propdev.impl.s2s.S2sOpportunity;
import org.kuali.coeus.s2sgen.api.core.AuditError;
import org.kuali.coeus.s2sgen.api.core.S2SException;
import org.kuali.coeus.s2sgen.api.generate.FormMappingInfo;
import org.kuali.coeus.s2sgen.api.generate.FormMappingService;
import org.kuali.coeus.s2sgen.api.print.FormPrintResult;
import org.kuali.coeus.s2sgen.api.print.FormPrintService;
import org.kuali.coeus.s2sgen.impl.generate.S2SBaseFormGenerator;
import org.kuali.coeus.s2sgen.impl.generate.S2SFormGenerator;
import org.kuali.coeus.s2sgen.impl.generate.S2SFormGeneratorRetrievalService;
import org.kuali.coeus.s2sgen.impl.print.FormPrintServiceImpl;
import org.kuali.coeus.s2sgen.impl.validate.S2SValidatorService;
import org.kuali.coeus.s2sgen.impl.validate.ValidationResult;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.infrastructure.RoleConstants;
import org.kuali.kra.test.infrastructure.KcIntegrationTestBase;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.kuali.coeus.sys.framework.service.KcServiceLocator.getService;
/**
 * This is the base class for all generator Junit test classes.
 */
public abstract class S2STestBase extends KcIntegrationTestBase {
    private final static String ORGANIZATION_ID = "000001";

    private S2SBaseFormGenerator generatorObject;

    @Before
    public void setUp() throws Exception {
        GlobalVariables.setUserSession(new UserSession("quickstart"));
    }

    @After
    public void tearDown() throws Exception {
        GlobalVariables.setUserSession(null);
    }


    public FormPrintService getFormPrintService() {
        FormPrintService formPrintService = KcServiceLocator.getService(FormPrintService.class);
        ((FormPrintServiceImpl) formPrintService).setS2SFormGeneratorService(new S2SFormGeneratorRetrievalService() {
            @Override
            public S2SFormGenerator getS2SGenerator(String proposalNumber, String nameSpace) throws S2SException {
                generatorObject = KcServiceLocator.getService(getFormGeneratorName());
                return generatorObject;
            }

            @Override
            public XmlOptions getXmlOptionsPrefixes() {
                return KcServiceLocator.getService(S2SFormGeneratorRetrievalService.class).getXmlOptionsPrefixes();
            }
        });

        return formPrintService;
    }

    protected abstract void prepareData(ProposalDevelopmentDocument document) throws Exception;
    protected abstract String getFormGeneratorName();

    @Test
    public void testValidateForm() throws Exception {
        GlobalVariables.setUserSession(new UserSession("quickstart"));
        ProposalDevelopmentDocument document = initializeApp();

        prepareData(document);
        prepareS2sData(document);

        document = saveDocument(document);

        ArrayList<AuditError> errors = new ArrayList<>();
        generatorObject.setAuditErrors(errors);
        generatorObject.setAttachments(new ArrayList<>());
        XmlObject object=generatorObject.getFormObject(document);
        ValidationResult result = getService(S2SValidatorService.class).validateForm(object, generatorObject.getFormName());
        for (AuditError auditError : errors) {
            assertNull(auditError.getMessageKey()+":"+auditError.getErrorKey(),auditError.getErrorKey());
        }

        for (AuditError auditError : result.getErrors()) {
            assertNull(auditError.getMessageKey()+":"+auditError.getErrorKey(),auditError.getErrorKey());
        }
    }

    @Test
    public void testPrintForm() throws Exception {
        GlobalVariables.setUserSession(new UserSession("quickstart"));
        ProposalDevelopmentDocument document = initializeApp();

        prepareData(document);
        prepareS2sData(document);

        document = saveDocument(document);

        selectForm(document);

        FormPrintService formPrintService = getFormPrintService();
        FormPrintResult result = formPrintService.printForm(document);

        Assert.assertNotNull(result.getFile());
        Assert.assertTrue(ArrayUtils.isNotEmpty(result.getFile().getData()));
        Assert.assertTrue(StringUtils.isNotBlank(result.getFile().getName()));
        Assert.assertTrue(StringUtils.isNotBlank(result.getFile().getType()));

        for (AuditError auditError : result.getErrors()) {
            assertNull(auditError.getMessageKey()+":"+auditError.getErrorKey(),auditError.getErrorKey());
        }
    }

    public void saveProposalDocument(ProposalDevelopmentDocument pd) throws Exception {
        pd.setUpdateUser("quickst");
        pd.setUpdateTimestamp(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
        DocumentHeader docHeader = pd.getDocumentHeader();
        docHeader.setDocumentDescription("Test s2s service description");
        String docNumber = docHeader.getDocumentNumber();
        assertNotNull(docNumber);
        assertNotNull(pd.getDevelopmentProposal());
        KRADServiceLocatorWeb.getDocumentService().saveDocument(pd);
    }

    public ProposalDevelopmentDocument initializeDocument() throws Exception {
        ProposalDevelopmentDocument pd = (ProposalDevelopmentDocument) KRADServiceLocatorWeb.getDocumentService().getNewDocument("ProposalDevelopmentDocument");
        Assert.assertNotNull(pd.getDocumentHeader().getWorkflowDocument());
        ProposalDevelopmentService pdService = getService(ProposalDevelopmentService.class);
        pdService.initializeUnitOrganizationLocation(pd);
        pdService.initializeProposalSiteNumbers(pd);
        return pd;
    }

    public DevelopmentProposal initializeDevelopmentProposal(ProposalDevelopmentDocument pd) {
        DevelopmentProposal developmentProposal = pd.getDevelopmentProposal();
        developmentProposal.setPrimeSponsorCode("000120");
        developmentProposal.setActivityTypeCode("1");
        developmentProposal.refreshReferenceObject("activityType");
        developmentProposal.setSponsorCode("000162");
        developmentProposal.setOwnedByUnitNumber("000001");
        developmentProposal.refreshReferenceObject("ownedByUnit");
        developmentProposal.setProposalTypeCode("1");
        developmentProposal.setCreationStatusCode("1");
        developmentProposal.setApplicantOrganizationId("000001");
        developmentProposal.setPerformingOrganizationId("000001");
        developmentProposal.setNoticeOfOpportunityCode("1");
        developmentProposal.setRequestedStartDateInitial(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
        developmentProposal.setRequestedEndDateInitial(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
        developmentProposal.setTitle("Test s2s service title");
        developmentProposal.setDeadlineType("P");
        developmentProposal.setDeadlineDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
        developmentProposal.setNsfSequenceNumber(1);
        return developmentProposal;
    }

    protected ProposalDevelopmentDocument saveDocument(Document bo) throws Exception {
        return (ProposalDevelopmentDocument) getService(DocumentService.class).saveDocument(bo);
    }

    protected void saveBO(PersistableBusinessObject bo) throws Exception {
        getService(DataObjectService.class).save(bo);
    }

    public ProposalDevelopmentDocument initializeApp() throws Exception {
        generatorObject = KcServiceLocator.getService(getFormGeneratorName());
        ProposalDevelopmentDocument document = initializeDocument();
        initializeDevelopmentProposal(document);
        Assert.assertNotNull(document.getDocumentHeader().getWorkflowDocument());
        saveProposalDocument(document);
        document = (ProposalDevelopmentDocument) KRADServiceLocatorWeb.getDocumentService().getByDocumentHeaderId(document.getDocumentHeader().getDocumentNumber());
        getService(KcAuthorizationService.class).addDocumentLevelRole(GlobalVariables.getUserSession().getPrincipalId(), RoleConstants.AGGREGATOR, document);
        assertNotNull(document.getDevelopmentProposal());
        return document;
    }

    protected void prepareS2sData(ProposalDevelopmentDocument document) {
        S2sOpportunity opportunity = new S2sOpportunity();
        opportunity.setDevelopmentProposal(document.getDevelopmentProposal());
        opportunity.setProviderCode("1");
        opportunity.setOpportunity("An opportunity");
        opportunity.setOpportunityTitle("A title");

        List<S2sOppForms> forms = new ArrayList<>();

        S2sOppForms form = new S2sOppForms();
        form.setAvailable(true);

        S2sOppForms.S2sOppFormsId id = new S2sOppForms.S2sOppFormsId();
        id.setProposalNumber(document.getDevelopmentProposal().getProposalNumber());
        id.setOppNameSpace(findNamespace());
        form.setS2sOppFormsId(id);

        forms.add(form);

        opportunity.setS2sOppForms(forms);

        document.getDevelopmentProposal().setS2sOpportunity(opportunity);
    }

    protected String findNamespace() {
        FormMappingService ms = KcServiceLocator.getService(FormMappingService.class);
        for (FormMappingInfo info : ms.getBindings().values()) {
            if (getFormGeneratorName().equals(info.getGeneratorName())) {
                return info.getNameSpace();
            }
        }
        throw new RuntimeException("cannot find binding for : " + getFormGeneratorName() + " from the available bindings: " + ms.getBindings());
    }

    protected void selectForm(ProposalDevelopmentDocument document) {
        for (S2sOppForms form : document.getDevelopmentProposal().getS2sOppForms()) {
            form.setSelectToPrint(true);
        }
    }
    protected Narrative createNarrative(String narrativeTypeCode) throws IOException {
        Narrative narrative = new Narrative();
        NarrativeAttachment narrativeAttachment = new NarrativeAttachment();
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
        Resource resource = resourceLoader.getResource(S2STestConstants.ATT_PACKAGE + "/exercise1.pdf");
        InputStream inStream = resource.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(inStream);
        byte[] narrativePdf = new byte[bis.available()];
        narrativeAttachment.setData(narrativePdf);
        narrativeAttachment.setName("exercise1.pdf");
        narrative.setModuleNumber(1);
        narrative.setModuleSequenceNumber(1);
        narrative.setModuleStatusCode("C");
        narrative.setNarrativeTypeCode(narrativeTypeCode);
        narrative.refreshReferenceObject("narrativeType");
        narrative.setNarrativeAttachment(narrativeAttachment);
        narrative.setObjectId("12345678890abcd");
        narrative.setName("exercise1");
        return narrative;
    }

    protected ProposalSite createProposalSite(ProposalDevelopmentDocument document, Integer siteNumber, Integer locationTypeCode) {
        ProposalSite proposalSite = new ProposalSite();

        proposalSite.setOrganizationId(ORGANIZATION_ID);
        proposalSite.initializeDefaultCongressionalDistrict();
        proposalSite.setSiteNumber(siteNumber);
        proposalSite.setDevelopmentProposal(document.getDevelopmentProposal());
        proposalSite.setLocationTypeCode(locationTypeCode);

        return proposalSite;
    }
}
