/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.irb.auth;

import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.irb.actions.ProtocolActionType;

/**
 * Is the user allowed to request a close of the enrollment of a protocol?
 */
public class ProtocolRequestReOpenEnrollmentAuthorizer extends ProtocolAuthorizer {

    @Override
    public boolean isAuthorized(String userId, ProtocolTask task) {
        return !isAmendmentOrRenewal(task.getProtocol()) &&
               canExecuteAction(task.getProtocol(), ProtocolActionType.REQUEST_TO_REOPEN_ENROLLMENT) &&
               hasPermission(userId, task.getProtocol(), PermissionConstants.SUBMIT_PROTOCOL); 
    }
}
