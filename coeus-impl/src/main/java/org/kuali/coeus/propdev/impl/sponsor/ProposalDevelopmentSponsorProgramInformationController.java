/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.sponsor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentControllerBase;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocumentForm;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.kra.institutionalproposal.proposaladmindetails.ProposalAdminDetails;
import org.kuali.rice.krad.service.KualiRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Controller
public class ProposalDevelopmentSponsorProgramInformationController extends ProposalDevelopmentControllerBase {

    @Autowired
    @Qualifier("kualiRuleService")
    private KualiRuleService kualiRuleService;

    @Override
    @Transactional @RequestMapping(value = "/proposalDevelopment", params = {"methodToCall=navigate", "actionParameters[navigateToPageId]=PropDev-SponsorProgramInfoPage"})
    public ModelAndView navigate(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isNotEmpty(form.getDevelopmentProposal().getContinuedFrom()) &&
                StringUtils.isEmpty(form.getDevelopmentProposal().getPrevGrantsGovTrackingID())) {
            String instProposalId = getInstitutionalProposalId(form.getDevelopmentProposal().getContinuedFrom());
            DevelopmentProposal continuedFrom = getContinuedFromDevelopmentProposal(instProposalId);
            if (continuedFrom != null && CollectionUtils.isNotEmpty(continuedFrom.getS2sAppSubmission())) {
                String ggTrackingId = continuedFrom.getS2sAppSubmission().get(continuedFrom.getS2sAppSubmission().size() - 1).getGgTrackingId();
                form.getDevelopmentProposal().setPrevGrantsGovTrackingID(ggTrackingId);
            }
        }
        getKualiRuleService().applyRules(new AddProposalSponsorAndProgramInformationEvent("", form.getProposalDevelopmentDocument()));

        return super.navigate(form, result, request, response);
    }

    public KualiRuleService getKualiRuleService() {
        return kualiRuleService;
    }

    protected String getInstitutionalProposalId(String instProposalNumber) {
        List<InstitutionalProposal> institutionalProposals = (List<InstitutionalProposal>) getLegacyDataAdapter().findMatching(InstitutionalProposal.class, Collections.singletonMap("proposalNumber", instProposalNumber));
        if (CollectionUtils.isNotEmpty(institutionalProposals)) {
            return String.valueOf(institutionalProposals.get(0).getProposalId());
        }
        return StringUtils.EMPTY;
    }

    protected DevelopmentProposal getContinuedFromDevelopmentProposal(String instProposalId) {
        List<ProposalAdminDetails> details = (List<ProposalAdminDetails>) getLegacyDataAdapter().findMatching(ProposalAdminDetails.class, Collections.singletonMap("instProposalId", instProposalId));
        if (CollectionUtils.isNotEmpty(details)) {
            return getDataObjectService().find(DevelopmentProposal.class, details.get(0).getDevProposalNumber());
        }
        return null;
    }

}
