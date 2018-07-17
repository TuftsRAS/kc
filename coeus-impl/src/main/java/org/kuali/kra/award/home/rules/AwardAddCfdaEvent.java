/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.kra.award.home.rules;

import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class AwardAddCfdaEvent extends KcDocumentEventBase {

    private static final org.apache.logging.log4j.Logger LOG =
            org.apache.logging.log4j.LogManager.getLogger(AwardAddCfdaEvent.class);

    private Award award;

    public AwardAddCfdaEvent(AwardDocument document) {
        super("adding cfda number" + getDocumentId(document), "", document);
        logEvent();
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AwardCfdaRule) rule).processAddCfdaRules(this);
    }

    @Override
    public Class<AwardCfdaRule> getRuleInterfaceClass() {
        return AwardCfdaRule.class;
    }

    @Override
    protected void logEvent() {
        LOG.info("Logging AwardAddCfdaEvent");
    }

    public Award getAward() {
        return award;
    }
}
