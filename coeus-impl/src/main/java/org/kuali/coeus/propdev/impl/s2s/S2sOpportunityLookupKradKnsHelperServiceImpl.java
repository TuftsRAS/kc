/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s;


import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentConstants;
import org.kuali.coeus.propdev.impl.s2s.connect.S2sCommunicationException;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component("s2sOpportunityLookupKradKnsHelperService")
public class S2sOpportunityLookupKradKnsHelperServiceImpl implements S2sOpportunityLookupKradKnsHelperService {

    private static final Logger LOG = LogManager.getLogger(S2sOpportunityLookupKradKnsHelperServiceImpl.class);

    @Autowired
    @Qualifier("s2sSubmissionService")
    private S2sSubmissionService s2sSubmissionService;

    @Autowired
    @Qualifier("globalVariableService")
    private GlobalVariableService globalVariableService;

    @Override
    public List<S2sOpportunity> performSearch(String providerCode, String cfdaNumber, String competitionId, String opportunityId, String packageId) {
        if (StringUtils.isBlank(providerCode)) {
            globalVariableService.getMessageMap().putError(ProposalDevelopmentConstants.S2sConstants.PROVIDER_CODE, KeyConstants.ERROR_S2S_PROVIDER_INVALID);
        }

        try {
            return getS2sSubmissionService().searchOpportunity(providerCode, cfdaNumber, opportunityId, competitionId, packageId);
        } catch (S2sCommunicationException e) {
            LOG.error(e.getMessage(), e);
            getGlobalVariableService().getMessageMap().putError(Constants.NO_FIELD, e.getErrorKey(), e.getMessage());
            return Collections.emptyList();
        }
    }

    protected S2sSubmissionService getS2sSubmissionService() {
        return s2sSubmissionService;
    }

    public void setS2sSubmissionService(S2sSubmissionService s2sSubmissionService) {
        this.s2sSubmissionService = s2sSubmissionService;
    }

    public GlobalVariableService getGlobalVariableService() {
        return globalVariableService;
    }

    public void setGlobalVariableService(GlobalVariableService globalVariableService) {
        this.globalVariableService = globalVariableService;
    }
}
