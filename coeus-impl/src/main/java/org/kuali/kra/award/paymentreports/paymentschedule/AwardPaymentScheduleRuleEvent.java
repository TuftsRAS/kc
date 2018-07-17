/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.paymentreports.paymentschedule;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * This the AwardPaymentScheduleRuleEvent
 */
public class AwardPaymentScheduleRuleEvent extends KcDocumentEventBase {
    private static final Logger LOG = LogManager.getLogger(AwardPaymentScheduleRuleEvent.class);
    
    private Award award;
    private AwardPaymentSchedule paymentScheduleItem;
    
    public AwardPaymentScheduleRuleEvent(String errorPathPrefix, 
                                            AwardDocument awardDocument,
                                            Award award,
                                            AwardPaymentSchedule paymentScheduleItem) {
        super("Payment Schedule item", errorPathPrefix, awardDocument);
        this.award = award;
        this.paymentScheduleItem = paymentScheduleItem;
    }

    /**
     * Convenience method to return an Award
     * @return
     */
    public Award getAward() {
        return award;
    }
    
    /**
     * Convenience method to return an AwardDocument
     * @return
     */
    public AwardDocument getAwardDocument() {
        return (AwardDocument) getDocument();
    }
    
    /**
     * This method returns the payment schedule item for validation
     * @return
     */
    public AwardPaymentSchedule getPaymentScheduleItemForValidation() {
        return paymentScheduleItem;
    }   

    @Override
    protected void logEvent() {
        LOG.info("Logging AwardPaymentScheduleRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return AwardPaymentScheduleRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AwardPaymentScheduleRule)rule).processAwardPaymentScheduleBusinessRules(this);
    }   
}
