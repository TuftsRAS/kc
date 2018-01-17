/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.auth;

import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.ProtocolSpecialVersion;

public class ContinuationAuthorizer extends ProtocolAuthorizerBase {

    @Override
    public boolean isAuthorized(String userId, ProtocolTaskBase task) {
        return false;
    }

    protected final boolean isAmendmentOrRenewalOrContinuation(ProtocolBase protocol) {
        return protocol.getProtocolNumber() != null &&
               (protocol.getProtocolNumber().contains(ProtocolSpecialVersion.AMENDMENT.getCode()) ||
                       protocol.getProtocolNumber().contains(ProtocolSpecialVersion.CONTINUATION.getCode()) ||
                       protocol.getProtocolNumber().contains(ProtocolSpecialVersion.RENEWAL.getCode()));
    }

}
