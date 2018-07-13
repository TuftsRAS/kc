/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.mail;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.framework.mail.EmailAttachment;
import org.kuali.coeus.common.framework.mail.KcEmailService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("kcEmailService")
public class KcEmailServiceImpl implements KcEmailService {
    private static final Logger LOG = LogManager.getLogger(KcEmailServiceImpl.class);
    
    private static final String DEFAULT_ENCODING = "UTF-8";

    protected static final String EMAIL_NOTIFICATIONS_ENABLED_PARAM = "EMAIL_NOTIFICATIONS_ENABLED";
    protected static final boolean EMAIL_NOTIFICATIONS_ENABLED_DEFAULT = false;

    protected static final String EMAIL_NOTIFICATIONS_TEST_ENABLED_PARAM = "EMAIL_NOTIFICATION_TEST_ENABLED";
    protected static final boolean EMAIL_NOTIFICATIONS_TEST_ENABLED_DEFAULT = false;

    protected static final String EMAIL_NOTIFICATION_TEST_ADDRESS_PARAM = "EMAIL_NOTIFICATION_TEST_ADDRESS";

    protected static final String EMAIL_NOTIFICATIONS_FROM_ADDRESS_PARAM = "EMAIL_NOTIFICATION_FROM_ADDRESS";
    protected static final String EMAIL_NOTIFICATIONS_FROM_ADDRESS_CONFIG_PARAM = "mail.from";

    protected static final String KC_DEFAULT_EMAIL_RECIPIENT_PARAM = "KC_DEFAULT_EMAIL_RECIPIENT";

    @Autowired
    @Qualifier("mailSender")
    private JavaMailSenderImpl mailSender;
    
    @Autowired
    @Qualifier("parameterService")
    private ParameterService parameterService;

    @Autowired
    @Qualifier("kualiConfigurationService")
    private ConfigurationService configurationService;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void sendEmail(String from, Set<String> toAddresses, String subject, Set<String> ccAddresses,
                          Set<String> bccAddresses, String body, boolean htmlMessage) {
        sendEmailWithAttachments(from, toAddresses, subject, ccAddresses, bccAddresses, body, htmlMessage, null);
    }

    @Override
    public void sendEmailWithAttachments(String from, Set<String> toAddresses, String subject, Set<String> ccAddresses,
                                         Set<String> bccAddresses, String body, boolean htmlMessage, List<EmailAttachment> attachments) {
        if (mailSender != null && isEmailEnabled()) {
            populateDefaultAddressIfNoRecipients(toAddresses);

            if(CollectionUtils.isEmpty(toAddresses) &&
                    CollectionUtils.isEmpty(ccAddresses) &&
                    CollectionUtils.isEmpty(bccAddresses)){
                return;
            }

            String testAddress = getEmailNotificationTestAddress();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            
            try {
                helper = new MimeMessageHelper(message, true, DEFAULT_ENCODING);
                helper.setFrom(from);
                
                if (StringUtils.isNotBlank(subject)) {
                    helper.setSubject(subject);
                } else {
                    LOG.warn("Sending message with empty subject.");
                }

                if (isProduction()) {
                    helper.setText(body, htmlMessage);
                    if (CollectionUtils.isNotEmpty(toAddresses)) {
                        for (String toAddress: toAddresses) {
                            try {
                                helper.addTo(toAddress);
                            } catch (Exception ex) {
                                LOG.warn("Could not set to address:", ex);
                            }
                        }
                    }
                    if (CollectionUtils.isNotEmpty(ccAddresses)) {
                        for (String ccAddress: ccAddresses) {
                            try{
                                helper.addCc(ccAddress);
                            } catch (Exception ex) {
                                LOG.warn("Could not set to address:", ex);
                            }
                        }
                    }
                    if (CollectionUtils.isNotEmpty(bccAddresses)) {
                        for (String bccAddress : bccAddresses) {
                            try {
                                helper.addBcc(bccAddress);
                            } catch (Exception ex) {
                                LOG.warn("Could not set to address:", ex);
                            }
                        }
                    }
                } else if (isEmailTestEnabled() && StringUtils.isNotBlank(testAddress)) {
                    helper.setText(getTestMessageBody(body, toAddresses, ccAddresses, bccAddresses), true);
                    helper.addTo(testAddress);
                } else {
                    // If we're not in a Production environment and test emails aren't enabled, don't send anything
                    return;
                }
                
                if (CollectionUtils.isNotEmpty(attachments)) {
                    for (EmailAttachment attachment : attachments) {
                        try {
                            helper.addAttachment(attachment.getFileName(), new ByteArrayResource(attachment.getContents()), attachment.getMimeType());
                        } catch (Exception ex) {
                            LOG.warn("Could not set to address:", ex);
                        }
                    }
                }
                mailSender.send(message);

            } catch (MessagingException ex) {
                LOG.error("Failed to create mime message helper.", ex);
            }
        } else {
            LOG.info("Failed to send email due to inability to obtain valid email mailSender, please check your configuration.");
        }
    }

    private void populateDefaultAddressIfNoRecipients(Set<String> recipients) {
        if (CollectionUtils.isEmpty(recipients)) {
            String defaultRecipient = getDefaultToAddress();
            if (StringUtils.isNotBlank(defaultRecipient)) {
                recipients.add(defaultRecipient);
            }
        }
    }

    private String getTestMessageBody(String body, Set<String> toAddresses, Set<String> ccAddresses, Set<String> bccAddresses) {
        StringBuffer testEmailBody = new StringBuffer("");
        testEmailBody.append( "-----------------------------------------------------------<br/>");
        testEmailBody.append("TEST MODE<br/>");
        testEmailBody.append("In Production mode this mail will be sent to the following... <br/>");
        if (CollectionUtils.isNotEmpty(toAddresses)) {
            testEmailBody.append("TO: ");
            testEmailBody.append(toAddresses);
        }
        if (CollectionUtils.isNotEmpty(ccAddresses)) {
            testEmailBody.append("CC: ");
            testEmailBody.append(toAddresses);
        }
        if (CollectionUtils.isNotEmpty(bccAddresses)) {
            testEmailBody.append("BCC: ");
            testEmailBody.append(toAddresses);
        }
        testEmailBody.append("<br/>-----------------------------------------------------------");
        return testEmailBody.toString()+"<br/>"+body;
        
    }

    protected boolean isProduction() {
        return ConfigContext.getCurrentContextConfig().isProductionEnvironment();
    }

    protected boolean isEmailEnabled() {
        return parameterService.getParameterValueAsBoolean(Constants.KC_GENERIC_PARAMETER_NAMESPACE,
                Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, EMAIL_NOTIFICATIONS_ENABLED_PARAM, EMAIL_NOTIFICATIONS_ENABLED_DEFAULT);
    }

    protected boolean isEmailTestEnabled() {
        return parameterService.getParameterValueAsBoolean(Constants.KC_GENERIC_PARAMETER_NAMESPACE,
                Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, EMAIL_NOTIFICATIONS_TEST_ENABLED_PARAM, EMAIL_NOTIFICATIONS_TEST_ENABLED_DEFAULT);
    }

    private String getEmailNotificationTestAddress() {
        return parameterService.getParameterValueAsString(Constants.KC_GENERIC_PARAMETER_NAMESPACE,
                Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, EMAIL_NOTIFICATION_TEST_ADDRESS_PARAM);
    }

    @Override
    public String getDefaultFromAddress() {
        String xmlFromAddress = configurationService.getPropertyValueAsString(EMAIL_NOTIFICATIONS_FROM_ADDRESS_CONFIG_PARAM);
        return parameterService.getParameterValueAsString(Constants.KC_GENERIC_PARAMETER_NAMESPACE,
                Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, EMAIL_NOTIFICATIONS_FROM_ADDRESS_PARAM, xmlFromAddress);
    }

    protected String getDefaultToAddress() {
        return parameterService.getParameterValueAsString(Constants.KC_GENERIC_PARAMETER_NAMESPACE,
                Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, KC_DEFAULT_EMAIL_RECIPIENT_PARAM);
    }

    public void setMailSender(JavaMailSenderImpl mailSender) {
    	this.mailSender = mailSender;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public JavaMailSenderImpl getMailSender() {
    	return mailSender;
    }

    public void setParameterService(ParameterService parameterService) {
    	this.parameterService = parameterService;
    }
    
    public ParameterService getParameterService() {
    	return parameterService;
    }
}
