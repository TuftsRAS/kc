/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.person.citi;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.framework.person.citi.CitiDataLoadingService;
import org.kuali.coeus.common.framework.person.citi.CitiDataProcessingService;
import org.kuali.coeus.common.framework.person.citi.CitiJob;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.rice.krad.UserSession;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component("citiJob")
public class CitiJobImpl extends QuartzJobBean implements CitiJob  {

    private static final Logger LOG = LogManager.getLogger(CitiJobImpl.class);
    private static final String KC_SYSTEM_PRINCIPAL_NM = "kc";

    private CitiDataProcessingService citiDataProcessingService;
    private GlobalVariableService globalVariableService;
    private CitiDataLoadingService citiDataLoadingService;

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("Starting CITI job");

        getGlobalVariableService().doInNewGlobalVariables(new UserSession(KC_SYSTEM_PRINCIPAL_NM), () -> {
            getCitiDataLoadingService().loadRecords();
            getCitiDataProcessingService().processRecords();
            return null;
        });

        LOG.info("Finished CITI job");
    }

    public CitiDataProcessingService getCitiDataProcessingService() {
        return citiDataProcessingService;
    }

    public void setCitiDataProcessingService(CitiDataProcessingService citiDataProcessingService) {
        this.citiDataProcessingService = citiDataProcessingService;
    }

    public GlobalVariableService getGlobalVariableService() {
        return globalVariableService;
    }

    public void setGlobalVariableService(GlobalVariableService globalVariableService) {
        this.globalVariableService = globalVariableService;
    }

    public CitiDataLoadingService getCitiDataLoadingService() {
        return citiDataLoadingService;
    }

    public void setCitiDataLoadingService(CitiDataLoadingService citiDataLoadingService) {
        this.citiDataLoadingService = citiDataLoadingService;
    }

}
