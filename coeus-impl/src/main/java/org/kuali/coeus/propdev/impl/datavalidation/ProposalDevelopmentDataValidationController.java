/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.datavalidation;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentConstants;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentControllerBase;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocumentForm;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentViewHelperServiceImpl;
import org.kuali.rice.krad.uif.UifParameters;

import org.kuali.rice.krad.web.service.RefreshControllerService;
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


@Controller
public class ProposalDevelopmentDataValidationController extends ProposalDevelopmentControllerBase {


    @Autowired
    @Qualifier("refreshControllerService")
    private RefreshControllerService refreshControllerService;

    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=validateData")
    public ModelAndView validateData(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, BindingResult result,
                                           HttpServletRequest request, HttpServletResponse response) {

        if (form.isAuditActivated()) {
            form.setDataValidationItems(((ProposalDevelopmentViewHelperServiceImpl)form.getViewHelperService()).populateDataValidation(form));
        }

        return getModelAndViewService().showDialog(ProposalDevelopmentConstants.KradConstants.DATA_VALIDATION_DIALOG_ID, true, form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=toggleValidation")
    public ModelAndView toggleValidation(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, BindingResult result,
                                     HttpServletRequest request, HttpServletResponse response) {
        form.setAuditActivated(!form.isAuditActivated());
        if(form.isAuditActivated()) {
            form.setDataValidationItems(((ProposalDevelopmentViewHelperServiceImpl)form.getViewHelperService()).populateDataValidation(form));
        }
        return getRefreshControllerService().refresh(form);

    }


    @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=navigateToError")
    public ModelAndView navigateToError(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        String pageId = form.getActionParamaterValue(UifParameters.NAVIGATE_TO_PAGE_ID);
        if (StringUtils.equals(pageId,ProposalDevelopmentDataValidationConstants.CREDIT_ALLOCATION_PAGE_ID)) {
            ((ProposalDevelopmentViewHelperServiceImpl)form.getViewHelperService()).populateCreditSplits(form);
        }

        if (StringUtils.equals(pageId,ProposalDevelopmentDataValidationConstants.QUESTIONNAIRE_PAGE_ID)) {
            ((ProposalDevelopmentViewHelperServiceImpl)form.getViewHelperService()).populateQuestionnaires(form);
        }

        if (StringUtils.equals(pageId,ProposalDevelopmentDataValidationConstants.SUPPLEMENTAL_PAGE_ID)) {
            ((ProposalDevelopmentViewHelperServiceImpl)form.getViewHelperService()).populateCustomData(form);
        }
        getAuditHelper().auditConditionally(form);
        form.setAjaxReturnType("update-page");
        return getNavigationControllerService().navigate(form);
    }

    public RefreshControllerService getRefreshControllerService() {
        return refreshControllerService;
    }

    public void setRefreshControllerService(RefreshControllerService refreshControllerService) {
        this.refreshControllerService = refreshControllerService;
    }
}
