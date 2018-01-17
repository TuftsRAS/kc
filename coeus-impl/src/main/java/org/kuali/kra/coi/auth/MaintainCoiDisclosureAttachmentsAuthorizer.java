/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.coi.auth;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.coi.CoiDisclosure;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.kim.bo.KcKimAttributes;
import org.kuali.rice.kim.api.permission.PermissionService;

import java.util.HashMap;
import java.util.Map;

public class MaintainCoiDisclosureAttachmentsAuthorizer extends CoiDisclosureAuthorizer {

    @Override
    public boolean isAuthorized(String userId, CoiDisclosureTask task) {
        boolean hasPermission = true;
        
        // check if the user is creating a new disclosure and has the explicit report disclosure permissions
        CoiDisclosure coiDisclosure = task.getCoiDisclosure();
        if (isNewDisclosure(coiDisclosure)) {
            hasPermission = hasUnitPermission(userId, Constants.MODULE_NAMESPACE_COIDISCLOSURE, PermissionConstants.REPORT_COI_DISCLOSURE);
        } else if (isNotSubmitted(coiDisclosure)) {
            if (isDisclosureReporter(userId, coiDisclosure)) {
                hasPermission = true;
            } else {
                //hasPermission = hasUnitPermission(userId, Constants.MODULE_NAMESPACE_COIDISCLOSURE, PermissionConstants.MAINTAIN_COI_DISCLOSURE_NOTES)
                hasPermission = getPermissionService().isAuthorized(userId, Constants.MODULE_NAMESPACE_COIDISCLOSURE, PermissionConstants.MAINTAIN_COI_DISCLOSURE_NOTES, getQualificationMap(task.getCoiDisclosure()))
                            && isEditableByAdminReviewer(coiDisclosure);
            }
        } else {
            // check if the user is the original reporter for the saved disclosure and that the disclosure is editable.  
            // Since the disclosure is submitted at this point, the reporter no longer has permission to edit.
            if (isDisclosureReporter(userId, coiDisclosure)) {
                hasPermission = false;
            } else {
                //hasPermission = hasUnitPermission(userId, Constants.MODULE_NAMESPACE_COIDISCLOSURE, PermissionConstants.MAINTAIN_COI_DISCLOSURE_NOTES)
                hasPermission = getPermissionService().isAuthorized(userId, Constants.MODULE_NAMESPACE_COIDISCLOSURE, PermissionConstants.MAINTAIN_COI_DISCLOSURE_NOTES, getQualificationMap(task.getCoiDisclosure()))
                    && !isDocumentFinal(coiDisclosure) && isEditableByAdminReviewer(coiDisclosure);                    
            }
        }
        
    return hasPermission;  
    }

    protected boolean isNewDisclosure(CoiDisclosure coiDisclosure) {
        if (coiDisclosure == null) {
            return true;
        } else {
            if (coiDisclosure.getPersonId() == null) {
                return true;
            }
        }
        
        return false;
    }
    
    protected boolean isNotSubmitted(CoiDisclosure coiDisclosure) {
        return !coiDisclosure.isSubmitted();
    }
    
    protected boolean isDocumentFinal(CoiDisclosure coiDisclosure) {
        return coiDisclosure.getCoiDisclosureDocument().getDocumentHeader().getWorkflowDocument().isFinal();     
    }
    
    protected boolean isDisclosureReporter(String userId, CoiDisclosure coiDisclosure) {
        return StringUtils.equals(userId, coiDisclosure.getPersonId());
    }

    protected PermissionService getPermissionService() {
        return KcServiceLocator.getService(PermissionService.class);
    }

    private Map<String, String> getQualificationMap(CoiDisclosure coiDisclosure) {
        Map<String, String> qualifications = new HashMap<String, String>();
        
        qualifications.put("coiDisclosureId", coiDisclosure.getCoiDisclosureId().toString());
        qualifications.put(KcKimAttributes.UNIT_NUMBER, "*");
        
        return qualifications;
    }

}
