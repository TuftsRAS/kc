/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.rule.event;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.rule.AwardCommentsRule;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class AwardCommentsRuleEvent extends KcDocumentEventBase {
    private static final Logger LOG = LogManager.getLogger(AwardCommentsRuleEvent.class);

    public AwardCommentsRuleEvent(String errorPathPrefix, AwardDocument awardDocument) {
        super("Processing rules for Award Comments", errorPathPrefix, awardDocument);
    }

    @Override
    protected void logEvent() {
        LOG.info("Logging AwardCommentsRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return AwardCommentsRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AwardCommentsRule) rule).processAwardCommentsBusinessRules(this);
    }
}
