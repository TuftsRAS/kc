/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.detailsdates;

import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.rice.krad.rules.rule.BusinessRule;


public class AwardDetailsAndDatesSaveEvent extends KcDocumentEventBase {

    private static final org.apache.logging.log4j.Logger LOG =
        org.apache.logging.log4j.LogManager.getLogger(AwardDetailsAndDatesSaveEvent.class);
    
    private Award award;
    
    /**
     * Constructs a AddAwardTransferringSponsorEvent.
     * @param description
     * @param errorPathPrefix
     * @param document
     * @param awardTransferringSponsor
     */
    public AwardDetailsAndDatesSaveEvent(AwardDocument document, Award award) {
        super("adding an award transferring sponsor object" + getDocumentId(document), "awardAmountInfos", document);
        this.award = award;
        logEvent();
    }
    
    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AwardDetailsAndDatesRule) rule).processSaveAwardDetailsAndDates(this);
    }
    
    @Override
    public Class<AwardDetailsAndDatesRule> getRuleInterfaceClass() {
        return AwardDetailsAndDatesRule.class;
    }
    
    @Override
    protected void logEvent() {
        LOG.info("Logging AwardDetailsAndDatesSaveEvent");
    }

    public Award getAward() {
        return award;
    }

}
