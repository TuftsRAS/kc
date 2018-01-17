/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.person.creditsplit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.coeus.propdev.impl.person.ProposalPerson;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class CalculateCreditSplitEvent  extends KcDocumentEventBase {

    private static final Log LOG = LogFactory.getLog(CalculateCreditSplitEvent.class);

    /**
     * Constructs an CalculateCreditSplitEvent with the given errorPathPrefix, document.
     * 
     * @param errorPathPrefix
     * @param document
     * @see org.kuali.coeus.sys.framework.rule.KcDocumentEventBase#KcDocumentEventBase(String, String, Document)
     */
    public CalculateCreditSplitEvent(String errorPathPrefix, ProposalDevelopmentDocument document) {
        super("Saving personnel on document " + getDocumentId(document), errorPathPrefix, document);
        logEvent();
    }
    

    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");
        
        for (ProposalPerson person : ((ProposalDevelopmentDocument) getDocument()).getDevelopmentProposal().getProposalPersons()) {
            logMessage.append(person.toString());
            logMessage.append(", ");
        }
        
        if (logMessage.substring(logMessage.length() - 2).equals(", ")) {
            logMessage.delete(logMessage.length() - 2, logMessage.length());
        }

        LOG.debug(logMessage);
        
    }

    
    /**
     * Constructs an SaveKeyPersonEvent with the given errorPathPrefix, document, and proposalPerson.
     * 
     * @param errorPathPrefix
     * @param document
     */
    public CalculateCreditSplitEvent(String errorPathPrefix, Document document) {
        this(errorPathPrefix, (ProposalDevelopmentDocument) document);
    }
    
    
    @Override
    public Class getRuleInterfaceClass() {

        return CalculateCreditSplitRule.class;
    }
    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        LOG.info("Calling processCalculateCreditSplitBusinessRules on " + rule.getClass().getSimpleName());
        return ((CalculateCreditSplitRule) rule).processCalculateCreditSplitBusinessRules((ProposalDevelopmentDocument) getDocument());
    }

   
    
    
    

}
