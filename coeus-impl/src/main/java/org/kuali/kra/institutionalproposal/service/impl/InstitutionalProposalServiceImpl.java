/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.service.impl;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.coi.framework.Project;
import org.kuali.coeus.coi.framework.ProjectPublisher;
import org.kuali.coeus.coi.framework.ProjectRetrievalService;
import org.kuali.coeus.common.budget.api.rate.RateClassType;
import org.kuali.coeus.common.budget.framework.core.Budget;
import org.kuali.coeus.common.budget.framework.distribution.BudgetCostShare;
import org.kuali.coeus.common.budget.framework.distribution.BudgetUnrecoveredFandA;
import org.kuali.coeus.common.budget.framework.period.BudgetPeriod;
import org.kuali.coeus.common.framework.custom.attr.CustomAttribute;
import org.kuali.coeus.common.framework.custom.attr.CustomAttributeDocValue;
import org.kuali.coeus.common.framework.custom.attr.CustomAttributeDocument;
import org.kuali.coeus.common.framework.fiscalyear.FiscalYearMonthService;
import org.kuali.coeus.common.framework.version.VersionException;
import org.kuali.coeus.common.framework.version.VersionStatus;
import org.kuali.coeus.common.framework.version.VersioningService;
import org.kuali.coeus.propdev.impl.budget.ProposalDevelopmentBudgetExt;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.impl.core.ProposalTypeService;
import org.kuali.coeus.propdev.impl.keyword.PropScienceKeyword;
import org.kuali.coeus.propdev.impl.person.ProposalPerson;
import org.kuali.coeus.propdev.impl.person.ProposalPersonUnit;
import org.kuali.coeus.propdev.impl.person.creditsplit.CreditSplitConstants;
import org.kuali.coeus.propdev.impl.person.creditsplit.ProposalPersonCreditSplit;
import org.kuali.coeus.propdev.impl.person.creditsplit.ProposalUnitCreditSplit;
import org.kuali.coeus.propdev.impl.specialreview.ProposalSpecialReview;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.institutionalproposal.ProposalStatus;
import org.kuali.kra.institutionalproposal.contacts.InstitutionalProposalPerson;
import org.kuali.kra.institutionalproposal.contacts.InstitutionalProposalPersonCreditSplit;
import org.kuali.kra.institutionalproposal.contacts.InstitutionalProposalPersonUnit;
import org.kuali.kra.institutionalproposal.contacts.InstitutionalProposalPersonUnitCreditSplit;
import org.kuali.kra.institutionalproposal.customdata.InstitutionalProposalCustomData;
import org.kuali.kra.institutionalproposal.dao.InstitutionalProposalDao;
import org.kuali.kra.institutionalproposal.document.InstitutionalProposalDocument;
import org.kuali.kra.institutionalproposal.exception.InstitutionalProposalCreationException;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposalCostShare;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposalFandA;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposalUnrecoveredFandA;
import org.kuali.kra.institutionalproposal.printing.service.InstitutionalProposalPersonService;
import org.kuali.kra.institutionalproposal.proposaladmindetails.ProposalAdminDetails;
import org.kuali.kra.institutionalproposal.service.InstitutionalProposalService;
import org.kuali.kra.institutionalproposal.service.InstitutionalProposalVersioningService;
import org.kuali.kra.institutionalproposal.specialreview.InstitutionalProposalSpecialReview;
import org.kuali.kra.institutionalproposal.specialreview.InstitutionalProposalSpecialReviewExemption;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
public class InstitutionalProposalServiceImpl implements InstitutionalProposalService {
    private static final Logger LOG = LogManager.getLogger(InstitutionalProposalServiceImpl.class);

    private static final String WORKFLOW_EXCEPTION_MESSAGE = "Caught workflow exception creating new Institutional Proposal";
    private static final String VERSION_EXCEPTION_MESSAGE = "Caught version exception creating new Institutional Proposal";
    private static final String ROUTE_MESSAGE = "Autogenerated Institutional Proposal from Development Proposal ";
    private static final String NO_PRIOR_VERSION_MESSAGE = "Tried to version an InstitutionalProposal where no prior version exists.";
    private static final String NEW_DOCUMENT_DESCRIPTION = "Generated by Dev Proposal ";
    private static final String DECIMAL_FORMAT = "00000000";
    private static final String DECIMAL_FORMAT_FOR_NEW_IP = "0000";
    private static final String PROPOSAL_NUMBER = "proposalNumber";
    private static final String SEQUENCE_NUMBER = "sequenceNumber";
    private static final String ACTIVE_VALUE = "Y";
    private static final String INST_PROPOSAL_ID = "instProposalId";
    private static final int DEFAULT_STATUS_CODE = 1;
    private static final int WITHDRAWN_STATUS_CODE = 5;
    private static final Integer DEFAULT_COST_SHARE_TYPE_CODE = 1;
    private static final String VALID_FUNDING_PROPOSAL_STATUS_CODES = "validFundingProposalStatusCodes";
    private static final String SEPARATOR = ",";
    private static final String TRUE_INDICATOR_VALUE = "1";
    private static final String FALSE_INDICATOR_VALUE = "0";
    private static final int VERSION_1 = 1;

    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private VersioningService versioningService;
    private InstitutionalProposalVersioningService institutionalProposalVersioningService;
    private SequenceAccessorService sequenceAccessorService;
    private ParameterService parameterService;
    private InstitutionalProposalDao institutionalProposalDao;
    private FiscalYearMonthService fiscalYearMonthService;
    private InstitutionalProposalPersonService institutionalProposalPersonService;


    public FiscalYearMonthService getFiscalYearMonthService() {
        if (fiscalYearMonthService == null) {
            fiscalYearMonthService = KcServiceLocator.getService(FiscalYearMonthService.class);
        }
        return fiscalYearMonthService;
    }

    public void setFiscalYearMonthService(
            FiscalYearMonthService fiscalYearMonthService) {
        this.fiscalYearMonthService = fiscalYearMonthService;
    }

    private DataObjectService dataObjectService;

    private ProjectRetrievalService instPropProjectRetrievalService;

    private ProposalTypeService proposalTypeService;
    private ProjectPublisher projectPublisher;

    public ProposalTypeService getProposalTypeService() {
        //since PD is loaded after IP and @Lazy does not work, we have to use the ServiceLocator
        if (proposalTypeService == null) {
            proposalTypeService = KcServiceLocator.getService(ProposalTypeService.class);
        }


        return proposalTypeService;
    }

    public ProjectPublisher getProjectPublisher() {
        //since COI is loaded last and @Lazy does not work, we have to use the ServiceLocator
        if (projectPublisher == null) {
            projectPublisher = KcServiceLocator.getService(ProjectPublisher.class);
        }

        return projectPublisher;
    }


    /**
     * Creates a new pending Institutional Proposal based on given development proposal and budget.
     *
     * @param developmentProposal DevelopmentProposal
     * @param budget Budget
     * @return String The new institutional proposal
     * @see org.kuali.kra.institutionalproposal.service.InstitutionalProposalService#createInstitutionalProposal(DevelopmentProposal, Budget)
     */
    @Override
    public InstitutionalProposal createInstitutionalProposal(DevelopmentProposal developmentProposal, Budget budget) {

        try {
            InstitutionalProposal institutionalProposal = new InstitutionalProposal();

            // Set proposal number on new Institutional Proposal so that it will be propagated to
            // all created child BO's before initial save.
            institutionalProposal.setProposalNumber(getNextInstitutionalProposalNumber());

            InstitutionalProposalDocument institutionalProposalDocument = mergeProposals(institutionalProposal, developmentProposal, budget);
            setInstitutionalProposalIndicators(institutionalProposalDocument.getInstitutionalProposal());
            documentService.routeDocument(institutionalProposalDocument, ROUTE_MESSAGE + developmentProposal.getProposalNumber(), new ArrayList<>());
            final Project project = getInstPropProjectRetrievalService().retrieveProject(institutionalProposalDocument.getInstitutionalProposal().getProposalNumber());
            if (project != null) {
                getProjectPublisher().publishProject(project);
            }
            return institutionalProposalDocument.getInstitutionalProposal();
        } catch (WorkflowException ex) {
            throw new InstitutionalProposalCreationException(WORKFLOW_EXCEPTION_MESSAGE, ex);
        }
    }

    /**
     * Creates a new active version of the Institutional Proposal corresponding to the given proposal number, 
     * with data copied from the given development proposal and budget.
     *
     * @param proposalNumber String
     * @param developmentProposal DevelopmentProposal
     * @param budget Budget
     * @return String The new version number
     * @see org.kuali.kra.institutionalproposal.service.InstitutionalProposalService#createInstitutionalProposalVersion(String, DevelopmentProposal, Budget)
     */
    @Override
    public InstitutionalProposal createInstitutionalProposalVersion(String proposalNumber, DevelopmentProposal developmentProposal, Budget budget) {

        try {
            InstitutionalProposalDocument newInstitutionalProposalDocument = versionProposal(proposalNumber, developmentProposal, budget);
            setInstitutionalProposalIndicators(newInstitutionalProposalDocument.getInstitutionalProposal());
            documentService.routeDocument(newInstitutionalProposalDocument, ROUTE_MESSAGE + developmentProposal.getProposalNumber(),
                    new ArrayList<>());
            institutionalProposalVersioningService.updateInstitutionalProposalVersionStatus(newInstitutionalProposalDocument.getInstitutionalProposal(),
                    VersionStatus.ACTIVE);
            final Project project = getInstPropProjectRetrievalService().retrieveProject(newInstitutionalProposalDocument.getInstitutionalProposal().getProposalNumber());
            if (project != null) {
                getProjectPublisher().publishProject(project);
            }
            return newInstitutionalProposalDocument.getInstitutionalProposal();
        } catch (WorkflowException|VersionException e) {
            throw new InstitutionalProposalCreationException(VERSION_EXCEPTION_MESSAGE, e);
        }
    }

    /**
     * Return an Institutional Proposal, if one exists.
     *
     * @param proposalId String
     * @return InstitutionalProposal, or null if none is found.
     * @see org.kuali.kra.institutionalproposal.service.InstitutionalProposalService#getInstitutionalProposal(String)
     */
    @Override
    public InstitutionalProposal getInstitutionalProposal(String proposalId) {
        Map<String, String> criteria = new HashMap<>();
        criteria.put(InstitutionalProposal.PROPOSAL_ID_PROPERTY_STRING, proposalId);
        return  businessObjectService.findByPrimaryKey(InstitutionalProposal.class, criteria);
    }

    /**
     * Return the PENDING version of an Institutional Proposal, if one exists.
     * Note, PENDING here refers to the Version Status, NOT the Proposal Status of the Institutional Proposal.
     *
     * This is just a pass-through to InstitutionalProposalVersioningService, but we needed this method to be part of 
     * the module API.
     *
     * @param proposalNumber String
     * @return InstitutionalProposal, or null if a PENDING version is not found.
     * @see org.kuali.coeus.common.framework.version.VersionStatus
     */
    @Override
    public InstitutionalProposal getPendingInstitutionalProposalVersion(String proposalNumber) {
        return institutionalProposalVersioningService.getPendingInstitutionalProposalVersion(proposalNumber);
    }

    /**
     * Return the ACTIVE version of an Institutional Proposal, if one exists.
     * Note, ACTIVE here refers to the Version Status, NOT the Proposal Status of the Institutional Proposal.
     *
     * @param proposalNumber String
     * @return InstitutionalProposal, or null if a ACTIVE version is not found.
     * @see org.kuali.coeus.common.framework.version.VersionStatus
     */
    @Override
    public InstitutionalProposal getActiveInstitutionalProposalVersion(String proposalNumber) {
        return institutionalProposalVersioningService.getActiveInstitutionalProposalVersion(proposalNumber);
    }

    /**
     * Designate one or more Institutional Proposals as Funded by an Award.
     *
     * If the given Proposal has a Proposal Status of Pending, a new Final version of the Proposal
     * will be created with a Proposal Status of Funded.
     *
     * If the current Active version is already Funded, it will be left alone.
     *
     * @param proposalNumbers The proposals to update.
     * @return List&lt;InstitutionalProposal&gt; The new Funded versions.
     */
    @Override
    public List<InstitutionalProposal> fundInstitutionalProposals(Set<String> proposalNumbers) {

        List<InstitutionalProposal> updatedProposals = new ArrayList<>();

        try {
            for (String proposalNumber : proposalNumbers) {
                InstitutionalProposal activeVersion = getActiveInstitutionalProposal(proposalNumber);

                if (activeVersion != null && !ProposalStatus.FUNDED.equals(activeVersion.getStatusCode())) {

                    LOG.info("Creating a new version of proposal " + proposalNumber + ".");

                    InstitutionalProposal newVersion = versioningService.createNewVersion(activeVersion);
                    newVersion.setStatusCode(ProposalStatus.FUNDED);
                    newVersion.setAwardFundingProposals(new ArrayList<>());

                    InstitutionalProposalDocument institutionalProposalDocument =
                            (InstitutionalProposalDocument) documentService.getNewDocument(InstitutionalProposalDocument.class);

                    institutionalProposalDocument.getDocumentHeader().setDocumentDescription(
                            activeVersion.getInstitutionalProposalDocument().getDocumentHeader().getDocumentDescription());

                    institutionalProposalDocument.setInstitutionalProposal(newVersion);
                    setInstitutionalProposalIndicators(institutionalProposalDocument.getInstitutionalProposal());
                    documentService.routeDocument(institutionalProposalDocument,
                            "Update Proposal Status to Funded", new ArrayList<>());

                    updatedProposals.add(newVersion);

                } else if (activeVersion != null && ProposalStatus.FUNDED.equals(activeVersion.getStatusCode())) {
                    LOG.info("Skipped creating a new version of proposal " + proposalNumber + " - proposal is already Funded.");
                } else if (activeVersion == null) {
                    LOG.warn("Could not designate proposal " + proposalNumber + " as Funded: no Active version found.");
                }
            }

            return updatedProposals;

        } catch (WorkflowException we) {
            throw new InstitutionalProposalCreationException(WORKFLOW_EXCEPTION_MESSAGE, we);
        } catch (VersionException ve) {
            throw new InstitutionalProposalCreationException(VERSION_EXCEPTION_MESSAGE, ve);
        }
    }

    /**
     * Designate the given Proposals as no longer funded by the given Award.
     *
     * If the given Award was the only funding Award for a Proposal, a new Final version of the Proposal
     * will be created with a Proposal Status of Pending.
     *
     * If the Proposal has other funding Awards, it will be left alone.  It will also be left alone
     * if it is funded by the active version of the given award number (this is a functional requirement).
     *
     * @param proposalNumbers The proposals to update.
     * @param awardNumber The Award that is de-funding the proposal.
     * @param awardSequence The sequence number of the Award.
     * @return List&lt;InstitutionalProposal&gt; The new Pending versions.
     */
    @Override
    public List<InstitutionalProposal> defundInstitutionalProposals(Set<String> proposalNumbers, String awardNumber, Integer awardSequence) {

        List<InstitutionalProposal> updatedProposals = new ArrayList<>();

        try {
            for (String proposalNumber : proposalNumbers) {
                InstitutionalProposal activeVersion = getActiveInstitutionalProposal(proposalNumber);

                if (activeVersion != null && activeVersion.isFundedByAward(awardNumber, awardSequence)
                        && activeVersion.getActiveAwardFundingProposals().size() == 1) {
                    LOG.info("Creating a new version of proposal " + proposalNumber + ".");

                    InstitutionalProposal newVersion = versioningService.createNewVersion(activeVersion);
                    newVersion.getAwardFundingProposals().clear();
                    newVersion.setStatusCode(ProposalStatus.PENDING);

                    InstitutionalProposalDocument institutionalProposalDocument =
                            (InstitutionalProposalDocument) documentService.getNewDocument(InstitutionalProposalDocument.class);

                    institutionalProposalDocument.getDocumentHeader().setDocumentDescription(
                            activeVersion.getInstitutionalProposalDocument().getDocumentHeader().getDocumentDescription());

                    institutionalProposalDocument.setInstitutionalProposal(newVersion);
                    setInstitutionalProposalIndicators(institutionalProposalDocument.getInstitutionalProposal());
                    documentService.routeDocument(institutionalProposalDocument,
                            "Update Proposal Status to Pending", new ArrayList<>());

                    updatedProposals.add(newVersion);

                } else {
                    LOG.info("Skipped setting proposal " + proposalNumber + " to Pending. It is either funded by another Award, "
                            + "another version of Award " + awardNumber + ", or no active version found.");
                }
            }

            return updatedProposals;

        } catch (WorkflowException we) {
            throw new InstitutionalProposalCreationException(WORKFLOW_EXCEPTION_MESSAGE, we);
        } catch (VersionException ve) {
            throw new InstitutionalProposalCreationException(VERSION_EXCEPTION_MESSAGE, ve);
        }
    }

    @Override
    public List<InstitutionalProposal> getProposalsForProposalNumber(String proposalNumber) {
        return new ArrayList<>(businessObjectService.findMatchingOrderBy(InstitutionalProposal.class,
                Collections.singletonMap(PROPOSAL_NUMBER, proposalNumber),
                SEQUENCE_NUMBER,
                true));
    }

    @Override
    public List<DevelopmentProposal> getAllLinkedDevelopmentProposals(String proposalNumber) {
        List<DevelopmentProposal> result = new ArrayList<>();
        List<InstitutionalProposal> proposals = getProposalsForProposalNumber(proposalNumber);
        for (InstitutionalProposal curProposal : proposals) {
            List<ProposalAdminDetails> details = new ArrayList<>(businessObjectService.findMatching(ProposalAdminDetails.class,
                    Collections.singletonMap(INST_PROPOSAL_ID, curProposal.getProposalId())));
            for (ProposalAdminDetails detail : details) {
                result.add(dataObjectService.find(DevelopmentProposal.class, detail.getDevProposalNumber()));
            }
        }
        return result;
    }

    @Override
    public String getNextInstitutionalProposalNumber() {

        boolean fyBasedIp = getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, ParameterConstants.DOCUMENT_COMPONENT, Constants.FISCAL_YEAR_BASED_IP);

        return fyBasedIp ? getNextIPFiscalYearBased() :
                new DecimalFormat(DECIMAL_FORMAT).format(sequenceAccessorService.getNextAvailableSequenceNumber(Constants.INSTITUTIONAL_PROPSAL_PROPSAL_NUMBER_SEQUENCE, InstitutionalProposal.class));
    }

    private String getNextIPFiscalYearBased() {
        String fiscalYear =  StringUtils.substring(this.getFiscalYearMonthService().getCurrentFiscalYear().toString(),2,4);
        String fiscalMonth = StringUtils.leftPad(this.getFiscalYearMonthService().getCurrentFiscalMonthForDisplay().toString(), 2, "0");
        Long nextProposalNumberSeq = sequenceAccessorService.getNextAvailableSequenceNumber(Constants.IP_NUMBER_SEQUENCE_FY_BASED, InstitutionalProposal.class);
        return fiscalYear + fiscalMonth + new DecimalFormat(DECIMAL_FORMAT_FOR_NEW_IP).format(nextProposalNumberSeq);
    }

    protected InstitutionalProposal getActiveInstitutionalProposal(String proposalNumber) {
        Map<String, String> criteria = new HashMap<>();
        criteria.put(InstitutionalProposal.PROPOSAL_NUMBER_PROPERTY_STRING, proposalNumber);
        criteria.put(InstitutionalProposal.PROPOSAL_SEQUENCE_STATUS_PROPERTY_STRING, VersionStatus.ACTIVE.toString());
        Collection results = businessObjectService.findMatching(InstitutionalProposal.class, criteria);
        if (results.isEmpty()) {
            return null;
        }

        return (InstitutionalProposal) results.toArray()[0];
    }

    protected InstitutionalProposalDocument versionProposal(String proposalNumber, DevelopmentProposal developmentProposal, Budget budget)
            throws VersionException, WorkflowException {

        InstitutionalProposal currentVersion = getActiveInstitutionalProposal(proposalNumber);
        if (currentVersion == null) {
            throw new RuntimeException(NO_PRIOR_VERSION_MESSAGE);
        }
        ObjectUtils.materializeObjects(currentVersion.getInstitutionalProposalScienceKeywords());
        InstitutionalProposal newVersion = versioningService.createNewVersion(currentVersion);
        return mergeProposals(newVersion, developmentProposal, budget);
    }

    protected InstitutionalProposalDocument mergeProposals(InstitutionalProposal institutionalProposal, DevelopmentProposal developmentProposal, Budget budget)
            throws WorkflowException {

        InstitutionalProposalDocument institutionalProposalDocument =
                (InstitutionalProposalDocument) documentService.getNewDocument(InstitutionalProposalDocument.class);

        institutionalProposalDocument.getDocumentHeader().setDocumentDescription(
                NEW_DOCUMENT_DESCRIPTION + developmentProposal.getProposalNumber());

        institutionalProposalDocument.setInstitutionalProposal(institutionalProposal);

        doBaseFieldsDataFeed(institutionalProposal, developmentProposal);
        doCustomAttributeDataFeed(institutionalProposalDocument, developmentProposal);

        institutionalProposal.getProjectPersons().clear();
        for (ProposalPerson pdPerson : developmentProposal.getProposalPersons()) {
            institutionalProposal.add(generateInstitutionalProposalPerson(pdPerson));
        }

        institutionalProposal.getSpecialReviews().clear();
        for (ProposalSpecialReview dpSpecialReview : developmentProposal.getPropSpecialReviews()) {
            institutionalProposal.addSpecialReview(generateIpSpecialReview(dpSpecialReview));
        }
        if (!institutionalProposal.getSpecialReviews().isEmpty()) {
            institutionalProposal.setSpecialReviewIndicator(TRUE_INDICATOR_VALUE);
        }

        institutionalProposal.getInstitutionalProposalScienceKeywords().clear();
        for (PropScienceKeyword dpKeyword : developmentProposal.getPropScienceKeywords()) {
            institutionalProposal.addKeyword(dpKeyword.getScienceKeyword());
        }
        if (!institutionalProposal.getInstitutionalProposalScienceKeywords().isEmpty()) {
            institutionalProposal.setScienceCodeIndicator(TRUE_INDICATOR_VALUE);
        }

        if (budget != null) {
            doBudgetDataFeed(institutionalProposal, budget);
        }
        institutionalProposal.refreshNonUpdateableReferences();

        return institutionalProposalDocument;
    }

    protected void doBaseFieldsDataFeed(InstitutionalProposal institutionalProposal, DevelopmentProposal developmentProposal) {
    	if (institutionalProposal.getSequenceNumber() == VERSION_1) {
			institutionalProposal.setProposalTypeCode(convertToInstitutionalProposalTypeCode(developmentProposal.getProposalTypeCode()));
		}
        institutionalProposal.setActivityTypeCode(developmentProposal.getActivityTypeCode());
        if (developmentProposal.getProposalDocument().getDocumentHeader().getWorkflowDocument().isDisapproved()) {
            //if rejected set status code to WITHDRAWN
            institutionalProposal.setStatusCode(getWithdrawnStatusCode());
        } else {
            institutionalProposal.setStatusCode(getDefaultStatusCode());
        }
        institutionalProposal.setSponsorCode(developmentProposal.getSponsorCode());
        institutionalProposal.setTitle(developmentProposal.getTitle());
        institutionalProposal.setSubcontractFlag(developmentProposal.getSubcontracts() != null ? developmentProposal.getSubcontracts() : false);
        institutionalProposal.setRequestedStartDateTotal(developmentProposal.getRequestedStartDateInitial());
        institutionalProposal.setRequestedEndDateTotal(developmentProposal.getRequestedEndDateInitial());
        institutionalProposal.setDeadlineDate(developmentProposal.getDeadlineDate());
        institutionalProposal.setDeadlineTime(developmentProposal.getDeadlineTime());
        institutionalProposal.setNoticeOfOpportunityCode(developmentProposal.getNoticeOfOpportunityCode());
        institutionalProposal.setNumberOfCopies(developmentProposal.getNumberOfCopies());
        institutionalProposal.setDeadlineType(developmentProposal.getDeadlineType());
        institutionalProposal.setMailBy(developmentProposal.getMailBy());
        institutionalProposal.setMailType(developmentProposal.getMailType());
        institutionalProposal.setMailAccountNumber(developmentProposal.getMailAccountNumber());
        institutionalProposal.setMailDescription(developmentProposal.getMailDescription());
        institutionalProposal.setPrimeSponsorCode(developmentProposal.getPrimeSponsorCode());
        institutionalProposal.setCurrentAwardNumber(developmentProposal.getCurrentAwardNumber());
        institutionalProposal.setCfdaNumber(developmentProposal.getCfdaNumber());
        institutionalProposal.setNewDescription(developmentProposal.getNewDescription());
        institutionalProposal.setNoticeOfOpportunityCode(developmentProposal.getNoticeOfOpportunityCode());
        institutionalProposal.setNsfSequenceNumber(developmentProposal.getNsfSequenceNumber());
        institutionalProposal.setSponsorProposalNumber(developmentProposal.getSponsorProposalNumber());
        institutionalProposal.setOpportunity(developmentProposal.getProgramAnnouncementNumber());
        institutionalProposal.setCfdaNumber(developmentProposal.getCfdaNumber());
        institutionalProposal.setLeadUnitNumber(developmentProposal.getUnitNumber());
        institutionalProposal.setDefaultInitialContractAdmin();
        if (developmentProposal.getRolodex() != null) {
            institutionalProposal.setRolodexId(developmentProposal.getRolodex().getRolodexId());
        }
        if(developmentProposal.getAnticipatedAwardType()!=null) {
            institutionalProposal.setAwardTypeCode(developmentProposal.getAnticipatedAwardType().getCode());
        }
    }

    private int convertToInstitutionalProposalTypeCode(String developmentProposalTypeCode) {
        if(StringUtils.equals(developmentProposalTypeCode, getProposalTypeService().getNewChangedOrCorrectedProposalTypeCode())) {
            return Integer.parseInt(getProposalTypeService().getNewProposalTypeCode());
        } else if(StringUtils.equals(developmentProposalTypeCode,getProposalTypeService().getSupplementChangedOrCorrectedProposalTypeCode())) {
            return  Integer.parseInt(getProposalTypeService().getContinuationProposalTypeCode());
        } else if(StringUtils.equals(developmentProposalTypeCode,getProposalTypeService().getRenewalChangedOrCorrectedProposalTypeCode())) {
            return Integer.parseInt(getProposalTypeService().getRenewProposalTypeCode());
        } else {
            return Integer.parseInt(developmentProposalTypeCode);
        }
    }

    protected void doCustomAttributeDataFeed(InstitutionalProposalDocument institutionalProposalDocument, DevelopmentProposal developmentProposal) throws WorkflowException {
        Map<String, CustomAttributeDocument> dpCustomAttributes = developmentProposal.getProposalDocument().getCustomAttributeDocuments();
        Map<String, CustomAttributeDocument> ipCustomAttributes = institutionalProposalDocument.getCustomAttributeDocuments();
        List<InstitutionalProposalCustomData> ipCustomDataList = institutionalProposalDocument.getInstitutionalProposal().getInstitutionalProposalCustomDataList();
        InstitutionalProposalCustomData ipCustomData;
        CustomAttributeDocument dpCustomAttributeDocument;
        for (String key : dpCustomAttributes.keySet()) {
            if (ipCustomAttributes.containsKey(key)) {
                dpCustomAttributeDocument = dpCustomAttributes.get(key);
                ipCustomAttributes.put(key, dpCustomAttributeDocument);
                ipCustomData = new InstitutionalProposalCustomData();
                ipCustomData.setCustomAttribute(new CustomAttribute());
                ipCustomData.getCustomAttribute().setId(dpCustomAttributeDocument.getId());
                ipCustomData.setCustomAttributeId(dpCustomAttributeDocument.getId());
                ipCustomData.setInstitutionalProposal(institutionalProposalDocument.getInstitutionalProposal());
                ipCustomData.setValue(getCustomAttributeValue(developmentProposal.getProposalDocument().getCustomDataList(),key));
                ipCustomDataList.add(ipCustomData);
            }
        }
    }

    protected String getCustomAttributeValue(List<CustomAttributeDocValue> values, String key) {
        for (CustomAttributeDocValue value : values) {
            if (StringUtils.equals(String.valueOf(value.getId()), key)) {
                return value.getValue();
            }
        }
        return null;
    }

    protected boolean getOptIn() {
        return getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, Constants
                .KC_ALL_PARAMETER_DETAIL_TYPE_CODE, CreditSplitConstants.ENABLE_OPT_IN_PERSONNEL_CREDIT_SPLIT_FUNCTIONALITY);
    }

    protected InstitutionalProposalPerson generateInstitutionalProposalPerson(ProposalPerson pdPerson) {
        InstitutionalProposalPerson ipPerson = new InstitutionalProposalPerson();
        ipPerson.setIncludeInCreditAllocation(pdPerson.getIncludeInCreditAllocation());
        if (ObjectUtils.isNotNull(pdPerson.getPersonId())) {
            ipPerson.setPersonId(pdPerson.getPersonId());
        }
        if (ObjectUtils.isNotNull(pdPerson.getRolodexId())) {
            ipPerson.setRolodexId(pdPerson.getRolodexId());
        }
        ipPerson.setContactRoleCode(pdPerson.getRole().getRoleCode());
        final boolean generateCreditSplit = BooleanUtils.isTrue(getInstitutionalProposalPersonService().generateCreditSplitForPerson(ipPerson));
        if (generateCreditSplit) {
            for (ProposalPersonCreditSplit pdPersonCreditSplit : pdPerson.getCreditSplits()) {
                InstitutionalProposalPersonCreditSplit ipPersonCreditSplit = new InstitutionalProposalPersonCreditSplit();
                ipPersonCreditSplit.setCredit(pdPersonCreditSplit.getCredit());
                ipPersonCreditSplit.setInvCreditTypeCode(pdPersonCreditSplit.getInvCreditTypeCode());
                ipPersonCreditSplit.setNewCollectionRecord(pdPersonCreditSplit.isNewCollectionRecord());
                ipPerson.add(ipPersonCreditSplit);
            }
        }

        for (ProposalPersonUnit pdPersonUnit : pdPerson.getUnits()) {
            InstitutionalProposalPersonUnit ipPersonUnit = new InstitutionalProposalPersonUnit();
            ipPersonUnit.setLeadUnit(pdPersonUnit.isLeadUnit());
            ipPersonUnit.setNewCollectionRecord(pdPersonUnit.isNewCollectionRecord());
            ipPersonUnit.setUnitNumber(pdPersonUnit.getUnitNumber());
            if (generateCreditSplit) {
                for (ProposalUnitCreditSplit pdPersonCreditSplit : pdPersonUnit.getCreditSplits()) {
                    InstitutionalProposalPersonUnitCreditSplit ipPersonUnitCreditSplit = new InstitutionalProposalPersonUnitCreditSplit();
                    ipPersonUnitCreditSplit.setCredit(pdPersonCreditSplit.getCredit());
                    ipPersonUnitCreditSplit.setInvCreditTypeCode(pdPersonCreditSplit.getInvCreditTypeCode());
                    ipPersonUnitCreditSplit.setNewCollectionRecord(pdPersonCreditSplit.isNewCollectionRecord());
                    ipPersonUnit.add(ipPersonUnitCreditSplit);
                }
            }
            ipPerson.add(ipPersonUnit);
        }

        ipPerson.setFaculty(pdPerson.getFacultyFlag());
        ipPerson.setFullName(pdPerson.getFullName());
        ipPerson.setKeyPersonRole(pdPerson.getProjectRole());
        ipPerson.setNewCollectionRecord(pdPerson.isNewCollectionRecord());
        ipPerson.setRoleCode(pdPerson.getRole().getRoleCode());
        ipPerson.setTotalEffort(pdPerson.getPercentageEffort());
        ipPerson.setAcademicYearEffort(pdPerson.getAcademicYearEffort());
        ipPerson.setCalendarYearEffort(pdPerson.getCalendarYearEffort());
        ipPerson.setSummerEffort(pdPerson.getSummerEffort());


        return ipPerson;
    }

    protected InstitutionalProposalSpecialReview generateIpSpecialReview(ProposalSpecialReview dpSpecialReview) {
        InstitutionalProposalSpecialReview ipSpecialReview = new InstitutionalProposalSpecialReview();
        ipSpecialReview.setApplicationDate(dpSpecialReview.getApplicationDate());
        ipSpecialReview.setApprovalDate(dpSpecialReview.getApprovalDate());
        ipSpecialReview.setApprovalTypeCode(dpSpecialReview.getApprovalTypeCode());
        ipSpecialReview.setComments(dpSpecialReview.getComments());
        ipSpecialReview.setExpirationDate(dpSpecialReview.getExpirationDate());
        ipSpecialReview.setProtocolNumber(dpSpecialReview.getProtocolNumber());
        ipSpecialReview.setSpecialReviewType(dpSpecialReview.getSpecialReviewType());
        ipSpecialReview.setApprovalType(dpSpecialReview.getApprovalType());
        ipSpecialReview.setSpecialReviewTypeCode(dpSpecialReview.getSpecialReviewTypeCode());
        ipSpecialReview.setSpecialReviewNumber(dpSpecialReview.getSpecialReviewNumber());
        for (String dpExempt : dpSpecialReview.getExemptionTypeCodes()) {
            InstitutionalProposalSpecialReviewExemption newIpSpecialReviewExemption = ipSpecialReview.createSpecialReviewExemption(dpExempt);
            ipSpecialReview.getSpecialReviewExemptions().add(newIpSpecialReviewExemption);
            ipSpecialReview.getExemptionTypeCodes().add(dpExempt);
        }
        return ipSpecialReview;
    }

    protected void doBudgetDataFeed(InstitutionalProposal institutionalProposal, Budget budget) {
        // Base fields from Budget
        institutionalProposal.setRequestedStartDateInitial(budget.getBudgetPeriods().get(0).getStartDate());
        institutionalProposal.setRequestedEndDateInitial(budget.getBudgetPeriods().get(0).getEndDate());

        if (budget.getModularBudgetFlag()) {
            if (budget.getBudgetPeriod(0) != null &&
                    budget.getBudgetPeriod(0).getBudgetModular() != null &&
                    budget.getBudgetPeriod(0).getBudgetModular().getTotalDirectCost() != null) {
                institutionalProposal.setTotalDirectCostInitial(
                        new ScaleTwoDecimal(budget.getBudgetPeriod(0).getBudgetModular().getTotalDirectCost().bigDecimalValue()));
            }
            budget.getBudgetPeriod(0).getBudgetModular().calculateTotalFnaRequested();
            if (budget.getBudgetPeriod(0).getBudgetModular().getTotalFnaRequested() != null) {
                institutionalProposal.setTotalIndirectCostInitial(
                        new ScaleTwoDecimal(budget.getBudgetPeriod(0).getBudgetModular().getTotalFnaRequested().bigDecimalValue()));
            }

            ScaleTwoDecimal totalDirect = new ScaleTwoDecimal(0);
            ScaleTwoDecimal totalIndirect = new ScaleTwoDecimal(0);
            for (BudgetPeriod bp : budget.getBudgetPeriods()) {
                if (bp.getBudgetModular() != null) {
                    if (bp.getBudgetModular().getTotalDirectCost() != null) {
                        totalDirect = totalDirect.add(bp.getBudgetModular().getTotalDirectCost());
                    }
                    bp.getBudgetModular().calculateTotalFnaRequested();
                    if (bp.getBudgetModular().getTotalFnaRequested() != null) {
                        totalIndirect = totalIndirect.add(bp.getBudgetModular().getTotalFnaRequested());
                    }
                }
            }
            institutionalProposal.setTotalDirectCostTotal(new ScaleTwoDecimal(totalDirect.bigDecimalValue()));
            institutionalProposal.setTotalIndirectCostTotal(new ScaleTwoDecimal(totalIndirect.bigDecimalValue()));

        } else {
            institutionalProposal.setTotalDirectCostInitial(new ScaleTwoDecimal(budget.getBudgetPeriod(0).getTotalDirectCost().bigDecimalValue()));
            institutionalProposal.setTotalIndirectCostInitial(new ScaleTwoDecimal(budget.getBudgetPeriod(0).getTotalIndirectCost().bigDecimalValue()));
            institutionalProposal.setTotalDirectCostTotal(new ScaleTwoDecimal(budget.getTotalDirectCost().bigDecimalValue()));
            institutionalProposal.setTotalIndirectCostTotal(new ScaleTwoDecimal(budget.getTotalIndirectCost().bigDecimalValue()));
        }

        populateCostSharesFromBudget(institutionalProposal, budget);

        populateUnrecoveredFandA(institutionalProposal, budget);

        copyIndirectRates(institutionalProposal, budget);

    }

    private void populateUnrecoveredFandA(InstitutionalProposal institutionalProposal, Budget budget) {
        institutionalProposal.getInstitutionalProposalUnrecoveredFandAs().clear();
        for (BudgetUnrecoveredFandA budgetUfa : budget.getBudgetUnrecoveredFandAs()) {
            InstitutionalProposalUnrecoveredFandA ipUfa = new InstitutionalProposalUnrecoveredFandA();
            ipUfa.setApplicableIndirectcostRate(new ScaleTwoDecimal(budgetUfa.getApplicableRate().bigDecimalValue()));
            ipUfa.setFiscalYear(budgetUfa.getFiscalYear().toString());
            ipUfa.setOnCampusFlag(ACTIVE_VALUE.equals(budgetUfa.getOnCampusFlag()));
            ipUfa.setSourceAccount(budgetUfa.getSourceAccount());
            ipUfa.setIndirectcostRateTypeCode(Integer.parseInt(budget.getOhRateClassCode()));
            ipUfa.setUnderrecoveryOfIndirectcost(new ScaleTwoDecimal(budgetUfa.getAmount().bigDecimalValue()));
            institutionalProposal.add(ipUfa);
        }
        if (!institutionalProposal.getInstitutionalProposalUnrecoveredFandAs().isEmpty()) {
            institutionalProposal.setIdcRateIndicator(TRUE_INDICATOR_VALUE);
        }
    }

    private void populateCostSharesFromBudget(InstitutionalProposal institutionalProposal, Budget budget) {
        institutionalProposal.getInstitutionalProposalCostShares().clear();
        for (BudgetCostShare budgetCostShare : budget.getBudgetCostShares()) {
            InstitutionalProposalCostShare ipCostShare = new InstitutionalProposalCostShare();
            ipCostShare.setCostShareTypeCode(getCostShareTypeCode(budgetCostShare));
            if (isCostShareTypeEnabled()) {
                ProposalDevelopmentBudgetExt budgetExt = (ProposalDevelopmentBudgetExt) budgetCostShare.getBudget();
                institutionalProposal.getCostShareComment().setComments(budgetExt.getCostShareComment());
            }
            ipCostShare.setAmount(new ScaleTwoDecimal(budgetCostShare.getShareAmount().bigDecimalValue()));
            ipCostShare.setCostSharePercentage(new ScaleTwoDecimal(budgetCostShare.getSharePercentage().bigDecimalValue()));
            ipCostShare.setProjectPeriod(budgetCostShare.getProjectPeriod().toString());
            ipCostShare.setSourceAccount(budgetCostShare.getSourceAccount());
            ipCostShare.setUnitNumber(budgetCostShare.getUnitNumber());
            ipCostShare.setUnit(budgetCostShare.getUnit());
            institutionalProposal.add(ipCostShare);
        }
        if (!institutionalProposal.getInstitutionalProposalCostShares().isEmpty()) {
            institutionalProposal.setCostSharingIndicator(TRUE_INDICATOR_VALUE);
        }
    }

    protected Integer getCostShareTypeCode(BudgetCostShare budgetCostShare) {
        if (isCostShareTypeEnabled()) {
            return budgetCostShare.getCostShareTypeCode() == null ? getCostShareTypeDefault() : budgetCostShare.getCostShareTypeCode();
        }
        return DEFAULT_COST_SHARE_TYPE_CODE;
    }

    protected void copyIndirectRates(InstitutionalProposal institutionalProposal, Budget budget) {
        if (isLifecyleRatesFlowthruEnabled() && budget.getBudgetRates() != null) {
            budget.getBudgetRates().stream().filter(budgetRate -> budgetRate.getRateClass().getRateClassTypeCode().equalsIgnoreCase(RateClassType.OVERHEAD.getRateClassType()) &&
                    budgetRate.getRateClassCode().equalsIgnoreCase(budget.getRateClass().getCode())).forEach(budgetRate -> {
                        InstitutionalProposalFandA fandA = new InstitutionalProposalFandA();
                        fandA.setRateTypeCode(budgetRate.getRateClassCode());
                        fandA.setActivityTypeCode(budgetRate.getActivityTypeCode());
                        fandA.setFiscalYear(budgetRate.getFiscalYear());
                        fandA.setApplicableRate(budgetRate.getApplicableRate());
                        fandA.setInstituteRate(budgetRate.getInstituteRate());
                        fandA.setOnOffCampusFlag(budgetRate.getOnOffCampusFlag());
                        fandA.setStartDate(budgetRate.getStartDate());
                        institutionalProposal.add(fandA);
                    }
            );
        }
    }

    protected Integer getDefaultStatusCode() {
        return DEFAULT_STATUS_CODE;
    }

    protected Integer getWithdrawnStatusCode() {
        return WITHDRAWN_STATUS_CODE;
    }

    protected boolean isLifecyleRatesFlowthruEnabled() {
        return parameterService.getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_INSITUTIONAL_PROPOSAL,
                ParameterConstants.ALL_COMPONENT,
                Constants.ENABLE_LIFECYCLE_RATES_FLOWTHRU);
    }

    protected boolean isCostShareTypeEnabled() {
        return parameterService.getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_GEN,
                ParameterConstants.ALL_COMPONENT,
                Constants.ENABLE_COST_SHARE_ACCOUNT_VALIDATION);
    }

    protected Integer getCostShareTypeDefault() {
        String costSharetype = parameterService.getParameterValueAsString(Constants.MODULE_NAMESPACE_INSITUTIONAL_PROPOSAL,
                ParameterConstants.ALL_COMPONENT,
                Constants.DEFAULT_IP_AWARD_COST_SHARE_TYPE);
        return !StringUtils.isEmpty(costSharetype) ? Integer.parseInt(costSharetype) : null;
    }

    @Override
    public InstitutionalProposalDocument createAndSaveNewVersion(InstitutionalProposal currentInstitutionalProposal,
                                                                 InstitutionalProposalDocument currentInstitutionalProposalDocument) throws VersionException, WorkflowException {
        InstitutionalProposal newVersion = getVersioningService().createNewVersion(currentInstitutionalProposal);
        syncNewCustomAttributes(newVersion, currentInstitutionalProposal);

        newVersion.setProposalSequenceStatus(VersionStatus.PENDING.toString());
        newVersion.setAwardFundingProposals(null);
        InstitutionalProposalDocument newInstitutionalProposalDocument =
                (InstitutionalProposalDocument) getDocumentService().getNewDocument(InstitutionalProposalDocument.class);
        newInstitutionalProposalDocument.getDocumentHeader().setDocumentDescription(currentInstitutionalProposalDocument.getDocumentHeader().getDocumentDescription());
        newInstitutionalProposalDocument.setInstitutionalProposal(newVersion);
        getDocumentService().saveDocument(newInstitutionalProposalDocument);
        return newInstitutionalProposalDocument;
    }

    /**
     * This method is to synch custom attributes. During version process only existing custom attributes
     * available in the old document is copied. We need to make sure we have all the latest custom attributes
     * tied to the new document.
     */
    protected void syncNewCustomAttributes(InstitutionalProposal newInstitutionalProposal, InstitutionalProposal oldInstitutionalProposal) {
        final Set<Integer> availableCustomAttributes = newInstitutionalProposal.getInstitutionalProposalCustomDataList().stream().map(customData -> customData.getCustomAttributeId().intValue()).collect(Collectors.toSet());

        if(oldInstitutionalProposal.getInstitutionalProposalDocument() != null) {
            Map<String, CustomAttributeDocument> customAttributeDocuments = oldInstitutionalProposal.getInstitutionalProposalDocument().getCustomAttributeDocuments();
            for (Map.Entry<String, CustomAttributeDocument> entry : customAttributeDocuments.entrySet()) {
                CustomAttributeDocument customAttributeDocument = entry.getValue();
                if(!availableCustomAttributes.contains(customAttributeDocument.getId().intValue())) {
                    InstitutionalProposalCustomData customData = new InstitutionalProposalCustomData();
                    customData.setCustomAttributeId(customAttributeDocument.getId());
                    customData.setCustomAttribute(customAttributeDocument.getCustomAttribute());
                    customData.setValue("");
                    customData.setInstitutionalProposal(newInstitutionalProposal);
                    newInstitutionalProposal.getInstitutionalProposalCustomDataList().add(customData);
                }
            }
        }
    }

    /**
     * This function verifies that the indicator fields are set, and if they aren't sets them.
     * If an IP is being versioned and the home unit or cost shares allocations are changed, there may be problems with these fields.
     */
    protected void setInstitutionalProposalIndicators(InstitutionalProposal institutionalProposal) {
        if (!institutionalProposal.getInstitutionalProposalCostShares().isEmpty()) {
            institutionalProposal.setCostSharingIndicator(TRUE_INDICATOR_VALUE);
        } else {
            institutionalProposal.setCostSharingIndicator(FALSE_INDICATOR_VALUE);
        }
        if (!institutionalProposal.getSpecialReviews().isEmpty()) {
            institutionalProposal.setSpecialReviewIndicator(TRUE_INDICATOR_VALUE);
        } else {
            institutionalProposal.setSpecialReviewIndicator(FALSE_INDICATOR_VALUE);
        }
        if (!institutionalProposal.getInstitutionalProposalUnrecoveredFandAs().isEmpty()) {
            institutionalProposal.setIdcRateIndicator(TRUE_INDICATOR_VALUE);
        } else {
            institutionalProposal.setIdcRateIndicator(FALSE_INDICATOR_VALUE);
        }
        if (!institutionalProposal.getInstitutionalProposalScienceKeywords().isEmpty()) {
            institutionalProposal.setScienceCodeIndicator(TRUE_INDICATOR_VALUE);
        } else {
            institutionalProposal.setScienceCodeIndicator(FALSE_INDICATOR_VALUE);
        }
    }

    public InstitutionalProposalPersonService getInstitutionalProposalPersonService() {
        return institutionalProposalPersonService;
    }

    public void setInstitutionalProposalPersonService(InstitutionalProposalPersonService institutionalProposalPersonService) {
        this.institutionalProposalPersonService = institutionalProposalPersonService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setVersioningService(VersioningService versioningService) {
        this.versioningService = versioningService;
    }

    public void setInstitutionalProposalVersioningService(InstitutionalProposalVersioningService institutionalProposalVersioningService) {
        this.institutionalProposalVersioningService = institutionalProposalVersioningService;
    }

    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }

    @Override
    public Collection<String> getValidFundingProposalStatusCodes() {
        String value = getParameterService().getParameterValueAsString(InstitutionalProposalDocument.class, VALID_FUNDING_PROPOSAL_STATUS_CODES);
        return Arrays.asList(value.split(SEPARATOR));
    }

    @Override
    public Long getProposalId(Award award) {
        return getInstitutionalProposalDao().getProposalId(award);
    }

    protected ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public VersioningService getVersioningService() {
        return versioningService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    public InstitutionalProposalDao getInstitutionalProposalDao() {
        return institutionalProposalDao;
    }

    public void setInstitutionalProposalDao(InstitutionalProposalDao institutionalProposalDao) {
        this.institutionalProposalDao = institutionalProposalDao;
    }

    public ProjectRetrievalService getInstPropProjectRetrievalService() {
        return instPropProjectRetrievalService;
    }

    public void setInstPropProjectRetrievalService(ProjectRetrievalService instPropProjectRetrievalService) {
        this.instPropProjectRetrievalService = instPropProjectRetrievalService;
    }

    public void setProposalTypeService(ProposalTypeService proposalTypeService) {
        this.proposalTypeService = proposalTypeService;
    }


}
