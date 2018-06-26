/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.home.rules;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.DocumentAuditRule;
import org.kuali.rice.krad.util.AuditCluster;
import org.kuali.rice.krad.util.AuditError;

import java.util.ArrayList;
import java.util.List;

public class AwardCfdaAuditRule implements DocumentAuditRule {

    public static final String HOME_PAGE_AUDIT_WARNINGS = "homePageAuditWarnings";
    private GlobalVariableService globalVariableService;

    @Override
    public boolean processRunAuditBusinessRules(Document document) {
        AwardDocument awardDocument = (AwardDocument) document;

        for (int i = 0; i < awardDocument.getAward().getAwardCfdas().size(); i ++) {
            final String cfdaNumber = awardDocument.getAward().getAwardCfdas().get(i).getCfdaNumber();
            if(!isValidCfda(cfdaNumber)) {
                getAuditWarnings(HOME_PAGE_AUDIT_WARNINGS, Constants.AUDIT_WARNINGS).add(
                        new AuditError(String.format(Constants.DOCUMENT_AWARD_CFDA_NUMBER, i), KeyConstants.CFDA_INVALID,
                                Constants.MAPPING_AWARD_HOME_PAGE + "." + Constants.MAPPING_AWARD_HOME_DETAILS_AND_DATES_PAGE_ANCHOR, new String[]{cfdaNumber}));

            }
        }

        return true;
    }

    public boolean isValidCfda(String cfdaNumber) {
        return StringUtils.isBlank(cfdaNumber) || cfdaNumber.matches(Constants.CFDA_REGEX);
    }

    protected List<AuditError> getAuditWarnings(String key, String severity) {
        if (!getGlobalVariableService().getAuditErrorMap().containsKey(key)) {
            getGlobalVariableService().getAuditErrorMap().put(key, new AuditCluster(Constants.MAPPING_AWARD_HOME_DETAILS_AND_DATES_PAGE_NAME, new ArrayList<AuditError>(), severity));
        }
        return getGlobalVariableService().getAuditErrorMap().get(key).getAuditErrorList();

    }

    public GlobalVariableService getGlobalVariableService() {
        if (globalVariableService == null) {
            globalVariableService = KcServiceLocator.getService(GlobalVariableService.class);
        }
        return globalVariableService;
    }

}
