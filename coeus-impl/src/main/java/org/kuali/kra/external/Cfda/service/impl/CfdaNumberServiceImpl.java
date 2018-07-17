/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.external.Cfda.service.impl;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.CFDA;
import org.kuali.kra.external.Cfda.CfdaDTO;
import org.kuali.kra.external.Cfda.service.CfdaNumberService;
import org.kuali.kra.external.HashMapElement;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is used for querying CFDA data from KC.
 */
public class CfdaNumberServiceImpl implements CfdaNumberService {

    private BusinessObjectService businessObjectService;
    private static final Logger LOG = LogManager.getLogger(CfdaNumberServiceImpl.class);
    
    /**
     * This method is used to return the cfda number of an award.
     */
    @Override
    public List<CfdaDTO> getCfdaNumber(String financialAccountNumber, String financialChartOfAccounts) {
        final List<Award> awards = getAwards(financialAccountNumber, financialChartOfAccounts);
        if (ObjectUtils.isNotNull(awards)) {
            return awards.stream().flatMap(award -> award.getAwardCfdas().stream()).map(awardCfda -> {
                    final CFDA cfda = businessObjectService.findByPrimaryKey(CFDA.class, Collections.singletonMap("cfdaNumber", awardCfda.getCfdaNumber()));
                    if (ObjectUtils.isNotNull(cfda)) {
                        final CfdaDTO cfdaDTO = boToDTO(cfda);
                        cfdaDTO.setAwardId(awardCfda.getAwardId() + "");
                        return cfdaDTO;
                    }
                    return null;
            }).filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }


    @Override
    public List<CfdaDTO> lookupCfda(List<HashMapElement> criteria) {
        HashMap<String, String> searchCriteria =  new HashMap<>();
        final List<CFDA> cfdaNumbers;
        // if the criteria passed is null, then return all units.
        if (ObjectUtils.isNull(criteria)) {
            cfdaNumbers =  new ArrayList<>(businessObjectService.findAll(CFDA.class));
        } else {
            // Reconstruct Hashmap from object list
            for (HashMapElement element : criteria) {
                searchCriteria.put(element.getKey(), element.getValue());  
            }
            cfdaNumbers =  new ArrayList<>(businessObjectService.findMatching(CFDA.class, searchCriteria));
        }
        
        List<CfdaDTO> cfdaDTOs = new ArrayList<>();
        for (CFDA cfda : cfdaNumbers) {
            cfdaDTOs.add(boToDTO(cfda));
        }
        
        return cfdaDTOs;
    }

    protected CfdaDTO boToDTO(CFDA cfda) {
        CfdaDTO cfdaDTO = new CfdaDTO();
        cfdaDTO.setCfdaMaintenanceTypeId(cfda.getCfdaMaintenanceTypeId());
        cfdaDTO.setCfdaNumber(cfda.getCfdaNumber());
        cfdaDTO.setCfdaProgramTitleName(cfda.getCfdaProgramTitleName());
        cfdaDTO.setActive(cfda.getActive());
        return cfdaDTO;
    }
    
    /**
     * This method returns awards based on the account number and chart of account.
     */
    protected List<Award> getAwards(String financialAccountNumber, String chartOfAccounts) {
        List<Award> awards;
        HashMap<String, String> searchCriteria =  new HashMap<>();
        searchCriteria.put("accountNumber", financialAccountNumber);  
        searchCriteria.put("financialChartOfAccountsCode", chartOfAccounts);
        awards = new ArrayList<>(businessObjectService.findMatching(Award.class, searchCriteria));
        if (ObjectUtils.isNotNull(awards) && !awards.isEmpty()) {
            return awards;
        } else {
            LOG.warn("No award found for the account number " + financialAccountNumber + " and chart " + "chartOfAccounts");            
            return null;
        }   
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}
