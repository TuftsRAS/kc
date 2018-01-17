/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.irb.auth;

import org.kuali.kra.irb.actions.amendrenew.ProtocolModule;

/**
 * The Modify Protocol Others Authorizer determines if
 * the user can modify the other parts of a protocol.
 */
public class ModifyProtocolOthersAuthorizer extends ModifyAmendmentAuthorizer {

    public ModifyProtocolOthersAuthorizer() {
        super(ProtocolModule.OTHERS);
    }
}
