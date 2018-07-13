/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.abstrct;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * The AddAbstractEvent is generated when a Proposal Abstract is to be added to
 * a Proposal Development Document.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class AddAbstractEvent extends KcDocumentEventBase {
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(AddAbstractEvent.class);
    
    private ProposalAbstract proposalAbstract;
    
    /**
     * Constructs an AddAbstractEvent.
     * 
     * @param document proposal development document
     * @param proposalAbstract the proposal abstract that is being added
     */
    public AddAbstractEvent(ProposalDevelopmentDocument document, ProposalAbstract proposalAbstract) {
        super("adding abstract to document " + getDocumentId(document), "", document);
    
        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        this.proposalAbstract = (ProposalAbstract) ObjectUtils.deepCopy(proposalAbstract);
    
        logEvent();
    }
    
    @Override
    public void validate() {
        super.validate();
        if (this.proposalAbstract == null) {
            throw new IllegalArgumentException("invalid (null) proposal abstract");
        }
    }
    
    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (this.proposalAbstract == null) {
            logMessage.append("null proposalAbstract");
        }
        else {
            logMessage.append(this.proposalAbstract.toString());
        }

        LOG.debug(logMessage);
    }

    @Override
    public Class getRuleInterfaceClass() {
        return AbstractsRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AbstractsRule) rule).processAddAbstractBusinessRules((ProposalDevelopmentDocument) this.getDocument(), 
                                                                        this.proposalAbstract);
    }
}
