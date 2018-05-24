/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.s2s.connect.S2sCommunicationException;
import org.kuali.coeus.propdev.impl.s2s.nih.NihSubmissionValidationService;
import org.kuali.coeus.propdev.impl.s2s.nih.NihValidationMapping;
import org.kuali.coeus.propdev.impl.s2s.nih.NihValidationServiceUtils;
import org.kuali.coeus.propdev.impl.s2s.nih.ValidationMessageDto;
import org.kuali.coeus.s2sgen.api.core.S2SException;
import org.kuali.coeus.s2sgen.api.generate.AttachmentData;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.coeus.common.api.sponsor.hierarchy.SponsorHierarchyService;
import org.kuali.coeus.s2sgen.api.generate.FormGenerationResult;
import org.kuali.coeus.s2sgen.api.generate.FormGeneratorService;
import org.kuali.coeus.s2sgen.api.core.AuditError;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.AuditCluster;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.DocumentAuditRule;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.kuali.coeus.propdev.impl.datavalidation.ProposalDevelopmentDataValidationConstants.*;

public class ProposalDevelopmentGrantsGovAuditRule  implements DocumentAuditRule{

    private static final Log LOG = LogFactory.getLog(ProposalDevelopmentGrantsGovAuditRule.class);
    private static final String VALIDATION_SERVICE = "Validation Service";
    private static final String ERROR = "Error";
    private static final String WARNINGS = "Warnings";
    private static final String PAGE_SECTION_DELIMETER = ".";
    private static final String ERROR_CODE = "E";
    private static final String SCHEMA_ERROR_RULE_NUMBER = "000.6";
    private static final String INFO_NIH_VALIDATION_SERVICE_IGNORED = "info.nih.validation.service.ignored";
    public static final String RULE_NUMBER = "ruleNumber";
    public static final String ACTIVE = "active";

    private ParameterService parameterService;
    private GlobalVariableService globalVariableService;
    private BusinessObjectService businessObjectService;

    protected ParameterService getParameterService() {
        if (this.parameterService == null) {
            this.parameterService = KcServiceLocator.getService(ParameterService.class);
        }
        return this.parameterService;
    }

    protected GlobalVariableService getGlobalVariableService() {
        if (this.globalVariableService == null) {
            this.globalVariableService = KcServiceLocator.getService(GlobalVariableService.class);
        }
        return this.globalVariableService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        if (this.businessObjectService == null) {
            this.businessObjectService = KcServiceLocator.getService(BusinessObjectService.class);
        }
        return this.businessObjectService;
    }

    @Override
    public boolean processRunAuditBusinessRules(Document document) {
        boolean valid = true;

        ProposalDevelopmentDocument proposalDevelopmentDocument = (ProposalDevelopmentDocument)document;
		if (proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity() != null && (proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity().getS2sSubmissionTypeCode() == null || StringUtils.equalsIgnoreCase(proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity().getS2sSubmissionTypeCode(), ""))) {
            valid = false;
            getAuditErrors(Constants.S2S_PAGE_NAME, Constants.S2S_OPPORTUNITY_SECTION_NAME,"", ERROR).add(new org.kuali.rice.krad.util.AuditError(S2S_SUBMISSIONTYPE_CODE_KEY, KeyConstants.ERROR_NOT_SELECTED_SUBMISSION_TYPE, Constants.S2S_PAGE_ID+ PAGE_SECTION_DELIMETER + Constants.S2S_OPPORTUNITY_SECTION_ID));
        }
        
        if (proposalDevelopmentDocument.getDevelopmentProposal().getProposalTypeCode() != null && proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity() != null && proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity().getOpportunityId() != null && proposalDevelopmentDocument.getDevelopmentProposal().getProposalTypeCode().equals(getParameterService().getParameterValueAsString(ProposalDevelopmentDocument.class, KeyConstants.PROPOSALDEVELOPMENT_PROPOSALTYPE_REVISION)) && proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity().getRevisionCode() == null) {
            valid &= false;
            getAuditErrors(Constants.S2S_PAGE_NAME, Constants.S2S_OPPORTUNITY_SECTION_NAME,"", ERROR).add(new org.kuali.rice.krad.util.AuditError(REVISION_CODE_KEY, KeyConstants.ERROR_IF_PROPOSALTYPE_IS_REVISION, Constants.S2S_PAGE_ID+ PAGE_SECTION_DELIMETER + Constants.S2S_OPPORTUNITY_SECTION_ID));
        }
        if((getSponsorHierarchyService().isSponsorNihOsc(proposalDevelopmentDocument.getDevelopmentProposal().getSponsorCode())||
                    getSponsorHierarchyService().isSponsorNihMultiplePi(proposalDevelopmentDocument.getDevelopmentProposal().getSponsorCode()))&&
                    proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity()!=null &&
                    proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity().getCompetetionId()!=null &&
                    proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity().getCompetetionId().equals("ADOBE-FORMS-A")){
        	getAuditErrors(Constants.S2S_PAGE_NAME, Constants.S2S_OPPORTUNITY_SECTION_NAME,"", ERROR).add(new org.kuali.rice.krad.util.AuditError(COMPETITION_ID, KeyConstants.ERROR_IF_COMPETITION_ID_IS_INVALID, Constants.S2S_PAGE_ID+ PAGE_SECTION_DELIMETER + Constants.S2S_OPPORTUNITY_SECTION_ID));
        	valid= false;
        }

        if (proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity() != null){
            final String provider = proposalDevelopmentDocument.getDevelopmentProposal().getS2sOpportunity().getS2sProvider().getDescription();
		    try {
                FormGenerationResult result = getS2sValidatorService().generateAndValidateForms(proposalDevelopmentDocument);
                valid &= result.isValid();
                //the NIH Validation Service detects multiple submissions, so calling this after a S2S Submission will cause an error.
                if (result.isValid() && CollectionUtils.isEmpty(proposalDevelopmentDocument.getDevelopmentProposal().getS2sAppSubmission())) {
                    valid &= nihValidation(proposalDevelopmentDocument.getDevelopmentProposal().getApplicantOrganization().getOrganization().getDunsNumber(), result.getApplicationXml(), result.getAttachments());
                }
                setValidationErrorMessage(result.getErrors(), provider);
            } catch (S2SException e) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Error validating submission information", e);
                }
                getAuditErrors(Constants.S2S_PAGE_NAME, Constants.S2S_OPPORTUNITY_SECTION_NAME, provider, ERROR).add(new org.kuali.rice.krad.util.AuditError(Constants.NO_FIELD, Constants.GRANTS_GOV_GENERIC_ERROR_KEY, Constants.S2S_PAGE_ID+ PAGE_SECTION_DELIMETER + Constants.S2S_OPPORTUNITY_SECTION_ID, new String[] { StringUtils.isNotBlank(e.getErrorMessage()) ? e.getErrorMessage() : e.getMessage() }));
                valid = false;
            }
        }

        return valid;
    }

    private boolean nihValidation(String dunsNumber, String applicationXml, List<AttachmentData> attachments) {
        boolean result;

        try {
            final List<ValidationMessageDto> response = getNihSubmissionValidationService().validateApplication(applicationXml, attachments, dunsNumber);
            result = response.isEmpty();

            final List<ValidationMessageDto> errors = response.stream()
                    .filter(msg -> ERROR_CODE.equals(msg.getValidationSeverityCode()))
                    .filter(msg -> !SCHEMA_ERROR_RULE_NUMBER.equals(msg.getValidationRuleNumber()))
                    .collect(Collectors.toList());

            final List<ValidationMessageDto> warnings = response.stream()
                    .filter(msg -> !ERROR_CODE.equals(msg.getValidationSeverityCode()))
                    .filter(msg -> !SCHEMA_ERROR_RULE_NUMBER.equals(msg.getValidationRuleNumber()))
                    .collect(Collectors.toList());

            final boolean schemaFailure = response.stream()
                    .anyMatch(msg -> SCHEMA_ERROR_RULE_NUMBER.equals(msg.getValidationRuleNumber()));

            convertToAuditErrors(errors, warnings);

            if (schemaFailure) {
                getGlobalVariableService().getMessageMap().putInfo(KRADConstants.GLOBAL_MESSAGES, INFO_NIH_VALIDATION_SERVICE_IGNORED);
            }

        } catch (S2sCommunicationException ex) {
            LOG.error("Error validating with nih.gov", ex);
            getAuditErrors(Constants.S2S_PAGE_NAME, Constants.S2S_OPPORTUNITY_SECTION_NAME,VALIDATION_SERVICE, ERROR)
                    .add(new org.kuali.rice.krad.util.AuditError(Constants.S2S_PAGE_ID, ex.getErrorKey(), StringUtils.EMPTY, ex.getMessageWithParams() ));
            result = false;
        }
        return result;
    }

    protected void convertToAuditErrors(List<ValidationMessageDto> errors, List<ValidationMessageDto> warnings) {
        sortMessages(errors).forEach(error -> addToAuditErrors(error, getNihValidationMappings(error), ERROR));

        sortMessages(warnings).forEach(warning -> {
            final List<NihValidationMapping> nihValidationMappings = getNihValidationMappings(warning);
            String errorType = nihValidationMappings.isEmpty() ? WARNINGS : nihValidationMappings.get(0).getForceError() ? ERROR : WARNINGS;
            addToAuditErrors(warning, nihValidationMappings, errorType);
        });
    }

    protected void addToAuditErrors(ValidationMessageDto error, List<NihValidationMapping> mapping, String errorType) {
        String pageId;
        String sectionId;
        if (mapping.isEmpty() || StringUtils.isEmpty(mapping.get(0).getPageId())) {
            pageId = Constants.S2S_PAGE_NAME;
            sectionId = Constants.S2S_OPPORTUNITY_SECTION_NAME;
        } else {
            pageId = mapping.get(0).getPageId();
            sectionId = mapping.get(0).getSectionId();
        }
        getAuditErrors(pageId, sectionId, VALIDATION_SERVICE, errorType).add(getCustomizedAuditError(error));
    }

    protected Stream<ValidationMessageDto> sortMessages(List<ValidationMessageDto> messages) {
        final Comparator<ValidationMessageDto> comparator = Comparator.comparing(ValidationMessageDto::getFormName)
                .thenComparing(ValidationMessageDto::getValidationRuleNumber)
                .thenComparingInt(ValidationMessageDto::getValidationMessageId)
                .thenComparing(ValidationMessageDto::getValidationMessageText);

        return messages.stream()
                .sorted(comparator);
    }

    protected org.kuali.rice.krad.util.AuditError getCustomizedAuditError(ValidationMessageDto msg) {
        return createAuditErrorBasedOnMapping(msg, getNihValidationMappings(msg));
    }

    protected List<NihValidationMapping> getNihValidationMappings(ValidationMessageDto msg) {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put(RULE_NUMBER, msg.getValidationRuleNumber());
        criteria.put(ACTIVE, Boolean.TRUE);
        return (List<NihValidationMapping>) getBusinessObjectService().findMatching(NihValidationMapping.class, criteria);
    }

    protected org.kuali.rice.krad.util.AuditError createAuditErrorBasedOnMapping(ValidationMessageDto msg, List<NihValidationMapping> matches) {
        if (matches.isEmpty()) {
            return new org.kuali.rice.krad.util.AuditError(
                    Constants.S2S_PAGE_ID, Constants.GRANTS_GOV_GENERIC_ERROR_KEY, StringUtils.EMPTY,
                    new String[]{NihValidationServiceUtils.toMessageString(msg)});
        } else {
            NihValidationMapping match = matches.get(0);
            if (!StringUtils.isEmpty(match.getCustomMessage())) {
                if (match.getAppendToOriginal()) {
                    msg.setValidationMessageText(msg.getValidationMessageText() + StringUtils.SPACE + match.getCustomMessage());
                } else {
                    msg.setValidationMessageText(match.getCustomMessage());
                }
            }
            return new org.kuali.rice.krad.util.AuditError(
                    StringUtils.isEmpty(match.getPageId()) ? Constants.S2S_PAGE_ID : match.getPageId(),
                    Constants.GRANTS_GOV_GENERIC_ERROR_KEY,
                    getAuditLink(matches.get(0)),
                    new String[]{NihValidationServiceUtils.toMessageString(msg)});
        }
    }

    protected List<org.kuali.rice.krad.util.AuditError> getAuditErrors(String areaName, String sectionName, String provider, String level) {
        String clusterKey = areaName + PAGE_SECTION_DELIMETER + sectionName + "." + level;
        final String s2sClusterKey = clusterKey + ".s2s";
        AuditCluster value = getGlobalVariableService().getAuditErrorMap().computeIfAbsent(s2sClusterKey, k ->
                new AuditCluster(clusterKey, new ArrayList<org.kuali.rice.krad.util.AuditError>(), provider + " " + level));
        if (value != null) {
            getGlobalVariableService().getAuditErrorMap().put(s2sClusterKey, value);
            return getGlobalVariableService().getAuditErrorMap().get(clusterKey+".s2s").getAuditErrorList();
        }
        return new ArrayList<>();
    }

    protected String getAuditLink(NihValidationMapping match) {
        return StringUtils.isEmpty(match.getPageId()) ? StringUtils.EMPTY :
                StringUtils.isEmpty(match.getSectionId()) ? match.getPageId() : match.getPageId() + "." + match.getSectionId();
    }

    protected void setValidationErrorMessage(List<AuditError> s2sErrors, String provider) {
        if (s2sErrors != null) {
            LOG.info("Error list size:" + s2sErrors.size() + s2sErrors.toString());

            for (AuditError error : s2sErrors) {
                if (StringUtils.equals(error.getLink(), ABSTRACTS_ATTACHMENTS)) {
                    getAuditErrors(ATTACHMENT_PAGE_NAME,ATTACHMENT_PROPOSAL_SECTION_NAME,provider, error.getLevel() == AuditError.Level.WARNING ? AUDIT_WARNINGS : AUDIT_ERRORS).add(new org.kuali.rice.krad.util.AuditError(NARRATIVES_KEY,
                            Constants.GRANTS_GOV_GENERIC_ERROR_KEY, ATTACHMENT_PAGE_ID + PAGE_SECTION_DELIMETER + ATTACHMENT_PROPOSAL_SECTION_ID,
                            new String[]{error.getMessageKey()}));
                } else if (StringUtils.equals(error.getLink(), QUESTIONS)) {
                    getAuditErrors(QUESTIONNAIRE_PAGE_NAME,NO_SECTION_ID,provider, error.getLevel() == AuditError.Level.WARNING ? AUDIT_WARNINGS : AUDIT_ERRORS).add(new org.kuali.rice.krad.util.AuditError(QUESTIONNAIRE_PAGE_ID,
                            Constants.GRANTS_GOV_GENERIC_ERROR_KEY, QUESTIONNAIRE_PAGE_ID,
                            new String[]{error.getMessageKey()}));
                } else if (StringUtils.equals(error.getLink(), KEY_PERSONNEL)) {
                    getAuditErrors(PERSONNEL_PAGE_NAME,NO_SECTION_ID,provider, error.getLevel() == AuditError.Level.WARNING ? AUDIT_WARNINGS : AUDIT_ERRORS).add(new org.kuali.rice.krad.util.AuditError(PERSONNEL_PAGE_ID,
                            Constants.GRANTS_GOV_GENERIC_ERROR_KEY, PERSONNEL_PAGE_ID,
                            new String[]{error.getMessageKey()}));
                } else if (StringUtils.equals(error.getLink(), PROPOSAL_ORGANIZATION_LOCATION)){
                    getAuditErrors(ORGANIZATION_PAGE_NAME,APPLICANT_ORGANIZATION_SECTION_NAME, provider, error.getLevel() == AuditError.Level.WARNING ? AUDIT_WARNINGS : AUDIT_ERRORS).add(new org.kuali.rice.krad.util.AuditError(ORGANIZATION_PAGE_ID,
                            Constants.GRANTS_GOV_GENERIC_ERROR_KEY, ORGANIZATION_PAGE_ID + PAGE_SECTION_DELIMETER + APPLICANT_ORGANIZATION_SECTION_ID,
                            new String[]{error.getMessageKey()}));
                } else if (StringUtils.equals(error.getLink(), PROPOSAL_SPONSOR_PROGRAM_INFO_LOCATION)) {
                    getAuditErrors(SPONSOR_PROGRAM_INFO_PAGE_NAME, NO_SECTION_ID, provider, error.getLevel() == AuditError.Level.WARNING ? AUDIT_WARNINGS : AUDIT_ERRORS).add(new org.kuali.rice.krad.util.AuditError(SPONSOR_PROGRAM_INFO_PAGE_ID,
                            Constants.GRANTS_GOV_GENERIC_ERROR_KEY, SPONSOR_PROGRAM_INFO_PAGE_ID,
                            new String[]{error.getMessageKey()}));
                } else {
                    getAuditErrors(Constants.S2S_PAGE_NAME, Constants.S2S_OPPORTUNITY_SECTION_NAME,provider, error.getLevel() == AuditError.Level.WARNING ? AUDIT_WARNINGS : AUDIT_ERRORS).add(new org.kuali.rice.krad.util.AuditError(Constants.S2S_PAGE_ID,
                            Constants.GRANTS_GOV_GENERIC_ERROR_KEY, Constants.S2S_PAGE_ID + PAGE_SECTION_DELIMETER + Constants.S2S_OPPORTUNITY_SECTION_ID,
                            new String[]{error.getMessageKey()}));
                }
            }
        }
    }

    private SponsorHierarchyService getSponsorHierarchyService() {
        return KcServiceLocator.getService(SponsorHierarchyService.class);
    }
    private FormGeneratorService getS2sValidatorService() {
        return KcServiceLocator.getService(FormGeneratorService.class);
    }

    private NihSubmissionValidationService getNihSubmissionValidationService() {
        return KcServiceLocator.getService(NihSubmissionValidationService.class);
    }
}
