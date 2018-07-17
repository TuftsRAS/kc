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

import java.util.List;

/**
 * The DeleteProposalUserEvent is generated when a Proposal User is to be delete from
 * a Proposal Development Document.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class DeleteProposalUserEvent extends KcDocumentEventBase {
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(DeleteProposalUserEvent.class);
    
    private int index;
    private List<ProposalUserRoles> list;
    
    /**
     * Constructs an DeleteProposalUserEvent.
     * 
     * @param document proposal development document
     * @param list the list of proposal user roles
     * @param index the index into the list corresponding to the user to delete
     */
    public DeleteProposalUserEvent(ProposalDevelopmentDocument document, List<ProposalUserRoles> list, int index) {
        super("delete proposal user from to document " + getDocumentId(document), "", document);
    
        this.list = list;
        this.index = index;
    
        logEvent();
    }
    
    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");
        logMessage.append(this.index);
        LOG.debug(logMessage);
    }

    @Override
    public Class getRuleInterfaceClass() {
        return PermissionsRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((PermissionsRule) rule).processDeleteProposalUserBusinessRules((ProposalDevelopmentDocument) this.getDocument(), 
                                                                               list, index);
    }
}
