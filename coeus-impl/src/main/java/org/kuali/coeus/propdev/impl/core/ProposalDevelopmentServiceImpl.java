/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.core;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.coi.framework.Project;
import org.kuali.coeus.coi.framework.ProjectPublisher;
import org.kuali.coeus.coi.framework.ProjectRetrievalService;
import org.kuali.coeus.common.budget.framework.core.Budget;
import org.kuali.coeus.common.framework.auth.SystemAuthorizationService;
import org.kuali.coeus.common.framework.unit.Unit;
import org.kuali.coeus.common.framework.unit.UnitService;
import org.kuali.coeus.propdev.impl.location.ProposalSite;
import org.kuali.coeus.propdev.impl.person.ProposalPersonCertificationDetails;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.kra.institutionalproposal.proposaladmindetails.ProposalAdminDetails;
import org.kuali.kra.kim.bo.KcKimAttributes;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("proposalDevelopmentService")
public class ProposalDevelopmentServiceImpl implements ProposalDevelopmentService {

    public static final String PROPOSAL_NUMBER = "proposalNumber";
    protected final Logger LOG = LogManager.getLogger(ProposalDevelopmentServiceImpl.class);

    @Autowired
    @Qualifier("businessObjectService")
    private BusinessObjectService businessObjectService;

    @Autowired
    @Qualifier("dataObjectService")
    private DataObjectService dataObjectService;

    @Autowired
    @Qualifier("parameterService")
    private ParameterService parameterService;

    @Autowired
    @Qualifier("documentService")
    private DocumentService documentService;

    @Autowired
    @Qualifier("roleService")
    private RoleService roleService;

    @Autowired
    @Qualifier("unitService")
    private UnitService unitService;

    @Autowired
    @Qualifier("systemAuthorizationService")
    private SystemAuthorizationService systemAuthorizationService;

    @Autowired
    @Qualifier("proposalTypeService")
    private ProposalTypeService proposalTypeService;

    @Autowired
    @Qualifier("propDevProjectRetrievalService")
    private ProjectRetrievalService projectRetrievalService;

    private ProjectPublisher projectPublisher;

    /**
     * This method gets called from the "save" action. It initializes the applicant org. on the first save; it also sets the
     * performing org. if the user didn't make a selection.
     */
    @Override
    public void initializeUnitOrganizationLocation(ProposalDevelopmentDocument proposalDevelopmentDocument) {
        ProposalSite applicantOrganization = proposalDevelopmentDocument.getDevelopmentProposal().getApplicantOrganization();
        DevelopmentProposal developmentProposal = proposalDevelopmentDocument.getDevelopmentProposal();

        // Unit number chosen, set Applicant Organization
        if (developmentProposal.getOwnedByUnitNumber() != null &&
                (applicantOrganization == null || applicantOrganization.getOrganization() == null)) {
            // get Lead Unit details
            developmentProposal.refreshReferenceObject("ownedByUnit");
            String applicantOrganizationId = developmentProposal.getOwnedByUnit().getOrganizationId();

            // get Organzation assoc. w/ Lead Unit, set applicant org
            applicantOrganization = createProposalSite(applicantOrganizationId);
            applicantOrganization.setDevelopmentProposal(developmentProposal);
            developmentProposal.setApplicantOrganization(applicantOrganization);
        }

        // On first save, set Performing Organization if not selected
        ProposalSite performingOrganization = developmentProposal.getPerformingOrganization();
        if (StringUtils.isEmpty(developmentProposal.getProposalNumber()) && performingOrganization.getOrganization() == null
                && developmentProposal.getOwnedByUnitNumber() != null) {
            String performingOrganizationId = developmentProposal.getOwnedByUnit().getOrganizationId();
            performingOrganization = createProposalSite(performingOrganizationId);
            performingOrganization.setDevelopmentProposal(developmentProposal);
            developmentProposal.setPerformingOrganization(performingOrganization);
        }
    }

    /**
     * Constructs a ProposalSite; initializes the organization, and locationName fields, and sets the default district if there is
     * one defined for the Organization.
     *
     */
    protected ProposalSite createProposalSite(String organizationId) {
        ProposalSite proposalSite = new ProposalSite();
        proposalSite.setOrganizationId(organizationId);
        proposalSite.initializeDefaultCongressionalDistrict();
        return proposalSite;
    }

    protected int getNextSiteNumber(ProposalDevelopmentDocument proposalDevelopmentDocument) {
        return proposalDevelopmentDocument.getDocumentNextValue(Constants.PROPOSAL_LOCATION_SEQUENCE_NUMBER);
    }

    @Override
    public void initializeProposalSiteNumbers(ProposalDevelopmentDocument proposalDevelopmentDocument) {
        for (ProposalSite proposalSite : proposalDevelopmentDocument.getDevelopmentProposal().getProposalSites()){
            if (proposalSite.getSiteNumber() == null) {
                proposalSite.setSiteNumber(getNextSiteNumber(proposalDevelopmentDocument));
            }
        	if (proposalSite.getDevelopmentProposal() == null) {
        		proposalSite.setDevelopmentProposal(proposalDevelopmentDocument.getDevelopmentProposal());
        	}
        	// Force updates of Organization/Rolodex related data.
        	if (proposalSite.getOrganizationId() != null) {
        	    proposalSite.setOrganizationId(proposalSite.getOrganizationId());
            }
            if (proposalSite.getRolodexId() != null) {
                proposalSite.setRolodexId(proposalSite.getRolodexId());
            }
        }   
    }

    @Override
    public List<Unit> getDefaultModifyProposalUnitsForUser(String userId) {
        return getUnitsForCreateProposal(userId);
    }

    protected String getPropertyValue(BusinessObject businessObject, String fieldName) {
        String displayValue = "";
        try {
            displayValue = (String) ObjectUtils.getPropertyValue(businessObject, fieldName);
        }
        // Might happen due to Unknown Property Exception
        catch (RuntimeException e) {
            LOG.warn(e.getMessage(), e);
        }
        return displayValue;
    }

    @Override
    public boolean isGrantsGovEnabledForProposal(DevelopmentProposal devProposal) {
        String federalSponsorTypeCode = getParameterService().getParameterValueAsString(AwardDocument.class, Constants.FEDERAL_SPONSOR_TYPE_CODE);
        return !devProposal.isChild() && devProposal.getSponsor() != null
                && StringUtils.equals(devProposal.getSponsor().getSponsorTypeCode(), federalSponsorTypeCode);
    }


    private void handleProjectPush(String sourceIdentifier) {
        Project project = projectRetrievalService.retrieveProject(sourceIdentifier);
        project.setActive(false);
        getProjectPublisher().publishProject(project);
    }

    @Override
    public ProposalDevelopmentDocument deleteProposal(ProposalDevelopmentDocument proposalDocument) throws WorkflowException {

        handleProjectPush(proposalDocument.getDevelopmentProposal().getProposalNumber());
        deleteCertDetails(proposalDocument.getDevelopmentProposal());
        proposalDocument.getDevelopmentProposal().setFinalBudget(null);
        dataObjectService.delete(proposalDocument.getDevelopmentProposal());

        proposalDocument.setDevelopmentProposal(null);
        proposalDocument.setProposalDeleted(true);

        proposalDocument = (ProposalDevelopmentDocument)getDocumentService().saveDocument(proposalDocument);
        return (ProposalDevelopmentDocument) getDocumentService().cancelDocument(proposalDocument, "Delete Proposal");
    }

    protected void deleteCertDetails(DevelopmentProposal proposal) {
        HashMap<String, String> criteria = new HashMap<>();
        criteria.put(PROPOSAL_NUMBER, proposal.getProposalNumber());
        dataObjectService.deleteMatching(ProposalPersonCertificationDetails.class, QueryByCriteria.Builder.andAttributes(criteria).build());
        proposal.getProposalPersons().forEach(proposalPerson -> proposalPerson.setCertificationDetails(null));
    }

    protected DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    public Budget getFinalBudget(DevelopmentProposal proposal) {
    	return proposal.getFinalBudget();
    }


    /**
     * Return the institutional proposal linked to the development proposal.
     */
    @Override
    public InstitutionalProposal getInstitutionalProposal(String devProposalNumber) {
        Map<String, Object> values = new HashMap<>();
        values.put("devProposalNumber", devProposalNumber);
        Collection<ProposalAdminDetails> proposalAdminDetails = getBusinessObjectService().findMatching(ProposalAdminDetails.class,values);

        for (ProposalAdminDetails pad : proposalAdminDetails) {
        	if (pad.getInstProposalId() != null) {
        		return getBusinessObjectService().findBySinglePrimaryKey(InstitutionalProposal.class, pad.getInstProposalId());
        	}
        }
        return null;
    }

    @Override
    public List<Unit> getUnitsForCreateProposal(String userId) {
        final String namespaceCode = Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT;
        final String permissionName = PermissionConstants.CREATE_PROPOSAL;

        Set<Unit> units = new LinkedHashSet<>();
        // Start by getting all of the Qualified Roles that the person is in.  For each
        // qualified role that has the UNIT_NUMBER qualification, check to see if the role
        // has the required permission.  If so, add that unit to the list.  Also, if the
        // qualified role has the SUBUNITS qualification set to YES, then also add all of the
        // subunits the to the list.
        Map<String, String> qualifiedRoleAttributes = new HashMap<>();
        qualifiedRoleAttributes.put(KcKimAttributes.UNIT_NUMBER, "*");
        Map<String,String> qualification =new HashMap<>(qualifiedRoleAttributes);
        final Set<String> roleIds = new HashSet<>(systemAuthorizationService.getRoleIdsForPermission(permissionName, namespaceCode));

        Set<Map<String,String>> qualifiers = new HashSet<>(roleService.getNestedRoleQualifiersForPrincipalByRoleIds(userId, new ArrayList<>(roleIds), qualification));

        for (Map<String,String> qualifier : qualifiers) {
            Unit unit = getUnitService().getUnit(qualifier.get(KcKimAttributes.UNIT_NUMBER));
            if (unit != null && unit.isActive()) {
                units.add(unit);
                if (qualifier.containsKey(KcKimAttributes.SUBUNITS) && (StringUtils.equalsIgnoreCase("Y", qualifier.get(KcKimAttributes.SUBUNITS)) || StringUtils.equalsIgnoreCase("Yes", qualifier.get(KcKimAttributes.SUBUNITS)))) {
                    addDescendantUnits(unit, units);
                }
            }
        }
        //the above line could potentially be a performance problem - need to revisit
        return new ArrayList<>(units);
    }

    @Override
    public boolean autogenerateInstitutionalProposal() {
    	return getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, 
                ParameterConstants.DOCUMENT_COMPONENT, KeyConstants.AUTOGENERATE_INSTITUTIONAL_PROPOSAL_PARAM);
    }

    @Override
    public String getIPGenerateOption(DevelopmentProposal developmentProposal) {
        if( isProposalTypeChangeCorrected(developmentProposal.getProposalTypeCode())) {
            return ProposalDevelopmentConstants.ResubmissionOptions.GENERATE_NEW_VERSION_OF_ORIGINAL_IP;
        }else{
            return ProposalDevelopmentConstants.ResubmissionOptions.GENERATE_NEW_IP;
        }
    }

    @Override
    public boolean isProposalReniewedOrChangeCorrected(DevelopmentProposal developmentProposal) {
       return (getProposalTypeService().getContinuationProposalTypeCode().equals(developmentProposal.getProposalTypeCode())
           || getProposalTypeService().getRenewProposalTypeCode().equals(developmentProposal.getProposalTypeCode())
           || getProposalTypeService().getResubmissionProposalTypeCode().equals(developmentProposal.getProposalTypeCode())
           || getProposalTypeService().getRevisionProposalTypeCode().equals(developmentProposal.getProposalTypeCode())
           || isProposalTypeChangeCorrected(developmentProposal.getProposalTypeCode())
           || isSubmissionChangeCorrected(developmentProposal));
    }

    private boolean isSubmissionChangeCorrected(DevelopmentProposal developmentProposal) {
        return developmentProposal.getS2sOpportunity() != null && getProposalTypeService().getS2SSubmissionChangeCorrectedCode().equals(developmentProposal.getS2sOpportunity().getS2sSubmissionTypeCode());
    }

    private boolean isProposalTypeChangeCorrected(String proposalTypeCode) {
        return (getProposalTypeService().getNewChangedOrCorrectedProposalTypeCode().equals(proposalTypeCode)
            || getProposalTypeService().getRenewalChangedOrCorrectedProposalTypeCode().equals(proposalTypeCode)
            || getProposalTypeService().getResubmissionChangedOrCorrectedProposalTypeCode().equals(proposalTypeCode)
            || getProposalTypeService().getSupplementChangedOrCorrectedProposalTypeCode().equals(proposalTypeCode)
            || getProposalTypeService().getBudgetSowUpdateProposalTypeCode().equals(proposalTypeCode));
    }

    protected void addDescendantUnits(Unit parentUnit, Set<Unit> units) {
        List<Unit> subunits = getUnitService().getActiveSubUnits(parentUnit.getUnitNumber());
        if (CollectionUtils.isNotEmpty(subunits)) {
            units.addAll(subunits);
            for (Unit subunit : subunits) {
                addDescendantUnits(subunit, units);
            }
        }
    }
    
    protected RoleService getRoleService() {
        return roleService;
    }

    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    public SystemAuthorizationService getSystemAuthorizationService() {
        return systemAuthorizationService;
    }

    public void setSystemAuthorizationService(SystemAuthorizationService systemAuthorizationService) {
        this.systemAuthorizationService = systemAuthorizationService;
    }

    public UnitService getUnitService() {
        return unitService;
    }

    public void setUnitService(UnitService unitService) {
        this.unitService = unitService;
    }

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    protected ParameterService getParameterService() {
        return parameterService;
    }

    public void setBusinessObjectService(BusinessObjectService bos) {
        businessObjectService = bos;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public ProposalTypeService getProposalTypeService() {
        return proposalTypeService;
    }

    public void setProposalTypeService(ProposalTypeService proposalTypeService) {
        this.proposalTypeService = proposalTypeService;
    }

    public ProjectRetrievalService getProjectRetrievalService() {
        return projectRetrievalService;
    }

    public void setProjectRetrievalService(ProjectRetrievalService projectRetrievalService) {
        this.projectRetrievalService = projectRetrievalService;
    }

    public ProjectPublisher getProjectPublisher() {
        //since COI is loaded last and @Lazy does not work, we have to use the ServiceLocator
        if (projectPublisher == null) {
            projectPublisher = KcServiceLocator.getService(ProjectPublisher.class);
        }

        return projectPublisher;
    }

    public void setProjectPublisher(ProjectPublisher projectPublisher) {
        this.projectPublisher = projectPublisher;
    }
}
