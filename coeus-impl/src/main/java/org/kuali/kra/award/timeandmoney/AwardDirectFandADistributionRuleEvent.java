/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.timeandmoney;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.timeandmoney.document.TimeAndMoneyDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

import java.util.List;

/**
 * This class represents the rule event for Award Direct F and A Distribution tab.
 */
public class AwardDirectFandADistributionRuleEvent extends KcDocumentEventBase {

    private static final Logger LOG = LogManager.getLogger(AwardDirectFandADistributionRuleEvent.class);
    private static final String DIRECT_FNA_DISTRIBUTION = "Direct F and A Distribution";
    
    AwardDirectFandADistribution awardDirectFandADistribution;
    List<AwardDirectFandADistribution> awardDirectFandADistributions;
    int currentIndex;
    
    /**
     * Constructor for rule event for add rules.
     * @param errorPathPrefix
     * @param awardDocument
     * @param awardDirectFandADistribution
     */
    public AwardDirectFandADistributionRuleEvent(String errorPathPrefix, 
                                                        TimeAndMoneyDocument timeAndMoneyDocument, AwardDirectFandADistribution awardDirectFandADistribution) {
            super(DIRECT_FNA_DISTRIBUTION, errorPathPrefix, timeAndMoneyDocument);
            this.awardDirectFandADistribution = awardDirectFandADistribution;
    }
    
    /**
     * Constructor for rule event for save rules.
     * @param errorPathPrefix
     * @param awardDocument
     * @param awardDirectFandADistributions
     */
    public AwardDirectFandADistributionRuleEvent(String errorPathPrefix, 
                                                        TimeAndMoneyDocument timeAndMoneyDocument, 
                                                            List<AwardDirectFandADistribution> awardDirectFandADistributions) {
            super(DIRECT_FNA_DISTRIBUTION, errorPathPrefix, timeAndMoneyDocument);
            this.awardDirectFandADistributions = awardDirectFandADistributions;
    }
    
    /**
     * Convenience method to return an AwardDocument
     * @return
     */
    public TimeAndMoneyDocument getTimeAndMoneyDocument() {
        return (TimeAndMoneyDocument) getDocument();
    }
    
    /**
     * This method returns the awardDirectFandADistribution for validation
     * @return
     */
    public AwardDirectFandADistribution getAwardDirectFandADistributionForValidation() {
        return awardDirectFandADistribution;
    }
    
    /**
     * This method returns the list of awardDirectFandADistributions for validation
     * @return
     */
    public List<AwardDirectFandADistribution> getAwardDirectFandADistributionsForValidation() {
        return awardDirectFandADistributions;
    }
    
    
    @Override
    protected void logEvent() {
        LOG.info("Logging AwardDirectFandADistributionRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return AwardDirectFandADistributionRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AwardDirectFandADistributionRule) rule).processAddAwardDirectFandADistributionBusinessRules(this);
    }

    /**
     * Gets the currentIndex attribute. 
     * @return Returns the currentIndex.
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Sets the currentIndex attribute value.
     * @param currentIndex The currentIndex to set.
     */
    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

}
