/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.kra.award.home.rules.impl;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.rules.AwardAddCfdaEvent;
import org.kuali.kra.award.home.rules.AwardCfdaRule;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;

public class AwardCfdaRuleImpl implements AwardCfdaRule {

    private GlobalVariableService globalVariableService;

    @Override
    public boolean processAddCfdaRules(AwardAddCfdaEvent awardAddCfdaEvent) {
        AwardDocument awardDocument = (AwardDocument) awardAddCfdaEvent.getDocument();

        boolean valid = true;

        for (int i = 0; i < awardDocument.getAward().getAwardCfdas().size(); i ++) {
            final String cfdaNumber = awardDocument.getAward().getAwardCfdas().get(i).getCfdaNumber();
            if (StringUtils.isBlank(cfdaNumber)) {
                getGlobalVariableService().getMessageMap().putError(String.format(Constants.DOCUMENT_AWARD_CFDA_NUMBER, i), KeyConstants.CFDA_REQUIRED);
                valid = false;
            } else if (!isValidCfda(cfdaNumber) &&
                    getGlobalVariableService().getMessageMap().getMessages(String.format(Constants.DOCUMENT_DEVELOPMENT_PROPOSAL_CFDA_NUMBER, i)) == null) {
                getGlobalVariableService().getMessageMap().putWarning(String.format(Constants.DOCUMENT_AWARD_CFDA_NUMBER, i), KeyConstants.CFDA_INVALID, cfdaNumber);
            }
        }
        return valid;
    }

    public boolean isValidCfda(String cfdaNumber) {
        return cfdaNumber.matches(Constants.CFDA_REGEX);
    }

    public GlobalVariableService getGlobalVariableService() {
        if (globalVariableService == null) {
            globalVariableService = KcServiceLocator.getService(GlobalVariableService.class);
        }
        return globalVariableService;
    }
}
