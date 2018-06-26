/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.propdev.impl.s2s.nih;

import gov.nih.era.external.SubmissionValidationServiceStub;
import gov.nih.era.svs.types.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.coeus.propdev.api.s2s.MockS2SConfigurationService;
import org.kuali.coeus.s2sgen.api.core.ConfigurationConstants;

import java.util.Collections;
import java.util.List;

public class NihSubmissionValidationServiceImplTest {

    private NihSubmissionValidationServiceImpl nihSubmissionValidationService;
    private MockSubmissionValidationServiceStub submissionValidationServiceStub;
    private MockS2SConfigurationService s2SConfigurationService;

    @Before
    public void lookupService() {
        s2SConfigurationService = new MockS2SConfigurationService();
        submissionValidationServiceStub = new MockSubmissionValidationServiceStub();
        nihSubmissionValidationService = new NihSubmissionValidationServiceImpl() {
            @Override
            SubmissionValidationServiceStub createConfiguredService(String dunsNumber) {
                return submissionValidationServiceStub;
            }
        };
        nihSubmissionValidationService.setS2SConfigurationService(s2SConfigurationService);
    }

    @Test
    public void test_null_xml() {
        s2SConfigurationService.setValueAsString("Enable_NIH_Validation_Service", "Y");
        s2SConfigurationService.setValueAsString( ConfigurationConstants.MULTI_CAMPUS_ENABLED, "N");

        submissionValidationServiceStub.setResponse(createEmptyResponse());
        List<ValidationMessageDto> response = nihSubmissionValidationService.validateApplication(null, Collections.emptyList(), "1234");
        assertEmptyResponse(response);
    }

    @Test
    public void test_empty_xml() {
        s2SConfigurationService.setValueAsString("Enable_NIH_Validation_Service", "Y");
        s2SConfigurationService.setValueAsString( ConfigurationConstants.MULTI_CAMPUS_ENABLED, "N");

        submissionValidationServiceStub.setResponse(createEmptyResponse());
        List<ValidationMessageDto> response = nihSubmissionValidationService.validateApplication(" ", Collections.emptyList(), "1234");
        assertEmptyResponse(response);
    }

    @Test
    public void test_not_enabled() {
        s2SConfigurationService.setValueAsString("Enable_NIH_Validation_Service", "N");
        s2SConfigurationService.setValueAsString( ConfigurationConstants.MULTI_CAMPUS_ENABLED, "N");

        submissionValidationServiceStub.setResponse(createEmptyResponse());
        List<ValidationMessageDto> response = nihSubmissionValidationService.validateApplication("<dummyXml></dummyXml>", Collections.emptyList(), "1234");
        assertEmptyResponse(response);
    }


    @Test
    public void test_caching_enabled() {
        s2SConfigurationService.setValueAsString("Enable_NIH_Validation_Service", "Y");
        s2SConfigurationService.setValueAsString( ConfigurationConstants.MULTI_CAMPUS_ENABLED, "N");
        s2SConfigurationService.setValueAsString( "Enable_NIH_Validation_Service_Caching", "Y");

        submissionValidationServiceStub.setResponse(createNonEmptyResponse1());
        List<ValidationMessageDto> response1 = nihSubmissionValidationService.validateApplication("<dummyXml></dummyXml>", Collections.emptyList(), "1234");
        assertNonEmptyResponse(response1);

        submissionValidationServiceStub.setResponse(createAssertIfAccessed("The submissionValidationServiceStub should not have been called with the same request and caching enabled"));
        List<ValidationMessageDto> response2 = nihSubmissionValidationService.validateApplication("<dummyXml></dummyXml>", Collections.emptyList(), "1234");
        assertNonEmptyResponse(response2);

        Assert.assertEquals(response1, response2);
    }

    @Test
    public void test_caching_enabled_diff_request() {
        s2SConfigurationService.setValueAsString("Enable_NIH_Validation_Service", "Y");
        s2SConfigurationService.setValueAsString( ConfigurationConstants.MULTI_CAMPUS_ENABLED, "N");
        s2SConfigurationService.setValueAsString( "Enable_NIH_Validation_Service_Caching", "Y");

        submissionValidationServiceStub.setResponse(createNonEmptyResponse1());
        List<ValidationMessageDto> response1 = nihSubmissionValidationService.validateApplication("<dummyXml></dummyXml>", Collections.emptyList(), "1234");
        assertNonEmptyResponse(response1);

        submissionValidationServiceStub.setResponse(createNonEmptyResponse2());
        List<ValidationMessageDto> response2 = nihSubmissionValidationService.validateApplication("<Diff></Diff>", Collections.emptyList(), "1234");
        assertNonEmptyResponse(response2);

        Assert.assertNotEquals(response1, response2);
    }


    @Test
    public void test_caching_disabled() {
        s2SConfigurationService.setValueAsString("Enable_NIH_Validation_Service", "Y");
        s2SConfigurationService.setValueAsString( ConfigurationConstants.MULTI_CAMPUS_ENABLED, "N");
        s2SConfigurationService.setValueAsString( "Enable_NIH_Validation_Service_Caching", "N");

        submissionValidationServiceStub.setResponse(createNonEmptyResponse1());
        List<ValidationMessageDto> response1 = nihSubmissionValidationService.validateApplication("<dummyXml></dummyXml>", Collections.emptyList(), "1234");
        assertNonEmptyResponse(response1);

        //even though the same request, with caching off the submissionValidationServiceStub will be called returning response 2
        submissionValidationServiceStub.setResponse(createNonEmptyResponse2());
        List<ValidationMessageDto> response2 = nihSubmissionValidationService.validateApplication("<dummyXml></dummyXml>", Collections.emptyList(), "1234");
        assertNonEmptyResponse(response2);

        Assert.assertNotEquals(response1, response2);

    }

    private void assertEmptyResponse(List<ValidationMessageDto> response) {
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isEmpty());
    }

    private void assertNonEmptyResponse(List<ValidationMessageDto> response) {
        Assert.assertNotNull(response);
        Assert.assertFalse(response.isEmpty());
    }

    static class MockSubmissionValidationServiceStub implements SubmissionValidationServiceStub {
        private ValidateApplicationResponse response;

        @Override
        public ValidateApplicationResponse validateComponent(ValidateComponentRequest parameters) {
            throw new IllegalArgumentException("unused in Kuali Coeus");
        }

        @Override
        public ValidateApplicationResponse validateApplication(ValidateApplicationRequest parameters) {
            return response;
        }

        public void setResponse(ValidateApplicationResponse response) {
            this.response = response;
        }
    }

    private ValidateApplicationResponse createEmptyResponse() {
        return new ValidateApplicationResponse();
    }

    private ValidateApplicationResponse createNonEmptyResponse1() {
        ValidateApplicationResponse response = new ValidateApplicationResponse();
        ValidationMessageList messages = new ValidationMessageList();
        ValidationMessage message = new ValidationMessage();
        message.setValidationMessageText("mock text 1");
        messages.getValidationMessage().add(message);
        response.setValidationMessageList(messages);
        return response;
    }

    private ValidateApplicationResponse createNonEmptyResponse2() {
        ValidateApplicationResponse response = new ValidateApplicationResponse();
        ValidationMessageList messages = new ValidationMessageList();
        ValidationMessage message = new ValidationMessage();
        message.setValidationMessageText("mock text 2");
        messages.getValidationMessage().add(message);
        response.setValidationMessageList(messages);
        return response;
    }

    private ValidateApplicationResponse createAssertIfAccessed(String message) {
        return new ValidateApplicationResponse() {
            @Override
            public ValidationMessageList getValidationMessageList() {
                Assert.fail(message);
                return null;
            }
        };
    }
}
