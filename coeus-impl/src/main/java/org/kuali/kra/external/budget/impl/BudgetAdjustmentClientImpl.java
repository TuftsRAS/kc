/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.external.budget.impl;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.kfs.module.external.kc.service.BudgetAdjustmentService;
import org.kuali.kfs.module.external.kc.service.BudgetAdjustmentServiceSOAP;
import org.kuali.kra.infrastructure.Constants;

import javax.xml.ws.WebServiceClient;
import java.net.MalformedURLException;
import java.net.URL;



/**
 * This class is an implementation of the BudgetAdjustmentClient. 
 * This implementation is used to communicate with the financial system
 * via SOAP.
 */
public class BudgetAdjustmentClientImpl extends BudgetAdjustmentClientBase {
    
    public final static URL WSDL_LOCATION;
    
    private static BudgetAdjustmentClientImpl client;

    private static final Logger LOG = LogManager.getLogger(BudgetAdjustmentClientImpl.class);

    
    private BudgetAdjustmentClientImpl() {
    }

    public static BudgetAdjustmentClientImpl getInstance() {
        if (client == null) {
            client = new BudgetAdjustmentClientImpl();
        }
        return client;
      }

    static
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (null == cl) {
            cl = BudgetAdjustmentClientImpl.class.getClassLoader();
        }
        String wsdlPath =  ((WebServiceClient) (BudgetAdjustmentServiceSOAP.class.getAnnotation(WebServiceClient.class))).wsdlLocation();
        WSDL_LOCATION = cl.getResource(wsdlPath); 
    }
    
    @Override
    protected BudgetAdjustmentService getServiceHandle() {
        URL wsdlURL = null;
        
        boolean getFinSystemURLFromWSDL = getParameterService().getParameterValueAsBoolean("KC-AWARD", "Document", Constants.GET_FIN_SYSTEM_URL_FROM_WSDL);
        
        if (getFinSystemURLFromWSDL) {
            wsdlURL = WSDL_LOCATION;
        } else {
            String serviceEndPointUrl = getConfigurationService().getPropertyValueAsString(Constants.FIN_SYSTEM_INTEGRATION_SERVICE_URL);
            try {
                wsdlURL = new URL(serviceEndPointUrl + SOAP_SERVICE_NAME + "?wsdl");
            } catch (MalformedURLException mue) {
                LOG.error("Could not construct financial system URL from config file: " + mue.getMessage(), mue);
            }
        }
        
        BudgetAdjustmentServiceSOAP ss = new BudgetAdjustmentServiceSOAP(wsdlURL, SERVICE_NAME);
        return ss.getBudgetAdjustmentServicePort();  
    }


}
