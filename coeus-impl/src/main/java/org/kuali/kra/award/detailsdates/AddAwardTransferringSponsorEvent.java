/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.detailsdates;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.sponsor.Sponsor;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * This class represents the AddAwardTransferringSponsorEvent
 * 
 * @author Kuali Coeus development team
 */
public class AddAwardTransferringSponsorEvent extends KcDocumentEventBase {
    
    private static final org.apache.logging.log4j.Logger LOG =
        org.apache.logging.log4j.LogManager.getLogger(AddAwardTransferringSponsorEvent.class);
    
    private Sponsor sponsorToBecomeAwardTransferringSponsor;
    private Award award;
    
    /**
     * Constructs a AddAwardTransferringSponsorEvent.
     * @param description
     * @param errorPathPrefix
     * @param document
     * @param awardTransferringSponsor
     */
    public AddAwardTransferringSponsorEvent(String errorPathPrefix, AwardDocument document, Award award, Sponsor sponsor) {
        super("adding an award transferring sponsor object" + getDocumentId(document), errorPathPrefix, document);
        this.sponsorToBecomeAwardTransferringSponsor = sponsor;
        this.award = award;
        logEvent();
    }
    
    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AwardDetailsAndDatesRule) rule).processAddAwardTransferringSponsorEvent(this);
    }
    
    @Override
    public Class<AwardDetailsAndDatesRule> getRuleInterfaceClass() {
        return AwardDetailsAndDatesRule.class;
    }
    
    /**
     * Logs the event type and some information about the associated sponsor
     */
    @Override
    protected void logEvent() {
        if(LOG.isDebugEnabled()) {
            StringBuilder logMessage = new StringBuilder(StringUtils.substringAfterLast(
                    this.getClass().getName(), "."));
            logMessage.append(" with ");

            if (getSponsorToBecomeAwardTransferringSponsor() == null) {
                logMessage.append("null Award Transferring Sponsor");
            } else {
                logMessage.append(getSponsorToBecomeAwardTransferringSponsor().toString());
            }

            LOG.debug(logMessage);            
        }
    }

    public Sponsor getSponsorToBecomeAwardTransferringSponsor() {
        return sponsorToBecomeAwardTransferringSponsor;
    }

    public Award getAward() {
        return award;
    }
    
}
