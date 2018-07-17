/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.framework.auth;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.framework.auth.perm.Permissionable;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.kim.bo.KcKimAttributes;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.impl.KimDocumentTypeAuthorizer;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class KcWorkflowDocumentAuthorizerBase extends KimDocumentTypeAuthorizer {
    
    protected static final Logger LOG = LogManager.getLogger(KcWorkflowDocumentAuthorizerBase.class);
    
    private transient DocumentService documentService;
    private transient BusinessObjectService businessObjectService;

    /**
     * Implements {@link org.kuali.rice.kew.doctype.service.DocumentTypePermissionService#canRecall(String, org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue)}
     */
    @Override
    public boolean canRecall(String principalId, DocumentRouteHeaderValue document) {
        validatePrincipalId(principalId);
        validateDocument(document);
        String documentId = document.getDocumentId();
        DocumentType documentType = document.getDocumentType();
        String documentStatus = document.getDocRouteStatus();
        String appDocStatus = document.getAppDocStatus();
        List<String> routeNodeNames = document.getCurrentNodeNames();
        validateDocumentType(documentType);
        validateDocumentStatus(documentStatus);
        // no need to validate appdocstatus, this is a free-form application defined value

        // add appDocStatus to the details
        List<Map<String, String>> permissionDetailList = buildDocumentTypePermissionDetailsForNodes(documentType, routeNodeNames, documentStatus, null);
        if (!StringUtils.isBlank(appDocStatus)) {
            for (Map<String, String> details: permissionDetailList) {
                details.put(KewApiConstants.APP_DOC_STATUS_DETAIL, appDocStatus);
            }
        }
        
        // loop over permission details, only one of them needs to be authorized
        for (Map<String, String> permissionDetails : permissionDetailList) {
            if (useKimPermission(KewApiConstants.KEW_NAMESPACE, KewApiConstants.RECALL_PERMISSION, permissionDetails, false)) {
                if (getPermissionService().isPermissionDefinedByTemplate(KewApiConstants.KEW_NAMESPACE, KewApiConstants.RECALL_PERMISSION, permissionDetails)) {
                    List<Map<String, String>> qualifierList = getSetsOfRoleQualifiers(document, permissionDetails.get(KewApiConstants.ROUTE_NODE_NAME_DETAIL));
                    for (Map<String, String> roleQualifiers : qualifierList) {
                        if (getPermissionService().isAuthorizedByTemplate(principalId, KewApiConstants.KEW_NAMESPACE,
                                KewApiConstants.RECALL_PERMISSION, permissionDetails, roleQualifiers)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    protected List<Map<String, String>> getSetsOfRoleQualifiers(DocumentRouteHeaderValue document, String routeNodeName) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        Map<String, String> defaultQualifications = buildDocumentRoleQualifiers(document, routeNodeName);
        result.add(defaultQualifications);
        Permissionable permissionable = getPermissionable(document.getDocumentId());
        if (permissionable != null) {
            Map<String, String> docNbrQualifiers = new HashMap<String, String>();
            docNbrQualifiers.put(permissionable.getDocumentKey(), permissionable.getDocumentNumberForPermission());
            docNbrQualifiers.putAll(defaultQualifications);
            result.add(docNbrQualifiers);
            
            Map<String, String> unitNumberQualifiers = new HashMap<String, String>();
            unitNumberQualifiers.put(KcKimAttributes.UNIT_NUMBER, permissionable.getLeadUnitNumber());
            unitNumberQualifiers.putAll(defaultQualifications);
            result.add(unitNumberQualifiers);
        }
        return result;
    }
    
    protected abstract Permissionable getPermissionable(String documentId);

    public DocumentService getDocumentService() {
        if (documentService == null) {
            documentService = KcServiceLocator.getService(DocumentService.class);
        }
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = KcServiceLocator.getService(BusinessObjectService.class);
        }
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}

