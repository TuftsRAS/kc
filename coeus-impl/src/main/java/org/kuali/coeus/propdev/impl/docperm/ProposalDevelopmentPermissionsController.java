/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.docperm;

import org.kuali.coeus.common.framework.person.PersonTypeConstants;
import org.kuali.coeus.common.view.wizard.framework.WizardControllerService;
import org.kuali.coeus.common.view.wizard.framework.WizardResultsDto;
import org.kuali.coeus.propdev.impl.core.*;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.web.service.RefreshControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * The ProposalDevelopmentPermissionsController responds to user events from the
 * Permissions web page.  
 *
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
@Controller
public class ProposalDevelopmentPermissionsController extends ProposalDevelopmentControllerBase {

    @Autowired
    @Qualifier("refreshControllerService")
    private RefreshControllerService refreshControllerService;

    @Autowired
    @Qualifier("wizardControllerService")
    private WizardControllerService wizardControllerService;

    
    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=savePermission")
    public ModelAndView savePermission(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {

        final String selectedCollectionPath = form.getActionParamaterValue(UifParameters.SELECTED_COLLECTION_PATH);
        String selectedLine = form.getActionParamaterValue(UifParameters.SELECTED_LINE_INDEX);

        boolean success = getProposalDevelopmentPermissionsService().validateUpdatePermissions(form.getProposalDevelopmentDocument(), form.getWorkingUserRoles(),
                form.getWorkingUserRoles().get(Integer.parseInt(selectedLine)));

        if (success){
            getProposalDevelopmentPermissionsService().processUpdatePermission(form.getProposalDevelopmentDocument(),form.getWorkingUserRoles().get(Integer.parseInt(selectedLine)));

            if(form.getEditableCollectionLines().containsKey(selectedCollectionPath)){
                form.getEditableCollectionLines().get(selectedCollectionPath).remove(selectedLine);
            }
            form.setEvaluateFlagsAndModes(true);
        }

        return getRefreshControllerService().refresh(form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=performPermissionSearch")
    public ModelAndView performPermissionSearch(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        form.getAddKeyPersonHelper().getResults().clear();
        List<Object> results = new ArrayList<Object>();

        for (Object object : getWizardControllerService().performWizardSearch(form.getAddKeyPersonHelper().getLookupFieldValues(),form.getAddKeyPersonHelper().getLineType())) {
            WizardResultsDto wizardResult = (WizardResultsDto) object;
            String userName = wizardResult.getKcPerson().getUserName();
            if (!userAlreadyExists(userName, form.getWorkingUserRoles())) {
                results.add(object);
            }
        }

        form.getAddKeyPersonHelper().setResults(results);
        return getRefreshControllerService().refresh(form);
    }
    protected boolean userAlreadyExists(String userName, List<ProposalUserRoles> existingUsers) {
        for (ProposalUserRoles existingUser: existingUsers ) {
            if (userName.equals(existingUser.getUsername())) {
                return true;
            }
        }
        return false;
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=addPermission")
    public ModelAndView addPermission(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        ProposalDevelopmentDocument document = form.getProposalDevelopmentDocument();

        ProposalUserRoles newProposalUserRoles = new ProposalUserRoles();

        for (Object object : form.getAddKeyPersonHelper().getResults()) {
            WizardResultsDto wizardResult = (WizardResultsDto) object;
            if (wizardResult.isSelected() == true) {
                if (wizardResult.getKcPerson() != null) {
                    newProposalUserRoles.setFullname(wizardResult.getKcPerson().getFullName());
                    newProposalUserRoles.setUsername(wizardResult.getKcPerson().getUserName());
                }
                break;
            }
        }

       Object roleNames =  form.getAddKeyPersonHelper().getParameter("roleNames");
        if (roleNames instanceof String) {
            newProposalUserRoles.getRoleNames().add((String)roleNames);
        } else if (roleNames instanceof String[]) {
           for (String roleName : (String[])roleNames) {
               newProposalUserRoles.getRoleNames().add(roleName);
           }
        }
        if (getProposalDevelopmentPermissionsService().validateAddPermissions(document, form.getWorkingUserRoles(),newProposalUserRoles)) {
            getProposalDevelopmentPermissionsService().processAddPermission(document,newProposalUserRoles);
            form.getWorkingUserRoles().add(newProposalUserRoles);
            if (form.getWorkflowDocument().isEnroute()) {
                return super.save(form);
            }
        }


        return getRefreshControllerService().refresh(form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=prepareAddPermission"})
    public ModelAndView prepareAddPermission(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        form.getAddKeyPersonHelper().setLineType(PersonTypeConstants.EMPLOYEE.getCode());
        return getModelAndViewService().showDialog("PropDev-PermissionsPage-Wizard",true,form);
    }

    public WizardControllerService getWizardControllerService() {
        return wizardControllerService;
    }

    public void setWizardControllerService(WizardControllerService wizardControllerService) {
        this.wizardControllerService = wizardControllerService;
    }

    public RefreshControllerService getRefreshControllerService() {
        return refreshControllerService;
    }

    public void setRefreshControllerService(RefreshControllerService refreshControllerService) {
        this.refreshControllerService = refreshControllerService;
    }
}
