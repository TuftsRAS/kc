/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.auth;

import org.kuali.coeus.common.framework.auth.SystemAuthorizationService;
import org.kuali.coeus.common.framework.auth.UnitAuthorizationService;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * The Unit Authorization Service Implementation.
 *
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
@Component("unitAuthorizationService")
public class UnitAuthorizationServiceImpl implements UnitAuthorizationService {

    public static final String UNIT_NUMBER = "unitNumber";

    @Autowired
    @Qualifier("systemAuthorizationService")
    private SystemAuthorizationService systemAuthorizationService;

    @Autowired
    @Qualifier("roleService")
    private RoleService roleManagementService;

    @Autowired
    @Qualifier("permissionService")
    private PermissionService permissionService;
    
    protected RoleService getRoleService() {
        return roleManagementService;
    }

    public void setRoleManagementService(RoleService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }

    protected SystemAuthorizationService getSystemAuthorizationService() {
        return systemAuthorizationService;
    }
    
    public void setSystemAuthorizationService(SystemAuthorizationService systemAuthorizationService) {
        this.systemAuthorizationService = systemAuthorizationService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public boolean hasPermission(String userId, String unitNumber, String namespaceCode, String permissionName) {
        boolean userHasPermission = false;

        if (unitNumber != null) {
            // Check the unit for the permission. If the user doesn't have the permission
            // in the given unit, traverse up the Unit Hierarchy to see if the user has
            // the permission in a higher-level unit with the SUBUNITS flag set to YES.
            Map<String, String> qualifiedRoleAttributes = new HashMap<String, String>();
            qualifiedRoleAttributes.put(UNIT_NUMBER, unitNumber);
            
            //The UnitHierarchyRoleTypeService takes care of traversing the Unit tree.
            userHasPermission = permissionService.isAuthorized(userId, namespaceCode, permissionName, qualifiedRoleAttributes);
        }
        return userHasPermission;
    }

    @Override
    public boolean hasPermission(String userId, String namespaceCode, String permissionName) {
        boolean userHasPermission = false;
        Map<String, String> qualifiedRoleAttributes = new HashMap<String, String>();
        qualifiedRoleAttributes.put(UNIT_NUMBER, "*");
        userHasPermission = permissionService.isAuthorized(userId, namespaceCode, permissionName, qualifiedRoleAttributes);
        return userHasPermission;
    }
}
