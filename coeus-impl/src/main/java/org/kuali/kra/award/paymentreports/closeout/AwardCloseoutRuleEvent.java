/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.paymentreports.closeout;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * This the AwardCloseoutRuleEvent
 */
public class AwardCloseoutRuleEvent extends KcDocumentEventBase {
    private static final Logger LOG = LogManager.getLogger(AwardCloseoutRuleEvent.class);
    
    private Award award;
    private AwardCloseout closeoutItem;
    
    /**
     * 
     * Constructs a AwardCloseoutRuleEvent.java.
     * @param errorPathPrefix
     * @param awardDocument
     * @param award
     * @param closeoutItem
     */
    public AwardCloseoutRuleEvent(String errorPathPrefix, 
                                            AwardDocument awardDocument,
                                            Award award,
                                            AwardCloseout closeoutItem) {
        super("Closeout item", errorPathPrefix, awardDocument);
        this.award = award;
        this.closeoutItem = closeoutItem;
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
     * This method returns the closeout item for validation
     * @return
     */
    public AwardCloseout getCloseoutItemForValidation() {
        return closeoutItem;
    }   

    @Override
    protected void logEvent() {
        LOG.info("Logging AwardCloseoutRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return AwardCloseoutRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AwardCloseoutRule)rule).processAwardCloseoutBusinessRules(this);
    }   
}
