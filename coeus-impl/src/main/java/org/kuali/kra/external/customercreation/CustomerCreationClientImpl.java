/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.external.customercreation;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.kfs.module.external.kc.service.CustomerCreationService;
import org.kuali.kfs.module.external.kc.service.CustomerCreationServiceSOAP;
import org.kuali.kfs.module.external.kc.service.DunningCampaignServiceSOAP;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.api.config.property.ConfigContext;

import javax.xml.ws.WebServiceClient;
import java.net.MalformedURLException;
import java.net.URL;

public final class CustomerCreationClientImpl extends CustomerCreationClientBase {
    
    public final static URL WSDL_LOCATION;
    
    private static CustomerCreationClientImpl client;
    
    private static final Logger LOG = LogManager.getLogger(CustomerCreationClientImpl.class);
    
    private CustomerCreationClientImpl() {
    }

    public static CustomerCreationClientImpl getInstance() {
        if (client == null) {
            client = new CustomerCreationClientImpl();
        }
        return client;
    }
      
    static
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (null == cl) {
            cl = CustomerCreationClientImpl.class.getClassLoader();
        }
        String wsdlPath =  ((WebServiceClient) (DunningCampaignServiceSOAP.class.getAnnotation(WebServiceClient.class))).wsdlLocation();
        WSDL_LOCATION = cl.getResource(wsdlPath); 
    }
    
    @Override
    protected CustomerCreationService getServiceHandle() {
        URL wsdlURL = null;
        
        boolean getFinSystemURLFromWSDL = getParameterService().getParameterValueAsBoolean("KC-AWARD", "Document", Constants.GET_FIN_SYSTEM_URL_FROM_WSDL);
        
        if (getFinSystemURLFromWSDL) {
            wsdlURL = WSDL_LOCATION;
        } else {
            String serviceEndPointUrl = ConfigContext.getCurrentContextConfig().getProperty(Constants.FIN_SYSTEM_INTEGRATION_SERVICE_URL);
            try {
                wsdlURL = new URL(serviceEndPointUrl + SOAP_SERVICE_NAME + "?wsdl");
            } catch (MalformedURLException mue) {
                LOG.error("Could not construct financial system URL from config file: " + mue.getMessage(), mue);
            }
        }
        
        CustomerCreationServiceSOAP ss = new CustomerCreationServiceSOAP(wsdlURL, SERVICE_NAME);
        return ss.getCustomerCreationServicePort();
    }

}
