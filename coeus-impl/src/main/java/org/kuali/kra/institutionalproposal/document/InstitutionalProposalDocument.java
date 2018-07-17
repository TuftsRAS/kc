/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.coi.framework.Project;
import org.kuali.coeus.coi.framework.ProjectPublisher;
import org.kuali.coeus.coi.framework.ProjectRetrievalService;
import org.kuali.coeus.common.framework.auth.perm.Permissionable;
import org.kuali.coeus.common.framework.custom.DocumentCustomData;
import org.kuali.coeus.common.framework.version.VersionStatus;
import org.kuali.coeus.common.permissions.impl.PermissionableKeys;
import org.kuali.coeus.sys.framework.model.KcTransactionalDocumentBase;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.RoleConstants;
import org.kuali.kra.institutionalproposal.InstitutionalProposalConstants;
import org.kuali.kra.institutionalproposal.contacts.InstitutionalProposalPerson;
import org.kuali.kra.institutionalproposal.contacts.InstitutionalProposalPersonCreditSplit;
import org.kuali.kra.institutionalproposal.contacts.InstitutionalProposalPersonUnit;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.kra.institutionalproposal.service.InstitutionalProposalVersioningService;
import org.kuali.kra.institutionalproposal.specialreview.InstitutionalProposalSpecialReview;
import org.kuali.kra.institutionalproposal.specialreview.InstitutionalProposalSpecialReviewExemption;
import org.kuali.kra.kim.bo.KcKimAttributes;
import org.kuali.kra.krms.KcKrmsConstants;
import org.kuali.coeus.common.framework.krms.KrmsRulesContext;
import org.kuali.coeus.common.impl.krms.KcKrmsFactBuilderServiceHelper;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants.COMPONENT;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants.NAMESPACE;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krms.api.engine.Facts.Builder;

@NAMESPACE(namespace=InstitutionalProposalConstants.INSTITUTIONAL_PROPOSAL_NAMESPACE)
@COMPONENT(component=ParameterConstants.DOCUMENT_COMPONENT)
public class InstitutionalProposalDocument extends KcTransactionalDocumentBase implements KrmsRulesContext, Permissionable {

    public static final String DOCUMENT_TYPE_CODE = "INPR";
    private static final long serialVersionUID = 5101782927161970631L;
    private static final String NAMESPACE_CODE = "namespaceCode";
    private static final String NAME = "name";
    private static final Logger LOG = LogManager.getLogger(InstitutionalProposalDocument.class);


    private List<InstitutionalProposal> institutionalProposalList;
    private transient KcKrmsFactBuilderServiceHelper institutionalProposalFactBuilderService;
    private transient ProjectRetrievalService projectRetrievalService;
    private transient ProjectPublisher projectPublisher;

    public InstitutionalProposalDocument(){        
        super();        
        init();
    }

    public InstitutionalProposal getInstitutionalProposal() {
        return institutionalProposalList.get(0);
    }

    public void setInstitutionalProposal(InstitutionalProposal institutionalProposal) {
        institutionalProposalList.set(0, institutionalProposal);
    }
   

    public List<InstitutionalProposal> getInstitutionalProposalList() {
        return institutionalProposalList;
    }

    public void setInstitutionalProposalList(List<InstitutionalProposal> institutionalProposalList) {
        this.institutionalProposalList = institutionalProposalList;
    }

    @Override
    public String getDocumentTypeCode() {
        return DOCUMENT_TYPE_CODE;
    }
    
    protected void init() {
        institutionalProposalList = new ArrayList<>();
        institutionalProposalList.add(new InstitutionalProposal());
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();       
        
        InstitutionalProposal institutionalProposal = getInstitutionalProposal();
        
        managedLists.add(institutionalProposal.getInstitutionalProposalUnitContacts());
        managedLists.add(institutionalProposal.getInstitutionalProposalScienceKeywords());
        managedLists.add(institutionalProposal.getInstitutionalProposalCostShares());
        managedLists.add(institutionalProposal.getInstitutionalProposalUnrecoveredFandAs());
        managedLists.add(institutionalProposal.getInstitutionalProposalFandAs());
        managedLists.add(institutionalProposal.getAwardFundingProposals());
        managedLists.add(institutionalProposal.getInstitutionalProposalNotepads());
        
        List<InstitutionalProposalSpecialReviewExemption> specialReviewExemptions = new ArrayList<>();
        for (InstitutionalProposalSpecialReview specialReview : getInstitutionalProposal().getSpecialReviews()) {
            specialReviewExemptions.addAll(specialReview.getSpecialReviewExemptions());        
        }
        managedLists.add(specialReviewExemptions);
        managedLists.add(institutionalProposal.getSpecialReviews());
        
        // For project personnel 
        List<InstitutionalProposalPersonUnit> units = new ArrayList<>();
        List<InstitutionalProposalPersonCreditSplit> creditSplits = new ArrayList<>();
        for (InstitutionalProposalPerson person : institutionalProposal.getProjectPersons()) {
            units.addAll(person.getUnits());
            creditSplits.addAll(person.getCreditSplits());
        }
        managedLists.add(units);
        managedLists.add(creditSplits);
        managedLists.add(institutionalProposal.getProjectPersons());
        managedLists.add(institutionalProposal.getProposalCfdas());

        return managedLists;
    }
    
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        executeAsLastActionUser(() -> {
            super.doRouteStatusChange(statusChangeEvent);

            final String newStatus = statusChangeEvent.getNewRouteStatus();

            LOG.debug(String.format("********************* Status Change: from %s to %s", statusChangeEvent.getOldRouteStatus(), newStatus));

            if (KewApiConstants.ROUTE_HEADER_PROCESSED_CD.equalsIgnoreCase(newStatus)) {
                getInstitutionalProposalVersioningService().updateInstitutionalProposalVersionStatus(this.getInstitutionalProposal(), VersionStatus.ACTIVE);
            } else if (newStatus.equalsIgnoreCase(KewApiConstants.ROUTE_HEADER_CANCEL_CD) || newStatus.equalsIgnoreCase(KewApiConstants.ROUTE_HEADER_DISAPPROVED_CD)) {
                getInstitutionalProposalVersioningService().updateInstitutionalProposalVersionStatus(this.getInstitutionalProposal(), VersionStatus.CANCELED);
            }

            final Project project = getProjectRetrievalService().retrieveProject(getInstitutionalProposal().getProposalNumber());
            if (project != null) {
                getProjectPublisher().publishProject(project);
            }

            return null;
        });
    }
    
    private InstitutionalProposalVersioningService getInstitutionalProposalVersioningService() {
        return KcServiceLocator.getService(InstitutionalProposalVersioningService.class);
    }
    
    @Override
    public void prepareForSave() {
        super.prepareForSave();
        if (ObjectUtils.isNull(this.getVersionNumber())) {
            this.setVersionNumber(0L);
        }
    }

    @Override
    public String getDocumentBoNumber() {
        return getInstitutionalProposal().getInstProposalNumber();
    }
    
    /**
     * This method is to check whether rice async routing is ok now.   
     * Close to hack.  called by holdingpageaction
     * Different document type may have different routing set up, so each document type
     * can implement its own isProcessComplete
     */
    @Override
    public boolean isProcessComplete() {
        boolean isComplete = false;
        
        if (getDocumentHeader().hasWorkflowDocument()) {
            String docRouteStatus = getDocumentHeader().getWorkflowDocument().getStatus().getCode();
            if (KewApiConstants.ROUTE_HEADER_FINAL_CD.equals(docRouteStatus)) {
                isComplete = true;
            }
        }
           
        return isComplete;
    }

    @Override
    public List<? extends DocumentCustomData> getDocumentCustomData() {
        return getInstitutionalProposal().getInstitutionalProposalCustomDataList();
    }
    
    @Override
    public void populateContextQualifiers(Map<String, String> qualifiers) {
        qualifiers.put(NAMESPACE_CODE, Constants.MODULE_NAMESPACE_INSITUTIONAL_PROPOSAL);
        qualifiers.put(NAME, KcKrmsConstants.InstitutionalProposal.INSTITUTIONAL_PROPOSAL_CONTEXT);
    }

    @Override
    public void addFacts(Builder factsBuilder) {
        getInstitutionalProposalFactBuilderService().addFacts(factsBuilder, this);
    }

    @Override
    public void populateAgendaQualifiers(Map<String, String> qualifiers) {
        qualifiers.put(KcKrmsConstants.UNIT_NUMBER, getLeadUnitNumber());
    }

    @Override
    public String getDocumentNumberForPermission() {
       return getInstitutionalProposal().getInstProposalNumber();
    }

    @Override
    public String getDocumentKey() {
        return PermissionableKeys.INSTITUTIONAL_PROPOSAL_KEY;
    }

    @Override
    public List<String> getRoleNames() {
        return new ArrayList<>();
    }

    @Override
    public String getNamespace() {
        return Constants.MODULE_NAMESPACE_INSITUTIONAL_PROPOSAL;
    }

    @Override
    public String getLeadUnitNumber() {
        return getInstitutionalProposal().getLeadUnitNumber();
    }

    @Override
    public String getDocumentRoleTypeCode() {
        return RoleConstants.INSTITUTIONAL_PROPOSAL_TYPE;
    }

    public KcKrmsFactBuilderServiceHelper getInstitutionalProposalFactBuilderService() {
        if (institutionalProposalFactBuilderService == null) {
            institutionalProposalFactBuilderService = KcServiceLocator.getService("institutionalProposalFactBuilderService");
        }

        return institutionalProposalFactBuilderService;
    }

    public void setInstitutionalProposalFactBuilderService(KcKrmsFactBuilderServiceHelper institutionalProposalFactBuilderService) {
        this.institutionalProposalFactBuilderService = institutionalProposalFactBuilderService;
    }

    public ProjectPublisher getProjectPublisher() {
        if (projectPublisher == null) {
            projectPublisher = KcServiceLocator.getService(ProjectPublisher.class);
        }

        return projectPublisher;
    }

    public void setProjectPublisher(ProjectPublisher projectPublisher) {
        this.projectPublisher = projectPublisher;
    }

    public ProjectRetrievalService getProjectRetrievalService() {
        if (projectRetrievalService == null) {
            projectRetrievalService = KcServiceLocator.getService("instPropProjectRetrievalService");
        }

        return projectRetrievalService;
    }

    public void setProjectRetrievalService(ProjectRetrievalService projectRetrievalService) {
        this.projectRetrievalService = projectRetrievalService;
    }

    @Override
    public Map<String, String> getKrmsRoleQualifiers() {
        Map<String, String> qualifiers = new HashMap<>();
        qualifiers.put(KcKimAttributes.PROPOSAL, getInstitutionalProposal().getProposalId().toString());

        return qualifiers;
    }
}
