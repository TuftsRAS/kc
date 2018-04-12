/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.person;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.coeus.common.framework.person.PropAwardPersonRole;
import org.kuali.coeus.propdev.impl.attachment.ProposalDevelopmentProposalAttachmentsAuditRule;
import org.kuali.coeus.propdev.impl.auth.perm.ProposalDevelopmentPermissionsService;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentConstants;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentUtils;
import org.kuali.coeus.propdev.impl.datavalidation.ProposalDevelopmentDataValidationConstants;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.rule.KcTransactionalDocumentRuleBase;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.coeus.propdev.impl.person.question.ProposalPersonModuleQuestionnaireBean;
import org.kuali.coeus.common.questionnaire.framework.answer.AnswerHeader;
import org.kuali.coeus.common.questionnaire.framework.answer.QuestionnaireAnswerService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.AuditCluster;
import org.kuali.rice.krad.util.AuditError;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.DocumentAuditRule;
import org.kuali.rice.krad.rules.rule.event.ApproveDocumentEvent;

import java.util.ArrayList;
import java.util.List;

import static org.kuali.coeus.propdev.impl.core.ProposalDevelopmentConstants.PropDevParameterConstants.ENABLE_KEY_PERSON_VALIDATION_FOR_NON_EMPLOYEE_PERSONNEL;
import static org.kuali.kra.infrastructure.KeyConstants.ERROR_PROPOSAL_PERSON_CERTIFICATION_INCOMPLETE;
import static org.kuali.kra.infrastructure.KeyConstants.ERROR_PROPOSAL_PERSON_NONEMPLOYEE_CERTIFICATION_INCOMPLETE;

public class KeyPersonnelCertificationRule extends KcTransactionalDocumentRuleBase implements DocumentAuditRule {

    public static final String KEY_PERSONNEL_AUDIT_CLUSTER_KEY = "keyPersonnelAuditErrors";
    
    private static final Log LOG = LogFactory.getLog(ProposalDevelopmentProposalAttachmentsAuditRule.class);
    private QuestionnaireAnswerService questionnaireAnswerService;
    private ProposalDevelopmentPermissionsService permissionsService;
    private ParameterService parameterService;
    private ProposalDevelopmentPermissionsService proposalDevelopmentPermissionsService;

    protected QuestionnaireAnswerService getQuestionnaireAnswerService(){
        if (questionnaireAnswerService == null)
            questionnaireAnswerService = KcServiceLocator.getService(QuestionnaireAnswerService.class);
        return questionnaireAnswerService;
    }
    
    @Override
    public boolean processRunAuditBusinessRules(Document document) {
        boolean valid = true;
        ProposalDevelopmentDocument pdDoc = (ProposalDevelopmentDocument) document;
        valid &= doesNonEmployeeHaveCertification(pdDoc.getDevelopmentProposal().getProposalPersons());
        if(getKeyPersonCertDeferralParm().equalsIgnoreCase(ProposalDevelopmentConstants.ParameterValues.KEY_PERSON_CERTIFICATION_BEFORE_SUBMIT)) {
            valid &= this.validateAllCertificationsComplete(pdDoc);
        }
        else if(getKeyPersonCertDeferralParm().equalsIgnoreCase(ProposalDevelopmentConstants.ParameterValues.KEY_PERSON_CERTIFICATION_BEFORE_APPROVE)) {
           valid &= isRouterPiAndCertified(pdDoc);
        }
        else {
            LOG.warn("System parameter 'KEY_PERSON_CERTIFICATION_DEFERRAL' should be one of 'BA' or 'BS'.");
            return false;
        }

        return valid;
    }

    public boolean doesNonEmployeeHaveCertification(List<ProposalPerson> proposalPersons) {
        final Boolean validationEnabled = getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, Constants.PARAMETER_COMPONENT_DOCUMENT,
                ENABLE_KEY_PERSON_VALIDATION_FOR_NON_EMPLOYEE_PERSONNEL);
        int index = 0;
        boolean valid = true;
        if (validationEnabled) {
            for (ProposalPerson person : proposalPersons) {
                if (doesNonEmployeeHaveCertification(person)) {
                    generateAuditError(index, person.getFullName(), ERROR_PROPOSAL_PERSON_NONEMPLOYEE_CERTIFICATION_INCOMPLETE);
                    valid = false;
                }
            }
            index++;
        }
        return valid;
    }

    public boolean doesNonEmployeeHaveCertification(ProposalPerson person) {
        return person.getPersonId() == null && getPermissionsService().doesPersonRequireCertification(person) && !validKeyPersonCertification(person);
    }

    protected ProposalDevelopmentPermissionsService getProposalDevelopmentPermissionsService(){
        if (proposalDevelopmentPermissionsService == null)
            proposalDevelopmentPermissionsService = KcServiceLocator.getService(ProposalDevelopmentPermissionsService.class);
        return proposalDevelopmentPermissionsService;
    }

    protected ParameterService getParameterService(){
        if (parameterService == null)
            parameterService = KcServiceLocator.getService(ParameterService.class);
        return parameterService;
    }

    public ProposalDevelopmentPermissionsService getPermissionsService() {
        if (permissionsService == null) {
            permissionsService = KcServiceLocator.getService(ProposalDevelopmentPermissionsService.class);
        }
        return permissionsService;
    }

    protected boolean isRouterPiAndCertified(ProposalDevelopmentDocument pdDoc) {
        String loggedInUser = getLoggedInUser();
        for (ProposalPerson person : pdDoc.getDevelopmentProposal().getProposalPersons()) {
            if (isPersonLoggedInPiOrCoi(loggedInUser, person)) {
                if (needsCertification(person) && !validKeyPersonCertification(person)) {
                    generateAuditError(0,person.getFullName(), ERROR_PROPOSAL_PERSON_CERTIFICATION_INCOMPLETE);
                    return false;
                }
            }
        }
        return true;
    }

    protected String getLoggedInUser() {
        return getGlobalVariableService().getUserSession().getPrincipalId();
    }

    private boolean isPersonLoggedInPiOrCoi(String loggedInUser, ProposalPerson person) {
        return StringUtils.equalsIgnoreCase(person.getPersonId(), loggedInUser) &&
                (person.isCoInvestigator() || person.isPrincipalInvestigator());
    }

    @Override
    public boolean processRouteDocument(Document document) {
        return processRunAuditBusinessRules(document);
    }

    @Override
    public boolean processApproveDocument(ApproveDocumentEvent approveEvent) {
        boolean isValid = true;

        if(getKeyPersonCertDeferralParm().equals(ProposalDevelopmentConstants.ParameterValues.KEY_PERSON_CERTIFICATION_BEFORE_APPROVE)) {
            //validation based on session user existing as key person and possibly aggregator.
            isValid &= this.validateKeyPersonCertification((ProposalDevelopmentDocument) approveEvent.getDocument(),
                    getGlobalVariableService().getUserSession().getPerson());
        }
        
        return isValid;
    }    
    
    protected boolean validateAllCertificationsComplete(ProposalDevelopmentDocument document) {
        boolean retval = true;
        int count = 0;
        for (ProposalPerson person : document.getDevelopmentProposal().getProposalPersons()) {
            if (needsCertification(person) && !validKeyPersonCertification(person)) {
                generateAuditError(count,person.getFullName(), ERROR_PROPOSAL_PERSON_CERTIFICATION_INCOMPLETE);
                retval = false;
            }
            count++;
        }
        return retval;
    }
    
    /*
     * validates specifically the key person certification, if any, of the proposal person matching the
     * person in the user session
     */
    protected boolean validateKeyPersonCertification(ProposalDevelopmentDocument document, Person user) {
        boolean retval = true;
        int count = 0;
        for (ProposalPerson person : document.getDevelopmentProposal().getProposalPersons()) {
            if(StringUtils.equals(user.getPrincipalId(),person.getPersonId())
                    && needsCertification(person) && !validKeyPersonCertification(person)) {
                generateAuditError(count,person.getFullName(), ERROR_PROPOSAL_PERSON_CERTIFICATION_INCOMPLETE);
                return false;
            }
            count++;
        }
        return retval;
    }
    
    protected boolean validKeyPersonCertification(ProposalPerson person) {
        return validateYesNoQuestions(person);
    }

    public boolean isRolodexCertificationEnabled(ProposalPerson person) {
        return person.getPersonId() == null ? getProposalDevelopmentPermissionsService().isRolodexCertificationEnabled() : Boolean.TRUE;
    }

    protected boolean needsCertification(ProposalPerson person) {
        //questionnaires should continue to be answerable only to the following approvers,
        //possibly as well as other roles. i.e. Aggregator.
        PropAwardPersonRole personRole = person.getRole();
        return (personRole.getCertificationRequired() && isRolodexCertificationEnabled(person) &&
                doesRoleRequireCertification(person, personRole));
    }

    protected boolean doesRoleRequireCertification(ProposalPerson person, PropAwardPersonRole personRole) {
        return personRole.getRoleCode().equals(Constants.CO_INVESTIGATOR_ROLE)
        || personRole.getRoleCode().equals(Constants.PRINCIPAL_INVESTIGATOR_ROLE)
        || (personRole.getRoleCode().equals(Constants.KEY_PERSON_ROLE) && person.getOptInCertificationStatus());
    }

    /**
     * Yes/No questions have to be submitted to Grants.gov on document route. If the submitter has not completed the certifications,
     * errors should be displayed in audit mode.
     * 
     * 
     * @param investigator Proposal Investigator
     * @return true if the given PI's Yes/No Questions are completed
     */
    protected boolean validateYesNoQuestions(ProposalPerson investigator) {
        boolean retval = true;
        
        ProposalPersonModuleQuestionnaireBean bean = new ProposalPersonModuleQuestionnaireBean(investigator.getDevelopmentProposal(), investigator);
        List<AnswerHeader> headers = getQuestionnaireAnswerService().getQuestionnaireAnswer(bean);
        
        for (AnswerHeader head : headers) {
            retval &= head.isCompleted();
        }
               
        return retval;
    }
    
    protected void generateAuditError(int count, String personFullName, String errorMessage) {
        final String errorStarter = "document.developmentProposal.proposalPersons[";
        final String errorFinish = "].questionnaireHelper.answerHeaders[0].questions";
        
        String errorKey = errorStarter + count + errorFinish;

        //Displays the error within the audit log.
        AuditError error = new AuditError(errorKey, errorMessage,
                ProposalDevelopmentDataValidationConstants.PERSONNEL_PAGE_ID, new String[]{personFullName});
        getAuditErrors().add(error);

    }

    /**
     * This method should only be called if an audit error is intending to be added because it will actually add a <code>{@link List&lt;AuditError&gt;}</code>
     * to the auditErrorMap.
     * 
     * @return List of AuditError instances
     */
    @SuppressWarnings("unchecked")
    protected List<AuditError> getAuditErrors() {
        List<AuditError> auditErrors = new ArrayList<AuditError>();
        
        if (!GlobalVariables.getAuditErrorMap().containsKey(KEY_PERSONNEL_AUDIT_CLUSTER_KEY)) {
            GlobalVariables.getAuditErrorMap().put(KEY_PERSONNEL_AUDIT_CLUSTER_KEY, 
                    new AuditCluster(Constants.KEY_PERSONNEL_PANEL_NAME, auditErrors, Constants.AUDIT_ERRORS));
        } else {
            auditErrors = ((AuditCluster) GlobalVariables.getAuditErrorMap().get(KEY_PERSONNEL_AUDIT_CLUSTER_KEY)).getAuditErrorList();
        }
        return auditErrors;
    }


    protected String getKeyPersonCertDeferralParm() {
        return ProposalDevelopmentUtils
                .getProposalDevelopmentDocumentParameter(ProposalDevelopmentUtils.KEY_PERSON_CERTIFICATION_DEFERRAL_PARM);
    }

    public GlobalVariableService getGlobalVariableService() {
        return KcServiceLocator.getService(GlobalVariableService.class);
    }
}
