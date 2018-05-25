/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.copy;


import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.propdev.impl.core.*;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.krad.uif.UifParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.Properties;

@Controller
public class ProposalDevelopmentCopyController extends ProposalDevelopmentControllerBase {

    public static final String COPY_DIALOG = "PropDev-CopyDialog-Section";

    @Autowired
    @Qualifier("documentTypeService")
    private DocumentTypeService documentTypeService;

    @Autowired
    @Qualifier("proposalCopyService")
    private ProposalCopyService proposalCopyService;

    @Transactional @RequestMapping(value = "/proposalDevelopment", params = "methodToCall=copy")
    public ModelAndView copy(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        ProposalCopyCriteria proposalCopyCriteria = form.getProposalCopyCriteria();
        if (StringUtils.isNotBlank(proposalCopyCriteria.getLeadUnitNumber())) {
            ProposalDevelopmentDocument proposalDevelopmentDocument = form.getProposalDevelopmentDocument();

            getPessimisticLockService().releaseAllLocksForUser(proposalDevelopmentDocument.getPessimisticLocks(), getGlobalVariableService().getUserSession().getPerson());
            ProposalDevelopmentDocument newDoc = getProposalCopyService().copyProposal(proposalDevelopmentDocument, proposalCopyCriteria);
            if (newDoc != null) {
                return returnToDocument(form, newDoc.getDocumentNumber());
            }
        }
        return null;
    }


    @Transactional @RequestMapping(value = "/proposalDevelopment", params = "methodToCall=displayCopyDialog")
    public ModelAndView displayCopyDialog(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        ((ProposalDevelopmentViewHelperServiceImpl)form.getView().getViewHelperService()).populateQuestionnaires(form);
        return getModelAndViewService().showDialog(COPY_DIALOG,true,form);
    }

    protected  ModelAndView returnToDocument(ProposalDevelopmentDocumentForm form, String newDocNum) {
        String docHandlerUrl = getDocHandlerUrl(form);
        Properties props = new Properties();
        props.put(KewApiConstants.COMMAND_PARAMETER, KewApiConstants.DOCSEARCH_COMMAND);
        props.put(KewApiConstants.DOCUMENT_ID_PARAMETER, newDocNum);
        if (StringUtils.isNotBlank(form.getReturnFormKey())) {
            props.put(UifParameters.FORM_KEY, form.getReturnFormKey());
        }

        return getModelAndViewService().performRedirect(form, docHandlerUrl, props);
    }

    protected String getDocHandlerUrl(ProposalDevelopmentDocumentForm form) {
        DocumentType docType = getDocumentTypeService().getDocumentTypeByName(form.getDocTypeName());
        return docType.getResolvedDocumentHandlerUrl();
    }

    public DocumentTypeService getDocumentTypeService() {
        return documentTypeService;
    }

    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    public ProposalCopyService getProposalCopyService() {
        return proposalCopyService;
    }

    public void setProposalCopyService(ProposalCopyService proposalCopyService) {
        this.proposalCopyService = proposalCopyService;
    }
}
