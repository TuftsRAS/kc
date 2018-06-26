/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s.nih;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import gov.nih.era.external.SubmissionValidationServiceStub;
import gov.nih.era.external.ValidateApplicationError;
import gov.nih.era.svs.types.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.kuali.coeus.propdev.api.s2s.S2SConfigurationService;

import org.kuali.coeus.propdev.impl.s2s.connect.S2sCommunicationException;
import org.kuali.coeus.s2sgen.api.core.ConfigurationConstants;
import org.kuali.coeus.s2sgen.api.generate.AttachmentData;
import org.kuali.coeus.sys.framework.util.JaxbUtils;
import org.kuali.kra.infrastructure.KeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.bind.JAXBException;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component("nihSubmissionValidationService")
public class NihSubmissionValidationServiceImpl implements NihSubmissionValidationService {

    private static final String KEY_TYPE = "JKS";
    private static final String NIH_GOV_S2S_CN_CHECK = "nih.gov.s2s.cn.check";
    private static final String NIH_GOV_S2S_KEYSTORE_PASSWORD = "nih.gov.s2s.keystore.password";
    private static final String NIH_GOV_S2S_KEYSTORE_LOCATION = "nih.gov.s2s.keystore.location";
    private static final String NIH_GOV_S2S_TRUSTSTORE_LOCATION = "nih.gov.s2s.truststore.location";
    private static final String NIH_GOV_S2S_HOST = "nih.gov.s2s.host";
    private static final String NIH_GOV_S2S_PORT = "nih.gov.s2s.port";
    private static final String NIH_GOV_S2S_TRUSTSTORE_PASSWORD = "nih.gov.s2s.truststore.password";
    private static final String ENABLE_NIH_VALIDATION_SERVICE = "Enable_NIH_Validation_Service";
    private static final String ENABLE_NIH_VALIDATION_SERVICE_CACHING = "Enable_NIH_Validation_Service_Caching";
    private static final String APPLICATION_PDF = "application/pdf";

    private static final Logger LOG = LogManager.getLogger(NihSubmissionValidationServiceImpl.class);
    private static final String ERROR_NIH_VALIDATION_SERVICE_UNKNOWN = "error.nih.validation.service.unknown";

    private static final Cache<Integer, ValidateApplicationResponse> REQUEST_CACHE = CacheBuilder.newBuilder()
            .maximumSize(500)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    @Autowired
    @Qualifier("s2SConfigurationService")
    private S2SConfigurationService s2SConfigurationService;

    @Override
    public List<ValidationMessageDto>  validateApplication(String xmlText, List<AttachmentData> attachments, String dunsNumber) {

        if (StringUtils.isBlank(xmlText) || !s2SConfigurationService.getValueAsBoolean(ENABLE_NIH_VALIDATION_SERVICE)) {
            return Collections.emptyList();
        } else {
            if (!s2SConfigurationService.getValueAsBoolean(ENABLE_NIH_VALIDATION_SERVICE_CACHING)) {
                return toDtos(callValidateApplication(xmlText, attachments, dunsNumber));
            } else {
                try {
                    return toDtos(REQUEST_CACHE.get(Objects.hash(xmlText, attachments, dunsNumber), () -> callValidateApplication(xmlText, attachments, dunsNumber)));
                } catch (ExecutionException e) {
                    throw new S2sCommunicationException(ERROR_NIH_VALIDATION_SERVICE_UNKNOWN, e);
                }
            }
        }
    }

    protected ValidateApplicationResponse callValidateApplication(String xmlText, List<AttachmentData> attachments, String dunsNumber) {
        final ValidateApplicationRequest parameters = new ValidateApplicationRequest();
        parameters.setGrantApplicationXML(xmlText);

        if (attachments != null) {
            final List<AttachmentMetaData> attachmentMetaDatas = parameters.getAttachmentMetaData();
            attachments.stream().map(this::toAttachmentMetaData).forEach(attachmentMetaDatas::add);
        }

        try {
            debugLogJaxbObject(ValidateApplicationRequest.class, parameters);
            final ValidateApplicationResponse response = createConfiguredService(dunsNumber).validateApplication(parameters);
            if (response.getValidationMessageList() == null) {
                response.setValidationMessageList(new ValidationMessageList());
            }
            debugLogJaxbObject(ValidateApplicationResponse.class, response);
            return response;
        } catch (ValidateApplicationError | SOAPFaultException validateApplicationError) {
            throw new S2sCommunicationException(ERROR_NIH_VALIDATION_SERVICE_UNKNOWN, validateApplicationError);
        }
    }

    private List<ValidationMessageDto> toDtos(ValidateApplicationResponse response) {
        if (response == null || response.getValidationMessageList() == null) {
            return Collections.emptyList();
        } else {
            return response.getValidationMessageList().getValidationMessage()
                    .stream()
                    .map(vm -> {
                        final ValidationMessageDto dto = new ValidationMessageDto();
                        dto.setValidationSubApplicationGroupID(vm.getValidationSubApplicationGroupID());
                        dto.setValidationSubApplicationID(vm.getValidationSubApplicationID());
                        dto.setValidationRuleNumber(vm.getValidationRuleNumber());
                        dto.setValidationSeverityCode(vm.getValidationSeverityCode());
                        dto.setValidationMessageText(vm.getValidationMessageText());
                        dto.setValidationMessageId(vm.getValidationMessageId());
                        dto.setFormName(vm.getFormName());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }
    }

    private AttachmentMetaData toAttachmentMetaData(AttachmentData attachment) {
        final AttachmentMetaData attachmentMetaData = new AttachmentMetaData();
        attachmentMetaData.setFileLocation(attachment.getContentId());
        attachmentMetaData.setFileName(attachment.getFileName());
        attachmentMetaData.setMimeType(attachment.getFileName().toLowerCase().endsWith(".pdf") ? APPLICATION_PDF : attachment.getContentType());
        attachmentMetaData.setSizeInBytes(attachment.getContent().length);
        attachmentMetaData.setPageCount(getPageCount(attachment));
        return attachmentMetaData;
    }

    private int getPageCount(AttachmentData attachment) {
        try (final PDDocument document = PDDocument.load(attachment.getContent())) {
            return document.getNumberOfPages();
        } catch (IOException|RuntimeException e) {
            LOG.warn(e);
            return 1;
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

    SubmissionValidationServiceStub createConfiguredService(String dunsNumber) {
        final Boolean multiCampusEnabled = s2SConfigurationService.getValueAsBoolean(ConfigurationConstants.MULTI_CAMPUS_ENABLED);

        final SubmissionValidationServiceStub applicantWebService;

        final TLSClientParameters tlsConfig = new TLSClientParameters();
        tlsConfig.setDisableCNCheck(s2SConfigurationService.getValueAsBoolean(NIH_GOV_S2S_CN_CHECK));

        try {
            final String keyStorePassword = s2SConfigurationService.getValueAsString(NIH_GOV_S2S_KEYSTORE_PASSWORD);
            final KeyStore keyStore = KeyStore.getInstance(KEY_TYPE);
            keyStore.load(new FileInputStream(s2SConfigurationService.getValueAsString(NIH_GOV_S2S_KEYSTORE_LOCATION)), keyStorePassword.toCharArray());
            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            if (StringUtils.isNotBlank(dunsNumber) && multiCampusEnabled) {
                final KeyStore keyStoreAlias = KeyStore.getInstance(KEY_TYPE);
                final Certificate[] certificates = keyStore.getCertificateChain(dunsNumber);
                final Key key = keyStore.getKey(dunsNumber, keyStorePassword.toCharArray());
                keyStoreAlias.load(null, null);
                keyStoreAlias.setKeyEntry(dunsNumber, key, keyStorePassword.toCharArray(), certificates);
                keyManagerFactory.init(keyStoreAlias, keyStorePassword.toCharArray());
            } else {
                keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());
            }

            final KeyManager[] km = keyManagerFactory.getKeyManagers();
            tlsConfig.setKeyManagers(km);

            final KeyStore trustStore = KeyStore.getInstance(KEY_TYPE);
            trustStore.load(new FileInputStream(s2SConfigurationService.getValueAsString(NIH_GOV_S2S_TRUSTSTORE_LOCATION)), s2SConfigurationService.getValueAsString(NIH_GOV_S2S_TRUSTSTORE_PASSWORD).toCharArray());
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            final TrustManager[] tm = trustManagerFactory.getTrustManagers();
            tlsConfig.setTrustManagers(tm);
        } catch (KeyStoreException|NoSuchAlgorithmException|CertificateException|IOException|UnrecoverableKeyException e) {
            throw new S2sCommunicationException(KeyConstants.ERROR_S2S_UNKNOWN, e);
        }

        final JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress(getSoapHost(s2SConfigurationService.getValueAsString(NIH_GOV_S2S_HOST), s2SConfigurationService.getValueAsString(NIH_GOV_S2S_PORT)));
        factory.setServiceClass(SubmissionValidationServiceStub.class);

        applicantWebService = (SubmissionValidationServiceStub) factory.create();
        final Client client = ClientProxy.getClient(applicantWebService);
        final HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();

        final HTTPConduit conduit = (HTTPConduit) client.getConduit();
        conduit.setClient(httpClientPolicy);
        conduit.setTlsClientParameters(tlsConfig);

        return applicantWebService;
    }

    protected String getSoapHost(String hostStr, String portStr) {
        StringBuilder host = new StringBuilder();
        host.append(hostStr);

        if ((!host.toString().endsWith("/")) && (!portStr.startsWith("/"))) {
            host.append("/");
        }
        host.append(portStr);
        return host.toString();
    }

    public S2SConfigurationService getS2SConfigurationService() {
        return s2SConfigurationService;
    }

    public void setS2SConfigurationService(S2SConfigurationService s2SConfigurationService) {
        this.s2SConfigurationService = s2SConfigurationService;
    }
}
