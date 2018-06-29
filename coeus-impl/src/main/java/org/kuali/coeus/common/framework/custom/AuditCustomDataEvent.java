/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.framework.custom;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.custom.attr.CustomAttribute;
import org.kuali.coeus.sys.framework.model.KcTransactionalDocumentBase;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.krad.util.AuditCluster;
import org.kuali.rice.krad.util.AuditError;

import java.util.ArrayList;
import java.util.List;

public class AuditCustomDataEvent extends SaveCustomDataEvent {

    private static final String CUSTOM_DATA_AUDIT_KEY = "CustomData";
    private static final String CUSTOM_DATA_AUDIT_ERRORS = "Errors";
    private static final String CUSTOM_DATA_AUDIT_WARNINGS = "Warnings";

    public AuditCustomDataEvent(KcTransactionalDocumentBase document) {
        super(document, true);
    }
    
    @Override
    public void reportError(CustomAttribute customAttribute, String propertyName, String errorKey, String... errorParams) {
        reportErrorOrWarning(customAttribute, propertyName, errorKey, Constants.AUDIT_ERRORS, errorParams);
    }

    @Override
    public void reportWarning(CustomAttribute customAttribute, String propertyName, String errorKey, String... errorParams) {
        reportErrorOrWarning(customAttribute, propertyName, errorKey, Constants.AUDIT_WARNINGS, errorParams);
    }

    public void reportErrorOrWarning(CustomAttribute customAttribute, String propertyName, String errorKey, String errorCategory, String... errorParams) {
        String category = Constants.AUDIT_ERRORS.equals(errorCategory) ? CUSTOM_DATA_AUDIT_ERRORS : CUSTOM_DATA_AUDIT_WARNINGS;
        String key = CUSTOM_DATA_AUDIT_KEY + StringUtils.deleteWhitespace(customAttribute.getGroupName()) + category;
        AuditCluster auditCluster = getGlobalVariableService().getAuditErrorMap().get(key);
        if (auditCluster == null) {
            List<AuditError> auditErrors = new ArrayList<AuditError>();
            auditCluster = new AuditCluster(customAttribute.getGroupName(), auditErrors, errorCategory);
            getGlobalVariableService().getAuditErrorMap().put(key, auditCluster);
        }
        List<AuditError> auditErrors = auditCluster.getAuditErrorList();
        auditErrors.add(new AuditError(propertyName, errorKey, StringUtils.deleteWhitespace(Constants.CUSTOM_ATTRIBUTES_PAGE + "." + customAttribute.getGroupName()), errorParams));
    }

}
