/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s.connect;

import gov.grants.apply.services.applicantwebservices_v2.*;
import gov.grants.apply.system.applicantcommonelements_v1.OpportunityFilter;
import gov.grants.apply.system.applicantcommonelements_v1.SubmissionFilter;
import gov.grants.apply.system.applicantcommonelements_v1.SubmissionFilterType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.configuration.security.FiltersType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.kuali.coeus.propdev.api.s2s.S2SConfigurationService;
import org.kuali.coeus.sys.framework.util.JaxbUtils;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.s2sgen.api.core.ConfigurationConstants;
import org.kuali.rice.krad.data.DataObjectService;

import gov.grants.apply.services.applicantwebservices_v2_0.ApplicantWebServicesPortType;
import gov.grants.apply.services.applicantwebservices_v2_0.ErrorMessage;
import gov.grants.apply.system.grantscommonelements_v1.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.activation.DataHandler;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.security.*;
import java.util.*;

/**
 * 
 * This class is used to make web service call to grants.gov
 */
public class S2SConnectorServiceBase implements S2SConnectorService {

    protected static final Logger LOG = LogManager.getLogger(S2SConnectorServiceBase.class);

    @Autowired
    @Qualifier("dataObjectService")
    private DataObjectService dataObjectService;

    @Autowired
    @Qualifier("s2SConfigurationService")
    private S2SConfigurationService s2SConfigurationService;

    protected S2SConfigurationReader s2SConfigurationReader;

    @Override
    public GetOpportunityListResponse getOpportunityList(String cfdaNumber, String opportunityId, String competitionId, String packageId)
            throws S2sCommunicationException {
        final ApplicantWebServicesPortType port = configureApplicantIntegrationSoapPort(null,false);
        final GetOpportunityListRequest request = new GetOpportunityListRequest();
        request.setPackageID(packageId);

        if (StringUtils.isBlank(packageId) && (StringUtils.isNotBlank(cfdaNumber) || StringUtils.isNotBlank(competitionId) || StringUtils.isNotBlank(opportunityId))) {
            final OpportunityFilter filter = new OpportunityFilter();
            filter.setCFDANumber(cfdaNumber);
            filter.setCompetitionID(competitionId);
            filter.setFundingOpportunityNumber(opportunityId);
            request.setOpportunityFilter(filter);
        }

        try {
            debugLogJaxbObject(GetOpportunityListRequest.class, request);
            final GetOpportunityListResponse response = port.getOpportunityList(request);
            debugLogJaxbObject(GetOpportunityListResponse.class, response);
            return response;
        } catch(SOAPFaultException soapFault){
            LOG.error("Error while getting list of opportunities", soapFault);
            if(soapFault.getMessage().contains("Connection refused")){
                throw new S2sCommunicationException(KeyConstants.ERROR_GRANTSGOV_OPP_SER_UNAVAILABLE,soapFault.getMessage());
            }else{
                throw new S2sCommunicationException(KeyConstants.ERROR_S2S_UNKNOWN,soapFault.getMessage());
            }
        }catch (ErrorMessage|WebServiceException e) {
            LOG.error("Error while getting list of opportunities", e);
            throw new S2sCommunicationException(KeyConstants.ERROR_S2S_UNKNOWN, e.getMessage());
        }
    }

    @Override
    public GetApplicationInfoResponse getApplicationInfo(String ggTrackingId, String proposalNumber)
            throws S2sCommunicationException {
        ApplicantWebServicesPortType port = getApplicantIntegrationSoapPort(proposalNumber);
        GetApplicationInfoRequest request = new GetApplicationInfoRequest();
        request.setGrantsGovTrackingNumber(ggTrackingId);
        try {
            debugLogJaxbObject(GetApplicationInfoRequest.class, request);
            final GetApplicationInfoResponse response = port.getApplicationInfo(request);
            debugLogJaxbObject(GetApplicationInfoResponse.class, response);
            return response;
        } catch (ErrorMessage|WebServiceException e) {
            LOG.error("Error while getting proposal submission status details", e);
            throw new S2sCommunicationException(KeyConstants.ERROR_GRANTSGOV_SERVER_STATUS_REFRESH,e.getMessage());
        }
    }

    @Override
    public GetSubmissionListResponse getSubmissionList(String opportunityId, String ggTrackingId, String packageId, String submissionTitle, String status, String proposalNumber)
            throws S2sCommunicationException {
        GetSubmissionListRequest request = new GetSubmissionListRequest();
        List<SubmissionFilter> filterList = request.getSubmissionFilter();

        if (StringUtils.isNotBlank(opportunityId)) {
            SubmissionFilter submissionFilter = new SubmissionFilter();
            submissionFilter.setType(SubmissionFilterType.FUNDING_OPPORTUNITY_NUMBER);
            submissionFilter.setValue(opportunityId);
            filterList.add(submissionFilter);
        }

        if (StringUtils.isNotBlank(ggTrackingId)) {
            SubmissionFilter submissionFilter = new SubmissionFilter();
            submissionFilter.setType(SubmissionFilterType.GRANTS_GOV_TRACKING_NUMBER);
            submissionFilter.setValue(ggTrackingId);
            filterList.add(submissionFilter);
        }

        if (StringUtils.isNotBlank(packageId)) {
            SubmissionFilter submissionFilter = new SubmissionFilter();
            submissionFilter.setType(SubmissionFilterType.PACKAGE_ID);
            submissionFilter.setValue(packageId);
            filterList.add(submissionFilter);
        }

        if (StringUtils.isNotBlank(submissionTitle)) {
            SubmissionFilter submissionFilter = new SubmissionFilter();
            submissionFilter.setType(SubmissionFilterType.SUBMISSION_TITLE);
            submissionFilter.setValue(submissionTitle);
            filterList.add(submissionFilter);
        }

        if (StringUtils.isNotBlank(status)) {
            SubmissionFilter submissionFilter = new SubmissionFilter();
            submissionFilter.setType(SubmissionFilterType.STATUS);
            submissionFilter.setValue(status);
            filterList.add(submissionFilter);
        }

        ApplicantWebServicesPortType port = getApplicantIntegrationSoapPort(proposalNumber);
        try {
            debugLogJaxbObject(GetSubmissionListRequest.class, request);
            final GetSubmissionListResponse response = port.getSubmissionList(request);
            debugLogJaxbObject(GetSubmissionListResponse.class, response);
            return response;
        }
        catch (ErrorMessage|WebServiceException e) {
            LOG.error("Error occurred while fetching application list", e);
            throw new S2sCommunicationException(KeyConstants.ERROR_GRANTSGOV_SERVER_APPLICATION_LIST_REFRESH,e.getMessage());
        }
    }

    /**
     * This method is to submit a proposal to grants.gov
     * 
     * @param xmlText xml format of the form object.
     * @param attachments attachments of the proposal.
     * @param proposalNumber proposal number.
     * @return SubmitApplicationResponse corresponding to the input parameters passed.
     * @throws S2sCommunicationException
     * @see org.kuali.coeus.propdev.impl.s2s.connect.S2SConnectorService#submitApplication(java.lang.String, java.util.Map, java.lang.String)
     */
    @Override
    public SubmitApplicationResponse submitApplication(String xmlText, Map<String, DataHandler> attachments, String proposalNumber)
            throws S2sCommunicationException {
        ApplicantWebServicesPortType port = getApplicantIntegrationSoapPort(proposalNumber);
        SubmitApplicationRequest request = new SubmitApplicationRequest();

        for (Map.Entry<String, DataHandler> entry : attachments.entrySet()) {
            DataHandler dataHandler = entry.getValue();
            Attachment attachment = new Attachment();
            attachment.setFileContentId(entry.getKey());
            attachment.setFileDataHandler(dataHandler);
            request.getAttachment().add(attachment);
        }
        request.setGrantApplicationXML(xmlText);
        
        try {
            debugLogJaxbObject(SubmitApplicationRequest.class, request);
            SubmitApplicationResponse response =  port.submitApplication(request);
            debugLogJaxbObject(SubmitApplicationResponse.class, response);
            return response;
        }catch (ErrorMessage e) {
            LOG.error("Error occurred while submitting proposal to Grants Gov", e);
            throw new S2sCommunicationException(KeyConstants.ERROR_GRANTSGOV_SERVER_SUBMIT_APPLICATION,e.getMessage());
        }catch(WebServiceException e){
            LOG.error("Error occurred while submitting proposal to Grants Gov", e);
            throw new S2sCommunicationException(KeyConstants.ERROR_S2S_UNKNOWN,e.getMessage());
        }
    }

    private <T> void debugLogJaxbObject(Class<? extends T> clazz, T o) {
        if (LOG.isDebugEnabled()) {
            try {
                LOG.debug(JaxbUtils.toString(clazz, o));
            } catch (JAXBException e) {
                LOG.debug("Unable to marshall object", e);
            }
        }
    }

    /**
     * 
     * This method is to get Soap Port in case of multicampus
     * 
     * @param proposalNumber Proposal number.
     * @return ApplicantIntegrationPortType Soap port used for applicant integration.
     * @throws S2sCommunicationException
     */
    protected ApplicantWebServicesPortType getApplicantIntegrationSoapPort(String proposalNumber) throws S2sCommunicationException {
        DevelopmentProposal pdDoc = dataObjectService.find(
                DevelopmentProposal.class, proposalNumber);
        Boolean multiCampusEnabled = s2SConfigurationService.getValueAsBoolean(ConfigurationConstants.MULTI_CAMPUS_ENABLED);
        return configureApplicantIntegrationSoapPort(pdDoc.getApplicantOrganization().getOrganization().getDunsNumber(), multiCampusEnabled);
    }
    

    /**
     * 
     * This method is to get Soap Port
     * 
     * @return ApplicantIntegrationPortType Soap port used for applicant integration.
     * @throws S2sCommunicationException
     */
    protected ApplicantWebServicesPortType configureApplicantIntegrationSoapPort(String alias,boolean mulitCampusEnabled)
                                                                                throws S2sCommunicationException {
        System.clearProperty("java.protocol.handler.pkgs");
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress(getS2SSoapHost());
        factory.setServiceClass(ApplicantWebServicesPortType.class);
        // enabling mtom from V2 onwards
        // works for grants.gov but not for research.gov, get a mime related error.
        //Couldn't find MIME boundary: --uuid
        //disable for research.gov. This is not a big deal because submissions with attachments
        // go to grants.gov anyways and not to research.gov
        if (!StringUtils.equalsIgnoreCase(s2SConfigurationReader.getServiceHost(), Constants.RESEARCH_GOV_SERVICE_HOST)) {
            Map<String,Object> properties = new HashMap<>();
            properties.put("mtom-enabled", Boolean.TRUE);
            factory.setProperties(properties);
        }
        
        ApplicantWebServicesPortType applicantWebService = (ApplicantWebServicesPortType)factory.create();
        Client client = ClientProxy.getClient(applicantWebService); 
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(0);
        httpClientPolicy.setReceiveTimeout(0);
        httpClientPolicy.setAllowChunking(false);
        HTTPConduit conduit = (HTTPConduit) client.getConduit();
        conduit.setClient(httpClientPolicy);
        TLSClientParameters tlsConfig = new TLSClientParameters();
        setPossibleCypherSuites(tlsConfig);
        configureKeyStoreAndTrustStore(tlsConfig, alias, mulitCampusEnabled);
        conduit.setTlsClientParameters(tlsConfig);
        return applicantWebService;
    }


    protected void setPossibleCypherSuites(TLSClientParameters tlsConfig) {
        FiltersType filters = new FiltersType();
        filters.getInclude().add("SSL_RSA_WITH_RC4_128_MD5");
        filters.getInclude().add("SSL_RSA_WITH_RC4_128_SHA");
        filters.getInclude().add("SSL_RSA_WITH_3DES_EDE_CBC_SHA");
        filters.getInclude().add("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA");
        filters.getInclude().add("TLS_DHE_RSA_WITH_AES_128_CBC_SHA");
        filters.getInclude().add("TLS_DHE_DSS_WITH_AES_128_CBC_SHA256");
        filters.getInclude().add(".*_EXPORT_.*");
        filters.getInclude().add(".*_EXPORT1024_.*");
        filters.getInclude().add(".*_WITH_DES_.*");
        filters.getInclude().add(".*_WITH_3DES_.*");
        filters.getInclude().add(".*_WITH_RC4_.*");
        filters.getInclude().add(".*_WITH_NULL_.*");
        filters.getInclude().add(".*_DH_anon_.*");

        tlsConfig.setDisableCNCheck(s2SConfigurationReader.getDisableCNCheck());
        tlsConfig.setSecureSocketProtocol(s2SConfigurationReader.getCertAlgorithm());
        tlsConfig.setCipherSuitesFilter(filters);
    }

    /**
     * This method is to confgiure KeyStore and Truststore for Grants.Gov webservice client
     */
    protected void configureKeyStoreAndTrustStore(TLSClientParameters tlsConfig, String alias, boolean mulitCampusEnabled)
            throws S2sCommunicationException {
        KeyStore keyStore = s2SConfigurationReader.getKeyStore();
        KeyManagerFactory keyManagerFactory;
        try {
            keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            if (alias != null && mulitCampusEnabled) {
                KeyStore keyStoreAlias = s2SConfigurationReader.getKeyStoreAlias(alias);
                keyManagerFactory.init(keyStoreAlias, s2SConfigurationReader.getKeyStorePassword().toCharArray());
            }else {
                keyManagerFactory.init(keyStore, s2SConfigurationReader.getKeyStorePassword().toCharArray());
            }
            KeyManager[] km = keyManagerFactory.getKeyManagers();
            tlsConfig.setKeyManagers(km);
            KeyStore trustStore = s2SConfigurationReader.getTrustStore();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            TrustManager[] tm = trustManagerFactory.getTrustManagers();
            tlsConfig.setTrustManagers(tm);
        }catch (NoSuchAlgorithmException|KeyStoreException|UnrecoverableKeyException e){
            LOG.error(e.getMessage(), e);
            throw new S2sCommunicationException(KeyConstants.ERROR_KEYSTORE_CONFIG,e.getMessage());
        }
    }

    /**
     * 
     * This method returns the Host URL for S2S web services
     * 
     * @return {@link String} host URL
     * @throws S2sCommunicationException if unable to read property file
     */
    protected String getS2SSoapHost() throws S2sCommunicationException {
        StringBuilder host = new StringBuilder();
        host.append(s2SConfigurationReader.getServiceHost());
        String port = s2SConfigurationReader.getServicePort();
        if ((!host.toString().endsWith("/")) && (!port.startsWith("/"))) {
            host.append("/");
        }
        host.append(port);
        return host.toString();
    }

    public S2SConfigurationReader getS2SConfigurationReader() {
        return s2SConfigurationReader;
    }

    public void setS2SConfigurationReader(S2SConfigurationReader s2SConfigurationReader) {
        this.s2SConfigurationReader = s2SConfigurationReader;
    }

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    public S2SConfigurationService getS2SConfigurationService() {
        return s2SConfigurationService;
    }

    public void setS2SConfigurationService(S2SConfigurationService s2SConfigurationService) {
        this.s2SConfigurationService = s2SConfigurationService;
    }
}
