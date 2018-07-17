/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.paymentreports.awardreports;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * This the AwardPaymentScheduleRuleEvent
 */
public class AwardReportTermRecipientRuleEvent extends KcDocumentEventBase {
    private static final Logger LOG = LogManager.getLogger(AwardReportTermRecipientRuleEvent.class);
    
    private Award award;
    private AwardReportTerm parentAwardReportTerm;
    private AwardReportTermRecipient awardReportTermRecipientItem;
    
    public AwardReportTermRecipientRuleEvent(String errorPathPrefix, 
                                            AwardDocument awardDocument,
                                            Award award,
                                            AwardReportTerm parentAwardReportTerm,
                                            AwardReportTermRecipient awardReportTermRecipientItem) {
        super("Payment Schedule item", errorPathPrefix, awardDocument);
        this.award = award;
        this.awardReportTermRecipientItem = awardReportTermRecipientItem;
        this.parentAwardReportTerm = parentAwardReportTerm;
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
    public AwardReportTermRecipient getAwardReportTermRecipientItemForValidation() {
        return awardReportTermRecipientItem;
    }   

    @Override
    protected void logEvent() {
        LOG.info("Logging AwardPaymentScheduleRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return AwardReportTermRecipientRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AwardReportTermRecipientRule)rule).processAwardReportTermRecipientBusinessRules(this);
    }

    /**
     * Gets the parentAwardReportTerm attribute. 
     * @return Returns the parentAwardReportTerm.
     */
    public AwardReportTerm getParentAwardReportTerm() {
        return parentAwardReportTerm;
    }

    /**
     * Sets the parentAwardReportTerm attribute value.
     * @param parentAwardReportTerm The parentAwardReportTerm to set.
     */
    public void setParentAwardReportTerm(AwardReportTerm parentAwardReportTerm) {
        this.parentAwardReportTerm = parentAwardReportTerm;
    }   
}
