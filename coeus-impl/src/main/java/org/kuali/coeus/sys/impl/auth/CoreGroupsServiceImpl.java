/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.sys.impl.auth;

import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.auth.CoreGroupsService;
import org.kuali.coeus.sys.framework.auth.GroupDto;
import org.kuali.coeus.sys.framework.rest.AuthServiceRestUtilService;
import org.kuali.coeus.sys.framework.rest.RestServiceConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

@Service("coreGroupsService")
public class CoreGroupsServiceImpl implements CoreGroupsService {

	private static final Logger LOG = LogManager.getLogger(CoreGroupsServiceImpl.class);
	private static final Integer NUMBER_OF_GROUPS_LIMIT = 100000;
	private static final String LIMIT_PARAM = "limit";

	@Autowired
	@Qualifier("authServiceRestUtilService")
	private AuthServiceRestUtilService authServiceRestUtilService;

	@Autowired
	@Qualifier("kualiConfigurationService")
	private ConfigurationService configurationService;

	@Autowired
	@Qualifier("restOperations")
	private RestOperations restOperations;

	@Override
	public String getCategoriesApiUrl() {
		return configurationService.getPropertyValueAsString(RestServiceConstants.Configuration.CATEGORIES_URL) + "/";
	}

	@Override
	public String getGroupsApiUrl() {
		return configurationService.getPropertyValueAsString(RestServiceConstants.Configuration.GROUPS_URL) + "/";
	}

	@Override
	public List<GroupDto> getAllGroups() {
		String uri = UriComponentsBuilder.fromHttpUrl(getGroupsApiUrl())
				.queryParam(LIMIT_PARAM, NUMBER_OF_GROUPS_LIMIT)
				.build().encode().toString();
		ResponseEntity<List<GroupDto>> result = restOperations.exchange(uri, HttpMethod.GET,
				new HttpEntity<String>(authServiceRestUtilService.getAuthServiceStyleHttpHeadersForUser()),
				new ParameterizedTypeReference<List<GroupDto>>() {
				});
		if (LOG.isInfoEnabled()) {
			LOG.info("GET " + uri + " returned " + result.getBody().size() + " groups");
		}
		return result.getBody();
	}

	@Override
	public String getUnitNumberForGroup(GroupDto group) {
		return group.getFields().stream().filter(f -> StringUtils.equals(f.getId(), UNIT_NUMBER_FIELD_ID)).map(GroupDto.GroupFields::getValue).findFirst().orElse(null);
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public AuthServiceRestUtilService getAuthServiceRestUtilService() {
		return authServiceRestUtilService;
	}

	public void setAuthServiceRestUtilService(AuthServiceRestUtilService authServiceRestUtilService) {
		this.authServiceRestUtilService = authServiceRestUtilService;
	}

	public RestOperations getRestOperations() {
		return restOperations;
	}

	public void setRestOperations(RestOperations restOperations) {
		this.restOperations = restOperations;
	}

}
