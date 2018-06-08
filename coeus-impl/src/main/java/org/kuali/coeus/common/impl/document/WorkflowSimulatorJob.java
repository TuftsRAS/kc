package org.kuali.coeus.common.impl.document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.coeus.common.api.document.service.WorkflowDetailsService;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class WorkflowSimulatorJob extends QuartzJobBean  {
    private static final Log LOG = LogFactory.getLog(WorkflowSimulatorJob.class);
    private WorkflowDetailsService workflowDetailsService;
    private String user;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("Starting workflow simulation on enroute documents.");
        UserSession userSession = new UserSession(user);
        GlobalVariables.setUserSession(userSession);
        getWorkflowDetailsService().simulateWorkflowOnAllDocuments();
        LOG.info("Done with workflow simulation on enroute documents.");
    }

    public WorkflowDetailsService getWorkflowDetailsService() {
        return workflowDetailsService;
    }

    public void setWorkflowDetailsService(WorkflowDetailsService workflowDetailsService) {
        this.workflowDetailsService = workflowDetailsService;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
