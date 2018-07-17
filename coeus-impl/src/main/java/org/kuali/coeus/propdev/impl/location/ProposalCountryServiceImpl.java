/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.location;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.rice.location.api.country.CountryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("proposalCountryService")
public class ProposalCountryServiceImpl implements ProposalCountryService {

    private static final Logger LOG = LogManager.getLogger(ProposalCountryServiceImpl.class);

    @Autowired
    @Qualifier("countryService")
    private CountryService countryService;
    
    @Override
    public String convertAltCountryCodeToRealCountryCode(String currentCountryCode) {
        try {
            return getCountryService().getCountryByAlternateCode(currentCountryCode).getCode();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return "";
        }
    }

    public CountryService getCountryService() {
        return countryService;
    }

    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }
    
}
