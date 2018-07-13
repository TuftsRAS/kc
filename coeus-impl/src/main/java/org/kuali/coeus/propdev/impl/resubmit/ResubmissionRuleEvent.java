/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.resubmit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * Defines the event of validating the options for Proposal Development resubmission.
 */
public class ResubmissionRuleEvent extends KcDocumentEventBase {
    
    private static final Logger LOG = LogManager.getLogger(ResubmissionRuleEvent.class);
    
    private String resubmissionOption;
    
    /**
     * Constructs a ResubmissionRuleEvent.
     * 
     * @param document the document being validated
     * @param resubmissionOption the option for resubmission entered by the user
     */
    public ResubmissionRuleEvent(Document document, String resubmissionOption) {
        super("resubmission prompt for proposal document " + getDocumentId(document), "", document);
        
        this.resubmissionOption = resubmissionOption;
    }
    
    public String getResubmissionOption() {
        return resubmissionOption;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((ResubmissionPromptRule) rule).processResubmissionPromptBusinessRules(this);
    }

    @Override
    public Class<ResubmissionPromptRule> getRuleInterfaceClass() {
        return ResubmissionPromptRule.class;
    }

    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        LOG.debug(logMessage);
    }

}
