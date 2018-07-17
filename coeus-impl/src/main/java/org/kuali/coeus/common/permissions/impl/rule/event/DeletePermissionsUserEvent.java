/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.permissions.impl.rule.event;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.permissions.impl.rule.PermissionsRule;
import org.kuali.coeus.common.permissions.impl.web.bean.User;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;

import java.util.List;

/**
 * The DeletePermissionsUserEvent is generated when a user is to be deleted from
 * a document via the Permissions tab web page.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class DeletePermissionsUserEvent extends KcDocumentEventBase {
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(DeletePermissionsUserEvent.class);
    
    private int index;
    private List<User> users;
    
    /**
     * Constructs an DeletePermissionsUserEvent.
     * @param document the document
     * @param users the current list of users who have roles in the document
     * @param index the index into the users of the user to delete
     */
    public DeletePermissionsUserEvent(Document document, List<User> users, int index) {
        super("delete user roles from document " + getDocumentId(document), "", document);
    
        this.users = users;
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
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return PermissionsRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((PermissionsRule) rule).processDeletePermissionsUserBusinessRules(this.getDocument(), 
                                                                                  users, index);
    }
}
