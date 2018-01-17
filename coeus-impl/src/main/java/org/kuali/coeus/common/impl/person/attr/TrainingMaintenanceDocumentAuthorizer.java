/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.person.attr;

import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.HashMap;
import java.util.Map;

public class TrainingMaintenanceDocumentAuthorizer extends MaintenanceDocumentAuthorizerBase {
    public static final String PERMISSION_MAINTAIN_TRAINING = "Maintain Training";
    public static final String KC_SYS = "KC-SYS";
    
    
    @Override
    public boolean canInitiate(String documentTypeName, Person user) {
        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, documentTypeName);
        
        boolean retVal =  getPermissionService().isAuthorized(user.getPrincipalId(), KC_SYS, PERMISSION_MAINTAIN_TRAINING, permissionDetails);
        return retVal;
    }
    
    @Override
    public boolean canMaintain(Object dataObject, Person user) {    
        Map<String, String> permissionDetails = new HashMap<>(2);
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME,
                getDocumentDictionaryService().getMaintenanceDocumentTypeName(
                        dataObject.getClass()));
        permissionDetails.put(KRADConstants.MAINTENANCE_ACTN, KC_SYS);
        return !permissionExistsByTemplate(KC_SYS,
                KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT,
                permissionDetails)
                || isAuthorizedByTemplate(
                        dataObject,
                        KC_SYS,
                        KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT,
                        user.getPrincipalId(), permissionDetails, null);
    }
}
