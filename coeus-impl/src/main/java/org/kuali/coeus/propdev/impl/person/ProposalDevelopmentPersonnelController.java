/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.person;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.notification.impl.NotificationHelper;
import org.kuali.coeus.common.notification.impl.bo.KcNotification;
import org.kuali.coeus.common.questionnaire.framework.answer.QuestionnaireAnswerService;
import org.kuali.coeus.common.view.wizard.framework.WizardControllerService;
import org.kuali.coeus.common.view.wizard.framework.WizardResultsDto;
import org.kuali.coeus.propdev.impl.auth.ProposalDevelopmentDocumentAuthorizer;
import org.kuali.coeus.propdev.impl.coi.CoiConstants;
import org.kuali.coeus.propdev.impl.core.*;
import org.kuali.coeus.common.questionnaire.framework.answer.Answer;
import org.kuali.coeus.common.questionnaire.framework.answer.AnswerHeader;
import org.kuali.coeus.propdev.impl.notification.ProposalDevelopmentNotificationContext;
import org.kuali.coeus.propdev.impl.notification.ProposalDevelopmentNotificationRenderer;
import org.kuali.coeus.propdev.impl.person.attachment.ProposalPersonBiography;
import org.kuali.coeus.common.api.sponsor.hierarchy.SponsorHierarchyService;
import org.kuali.coeus.common.framework.person.PersonTypeConstants;
import org.kuali.coeus.propdev.impl.person.question.ProposalPersonQuestionnaireHelper;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.controller.MethodAccessible;
import org.kuali.rice.krad.web.form.DocumentFormBase;
import org.kuali.rice.krad.web.service.CollectionControllerService;
import org.kuali.rice.krad.web.service.RefreshControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class ProposalDevelopmentPersonnelController extends ProposalDevelopmentControllerBase {

    public static final String PROPOSAL_PERSONS_PATH = "document.developmentProposal.proposalPersons";
    public static final String CERTIFICATION_UPDATE_FEATURE_FLAG = "CERTIFICATION_UPDATE_FEATURE_FLAG";
    public static final String CERTIFICATION_ACTION_TYPE_CODE = "104";
    public static final String CERTIFY_NOTIFICATION = "Certify Notification";
    public static final String PERSON_ROLE = "personRole";
	public static final String CERTIFICATION_ACTION_TYPE_COI = "107";
    private static final String INFO_PROPOSAL_CERTIFIED = "info.proposal.certified";
    private static final String WARN_PROPOSAL_CERTIFIED = "warn.proposal.not.certified";

    @Autowired
    @Qualifier("refreshControllerService")
    private RefreshControllerService refreshControllerService;

    @Autowired
    @Qualifier("wizardControllerService")
    private WizardControllerService wizardControllerService;

    @Autowired
    @Qualifier("questionnaireAnswerService")
    private QuestionnaireAnswerService questionnaireAnswerService;

	@Autowired
    @Qualifier("sponsorHierarchyService")
    private SponsorHierarchyService sponsorHierarchyService;

    @Autowired
    @Qualifier("collectionControllerService")
    private CollectionControllerService collectionControllerService;

	@Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=navigate", "actionParameters[navigateToPageId]=PropDev-PersonnelPage"})
    public ModelAndView navigateToPersonnel(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response)  {
        for (ProposalPerson person : form.getProposalDevelopmentDocument().getDevelopmentProposal().getProposalPersons()) {
            //workaround for the document associated with the OJB retrived dev prop not having a workflow doc.
            person.setDevelopmentProposal(form.getProposalDevelopmentDocument().getDevelopmentProposal());
            person.getQuestionnaireHelper().populateAnswers();
        }
        return super.navigate(form, result, request, response);
    }



    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=checkForNewerVersionOfCertification"})
    public ModelAndView checkForNewerVersionOfCertification(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        Boolean certificationUpdateFeatureFlag = getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_PROPOSAL_DEVELOPMENT, Constants.PARAMETER_COMPONENT_DOCUMENT, CERTIFICATION_UPDATE_FEATURE_FLAG);
        ProposalDevelopmentDocumentAuthorizer proposalDevelopmentDocumentAuthorizer = new ProposalDevelopmentDocumentAuthorizer();

        if (certificationUpdateFeatureFlag && isNewerVersionPublished(form)
                && proposalDevelopmentDocumentAuthorizer.isProposalStateEditableForCertification(form.getDevelopmentProposal())) {
            return getModelAndViewService().showDialog(ProposalDevelopmentConstants.KradConstants.PROP_DEV_PERSONNEL_PAGE_UPDATE_CERTIFICATION_DIALOG,false,form);
        }
       return null;
    }

    protected boolean isNewerVersionPublished(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        for (ProposalPerson person : form.getProposalDevelopmentDocument().getDevelopmentProposal().getProposalPersons()) {
            if (person.getQuestionnaireHelper().getAnswerHeaders().get(0).isNewerVersionPublished()) {
               return true;
            }
        }
        return false;
    }

    @Transactional
    @RequestMapping(value = "/proposalDevelopment", params = "methodToCall=updateCertification")
    public ModelAndView updateCertification(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form)  {
        for (ProposalPerson person : form.getProposalDevelopmentDocument().getDevelopmentProposal().getProposalPersons()) {
            int index = 0;
            for (AnswerHeader answerHeader : person.getQuestionnaireHelper().getAnswerHeaders()) {
                answerHeader.setUpdateOption(form.getUpdateAnswerHeader().getUpdateOption());
                person.getQuestionnaireHelper().updateQuestionnaireAnswer(index);
                index++;
            }
        }

        form.setUpdateAnswerHeader(new AnswerHeader());
        return super.save(form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=prepareAddPersonDialog"})
    public ModelAndView prepareAddPersonDialog(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        form.getAddKeyPersonHelper().setLineType(PersonTypeConstants.EMPLOYEE.getCode());
        return getModelAndViewService().showDialog(ProposalDevelopmentConstants.KradConstants.PROP_DEV_PERSONNEL_PAGE_WIZARD, true, form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=navigateToPersonError"})
    public ModelAndView navigateToPersonError(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response)  {
        form.setAjaxReturnType("update-page");
    	return navigateToPersonnel(form, result, request, response);
    } 
    
    @Override
    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=save", "pageId=PropDev-PersonnelPage"})
    public ModelAndView save(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, BindingResult result, 
    		HttpServletRequest request, HttpServletResponse response) {
        return super.save(form);
    }

   @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=performPersonnelSearch")
   public ModelAndView performPersonnelSearch(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, BindingResult result,
           HttpServletRequest request, HttpServletResponse response) {
       form.getAddKeyPersonHelper().getResults().clear();
       List<Object> results = new ArrayList<>();

       for (Object object : getWizardControllerService().performWizardSearch(form.getAddKeyPersonHelper().getLookupFieldValues(),form.getAddKeyPersonHelper().getLineType())) {
           WizardResultsDto wizardResult = (WizardResultsDto) object;
           String personId = wizardResult.getKcPerson() != null ? wizardResult.getKcPerson().getPersonId() : wizardResult.getRolodex().getRolodexId().toString();
           if (!personAlreadyExists(personId,form.getDevelopmentProposal().getProposalPersons())) {
               results.add(object);
           }
       }

       form.getAddKeyPersonHelper().setResults(results);
       return getRefreshControllerService().refresh(form);
   }

   @Transactional @RequestMapping(value = "/proposalDevelopment", params="methodToCall=addPerson")
   public ModelAndView addPerson(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, BindingResult result,
           HttpServletRequest request, HttpServletResponse response) {
       ProposalPerson newProposalPerson = new ProposalPerson();
       for (Object object : form.getAddKeyPersonHelper().getResults()) {
           WizardResultsDto wizardResult = (WizardResultsDto) object;
           if (wizardResult.isSelected()) {
               if (wizardResult.getKcPerson() != null) {
                   newProposalPerson.setPersonId(wizardResult.getKcPerson().getPersonId());
                   newProposalPerson.setFullName(wizardResult.getKcPerson().getFullName());
                   newProposalPerson.setUserName(wizardResult.getKcPerson().getUserName());
               } else if (wizardResult.getRolodex() != null) {
                   newProposalPerson.setRolodexId(wizardResult.getRolodex().getRolodexId());
                   newProposalPerson.setFullName(wizardResult.getRolodex().getFullName());

               }
               break;
           }
       }

       newProposalPerson.setProposalPersonRoleId((String) form.getAddKeyPersonHelper().getParameter(PERSON_ROLE));
       if (form.getAddKeyPersonHelper().getParameterMap().containsKey(ProposalDevelopmentConstants.Parameters.KEY_PERSON_PROJECT_ROLE)) {
        newProposalPerson.setProjectRole((String) form.getAddKeyPersonHelper().getParameter(ProposalDevelopmentConstants.Parameters.KEY_PERSON_PROJECT_ROLE));
       }

       if (!getKualiRuleService().applyRules(new AddKeyPersonEvent(form.getProposalDevelopmentDocument(),newProposalPerson))) {
           return reportKeyPersonError(form);
       }
       getKeyPersonnelService().addProposalPerson(newProposalPerson, form.getProposalDevelopmentDocument());
       form.getProposalDevelopmentDocument().getDevelopmentProposal().getProposalPersons().sort(ProposalPersonRoleComparator.INSTANCE);
       form.getAddKeyPersonHelper().reset();
       form.setAjaxReturnType(UifConstants.AjaxReturnTypes.UPDATEPAGE.getKey());
       super.save(form);
       return getKcCommonControllerService().closeDialog(ProposalDevelopmentConstants.KradConstants.PROP_DEV_PERSONNEL_PAGE_WIZARD, form);
   }

    protected ModelAndView reportKeyPersonError(ProposalDevelopmentDocumentForm form) {
        form.setAjaxReturnType(UifConstants.AjaxReturnTypes.UPDATECOMPONENT.getKey());
        form.setUpdateComponentId(ProposalDevelopmentConstants.KradConstants.PROP_DEV_PERSONNEL_PAGE_WIZARD);
        return getModelAndViewService().getModelAndView(form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=navigate", "actionParameters[navigateToPageId]=PropDev-CreditAllocationPage"})
    public ModelAndView navigateToCreditAllocation(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, BindingResult result,
                                  HttpServletRequest request, HttpServletResponse response)  {

        ModelAndView mv = super.navigate(form,result,request,response);
        ((ProposalDevelopmentViewHelperServiceImpl) form.getViewHelperService()).populateCreditSplits(form);
        return mv;
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=deleteLine", "pageId=PropDev-PersonnelPage"})
    public ModelAndView deletePerson(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form,
        @RequestParam("actionParameters[" + UifParameters.SELECTED_COLLECTION_PATH + "]") String selectedCollectionPath,
        @RequestParam("actionParameters[" + UifParameters.SELECTED_LINE_INDEX + "]") String selectedLine) {
        if (selectedCollectionPath.equals(PROPOSAL_PERSONS_PATH)) {
            Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(form, selectedCollectionPath);
            Object deleteLine = ((List<Object>) collection).get(Integer.parseInt(selectedLine));

            deleteProposalPersonBios(form.getDevelopmentProposal(), (ProposalPerson) deleteLine);
            deleteCertDetails((ProposalPerson) deleteLine);
        }

        return getCollectionControllerService().deleteLine(form);
    }

    private void deleteCertDetails(ProposalPerson person) {
        if (person.getCertificationDetails() != null) {
            getDataObjectService().delete(person.getCertificationDetails());
        }
    }

    private void deleteProposalPersonBios(DevelopmentProposal proposal, ProposalPerson deleteLine) {
        List<ProposalPersonBiography> tmpBios= new ArrayList<>();
        String personIdOfDeletedLine = deleteLine.getPersonId();
        for (ProposalPersonBiography biography : proposal.getPropPersonBios()) {
            if (personIdOfDeletedLine == null) {
                Integer rolodexId = deleteLine.getRolodexId();
                if (!(rolodexId != null && rolodexId.equals(biography.getRolodexId()))) {
                    tmpBios.add(biography);
                }
            }
            else {
                if (!personIdOfDeletedLine.equals(biography.getPersonId()))
                    tmpBios.add(biography);
            }
        }
        proposal.setPropPersonBios(tmpBios);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params = "methodToCall=clearAnswers")
   public ModelAndView clearAnswers(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result,
           HttpServletRequest request, HttpServletResponse response)  {
	   ProposalDevelopmentDocumentForm pdForm = (ProposalDevelopmentDocumentForm) form;
	   String personNumber = pdForm.getActionParamaterValue("personNumber");
	   for (ProposalPerson person : pdForm.getProposalDevelopmentDocument().getDevelopmentProposal().getProposalPersons()) {
		   if (StringUtils.equals(personNumber, person.getProposalPersonNumber().toString())) {
			   //get the certification questions
			   AnswerHeader ah = person.getQuestionnaireHelper().getAnswerHeaders().get(0);
			   for (Answer answer : ah.getAnswers()) {
				   answer.setAnswer(null);
			   }
		   }
	   }
	   saveUpdatePersonAnswerHeaders(pdForm.getDevelopmentProposal(), request.getParameter(UifParameters.PAGE_ID));
	   return this.save(pdForm, result, request, response);
   }
    @Transactional @RequestMapping(value = "/proposalDevelopment", params = "methodToCall=certificationToggle")
    public ModelAndView certificationToggle(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result,
                                            HttpServletRequest request, HttpServletResponse response)  {
        ProposalDevelopmentDocumentForm pdForm = (ProposalDevelopmentDocumentForm) form;
        String personNumber = pdForm.getActionParamaterValue("personNumber");
        for (ProposalPerson person : pdForm.getProposalDevelopmentDocument().getDevelopmentProposal().getProposalPersons()) {
            if (StringUtils.equals(personNumber, person.getProposalPersonNumber().toString())) {

                person.setOptInCertificationStatus(!person.getOptInCertificationStatus());
                if (!person.getOptInCertificationStatus()){
                    return clearAnswers(form, result, request, response);
                }
            }
        }
        return getRefreshControllerService().refresh(form);
    }


    @Transactional @RequestMapping(value = "/proposalDevelopment", params = "methodToCall=viewCertification")
    public ModelAndView viewCertification(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form,
                                       @RequestParam("actionParameters[" + UifParameters.SELECTED_LINE_INDEX + "]") String selectedLine) {
    ProposalPerson person = form.getDevelopmentProposal().getProposalPerson(Integer.parseInt(selectedLine));
    person.getQuestionnaireHelper().populateAnswers();
    form.setProposalPersonQuestionnaireHelper(person.getQuestionnaireHelper());
    return getModelAndViewService().showDialog("PropDev-SubmitPage-CertificationDetail",true,form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params = "methodToCall=prepareVerifyCertificationDialog")
    public ModelAndView prepareVerifyCertificationDialog(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form,
                                                      @RequestParam("actionParameters[" + UifParameters.SELECTED_LINE_INDEX + "]") String selectedLine)  {
        ProposalPerson person = form.getDevelopmentProposal().getProposalPerson(Integer.parseInt(selectedLine));
        prepareNoticationHelper(form, person);
       if (form.getNotificationHelper().getPromptUserForNotificationEditor(form.getNotificationHelper().getNotificationContext())) {
           return getModelAndViewService().showDialog(ProposalDevelopmentConstants.KradConstants.PROP_DEV_PERSONNEL_PAGE_VERIFY_NOTIFICATION_DIALOG, true, form);
       } else {
           return sendCertificationNotification(form);
       }
    }

    protected void prepareNoticationHelper(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form, ProposalPerson person) {
        NotificationHelper<ProposalDevelopmentNotificationContext> notificationHelper = form.getNotificationHelper();
        notificationHelper.setNotification(getKcNotificationService().createNotificationObject(createNotificationContext(form.getDevelopmentProposal(), person)));
        notificationHelper.setNotificationContext(createNotificationContext(form.getDevelopmentProposal(), person));
        notificationHelper.setNotificationRecipients(createRecipientFromPerson(person));
        notificationHelper.setNewPersonId(person.getPersonId());
    }

    protected ProposalDevelopmentNotificationContext createNotificationContext(DevelopmentProposal developmentProposal, ProposalPerson person) {
    	ProposalDevelopmentNotificationContext context;
    	String sponsorHeirarchy =   getParameterService().getParameterValueAsString(ProposalDevelopmentDocument.class, CoiConstants.COI_SPONSOR_HIERARCHY); 
    	String sponsorHeirarchyLevelName =   getParameterService().getParameterValueAsString(ProposalDevelopmentDocument.class, CoiConstants.COI_SPONSOR_HEIRARCHY_LEVEL1);
    	if (getSponsorHierarchyService().isSponsorInHierarchy(developmentProposal.getSponsorCode(), sponsorHeirarchy,1,sponsorHeirarchyLevelName)) {
    		context = new ProposalDevelopmentNotificationContext(developmentProposal, CERTIFICATION_ACTION_TYPE_COI, CERTIFY_NOTIFICATION);
    	} else {
    		context = new ProposalDevelopmentNotificationContext(developmentProposal, CERTIFICATION_ACTION_TYPE_CODE, CERTIFY_NOTIFICATION);
    	}
    	ProposalDevelopmentNotificationRenderer renderer = (ProposalDevelopmentNotificationRenderer) context.getRenderer();
    	renderer.setDevelopmentProposal(developmentProposal);
    	renderer.setProposalPerson(person);
    	return context;
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params = "methodToCall=sendCertificationNotification")
    public ModelAndView sendCertificationNotification(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        NotificationHelper<ProposalDevelopmentNotificationContext> notificationHelper = form.getNotificationHelper();
        ProposalPerson person =((ProposalDevelopmentNotificationRenderer) notificationHelper.getNotificationContext().getRenderer()).getProposalPerson();
        getKcNotificationService().sendNotification(notificationHelper.getNotificationContext(), notificationHelper.getNotification(),
                notificationHelper.getNotificationRecipients());
        getGlobalVariableService().getMessageMap().putInfoForSectionId(ProposalDevelopmentConstants.KradConstants.PROP_DEV_PERSONNEL_PAGE_COLLECTION, KeyConstants.INFO_NOTIFICATIONS_SENT,
                person.getFullName() + " " + notificationHelper.getNotification().getCreateTimestamp());
        person.setLastNotification(getDateTimeService().getCurrentTimestamp());
        return super.save(form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params = "methodToCall=sendAllCertificationNotifications")
    public ModelAndView sendAllCertificationNotifications(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        int index = 0;
        for (ProposalPerson proposalPerson : form.getDevelopmentProposal().getProposalPersons()) {
            if (proposalPerson.isSelectedPerson() && !isQuestionnaireComplete(proposalPerson.getQuestionnaireHelper())) {
                    sendPersonNotification(form, String.valueOf(index));
            }
            index++;
        }
        return super.save(form);
    }

    public void sendPersonNotification(ProposalDevelopmentDocumentForm form, String selectedLine) {
        ProposalPerson person = form.getDevelopmentProposal().getProposalPerson(Integer.parseInt(selectedLine));
        ProposalDevelopmentNotificationContext context = createNotificationContext(form.getDevelopmentProposal(), person);
        KcNotification notification = getKcNotificationService().createNotificationObject(context);
        getKcNotificationService().sendNotification(context, notification, createRecipientFromPerson(person));
        getGlobalVariableService().getMessageMap().putInfoForSectionId(ProposalDevelopmentConstants.KradConstants.PROP_DEV_PERSONNEL_PAGE_COLLECTION, KeyConstants.INFO_NOTIFICATIONS_SENT, person.getFullName() + " " + notification.getCreateTimestamp());
        person.setLastNotification(getDateTimeService().getCurrentTimestamp());
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=movePersonUp"})
    public ModelAndView movePersonUp(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form,
                                     @RequestParam("actionParameters[" + UifParameters.SELECTED_LINE_INDEX + "]") String selectedLine) {

        swapAdjacentPersonnel(form.getDevelopmentProposal().getProposalPersons(), Integer.parseInt(selectedLine), MoveOperationEnum.MOVING_PERSON_UP);

        return getRefreshControllerService().refresh(form);
    }

    @Transactional @RequestMapping(value = "/proposalDevelopment", params={"methodToCall=movePersonDown"})
    public ModelAndView movePersonDown(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form,
                                     @RequestParam("actionParameters[" + UifParameters.SELECTED_LINE_INDEX + "]") String selectedLine) {

        swapAdjacentPersonnel(form.getDevelopmentProposal().getProposalPersons(), Integer.parseInt(selectedLine), MoveOperationEnum.MOVING_PERSON_DOWN);

        return getRefreshControllerService().refresh(form);
    }

    @MethodAccessible @Transactional @RequestMapping(value = "/proposalDevelopment", params = "methodToCall=closeCertWithSave")
    public ModelAndView closeCertWithSave(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {

        String selectedPersonId = form.getProposalPersonQuestionnaireHelper().getProposalPerson().getPersonId();
        ProposalPersonQuestionnaireHelper proposalPersonQuestionnaireHelper;
        Set<String> personIdsMissingCertification = getPersonIdsMissingCertification(form.getDevelopmentProposal().getProposalPersons(), selectedPersonId);
        boolean complete = false;
        for (ProposalPerson proposalPerson : form.getDevelopmentProposal().getProposalPersons()) {
            if (StringUtils.equals(proposalPerson.getPersonId(),selectedPersonId)) {
                proposalPersonQuestionnaireHelper = proposalPerson.getQuestionnaireHelper();
                List<AnswerHeader> newAnswerHeaders = form.getProposalPersonQuestionnaireHelper().getAnswerHeaders();

                complete = isQuestionnaireComplete(form.getProposalPersonQuestionnaireHelper());
                getBusinessObjectService().save(newAnswerHeaders);
                proposalPersonQuestionnaireHelper.setAnswerHeaders(newAnswerHeaders);

                getKeyPersonnelService().saveCertDetails(proposalPerson, getGlobalVariableService().getUserSession().getPrincipalId(), getDateTimeService().getCurrentTimestamp());
            }
        }

        if (personIdsMissingCertification.size() == 1 && personIdsMissingCertification.contains(selectedPersonId) && complete) {
            sendAllCertificationCompleteNotificationIfEnabled(form.getDevelopmentProposal());
        }

        if (complete && getGlobalVariableService().getMessageMap().hasNoErrors()) {
            getGlobalVariableService().getMessageMap().putInfo(KRADConstants.GLOBAL_MESSAGES, INFO_PROPOSAL_CERTIFIED);
        } else if (!complete) {
            getGlobalVariableService().getMessageMap().putWarning(KRADConstants.GLOBAL_MESSAGES, WARN_PROPOSAL_CERTIFIED);
        }

        releaseLocksForLoggedInUser(form);
        return getNavigationControllerService().returnToHub(form);
    }

    protected Set<String> getPersonIdsMissingCertification(List<ProposalPerson> proposalPeople, String selectedPersonId) {
	    return proposalPeople.stream().filter(person -> {
            if (StringUtils.equals(person.getPersonId(), selectedPersonId)) {
                // If this person is the one open in the certification view, then we need to retrieve their answers
                // from the DB to determine if their certification is complete-- their new answers have already been
                // populated into their questionnaire helper
                return person.getQuestionnaireHelper().getAnswerHeaders().stream()
                        .map(ah -> retrieveCurrentAnswerHeader(ah.getId()))
                        .anyMatch(ah -> ah != null && !ah.isCompleted());
            } else {
                // We can get other peoples' certification answers directly from the questionnaire helper since they
                // aren't being modified as part of the certification view request
                return person.getQuestionnaireHelper().getAnswerHeaders().stream()
                        .anyMatch(ah -> !ah.isCompleted());
            }
        }).map(ProposalPerson::getPersonId).collect(Collectors.toSet());
    }

    @Transactional
    @RequestMapping(value ="/proposalDevelopment",params = "methodToCall=closeCertWithoutSave")
    public ModelAndView closeCertWithoutSave(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        releaseLocksForLoggedInUser(form);
        return getNavigationControllerService().returnToHub(form);
    }

    protected void releaseLocksForLoggedInUser(ProposalDevelopmentDocumentForm form) {
        if (form.getProposalDevelopmentDocument().getPessimisticLocks() != null) {
                getPessimisticLockService().releaseAllLocksForUser(form.getProposalDevelopmentDocument().getPessimisticLocks(), getGlobalVariableService().getUserSession().getPerson());
        }
    }

    @Transactional @RequestMapping(value ="/proposalDevelopment", params = "methodToCall=certifyAnswers")
    public ModelAndView certifyAnswers(@ModelAttribute("KualiForm") ProposalDevelopmentDocumentForm form) {
        return getModelAndViewService().showDialog(ProposalDevelopmentConstants.KradConstants.PROP_DEV_CERT_CLOSE_DIALOG, true, form);
    }

    public boolean isQuestionnaireComplete(ProposalPersonQuestionnaireHelper helper) {
        boolean retVal = true;
        if (helper != null && helper.getAnswerHeaders() != null) {
            for (AnswerHeader ah : helper.getAnswerHeaders()) {
                boolean complete = questionnaireAnswerService.isQuestionnaireAnswerComplete(ah.getAnswers());
                ah.setCompleted(complete);
                retVal &= complete;
            }
        }
        return retVal;
    }

    private enum MoveOperationEnum {
        MOVING_PERSON_DOWN (1),
        MOVING_PERSON_UP(-1);

        private int offset;

        MoveOperationEnum(int offset) {
            this.offset = offset;
        }

        public int getOffset() { return offset; }
    }

    private void swapAdjacentPersonnel(List<ProposalPerson> keyPersonnel, int index1, MoveOperationEnum op) {
        ProposalPerson movingPerson = keyPersonnel.get(index1);

        if ((op == MoveOperationEnum.MOVING_PERSON_DOWN && movingPerson.isMoveDownAllowed()) || (op == MoveOperationEnum.MOVING_PERSON_UP && movingPerson.isMoveUpAllowed())) {
            int index2 = index1 + op.getOffset();
            keyPersonnel.set(index1, keyPersonnel.get(index2));
            keyPersonnel.set(index2, movingPerson);
        }
    }

    protected boolean personAlreadyExists(String personId, List<ProposalPerson> existingPersons) {
        for (ProposalPerson existingPerson: existingPersons ) {
            if (personId.equals(existingPerson.getPersonId())) {
                return true;
            }
        }
        return false;
    }

    public WizardControllerService getWizardControllerService() {
        return wizardControllerService;
    }

    public void setWizardControllerService(WizardControllerService wizardControllerService) {
        this.wizardControllerService = wizardControllerService;
    }

	public static class ProposalPersonRoleComparator implements Comparator<ProposalPerson> {

        private static final Comparator<ProposalPerson> INSTANCE = new ProposalPersonRoleComparator();

        @Override
        public int compare(ProposalPerson person1, ProposalPerson person2) {
			int retval = 0;
			if (person1.isInvestigator() || person2.isInvestigator()) {
				if (person1.isPrincipalInvestigator() || person2.isPrincipalInvestigator()) {
					if (person1.isPrincipalInvestigator()) {
						retval--;
					}
					if (person2.isPrincipalInvestigator()) {
						retval++;
					}
				}
				if (retval == 0) {
					if (person1.isMultiplePi() || person2.isMultiplePi()) {
						if (person1.isMultiplePi()) {
							retval--;
						}
						if (person2.isMultiplePi()) {
							retval++;
						}
					}
				}
			}
			if (retval == 0) {
				if (person1.isCoInvestigator() || person2.isCoInvestigator()) {
					if (person1.isCoInvestigator()) {
						retval--;
					}
					if (person2.isCoInvestigator()) {
						retval++;
					}
				}
			}
			if (retval == 0) {
				if (person1.isKeyPerson() || person2.isKeyPerson()) {
					if (person1.isKeyPerson()) {
						retval--;
					}
					if (person2.isKeyPerson()) {
						retval++;
					}
				}
			}
			return retval;
		}
	}
    
	public SponsorHierarchyService getSponsorHierarchyService() {
		return sponsorHierarchyService;
	}

	public void setSponsorHierarchyService(
			SponsorHierarchyService sponsorHierarchyService) {
		this.sponsorHierarchyService = sponsorHierarchyService;
	}

    public RefreshControllerService getRefreshControllerService() {
        return refreshControllerService;
    }

    public void setRefreshControllerService(RefreshControllerService refreshControllerService) {
        this.refreshControllerService = refreshControllerService;
    }

    public QuestionnaireAnswerService getQuestionnaireAnswerService() {
        return questionnaireAnswerService;
    }

    public void setQuestionnaireAnswerService(QuestionnaireAnswerService questionnaireAnswerService) {
        this.questionnaireAnswerService = questionnaireAnswerService;
    }

    public CollectionControllerService getCollectionControllerService() {
        return collectionControllerService;
    }

    public void setCollectionControllerService(CollectionControllerService collectionControllerService) {
        this.collectionControllerService = collectionControllerService;
    }
}
