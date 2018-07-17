/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.propdev.api.s2s.S2sFormConfigurationContract;
import org.kuali.coeus.propdev.api.s2s.S2sUserAttachedFormFileContract;
import org.kuali.coeus.propdev.api.s2s.UserAttachedFormService;
import org.kuali.coeus.propdev.impl.auth.ProposalDevelopmentDocumentAuthorizer;
import org.kuali.coeus.propdev.impl.auth.ProposalDevelopmentDocumentViewAuthorizer;
import org.kuali.coeus.propdev.impl.core.*;
import org.kuali.coeus.propdev.impl.datavalidation.ProposalDevelopmentDataValidationConstants;
import org.kuali.coeus.propdev.impl.s2s.connect.S2sCommunicationException;
import org.kuali.coeus.propdev.impl.s2s.override.S2sOverride;
import org.kuali.coeus.propdev.impl.s2s.override.S2sOverrideApplicationData;
import org.kuali.coeus.propdev.impl.s2s.override.S2sOverrideAttachment;
import org.kuali.coeus.propdev.impl.sponsor.ProposalCfda;
import org.kuali.coeus.s2sgen.api.core.S2SException;
import org.kuali.coeus.s2sgen.api.generate.FormGenerationResult;
import org.kuali.coeus.s2sgen.api.generate.FormGeneratorService;
import org.kuali.coeus.s2sgen.api.print.FormPrintResult;
import org.kuali.coeus.s2sgen.api.print.FormPrintService;
import org.kuali.coeus.sys.api.model.KcFile;
import org.kuali.coeus.sys.framework.controller.ControllerFileUtils;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.util.AuditCluster;
import org.kuali.rice.krad.util.AuditError;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.service.RefreshControllerService;
import org.kuali.rice.krad.web.service.impl.CollectionControllerServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

@Controller
public class ProposalDevelopmentS2SController extends ProposalDevelopmentControllerBase {
    private static final String CONTENT_TYPE_XML = "text/xml";
    private static final String CONTENT_TYPE_PDF = "application/pdf";

    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(ProposalDevelopmentS2SController.class);
    private static final String GRANTS_GOV_FORM_VALIDATION_ERRORS = "grantsGovFormValidationErrors";
    private static final String CURRENT_GRANT_APPLICATION_XML = "Current Grant Application.xml";
    private static final String OVERRRIDDEN_GRANT_APPLICATION_XML = "Overridden Grant Application.xml";

    @Autowired
    @Qualifier("refreshControllerService")
    private RefreshControllerService refreshControllerService;

    @Autowired
    @Qualifier("s2sSubmissionService")
    private S2sSubmissionService s2sSubmissionService;

    @Autowired
    @Qualifier("s2sUserAttachedFormService")
    private S2sUserAttachedFormService s2sUserAttachedFormService;

    @Autowired
    @Qualifier("userAttachedFormService")
    private UserAttachedFormService userAttachedFormService;

    @Autowired
    @Qualifier("formPrintService")
    private FormPrintService formPrintService;

    @Autowired
    @Qualifier("proposalDevelopmentDocumentViewAuthorizer")
    private ProposalDevelopmentDocumentViewAuthorizer proposalDevelopmentDocumentViewAuthorizer;

    @Autowired
    @Qualifier("formGeneratorService")
    private FormGeneratorService formGeneratorService;

    private static final String ERROR_NO_GRANTS_GOV_FORM_SELECTED = "error.proposalDevelopment.no.grants.gov.form.selected";

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=refresh", "refreshCaller=S2sOpportunity-LookupView"})
   public ModelAndView refresh(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response)
           throws Exception {
       ProposalDevelopmentDocument document = form.getProposalDevelopmentDocument();
       DevelopmentProposal proposal = document.getDevelopmentProposal();
       if(form.getNewS2sOpportunity() != null 
               && StringUtils.isNotEmpty(form.getNewS2sOpportunity().getOpportunityId())) {

           proposal.setS2sOpportunity(form.getNewS2sOpportunity());
           proposal.getS2sOpportunity().setDevelopmentProposal(proposal);

           //Set default S2S Submission Type
           if (StringUtils.isBlank(form.getNewS2sOpportunity().getS2sSubmissionTypeCode())){
               String defaultS2sSubmissionTypeCode = getProposalTypeService().getDefaultSubmissionTypeCode(proposal.getProposalTypeCode());
               proposal.getS2sOpportunity().setS2sSubmissionTypeCode(defaultS2sSubmissionTypeCode);
               getDataObjectService().wrap(proposal.getS2sOpportunity()).fetchRelationship("s2sSubmissionType");
           }

           final String opportunityTitle = form.getNewS2sOpportunity().getOpportunityTitle();
           String trimmedTitle= StringUtils.substring(opportunityTitle, 0, ProposalDevelopmentConstants.S2sConstants.OPP_TITLE_MAX_LENGTH);
           //Set Opportunity Title and Opportunity ID in the Sponsor & Program Information section
           proposal.setProgramAnnouncementTitle(trimmedTitle);
           proposal.setProposalCfdas(new ArrayList<>());
           if (form.getNewS2sOpportunity().getS2sOpportunityCfdas() != null) {
               proposal.setProposalCfdas(form.getNewS2sOpportunity().getS2sOpportunityCfdas().stream().map(cfda -> {
                   cfda.setProposalNumber(proposal.getProposalNumber());
                   final ProposalCfda proposalCfda = new ProposalCfda();
                   proposalCfda.setCfdaNumber(cfda.getCfdaNumber());
                   proposalCfda.setCfdaDescription(cfda.getCfdaDescription());
                   proposalCfda.setProposalNumber(proposal.getProposalNumber());
                   return proposalCfda;
               }).collect(Collectors.toList()));
           }

           proposal.setProgramAnnouncementNumber(form.getNewS2sOpportunity().getOpportunityId());
           form.setNewS2sOpportunity(new S2sOpportunity());
       }

       S2sOpportunity s2sOpportunity = proposal.getS2sOpportunity();

       try {
           if (s2sOpportunity != null && s2sOpportunity.getSchemaUrl() != null) {
               final List<String> missingMandatoryForms = s2sSubmissionService.setMandatoryForms(proposal, s2sOpportunity);
               final List<? extends S2sFormConfigurationContract> cfgs = getS2sFormConfigurationService().findAllS2sFormConfigurations();

               final List<? extends S2sFormConfigurationContract> missingMandatoryCfgs = cfgs.stream()
                       .filter(missingMandatoryCfg -> missingMandatoryForms.contains(missingMandatoryCfg.getFormName()))
                       .filter(missingMandatoryCfg -> StringUtils.isNotBlank(missingMandatoryCfg.getInactiveMessage()))
                       .collect(Collectors.toList());

               missingMandatoryCfgs.forEach(missingMandatoryCfg -> getGlobalVariableService().getMessageMap().putError(Constants.NO_FIELD, ProposalDevelopmentDataValidationConstants.GENERIC_ERROR_LABEL_VALUE, missingMandatoryCfg.getFormName(), missingMandatoryCfg.getInactiveMessage()));
               final List<String> unreportedMissingMandatory = missingMandatoryForms.stream()
                       .filter(mmf -> missingMandatoryCfgs.stream().map(S2sFormConfigurationContract::getFormName)
                               .noneMatch(mmf::equals))
                       .collect(Collectors.toList());

               if (CollectionUtils.isNotEmpty(unreportedMissingMandatory)) {
                   getGlobalVariableService().getMessageMap().putError(Constants.NO_FIELD, KeyConstants.ERROR_IF_OPPORTUNITY_ID_IS_INVALID, s2sOpportunity.getOpportunityId(), StringUtils.join(unreportedMissingMandatory, ","));
               }

               if (CollectionUtils.isNotEmpty(missingMandatoryForms)) {
                   proposal.setS2sOpportunity(null);
               }

               final List<? extends S2sFormConfigurationContract> missingOptionalCfgs = cfgs.stream()
                       .filter(missingOptionalCfg -> !missingMandatoryForms.contains(missingOptionalCfg.getFormName()))
                       .filter(missingOptionalCfg -> s2sOpportunity.getS2sOppForms() != null && s2sOpportunity.getS2sOppForms().stream().anyMatch(s2sOppForm -> s2sOppForm.getFormName().equals(missingOptionalCfg.getFormName())))
                       .filter(missingOptionalCfg -> StringUtils.isNotBlank(missingOptionalCfg.getInactiveMessage()))
                       .collect(Collectors.toList());

               missingOptionalCfgs.forEach(missingOptionalCfg -> getGlobalVariableService().getMessageMap().putWarning(Constants.NO_FIELD, ProposalDevelopmentDataValidationConstants.GENERIC_ERROR_LABEL_VALUE, missingOptionalCfg.getFormName(), missingOptionalCfg.getInactiveMessage()));
           }
       }catch(S2sCommunicationException ex){
           if(ex.getErrorKey().equals(KeyConstants.ERROR_GRANTSGOV_NO_FORM_ELEMENT)) {
               ex.setMessage(s2sOpportunity.getOpportunityId());
           }
           getGlobalVariableService().getMessageMap().putError(Constants.NO_FIELD, ex.getErrorKey(),ex.getMessageWithParams());
           proposal.setS2sOpportunity(new S2sOpportunity());
       }
        super.save(form,result,request,response);
        return getRefreshControllerService().refresh(form);
   }


    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=clearOpportunity"})
   public ModelAndView clearOpportunity(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
       ((ProposalDevelopmentViewHelperServiceImpl)form.getViewHelperService()).clearOpportunity(form.getDevelopmentProposal());
       return getRefreshControllerService().refresh(form);
   }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=printFormsXml"})
    public ModelAndView printFormsXml(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, HttpServletResponse response)
            throws Exception {
        form.getDevelopmentProposal().setGrantsGovSelectFlag(true);
        return printForms(form,response);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=retrieveGrantApplicationXml"})
    public ModelAndView retrieveGrantApplicationXml(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, HttpServletResponse response)
            throws Exception {

        final S2sAppSubmission submission =  form.getDisplayedS2sAppSubmission();
        if (submission != null && submission.getS2sApplication() != null) {
            final S2sApplication application = submission.getS2sApplication();

            try (ByteArrayOutputStream out = new ByteArrayOutputStream(); StringReader in = new StringReader(application.getApplication())) {
                final Transformer tf = TransformerFactory.newInstance().newTransformer();
                tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tf.setOutputProperty(OutputKeys.INDENT, "yes");
                final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(in));
                tf.transform(new DOMSource(doc), new StreamResult(out));

                ControllerFileUtils.streamOutputToResponse(response, out, application.getContentType(), application.getName(), out.size());
            }
        }
        return null;
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=printForms"})
    public ModelAndView printForms(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, HttpServletResponse response)
            throws Exception {
        ProposalDevelopmentDocument proposalDevelopmentDocument = form.getProposalDevelopmentDocument();

        proposalDevelopmentDocumentViewAuthorizer.initializeDocumentAuthorizerIfNecessary(form.getProposalDevelopmentDocument());

        if (!((ProposalDevelopmentDocumentAuthorizer) proposalDevelopmentDocumentViewAuthorizer.getDocumentAuthorizer()).isAuthorizedToPrint(proposalDevelopmentDocument, getGlobalVariableService().getUserSession().getPerson())) {
            throw new AuthorizationException(getGlobalVariableService().getUserSession().getPrincipalName(), "printForms", "Proposal");
        }

        if(proposalDevelopmentDocument.getDevelopmentProposal().getSelectedS2sOppForms().isEmpty()){
            getGlobalVariableService().getMessageMap().putError("noKey", ERROR_NO_GRANTS_GOV_FORM_SELECTED);
            return getModelAndViewService().getModelAndView(form);
        }
        FormPrintResult formPrintResult = getFormPrintService().printForm(proposalDevelopmentDocument);

        setValidationErrorMessage(formPrintResult.getErrors());
        KcFile attachmentDataSource = formPrintResult.getFile();
        if(((attachmentDataSource==null || attachmentDataSource.getData()==null || attachmentDataSource.getData().length==0) && !proposalDevelopmentDocument.getDevelopmentProposal().getGrantsGovSelectFlag())
                || CollectionUtils.isNotEmpty(formPrintResult.getErrors())){
            boolean grantsGovErrorExists = copyAuditErrorsToPage();
            if(grantsGovErrorExists){
                getGlobalVariableService().getMessageMap().putError(GRANTS_GOV_FORM_VALIDATION_ERRORS, KeyConstants.VALIDATTION_ERRORS_BEFORE_GRANTS_GOV_SUBMISSION);
            }
            proposalDevelopmentDocument.getDevelopmentProposal().setGrantsGovSelectFlag(false);
            return getModelAndViewService().getModelAndView(form);
        }
        if (proposalDevelopmentDocument.getDevelopmentProposal().getGrantsGovSelectFlag()) {
            File grantsGovXmlDirectoryFile = getS2sSubmissionService().getGrantsGovSavedFile(proposalDevelopmentDocument);
            byte[] bytes = new byte[(int) grantsGovXmlDirectoryFile.length()];
            FileInputStream fileInputStream = new FileInputStream(grantsGovXmlDirectoryFile);
            fileInputStream.read(bytes);
            int size = bytes.length;
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(size)) {
                outputStream.write(bytes);
                ControllerFileUtils.streamOutputToResponse(response, outputStream, "binary/octet-stream", grantsGovXmlDirectoryFile.getName(), size);
                response.flushBuffer();
            }
            proposalDevelopmentDocument.getDevelopmentProposal().setGrantsGovSelectFlag(false);
            return getModelAndViewService().getModelAndView(form);
        }

        ControllerFileUtils.streamToResponse(attachmentDataSource, response);
        return getModelAndViewService().getModelAndView(form);
    }

    protected void setValidationErrorMessage(List<org.kuali.coeus.s2sgen.api.core.AuditError> errors) {
        if (errors != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Error list size:" + errors.size() + errors.toString());
            }
            final List<org.kuali.rice.krad.util.AuditError> auditErrors = errors
                    .stream()
                    .filter(error -> error.getLevel() != org.kuali.coeus.s2sgen.api.core.AuditError.Level.WARNING)
                    .map(error -> new org.kuali.rice.krad.util.AuditError(error.getErrorKey(), Constants.GRANTS_GOV_GENERIC_ERROR_KEY, error.getLink(), new String[]{error.getMessageKey()}))
                    .collect(Collectors.toList());

            final List<org.kuali.rice.krad.util.AuditError> auditWarnings = errors
                    .stream()
                    .filter(error -> error.getLevel() == org.kuali.coeus.s2sgen.api.core.AuditError.Level.WARNING)
                    .map(error -> new org.kuali.rice.krad.util.AuditError(error.getErrorKey(), Constants.GRANTS_GOV_GENERIC_ERROR_KEY, error.getLink(), new String[]{error.getMessageKey()}))
                    .collect(Collectors.toList());

            if (!auditErrors.isEmpty()) {
                getGlobalVariableService().getAuditErrorMap().put(
                        "grantsGovAuditErrors",
                        new AuditCluster(Constants.GRANTS_GOV_OPPORTUNITY_PANEL,
                                auditErrors, Constants.GRANTSGOV_ERRORS)
                );
            }

            if (!auditWarnings.isEmpty()) {
                getGlobalVariableService().getAuditErrorMap().put(
                        "grantsGovAuditErrors",
                        new AuditCluster(Constants.GRANTS_GOV_OPPORTUNITY_PANEL,
                                auditErrors, Constants.GRANTSGOV_WARNINGS)
                );
            }
        }
    }

    protected boolean copyAuditErrorsToPage() {
        boolean auditClusterFound = false;
        for (String errorKey : getGlobalVariableService().getAuditErrorMap().keySet()) {
            AuditCluster auditCluster = getGlobalVariableService().getAuditErrorMap().get(errorKey);
            if (StringUtils.equalsIgnoreCase(auditCluster.getCategory(), Constants.GRANTSGOV_ERRORS)) {
                auditClusterFound = true;
                for (Object error : auditCluster.getAuditErrorList()) {
                    AuditError auditError = (AuditError) error;
                    getGlobalVariableService().getMessageMap().putError(errorKey == null ? auditError.getErrorKey() : errorKey,
                            auditError.getMessageKey(), auditError.getParams());
                }
            }

            if (StringUtils.equalsIgnoreCase(auditCluster.getCategory(), Constants.GRANTSGOV_WARNINGS)) {
                for (Object error : auditCluster.getAuditErrorList()) {
                    AuditError auditError = (AuditError) error;
                    getGlobalVariableService().getMessageMap().putWarning(errorKey == null ? auditError.getErrorKey() : errorKey,
                            auditError.getMessageKey(), auditError.getParams());
                }
            }
        }
        return auditClusterFound;
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=addUserAttachedForm"})
    public ModelAndView addUserAttachedForm(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form)
            throws Exception {
        S2sUserAttachedForm s2sUserAttachedForm = form.getS2sUserAttachedForm();
        ProposalDevelopmentDocument proposalDevelopmentDocument = form.getProposalDevelopmentDocument();

        MultipartFile userAttachedFormFile = s2sUserAttachedForm.getNewFormFile();

        s2sUserAttachedForm.setNewFormFileBytes(userAttachedFormFile.getBytes());
        s2sUserAttachedForm.setFormFileName(userAttachedFormFile.getOriginalFilename());
        s2sUserAttachedForm.setProposalNumber(proposalDevelopmentDocument.getDevelopmentProposal().getProposalNumber());
        try{
            List<S2sUserAttachedForm> userAttachedForms = getS2sUserAttachedFormService().extractNSaveUserAttachedForms(proposalDevelopmentDocument,s2sUserAttachedForm);
            proposalDevelopmentDocument.getDevelopmentProposal().getS2sUserAttachedForms().addAll(userAttachedForms);
            form.setS2sUserAttachedForm(new S2sUserAttachedForm());
        }catch(S2SException ex){
            LOG.error(ex.getMessage(),ex);
            if(ex.getTabErrorKey()!=null){
                if(getGlobalVariableService().getMessageMap().getErrorMessagesForProperty(ex.getTabErrorKey())==null){
                    getGlobalVariableService().getMessageMap().putError(ex.getTabErrorKey(), ex.getErrorKey(),ex.getParams());
                }
            }else{
                getGlobalVariableService().getMessageMap().putError(Constants.NO_FIELD, ex.getErrorKey(),ex.getParams());
            }
        }

       return super.save(form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=viewUserAttachedFormXML"})
    public ModelAndView viewUserAttachedFormXML( ProposalDevelopmentDocumentForm form, HttpServletResponse response,
        @RequestParam("selectedLine") String selectedLine) throws Exception {

        DevelopmentProposal developmentProposal = form.getDevelopmentProposal();
        List<S2sUserAttachedForm> s2sAttachedForms = developmentProposal.getS2sUserAttachedForms();
        S2sUserAttachedForm selectedForm = s2sAttachedForms.get(Integer.parseInt(selectedLine));

        S2sUserAttachedFormFileContract userAttachedFormFile = getUserAttachedFormService().findUserAttachedFormFile(selectedForm);
        if(userAttachedFormFile!=null){
            ControllerFileUtils.streamToResponse(userAttachedFormFile.getXmlFile().getBytes(), selectedForm.getFormName()+".xml", CONTENT_TYPE_XML, response);
        }else{
            return getModelAndViewService().getModelAndView(form);
        }
        return null;
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=viewUserAttachedFormPDF"})
    public ModelAndView viewUserAttachedFormPDF( ProposalDevelopmentDocumentForm form, HttpServletResponse response,
                                                 @RequestParam("selectedLine") String selectedLine) throws Exception {
        DevelopmentProposal developmentProposal = form.getDevelopmentProposal();
        List<S2sUserAttachedForm> s2sAttachedForms = developmentProposal.getS2sUserAttachedForms();
        S2sUserAttachedForm selectedForm = s2sAttachedForms.get(Integer.parseInt(selectedLine));
        S2sUserAttachedFormFileContract userAttachedFormFile = getUserAttachedFormService().findUserAttachedFormFile(selectedForm);
        if(userAttachedFormFile!=null){
            ControllerFileUtils.streamToResponse(userAttachedFormFile.getFormFile(), selectedForm.getFormFileName(), CONTENT_TYPE_PDF, response);
        }else{
            return getModelAndViewService().getModelAndView(form);
        }
        return null;
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=deleteUserAttachedForm"})
    public ModelAndView deleteUserAttachedForm( ProposalDevelopmentDocumentForm form, HttpServletResponse response,
                                                 @RequestParam("selectedLine") String selectedLine) {
        S2sUserAttachedForm deleteForm = form.getDevelopmentProposal().getS2sUserAttachedForms().remove(Integer.parseInt(selectedLine));
        getDataObjectService().delete(deleteForm);
        getS2sUserAttachedFormService().resetFormAvailability(form.getProposalDevelopmentDocument(), deleteForm.getNamespace());
        return super.save(form);
    }


    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=refreshSubmissionDetails"})
    public ModelAndView refreshSubmissionDetails( ProposalDevelopmentDocumentForm form) {
        ProposalDevelopmentDocument document = form.getProposalDevelopmentDocument();
        try{
            getS2sSubmissionService().refreshGrantsGov(document);
        }catch(S2sCommunicationException ex){
            LOG.error(ex.getMessage(),ex);
            getGlobalVariableService().getMessageMap().putError(Constants.NO_FIELD, ex.getErrorKey(),ex.getMessage());
        }
        return getRefreshControllerService().refresh(form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=saveUserAttachedForm")
    public ModelAndView saveUserAttachedForm(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        final String selectedCollectionPath = form.getActionParamaterValue(UifParameters.SELECTED_COLLECTION_PATH);
        String selectedLine = form.getActionParamaterValue(UifParameters.SELECTED_LINE_INDEX);

        if(form.getEditableCollectionLines().containsKey(selectedCollectionPath)){
            form.getEditableCollectionLines().get(selectedCollectionPath).remove(selectedLine);
        }

        return super.save(form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=addNewS2sOverride"})
    public ModelAndView addNewS2sOverride(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        final ProposalDevelopmentDocument document = form.getProposalDevelopmentDocument();

        final S2sOverride s2sOverride = form.getNewS2sOverride();
        s2sOverride.setDevelopmentProposal(document.getDevelopmentProposal());
        s2sOverride.getDevelopmentProposal().setS2sOverride(s2sOverride);
        form.setNewS2sOverride(new S2sOverride());

        if (document.getDevelopmentProposal().getS2sOpportunity() != null) {
            try {
                final FormGenerationResult result = generateWithoutOverride(document);
                if (result.isValid()) {
                    syncGenerationResultToOverride(result, document.getDevelopmentProposal().getS2sOverride());
                }
            } catch (S2SException e) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Error creating submission information for override", e);
                }
            }
        }

        return super.save(form);
    }

    /**
     * When calling the generateAndValidateForms we need to temporarily disable the S2S Override so that the generation will not use it to create
     * the FormGenerationResult
     */
    private FormGenerationResult generateWithoutOverride(ProposalDevelopmentDocument document) {
        final boolean requiresApplOverrideDisable = document.getDevelopmentProposal().getS2sOverride() != null && document.getDevelopmentProposal().getS2sOverride().getApplicationOverride() != null &&
                document.getDevelopmentProposal().getS2sOverride().getApplicationOverride().getApplication() != null && document.getDevelopmentProposal().getS2sOverride().isActive();
        if (requiresApplOverrideDisable) {
            final String application = document.getDevelopmentProposal().getS2sOverride().getApplicationOverride().getApplication();
            try {
                document.getDevelopmentProposal().getS2sOverride().getApplicationOverride().setApplication(null);
                return getFormGeneratorService().generateAndValidateForms(document);
            } finally {
                document.getDevelopmentProposal().getS2sOverride().getApplicationOverride().setApplication(application);
            }
        } else {
            return getFormGeneratorService().generateAndValidateForms(document);
        }
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=removeExistingS2sOverride"})
    public ModelAndView removeExistingS2sOverride(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        //have to do explicit delete or the database record doesn't actually get deleted.
        getDataObjectService().delete(form.getDevelopmentProposal().getS2sOverride());
        form.getDevelopmentProposal().setS2sOverride(null);
        return super.save(form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=syncCurrentGrantInformation")
    public ModelAndView syncCurrentGrantInformation(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {

        final ProposalDevelopmentDocument document = form.getProposalDevelopmentDocument();
        if (document.getDevelopmentProposal().getS2sOpportunity() != null) {
            try {
                final FormGenerationResult result = generateWithoutOverride(document);
                if (result.isValid()) {
                    syncGenerationResultToOverride(result, document.getDevelopmentProposal().getS2sOverride());
                }

                setValidationErrorMessage(result.getErrors());
                if (copyAuditErrorsToPage()) {
                    getGlobalVariableService().getMessageMap().putError(GRANTS_GOV_FORM_VALIDATION_ERRORS, KeyConstants.VALIDATTION_ERRORS_BEFORE_GRANTS_GOV_SUBMISSION);
                }
            } catch (S2SException e) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Error creating submission information for override", e);
                }
                getGlobalVariableService().getMessageMap().putError(Constants.NO_FIELD, Constants.GRANTS_GOV_GENERIC_ERROR_KEY, StringUtils.isNotBlank(e.getErrorMessage()) ? e.getErrorMessage() : e.getMessage());
            }
        }

        return getModelAndViewService().getModelAndView(form);
    }

    protected void syncGenerationResultToOverride(FormGenerationResult result, S2sOverride s2sOverride) {

        if (s2sOverride.getApplication() == null) {
            s2sOverride.setApplication(new S2sOverrideApplicationData());
        }

        final S2sOverrideApplicationData data = s2sOverride.getApplication();

        data.setApplication(result.getApplicationXml());
        data.setName(CURRENT_GRANT_APPLICATION_XML);
        data.setAttachments(result.getAttachments()
                .stream()
                .map(attachment -> {
                    final S2sOverrideAttachment s2sOverrideAttachment = new S2sOverrideAttachment();
                    s2sOverrideAttachment.setApplication(data);
                    s2sOverrideAttachment.setContentId(attachment.getContentId());
                    s2sOverrideAttachment.setName(attachment.getFileName());
                    s2sOverrideAttachment.setData(attachment.getContent());
                    s2sOverrideAttachment.setContentType(attachment.getContentType());
                    return s2sOverrideAttachment;
                }).collect(Collectors.toList()));
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=pushCurrentToOverride")
    public ModelAndView pushCurrentToOverride(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        final S2sOverride s2sOverride = form.getDevelopmentProposal().getS2sOverride();
        if (s2sOverride != null && s2sOverride.getApplication() != null && s2sOverride.getApplication().getApplication() != null) {
            final S2sOverrideApplicationData current = s2sOverride.getApplication();
            if (s2sOverride.getApplicationOverride() == null) {
                s2sOverride.setApplicationOverride(new S2sOverrideApplicationData());
            }

            final S2sOverrideApplicationData override = s2sOverride.getApplicationOverride();
            override.setApplication(current.getApplication());
            override.setName(OVERRRIDDEN_GRANT_APPLICATION_XML);
            override.setAttachments(current.getAttachments()
                    .stream()
                    .map(currentAttachment -> {
                        final S2sOverrideAttachment s2sOverrideAttachment = new S2sOverrideAttachment();
                        s2sOverrideAttachment.setApplication(override);
                        s2sOverrideAttachment.setContentId(currentAttachment.getContentId());
                        s2sOverrideAttachment.setName(currentAttachment.getName());
                        s2sOverrideAttachment.setData(currentAttachment.getData());
                        s2sOverrideAttachment.setContentType(currentAttachment.getContentType());
                        return s2sOverrideAttachment;
                    }).collect(Collectors.toList()));
        }
        return getModelAndViewService().getModelAndView(form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=retrieveApplicationXml")
    public ModelAndView retrieveApplicationXml(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, HttpServletResponse response) throws IOException {
        final HttpServletRequest request = form.getRequest();
        final String attachmentPath = request.getParameter("propertyPath");

        if (StringUtils.isBlank(attachmentPath)) {
            throw new RuntimeException("Selected attachment was not set for line action");
        }

        final KcFile attachment = ObjectPropertyUtils.getPropertyValue(form, attachmentPath);
        final byte[] data = attachment.getData();
        KRADUtils.addAttachmentToResponse(response, new ByteArrayInputStream(data), attachment.getType(), attachment.getName(), data.length);
        response.flushBuffer();
        return null;
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=save", "pageId=PropDev-OpportunityPage"})
    public ModelAndView saveOpportunityPage(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) throws IOException {
        setApplicationOverride(form.getDevelopmentProposal().getS2sOverride());
        return super.save(form);
    }

    protected void setApplicationOverride(S2sOverride s2sOverride) throws IOException {
        if (s2sOverride != null && s2sOverride.getApplicationOverride() != null && s2sOverride.getApplicationOverride().getMultipartFile() != null) {
            final S2sOverrideApplicationData applicationOverride = s2sOverride.getApplicationOverride();
            final MultipartFile file = applicationOverride.getMultipartFile();
            final byte[] content = file.getBytes();
            if (ArrayUtils.isNotEmpty(content)) {
                applicationOverride.setApplication(new String(content));
                applicationOverride.setName(file.getOriginalFilename());
            }
        }
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=replaceS2sOverrideApplication")
    public ModelAndView replaceS2sOverrideApplication(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) throws IOException {
        setApplicationOverride(form.getDevelopmentProposal().getS2sOverride());
        return super.save(form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=updateHashS2sOverrideApplication")
    public ModelAndView updateHashS2sOverrideApplication(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        updateHashApp(form);
        return super.save(form);
    }

    private void updateHashApp(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        final S2sOverride s2sOverride = form.getDevelopmentProposal().getS2sOverride();
        if (s2sOverride != null && s2sOverride.getApplicationOverride() != null) {
            final boolean updated = s2sOverride.getApplicationOverride().updateSha1HashInXml();

            if (updated) {
                getGlobalVariableService().getMessageMap().putInfoForSectionId("PropDev-OpportunityPage-Override-Override", KeyConstants.S2S_OVERRIDDE_UPDATE_APP_HASH);
            }
        }
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=updateHashFileUploadLine")
    public ModelAndView updateHashFileUploadLine(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form)  {
        final CollectionControllerServiceImpl.CollectionActionParameters parameters = new CollectionControllerServiceImpl.CollectionActionParameters(form, true);

        final Runnable runnable = () -> processCollectionUpdateHashLine(form, parameters.getSelectedCollectionId(),
                parameters.getSelectedCollectionPath(), parameters.getSelectedLineIndex());

        ViewLifecycle.encapsulateLifecycle(form.getView(), form, form.getViewPostMetadata(), null, form.getRequest(), runnable);
        return super.save(form);
    }

    private void processCollectionUpdateHashLine(ProposalDevelopmentDocumentForm form, String collectionId, String collectionPath, int lineIndex) {
        final Collection<S2sOverrideAttachment> collection = ObjectPropertyUtils.getPropertyValue(form, collectionPath);
        if (collection == null) {
            throw new RuntimeException("Unable to get collection property from model for path: " + collectionPath);
        }

        if (collection instanceof List) {
            final S2sOverrideAttachment attachment = ((List<S2sOverrideAttachment>) collection).get(lineIndex);
            if (attachment != null) {
                final boolean updated = attachment.updateSha1HashInXml();
                if (updated) {
                    final String collectionLabel = (String) form.getViewPostMetadata().getComponentPostData(collectionId, UifConstants.PostMetadata.COLL_LABEL);
                    getGlobalVariableService().getMessageMap().putInfoForSectionId(collectionId, KeyConstants.S2S_OVERRIDDE_UPDATE_ATT_HASH, collectionLabel);
                    //if We could figure out how to refresh the application hash fields on the UI, if w3ould make sense to call updateHashApp() here.
                }
            }
        } else {
            throw new RuntimeException("Only List collection implementations are supported for the update hash by index method");
        }
    }

    public S2sSubmissionService getS2sSubmissionService() {
        return s2sSubmissionService;
    }

    public void setS2sSubmissionService(S2sSubmissionService s2sSubmissionService) {
        this.s2sSubmissionService = s2sSubmissionService;
    }

    public FormPrintService getFormPrintService() {
        return formPrintService;
    }

    public void setFormPrintService(FormPrintService formPrintService) {
        this.formPrintService = formPrintService;
    }

    public S2sUserAttachedFormService getS2sUserAttachedFormService() {
        return s2sUserAttachedFormService;
    }

    public void setS2sUserAttachedFormService(S2sUserAttachedFormService s2sUserAttachedFormService) {
        this.s2sUserAttachedFormService = s2sUserAttachedFormService;
    }

    public UserAttachedFormService getUserAttachedFormService() {
        return userAttachedFormService;
    }

    public void setUserAttachedFormService(UserAttachedFormService userAttachedFormService) {
        this.userAttachedFormService = userAttachedFormService;
    }

    public ProposalDevelopmentDocumentViewAuthorizer getProposalDevelopmentDocumentViewAuthorizer() {
        return proposalDevelopmentDocumentViewAuthorizer;
    }

    public void setProposalDevelopmentDocumentViewAuthorizer(ProposalDevelopmentDocumentViewAuthorizer proposalDevelopmentDocumentViewAuthorizer) {
        this.proposalDevelopmentDocumentViewAuthorizer = proposalDevelopmentDocumentViewAuthorizer;
    }

    public FormGeneratorService getFormGeneratorService() {
        return formGeneratorService;
    }

    public void setFormGeneratorService(FormGeneratorService formGeneratorService) {
        this.formGeneratorService = formGeneratorService;
    }

    public RefreshControllerService getRefreshControllerService() {
        return refreshControllerService;
    }

    public void setRefreshControllerService(RefreshControllerService refreshControllerService) {
        this.refreshControllerService = refreshControllerService;
    }
}

