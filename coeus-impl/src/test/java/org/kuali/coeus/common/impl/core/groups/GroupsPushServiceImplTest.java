/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.common.impl.core.groups;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
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
import org.kuali.coeus.sys.impl.auth.CoreGroupsServiceImpl;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupsPushServiceImplTest {

	private static final String USERS_URL = "http://localhost/api/v1/users";
	private static final String GROUPS_URL = "http://localhost/api/v1/groups";
	private static final String CATEGORIES_URL = "http://localhost/api/v1/categories";
	private List<UnitAdministratorType> unitAdministratorTypes = Arrays.asList(
			buildUnitAdminType("1", "IRB Admin"),
			buildUnitAdminType("2", "DEAN")
	);

	@Test
	public void testPushAllGroups_noGroups() {
		Map<String, List<String>> univRoleMembers = new HashMap<>();
		Map<String, List<String>> blRoleMembers = new HashMap<>();
		univRoleMembers.put("IRB_ADMIN", Arrays.asList("111111", "222222"));
		blRoleMembers.put("IRB_ADMIN", Arrays.asList("333333"));
		blRoleMembers.put("DEAN", Arrays.asList("444444"));
		GroupsPushServiceImpl service = new GroupsPushServiceImpl() {
			@Override
			protected List<UnitAdministratorType> getUnitAdministratorTypes() {
				return unitAdministratorTypes;
			}
		};
		service.setConfigurationService(getMockedConfigurationService());
		service.setAuthServiceRestUtilService(getMockedAuthServiceUtils());
		service.setUnitService(getMockedUnitService());
		RestOperations restOperations = getMockedRestOperations(new ArrayList<>(), new ArrayList<>());
		when(restOperations.exchange(contains(CATEGORIES_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(CategoryDto.class)))
				.thenAnswer(new NewCategoryAnswer("Level 1", null, "Level1Id"))
				.thenAnswer(new NewCategoryAnswer("Level 2", "Level1Id", "Level2Id"))
				.thenAnswer(new NewCategoryAnswer("Level 3", "Level2Id", "Level3Id"))
				.thenThrow(new RuntimeException("Should only add 3 categories"));
		when(restOperations.exchange(contains(GROUPS_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(GroupDto.class)))
				.thenAnswer(new NewGroupAnswer("University", null, "Level1Id", "000001", true, "Group000001", univRoleMembers))
				.thenAnswer(new NewGroupAnswer("Bloomington Campus", "Group000001", "Level2Id", "BL-BL", true, "GroupBL-BL", blRoleMembers))
				.thenAnswer(new NewGroupAnswer("Office of VP for Research", "GroupBL-BL", "Level3Id", "BL-RUGS", true, "GroupRUGS", Collections.emptyMap()))
				.thenAnswer(new NewGroupAnswer("University Purdue", "Group000001", "Level2Id", "IN-IN", true, "GroupIN", Collections.emptyMap()));
		service.setRestOperations(restOperations);
		CoreGroupsServiceImpl coreGroupsService = new CoreGroupsServiceImpl();
		coreGroupsService.setAuthServiceRestUtilService(service.getAuthServiceRestUtilService());
		coreGroupsService.setConfigurationService(service.getConfigurationService());
		coreGroupsService.setRestOperations(restOperations);
		service.setCoreGroupsService(coreGroupsService);
		service.pushAllGroups();
	}

	@Test
	public void testPushAllGroups_existingGroupsAndCategories() {
		Map<String, List<String>> univRoleMembers = new HashMap<>();
		Map<String, List<String>> blRoleMembers = new HashMap<>();
		univRoleMembers.put("IRB_ADMIN", Arrays.asList("111111", "222222"));
		blRoleMembers.put("IRB_ADMIN", Arrays.asList("333333"));
		blRoleMembers.put("DEAN", Arrays.asList("444444"));
		GroupsPushServiceImpl service = new GroupsPushServiceImpl() {
			@Override
			protected List<UnitAdministratorType> getUnitAdministratorTypes() {
				return unitAdministratorTypes;
			}
		};
		service.setConfigurationService(getMockedConfigurationService());
		service.setAuthServiceRestUtilService(getMockedAuthServiceUtils());
		service.setUnitService(getMockedUnitService());
		RestOperations restOperations = getMockedRestOperations(Arrays.asList(
				buildCategoryDto("Level1Id", null, "Level 1",
						new ArrayList<>(Arrays.asList(new CategoryDto.RoleSchemaDto("IRB_ADMIN", "IRB Admin"), new CategoryDto.RoleSchemaDto("DEAN", "Dean"))),
						new ArrayList<>(Arrays.asList(
								new CategoryDto.FieldSchemaDto(CoreGroupsService.UNIT_NUMBER_FIELD_ID, "string", "Unit Number"),
								new CategoryDto.FieldSchemaDto(CoreGroupsService.ACTIVE_FIELD_ID, "checkbox", "Active")
						))
				),
				buildCategoryDto("Level2Id", "Level1Id", "Level 2",
						new ArrayList<>(Arrays.asList(new CategoryDto.RoleSchemaDto("IRB_ADMIN", "IRB Admin"), new CategoryDto.RoleSchemaDto("DEAN", "Dean"))),
						new ArrayList<>(Arrays.asList(
								new CategoryDto.FieldSchemaDto(CoreGroupsService.UNIT_NUMBER_FIELD_ID, "string", "Unit Number"),
								new CategoryDto.FieldSchemaDto(CoreGroupsService.ACTIVE_FIELD_ID, "checkbox", "Active")
						))
				)
		), Arrays.asList(
				buildGroupDto("Group000001", null, "University", "Level1Id", new ArrayList<>(), new ArrayList<>(
						Arrays.asList(new GroupDto.GroupFields(CoreGroupsService.UNIT_NUMBER_FIELD_ID, "000001"), new GroupDto.GroupFields(CoreGroupsService.ACTIVE_FIELD_ID, "false")))),
				buildGroupDto("GroupBL-BL", "NotGroup000001", "Not Bloomington Campus", "NotLevel2Id", new ArrayList<>(Arrays.asList(
						new GroupDto.Role("IRB_ADMIN", Arrays.asList("333333")),
						new GroupDto.Role("DEAN", Arrays.asList("55555"))
				)), new ArrayList<>(Arrays.asList(
						new GroupDto.GroupFields(CoreGroupsService.UNIT_NUMBER_FIELD_ID, "BL-BL"),
						new GroupDto.GroupFields(CoreGroupsService.ACTIVE_FIELD_ID, "false")
				)))
		));

		when(restOperations.exchange(contains(CATEGORIES_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(CategoryDto.class)))
				.thenAnswer(new NewCategoryAnswer("Level 3", "Level2Id", "Level3Id"))
				.thenThrow(new RuntimeException("Should only add 1 category"));
		when(restOperations.exchange(contains(CATEGORIES_URL), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
				.thenAnswer(new UpdatedCategoryAnswer("Level 1", null, "Level1Id"))
				.thenAnswer(new UpdatedCategoryAnswer("Level 2", "Level1Id", "Level2Id"))
				.thenThrow(new RuntimeException("Should only update 2 categories"));
		when(restOperations.exchange(contains(GROUPS_URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(GroupDto.class)))
				.thenAnswer(new NewGroupAnswer("Office of VP for Research", "GroupBL-BL", "Level3Id", "BL-RUGS", true, "GroupRUGS", Collections.emptyMap()))
				.thenAnswer(new NewGroupAnswer("University Purdue", "Group000001", "Level2Id", "IN-IN", true, "GroupIN", Collections.emptyMap()));
		when(restOperations.exchange(contains(GROUPS_URL), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
				.thenAnswer(new UpdatedGroupAnswer("University", null, "Level1Id", "000001", true, "Group000001", univRoleMembers))
				.thenAnswer(new UpdatedGroupAnswer("Bloomington Campus", "Group000001", "Level2Id", "BL-BL", true, "GroupBL-BL", blRoleMembers));

		service.setRestOperations(restOperations);
		CoreGroupsServiceImpl coreGroupsService = new CoreGroupsServiceImpl();
		coreGroupsService.setAuthServiceRestUtilService(service.getAuthServiceRestUtilService());
		coreGroupsService.setConfigurationService(service.getConfigurationService());
		coreGroupsService.setRestOperations(restOperations);
		service.setCoreGroupsService(coreGroupsService);
		service.pushAllGroups();
	}

	private static class NewCategoryAnswer implements Answer<ResponseEntity<CategoryDto>> {
		private String name;
		private String parentId;
		private String newId;

		public NewCategoryAnswer(String name, String parentId, String newId) {
			this.name = name;
			this.parentId = parentId;
			this.newId = newId;
		}

		@Override
		public ResponseEntity<CategoryDto> answer(InvocationOnMock inv) {
			CategoryDto category = ((HttpEntity<CategoryDto>)inv.getArgument(2)).getBody();
			assertEquals(name, category.getName());
			assertEquals(parentId, category.getParentId());
			assertNotNull(category.getFieldSchemas().stream().filter(s -> StringUtils.equals(s.getId(), CoreGroupsService.UNIT_NUMBER_FIELD_ID)).findFirst().orElse(null));
			assertNotNull(category.getFieldSchemas().stream().filter(s -> StringUtils.equals(s.getId(), CoreGroupsService.ACTIVE_FIELD_ID)).findFirst().orElse(null));
			assertEquals(7, category.getRoleSchemas().size());
			CategoryDto newCategory = getCategoryClone(category);
			newCategory.setId(newId);
			return new ResponseEntity<CategoryDto>(newCategory, HttpStatus.CREATED);
		}
	}

	private static class NewGroupAnswer implements Answer<ResponseEntity<GroupDto>> {
		private String name;
		private String parentId;
		private String categoryId;
		private String unitNumber;
		private Boolean active;
		private Map<String, List<String>> roleMembers;
		private String newId;

		public NewGroupAnswer(String name, String parentId,
							  String categoryId, String unitNumber,
							  Boolean active, String newId,
							  Map<String, List<String>> roleMembers) {
			this.name = name;
			this.parentId = parentId;
			this.categoryId = categoryId;
			this.unitNumber = unitNumber;
			this.active = active;
			this.newId = newId;
			this.roleMembers = roleMembers;
		}

		@Override
		public ResponseEntity<GroupDto> answer(InvocationOnMock inv) {
			GroupDto group = ((HttpEntity<GroupDto>)inv.getArgument(2)).getBody();
			assertEquals(name, group.getName());
			assertEquals(parentId, group.getParentId());
			assertEquals(categoryId, group.getCategoryId());
			assertEquals(unitNumber, group.getFields().stream()
					.filter(s -> StringUtils.equals(s.getId(), CoreGroupsService.UNIT_NUMBER_FIELD_ID))
					.findFirst().map(f -> f.getValue()).orElse(null));
			assertEquals(active.toString(), group.getFields().stream()
					.filter(s -> StringUtils.equals(s.getId(), CoreGroupsService.ACTIVE_FIELD_ID))
					.findFirst().map(f -> f.getValue()).orElse(null));
			assertEquals(roleMembers.size(), group.getRoles().size());
			roleMembers.entrySet().stream()
					.forEach(e -> {
						GroupDto.Role role = group.getRoles().stream().filter(r -> StringUtils.equals(r.getId(), e.getKey())).findFirst().orElse(null);
						assertNotNull(role);
						assertEquals(e.getValue(), role.getValue());
					});
			GroupDto newGroup = getGroupClone(group);
			newGroup.setId(newId);
			return new ResponseEntity<GroupDto>(newGroup, HttpStatus.CREATED);
		}
	}

	private static class UpdatedCategoryAnswer implements Answer<ResponseEntity<String>> {
		private String name;
		private String parentId;
		private String newId;

		public UpdatedCategoryAnswer(String name, String parentId, String newId) {
			this.name = name;
			this.parentId = parentId;
			this.newId = newId;
		}

		@Override
		public ResponseEntity<String> answer(InvocationOnMock inv) {
			CategoryDto category = ((HttpEntity<CategoryDto>)inv.getArgument(2)).getBody();
			assertEquals(newId, category.getId());
			assertEquals(name, category.getName());
			assertEquals(parentId, category.getParentId());
			assertNotNull(category.getFieldSchemas().stream().filter(s -> StringUtils.equals(s.getId(), CoreGroupsService.UNIT_NUMBER_FIELD_ID)).findFirst().orElse(null));
			assertNotNull(category.getFieldSchemas().stream().filter(s -> StringUtils.equals(s.getId(), CoreGroupsService.ACTIVE_FIELD_ID)).findFirst().orElse(null));
			assertEquals(7, category.getRoleSchemas().size());
			return new ResponseEntity<String>("updated", HttpStatus.OK);
		}
	}

	private static class UpdatedGroupAnswer implements Answer<ResponseEntity<String>> {
		private String name;
		private String parentId;
		private String categoryId;
		private String unitNumber;
		private Boolean active;
		private Map<String, List<String>> roleMembers;
		private String newId;

		public UpdatedGroupAnswer(String name, String parentId,
								  String categoryId, String unitNumber,
								  Boolean active, String newId,
								  Map<String, List<String>> roleMembers) {
			this.name = name;
			this.parentId = parentId;
			this.categoryId = categoryId;
			this.unitNumber = unitNumber;
			this.active = active;
			this.newId = newId;
			this.roleMembers = roleMembers;
		}

		@Override
		public ResponseEntity<String> answer(InvocationOnMock inv) {
			GroupDto group = ((HttpEntity<GroupDto>)inv.getArgument(2)).getBody();
			assertEquals(newId, group.getId());
			assertEquals(name, group.getName());
			assertEquals(parentId, group.getParentId());
			assertEquals(categoryId, group.getCategoryId());
			assertEquals(unitNumber, group.getFields().stream()
					.filter(s -> StringUtils.equals(s.getId(), CoreGroupsService.UNIT_NUMBER_FIELD_ID))
					.findFirst().map(f -> f.getValue()).orElse(null));
			assertEquals(active.toString(), group.getFields().stream()
					.filter(s -> StringUtils.equals(s.getId(), CoreGroupsService.ACTIVE_FIELD_ID))
					.findFirst().map(f -> f.getValue()).orElse(null));
			assertEquals(roleMembers.size(), group.getRoles().size());
			roleMembers.entrySet().stream()
					.forEach(e -> {
						GroupDto.Role role = group.getRoles().stream().filter(r -> StringUtils.equals(r.getId(), e.getKey())).findFirst().orElse(null);
						assertNotNull(role);
						assertEquals(e.getValue(), role.getValue());
					});
			return new ResponseEntity<String>("updated", HttpStatus.OK);
		}
	}

	UnitAdministratorType buildUnitAdminType(String code, String description) {
		UnitAdministratorType unitAdminType = new UnitAdministratorType();
		unitAdminType.setCode(code);
		unitAdminType.setDescription(description);
		return unitAdminType;
	}

	CategoryDto buildCategoryDto(String id, String parentId, String name, List<CategoryDto.RoleSchemaDto> roleSchemas, List<CategoryDto.FieldSchemaDto> fieldSchemas) {
		CategoryDto category = new CategoryDto();
		category.setId(id);
		category.setParentId(parentId);
		category.setName(name);
		category.setRoleSchemas(roleSchemas);
		category.setFieldSchemas(fieldSchemas);
		return category;
	}

	GroupDto buildGroupDto(String id, String parentId, String name, String categoryId, List<GroupDto.Role> roles, List<GroupDto.GroupFields> fields) {
		GroupDto group = new GroupDto();
		group.setId(id);
		group.setParentId(parentId);
		group.setName(name);
		group.setCategoryId(categoryId);
		group.setRoles(roles);
		group.setFields(fields);
		return group;
	}

	static CategoryDto getCategoryClone(CategoryDto category) {
		CategoryDto clone = new CategoryDto();
		clone.setId(category.getId());
		clone.setName(category.getName());
		clone.setParentId(category.getParentId());
		clone.setFieldSchemas(category.getFieldSchemas());
		clone.setRoleSchemas(category.getRoleSchemas());
		return clone;
	}

	static GroupDto getGroupClone(GroupDto group) {
		GroupDto clone = new GroupDto();
		clone.setId(group.getId());
		clone.setName(group.getName());
		clone.setParentId(group.getParentId());
		clone.setFields(group.getFields());
		clone.setRoles(group.getRoles());
		return clone;
	}

	ConfigurationService getMockedConfigurationService() {
		ConfigurationService config = mock(ConfigurationService.class);
		when(config.getPropertyValueAsString(RestServiceConstants.Configuration.CATEGORIES_URL)).thenReturn(CATEGORIES_URL);
		when(config.getPropertyValueAsString(RestServiceConstants.Configuration.GROUPS_URL)).thenReturn(GROUPS_URL);
		when(config.getPropertyValueAsString(RestServiceConstants.Configuration.AUTH_USERS_URL)).thenReturn(USERS_URL);
		return config;
	}

	AuthServiceRestUtilService getMockedAuthServiceUtils() {
		AuthServiceRestUtilService utilService = mock(AuthServiceRestUtilService.class);
		when(utilService.getAuthServiceStyleHttpHeadersForUser()).thenReturn(new HttpHeaders());
		return utilService;
	}

	RestOperations getMockedRestOperations(List<CategoryDto> categories, List<GroupDto> groups) {
		RestOperations rest = mock(RestOperations.class);
		when(rest.exchange(contains(CATEGORIES_URL), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
				.thenReturn(new ResponseEntity<List<CategoryDto>>(categories, HttpStatus.OK));
		when(rest.exchange(contains(GROUPS_URL), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
				.thenReturn(new ResponseEntity<List<GroupDto>>(groups, HttpStatus.OK));
		when(rest.exchange(eq(USERS_URL + "/?schoolId=123"), eq(HttpMethod.GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuthUser>>() {})))
				.thenReturn(new ResponseEntity<>(Arrays.asList(new AuthUser("111111")), HttpStatus.CREATED));
		when(rest.exchange(eq(USERS_URL + "/?schoolId=234"), eq(HttpMethod.GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuthUser>>() {})))
				.thenReturn(new ResponseEntity<>(Arrays.asList(new AuthUser("222222")), HttpStatus.CREATED));
		when(rest.exchange(eq(USERS_URL + "/?schoolId=345"), eq(HttpMethod.GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuthUser>>() {})))
				.thenReturn(new ResponseEntity<>(Arrays.asList(new AuthUser("333333")), HttpStatus.CREATED));
		when(rest.exchange(eq(USERS_URL + "/?schoolId=456"), eq(HttpMethod.GET), any(HttpEntity.class), eq(new ParameterizedTypeReference<List<AuthUser>>() {})))
				.thenReturn(new ResponseEntity<>(Arrays.asList(new AuthUser("444444")), HttpStatus.CREATED));

		return rest;
	}

	Unit buildUnit(String unitNumber, String unitName, String parentUnitNumber, boolean active) {
		Unit unit = new Unit();
		unit.setActive(active);
		unit.setUnitNumber(unitNumber);
		unit.setUnitName(unitName);
		return unit;
	}

	UnitAdministrator buildUnitAdmin(String unitNumber, String personId, UnitAdministratorType type) {
		UnitAdministrator unitAdmin = new UnitAdministrator();
		unitAdmin.setUnitNumber(unitNumber);
		unitAdmin.setPersonId(personId);
		unitAdmin.setUnitAdministratorType(type);
		return unitAdmin;
	}

	UnitService getMockedUnitService() {
		Unit topUnit = buildUnit("000001", "University", null, true);
		UnitService service = mock(UnitService.class);
		when(service.getTopUnit()).thenReturn(topUnit);
		when(service.retrieveUnitAdministratorsByUnitNumber(anyString())).thenReturn(new ArrayList<>());
		when(service.retrieveUnitAdministratorsByUnitNumber("000001"))
				.thenReturn(Arrays.asList(
						buildUnitAdmin("000001", "123", unitAdministratorTypes.get(0)),
						buildUnitAdmin("000001", "234", unitAdministratorTypes.get(0))
				));
		when(service.retrieveUnitAdministratorsByUnitNumber("BL-BL"))
				.thenReturn(Arrays.asList(
						buildUnitAdmin("BL-BL", "345", unitAdministratorTypes.get(0)),
						buildUnitAdmin("BL-BL", "456", unitAdministratorTypes.get(1))
				));
		when(service.getSubUnits("000001")).thenReturn(Arrays.asList(
				buildUnit("BL-BL", "Bloomington Campus", "000001", true),
				buildUnit("IN-IN", "University Purdue", "000001", true)
		));
		when(service.getSubUnits("BL-BL")).thenReturn(Arrays.asList(
				buildUnit("BL-RUGS", "Office of VP for Research", "000001", true)
		));

		return service;
	}

}
