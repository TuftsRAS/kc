/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.attachment;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.util.ObjectUtils;

import java.io.Serializable;
import java.util.List;

/**
 * The NewNarrativeUserRightsEvent is generated when the User Rights for
 * a narrative are to be changed.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class NewNarrativeUserRightsEvent extends KcDocumentEventBase {
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(NewNarrativeUserRightsEvent.class);
    
    private List<NarrativeUserRights> newNarrativeUserRights;
    private int narrativeIndex;
    
    /**
     * Constructs a NewNarrativeUserRightsEvent.
     * @param document proposal development document
     * @param newNarrativeUserRights the list of user rights for a narrative
     * @param narrativeIndex the narrative to change in the proposal
     */
    public NewNarrativeUserRightsEvent(ProposalDevelopmentDocument document, List<NarrativeUserRights> newNarrativeUserRights, int narrativeIndex) {
        super("editing narrative user rights to document " + getDocumentId(document), "", document);
    
        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        this.newNarrativeUserRights = (List<NarrativeUserRights>) ObjectUtils.deepCopy((Serializable) newNarrativeUserRights);
        this.narrativeIndex = narrativeIndex;
        
        logEvent();
    }
    
    @Override
    public void validate() {
        super.validate();
        if (this.newNarrativeUserRights == null) {
            throw new IllegalArgumentException("invalid (null) newNarrativeUserRights");
        }
    }
    
    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (this.newNarrativeUserRights == null) {
            logMessage.append("null newNarrativeUserRights");
        }
        else {
            logMessage.append(this.newNarrativeUserRights.toString());
            logMessage.append(Integer.toString(this.narrativeIndex));
        }

        LOG.debug(logMessage);
    }

    @Override
    public Class getRuleInterfaceClass() {
        return NewNarrativeUserRightsRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((NewNarrativeUserRightsRule) rule).processNewNarrativeUserRightsBusinessRules((ProposalDevelopmentDocument) this.getDocument(), 
                                                                        this.newNarrativeUserRights, this.narrativeIndex);
    }
}
