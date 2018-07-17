/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.commitments;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.rice.krad.rules.rule.BusinessRule;


public class AwardBenefitsRatesRuleEvent extends KcDocumentEventBase {

    private static final Logger LOG = LogManager.getLogger(AwardBenefitsRatesRuleEvent.class);
    private Award award;
    
    public AwardBenefitsRatesRuleEvent(String errorPathPrefix, 
                                        Award award,
                                        AwardDocument awardDocument) {
        super("Benefits Rates", errorPathPrefix, awardDocument);
        this.award = award;
    }
    
    /**
     * Convenience method to return an AwardDocument
     * @return
     */
    public AwardDocument getAwardDocument() {
        return (AwardDocument) getDocument();
    }
    
    
    @Override
    protected void logEvent() {
        LOG.info("Logging AwardBenefitsRatesRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return AwardBenefitsRatesRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AwardBenefitsRatesRule)rule).processBenefitsRatesBusinessRules(this);
    }

    /**
     * Gets the award attribute. 
     * @return Returns the award.
     */
    public Award getAward() {
        return award;
    }

}
