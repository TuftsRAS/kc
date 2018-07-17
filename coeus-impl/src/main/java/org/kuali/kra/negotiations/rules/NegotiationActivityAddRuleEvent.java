/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.negotiations.rules;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.negotiations.bo.NegotiationActivity;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * 
 * Negotiation Activity Add Rule Event - Event to use when validating new activities.
 */
public class NegotiationActivityAddRuleEvent extends KcDocumentEventBase {

    private static final Logger LOG = LogManager.getLogger(NegotiationActivityAddRuleEvent.class);
    
    private NegotiationActivity newActivity;
    
    /**
     * 
     * Constructs a NegotiationActivityAddRuleEvent.java.
     * @param description
     * @param errorPathPrefix
     * @param document
     * @param newActivity
     */
    public NegotiationActivityAddRuleEvent(String description, String errorPathPrefix, Document document, NegotiationActivity newActivity) {
        super(description, errorPathPrefix, document);
        this.newActivity = newActivity;
    }
    
    @Override
    public Class<NegotiationActivityAddRule> getRuleInterfaceClass() {
        return NegotiationActivityAddRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((NegotiationActivityAddRule) rule).processAddNegotiationActivityRule(this);
    }

    @Override
    protected void logEvent() {
        LOG.info("Logging NegotiationActivityAddRuleEvent");
    }

    public NegotiationActivity getNewActivity() {
        return newActivity;
    }
}
