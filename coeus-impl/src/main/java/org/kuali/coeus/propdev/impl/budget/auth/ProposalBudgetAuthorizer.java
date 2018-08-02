/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.budget.auth;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.budget.framework.core.Budget;
import org.kuali.coeus.common.budget.framework.core.BudgetParentDocument;
import org.kuali.coeus.common.framework.auth.perm.KcAuthorizationService;
import org.kuali.coeus.propdev.impl.budget.ProposalDevelopmentBudgetExt;
import org.kuali.coeus.propdev.impl.budget.core.ProposalBudgetConstants.AuthConstants;
import org.kuali.coeus.propdev.impl.budget.core.ProposalBudgetForm;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentConstants;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.hierarchy.ProposalHierarchyException;
import org.kuali.coeus.propdev.impl.hierarchy.ProposalHierarchyService;
import org.kuali.coeus.propdev.impl.lock.ProposalBudgetLockService;
import org.kuali.coeus.sys.framework.workflow.KcDocumentRejectionService;
import org.kuali.coeus.sys.framework.workflow.KcWorkflowService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.authorization.PessimisticLock;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewAuthorizerBase;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * This class is the Budget Document Authorizer.  It determines the edit modes and
 * document actions for all budget documents.
 */
@Component("proposalBudgetAuthorizer")
public class ProposalBudgetAuthorizer extends ViewAuthorizerBase {

    private static final Logger LOG = LogManager.getLogger(ProposalBudgetAuthorizer.class);

    @Autowired
	@Qualifier("parameterService")
	private ParameterService parameterService;

    @Autowired
    @Qualifier("kcAuthorizationService")
    private KcAuthorizationService kcAuthorizationService;

    @Autowired
    @Qualifier("kcDocumentRejectionService")
    private KcDocumentRejectionService kcDocumentRejectionService;

    @Autowired
    @Qualifier("kcWorkflowService")
    private KcWorkflowService kcWorkflowService;

    @Autowired
    @Qualifier("proposalBudgetLockService")
    private ProposalBudgetLockService proposalBudgetLockService;

    @Autowired
    @Qualifier("proposalHierarchyService")
    private ProposalHierarchyService proposalHierarchyService;

    @Override
    public Set<String> getEditModes(View view, ViewModel model, Person user, Set<String> editModes) {
    	ProposalBudgetForm form = (ProposalBudgetForm) model;
        ProposalDevelopmentBudgetExt budget = form.getBudget();
        ProposalDevelopmentDocument parentDocument = (ProposalDevelopmentDocument) budget.getBudgetParent().getDocument();

        if (isAuthorizedToAddBudget(parentDocument, user)) {
        	editModes.add(AuthConstants.ADD_BUDGET_EDIT_MODE);
        }
        
        if (isAuthorizedToModifyBudget(budget, user)) {
        	editModes.add(AuthConstants.VIEW_BUDGET_EDIT_MODE);
        	if(!form.isViewOnly()) {
            	editModes.add(AuthConstants.CHANGE_COMPLETE_STATUS);
        	}
        	if (!isBudgetComplete(budget)) {
        		if (!budget.getDevelopmentProposal().isParent()) {
		            editModes.add(AuthConstants.MODIFY_BUDGET_EDIT_MODE);
		            if (isAuthorizedToModifyRates(budget, user)) {
		                editModes.add(AuthConstants.MODIFY_RATES_EDIT_MODE);
		            }
		            setPermissions(user, parentDocument, editModes);
        		} else {
        			editModes.add(AuthConstants.MODIFY_HIERARCHY_PARENT_BUDGET);
        		}
        	}
        } else if (isAuthorizedToViewBudget(budget, user)) {
            editModes.add(AuthorizationConstants.EditMode.VIEW_ONLY);
            editModes.add(AuthConstants.VIEW_BUDGET_EDIT_MODE);
            
            setPermissions(user, parentDocument, editModes);
        }
        else {
            editModes.add(AuthorizationConstants.EditMode.UNVIEWABLE);
        }
        
        if (isAuthorizedToMaintainProposalHierarchy(parentDocument, user)) {
            editModes.add(AuthConstants.MAINTAIN_PROPOSAL_HIERARCHY);
        }
        
        return editModes;
    }
    
    /**
     * Set the permissions to be used during the creation of the web pages.  
     * The JSP files can access the editModeMap (editingMode) to determine what
     * to display to the user.  For example, a JSP file may contain the following:
     * 
     *     <kra:section permission="modifyProposal">
     *         .
     *         .
     *         .
     *     </kra:section>
     * 
     * In the above example, the contents are only rendered if the user is allowed
     * to modify the proposal.  Note that permissions are always signified as 
     * either TRUE or FALSE.
     * 
     * @param user the user
     * @param doc the Proposal Development Document
     * @param editModes the edit mode map
     */
    protected void setPermissions(Person user, BudgetParentDocument doc, Set<String> editModes) {

        if (isAuthorizedToAddBudget(doc, user)) {
            editModes.add(AuthConstants.ADD_BUDGET_EDIT_MODE);
        }
        
        if (isAuthorizedToPrintProposal(doc, user)) {
            editModes.add(AuthConstants.PRINT_EDIT_MODE);
        }

        if (isAuthorizedToAlterProposalData(doc, user)) {
            editModes.add(ProposalDevelopmentConstants.AuthConstants.ALTER_PROPOSAL_DATA);
        }

        if (isAuthorizedToShowAlterBudgetData(doc, user)) {
            editModes.add(ProposalDevelopmentConstants.AuthConstants.SHOW_ALTER_PROPOSAL_DATA);
        }
    }

    @Override
    public boolean canOpenView(View view, ViewModel model, Person user) {
        ProposalBudgetForm form = (ProposalBudgetForm) model;
        ProposalDevelopmentBudgetExt budget = form.getBudget();

        return canOpen(budget, user);
    }

    public boolean canOpen(ProposalDevelopmentBudgetExt budget, Person user) {
        return isAuthorizedToViewBudget(budget, user);
    }
    
    public boolean canEdit(ProposalDevelopmentBudgetExt budget, Person user) {
        return isAuthorizedToModifyBudget(budget, user);
    }

    public boolean canEditModularBudget(ProposalDevelopmentBudgetExt budget, Person user) {
        return isAuthorizedToModifyBudget(budget, user, false);
    }
    
    public boolean canSave(ProposalDevelopmentBudgetExt budget, Person user) {
        return canEdit(budget, user);
    }
    
    public boolean canReload(ProposalDevelopmentBudgetExt budget, Person user) {
        return canEdit(budget, user);
    }

    public boolean canModifyBudget(ProposalDevelopmentBudgetExt budget, Person user) {
        return isAuthorizedToModifyBudget(budget, user) && !isBudgetComplete(budget);
    }

    /**
     * Is the Budget in the final state?
     */
    protected boolean isBudgetComplete(ProposalDevelopmentBudgetExt budget) {
		String budgetStatusCompleteCode = getParameterService().getParameterValueAsString(Budget.class, Constants.BUDGET_STATUS_COMPLETE_CODE);
		return StringUtils.equals(budgetStatusCompleteCode, budget.getBudgetStatus());
    }

    protected boolean isAuthorizedToModifyRates(ProposalDevelopmentBudgetExt budget, Person user) {
        ProposalDevelopmentDocument pdDocument = (ProposalDevelopmentDocument)budget.getBudgetParent().getDocument();
        boolean rejectedDocument = getKcDocumentRejectionService().isDocumentOnInitialNode(pdDocument.getDocumentHeader().getWorkflowDocument());

        return (!getKcWorkflowService().isInWorkflow(pdDocument) || rejectedDocument) &&
                getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MODIFY_PROPOSAL_RATES) 
                && !pdDocument.getDevelopmentProposal().getSubmitFlag();
    }

    public boolean isAuthorizedToViewBudget(ProposalDevelopmentBudgetExt budget, Person user) {
        ProposalDevelopmentDocument doc = (ProposalDevelopmentDocument) budget.getBudgetParent().getDocument();
        return getKcAuthorizationService().hasPermission(user.getPrincipalId(), doc, PermissionConstants.VIEW_BUDGET)
            || getKcAuthorizationService().hasPermission(user.getPrincipalId(), doc, PermissionConstants.MODIFY_BUDGET)
            || kcWorkflowService.hasWorkflowPermission(user.getPrincipalId(), doc);
    }

    protected boolean isAuthorizedToMaintainProposalHierarchy(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);
        return !pdDocument.isViewOnly() && getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MAINTAIN_PROPOSAL_HIERARCHY);
    }

    protected boolean isAuthorizedToModifyBudget(ProposalDevelopmentBudgetExt budget, Person user) {
        return isAuthorizedToModifyBudget(budget, user, true);
    }

    protected boolean isAuthorizedToModifyBudget(ProposalDevelopmentBudgetExt budget, Person user, boolean checkPessimisticLocks) {
        ProposalDevelopmentDocument pdDocument = (ProposalDevelopmentDocument)budget.getBudgetParent().getDocument();
        boolean rejectedDocument = getKcDocumentRejectionService().isDocumentOnInitialNode(pdDocument.getDocumentHeader().getWorkflowDocument());

        return (!getKcWorkflowService().isInWorkflow(pdDocument) || rejectedDocument) &&
                getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MODIFY_BUDGET) 
                && !pdDocument.getDevelopmentProposal().getSubmitFlag() && (!checkPessimisticLocks || userHasLockOnBudget(budget,user));
    }

    protected boolean userHasLockOnBudget(ProposalDevelopmentBudgetExt budget, Person user) {
        ProposalDevelopmentDocument document = budget.getDevelopmentProposal().getProposalDocument();
        for (PessimisticLock lock : document.getPessimisticLocks()) {
            if (lock.isOwnedByUser(user) && getProposalBudgetLockService().doesBudgetVersionMatchDescriptor(lock.getLockDescriptor(),budget.getBudgetVersionNumber())) {
                return true;
            }
        }
        return false;
    }

    protected boolean isAuthorizedToAddBudget(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);

        boolean hasPermission = false;

        boolean rejectedDocument = getKcDocumentRejectionService().isDocumentOnInitialNode(pdDocument.getDocumentHeader().getWorkflowDocument());

        if ((!getKcWorkflowService().isInWorkflow(pdDocument) || rejectedDocument) && !pdDocument.isViewOnly() && !pdDocument.getDevelopmentProposal().getSubmitFlag() && !pdDocument.getDevelopmentProposal().isParent()) {
            hasPermission = getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.MODIFY_BUDGET);
        }
        return hasPermission;
    }

    protected boolean isAuthorizedToPrintProposal(Document document, Person user) {
        final BudgetParentDocument bpDocument = ((BudgetParentDocument) document);
        return getKcAuthorizationService().hasPermission(user.getPrincipalId(), bpDocument, PermissionConstants.PRINT_PROPOSAL);
    }

    protected boolean isAuthorizedToAlterProposalData(Document document, Person user) {
        final ProposalDevelopmentDocument pdDocument = ((ProposalDevelopmentDocument) document);
        boolean ret = true;

        //check to see if the parent is enroute, if so deny the edit attempt.
        if(pdDocument.getDevelopmentProposal().isChild() ) {
            try {
                if (getProposalHierarchyService().getParentWorkflowDocument(pdDocument).isEnroute()) {
                    ret = false;
                }
            } catch (ProposalHierarchyException e) {
                LOG.error(String.format( "Exception looking up parent of DevelopmentProposal %s, authorizer is going to deny edit access to this child.", pdDocument.getDevelopmentProposal().getProposalNumber()), e);
                ret = false;
            }
        }

        if (ret){
            ret = getKcWorkflowService().isEnRoute(pdDocument) &&
                    !pdDocument.getDevelopmentProposal().getSubmitFlag() &&
                    getKcAuthorizationService().hasPermission(user.getPrincipalId(), pdDocument, PermissionConstants.ALTER_PROPOSAL_DATA);
        }


        return ret;
    }

    protected boolean isAuthorizedToShowAlterBudgetData(Document document, Person user) {
        return getKcWorkflowService().isInWorkflow(document);
    }

	public ParameterService getParameterService() {
		return parameterService;
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

    public KcAuthorizationService getKcAuthorizationService() {
        return kcAuthorizationService;
    }

    public void setKcAuthorizationService(KcAuthorizationService kcAuthorizationService) {
        this.kcAuthorizationService = kcAuthorizationService;
    }

    public KcDocumentRejectionService getKcDocumentRejectionService() {
        return kcDocumentRejectionService;
    }

    public void setKcDocumentRejectionService(KcDocumentRejectionService kcDocumentRejectionService) {
        this.kcDocumentRejectionService = kcDocumentRejectionService;
    }

    public KcWorkflowService getKcWorkflowService() {
        return kcWorkflowService;
    }

    public void setKcWorkflowService(KcWorkflowService kcWorkflowService) {
        this.kcWorkflowService = kcWorkflowService;
    }

    public ProposalBudgetLockService getProposalBudgetLockService() {
        return proposalBudgetLockService;
    }

    public void setProposalBudgetLockService(ProposalBudgetLockService proposalBudgetLockService) {
        this.proposalBudgetLockService = proposalBudgetLockService;
    }

    protected ProposalHierarchyService getProposalHierarchyService (){
        return proposalHierarchyService;
    }

    public void setProposalHierarchyService (ProposalHierarchyService proposalHierarchyService){
        this.proposalHierarchyService = proposalHierarchyService;
    }
}
