/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.web.struts.form;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.coi.framework.DisclosureProjectStatus;
import org.kuali.coeus.coi.framework.DisclosureStatusRetrievalService;
import org.kuali.coeus.common.notification.impl.NotificationHelper;
import org.kuali.coeus.sys.framework.validation.Auditable;
import org.kuali.coeus.sys.framework.model.KcTransactionalDocumentFormBase;
import org.kuali.coeus.sys.framework.model.MultiLookupForm;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.authorization.KraAuthorizationConstants;
import org.kuali.kra.common.web.struts.form.ReportHelperBean;
import org.kuali.kra.common.web.struts.form.ReportHelperBeanContainer;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.institutionalproposal.attachments.InstitutionalProposalAttachmentFormBean;
import org.kuali.kra.institutionalproposal.contacts.InstitutionalProposalCentralAdminContactsBean;
import org.kuali.kra.institutionalproposal.contacts.InstitutionalProposalCreditSplitBean;
import org.kuali.kra.institutionalproposal.contacts.InstitutionalProposalProjectPersonnelBean;
import org.kuali.kra.institutionalproposal.contacts.InstitutionalProposalUnitContactsBean;
import org.kuali.kra.institutionalproposal.customdata.InstitutionalProposalCustomDataFormHelper;
import org.kuali.kra.institutionalproposal.document.InstitutionalProposalDocument;
import org.kuali.kra.institutionalproposal.home.*;
import org.kuali.kra.institutionalproposal.notification.InstitutionalProposalNotificationContext;
import org.kuali.kra.institutionalproposal.printing.service.InstitutionalProposalPersonService;
import org.kuali.kra.institutionalproposal.specialreview.SpecialReviewHelper;
import org.kuali.coeus.common.framework.medusa.MedusaBean;
import org.kuali.coeus.common.framework.custom.CustomDataDocumentForm;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.datadictionary.HeaderNavigation;
import org.kuali.rice.kns.util.ActionFormUtilMap;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class InstitutionalProposalForm extends KcTransactionalDocumentFormBase implements Auditable, MultiLookupForm, ReportHelperBeanContainer,CustomDataDocumentForm {


    private static final long serialVersionUID = 4564236415580911082L;
    private static final String CUSTOM_DATA_NAV_TO = "customData";
    public static final String INSTITUTIONAL_PROPOSAL_ATTACHMENTS_FLAG = "INSTITUTIONAL_PROPOSAL_ATTACHMENTS_FLAG";
    public static final String ATTACHMENTS = "attachments";

    private boolean auditActivated;
    
    private String lookupResultsSequenceNumber;
    private String lookupResultsBOClassName;
    
    private SpecialReviewHelper specialReviewHelper;
    private InstitutionalProposalCustomDataFormHelper institutionalProposalCustomDataFormHelper;
    private NotificationHelper<InstitutionalProposalNotificationContext> notificationHelper;
    private InstitutionalProposalNotepadBean institutionalProposalNotepadBean;
    private InstitutionalProposalCostShareBean institutionalProposalCostShareBean;
    private InstitutionalProposalUnrecoveredFandABean institutionalProposalUnrecoveredFandABean;
    private InstitutionalProposalFandABean institutionalProposalFandABean;
    private InstitutionalProposalProjectPersonnelBean projectPersonnelBean;
    private InstitutionalProposalCreditSplitBean institutionalProposalCreditSplitBean;
    private InstitutionalProposalUnitContactsBean unitContactsBean;
    private InstitutionalProposalCentralAdminContactsBean centralAdminContactsBean;
    private InstitutionalProposalAttachmentFormBean institutionalProposalAttachmentBean;
    private boolean cfdaLookupRequired;
    private MedusaBean medusaBean;
    private ReportHelperBean reportHelperBean;
    private InstitutionalProposalCfda newProposalCfda;
    
    /* Populated from Proposal Log lookup for Proposal creation */
    private String proposalNumber;
    
    private transient String[] selectedAwardFundingProposals;
    
    private boolean viewFundingSource;
    private boolean docOpenedFromIPSearch;

    private transient List<DisclosureProjectStatus> disclosureProjectStatuses;
    private transient DisclosureStatusRetrievalService disclosureStatusRetrievalService;
    private transient InstitutionalProposalPersonService institutionalProposalPersonService;

    public InstitutionalProposalForm() {
        super();
        initialize();
    }
    
    @Override
    protected String getDefaultDocumentTypeName() {
        return "InstitutionalProposalDocument";
    }
    
    /**
     * 
     * This method initialize all form variables
     */
    public void initialize() {
        specialReviewHelper = new SpecialReviewHelper(this);
        institutionalProposalCustomDataFormHelper = new InstitutionalProposalCustomDataFormHelper(this);
        notificationHelper = new NotificationHelper<InstitutionalProposalNotificationContext>();
        institutionalProposalNotepadBean = new InstitutionalProposalNotepadBean(this);
        institutionalProposalCostShareBean = new InstitutionalProposalCostShareBean(this);
        institutionalProposalUnrecoveredFandABean = new InstitutionalProposalUnrecoveredFandABean(this);
        institutionalProposalFandABean = new InstitutionalProposalFandABean(this);
        projectPersonnelBean = new InstitutionalProposalProjectPersonnelBean(this);
        institutionalProposalCreditSplitBean = new InstitutionalProposalCreditSplitBean(this);
        medusaBean = new MedusaBean();
        reportHelperBean = new ReportHelperBean(this);
        unitContactsBean = new InstitutionalProposalUnitContactsBean(this);
        centralAdminContactsBean = new InstitutionalProposalCentralAdminContactsBean(this);
        institutionalProposalAttachmentBean = new InstitutionalProposalAttachmentFormBean(this);
        docOpenedFromIPSearch = false;
    }
    
    /**
     * 
     * This method returns the AwardDocument object.
     */
    public InstitutionalProposalDocument getInstitutionalProposalDocument() {
        return (InstitutionalProposalDocument) super.getDocument();
    }
    
    /**
     * This method returns a string representation of the document type
     */
    public String getDocumentTypeName() {
        return "InstitutionalProposalDocument";
    }

    public SpecialReviewHelper getSpecialReviewHelper() {
        return specialReviewHelper;
    }

    public void setSpecialReviewHelper(SpecialReviewHelper specialReviewHelper) {
        this.specialReviewHelper = specialReviewHelper;
    }

    @Override
    public InstitutionalProposalCustomDataFormHelper getCustomDataHelper() {
        return institutionalProposalCustomDataFormHelper;
    }

    public InstitutionalProposalCustomDataFormHelper getInstitutionalProposalCustomDataFormHelper() {
        return institutionalProposalCustomDataFormHelper;
    }

    public void setInstitutionalProposalCustomDataFormHelper(
            InstitutionalProposalCustomDataFormHelper institutionalProposalCustomDataFormHelper) {
        this.institutionalProposalCustomDataFormHelper = institutionalProposalCustomDataFormHelper;
    }

    public NotificationHelper<InstitutionalProposalNotificationContext> getNotificationHelper() {
        return notificationHelper;
    }

    public void setNotificationHelper(NotificationHelper<InstitutionalProposalNotificationContext> notificationHelper) {
        this.notificationHelper = notificationHelper;
    }

    public InstitutionalProposalNotepadBean getInstitutionalProposalNotepadBean() {
        return institutionalProposalNotepadBean;
    }

    public void setInstitutionalProposalNotepadBean(InstitutionalProposalNotepadBean institutionalProposalNotepadBean) {
        this.institutionalProposalNotepadBean = institutionalProposalNotepadBean;
    }

    public InstitutionalProposalProjectPersonnelBean getProjectPersonnelBean() {
        return projectPersonnelBean;
    }

    public void setProjectPersonnelBean(InstitutionalProposalProjectPersonnelBean projectPersonnelBean) {
        this.projectPersonnelBean = projectPersonnelBean;
    }
    
    public InstitutionalProposalUnitContactsBean getUnitContactsBean() {
        return unitContactsBean;
    }

    public InstitutionalProposalCentralAdminContactsBean getCentralAdminContactsBean() {
        return centralAdminContactsBean;
    }

    public InstitutionalProposalCreditSplitBean getInstitutionalProposalCreditSplitBean() {
        return institutionalProposalCreditSplitBean;
    }

    public void setInstitutionalProposalCreditSplitBean(InstitutionalProposalCreditSplitBean institutionalProposalCreditSplitBean) {
        this.institutionalProposalCreditSplitBean = institutionalProposalCreditSplitBean;
    }

    public InstitutionalProposalCostShareBean getInstitutionalProposalCostShareBean() {
        return institutionalProposalCostShareBean;
    }

    public void setInstitutionalProposalCostShareBean(InstitutionalProposalCostShareBean institutionalProposalCostShareBean) {
        this.institutionalProposalCostShareBean = institutionalProposalCostShareBean;
    }

    public InstitutionalProposalFandABean getInstitutionalProposalFandABean() {
        return institutionalProposalFandABean;
    }

    public void setInstitutionalProposalFandABean(InstitutionalProposalFandABean institutionalProposalFandABean) {
        this.institutionalProposalFandABean = institutionalProposalFandABean;
    }

    public InstitutionalProposalUnrecoveredFandABean getInstitutionalProposalUnrecoveredFandABean() {
        return institutionalProposalUnrecoveredFandABean;
    }

    public void setInstitutionalProposalUnrecoveredFandABean(
            InstitutionalProposalUnrecoveredFandABean institutionalProposalUnrecoveredFandABean) {
        this.institutionalProposalUnrecoveredFandABean = institutionalProposalUnrecoveredFandABean;
    }
    
    public InstitutionalProposalAttachmentFormBean getInstitutionalProposalAttachmentBean() {
        return institutionalProposalAttachmentBean;
    }
    
    public void setInstitutionalProposalAttachmentBean(InstitutionalProposalAttachmentFormBean institutionalProposalAttachmentFormBean) {
        this.institutionalProposalAttachmentBean=institutionalProposalAttachmentFormBean;
    }

    @Override
    public String getActionName() {
        return "institutionalProposal";
    }

    public String getSelectedLeadUnit() {
        return projectPersonnelBean.getSelectedLeadUnit();
    }

    public void setSelectedLeadUnit(String unitNumber) {
        projectPersonnelBean.setSelectedLeadUnit(unitNumber);
    }
    
    @Override
    protected String getLockRegion() {
        return KraAuthorizationConstants.LOCK_DESCRIPTOR_INSTITUTIONAL_PROPOSAL;
    }

    @Override
    protected void setSaveDocumentControl(Map editMode) {
        getDocumentActions().put(KRADConstants.KUALI_ACTION_CAN_SAVE, KRADConstants.KUALI_DEFAULT_TRUE_VALUE);

    }
    
    @Override
    public boolean isAuditActivated() {
        return this.auditActivated;
    }

    @Override
    public void setAuditActivated(boolean auditActivated) {
        this.auditActivated = auditActivated;
    }

    @Override
    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    @Override
    public String getLookupResultsBOClassName() {
        return lookupResultsBOClassName;
    }

    public void setLookupResultsBOClassName(String lookupResultsBOClassName) {
        this.lookupResultsBOClassName = lookupResultsBOClassName;
    }

    public String getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public MedusaBean getMedusaBean() {
        return medusaBean;
    }

    public void setMedusaBean(MedusaBean medusaBean) {
        this.medusaBean = medusaBean;
    }

    public InstitutionalProposalCfda getNewProposalCfda() {
        return newProposalCfda;
    }

    public void setNewProposalCfda(InstitutionalProposalCfda newProposalCfda) {
        this.newProposalCfda = newProposalCfda;
    }

    public String getValueFinderResultDoNotCache(){
        if (this.getActionFormUtilMap() instanceof ActionFormUtilMap) {
            ((ActionFormUtilMap) this.getActionFormUtilMap()).setCacheValueFinderResults(false);
        }
        return "";
    }
    
    public String getValueFinderResultCache(){
        if (this.getActionFormUtilMap() instanceof ActionFormUtilMap) {
            ((ActionFormUtilMap) this.getActionFormUtilMap()).setCacheValueFinderResults(true);
        }
        return "";
    }


    @Override
    public ReportHelperBean getReportHelperBean() {
        return reportHelperBean;
    }

    public String[] getSelectedAwardFundingProposals() {
        return selectedAwardFundingProposals;
    }

    public void setSelectedAwardFundingProposals(String[] selectedAwardFundingProposals) {
        this.selectedAwardFundingProposals = selectedAwardFundingProposals;
    }
    
    public boolean getDisplayEditButton() {
        return !getDocument().getDocumentHeader().getWorkflowDocument().isCanceled()
                && getInstitutionalProposalDocument().getInstitutionalProposal().isActiveVersion();
      }
    
    public boolean isCfdaLookupRequired() {
        String integration = getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_AWARD, 
                Constants.PARAMETER_COMPONENT_DOCUMENT, Constants.FIN_SYSTEM_INTEGRATION_ON_OFF_PARAMETER);
        cfdaLookupRequired = StringUtils.equals(integration, Constants.FIN_SYSTEM_INTEGRATION_ON) ? true : false;
        return cfdaLookupRequired;
    }

    protected ParameterService getParameterService() {
        return KcServiceLocator.getService(ParameterService.class);
    }
    
    public boolean getViewFundingSource() {
        return viewFundingSource;
    }
    
    public void setViewFundingSource(boolean viewFundingSource) {
        this.viewFundingSource = viewFundingSource;
    }

    public boolean isDocOpenedFromIPSearch() {
        return docOpenedFromIPSearch;
    }
    public void setDocOpenedFromIPSearch(boolean docOpened) {
        docOpenedFromIPSearch = docOpened;
    }
    
    @Override
    public HeaderNavigation[] getHeaderNavigationTabs() {
        
        HeaderNavigation[] navigation = super.getHeaderNavigationTabs();
        
        List<HeaderNavigation> resultList = new ArrayList<HeaderNavigation>();
            //We have to copy the HeaderNavigation elements into a new collection as the 
            //List returned by DD is it's cached copy of the header navigation list.
        for (HeaderNavigation nav : navigation) {
            if (StringUtils.equals(nav.getHeaderTabNavigateTo(),CUSTOM_DATA_NAV_TO)) {
                boolean displayTab = !((InstitutionalProposalDocument)this.getDocument()).getCustomAttributeDocuments().isEmpty();
                nav.setDisabled(!displayTab);
                if (displayTab) {
                    resultList.add(nav);
                }
            } else if (StringUtils.equals(nav.getHeaderTabNavigateTo(), Constants.MAPPING_INSTITUTIONAL_PROPOSAL_INTELLECTUAL_PROPERTY_REVIEW_PAGE)) {
                if (isIPReviewTabEnabled()) {
                    resultList.add(nav);
                }
            } else if (StringUtils.equals(nav.getHeaderTabNavigateTo(), ATTACHMENTS)) {
                if (isIPAttachmentsEnabled()) {
                    resultList.add(nav);
                }
            }
            else {
                resultList.add(nav);
            }
        }

        HeaderNavigation[] result = new HeaderNavigation[resultList.size()];
        resultList.toArray(result);
        return result;
    }

    public boolean isIPReviewTabEnabled() {
        return getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_INSITUTIONAL_PROPOSAL,
                Constants.PARAMETER_COMPONENT_DOCUMENT, Constants.PARAMETER_IP_REVIEW_TAB_ENABLED);
    }

    private boolean isIPAttachmentsEnabled() {
        return getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_INSITUTIONAL_PROPOSAL,
                Constants.PARAMETER_COMPONENT_DOCUMENT, INSTITUTIONAL_PROPOSAL_ATTACHMENTS_FLAG);
    }

    public List<ExtraButton> getExtraActionsButtons() {
        extraButtons.clear();
        
        String externalImageURL = Constants.KRA_EXTERNALIZABLE_IMAGES_URI_KEY;
        ConfigurationService configurationService = CoreApiServiceLocator.getKualiConfigurationService();
        
        String sendNotificationImage = configurationService.getPropertyValueAsString(externalImageURL) + "buttonsmall_send_notification.gif";
        addExtraButton("methodToCall.sendNotification", sendNotificationImage, "Send Notification");
        
        return extraButtons;
    }

    public String getShortUrl() {
        return getBaseShortUrl() + "/kc-common/proposals/" + getInstitutionalProposalDocument().getInstitutionalProposal().getInstProposalNumber();
    }

    public boolean getDisplayCoiDisclosureStatus() {
        return getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_INSITUTIONAL_PROPOSAL,
                Constants.PARAMETER_COMPONENT_DOCUMENT, Constants.ENABLE_DISCLOSURE_STATUS_FROM_COI_MODULE);
    }

    public boolean getDisplayCoiProjectStatus() {
        return getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_SYSTEM,
                Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, Constants.PROJECT_STATUS_FEATURE_FLAG);
    }

    public boolean getDisplayCoiDispositionStatus() {
        return getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_INSITUTIONAL_PROPOSAL,
                Constants.PARAMETER_COMPONENT_DOCUMENT, Constants.ENABLE_DISCLOSURE_DISPOSITION_STATUS_FROM_COI_MODULE);
    }

    public boolean isLifecycleRatesFlowthruEnabled() {
        return getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_INSITUTIONAL_PROPOSAL,
                Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, Constants.ENABLE_LIFECYCLE_RATES_FLOWTHRU);
    }

    public List<DisclosureProjectStatus> getDisclosureProjectStatuses() {
        if (disclosureProjectStatuses == null) {
            disclosureProjectStatuses = getDisclosureStatusRetrievalService().getDisclosureStatusesForProject(
                    Constants.MODULE_NAMESPACE_INSITUTIONAL_PROPOSAL, getInstitutionalProposalDocument().getInstitutionalProposal().getInstProposalNumber()
            );
        }
        return disclosureProjectStatuses;
    }

    public void refreshDisclosureProjectStatuses() {
        if (getDisplayCoiDispositionStatus() || getDisplayCoiDisclosureStatus()) {
            disclosureProjectStatuses = getDisclosureStatusRetrievalService().getDisclosureStatusesForProject(
                    Constants.MODULE_NAMESPACE_INSITUTIONAL_PROPOSAL, getInstitutionalProposalDocument().getInstitutionalProposal().getInstProposalNumber()
            );
        }
    }


    public boolean isCreditSplitOptInEnabled() {
        return getInstitutionalProposalPersonService().isCreditSplitOptInEnabled();
    }

    protected DisclosureStatusRetrievalService getDisclosureStatusRetrievalService() {
        if (disclosureStatusRetrievalService == null) {
            disclosureStatusRetrievalService = KcServiceLocator.getService(DisclosureStatusRetrievalService.class);
        }
        return disclosureStatusRetrievalService;
    }

    public InstitutionalProposalPersonService getInstitutionalProposalPersonService() {
        if (institutionalProposalPersonService == null) {
            institutionalProposalPersonService = KcServiceLocator.getService(InstitutionalProposalPersonService.class);
        }
        return institutionalProposalPersonService;
    }
}
