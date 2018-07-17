/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.award.commitments.AwardCostShareRuleEvent;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.AwardSponsorTerm;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * AwardSponsorTermRuleEvent class for rule processing.
 */
public class AwardSponsorTermRuleEvent extends KcDocumentEventBase {
    
    private static final Logger LOG = LogManager.getLogger(AwardCostShareRuleEvent.class);
    private AwardSponsorTerm awardSponsorTerm;
    private String sponsorTermCode;
    private int sponsorTermTypeCode;

    /**
     * Constructs a AwardSponsorTermRuleEvent.
     * @param errorPathPrefix
     * @param awardDocument
     * @param awardSponsorTerm
     * @param sponsorTermCode the sponsor term code from the HTTP request
     * @param sponsorTermTypeCode the index of the subpanel within the terms panel
     */
    public AwardSponsorTermRuleEvent(String errorPathPrefix, 
                                           AwardDocument awardDocument,
                                           AwardSponsorTerm awardSponsorTerm,
                                           String sponsorTermCode,
                                           int sponsorTermTypeCode) {
        super("Cost Share", errorPathPrefix, awardDocument);
        this.awardSponsorTerm = awardSponsorTerm;
        this.sponsorTermCode = sponsorTermCode;
        this.sponsorTermTypeCode = sponsorTermTypeCode;
    }
    
    /**
     * Convenience method to return an AwardDocument
     * @return
     */
    public AwardDocument getAwardDocument() {
        return (AwardDocument) getDocument();
    }
    
    /**
     * This method returns the sponsor term for validation
     * @return
     */
    public AwardSponsorTerm getAwardSponsorTermForValidation() {
        return awardSponsorTerm;
    }
    
    public String getSponsorTermCode() {
        return sponsorTermCode;
    }

    public int getSponsorTermTypeCode() {
        return sponsorTermTypeCode;
    }

    @Override
    protected void logEvent() {
        LOG.info("Logging AwardSponsorTermRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return AwardSponsorTermRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AwardSponsorTermRule)rule).processAddSponsorTermBusinessRules(this);
    }

}
