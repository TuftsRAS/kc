/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s;


import com.lowagie.text.pdf.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.coeus.propdev.api.s2s.S2sFormConfigurationService;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.specialreview.ProposalSpecialReviewHumanSubjectsAttachmentService;
import org.kuali.coeus.s2sgen.api.core.AuditError;
import org.kuali.coeus.s2sgen.api.core.S2SException;
import org.kuali.coeus.s2sgen.api.generate.FormGenerationResult;
import org.kuali.coeus.s2sgen.api.generate.FormGeneratorService;
import org.kuali.coeus.s2sgen.api.generate.FormMappingService;
import org.kuali.coeus.s2sgen.api.hash.GrantApplicationHashService;
import org.kuali.coeus.sys.api.model.KcFile;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.util.CollectionUtils;
import org.kuali.kra.infrastructure.KeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.kuali.coeus.propdev.impl.s2s.S2SXmlConstants.*;

@Component("s2sUserAttachedFormService")
public class S2sUserAttachedFormServiceImpl implements S2sUserAttachedFormService {

    private static final Log LOG = LogFactory.getLog(S2sUserAttachedFormServiceImpl.class);
    private static final String XFA_NS = "http://www.xfa.org/schema/xfa-data/1.0/";
    private static final String USER_ATTACHED_FORMS_ERRORS = "userAttachedFormsErrors";
    private static final String UPLOADED_FILE_IS_EMPTY = "Uploaded file is empty";
    private static final String NOT_FILLABLE_FORM = "Uploaded file is not Grants.Gov fillable form";
    private static final String DATASETS = "datasets";
    private static final String DATA = "data";
    private static final String NOT_CONTAIN_ANY_DATA = "The pdf form does not contain any data.";
    private static final String HUMAN_SUBJECT_STUDY_V1_0_PDF_POPOUT_NAME = "HumanSubjectStudy-V1.0.pdf";
    private static final String HUMAN_SUBJECT_STUDY_V1_0_PDF_POPOUT_HASH = "rC1ImTK7IEQ3Usp3FmNf1HKuMDE=";
    private static final String PHSHUMAN_SUBJECTS_AND_CLINICAL_TRIALS_INFO_V1_0 = "http://apply.grants.gov/forms/PHSHumanSubjectsAndClinicalTrialsInfo-V1.0";
    private static final String ATT = "ATT";
    private static final String HUMAN_SUBJECT_STUDY_V1_0 = "http://apply.grants.gov/forms/HumanSubjectStudy-V1.0";
    private static final String HUMAN_SUBJECT_STUDY = "HumanSubjectStudy";
    private static final String HUMAN_SUBJECT_STUDY_ATTACHMENT = "HumanSubjectStudyAttachment";

    @Autowired
    @Qualifier("formGeneratorService")
    private FormGeneratorService formGeneratorService;

    @Autowired
    @Qualifier("globalVariableService")
    private GlobalVariableService globalVariableService;

    @Autowired
    @Qualifier("s2sFormConfigurationService")
    private S2sFormConfigurationService s2sFormConfigurationService;

    @Autowired
    @Qualifier("formUtilityService")
    private FormUtilityService formUtilityService;

    @Autowired
    @Qualifier("formMappingService")
    private FormMappingService formMappingService;

    @Autowired
    @Qualifier("proposalSpecialReviewHumanSubjectsAttachmentService")
    private ProposalSpecialReviewHumanSubjectsAttachmentService proposalSpecialReviewHumanSubjectsAttachmentService;

    @Autowired
    @Qualifier("grantApplicationHashService")
    private GrantApplicationHashService grantApplicationHashService;

    @Override
    public List<S2sUserAttachedForm> extractNSaveUserAttachedForms(ProposalDevelopmentDocument developmentProposal,
                                                                   S2sUserAttachedForm s2sUserAttachedForm) throws TransformerException, IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        PdfReader reader = null;
        final List<S2sUserAttachedForm> formBeans;
        try{
            byte pdfFileContents[] = s2sUserAttachedForm.getNewFormFileBytes();
            if(pdfFileContents==null || pdfFileContents.length==0){
                S2SException s2sException = new S2SException(KeyConstants.S2S_USER_ATTACHED_FORM_EMPTY, UPLOADED_FILE_IS_EMPTY);
              s2sException.setTabErrorKey(USER_ATTACHED_FORMS_ERRORS);
              throw s2sException;
            }else{
                try{
                    reader = new PdfReader(pdfFileContents);
                }catch(IOException ioex){
                    S2SException s2sException = new S2SException(KeyConstants.S2S_USER_ATTACHED_FORM_NOT_PDF, NOT_FILLABLE_FORM,ioex.getMessage());
                    s2sException.setTabErrorKey(USER_ATTACHED_FORMS_ERRORS);
                    throw s2sException;
                }
                final List<KcFile> attachments = doHumanStudiesAttachmentsWorkaround(formUtilityService.extractAttachments(reader));
                final Collection<String> duplicates = CollectionUtils.findDuplicates(attachments, KcFile::getName);

                if (!duplicates.isEmpty()) {
                    S2SException s2sException = new S2SException();
                    s2sException.setErrorKey(KeyConstants.S2S_FORM_DUP_ATT);
                    s2sException.setParams(new String[] { StringUtils.isNotBlank(s2sUserAttachedForm.getFormName()) ? s2sUserAttachedForm.getFormName() : "User Attached Form", duplicates.stream().collect(Collectors.joining(", "))});
                    s2sException.setTabErrorKey(USER_ATTACHED_FORMS_ERRORS);
                    throw s2sException;
                }

                formBeans = extractAndPopulateXml(developmentProposal,reader,s2sUserAttachedForm,attachments);
            }
            setFormsAvailability(developmentProposal,formBeans);
        } finally {
            if (reader!=null) {
                reader.close();
            }
        }
        return formBeans;
    }

    private void throwInvalidError() {
        S2SException s2sException = new S2SException(KeyConstants.S2S_USER_ATTACHED_FORM_WRONG_FILE_TYPE, NOT_FILLABLE_FORM);
        s2sException.setTabErrorKey(USER_ATTACHED_FORMS_ERRORS);
        throw s2sException;
    }

    protected List<S2sUserAttachedForm> extractAndPopulateXml(ProposalDevelopmentDocument developmentProposal, PdfReader reader, S2sUserAttachedForm userAttachedForm, List<KcFile> attachments) throws TransformerException, IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        List<S2sUserAttachedForm> formBeans = new ArrayList<>();
        XfaForm xfaForm = reader.getAcroFields().getXfa();
        Document domDocument = xfaForm.getDomDocument();
        if (domDocument == null) {
            throwInvalidError();
        }

        final Element documentElement = domDocument.getDocumentElement();
        if (documentElement == null) {
            throwInvalidError();
        }

        final Element datasetsElement = (Element) documentElement.getElementsByTagNameNS(XFA_NS, DATASETS).item(0);
        if(datasetsElement == null){
            throwInvalidError();
        }
        final Element dataElement = (Element) datasetsElement.getElementsByTagNameNS(XFA_NS, DATA).item(0);
        if(dataElement == null){
            throwInvalidError();
        }

        final Node grantApplicationElement = dataElement.getChildNodes().item(0);

        if (grantApplicationElement == null) {
            S2SException s2sException = new S2SException(KeyConstants.S2S_USER_ATTACHED_FORM_NOT_FILLED, NOT_CONTAIN_ANY_DATA);
            s2sException.setTabErrorKey(USER_ATTACHED_FORMS_ERRORS);
            throw s2sException;
        }

        byte[] serializedXML = XfaForm.serializeDoc(grantApplicationElement);
        final Document document;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedXML)) {
            document = getFormUtilityService().createDomBuilder().parse(byteArrayInputStream);
        }
        if (document != null) {
            Element form;
            NodeList elements = document.getElementsByTagNameNS(META_GRANT_APPLICATION_NS, FORMS);
            Element element = (Element)elements.item(0);
            if(element!=null){
                NodeList formChildren = element.getChildNodes();
                int formsCount = formChildren.getLength();
                if(formsCount>1){
                     NodeList selectedOptionalFormElements = document.getElementsByTagNameNS(META_GRANT_APPLICATION_WRAPPER_NS, SELECTED_OPTIONAL_FORMS);
                    int selectedOptionalFormsCount = selectedOptionalFormElements==null?0:selectedOptionalFormElements.getLength();
                    if (selectedOptionalFormsCount > 0) {
                        Element selectedFormNode = (Element) selectedOptionalFormElements.item(0);
                        NodeList selectedForms = selectedFormNode.getElementsByTagNameNS(META_GRANT_APPLICATION_WRAPPER_NS, FORM_TAG_NAME);
                        int selectedFormsCount = selectedForms == null ? 0 : selectedForms.getLength();
                        if (selectedFormsCount > 0) {
                            List<String> seletctedForms = new ArrayList<>();
                            for (int j = 0; j < selectedFormsCount; j++) {
                                Element selectedForm = (Element) selectedForms.item(j);
                                String formName = selectedForm.getTextContent();
                                seletctedForms.add(formName);

                            }
                            for (int i = 0; i < formsCount; i++) {
                                form = (Element) formChildren.item(i);
                                String formNodeName = form.getLocalName();
                                if (seletctedForms.contains(formNodeName)) {
                                    addForm(developmentProposal, formBeans, form, userAttachedForm, attachments);
                                }
                            }
                        }
                    }
                }else{
                    form = (Element) formChildren.item(0);
                    addForm(developmentProposal,formBeans,form,userAttachedForm, attachments);
                }
            }else{
                form = document.getDocumentElement();
                addForm(developmentProposal,formBeans,form,userAttachedForm, attachments);
            }
        }
        return formBeans;
    }

    protected void addForm(ProposalDevelopmentDocument developmentProposal, List<S2sUserAttachedForm> formBeans, Element form,
                           S2sUserAttachedForm userAttachedFormBean, List<KcFile> attachments) throws TransformerException, XPathExpressionException {
        S2sUserAttachedForm userAttachedForm = processForm(developmentProposal, form,userAttachedFormBean,attachments);
        if(userAttachedForm!=null){
            formBeans.add(userAttachedForm);
        }
    }
    private void validateForm(ProposalDevelopmentDocument developmentProposal, S2sUserAttachedForm userAttachedForm) throws S2SException{
        S2sOpportunity opportunity = developmentProposal.getDevelopmentProposal().getS2sOpportunity();
        if(opportunity!=null){
            List<S2sUserAttachedForm> userAttachedForms = developmentProposal.getDevelopmentProposal().getS2sUserAttachedForms();
            for (S2sUserAttachedForm s2sForm : userAttachedForms) {
                if(userAttachedForm.getNamespace().equals(s2sForm.getNamespace())){
                    S2SException s2sException  = new S2SException(KeyConstants.S2S_DUPLICATE_USER_ATTACHED_FORM,
                                "The form is already available in the forms list",userAttachedForm.getFormName());
                    s2sException.setTabErrorKey(USER_ATTACHED_FORMS_ERRORS);
                    throw s2sException;
                }
            }
        }
    }

    private S2sUserAttachedForm processForm(ProposalDevelopmentDocument developmentProposal, Element form, S2sUserAttachedForm userAttachedForm, List<KcFile> attachments) throws TransformerException, XPathExpressionException {
        
        String formname;
        String namespaceUri;
        String formXML;
        namespaceUri = form.getNamespaceURI();
        formname = form.getLocalName();

        Document doc = formUtilityService.node2Dom(form);

        doHumanStudiesXmlWorkaround(doc, attachments);

        formUtilityService.correctAttachmentXml(doc, attachments);
        String xpathEmptyNodes = "//*[not(node()) and local-name(.) != 'FileLocation' and local-name(.) != 'HashValue' and local-name(.) != 'FileName']";// and not(FileLocation[@href])]";// and string-length(normalize-space(@*)) = 0 ]";
        String xpathOtherPers = "//*[local-name(.)='ProjectRole' and local-name(../../.)='OtherPersonnel' and count(../NumberOfPersonnel)=0]";
        formUtilityService.removeAllEmptyNodes(doc, xpathEmptyNodes, 0);
        formUtilityService.removeAllEmptyNodes(doc, xpathOtherPers, 1);
        formUtilityService.removeAllEmptyNodes(doc, xpathEmptyNodes, 0);
        formUtilityService.reorderXmlElements(doc);

        S2sUserAttachedForm newUserAttachedForm = cloneUserAttachedForm(userAttachedForm);
        newUserAttachedForm.setNamespace(namespaceUri);
        newUserAttachedForm.setFormName(formname);
        updateAttachmentNodes(newUserAttachedForm, attachments);
        formXML = formUtilityService.docToString(doc);

        S2sUserAttachedFormFile newUserAttachedFormFile = new S2sUserAttachedFormFile();
        newUserAttachedFormFile.setXmlFile(formXML);
        if (!validateUserAttachedFormFile(newUserAttachedFormFile, formname))
            return null;

        validateForm(developmentProposal,newUserAttachedForm);

        for (S2sUserAttachedFormAtt attachment : newUserAttachedForm.getS2sUserAttachedFormAtts()) {
            attachment.setS2sUserAttachedForm(newUserAttachedForm);
            for (S2sUserAttachedFormAttFile file : attachment.getS2sUserAttachedFormAttFiles()) {
                file.setS2sUserAttachedFormAtt(attachment);
            }
        }

        newUserAttachedFormFile.setFormFile(userAttachedForm.getNewFormFileBytes());
        newUserAttachedFormFile.setS2sUserAttachedForm(newUserAttachedForm);
        newUserAttachedFormFile.setUpdateUser(userAttachedForm.getUpdateUser());
        newUserAttachedForm.getS2sUserAttachedFormFileList().add(newUserAttachedFormFile);
        return newUserAttachedForm;
        
    }

    private List<KcFile> doHumanStudiesAttachmentsWorkaround(List<KcFile> attachments) {
        //remove the "empty" HumanSubjectStudy popout form from the list of attachments
        final List<KcFile> filteredAttachments =  attachments.stream()
                .filter(attachment -> !(HUMAN_SUBJECT_STUDY_V1_0_PDF_POPOUT_NAME.equals(attachment.getName()) && HUMAN_SUBJECT_STUDY_V1_0_PDF_POPOUT_HASH.equals(getGrantApplicationHashService().computeAttachmentHash(attachment.getData()))))
                .collect(Collectors.toList());

        if (LOG.isDebugEnabled()) {
            if (filteredAttachments.size() != attachments.size()) {
                LOG.debug(HUMAN_SUBJECT_STUDY_V1_0_PDF_POPOUT_NAME + " popup attachment with SHA-1 hash " + HUMAN_SUBJECT_STUDY_V1_0_PDF_POPOUT_HASH + " detected on User Attached Form and removed.");
            }
        }
        return filteredAttachments;
    }

    /**
     * Human Studies attachments are not properly deserialized by itext or pdfbox.  This method searches for human studies attachments deserializes
     * them into xml and manually appends the xml to the form.
     *
     * @param doc         the form as an Xml Document.  This document could get modified by this method.
     * @param attachments form attachments.  This map could get modified by this method.
     */
    private void doHumanStudiesXmlWorkaround(Document doc, List<KcFile> attachments) {
        final List<KcFile> allHsAttachments = new ArrayList<>();

        final NodeList hsAtt = doc.getElementsByTagNameNS(PHSHUMAN_SUBJECTS_AND_CLINICAL_TRIALS_INFO_V1_0, ATT);
        for (int i = 0; i < hsAtt.getLength(); i++) {
            final Node attachmentName = hsAtt.item(i);
            final List<KcFile> hsAttPdfs = attachments.stream().filter(a -> attachmentName.getTextContent().equals(a.getName())).collect(Collectors.toList());

            //remove the attachment from the attachment list because it is extracted and not included as a normal attachment
            attachments.removeAll(hsAttPdfs);
            hsAttPdfs.forEach(hsAttPdf -> {
                try {
                    final Map<String, Object> hsAttinfo = proposalSpecialReviewHumanSubjectsAttachmentService.getSpecialReviewAttachmentXmlFileData(hsAttPdf.getData());
                    if (hsAttinfo != null) {
                        final String hsXml = (String) hsAttinfo.get(ProposalSpecialReviewHumanSubjectsAttachmentService.CONTENT);
                        if (StringUtils.isNotBlank(hsXml)) {
                            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(hsXml.getBytes(StandardCharsets.UTF_8.name()))) {
                                final Document hsDocument = getFormUtilityService().createDomBuilder().parse(byteArrayInputStream);
                                final Node hsNode = hsDocument.getElementsByTagNameNS(HUMAN_SUBJECT_STUDY_V1_0, HUMAN_SUBJECT_STUDY).item(0);
                                final NodeList hsAttachment = doc.getElementsByTagNameNS(PHSHUMAN_SUBJECTS_AND_CLINICAL_TRIALS_INFO_V1_0, HUMAN_SUBJECT_STUDY_ATTACHMENT);
                                final Node hsAttachmentNode = hsAttachment.item(0);
                                hsAttachmentNode.appendChild(doc.importNode(hsNode, true));
                            }
                        }
                        final List<KcFile> hsctAttachments = (List<KcFile>) hsAttinfo.get(ProposalSpecialReviewHumanSubjectsAttachmentService.FILES);
                        if (hsctAttachments != null) {
                            allHsAttachments.addAll(hsctAttachments);
                        }
                    } else {
                        globalVariableService.getMessageMap().putError(USER_ATTACHED_FORMS_ERRORS, KeyConstants.S2S_USER_ATTACHED_FORM_NOT_VALID, "The human studies attachment " + attachmentName.getTextContent() + " is invalid");
                    }
                } catch (S2SException e) {
                    //throw here so that it isn't caught by an overly broach catch statement
                    throw e;
                } catch (RuntimeException | ParserConfigurationException | IOException | SAXException e) {
                    globalVariableService.getMessageMap().putError(USER_ATTACHED_FORMS_ERRORS, KeyConstants.S2S_USER_ATTACHED_FORM_NOT_VALID, "The human studies attachment " + attachmentName.getTextContent() + " is invalid");
                }
            });
        }
        attachments.addAll(allHsAttachments);
    }

    private boolean validateUserAttachedFormFile(S2sUserAttachedFormFile userAttachedFormFile, String formName) {
        FormGenerationResult result = formGeneratorService.validateUserAttachedFormFile(userAttachedFormFile, formName);
        if(!result.isValid()) {
            setValidationErrorMessage(result);
            return false;
        }
        return true;
    }

    protected void setValidationErrorMessage(FormGenerationResult result) {
        result.getErrors()
                .stream()
                .filter(error -> error.getLevel() == AuditError.Level.WARNING)
                .forEach(error -> globalVariableService.getMessageMap().putWarning(USER_ATTACHED_FORMS_ERRORS, KeyConstants.S2S_USER_ATTACHED_FORM_NOT_VALID, error.getMessageKey()));

        result.getErrors()
                .stream()
                .filter(error -> error.getLevel() != AuditError.Level.WARNING)
                .forEach(error -> globalVariableService.getMessageMap().putError(USER_ATTACHED_FORMS_ERRORS, KeyConstants.S2S_USER_ATTACHED_FORM_NOT_VALID, error.getMessageKey()));
    }


    private S2sUserAttachedForm cloneUserAttachedForm(
            S2sUserAttachedForm userAttachedForm) {
        S2sUserAttachedForm newUserAttachedForm = new S2sUserAttachedForm();
        newUserAttachedForm.setDescription(userAttachedForm.getDescription());
        newUserAttachedForm.setFormFileName(userAttachedForm.getFormFileName());
        newUserAttachedForm.setNamespace(userAttachedForm.getNamespace());
        newUserAttachedForm.setFormName(userAttachedForm.getFormName());
        newUserAttachedForm.setS2sUserAttachedFormAtts(userAttachedForm.getS2sUserAttachedFormAtts());
        newUserAttachedForm.setProposalNumber(userAttachedForm.getProposalNumber());
        newUserAttachedForm.setUpdateUser(userAttachedForm.getUpdateUser());
        newUserAttachedForm.setUpdateTimestamp(userAttachedForm.getUpdateTimestamp());
        return newUserAttachedForm;
    }

    private void updateAttachmentNodes(S2sUserAttachedForm userAttachedFormBean, List<KcFile> attachments) {
        final List<S2sUserAttachedFormAtt> attachmentList = attachments
                .stream()
                .map(attachment -> {
                    S2sUserAttachedFormAtt userAttachedFormAttachment = new S2sUserAttachedFormAtt();
                    S2sUserAttachedFormAttFile userAttachedFormAttachmentFile = new S2sUserAttachedFormAttFile();
                    userAttachedFormAttachmentFile.setAttachment(attachment.getData());
                    userAttachedFormAttachment.getS2sUserAttachedFormAttFiles().add(userAttachedFormAttachmentFile);
                    userAttachedFormAttachment.setContentId(attachment.getName());
                    userAttachedFormAttachment.setName(attachment.getName());
                    userAttachedFormAttachment.setType(attachment.getType());
                    userAttachedFormAttachment.setProposalNumber(userAttachedFormBean.getProposalNumber());
                    userAttachedFormAttachment.setS2sUserAttachedForm(userAttachedFormBean);
                    return userAttachedFormAttachment;
                })
                .collect(Collectors.toList());

        userAttachedFormBean.setS2sUserAttachedFormAtts(attachmentList);
    }

    @Override
    public void resetFormAvailability(ProposalDevelopmentDocument developmentProposal, String namespace) {
        S2sOpportunity opportunity = developmentProposal.getDevelopmentProposal().getS2sOpportunity();
        if(opportunity!=null) {
            List<S2sOppForms> oppForms = opportunity.getS2sOppForms(); 
            if(oppForms!=null) {
            	setS2sOppFormsAvailability(oppForms, namespace, false);
            }
        }
    }

    protected void setFormsAvailability(ProposalDevelopmentDocument developmentProposal, List<S2sUserAttachedForm> savedFormBeans) {
        S2sOpportunity opportunity = developmentProposal.getDevelopmentProposal().getS2sOpportunity();
        if(opportunity!=null){
            List<S2sOppForms> oppForms = opportunity.getS2sOppForms(); 
            if(oppForms!=null){
            	savedFormBeans.forEach(s2sUserAttachedForm -> setS2sOppFormsAvailability(oppForms, s2sUserAttachedForm.getNamespace(),  true));
            }
        }
    }
    
    protected void setS2sOppFormsAvailability(List<S2sOppForms> oppForms, String namespace, boolean uaf) {
    	oppForms.stream()
    	.filter(s2sOppForms -> s2sOppForms.getS2sOppFormsId().getOppNameSpace().equals(namespace))
    	.forEach(s2sOppForms -> {
    	    if (uaf) {
                s2sOppForms.setAvailable(true);
                s2sOppForms.setUserAttachedForm(true);
                s2sOppForms.setInclude(true);
            } else {
    	        //checking only generator support not UAT generators
    	        final boolean available = getFormMappingService().getFormInfo(namespace) != null;
                s2sOppForms.setAvailable(available);
                s2sOppForms.setUserAttachedForm(false);
                s2sOppForms.setInclude(s2sOppForms.getMandatory() && available);
            }
    	});
    }

    public GlobalVariableService getGlobalVariableService() {
        return globalVariableService;
    }

    public void setGlobalVariableService(GlobalVariableService globalVariableService) {
        this.globalVariableService = globalVariableService;
    }

    public FormGeneratorService getFormGeneratorService() {
        return formGeneratorService;
    }

    public void setFormGeneratorService(FormGeneratorService formGeneratorService) {
        this.formGeneratorService = formGeneratorService;
    }

    public FormUtilityService getFormUtilityService() {
        return formUtilityService;
    }

    public void setFormUtilityService(FormUtilityService formUtilityService) {
        this.formUtilityService = formUtilityService;
    }

    public FormMappingService getFormMappingService() {
        return formMappingService;
    }

    public void setFormMappingService(FormMappingService formMappingService) {
        this.formMappingService = formMappingService;
    }

    public S2sFormConfigurationService getS2sFormConfigurationService() {
        return s2sFormConfigurationService;
    }

    public void setS2sFormConfigurationService(S2sFormConfigurationService s2sFormConfigurationService) {
        this.s2sFormConfigurationService = s2sFormConfigurationService;
    }

    public ProposalSpecialReviewHumanSubjectsAttachmentService getProposalSpecialReviewHumanSubjectsAttachmentService() {
        return proposalSpecialReviewHumanSubjectsAttachmentService;
    }

    public void setProposalSpecialReviewHumanSubjectsAttachmentService(ProposalSpecialReviewHumanSubjectsAttachmentService proposalSpecialReviewHumanSubjectsAttachmentService) {
        this.proposalSpecialReviewHumanSubjectsAttachmentService = proposalSpecialReviewHumanSubjectsAttachmentService;
    }


    public GrantApplicationHashService getGrantApplicationHashService() {
        return grantApplicationHashService;
    }

    public void setGrantApplicationHashService(GrantApplicationHashService grantApplicationHashService) {
        this.grantApplicationHashService = grantApplicationHashService;
    }
}
