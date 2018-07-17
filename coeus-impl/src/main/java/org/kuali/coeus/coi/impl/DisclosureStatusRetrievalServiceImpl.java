/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.coi.impl;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.coi.framework.DisclosureProjectStatus;
import org.kuali.coeus.coi.framework.DisclosureStatusRetrievalService;
import org.kuali.coeus.sys.framework.auth.AuthConstants;
import org.kuali.coeus.sys.framework.auth.JwtService;
import org.kuali.coeus.sys.framework.mq.rest.RestRequest;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component("disclosureStatusRetrievalService")
public class DisclosureStatusRetrievalServiceImpl implements DisclosureStatusRetrievalService {
    private static Logger LOG = LogManager.getLogger(DisclosureStatusRetrievalServiceImpl.class);

    @Autowired
    @Qualifier("restOperations")
    private RestOperations restOperations;

    @Autowired
    @Qualifier("kualiConfigurationService")
    private ConfigurationService configurationService;

    @Autowired
    @Qualifier("jwtService")
    private JwtService jwtService;

    @Override
    public List<DisclosureProjectStatus> getDisclosureStatusesForProject(String sourceId, String projectId) {
        final String url = getCoiApiUrl() + sourceId + "/" + projectId;

        final RestRequest request = new RestRequest();
        final HttpHeaders headers = new HttpHeaders();
        headers.put(Constants.CONTENT_TYPE, Collections.singletonList(Constants.APPLICATION_JSON));
        headers.put(Constants.AUTHORIZATION_HEADER, Collections.singletonList(getAuthToken()));
        final HttpEntity<String> entity = new HttpEntity<>(request.getBody(), headers);
        return getDisclosureProjectStatuses(url, entity, org.springframework.http.HttpMethod.GET);
    }

    protected String getAuthToken() {
        return Constants.BEARER_TOKEN + getJwtService().createToken();
    }

    protected String getCoiApiUrl() {
        return getConfigurationService().getPropertyValueAsString(Constants.COI_PROJECTS_DISCLOSURE_STATUS_URL);
    }

    protected List<DisclosureProjectStatus> getDisclosureProjectStatuses(String url, HttpEntity<String> entity, HttpMethod method) {
        List<DisclosureProjectStatus> projectStatuses = new ArrayList<>();
        try {
            ResponseEntity<List<DisclosureProjectStatus>> response = getDisclosureStatusFromCoi(url, entity, method);
            projectStatuses = response.getBody();
            if (LOG.isDebugEnabled()) {
                LOG.debug(url+ "returned status code "  + response.getStatusCode());
            }
        } catch (UnknownHttpStatusCodeException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(url+ "returned status code "  + e.getRawStatusCode(), e);
            }
        } catch (RuntimeException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(url+ "returned error " + e.getMessage(), e);
            }
        }
        return projectStatuses;
    }

    protected ResponseEntity<List<DisclosureProjectStatus>> getDisclosureStatusFromCoi(String url, HttpEntity<String> entity, HttpMethod method) {
        return restOperations.exchange(url, method, entity, new ParameterizedTypeReference<List<DisclosureProjectStatus>>() { });
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public RestOperations getRestOperations() {
        return restOperations;
    }

    public void setRestOperations(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    public JwtService getJwtService() {
        return jwtService;
    }

    public void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }
}
