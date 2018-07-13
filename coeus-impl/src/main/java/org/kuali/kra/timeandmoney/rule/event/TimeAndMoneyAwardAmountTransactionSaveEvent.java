/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.timeandmoney.rule.event;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.timeandmoney.document.TimeAndMoneyDocument;
import org.kuali.kra.timeandmoney.rules.TimeAndMoneyAwardAmountTransactionRule;
import org.kuali.rice.krad.rules.rule.BusinessRule;


public class TimeAndMoneyAwardAmountTransactionSaveEvent extends KcDocumentEventBase {

    private static final Logger LOG = LogManager.getLogger(TimeAndMoneyAwardAmountTransactionSaveEvent.class);
    private static final String AWARD_AMOUNT_TRANSACTION = "Award Amount Transaction";
    
    /**
     * Constructor for rule event for save rules.
     * @param errorPathPrefix
     * @param timeAndMoneyDocument
     */
    public TimeAndMoneyAwardAmountTransactionSaveEvent(String errorPathPrefix, 
                                                        TimeAndMoneyDocument timeAndMoneyDocument) {
            super(AWARD_AMOUNT_TRANSACTION, errorPathPrefix, timeAndMoneyDocument);
    }
    
    
    
    /**
     * Convenience method to return an TimeAndMoneyDocument
     * @return
     */
    public TimeAndMoneyDocument getTimeAndMoneyDocument() {
        return (TimeAndMoneyDocument) getDocument();
    }
    
    @Override
    protected void logEvent() {
        LOG.info("Logging AwardAmountTransactionRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return TimeAndMoneyAwardAmountTransactionRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((TimeAndMoneyAwardAmountTransactionRule) rule).processSaveAwardAmountTransactionBusinessRules(this);
    }

}
