/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.coi.impl;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.coeus.coi.framework.ProjectPublisher;
import org.kuali.coeus.coi.framework.ProjectRetrievalService;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.util.KRADConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class CoiProjectAction extends KualiAction {
    private static final Logger LOG = LogManager.getLogger(CoiProjectAction.class);
    private static final String PUSH_MESSAGE_KEY = "info.project.push.started";
    private static final String PROJECT_PUSH = "ProjectPush";

    private transient Collection<ProjectRetrievalService> retrievalServices;
    private transient ProjectPublisher projectPublisher;
    private transient GlobalVariableService globalVariableService;

    public ActionForward publishAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        final String question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (question == null) {
            return this.performQuestionWithoutInput(mapping, form, request, response, PROJECT_PUSH, "Are you sure you want to push all projects to COI", KRADConstants.CONFIRMATION_QUESTION, KRADConstants.MAPPING_CANCEL, "");
        } else if (PROJECT_PUSH.equals(question)) {
            final String buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
            if (ConfirmationQuestion.YES.equals(buttonClicked)) {
                getRetrievalServices().stream()
                        .peek(s -> {
                            if (LOG.isInfoEnabled()) {
                                LOG.info("Retrieving Projects from: " + s.getClass().getName());
                            }
                        })
                        .flatMap(s -> s.retrieveProjects().stream())
                        .forEach(p -> getProjectPublisher().publishProject(p));

                getGlobalVariableService().getMessageList().add(PUSH_MESSAGE_KEY);
            }
        }

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
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

    public Collection<ProjectRetrievalService> getRetrievalServices() {
        if (retrievalServices == null) {
            retrievalServices = KcServiceLocator.getServicesOfType(ProjectRetrievalService.class);
        }

        return retrievalServices;
    }

    public void setRetrievalServices(Collection<ProjectRetrievalService> retrievalServices) {
        this.retrievalServices = retrievalServices;
    }

    public GlobalVariableService getGlobalVariableService() {
        if (globalVariableService == null) {
            globalVariableService = KcServiceLocator.getService(GlobalVariableService.class);
        }

        return globalVariableService;
    }

    public void setGlobalVariableService(GlobalVariableService globalVariableService) {
        this.globalVariableService = globalVariableService;
    }
}
