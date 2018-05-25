/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.basic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.coeus.propdev.impl.core.*;
import org.kuali.coeus.sys.framework.controller.DocHandlerService;
import org.kuali.coeus.sys.framework.controller.UifExportControllerService;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.rice.krad.uif.field.AttributeQueryResult;
import org.kuali.rice.krad.web.form.DocumentFormBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.CollectionControllerService;
import org.kuali.rice.krad.web.service.QueryControllerService;
import org.kuali.rice.krad.web.service.RefreshControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProposalDevelopmentCoreController extends ProposalDevelopmentControllerBase {


	@Autowired
	@Qualifier("uifExportControllerService")
	private UifExportControllerService uifExportControllerService;

	@Autowired
	@Qualifier("collectionControllerService")
	private CollectionControllerService collectionControllerService;

	@Autowired
	@Qualifier("queryControllerService")
	private QueryControllerService queryControllerService;

	@Autowired
	@Qualifier("refreshControllerService")
	private RefreshControllerService refreshControllerService;

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=defaultMapping")
	public ModelAndView defaultMapping(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) {
		return getTransactionalDocumentControllerService().start(form);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=start")
	public ModelAndView start(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		return getTransactionalDocumentControllerService().start(form);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=reload")
	public ModelAndView reload(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return getTransactionalDocumentControllerService().reload(form);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=sessionTimeout")
	public ModelAndView sessionTimeout(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) {
		return getTransactionalDocumentControllerService().sessionTimeout(form);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=addLine")
	public ModelAndView addLine(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
			return getCollectionControllerService().addLine(form); 
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=complete")
	public ModelAndView complete(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) {
		return getTransactionalDocumentControllerService().complete(form);
	}

	@Transactional @RequestMapping(value ="/proposalDevelopment", params = "methodToCall=addBlankLine")
	public ModelAndView addBlankLine(@ModelAttribute("KualiForm") UifFormBase uifForm, HttpServletRequest request,
			HttpServletResponse response) {
		return getCollectionControllerService().addBlankLine(uifForm);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=saveLine")
	public ModelAndView saveLine(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		return getCollectionControllerService().saveLine(form);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=deleteLine")
	public ModelAndView deleteLine(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) {
		return getCollectionControllerService().deleteLine(form);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=back")
	public ModelAndView back(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		return getNavigationControllerService().back(form);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=returnToPrevious")
	public ModelAndView returnToPrevious(@ModelAttribute("KualiForm") UifFormBase form) {
		return getNavigationControllerService().returnToPrevious(form);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=returnToHub")
	public ModelAndView returnToHub(@ModelAttribute("KualiForm") UifFormBase form) {
		return getNavigationControllerService().returnToHub(form);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=returnToHistory")
	public ModelAndView returnToHistory(@ModelAttribute("KualiForm") UifFormBase form, boolean homeFlag) {
		return getNavigationControllerService().returnToHistory(form, false, homeFlag, false);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=refresh")
	public ModelAndView refresh(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return getRefreshControllerService().refresh(form);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=performLookup")
	public ModelAndView performLookup(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) {
		return getQueryControllerService().performLookup(form);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=checkForm")
	public ModelAndView checkForm(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		return getModelAndViewService().checkForm(form);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=performFieldSuggest")
	public @ResponseBody AttributeQueryResult performFieldSuggest(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) {
		return getQueryControllerService().performFieldSuggest(form);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=downloadAttachment")
	public ModelAndView downloadAttachment(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) {
		return getTransactionalDocumentControllerService().downloadAttachment(form, response);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=performFieldQuery")
	public @ResponseBody AttributeQueryResult performFieldQuery(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) {
		return getQueryControllerService().performFieldQuery(form);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=tableCsvRetrieval", produces = {"text/csv"})
	@ResponseBody
	public String tableCsvRetrieval(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		return getUifExportControllerService().tableCsvRetrieval(form, request, response);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=tableXlsRetrieval", produces = {"text/csv"})
	@ResponseBody
	public String tableXlsRetrieval(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		return getUifExportControllerService().tableXlsRetrieval(form, request, response);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=tableXmlRetrieval", produces = {"text/csv"})
	@ResponseBody
	public String tableXmlRetrieval(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		return getUifExportControllerService().tableXmlRetrieval(form, request, response);
	}

	@Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=tableJsonRetrieval")
	public ModelAndView tableJsonRetrieval(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) {
		return getCollectionControllerService().tableJsonRetrieval(form);
	}
	
    @Transactional @RequestMapping(params = "methodToCall=retrieveCollectionPage")
	public ModelAndView retrieveCollectionPage(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) {
		return getCollectionControllerService().retrieveCollectionPage(form);
	}
    
    @Transactional @RequestMapping(params = "methodToCall=supervisorFunctions")
    public ModelAndView supervisorFunctions(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) {
        form.setEvaluateFlagsAndModes(true);
        return getTransactionalDocumentControllerService().supervisorFunctions(form);
    }

    @Transactional @RequestMapping(value ="/proposalDevelopment", params = "methodToCall=closeProposal")
    public ModelAndView closeProposal(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
		if (!form.isCanEditView() || form.isViewOnly()) {
			return closeWithoutSave(form);
		}
		return getModelAndViewService().showDialog(ProposalDevelopmentConstants.KradConstants.PROP_DEV_CLOSE_DIALOG, true, form);
    }

	@Transactional
	@RequestMapping(value ="/proposalDevelopment",params = "methodToCall=closeWithSave")
	public ModelAndView closeWithSave(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
		super.save(form);
		releaseLocksForLoggedInUser(form);
		return getNavigationControllerService().returnToHub(form);
	}

	@Transactional
	@RequestMapping(value ="/proposalDevelopment",params = "methodToCall=closeWithoutSave")
	public ModelAndView closeWithoutSave(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
		releaseLocksForLoggedInUser(form);
		return getNavigationControllerService().returnToHub(form);
	}

	@Transactional @RequestMapping(params="methodToCall=editProposal")
	public ModelAndView editProposal(
			@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
		return getModelAndViewService().performRedirect(form, KcServiceLocator.getService(DocHandlerService.class).getDocHandlerUrl(form.getDocId()) + "&command=displayDocSearchView&docId=" + form.getDocId());

	}

	protected void releaseLocksForLoggedInUser(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
		if (form.getProposalDevelopmentDocument().getPessimisticLocks() != null) {
				getPessimisticLockService().releaseAllLocksForUser(form.getProposalDevelopmentDocument().getPessimisticLocks(), getGlobalVariableService().getUserSession().getPerson());
        }
	}

	public UifExportControllerService getUifExportControllerService() {
		return uifExportControllerService;
	}

	public void setUifExportControllerService(UifExportControllerService uifExportControllerService) {
		this.uifExportControllerService = uifExportControllerService;
	}

	public CollectionControllerService getCollectionControllerService() {
		return collectionControllerService;
	}

	public void setCollectionControllerService(CollectionControllerService collectionControllerService) {
		this.collectionControllerService = collectionControllerService;
	}

	public QueryControllerService getQueryControllerService() {
		return queryControllerService;
	}

	public void setQueryControllerService(QueryControllerService queryControllerService) {
		this.queryControllerService = queryControllerService;
	}

	public RefreshControllerService getRefreshControllerService() {
		return refreshControllerService;
	}

	public void setRefreshControllerService(RefreshControllerService refreshControllerService) {
		this.refreshControllerService = refreshControllerService;
	}
}
