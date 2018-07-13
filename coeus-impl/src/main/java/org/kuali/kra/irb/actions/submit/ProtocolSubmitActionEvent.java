/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.irb.actions.submit;

import org.apache.logging.log4j.Logger;
import org.kuali.kra.protocol.ProtocolDocumentBase;
import org.kuali.kra.protocol.actions.submit.ProtocolSubmitAction;
import org.kuali.kra.protocol.actions.submit.ProtocolSubmitActionEventBase;

public class ProtocolSubmitActionEvent extends ProtocolSubmitActionEventBase {
        
    public ProtocolSubmitActionEvent(ProtocolDocumentBase document, ProtocolSubmitAction submitAction) {
        super(document, submitAction);
    }

    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(ProtocolSubmitActionEvent.class);

    @Override
    protected Logger getLOGHook() {
        return LOG;
    }
    
}
