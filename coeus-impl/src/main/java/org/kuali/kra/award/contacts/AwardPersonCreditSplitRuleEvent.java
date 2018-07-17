/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.contacts;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;

import java.util.Map;

/**
 * This class is used by AwardPersonCreditSplitRule implementers
 */
public class AwardPersonCreditSplitRuleEvent extends KcDocumentEventBase {
    private static final Logger LOG = LogManager.getLogger(AwardPersonCreditSplitRuleEvent.class);
    
    private Map<String, ScaleTwoDecimal> totalsByCreditSplitType;
    
    /**
     * Constructs a AwardPersonCreditSplitRuleEvent
     * @param description
     * @param errorPathPrefix
     * @param document
     */
    public AwardPersonCreditSplitRuleEvent(Document document, Map<String, ScaleTwoDecimal> totalsByCreditSplitType) {
        super("Credit splits invalid", "document.awardList[0].creditSplits.*", document);
        this.totalsByCreditSplitType = totalsByCreditSplitType;
    }

    @Override
    protected void logEvent() {
        LOG.info("Logging event");
    }

    @Override
    public Class<AwardPersonCreditSplitRule> getRuleInterfaceClass() {
        return AwardPersonCreditSplitRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return false;
    }

    /**
     * Gets the totalsByCreditSplitType attribute. 
     * @return Returns the totalsByCreditSplitType.
     */
    public Map<String, ScaleTwoDecimal> getTotalsByCreditSplitType() {
        return totalsByCreditSplitType;
    }
}
