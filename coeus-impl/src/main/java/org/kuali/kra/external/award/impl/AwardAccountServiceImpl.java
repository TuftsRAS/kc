/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.external.award.impl;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.framework.version.VersionStatus;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.external.award.AwardAccountDTO;
import org.kuali.kra.external.award.AwardAccountService;
import org.kuali.kra.external.service.KcDtoService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class implements the award account service.
 */
public class AwardAccountServiceImpl implements AwardAccountService {


    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;

    private static final Logger LOG = LogManager.getLogger(AwardAccountServiceImpl.class);
    private KcDtoService<AwardAccountDTO, Award> awardAccountDtoService;

    /**
     * This method returns all the awards linked to a financial account number and the chart
     * @see org.kuali.kra.external.award.AwardAccountService#getAwardAccount(java.lang.String)
     */
    @Override
    public List<AwardAccountDTO> getAwardAccounts(String financialAccountNumber, String chartOfAccounts) {
        if (ObjectUtils.isNull(financialAccountNumber) || ObjectUtils.isNull(chartOfAccounts)) {
            LOG.warn("One or both of the criteria sent was null.");
            return null;
        }
        List<Award> awards = getAwards(financialAccountNumber, chartOfAccounts);
        return getAwardAccountDTOs(awards); 
    }
    
    protected List<AwardAccountDTO> getAwardAccountDTOs(List<Award> awards) {
        List<AwardAccountDTO> awardDTOs = new ArrayList<AwardAccountDTO>();

        if (ObjectUtils.isNotNull(awards)) {
            for (Award award : awards) {
                awardDTOs.add(awardAccountDtoService.buildDto(award));
            }
            
        } 
        return awardDTOs;
    }


    /**
     * This method returns awards based on the account number and chart of account
     * @param financialAccountNumber
     * @param chartOfAccounts
     * @return
     */
    protected List<Award> getAwards(String financialAccountNumber, String chartOfAccounts) {
        List<Award> awards = new ArrayList<Award>();
        HashMap<String, String> searchCriteria =  new HashMap<String, String>();
        // It is possible to have a null chart of accounts code
        if (ObjectUtils.isNotNull(financialAccountNumber)) {
            searchCriteria.put("accountNumber", financialAccountNumber);  
            searchCriteria.put("financialChartOfAccountsCode", chartOfAccounts);
            // use the awardSequenceStatus to return the latest active award
            searchCriteria.put("awardSequenceStatus", VersionStatus.ACTIVE.name());
            awards = new ArrayList<Award>(businessObjectService.findMatching(Award.class, searchCriteria));
        }
        if (ObjectUtils.isNull(awards) || awards.isEmpty()) {           
            LOG.warn("No award found for the account number " + financialAccountNumber + " and chart " + "chartOfAccounts");            
        }  
        return awards;
    }

    @Override
    public boolean isFinancialRestApiEnabled() {
        return getParameterService().getParameterValueAsBoolean(
                Constants.PARAMETER_MODULE_AWARD,
                ParameterConstants.ALL_COMPONENT,
                Constants.AWARD_POST_ENABLED);
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected KcDtoService<AwardAccountDTO, Award> getAwardAccountDtoService() {
        return awardAccountDtoService;
    }

    public void setAwardAccountDtoService(KcDtoService<AwardAccountDTO, Award> awardAccountDtoService) {
        this.awardAccountDtoService = awardAccountDtoService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

}
