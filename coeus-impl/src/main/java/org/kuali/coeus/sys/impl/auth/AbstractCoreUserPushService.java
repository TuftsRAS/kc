/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.sys.impl.auth;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kuali.coeus.sys.framework.auth.CoreUserPushService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.auth.CoreUsersPushStatus;
import org.kuali.coeus.sys.framework.auth.AuthUser;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class AbstractCoreUserPushService<T> implements CoreUserPushService {

	private static final Integer NUMBER_OF_USERS_LIMIT = 100000;
	private static final String INCLUDE_INACTIVE_PARAM = "includeInactive";

	protected abstract List<T> getAllPeople();

	protected abstract AuthUser generateAuthUserFromPerson(T person, Map<String, String> groupIdsByUnitNumber);

	protected abstract String getUserRole(T person, final List<String> admins);

	protected abstract List<String> getAdminUsers();

	protected abstract boolean validUserToPush(T person);

	private static final Logger LOG = LogManager.getLogger(AbstractCoreUserPushService.class);
	private static final String LIMIT_PARAM = "limit";
	private static final String AUTH_USER_PUSH_USE_DEV_PASSWORD = "auth.user.push.use.dev.password";
	private static final String AUTH_USER_PUSH_DEV_PASSWORD = "auth.user.push.dev.password";

	@Autowired
	@Qualifier("restOperations")
	private RestOperations restOperations;
	@Autowired
	@Qualifier("authServiceRestUtilService")
	private AuthServiceRestUtilService authServiceRestUtilService;
	@Autowired
	@Qualifier("kualiConfigurationService")
	private ConfigurationService configurationService;
	@Autowired
	@Qualifier("coreGroupsService")
	private CoreGroupsService coreGroupsService;

	public AbstractCoreUserPushService() {
		super();
	}

	@Override
	public CoreUsersPushStatus pushAllUsers() {
		CoreUsersPushStatus status = new CoreUsersPushStatus();
		final List<String> admins = getAdminUsers();
		Map<String, String> groupIdByUnitNumber = coreGroupsService.getAllGroups().stream()
				.filter(g -> StringUtils.isNotBlank(coreGroupsService.getUnitNumberForGroup(g)))
				.collect(Collectors.toMap(g -> coreGroupsService.getUnitNumberForGroup(g), GroupDto::getId));
		List<AuthUser> peopleToSync = getAllPeople().stream()
				.filter(this::validUserToPush)
				.map(person -> {
					AuthUser authUser = generateAuthUserFromPerson(person, groupIdByUnitNumber);
					authUser.setRole(getUserRole(person, admins));
					return authUser;
				}).collect(Collectors.toList());
		status.setNumberOfUsers(peopleToSync.size());
		Map<String, AuthUser> authPersonMap = getAllAuthServiceUsers().stream().collect(Collectors.toMap(AuthUser::getSchoolId,
				Function.identity(), (v1, v2) -> v1));
		for (AuthUser person : peopleToSync) {
			AuthUser authServicePerson = authPersonMap.get(person.getSchoolId());
			try {
				if (person.isActive()) {
					if (authServicePerson == null) {
						addUserToAuthService(person, getOrGenerateUserPassword(person));
						status.addNumberAdded();
					} else if (authServicePerson.equals(person)) {
						status.addNumberSame();
					} else {
						updateUserInAuthService(person, authServicePerson.getId());
						status.addNumberUpdated();
					}
				} else if (authServicePerson != null) {
					disableUserInAuthService(authServicePerson);
					status.addNumberRemoved();
				}
			} catch (HttpClientErrorException|HttpServerErrorException e) {
				String message = "Error pushing user " + person.getUsername() + ":" + person.getSchoolId() + " to auth service - " + e.getMessage() + " -- " + e.getResponseBodyAsString();
				status.getErrors().put(person.getSchoolId(), message);
				LOG.error(message);
			} catch (Exception e) {
				String message = "Error pushing user " + person.getUsername() + ":" + person.getSchoolId() + " to auth service";
				status.getErrors().put(person.getSchoolId(), message);
				LOG.error(message, e);
			}
		}
		StringWriter infoMsg = new StringWriter();
		infoMsg.append("Auth Service Bulk Push: Users Found: ").append(Integer.valueOf(status.getNumberOfUsers()).toString())
				.append(", Users Added: ").append(Integer.valueOf(status.getNumberAdded()).toString())
				.append(", Users Updated: ").append(Integer.valueOf(status.getNumberUpdated()).toString())
				.append(", Users Deleted: ").append(Integer.valueOf(status.getNumberRemoved()).toString())
				.append(", Users Errored: ").append(Integer.valueOf(status.getErrors().size()).toString());
		LOG.info(infoMsg.toString());

		return status;
	}

	protected String getOrGenerateUserPassword(AuthUser person) {
		if (useDevPassword()) {
			return getDevPassword();
		} else {
			return UUID.randomUUID().toString();
		}
	}

	protected boolean useDevPassword() {
		return configurationService.getPropertyValueAsBoolean(AUTH_USER_PUSH_USE_DEV_PASSWORD);
	}

	protected String getDevPassword() {
		return configurationService.getPropertyValueAsString(AUTH_USER_PUSH_DEV_PASSWORD);
	}

	protected List<AuthUser> getAllAuthServiceUsers() {
		String uri = UriComponentsBuilder.fromHttpUrl(getUsersApiUrl()).queryParam(LIMIT_PARAM, NUMBER_OF_USERS_LIMIT).queryParam(INCLUDE_INACTIVE_PARAM, Boolean.TRUE).build().encode().toString();
		ResponseEntity<List<AuthUser>> result = restOperations.exchange(uri, HttpMethod.GET,
				new HttpEntity<String>(authServiceRestUtilService.getAuthServiceStyleHttpHeadersForUser()), new ParameterizedTypeReference<List<AuthUser>>() { });
		return result.getBody();
	}

	protected void addUserToAuthService(AuthUser newUser, String userPassword) {
		newUser.setPassword(userPassword);
		ResponseEntity<String> result = restOperations.exchange(getUsersApiUrl(), HttpMethod.POST,
				new HttpEntity<AuthUser>(newUser, authServiceRestUtilService.getAuthServiceStyleHttpHeadersForUser()), String.class);
		if (result.getStatusCode() != HttpStatus.CREATED) {
			throw new RestClientException(result.getBody());
		}
	}

	protected void updateUserInAuthService(AuthUser updatedUser, String userId) {
		ResponseEntity<String> result = restOperations.exchange(getUsersApiUrl() + userId, HttpMethod.PUT,
				new HttpEntity<AuthUser>(updatedUser, authServiceRestUtilService.getAuthServiceStyleHttpHeadersForUser()), String.class);
		if (result.getStatusCode() != HttpStatus.OK) {
			throw new RestClientException(result.getBody());
		}
	}

	protected void disableUserInAuthService(AuthUser disabledUser) {
		disabledUser.setActive(false);
		ResponseEntity<String> result = restOperations.exchange(getUsersApiUrl() + disabledUser.getId(), HttpMethod.PUT,
				new HttpEntity<AuthUser>(disabledUser, authServiceRestUtilService.getAuthServiceStyleHttpHeadersForUser()), String.class);
		if (result.getStatusCode() != HttpStatus.OK) {
			throw new RestClientException(result.getBody());
		}
	}

	protected String getUsersApiUrl() {
		return configurationService.getPropertyValueAsString(RestServiceConstants.Configuration.AUTH_USERS_URL) + "/";
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

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public CoreGroupsService getCoreGroupsService() {
		return coreGroupsService;
	}

	public void setCoreGroupsService(CoreGroupsService coreGroupsService) {
		this.coreGroupsService = coreGroupsService;
	}

}