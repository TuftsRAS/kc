/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.docperm;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.List;

/**
 * The AddProposalUserEvent is generated when a Proposal User is to be added to
 * a Proposal Development Document.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class AddProposalUserEvent extends KcDocumentEventBase {
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(AddProposalUserEvent.class);

    private ProposalUserRoles proposalUserRoles;
    private List<ProposalUserRoles> list;
    
    /**
     * Constructs an AddProposalUserEvent.
     * 
     * @param document proposal development document
     * @param list the list of proposal user roles
     * @param proposalUserRoles the proposal user that is being added
     */
    public AddProposalUserEvent(ProposalDevelopmentDocument document, List<ProposalUserRoles> list, ProposalUserRoles proposalUserRoles) {
        super("adding proposal user to document " + getDocumentId(document), "", document);
        
        this.list = list;
        
        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        
        this.proposalUserRoles = (ProposalUserRoles) ObjectUtils.deepCopy(proposalUserRoles);
    
        logEvent();
    }
    
    @Override
    public void validate() {
        super.validate();
        if (this.proposalUserRoles == null) {
            throw new IllegalArgumentException("invalid (null) proposal user");
        }
    }
    
    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (this.proposalUserRoles == null) {
            logMessage.append("null proposalUser");
        }
        else {
            logMessage.append(this.proposalUserRoles.toString());
        }

        LOG.debug(logMessage);
    }

    @Override
    public Class getRuleInterfaceClass() {
        return PermissionsRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((PermissionsRule) rule).processAddProposalUserBusinessRules((ProposalDevelopmentDocument) this.getDocument(), 
                                                                            list, this.proposalUserRoles);
    }
}
