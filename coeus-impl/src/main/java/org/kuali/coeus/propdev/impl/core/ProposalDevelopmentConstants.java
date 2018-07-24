/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.core;

public class ProposalDevelopmentConstants {

    public static class KradConstants {
		public static final String BUDGET_PAGE = "PropDev-BudgetPage";
        public static final String SUBMIT_PAGE = "PropDev-SubmitPage";
		public static final String PREVIOUS_PAGE_ARG = "previous";
		public static final String NEXT_PAGE_ARG = "next";
        public static final String DELIVERY_INFO_PAGE = "PropDev-DeliveryInfoPage";
        public static final String COMPLIANCE_ADD_DIALOG = "PropDev-CompliancePage_AddDialog";

        public static final String PERSONNEL_QUESTIONNAIRE = "personnelQuestionnaire";
        public static final String PROP_DEV_ATTACHMENTS_PAGE_PROPOSAL_DETAILS = "PropDev-AttachmentsPage-ProposalDetails";
        public static final String PROP_DEV_ATTACHMENTS_PAGE_PERSONNEL_DETAILS = "PropDev-AttachmentsPage-PersonnelDetails";
        public static final String PROP_DEV_ATTACHMENTS_PAGE_ABSTRACT_DETAILS = "PropDev-AttachmentsPage-AbstractDetails";
        public static final String PROP_DEV_ATTACHMENTS_PAGE_INTERNAL_DETAILS = "PropDev-AttachmentsPage-InternalDetails";
        public static final String PROP_DEV_ATTACHMENTS_PAGE_NOTE_DETAILS = "PropDev-AttachmentsPage-NoteDetails";
        public static final String PROP_DEV_ATTACHMENT_PAGE_VIEW_EDIT_RIGHT_DIALOG = "PropDev-AttachmentPage-ViewEditRightDialog";
        public static final String PROP_DEV_ATTACHMENT_PAGE_CONFIRM_ADD_ATTACHMENT_TO_CHILD = "PropDev-AttachmentPage-ConfirmAddAttachmentToChild";
        public static final String PROP_PER_DOC_TYPE = "propPerDocType";
        public static final String NARRATIVE_TYPE = "narrativeType";
        public static final String NARRATIVE_STATUS = "narrativeStatus";
        public static final String KC_SEND_NOTIFICATION_WIZARD = "Kc-SendNotification-Wizard";
        public static final String ATTACHMENT_TYPE = "attachmentType";
        public static final String PROPOSAL_ATTACHMENT = "proposalAttachment";
        public static final String INSTITUTE_ATTACHMENT = "instituteAttachment";
        public static final String BINDING_PATH = "bindingPath";
        public static final String PROPOSAL_DEVELOPMENT_ATTACHMENT_HELPER_NARRATIVE = "proposalDevelopmentAttachmentHelper.narrative";
        public static final String PROPOSAL_DEVELOPMENT_ATTACHMENT_HELPER_BIOGRAPHY = "proposalDevelopmentAttachmentHelper.biography";
        public static final String PROPOSAL_DEVELOPMENT_ATTACHMENT_HELPER = "proposalDevelopmentAttachmentHelper";
        public static final String DATA_VALIDATION_DIALOG_ID = "DataValidationSection";
        public static final String DATA_VALIDATION_SECTION_WITH_SUBMIT = "DataValidationSection-WithSubmit";
        public static final String DATA_VALIDATION_SECTION_WITH_APPROVE = "DataValidationSection-WithApprove";
        public static final String PROP_DEV_INITIATE_VIEW = "PropDev-InitiateView";
        public static final String PROP_DEV_CLOSE_DIALOG = "PropDev-Close-Dialog";
        public static final String PROP_DEV_CERT_CLOSE_DIALOG = "PropDev-CertificationClose-Dialog";
        public static final String PROP_DEV_PERSONNEL_PAGE_UPDATE_CERTIFICATION_DIALOG = "PropDev-PersonnelPage-UpdateCertification-Dialog";
        public static final String PROP_DEV_PERSONNEL_PAGE_VERIFY_NOTIFICATION_DIALOG = "PropDev-PersonnelPage-VerifyNotification-Dialog";
        public static final String PROP_DEV_PERSONNEL_PAGE_COLLECTION = "PropDev-PersonnelPage-Collection";
        public static final String PROP_DEV_PERSONNEL_PAGE_WIZARD = "PropDev-PersonnelPage-Wizard";
        public static final String PAGE_IS_DIRTY = "pageIsDirty";
    }

    public static class S2sConstants {

        public static final String OPPORTUNITY_ID = "opportunityId";
        public static final String COMPETITION_ID = "competitionId";
        public static final String COMPETITION_TITLE = "competitionTitle";
        public static final String PACKAGE_ID = "packageId";
        public static final String CFDA_NUMBER = "cfdaNumber";
        public static final String CLOSING_DATE = "closingDate";
        public static final String OPENING_DATE = "openingDate";
        public static final String OPPORTUNITY_TITLE = "opportunityTitle";
        public static final String SCHEMA_URL = "schemaUrl";
        public static final String INSTRUCTION_URL = "instructionUrl";
        public static final String S2S_OPPORTUNITY_CFDAS_SERIALIZED = "s2sOpportunityCfdasSerialized";
        public static final String PROVIDER_CODE = "providerCode" ;
        public static final int OPP_TITLE_MAX_LENGTH = 119;
    }

    public static class Parameters {
        public static final String KEY_PERSON_CERTIFICATION_SELF_CERTIFY_ONLY = "KEY_PERSON_CERTIFICATION_SELF_CERTIFY_ONLY";
        public static final String NOTIFY_ALL_CERTIFICATIONS_COMPLETE = "notifyAggregatorWhenAllCertificationsComplete";
        public static final String KEY_PERSON_PROJECT_ROLE = "keyPersonProjectRole";
        public static final String ALPHABETIZE_ATTACHMENT_TYPES = "alphabetizeAttachmentTypes";
    }

    public static class ParameterValues {
        public static final String KEY_PERSON_CERTIFICATION_BEFORE_SUBMIT = "BS";
        public static final String KEY_PERSON_CERTIFICATION_BEFORE_APPROVE = "BA";
    }

    public static class PropertyConstants {
        public static final String DOCUMENT_DEVELOPMENT_PROPOSAL = "document.developmentProposal";
        public static final String PERSONNEL_BIOS = DOCUMENT_DEVELOPMENT_PROPOSAL + ".propPersonBios";
        public static final String NARRATIVES = DOCUMENT_DEVELOPMENT_PROPOSAL + ".narratives";
        public static final String INSTITUTE_ATTACHMENTS = DOCUMENT_DEVELOPMENT_PROPOSAL + ".instituteAttachments";
        public static final String PROPOSAL_PERSONS = DOCUMENT_DEVELOPMENT_PROPOSAL + ".proposalPersons";
    }
    
    public static class NotificationConstants {
    	public static final String NOTIFICATION_STEP_0 = "0";
    	public static final String NOTIFICATION_STEP_2 = "2";
    	public static final String NOTIFICATION_S2S_SUBMIT_ACTION_CODE = "101";
    	public static final String NOTIFICATION_S2S_SUBMIT_CONTEXT_NAME =  "Proposal Submitted";
    }
    
    public static class ResubmissionOptions {
    	public static final String GENERATE_NEW_IP = "N";
    	public static final String GENERATE_NEW_VERSION_OF_IP = "A";
    	public static final String GENERATE_NEW_VERSION_OF_ORIGINAL_IP = "O";
    	public static final String DO_NOT_GENERATE_NEW_IP = "X";
    }
    
    public static class PropDevDocumentActions {
		public static final String SUBMIT_TO_S2S = "submitToS2S";
		public static final String SUBMIT_TO_SPONSOR = "submitToSponsor";
        public static final String NOTIFY_PROPOSAL_PERSONS = "NOTIFY_PROPOSAL_PERSONS";
    }
    
    public static class PropDevParameterConstants {
        public static final String CHANGE_CORRECTED_CODE = "S2S_SUBMISSION_TYPE_CODE_CHANGE_CORRECTED_APPLICATION";
        public static final String S2S_SUBMISSION_TYPE_CODE_PREAPPLICATION = "S2S_SUBMISSION_TYPE_CODE_PREAPPLICATION";
        public static final String PROPOSAL_TYPE_CODE_NEW_PARM = "PROPOSAL_TYPE_CODE_NEW";
        public static final String PROPOSAL_TYPE_CODE_RESUBMISSION_PARM = "PROPOSAL_TYPE_CODE_RESUBMISSION";
        public static final String PROPOSAL_TYPE_CODE_RENEWAL_PARM = "PROPOSAL_TYPE_CODE_RENEWAL";
        public static final String PROPOSAL_TYPE_CODE_CONTINUATION_PARM = "PROPOSAL_TYPE_CODE_CONTINUATION";
        public static final String PROPOSAL_TYPE_CODE_REVISION_PARM = "PROPOSAL_TYPE_CODE_REVISION";
        public static final String PROPOSAL_TYPE_CODE_TASK_ORDER_PARM = "PROPOSAL_TYPE_CODE_TASK_ORDER";
        public static final String PROPOSAL_TYPE_CODE_NEW_CHANGE_CORRECTED_PARM = "PROPOSAL_TYPE_CODE_NEW_CHANGE_CORRECTED";
        public static final String PROPOSAL_TYPE_CODE_RESUBMISSION_CHANGE_CORRECTED_PARM = "PROPOSAL_TYPE_CODE_RESUBMISSION_CHANGE_CORRECTED";
        public static final String PROPOSAL_TYPE_CODE_RENEWAL_CHANGE_CORRECTED_PARM = "PROPOSAL_TYPE_CODE_RENEWAL_CHANGE_CORRECTED";
        public static final String PROPOSAL_TYPE_CODE_SUPPLEMENT_CHANGE_CORRECTED_PARM = "PROPOSAL_TYPE_CODE_SUPPLEMENT_CHANGE_CORRECTED";
        public static final String PROPOSAL_TYPE_CODE_BUDGET_SOW_UPDATE_PARM = "PROPOSAL_TYPE_CODE_BUDGET_SOW_UPDATE";
        public static final String PROPOSAL_TYPE_CODE_PRE_PROPOSAL_PARM = "PROPOSAL_TYPE_CODE_PRE_PROPOSAL";
        public static final String ENABLE_KEY_PERSON_VALIDATION_FOR_NON_EMPLOYEE_PERSONNEL = "ENABLE_KEY_PERSON_VALIDATION_FOR_NON_EMPLOYEE_PERSONNEL";
        public static final String ENABLE_PD_WORKFLOW_DISAPPROVE = "ENABLE_PD_WORKFLOW_DISAPPROVE";
    }
    
    public static class AuthConstants {
		public static final String MODIFY_PROPOSAL_EDIT_MODE = "modifyProposal";
        public static final String MODIFY_BUDGET_EDIT_MODE = "modifyBudget";
		public static final String VIEW_ONLY_PROPOSAL_EDIT_MODE = "viewProposal";
        public static final String MAINTAIN_HIERARCHY_EDIT_MODE = "maintainProposalHierarchy";
        public static final String CAN_SAVE_CERTIFICATION = "canSaveCertification";
        public static final String MODIFY_S2S = "modifyS2s";
        public static final String MODIFY_S2S_OVERRIDE = "modifyS2sOverride";
        public static final String ALTER_PROPOSAL_DATA = "alterProposalData";
        public static final String SHOW_ALTER_PROPOSAL_DATA = "showAlterProposalData";
        public static final String OVERRIDE_PD_COMPLIANCE_ENTRY = "overridePDComplianceEntry";
    }

    public static class KewConstants {

        public static final String PROPOSAL_DEVELOPMENT_DOCUMENT = "ProposalDevelopmentDocument";
		public static final String AGGREGATORS_REQUEST_FOR_REVIEW_ANNOTATION = "Aggregators Request for Review";
        public static final String SUBMITTER_REQUEST_FOR_REVIEW_ANNOTATION = "Submitter Request for Review";
    }
    
    
}
