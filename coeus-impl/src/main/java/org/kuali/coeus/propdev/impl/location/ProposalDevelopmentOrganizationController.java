/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.location;

import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentControllerBase;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocumentForm;
import org.kuali.rice.krad.web.service.RefreshControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProposalDevelopmentOrganizationController extends ProposalDevelopmentControllerBase {

    private static final String ALLOW_APPLICANT_ORGANIZATION_CHANGE_PARAM = "Allow_Applicant_Organization_Change";
    private static final String ORGANIZATION_PROPERTY = "organization";

    @Autowired
    @Qualifier("refreshControllerService")
    private RefreshControllerService refreshControllerService;

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=refresh", "refreshCaller=Organization-LookupView"} )
    public ModelAndView refreshOrganization(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        initializeProposalSite(form.getDevelopmentProposal().getPerformingOrganization());
        if (getParameterService().getParameterValueAsBoolean(ProposalDevelopmentDocument.class, ALLOW_APPLICANT_ORGANIZATION_CHANGE_PARAM, false)) {
            initializeProposalSite(form.getDevelopmentProposal().getApplicantOrganization());
        }
        return getRefreshControllerService().refresh(form);
    }

    protected void initializeProposalSite(ProposalSite site) {
        getDataObjectService().wrap(site).fetchRelationship(ORGANIZATION_PROPERTY);
        site.initializeDefaultCongressionalDistrict();
    }

    public RefreshControllerService getRefreshControllerService() {
        return refreshControllerService;
    }

    public void setRefreshControllerService(RefreshControllerService refreshControllerService) {
        this.refreshControllerService = refreshControllerService;
    }
}
