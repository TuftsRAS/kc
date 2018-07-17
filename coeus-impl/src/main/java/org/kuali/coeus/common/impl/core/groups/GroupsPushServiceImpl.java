/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.common.impl.core.groups;

import static org.kuali.coeus.sys.framework.auth.CoreGroupsService.ACTIVE_FIELD_ID;
import static org.kuali.coeus.sys.framework.auth.CoreGroupsService.UNIT_NUMBER_FIELD_ID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.framework.core.groups.GroupsPushService;
import org.kuali.coeus.common.framework.core.groups.GroupsPushStatus;
import org.kuali.coeus.common.framework.unit.Unit;
import org.kuali.coeus.common.framework.unit.UnitService;
import org.kuali.coeus.common.framework.unit.admin.UnitAdministrator;
import org.kuali.coeus.common.framework.unit.admin.UnitAdministratorType;
import org.kuali.coeus.sys.framework.auth.AuthUser;
import org.kuali.coeus.sys.framework.auth.CategoryDto;
import org.kuali.coeus.sys.framework.auth.CoreGroupsService;
import org.kuali.coeus.sys.framework.auth.GroupDto;
import org.kuali.coeus.sys.framework.rest.AuthServiceRestUtilService;
import org.kuali.coeus.sys.framework.rest.RestServiceConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

@Service("groupsPushService")
public class GroupsPushServiceImpl implements GroupsPushService {

	private static final String ACTIVE_FIELD_DESCRIPTION = "Active";
	private static final String UNIT_NUMBER_FIELD_DESCRIPTION = "Unit Number";
	private static final String CHECKBOX_FIELD_TYPE = "checkbox";
	private static final String TEXT_FIELD_TYPE = "text";
	private static final Logger LOG = LogManager.getLogger(GroupsPushServiceImpl.class);
	private static final Integer NUMBER_OF_USERS_LIMIT = 100000;
	private static final String LIMIT_PARAM = "limit";
	private static final String[] PREDEFINED_ROLE_IDS = { "IRB Admin", "IACUC Admin", "IRB User", "IACUC User", "IRB Viewer", "IACUC Viewer" };

	@Autowired
	@Qualifier("unitService")
	private UnitService unitService;

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
	@Qualifier("dataObjectService")
	private DataObjectService dataObjectService;

	@Autowired
	@Qualifier("coreGroupsService")
	private CoreGroupsService coreGroupsService;

	@Override
	public GroupsPushStatus pushAllGroups() {
		GroupsPushStatus status = new GroupsPushStatus();
		List<CategoryDto> categories = getAllCategories().stream().sorted(Comparator.comparing(CategoryDto::getId))
				.collect(Collectors.toList());
		List<GroupDto> groups = getAllGroups();
		List<UnitAdministratorType> unitAdminTypes = getUnitAdministratorTypes();
		Unit topUnit = unitService.getTopUnit();
		syncGroups(Collections.<Unit>singletonList(topUnit), null, 1, null, categories, groups, unitAdminTypes, new ArrayList<>(), status);

		return status;
	}

	protected List<UnitAdministratorType> getUnitAdministratorTypes() {
		return dataObjectService.findAll(UnitAdministratorType.class)
				.getResults();
	}

	protected void syncGroups(List<Unit> units, GroupDto parentGroup, int level, CategoryDto parentCategory,
							  List<CategoryDto> categories, List<GroupDto> groups, List<UnitAdministratorType> unitAdminTypes,
							  List<Integer> levelsSynced, GroupsPushStatus status) {
		final Optional<CategoryDto> currentCategory = findChildCategory(parentCategory, categories);
		CategoryDto category;
		if (!currentCategory.isPresent() || !levelsSynced.contains(level)) {
			category = syncCategory(currentCategory, parentCategory, level,
					unitAdminTypes, status);
			if (!currentCategory.isPresent()) {
				categories.add(category);
			}
			levelsSynced.add(level);
		} else {
			category = currentCategory.get();
		}
		status.setNumberOfUnits(status.getNumberOfUnits() + units.size());
		units.stream().forEach(unit -> {
			GroupDto group = groups.stream()
					.filter(g -> StringUtils.equals(unit.getUnitNumber(), getField(g, UNIT_NUMBER_FIELD_ID).getValue()))
					.findFirst().orElse(new GroupDto());
			if (group.getId() == null) {
				status.setGroupsAdded(status.getGroupsAdded() + 1);
				group.setCategoryId(category.getId());
				group.setName(unit.getUnitName());
				if (parentGroup != null) {
					group.setParentId(parentGroup.getId());
				}
				group.getFields().add(new GroupDto.GroupFields(UNIT_NUMBER_FIELD_ID, unit.getUnitNumber()));
				group.getFields().add(new GroupDto.GroupFields(ACTIVE_FIELD_ID, Boolean.toString(unit.isActive())));
				addUnitAdmins(group, unit);
				group = addGroup(group);
			} else {
				status.setGroupsUpdated(status.getGroupsUpdated() + 1);
				group.setCategoryId(category.getId());
				group.setName(unit.getUnitName());
				if (parentGroup != null) {
					group.setParentId(parentGroup.getId());
				}
				getField(group, ACTIVE_FIELD_ID).setValue(Boolean.toString(unit.isActive()));
				addUnitAdmins(group, unit);
				updateGroup(group);
			}
			List<Unit> childUnits = unitService.getSubUnits(unit.getUnitNumber());
			if (childUnits != null && childUnits.size() > 0) {
				syncGroups(childUnits, group, level + 1, category, categories, groups, unitAdminTypes, levelsSynced, status);
			}
		});
	}

	protected void addUnitAdmins(GroupDto group, Unit unit) {
		Map<String, Set<String>> unitAdmins = unitService.retrieveUnitAdministratorsByUnitNumber(unit.getUnitNumber()).stream()
				.collect(Collectors.groupingBy(
						a -> a.getUnitAdministratorType().getDescription(),
						HashMap::new,
						Collectors.mapping(UnitAdministrator::getPersonId,  Collectors.toSet())
				));
		Map<String, GroupDto.Role> updatedRoles = unitAdmins.entrySet().stream()
				.map(entry ->
						new GroupDto.Role(createRoleIdFromAdminTypeDescription(entry.getKey()),
								entry.getValue().stream().map(this::getCoreUserId).filter(u -> u != null).collect(Collectors.toList())))
				.collect(Collectors.toMap(GroupDto.Role::getId, Function.identity()));
		group.setRoles(Stream.concat(
				group.getRoles().stream()
						.filter(r -> !updatedRoles.containsKey(r.getId())),
				updatedRoles.values().stream())
				.collect(Collectors.toList()));

	}

	protected String getCoreUserId(String schoolId) {
		String uri = UriComponentsBuilder.fromHttpUrl(getUsersApiUrl())
				.queryParam("schoolId", schoolId)
				.build().encode().toString();
		ResponseEntity<List<AuthUser>> result = restOperations.exchange(uri, HttpMethod.GET,
				new HttpEntity<String>(authServiceRestUtilService.getAuthServiceStyleHttpHeadersForUser()),
				new ParameterizedTypeReference<List<AuthUser>>(){ });
		if (!result.getBody().isEmpty()) {
			if (LOG.isInfoEnabled()) {
				LOG.info("GET " + uri + " returning " + result.getBody().get(0));
			}
			return result.getBody().get(0).getId();
		} else {
			return null;
		}
	}

	private GroupDto.GroupFields getField(GroupDto group, String fieldId) {
		return group.getFields().stream().filter(f -> StringUtils.equals(f.getId(), fieldId)).findFirst()
				.orElse(new GroupDto.GroupFields());
	}

	protected CategoryDto syncCategory(Optional<CategoryDto> existingCategory, CategoryDto parentCategory, int level,
									   List<UnitAdministratorType> unitAdminTypes, GroupsPushStatus status) {
		CategoryDto updatedCategory = existingCategory.orElse(null);
		if (updatedCategory == null) {
			status.setCategoriesAdded(status.getCategoriesAdded() + 1);
			updatedCategory = new CategoryDto();
			if (parentCategory != null) {
				updatedCategory.setParentId(parentCategory.getId());
			}
			updatedCategory.setName("Level " + level);
			updatedCategory.getFieldSchemas().add(new CategoryDto.FieldSchemaDto(UNIT_NUMBER_FIELD_ID, TEXT_FIELD_TYPE, UNIT_NUMBER_FIELD_DESCRIPTION));
			updatedCategory.getFieldSchemas().add(new CategoryDto.FieldSchemaDto(ACTIVE_FIELD_ID, CHECKBOX_FIELD_TYPE, ACTIVE_FIELD_DESCRIPTION));
			updateCategoryRoles(updatedCategory, unitAdminTypes);
			return addCategory(updatedCategory);
		} else {
			status.setCategoriesUpdated(status.getCategoriesUpdated() + 1);
			if (parentCategory != null) {
				updatedCategory.setParentId(parentCategory.getId());
			}
			updateCategoryRoles(updatedCategory, unitAdminTypes);
			updateCategory(updatedCategory);
			return updatedCategory;
		}

	}

	protected void updateCategoryRoles(CategoryDto category, List<UnitAdministratorType> unitAdminTypes) {
		Stream.concat(Stream.of(PREDEFINED_ROLE_IDS),
				unitAdminTypes.stream()
						.map(UnitAdministratorType::getDescription)
		).distinct().forEach(name -> {
			String id = createRoleIdFromAdminTypeDescription(name);
			Optional<CategoryDto.RoleSchemaDto> roleSchema = category.getRoleSchemas().stream()
					.filter(rs -> StringUtils.equals(rs.getId(), id))
					.findFirst();
			if (!roleSchema.isPresent()) {
				category.getRoleSchemas().add(new CategoryDto.RoleSchemaDto(id, name));
			}
		});
	}

	private String createRoleIdFromAdminTypeDescription(String name) {
		return name.toUpperCase().replaceAll("\\s+|\\.", "_");
	}


	protected Optional<CategoryDto> findChildCategory(CategoryDto parentCategory, List<CategoryDto> categories) {
		String parentId = parentCategory == null ? null : parentCategory.getId();
		if (parentId == null) {
			return categories.stream()
					.filter(c -> StringUtils.isBlank(c.getParentId()) || StringUtils.equals(c.getParentId(), c.getId()))
					.findFirst();
		} else {
			return categories.stream().filter(c -> StringUtils.equals(c.getParentId(), parentId)).findFirst();
		}
	}

	protected List<CategoryDto> getAllCategories() {
		String uri = UriComponentsBuilder.fromHttpUrl(coreGroupsService.getCategoriesApiUrl())
				.queryParam(LIMIT_PARAM, NUMBER_OF_USERS_LIMIT)
				.build().encode().toString();
		ResponseEntity<List<CategoryDto>> result = restOperations.exchange(uri, HttpMethod.GET,
				new HttpEntity<String>(authServiceRestUtilService.getAuthServiceStyleHttpHeadersForUser()),
				new ParameterizedTypeReference<List<CategoryDto>>() {
				});
		if (LOG.isInfoEnabled()) {
			LOG.info("GET " + uri + " returned " + result.getBody().size() + " categories");
		}
		return result.getBody();
	}

	protected List<GroupDto> getAllGroups() {
		return coreGroupsService.getAllGroups();
	}

	protected GroupDto getOneGroup(String groupId) {
		String uri = UriComponentsBuilder.fromHttpUrl(coreGroupsService.getGroupsApiUrl() + groupId)
				.build().encode().toString();
		ResponseEntity<GroupDto> result = restOperations.exchange(uri, HttpMethod.GET,
				new HttpEntity<String>(authServiceRestUtilService.getAuthServiceStyleHttpHeadersForUser()),
				GroupDto.class);
		if (LOG.isInfoEnabled()) {
			LOG.info("GET " + uri + " returned " + result.getBody());
		}
		return result.getBody();
	}

	protected CategoryDto addCategory(CategoryDto category) {
		ResponseEntity<CategoryDto> result = restOperations.exchange(coreGroupsService.getCategoriesApiUrl(), HttpMethod.POST,
				new HttpEntity<CategoryDto>(category,
						authServiceRestUtilService.getAuthServiceStyleHttpHeadersForUser()),
				CategoryDto.class);
		if (result.getStatusCode() != HttpStatus.CREATED) {
			throw new RestClientException(result.toString());
		} else {
			if (LOG.isInfoEnabled()) {
				LOG.info("POST " + coreGroupsService.getCategoriesApiUrl() + " returned " + result.getBody());
			}
			return result.getBody();
		}
	}

	protected void updateCategory(CategoryDto category) {
		String uri = coreGroupsService.getCategoriesApiUrl() + category.getId();
		ResponseEntity<String> result = restOperations.exchange(uri,
				HttpMethod.PUT, new HttpEntity<CategoryDto>(category,
						authServiceRestUtilService.getAuthServiceStyleHttpHeadersForUser()),
				String.class);
		if (LOG.isInfoEnabled()) {
			LOG.info("PUT " + uri + " returned status of " + result.getStatusCode() + " and body text of " + result.getBody());
		}
		if (result.getStatusCode() != HttpStatus.OK) {
			throw new RestClientException(result.getBody());
		}
	}

	protected GroupDto addGroup(GroupDto group) {
		String uri = coreGroupsService.getGroupsApiUrl();
		ResponseEntity<GroupDto> result = restOperations.exchange(coreGroupsService.getGroupsApiUrl(), HttpMethod.POST,
				new HttpEntity<GroupDto>(group, authServiceRestUtilService.getAuthServiceStyleHttpHeadersForUser()),
				GroupDto.class);
		if (result.getStatusCode() != HttpStatus.CREATED) {
			throw new RestClientException(result.toString());
		} else {
			if (LOG.isInfoEnabled()) {
				LOG.info("POST " + uri + " returned " + result.getBody());
			}
			return result.getBody();
		}
	}

	protected void updateGroup(GroupDto group) {
		String uri = coreGroupsService.getGroupsApiUrl() + group.getId();
		ResponseEntity<String> result = restOperations.exchange(uri, HttpMethod.PUT,
				new HttpEntity<GroupDto>(group, authServiceRestUtilService.getAuthServiceStyleHttpHeadersForUser()),
				String.class);
		if (LOG.isInfoEnabled()) {
			LOG.info("PUT " + uri + " returned status of " + result.getStatusCode() + " and body text of " + result.getBody());
		}
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

	public UnitService getUnitService() {
		return unitService;
	}

	public void setUnitService(UnitService unitService) {
		this.unitService = unitService;
	}

	public DataObjectService getDataObjectService() {
		return dataObjectService;
	}

	public void setDataObjectService(DataObjectService dataObjectService) {
		this.dataObjectService = dataObjectService;
	}

	public CoreGroupsService getCoreGroupsService() {
		return coreGroupsService;
	}

	public void setCoreGroupsService(CoreGroupsService coreGroupsService) {
		this.coreGroupsService = coreGroupsService;
	}
}
