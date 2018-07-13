/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.timeandmoney.transactions;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.timeandmoney.document.TimeAndMoneyDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * This the AwardPaymentScheduleRuleEvent
 */
public class TransactionRuleEvent extends KcDocumentEventBase {
    private static final Logger LOG = LogManager.getLogger(TransactionRuleEvent.class);
    
    private PendingTransaction pendingTransactionItem;
    
    public TransactionRuleEvent(String errorPathPrefix, 
                                            TimeAndMoneyDocument timeAndMoneyDocument,
                                            PendingTransaction pendingTransactionItem) {
        super("Pending Transaction item", errorPathPrefix, timeAndMoneyDocument);
        this.pendingTransactionItem = pendingTransactionItem;
    }

    /**
     * Convenience method to return an AwardDocument
     * @return
     */
    public TimeAndMoneyDocument getTimeAndMoneyDocument() {
        return (TimeAndMoneyDocument) getDocument();
    }
    
    /**
     * This method returns the Pending Transaction item for validation
     * @return
     */
    public PendingTransaction getPendingTransactionItemForValidation() {
        return pendingTransactionItem;
    }   

    @Override
    protected void logEvent() {
        LOG.info("Logging TransactionRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return TransactionRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((TransactionRule)rule).processPendingTransactionBusinessRules(this);
    }   
}
