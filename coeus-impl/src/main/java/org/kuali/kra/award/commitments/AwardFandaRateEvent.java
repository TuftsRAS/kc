/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.commitments;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * 
 * This is the base event class for <code>AwardFandaRate</code> business object.
 */
public abstract class AwardFandaRateEvent extends KcDocumentEventBase {
    private static final org.apache.logging.log4j.Logger LOG =
        org.apache.logging.log4j.LogManager.getLogger(AwardFandaRateEvent.class);

    private AwardFandaRate awardFandaRate;

    /**
     * 
     * Constructs a AwardFandaRateEventBase.java.
     * @param description
     * @param errorPathPrefix
     * @param document
     * @param awardFandaRate
     */
    protected AwardFandaRateEvent(String description, String errorPathPrefix, 
            AwardDocument document, AwardFandaRate awardFandaRate) {
        super(description, errorPathPrefix, document);
    
        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        this.awardFandaRate = (AwardFandaRate) ObjectUtils.deepCopy(awardFandaRate);
        logEvent();
    }

    /**
    * @return <code>{@link AwardFandaRate}</code> that triggered this event.
    */
    public AwardFandaRate getAwardFandaRate() {
        return awardFandaRate;
    }

    /**
    * @see org.kuali.core.rule.event.DocumentEvent#validate()
    */
    @Override
    public void validate() {
        super.validate();
        if (getAwardFandaRate() == null) {
            throw new IllegalArgumentException("invalid (null) award fandaRate");
        }
    }

    /**
    * Logs the event type and some information about the associated special review
    */
    @Override
    protected void logEvent() {
        if(LOG.isDebugEnabled()){
            StringBuilder logMessage = new StringBuilder(StringUtils.substringAfterLast(
                    this.getClass().getName(), "."));
            logMessage.append(" with ");
            
            // vary logging detail as needed
            if (getAwardFandaRate() == null) {
                logMessage.append("null award F and a Rate Event");
            }else {
                logMessage.append(getAwardFandaRate().toString());
            }
            
            LOG.debug(logMessage);    
        }
    }
    
}
