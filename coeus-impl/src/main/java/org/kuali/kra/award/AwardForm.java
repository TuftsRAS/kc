/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.award.AccountInformationBean;
import org.kuali.coeus.award.finance.AwardAccount;
import org.kuali.coeus.award.finance.AwardPostHistoryBean;
import org.kuali.coeus.award.finance.AwardPosts;
import org.kuali.coeus.award.finance.dao.AccountDao;
import org.kuali.coeus.award.finance.timeAndMoney.TimeAndMoneyPostHistoryBean;
import org.kuali.coeus.award.finance.timeAndMoney.TimeAndMoneyPosts;
import org.kuali.coeus.award.finance.timeAndMoney.dao.TimeAndMoneyPostsDao;
import org.kuali.coeus.coi.framework.DisclosureProjectStatus;
import org.kuali.coeus.coi.framework.DisclosureStatusRetrievalService;
import org.kuali.coeus.common.budget.framework.core.BudgetVersionFormBase;
import org.kuali.coeus.common.framework.custom.CustomDataDocumentForm;
import org.kuali.coeus.common.framework.medusa.MedusaBean;
import org.kuali.coeus.common.framework.person.KcPerson;
import org.kuali.coeus.common.framework.person.KcPersonService;
import org.kuali.coeus.common.framework.version.VersionStatus;
import org.kuali.coeus.common.framework.version.history.VersionHistoryService;
import org.kuali.coeus.common.notification.impl.NotificationHelper;
import org.kuali.coeus.common.permissions.impl.web.struts.form.PermissionsForm;
import org.kuali.coeus.sys.framework.model.MultiLookupForm;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.coeus.sys.framework.validation.Auditable;
import org.kuali.kra.authorization.KraAuthorizationConstants;
import org.kuali.kra.award.awardhierarchy.AwardHierarchy;
import org.kuali.kra.award.awardhierarchy.AwardHierarchyBean;
import org.kuali.kra.award.awardhierarchy.AwardHierarchyTempObject;
import org.kuali.kra.award.awardhierarchy.sync.AwardSyncBean;
import org.kuali.kra.award.budget.AwardBudgetLimitsBean;
import org.kuali.kra.award.budget.BudgetLimitSummaryHelper;
import org.kuali.kra.award.commitments.AwardFandaRate;
import org.kuali.kra.award.commitments.CostShareFormHelper;
import org.kuali.kra.award.contacts.*;
import org.kuali.kra.award.customdata.CustomDataHelper;
import org.kuali.kra.award.detailsdates.DetailsAndDatesFormHelper;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.AwardCfda;
import org.kuali.kra.award.home.AwardComment;
import org.kuali.kra.award.home.AwardService;
import org.kuali.kra.award.home.approvedsubawards.ApprovedSubawardFormHelper;
import org.kuali.kra.award.home.fundingproposal.AwardFundingProposal;
import org.kuali.kra.award.home.fundingproposal.AwardFundingProposalBean;
import org.kuali.kra.award.notesandattachments.attachments.AwardAttachmentFormBean;
import org.kuali.kra.award.notesandattachments.comments.AwardCommentBean;
import org.kuali.kra.award.notesandattachments.notes.AwardNotepadBean;
import org.kuali.kra.award.notification.AwardNotificationContext;
import org.kuali.kra.award.paymentreports.ReportClass;
import org.kuali.kra.award.paymentreports.awardreports.AwardReportsBean;
import org.kuali.kra.award.paymentreports.awardreports.reporting.ReportTracking;
import org.kuali.kra.award.paymentreports.awardreports.reporting.ReportTrackingBean;
import org.kuali.kra.award.paymentreports.awardreports.reporting.service.ReportTrackingService;
import org.kuali.kra.award.paymentreports.closeout.AwardCloseoutBean;
import org.kuali.kra.award.paymentreports.paymentschedule.PaymentScheduleBean;
import org.kuali.kra.award.paymentreports.specialapproval.approvedequipment.ApprovedEquipmentBean;
import org.kuali.kra.award.paymentreports.specialapproval.foreigntravel.ApprovedForeignTravelBean;
import org.kuali.kra.award.permissions.PermissionsHelper;
import org.kuali.kra.award.printing.AwardPrintNotice;
import org.kuali.kra.award.printing.AwardTransactionSelectorBean;
import org.kuali.kra.award.service.AwardHierarchyUIService;
import org.kuali.kra.award.specialreview.SpecialReviewHelper;
import org.kuali.kra.award.web.struts.action.SponsorTermFormHelper;
import org.kuali.kra.external.award.AwardAccountService;
import org.kuali.kra.external.award.web.AccountCreationPresentationHelper;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.criteria.CountFlag;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.datadictionary.HeaderNavigation;
import org.kuali.rice.kns.util.ActionFormUtilMap;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 * This class represents the Award Form Struts class.
 */
public class AwardForm extends BudgetVersionFormBase implements MultiLookupForm,Auditable,PermissionsForm,CustomDataDocumentForm {

    public static final String SAVE = "save";
    public static final String RELOAD = "reload";
    private static final String CUSTOM_DATA_NAV_TO = "customData";


    private static final int NUMBER_30 = 30;
    public static final String COLUMN = ":";
    public static final String UPDATE_TIMESTAMP_DD_NAME = "DataDictionary.Award.attributes.updateTimestamp";
    public static final String SPONSOR_DD_NAME = "DataDictionary.Sponsor.attributes.sponsorName";
    private static final Logger LOG = LogManager.getLogger(AwardForm.class);
    public static final String ACCOUNT_NUMBER = "accountNumber";
    public static final String METHOD_TO_CALL_POST_AWARD = "methodToCall.postAward";
    public static final String POST_AWARD_ALT_TEXT = "Post Award";
    public static final String BUTTONSMALL_POST_GIF = "buttonsmall_postawardbudget.gif";
    public static final String BUTTONSMALL_SEND_NOTIFICATION_GIF = "buttonsmall_send_notification.gif";
    public static final String SEND_NOTIFICATION = "Send Notification";
    public static final String METHOD_TO_CALL_SEND_NOTIFICATION = "methodToCall.sendNotification";
    private final String AWARD_HIERARCHY_TEMP_OBJ_PARAM_NAME_PREFIX = "awardHierarchyTempObject[";
    private final int AWARD_HIERARCHY_TEMP_OBJ_PARAM_NAME_PREFIX_LENGTH = AWARD_HIERARCHY_TEMP_OBJ_PARAM_NAME_PREFIX.length();
    
    private static final long serialVersionUID = -7633960906991275328L;
    
    private static final String PAYMENT_SCHEDULE_ACTIVE_LINKS_PARAMETER = "AwardPaymentScheduleActiveLinks";
    private static Boolean displayAwardPaymentScheduleActiveLinkFields;
    
    private String lookupResultsBOClassName;
    private String lookupResultsSequenceNumber;
    
    private String prevAwardNumber;
    private String prevRootAwardNumber;

    private AwardComment newAwardCostShareComment;
    private AwardCfda newAwardCfda;
    private AwardFandaRate newAwardFandaRate;    
    private List<ConcreteKeyValue> reportClasses;
    private String directIndirectViewEnabled;
    
    private ApprovedEquipmentBean approvedEquipmentBean;
    private AwardProjectPersonnelBean projectPersonnelBean;
    private AwardUnitContactsBean unitContactsBean;
    private AwardCentralAdminContactsBean centralAdminContactsBean;
    private AwardSponsorContactsBean sponsorContactsBean;
    private CostShareFormHelper costShareFormHelper;
    private SponsorTermFormHelper sponsorTermFormHelper;
    private ApprovedSubawardFormHelper approvedSubawardFormHelper;
    private DetailsAndDatesFormHelper detailsAndDatesFormHelper;
    private AwardCloseoutBean awardCloseoutBean;
    
    private ReportClass reportClassForPaymentsAndInvoices;
    private PaymentScheduleBean paymentScheduleBean;
    private ApprovedForeignTravelBean approvedForeignTravelBean;
    private AwardReportsBean awardReportsBean;
    private AwardCommentBean awardCommentBean;
    private AwardNotepadBean awardNotepadBean;
    private AwardAttachmentFormBean awardAttachmentFormBean;
    private MedusaBean medusaBean;
    private AwardBudgetLimitsBean awardBudgetLimitsBean;
    
    private boolean auditActivated;
    private CustomDataHelper customDataHelper = new CustomDataHelper(this);
    private PermissionsHelper permissionsHelper;
    private SpecialReviewHelper specialReviewHelper;
    private NotificationHelper<AwardNotificationContext> notificationHelper;
    private AwardCreditSplitBean awardCreditSplitBean;
    private Map<String, AwardHierarchy> awardHierarchyNodes;
    private String awardNumberInputTemp;
    private AwardFundingProposalBean fundingProposalBean;
    private String awardHierarchy;
    private String awardNumber;
    private String awardHierarchyTargetAwardNumber;
    private String awardHierarchySourceAwardStrList;
    private String awardHierarchyTargetAwardStrList;
    private String addRA;    
    private String deletedRas;
    private String rootAwardNumber;
    

    private AwardHierarchyBean awardHierarchyBean;
    private AwardPrintNotice awardPrintNotice;
    private AwardTransactionSelectorBean awardPrintChangeReport;
    private AwardTransactionSelectorBean awardTimeAndMoneyTransactionReport;
    private List<AwardComment> awardCommentHistoryByType;
    
    private Map< AwardTemplateSyncScope, Boolean > syncRequiresConfirmationMap;
    private AwardTemplateSyncScope[] currentSyncScopes;
    private String currentSyncQuestionId;
    private Integer oldTemplateCode;
    private boolean templateLookup = false;
    
    private String newProposalBudgetPeriods; 
    
    private String currentAwardNumber;
    private String currentSeqNumber;
    
    private List<ReportTracking> reportTrackingsToDelete = new ArrayList<>();
    
    private boolean viewFundingSource;

    private boolean syncMode;
    private AwardSyncBean awardSyncBean;
    
    private Long placeHolderAwardId;
    private boolean docOpenedFromAwardSearch;
    private BudgetLimitSummaryHelper budgetLimitSummary;

    private transient ParameterService parameterService;
    private transient AwardService awardService;
    private transient AwardHierarchyUIService awardHierarchyUIService;
    private transient ReportTrackingService reportTrackingService;
    private transient DataObjectService dataObjectService;

    private List<ReportTrackingBean> reportTrackingBeans;

    private AccountInformationBean accountInformationBean;
    private AccountCreationPresentationHelper accountCreationHelper;

    private transient List<DisclosureProjectStatus> disclosureProjectStatuses;
    private transient DisclosureStatusRetrievalService disclosureStatusRetrievalService;
    private transient PermissionService permissionService;
    private transient AccountDao accountDao;
    private transient KcPersonService kcPersonService;
    private transient TimeAndMoneyPostsDao timeAndMoneyPostsDao;
    private Boolean isBudgetVersionSummaryCumulative;
    private transient AwardAccountService awardAccountService;

    /**
     * Constructs a AwardForm with an existing AwardDocument. Used primarily by tests outside of Struts
     */
    public AwardForm() {
        super();
        initialize();
    }
    
    @Override
    protected String getDefaultDocumentTypeName() {
        return "AwardDocument";
    }
    
    /**
     * 
     * This method initialize all form variables
     */
    public void initialize() {
        newAwardFandaRate = new AwardFandaRate();
        awardCommentHistoryByType = new ArrayList<>();
        costShareFormHelper = new CostShareFormHelper(this);
        centralAdminContactsBean = new AwardCentralAdminContactsBean(this);
        sponsorTermFormHelper = new SponsorTermFormHelper(this);
        approvedSubawardFormHelper = new ApprovedSubawardFormHelper(this);
        approvedEquipmentBean = new ApprovedEquipmentBean(this);
        paymentScheduleBean = new PaymentScheduleBean(this);
        approvedForeignTravelBean = new ApprovedForeignTravelBean(this);
        projectPersonnelBean = new AwardProjectPersonnelBean(this);
        unitContactsBean = new AwardUnitContactsBean(this);
        sponsorContactsBean = new AwardSponsorContactsBean(this);
        detailsAndDatesFormHelper = new DetailsAndDatesFormHelper(this);
        awardReportsBean = new AwardReportsBean(this);
        awardNotepadBean = new AwardNotepadBean(this);
        awardAttachmentFormBean = new AwardAttachmentFormBean(this);
        setPermissionsHelper(new PermissionsHelper(this));
        setSpecialReviewHelper(new SpecialReviewHelper(this));
        setNotificationHelper(new NotificationHelper<>());
        awardCreditSplitBean = new AwardCreditSplitBean(this);
        awardCommentBean = new AwardCommentBean(this);
        awardCloseoutBean = new AwardCloseoutBean(this);
        awardHierarchyNodes = new TreeMap<>();
        fundingProposalBean = new AwardFundingProposalBean(this);
        awardPrintNotice = new AwardPrintNotice();
        awardPrintChangeReport = new AwardTransactionSelectorBean();
        buildReportTrackingBeans();
        awardHierarchyBean = new AwardHierarchyBean(this);
        initializeAccountBean();
        medusaBean = new MedusaBean();
        //sync
        syncRequiresConfirmationMap = null;
        currentSyncScopes = null;

        syncMode = false;
        awardSyncBean = new AwardSyncBean(this);
        setDirectIndirectViewEnabled(getDirectIndirectCostEnabled());
        budgetLimitSummary = new BudgetLimitSummaryHelper();
        awardBudgetLimitsBean = new AwardBudgetLimitsBean(this);
        accountCreationHelper = new AccountCreationPresentationHelper();
    }

    public String getDirectIndirectCostEnabled() {
        return getParameter(Constants.PARAMETER_MODULE_AWARD, ParameterConstants.DOCUMENT_COMPONENT, "ENABLE_AWD_ANT_OBL_DIRECT_INDIRECT_COST");
    }

    public String getParameter(String parameterModuleAward, String documentComponent, String parameterName) {
        return getParameterService().getParameterValueAsString(parameterModuleAward, documentComponent, parameterName);
    }

    public void buildReportTrackingBeans() {
        reportTrackingBeans = new ArrayList<>();
        int numberOfReportItems = this.getAwardDocument().getAward().getAwardReportTermItems().size();
        for (int i=0; i<numberOfReportItems; i++) {
            reportTrackingBeans.add(new ReportTrackingBean());
        }
    }

    public boolean isCreditSplitOptInEnabled() {
        return getAwardService().isCreditSplitOptInEnabled();
    }

    public AwardDocument getAwardDocument() {
        return (AwardDocument) super.getDocument();
    }
    

    public ApprovedEquipmentBean getApprovedEquipmentBean() {
        return approvedEquipmentBean;
    }
    

    public ApprovedForeignTravelBean getApprovedForeignTravelBean() {
        return approvedForeignTravelBean;
    }
    

    public AwardCentralAdminContactsBean getCentralAdminContactsBean() {
        return centralAdminContactsBean;
    }
    

    public CostShareFormHelper getCostShareFormHelper() {
        return costShareFormHelper;
    }
    

    public AwardCommentBean getAwardCommentBean() {
        return awardCommentBean;
    }
   
    /**
     * 
     * This method initializes either the document or the form based on the command value.
     */
    public void initializeFormOrDocumentBasedOnCommand(){
        if (KewApiConstants.INITIATE_COMMAND.equals(getCommand())) {
            getAwardDocument().initialize();
        }else{
            initialize();
        }
    }
    public AwardComment getNewAwardCostShareComment() {
        return newAwardCostShareComment;
    }

    public void setNewAwardCostShareComment(AwardComment newAwardCostShareComment) {
        this.newAwardCostShareComment = newAwardCostShareComment;
    }

    public AwardCfda getNewAwardCfda() {
        return newAwardCfda;
    }

    public void setNewAwardCfda(AwardCfda newAwardCfda) {
        this.newAwardCfda = newAwardCfda;
    }

    public AwardFandaRate getNewAwardFandaRate() {
        return newAwardFandaRate;
    }

    public void setNewAwardFandaRate(AwardFandaRate newAwardFandaRate) {
        this.newAwardFandaRate = newAwardFandaRate;
    }
    
    @Override
    protected void setSaveDocumentControl(Map editMode) {
        getDocumentActions().put(KRADConstants.KUALI_ACTION_CAN_SAVE, KRADConstants.KUALI_DEFAULT_TRUE_VALUE);
    }
    
    @Override
    protected String getLockRegion() {
        return KraAuthorizationConstants.LOCK_DESCRIPTOR_AWARD;
    }

    @Override
    public String getLookupResultsBOClassName() {
        return lookupResultsBOClassName;
    }

    @Override
    public void setLookupResultsBOClassName(String lookupResultsBOClassName) {
        this.lookupResultsBOClassName = lookupResultsBOClassName;
    }

    @Override
    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    @Override
    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    public List<AwardComment> getAwardCommentHistoryByType() {
        return awardCommentHistoryByType;
    }

    public void setAwardCommentHistoryByType(List<AwardComment> awardCommentHistoryByType) {
        this.awardCommentHistoryByType = awardCommentHistoryByType;
    }

    public String getSelectedLeadUnit() {
        return projectPersonnelBean.getSelectedLeadUnit();
    }
    
    public AwardSponsorContactsBean getSponsorContactsBean() {
        return sponsorContactsBean;
    }
    
    public AccountCreationPresentationHelper getAccountCreationHelper() {
        return accountCreationHelper;
    }

    public AwardUnitContactsBean getUnitContactsBean() {
        return unitContactsBean;
    }
    
    public AwardProjectPersonnelBean getProjectPersonnelBean() {
        return projectPersonnelBean;
    }
        
    public List<ConcreteKeyValue> getReportClasses() {
        if (reportClasses != null) {         
            Collections.sort(reportClasses);
        }
        
        return reportClasses;
    }


    public boolean isAccountCreated() {
        if (getAwardDocument().getAward().getAccountNumber() != null) {
            if (getAwardDocument().getAward().getFinancialAccountDocumentNumber() != null) {
                return true;
            }
            if (isFinancialSystemIntegrationOn()) {
                return getAwardDocument().getAward().getFinancialAccountDocumentNumber() != null;
            }
            if (getAwardAccountService().isFinancialRestApiEnabled()) {
                return accountExistsInQueue();
            }
        }
        return false;
    }

    protected boolean accountExistsInQueue() {
        Map<String, String> accountsMap = new HashMap<>();
        accountsMap.put(ACCOUNT_NUMBER, getAwardDocument().getAward().getAccountNumber());
        int count = getDataObjectService().findMatching(AwardPosts.class,
                QueryByCriteria.Builder.andAttributes(accountsMap).setCountFlag(CountFlag.ONLY).build()).getTotalRowCount();


        return count != 0;
    }

    private void initializeAccountBean() {
        accountInformationBean = new AccountInformationBean();
        if (getAwardDocument().getAward().getAccountNumber() != null && getAwardAccountService().isFinancialRestApiEnabled()) {
            AwardAccount account = getAccountFromQueue();
            if (Objects.nonNull(account)) {
                accountInformationBean.setIncome(account.getIncome());
                accountInformationBean.setBudgeted(account.getBudgeted());
                accountInformationBean.setAvailable(account.getAvailable());
                accountInformationBean.setExpense(account.getExpense());
                accountInformationBean.setPending(account.getPending());
                accountInformationBean.setComment(account.getComment());
            }
        }
    }

    protected AwardAccount getAccountFromQueue() {
        Map<String, String> accountsMap = new HashMap<>();
        accountsMap.put(ACCOUNT_NUMBER, getAwardDocument().getAward().getAccountNumber());
        List<AwardAccount> accounts = getDataObjectService().findMatching(AwardAccount.class,
                QueryByCriteria.Builder.andAttributes(accountsMap).build()).getResults();
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    public boolean isDisplayAccountBalances() {
        return getParameterService().getParameterValueAsBoolean(
                Constants.PARAMETER_MODULE_AWARD,
                ParameterConstants.ALL_COMPONENT,
                Constants.DISPLAY_ACCOUNT_BALANCES
        );
    }

    protected DataObjectService getDataObjectService() {
        if (dataObjectService == null) {
            dataObjectService = KcServiceLocator.getService(DataObjectService.class);
        }
        return dataObjectService;
    }

    protected boolean isFinancialSystemIntegrationOn() {
        return getParameterService().getParameterValueAsBoolean(
                Constants.PARAMETER_MODULE_AWARD,
                ParameterConstants.DOCUMENT_COMPONENT,
                Constants.FIN_SYSTEM_INTEGRATION_ON_OFF_PARAMETER
        );
    }

    public void setReportClasses(List<ConcreteKeyValue> reportClasses) {
        this.reportClasses = reportClasses;
    }


    public ApprovedSubawardFormHelper getApprovedSubawardFormHelper() {
        return approvedSubawardFormHelper;
    }

    public void setApprovedSubawardFormHelper(ApprovedSubawardFormHelper approvedSubawardFormHelper) {
        this.approvedSubawardFormHelper = approvedSubawardFormHelper;
    }
    
     public ReportClass getReportClassForPaymentsAndInvoices() {
        return reportClassForPaymentsAndInvoices;
    }



    public void setReportClassForPaymentsAndInvoices(ReportClass reportClassForPaymentsAndInvoices) {
        this.reportClassForPaymentsAndInvoices = reportClassForPaymentsAndInvoices;
    }    

    public SponsorTermFormHelper getSponsorTermFormHelper() {
        return sponsorTermFormHelper;
    }

    public void setSelectedLeadUnit(String unitNumber) {
        projectPersonnelBean.setSelectedLeadUnit(unitNumber);
    }

    public void setSponsorTermFormHelper(SponsorTermFormHelper sponsorTermFormHelper) {
        this.sponsorTermFormHelper = sponsorTermFormHelper;
    }

    public PaymentScheduleBean getPaymentScheduleBean() {
        return paymentScheduleBean;
    }

    public void setPaymentScheduleBean(PaymentScheduleBean paymentScheduleBean) {
        this.paymentScheduleBean = paymentScheduleBean;
    }
    
    @Override
    public boolean isAuditActivated() {
        return this.auditActivated;
    }

    @Override
    public void setAuditActivated(boolean auditActivated) {
        this.auditActivated = auditActivated;
    }

    public boolean isAwardInMultipleNodeHierarchy() {
        return getAwardDocument().getAward().isAwardInMultipleNodeHierarchy();
    }

    public boolean isAwardHasAssociatedTandMOrIsVersioned() {
        return getAwardDocument().getAward().isAwardHasAssociatedTandMOrIsVersioned();
    }

    public int getIndexOfAwardAmountInfoForDisplay() {
        return getAwardDocument().getAward().getIndexOfAwardAmountInfoForDisplay();
    }

    public DetailsAndDatesFormHelper getDetailsAndDatesFormHelper() {
        return detailsAndDatesFormHelper;
    }

    public void setDetailsAndDatesFormHelper(DetailsAndDatesFormHelper detailsAndDatesFormHelper) {
        this.detailsAndDatesFormHelper = detailsAndDatesFormHelper;
    }

    public SpecialReviewHelper getSpecialReviewHelper() {
        return specialReviewHelper;
    }

    public void setSpecialReviewHelper(SpecialReviewHelper specialReviewHelper) {
        this.specialReviewHelper = specialReviewHelper;
    }

    public NotificationHelper<AwardNotificationContext> getNotificationHelper() {
        return notificationHelper;
    }

    public void setNotificationHelper(NotificationHelper<AwardNotificationContext> notificationHelper) {
        this.notificationHelper = notificationHelper;
    }

    @Override
    public PermissionsHelper getPermissionsHelper() {
        return permissionsHelper;
    }

    public void setPermissionsHelper(PermissionsHelper awardPermissionsHelper) {
        this.permissionsHelper = awardPermissionsHelper;
    }

    public String getDocumentTypeName() {
        return "AwardDocument";
    }
    
    @Override
    public String getActionName() {
        return "award";
    }
    
    @Override
    public CustomDataHelper getCustomDataHelper() {
        return customDataHelper;
    }

    public void setCustomDataHelper(CustomDataHelper customDataHelper) {
        this.customDataHelper = customDataHelper;
    }

    public void setAwardAuditActivated(boolean awardAuditActivated) {
        this.auditActivated = awardAuditActivated;
    }


    public AwardCreditSplitBean getAwardCreditSplitBean() {
        return awardCreditSplitBean;
    }

    public void setAwardCreditSplitBean(AwardCreditSplitBean awardCreditSplitBean) {
        this.awardCreditSplitBean = awardCreditSplitBean;
    }

    public void setAwardProjectPersonnelBean(AwardProjectPersonnelBean projectPersonnelBean) {
        this.projectPersonnelBean = projectPersonnelBean;
    }

    public AwardReportsBean getAwardReportsBean() {
        return awardReportsBean;
    }

    public void setAwardReportsBean(AwardReportsBean awardReportsBean) {
        this.awardReportsBean = awardReportsBean;
    }

    public AwardCloseoutBean getAwardCloseoutBean() {
        return awardCloseoutBean;
    }

    public void setAwardCloseoutBean(AwardCloseoutBean awardCloseoutBean) {
        this.awardCloseoutBean = awardCloseoutBean;
    }

    public AwardNotepadBean getAwardNotepadBean() {
        return awardNotepadBean;
    }

    public void setAwardNotepadBean(AwardNotepadBean awardNotepadBean) {
        this.awardNotepadBean = awardNotepadBean;
    }

    public AwardAttachmentFormBean getAwardAttachmentFormBean() {
        return awardAttachmentFormBean;
    }

    public void setAwardAttachmentFormBean(AwardAttachmentFormBean awardAttachmentFormBean) {
        this.awardAttachmentFormBean = awardAttachmentFormBean;
    }


    public AwardFundingProposalBean getFundingProposalBean() {
        return fundingProposalBean;
    }

    public Map<String, AwardHierarchy> getAwardHierarchyNodes() {
        if(awardHierarchyNodes == null || awardHierarchyNodes.size()==0){
            awardHierarchyNodes = getAwardHierarchyBean().getAwardHierarchy(getAwardHierarchyBean().getRootNode(), new ArrayList<>());
        }
        return awardHierarchyNodes;
    }

    public void setAwardHierarchyNodes(Map<String, AwardHierarchy> awardHierarchyNodes) {
        this.awardHierarchyNodes = awardHierarchyNodes;
    }

    public String getPrevAwardNumber() {
        return prevAwardNumber;
    }

    public void setPrevAwardNumber(String prevAwardNumber) {
        this.prevAwardNumber = prevAwardNumber;
    }

    public String getPrevRootAwardNumber() {
        return prevRootAwardNumber;
    }

    public void setPrevRootAwardNumber(String prevRootAwardNumber) {
        this.prevRootAwardNumber = prevRootAwardNumber;
    }

    public String getAwardNumberInputTemp() {
        return awardNumberInputTemp;
    }

    public void setAwardNumberInputTemp(String awardNumberInputTemp) {
        this.awardNumberInputTemp = awardNumberInputTemp;
    }
    
    public AwardHierarchyBean getAwardHierarchyBean() {
        return awardHierarchyBean;
    }


    public String getAwardHierarchy() throws ParseException {
        awardHierarchy = "";
        if(StringUtils.isBlank(awardNumber)){ 
            awardNumber = this.getRootAwardNumber();
        }
        
        if (awardNumber!=null && StringUtils.isNotBlank(addRA) && addRA.equals("E")){
            setAwardHierarchy(getAwardHierarchyUIService().getSubAwardHierarchiesForTreeView(awardNumber, currentAwardNumber, currentSeqNumber));
        } else if (awardNumber!=null && StringUtils.isNotBlank(addRA) && addRA.equals("N")){
            setAwardHierarchy(getAwardHierarchyUIService().getRootAwardNode(awardNumber, currentAwardNumber, currentSeqNumber));  
        }
        
        return awardHierarchy;
    }
    
    public void setAwardHierarchy(String awardHierarchy) {
        this.awardHierarchy = awardHierarchy;
    }

    public String getAwardNumber() {
        return awardNumber;
    }

    public void setAwardNumber(String awardNumber) {
        this.awardNumber = awardNumber;
    }

    public String getAddRA() {
        return addRA;
    }

    public void setAddRA(String addRA) {
        this.addRA = addRA;
    }

    public String getDeletedRas() {
        return deletedRas;
    }

    public void setDeletedRas(String deletedRas) {
        this.deletedRas = deletedRas;
    }

    private AwardHierarchyUIService getAwardHierarchyUIService() {
        if (awardHierarchyUIService == null) {
            awardHierarchyUIService = KcServiceLocator.getService(AwardHierarchyUIService.class);
        }
        return awardHierarchyUIService;
    }
    
    private ReportTrackingService getReportTrackingService() {
        if (reportTrackingService == null) {
            reportTrackingService = KcServiceLocator.getService(ReportTrackingService.class);
        }
        return reportTrackingService;
    }

    public boolean getDisplayRegenerateButton() {
        return !this.getReportTrackingService().autoRegenerateReports(this.getAwardDocument().getAward());
    }

    public String getRootAwardNumber() {
        return rootAwardNumber;
    }

    public void setRootAwardNumber(String rootAwardNumber) {
        this.rootAwardNumber = rootAwardNumber;
    }

    public AwardPrintNotice getAwardPrintNotice() {
        return awardPrintNotice;
    }

    public void setAwardPrintNotice(AwardPrintNotice awardPrintNotice) {
        this.awardPrintNotice = awardPrintNotice;
    }

    public AwardTransactionSelectorBean getAwardPrintChangeReport() {
        return awardPrintChangeReport;
    }

    public void setAwardPrintChangeReport(AwardTransactionSelectorBean awardPrintChangeReport) {
        this.awardPrintChangeReport = awardPrintChangeReport;
    }

    public List<AwardHierarchyTempObject> getAwardHierarchyTempObjects() {
        if(getAwardDocument().getAward().getAwardHierarchyTempObjects() == null) {
            getAwardDocument().getAward().initializeAwardHierarchyTempObjects(); 
        }
        
        return getAwardDocument().getAward().getAwardHierarchyTempObjects();
    }
    
    public AwardHierarchyTempObject getAwardHierarchyTempObject(int index) {
        while(getAwardHierarchyTempObjects().size() <= index) {
            getAwardDocument().getAward().getAwardHierarchyTempObjects().add(new AwardHierarchyTempObject());
        }
        return getAwardDocument().getAward().getAwardHierarchyTempObjects().get(index);
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
    
    public List<ExtraButton> getExtraTopButtons() {
        extraButtons.clear();
        String externalImageURL = Constants.KRA_EXTERNALIZABLE_IMAGES_URI_KEY;
        String generatePeriodImage = lookupKualiConfigurationService().getPropertyValueAsString(externalImageURL) + "tinybutton-timemoney.gif";
        
        addExtraButton("methodToCall.timeAndMoney", generatePeriodImage, "Time And Money");
        
        return extraButtons;
    }

    public String getShortUrl() {
        return getBaseShortUrl() + "/kc-common/awards/" + getAwardDocument().getAward().getAwardNumber();
    }

    private ConfigurationService lookupKualiConfigurationService() {
        return CoreApiServiceLocator.getKualiConfigurationService();
    }

    @Override
    protected void addExtraButton(String property, String source, String altText){
        
        ExtraButton newButton = new ExtraButton();
        
        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);
        
        extraButtons.add(newButton);
    }

    public MedusaBean getMedusaBean() {
        return medusaBean;
    }

    public void setMedusaBean(MedusaBean medusaBean) {
        this.medusaBean = medusaBean;
    }
    
    /**
     * This is a hack to fix a problem with Award Hierarchy. The way the AH UI was implemented was in JavaScript. For some reason, the awardHierarchyTempObject
     * form field data doesn't get set on the temp objects by Rice's property setting mechanism. Time is short, so I just do it manually here. jack frosch
     *
     */
    @Override
    public void postprocessRequestParameters(Map requestParameters) {
        super.postprocessRequestParameters(requestParameters);

        @SuppressWarnings("unchecked") Map<String, Object> parms = requestParameters;
        for(String parmKey: parms.keySet()) {
            if(parmKey.startsWith(AWARD_HIERARCHY_TEMP_OBJ_PARAM_NAME_PREFIX)) {
                populateAwardHierarchyTempObject(parms, parmKey);
            }
        }
    }

    private void populateAwardHierarchyTempObject(Map<String, Object> parms, String parmKey) {
        int indexOfClosingBracket = parmKey.indexOf("]");
        String fieldName = parmKey.substring(indexOfClosingBracket + 2);
        Object fieldValue = parms.get(parmKey);
        int tempObjectIndex = Integer.valueOf(parmKey.substring(AWARD_HIERARCHY_TEMP_OBJ_PARAM_NAME_PREFIX_LENGTH, indexOfClosingBracket));
        AwardHierarchyTempObject tempObject = getAwardHierarchyTempObject(tempObjectIndex);
        populateAwardHierarchyTempObjectFromRequestParms(tempObject, fieldName, fieldValue);
        if(tempObject.getCopyDescendants() == null) {
            tempObject.setCopyDescendants(false);
        }
    }

    private void populateAwardHierarchyTempObjectFromRequestParms(AwardHierarchyTempObject tempObject, String fieldName, Object fieldValue) {
       try {
            BeanUtils.setProperty(tempObject, fieldName, fieldValue);
        } catch(Exception e) {
            String message = String.format("Attempt to set %s property to %s on AwardHierarchyTempObject resulted in exception", fieldName, fieldValue.toString());
            LOG.error(message, e);
            throw new IllegalArgumentException(message, e);
        }
    }


    /**
     * This map is generated in the action and stored in the form as synchronizations to the template can
     * span one or more requests.  Each entry indicates if the user must confirm the synchronization request
     * for a particular scope being synchronized.
     * 
     * @return The current scopes remaining to be synchronized.  The action is responsible for maintaining this field.
     */
    public Map<AwardTemplateSyncScope, Boolean> getSyncRequiresConfirmationMap() {
        return syncRequiresConfirmationMap;
    }

    public void setSyncRequiresConfirmationMap(Map<AwardTemplateSyncScope, Boolean> syncRequiresConfirmationMap) {
        this.syncRequiresConfirmationMap = syncRequiresConfirmationMap;
    }
    
    
    public void setCurrentSyncQuestionId( String currentSyncQuestionId ) {
        this.currentSyncQuestionId = currentSyncQuestionId;
    }
    
    public String getCurrentSyncQuestionId() {
        return currentSyncQuestionId;
    }
    
    
    /**
     * The currentSyncScopes array holds the array of scopes that are currently being synchronized 
     * with the award template.  It is set by the form when a sync is initiated by the user.  Since the
     * ui may request confirmations to sync each scope, this is done in a loop spanning one or more requests 
     * by the action, which removes the scopes as the synchronizations are confirmed and performed 
     * or are declined by the user.
     * 
     * @return The current scopes remaining to be synchronized.  The action is responsible for maintaining this field.
     */
    public AwardTemplateSyncScope[] getCurrentSyncScopes() {
        return currentSyncScopes;
    }

    public void setCurrentSyncScopes(AwardTemplateSyncScope[] currentSyncOperations) {
        this.currentSyncScopes = currentSyncOperations;
    }

    /**
     * Returns the value of oldTemplateCode.  This is set by the award action
     * when the user starts a template code lookup.
     * 
     * @return The template code of the award before the template lookup was done.
     */
    public Integer getOldTemplateCode() {
        return oldTemplateCode;
    }

    
    public void setOldTemplateCode(Integer oldTemplateCode) {
        this.oldTemplateCode = oldTemplateCode;
    }

    
    /**
     * Boolean flag to indicate that the user is in a template code lookup loop.
     * Set by the action when a template code lookup is initiated by the user.
     * 
     * @return templateLookup
     */
    public boolean isTemplateLookup() {
        return templateLookup;
    }

    public void setTemplateLookup(boolean templateCodeChange) {
        this.templateLookup = templateCodeChange;
    }
    
    public List<Long> getLinkedProposals() {
        List<Long> linkedProposals = new ArrayList<>();
        if (this.getAwardDocument() != null && this.getAwardDocument().getAward() != null) {
            for (AwardFundingProposal fundingProposal : this.getAwardDocument().getAward().getAllFundingProposals()) {
                linkedProposals.add(fundingProposal.getProposalId());
            }
        }
        return linkedProposals;
    }
    
    @Override
    public void populateHeaderFields(WorkflowDocument workflowDocument) {
        AwardDocument awardDocument = getAwardDocument();
        getDocInfo().clear();
        getDocInfo().add(
                new HeaderField("DataDictionary.KraAttributeReferenceDummy.attributes.principalInvestigator", awardDocument
                        .getAward().getPrincipalInvestigatorName()));

        String docIdAndStatus = COLUMN;
        if (workflowDocument != null) {
            docIdAndStatus = getAwardDocument().getDocumentNumber() + COLUMN + workflowDocument.getStatus().getLabel();
        }
        getDocInfo().add(new HeaderField("DataDictionary.Award.attributes.docIdStatus", "<div id = \"docIdAndStatus\">" + docIdAndStatus + "</div>"));
        String unitName = awardDocument.getAward().getUnitName();
        if (StringUtils.isNotBlank(unitName) && unitName.length() > NUMBER_30) {
            unitName = unitName.substring(0, NUMBER_30);
        }
        getDocInfo().add(new HeaderField("DataDictionary.AwardPersonUnit.attributes.leadUnit", unitName));
        getDocInfo().add(new HeaderField("DataDictionary.Award.attributes.awardIdAccount", "<div id = \"awardIdAccount\">" + getAwardIdAccount(awardDocument) + "</div>"));

        setupSponsor(awardDocument);
        setupLastUpdate(awardDocument, UPDATE_TIMESTAMP_DD_NAME);

    }

    private String getAwardIdAccount(AwardDocument awardDocument) {
        String awardNum = awardDocument.getAward().getAwardNumber();
        String account = awardDocument.getAward().getAccountNumber() != null ? awardDocument.getAward().getAccountNumber()
                : Constants.EMPTY_STRING;
        return awardNum + COLUMN + account;
    }

    private void setupSponsor(AwardDocument awardDocument) {
        if (awardDocument.getAward().getSponsor() == null) {
            getDocInfo().add(new HeaderField(SPONSOR_DD_NAME, ""));
        } else {
            String sponsorName = awardDocument.getAward().getSponsorName();
            if (StringUtils.isNotBlank(sponsorName) && sponsorName.length() > NUMBER_30) {
                sponsorName = sponsorName.substring(0, NUMBER_30);
            }
            getDocInfo().add(new HeaderField(SPONSOR_DD_NAME, sponsorName));
        }

    }

    public String getNewProposalBudgetPeriods() {
        return newProposalBudgetPeriods;
    }

    public void setNewProposalBudgetPeriods(String newProposalBudgetPeriods) {
        this.newProposalBudgetPeriods = newProposalBudgetPeriods;
    }
    
    public boolean getDisplayEditButton() {
        return !getAwardDocument().isCanceled() && VersionStatus.ACTIVE.toString().equals(getAwardDocument().getAward().getAwardSequenceStatus());
    }
    
    protected VersionHistoryService getVersionHistoryService() {
        return KcServiceLocator.getService(VersionHistoryService.class);
    }

    public String getCurrentAwardNumber() {
        return currentAwardNumber;
    }

    public void setCurrentAwardNumber(String currentAwardNumber) {
        this.currentAwardNumber = currentAwardNumber;
    }

    public String getCurrentSeqNumber() {
        return currentSeqNumber;
    }

    public void setCurrentSeqNumber(String currentSeqNumber) {
        this.currentSeqNumber = currentSeqNumber;
    }

    public String getCanCreateAward() {
        Boolean aFlag = this.getEditingMode().containsKey(Constants.CAN_CREATE_AWARD_KEY);
        return aFlag.toString();
    }
    
    public boolean getViewFundingSource() {
        return viewFundingSource;
    }
    
    public void setViewFundingSource(boolean viewFundingSource) {
        this.viewFundingSource = viewFundingSource;
    }

    public void setAwardTimeAndMoneyTransactionReport(AwardTransactionSelectorBean awardTimeAndMoneyTransactionReport) {
        this.awardTimeAndMoneyTransactionReport = awardTimeAndMoneyTransactionReport;
    }

    public AwardTransactionSelectorBean getAwardTimeAndMoneyTransactionReport() {
        return awardTimeAndMoneyTransactionReport;
    }

    protected ParameterService getParameterService() {
        if (this.parameterService == null) {
            this.parameterService = KcServiceLocator.getService(ParameterService.class);
        }
        return this.parameterService;
    }

    protected AwardService getAwardService() {
        if (this.awardService == null) {
            this.awardService = KcServiceLocator.getService(AwardService.class);
        }
        return awardService;
    }

    protected AwardAccountService getAwardAccountService() {
        if (this.awardAccountService == null) {
            this.awardAccountService = KcServiceLocator.getService(AwardAccountService.class);
        }
        return awardAccountService;
    }

    public String getDirectIndirectViewEnabled() {
        return directIndirectViewEnabled;
    }

    public boolean isSyncMode() {
        return syncMode;
    }

    public void setSyncMode(boolean syncMode) {
        this.syncMode = syncMode;
    }

    public AwardSyncBean getAwardSyncBean() {
        return awardSyncBean;
    }

    public void setAwardSyncBean(AwardSyncBean awardSyncBean) {
        this.awardSyncBean = awardSyncBean;
    }     

    public void setDirectIndirectViewEnabled(String directIndirectViewEnabled) {
        this.directIndirectViewEnabled = directIndirectViewEnabled;
    }
    
    @Override
    public HeaderNavigation[] getHeaderNavigationTabs() {
        
        HeaderNavigation[] navigation = super.getHeaderNavigationTabs();
        
        List<HeaderNavigation> resultList = new ArrayList<>();
            //We have to copy the HeaderNavigation elements into a new collection as the 
            //List returned by DD is it's cached copy of the header navigation list.
        for (HeaderNavigation nav : navigation) {
            if (StringUtils.equals(nav.getHeaderTabNavigateTo(),CUSTOM_DATA_NAV_TO)) {
                boolean displayTab = !this.getCustomDataHelper().getCustomAttributeDocuments().isEmpty();
                nav.setDisabled(!displayTab);
                if (displayTab) {
                    resultList.add(nav);
                }
            } else {
                resultList.add(nav);
            }
        }
        
        HeaderNavigation[] result = new HeaderNavigation[resultList.size()];
        resultList.toArray(result);
        return result;
    }

    protected boolean isAutoPostAward() {
        return getParameterService().getParameterValueAsBoolean(
                Constants.PARAMETER_MODULE_AWARD, ParameterConstants.ALL_COMPONENT, Constants.AWARD_AUTO_POST_ENABLED);
    }

    public List<ExtraButton> getExtraActionsButtons() {
        extraButtons.clear();
        
        String externalImageURL = Constants.KRA_EXTERNALIZABLE_IMAGES_URI_KEY;
        ConfigurationService configurationService = CoreApiServiceLocator.getKualiConfigurationService();
        
        String sendNotificationImage = configurationService.getPropertyValueAsString(externalImageURL) + BUTTONSMALL_SEND_NOTIFICATION_GIF;
        addExtraButton(METHOD_TO_CALL_SEND_NOTIFICATION, sendNotificationImage, SEND_NOTIFICATION);

        if (!isAutoPostAward() && getAwardDocument().isAuthorizedToPostAward(GlobalVariables.getUserSession().getPrincipalId())) {
            String postAwardBudgetImage = buildExtraButtonSourceURI(BUTTONSMALL_POST_GIF);
            addExtraButton(METHOD_TO_CALL_POST_AWARD, postAwardBudgetImage, POST_AWARD_ALT_TEXT);
        }

        return extraButtons;
    }

    public List<AwardPostHistoryBean> getAwardPostHistory() {
        List<AwardPosts> awardPosts = getAccountDao().getAllAwardPostsInHierarchy(getAwardDocument().getAward().getAccountNumber(),
                getAwardDocument().getAward().getAwardNumber());

        return awardPosts.stream().map(awardPost -> {
            final AwardPostHistoryBean awardPostHistoryBean = new AwardPostHistoryBean(awardPost.isPosted(),
                    awardPost.isActive(),
                    awardPost.getDocumentNumber(),
                    awardPost.getUpdateUser(),
                    awardPost.getUpdateTimestamp());
            KcPerson person = getKcPersonService().getKcPersonByUserName(awardPost.getUpdateUser());
            awardPostHistoryBean.setFirstName(person.getFirstName());
            awardPostHistoryBean.setLastName(person.getLastName());
            return awardPostHistoryBean;
        }).collect(Collectors.toList());
    }

    public List<TimeAndMoneyPostHistoryBean> getTmPostHistory() {
        List<TimeAndMoneyPosts> timeAndMoneyPosts = getTimeAndMoneyPostsDao().getActiveTimeAndMoneyPostsForAwardHierarchy(getAwardDocument().getAward().getAwardNumber());

        return timeAndMoneyPosts.stream().map(timeAndMoneyPost -> {
            final TimeAndMoneyPostHistoryBean timeAndMoneyPostHistoryBean = new TimeAndMoneyPostHistoryBean(
                    timeAndMoneyPost.isActive(),
                    timeAndMoneyPost.getDocumentNumber(),
                    timeAndMoneyPost.getUpdateUser(),
                    timeAndMoneyPost.getUpdateTimestamp());
            KcPerson person = getKcPersonService().getKcPersonByUserName(timeAndMoneyPost.getUpdateUser());
            timeAndMoneyPostHistoryBean.setFirstName(person.getFirstName());
            timeAndMoneyPostHistoryBean.setLastName(person.getLastName());
            return timeAndMoneyPostHistoryBean;
        }).collect(Collectors.toList());
    }

    public KcPersonService getKcPersonService() {
        if(kcPersonService == null) {
            kcPersonService = KcServiceLocator.getService(KcPersonService.class);
        }
        return kcPersonService;
    }

    protected AccountDao getAccountDao() {
        if (accountDao == null) {
            accountDao = KcServiceLocator.getService(AccountDao.class);
        }
        return accountDao;
    }

    protected TimeAndMoneyPostsDao getTimeAndMoneyPostsDao() {
        if (timeAndMoneyPostsDao == null) {
            timeAndMoneyPostsDao = KcServiceLocator.getService(TimeAndMoneyPostsDao.class);
        }
        return timeAndMoneyPostsDao;
    }

    protected PermissionService getPermissionService() {
        if (permissionService == null) {
            permissionService = KimApiServiceLocator.getPermissionService();
        }
        return permissionService;
    }

    protected String buildExtraButtonSourceURI(String buttonFileName) {
        return lookupKualiConfigurationService().getPropertyValueAsString(Constants.KRA_EXTERNALIZABLE_IMAGES_URI_KEY) + buttonFileName;
    }

    public Long getPlaceHolderAwardId() {
        return placeHolderAwardId;
    }

    public void setPlaceHolderAwardId(Long placeHolderAwardId) {
        this.placeHolderAwardId = placeHolderAwardId;
    }

    public boolean isDocOpenedFromAwardSearch() {
        return docOpenedFromAwardSearch;
    }

    public void setDocOpenedFromAwardSearch(boolean docOpenedFromAwardSearch) {
        this.docOpenedFromAwardSearch = docOpenedFromAwardSearch;
    }

    public BudgetLimitSummaryHelper getBudgetLimitSummary() {
        return budgetLimitSummary;
    }

    public void setBudgetLimitSummary(BudgetLimitSummaryHelper budgetLimitSummary) {
        this.budgetLimitSummary = budgetLimitSummary;
    }

    public AwardBudgetLimitsBean getAwardBudgetLimitsBean() {
        return awardBudgetLimitsBean;
    }

    public void setAwardBudgetLimitsBean(AwardBudgetLimitsBean awardBudgetLimitsBean) {
        this.awardBudgetLimitsBean = awardBudgetLimitsBean;
    }
    
    public void setAwardHierarchyUIService(AwardHierarchyUIService awardHierarchyUIService) {
        this.awardHierarchyUIService = awardHierarchyUIService;
    }

    public List<ReportTracking> getReportTrackingsToDelete() {
        return reportTrackingsToDelete;
    }

    public void setReportTrackingsToDelete(List<ReportTracking> reportTrackingsToDelete) {
        this.reportTrackingsToDelete = reportTrackingsToDelete;
    }
    
    /**
     * 
     * This method returns true if the AwardPaymentScheduleActiveLinks equals "Y" otherwise returns false.
     */
    public boolean getDisplayAwardPaymentScheduleActiveLinkFields() {
        if (displayAwardPaymentScheduleActiveLinkFields == null) {
            String parmVal = getParameter(Constants.PARAMETER_MODULE_AWARD, Constants.PARAMETER_COMPONENT_DOCUMENT, PAYMENT_SCHEDULE_ACTIVE_LINKS_PARAMETER);
            displayAwardPaymentScheduleActiveLinkFields = StringUtils.equalsIgnoreCase("Y", parmVal);
        }
        return displayAwardPaymentScheduleActiveLinkFields;
    }

    public void setReportTrackingService(ReportTrackingService reportTrackingService) {
        this.reportTrackingService = reportTrackingService;
    }

    public List<ReportTrackingBean> getReportTrackingBeans() {
        return reportTrackingBeans;
    }

    public void setReportTrackingBeans(List<ReportTrackingBean> reportTrackingBeans) {
        this.reportTrackingBeans = reportTrackingBeans;
    }
    
    public String determineRootAwardNumber() {
        String prevRootAwardNumber = getPrevRootAwardNumber();
        return prevRootAwardNumber != null ? prevRootAwardNumber : getAwardDocument().getAward().getAwardNumber();
    }

    public String determineParentAwardNumber() {
        String prevAwardNumber = getPrevAwardNumber();
        return prevAwardNumber != null ? prevAwardNumber : Constants.AWARD_HIERARCHY_DEFAULT_PARENT_OF_ROOT;
    }    
 
    // returns list of awards with the same award number and prior to or equal to this one
    public List<Award> getAwardsForHistoryDisplay() {
        List<Award>results = new ArrayList<Award>();
        List<Award>rawList = getAwardDocument().getAward().getAwardVersions();
        int sequenceNumber = getAwardDocument().getAward().getSequenceNumber();
        for (Award award : rawList) {
            if (award.getSequenceNumber() <= sequenceNumber) {
                results.add(award);
            }
        }
        Collections.reverse(results);
        return results;
    }

	public String getAwardHierarchyTargetAwardNumber() {
		return awardHierarchyTargetAwardNumber;
	}

	public void setAwardHierarchyTargetAwardNumber(String awardHierarchyTargetAwardNumber) {
		this.awardHierarchyTargetAwardNumber = awardHierarchyTargetAwardNumber;
	}

	public String getAwardHierarchySourceAwardStrList() {
		return awardHierarchySourceAwardStrList;
	}

	public void setAwardHierarchySourceAwardStrList(String awardHierarchySourceAwardStrList) {
		this.awardHierarchySourceAwardStrList = awardHierarchySourceAwardStrList;
	}

	public String getAwardHierarchyTargetAwardStrList() {
		return awardHierarchyTargetAwardStrList;
	}

	public void setAwardHierarchyTargetAwardStrList(String awardHierarchyTargetAwardStrList) {
		this.awardHierarchyTargetAwardStrList = awardHierarchyTargetAwardStrList;
	}

    public AccountInformationBean getAccountInformationBean() {
        return accountInformationBean;
    }

    public void setAccountInformationBean(AccountInformationBean accountInformationBean) {
        this.accountInformationBean = accountInformationBean;
    }

    public boolean isAwardObligatedAndAnticipatedAmountsEditable() {
        return getParameterService().getParameterValueAsBoolean(Constants.PARAMETER_MODULE_AWARD,
                ParameterConstants.DOCUMENT_COMPONENT, Constants.MAKE_AWD_CUM_ANTICIPATED_OBL_EDITABLE);
    }

    public boolean getDisplayCoiDisclosureStatus() {
        return getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_AWARD,
                Constants.PARAMETER_COMPONENT_DOCUMENT, Constants.ENABLE_DISCLOSURE_STATUS_FROM_COI_MODULE);
    }

    public boolean getDisplayCoiProjectStatus() {
        return getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_SYSTEM,
                Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, Constants.PROJECT_STATUS_FEATURE_FLAG);
    }

    public boolean getDisplayCoiDispositionStatus() {
        return getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_AWARD,
                Constants.PARAMETER_COMPONENT_DOCUMENT, Constants.ENABLE_DISCLOSURE_DISPOSITION_STATUS_FROM_COI_MODULE);
    }

    public List<DisclosureProjectStatus> getDisclosureProjectStatuses() {
        if (disclosureProjectStatuses == null) {
            disclosureProjectStatuses = getDisclosureStatusRetrievalService().getDisclosureStatusesForProject(
                    Constants.MODULE_NAMESPACE_AWARD, getAwardDocument().getAward().getAwardNumber()
            );
        }
        return disclosureProjectStatuses;
    }

    public void refreshDisclosureProjectStatuses() {
        if (getDisplayCoiDispositionStatus() || getDisplayCoiDisclosureStatus()) {
            disclosureProjectStatuses = getDisclosureStatusRetrievalService().getDisclosureStatusesForProject(
                    Constants.MODULE_NAMESPACE_AWARD, getAwardDocument().getAward().getAwardNumber()
            );
        }
    }

    protected DisclosureStatusRetrievalService getDisclosureStatusRetrievalService() {
        if (disclosureStatusRetrievalService == null) {
            disclosureStatusRetrievalService = KcServiceLocator.getService(DisclosureStatusRetrievalService.class);
        }
        return disclosureStatusRetrievalService;
    }

    public boolean isBudgetVersionSummaryCumulative() {
        if (isBudgetVersionSummaryCumulative == null) {
            isBudgetVersionSummaryCumulative = getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_AWARD_BUDGET,
                    ParameterConstants.ALL_COMPONENT,
                    Constants.AWARD_BUDGET_SUMMARY_CUMULATIVE);
        }
        return isBudgetVersionSummaryCumulative;
    }

}
