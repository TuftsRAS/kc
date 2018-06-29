/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.custom;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.custom.AuditCustomDataEvent;
import org.kuali.coeus.common.framework.custom.attr.CustomAttribute;
import org.kuali.coeus.sys.framework.model.KcTransactionalDocumentBase;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.krad.util.AuditCluster;
import org.kuali.rice.krad.util.AuditError;

import java.util.ArrayList;
import java.util.List;

import static org.kuali.coeus.propdev.impl.datavalidation.ProposalDevelopmentDataValidationConstants.SUPPLEMENTAL_PAGE_ID;
import static org.kuali.coeus.propdev.impl.datavalidation.ProposalDevelopmentDataValidationConstants.SUPPLEMENTAL_PAGE_NAME;

public class AuditProposalCustomDataEvent extends AuditCustomDataEvent {
	
	public AuditProposalCustomDataEvent(KcTransactionalDocumentBase document) {
		super(document);
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
        String key = SUPPLEMENTAL_PAGE_NAME + "." +customAttribute.getGroupName();
        AuditCluster auditCluster = getGlobalVariableService().getAuditErrorMap().get(key);
        if (auditCluster == null) {
            List<AuditError> auditErrors = new ArrayList<AuditError>();
            auditCluster = new AuditCluster(key, auditErrors, errorCategory);
            getGlobalVariableService().getAuditErrorMap().put(key, auditCluster);
        }
        List<AuditError> auditErrors = auditCluster.getAuditErrorList();
        auditErrors.add(new AuditError(StringUtils.removePattern(customAttribute.getGroupName() + "_" + customAttribute.getLabel(), "([^0-9a-zA-Z\\-_])"),
                errorKey, SUPPLEMENTAL_PAGE_ID + "." + customAttribute.getGroupName().replace(" ","_"), errorParams));

    }
}
