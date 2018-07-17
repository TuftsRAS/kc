/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.iacuc.actions.submit;

import org.apache.logging.log4j.Logger;
import org.kuali.kra.iacuc.IacucProtocolDocument;
import org.kuali.kra.protocol.actions.submit.ProtocolSubmitActionEventBase;

public class IacucProtocolSubmitActionEvent extends ProtocolSubmitActionEventBase {
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(IacucProtocolSubmitActionEvent.class);

    public IacucProtocolSubmitActionEvent(IacucProtocolDocument document, IacucProtocolSubmitAction submitAction) {
        super(document, submitAction);
    }

    @Override
    protected Logger getLOGHook() {
        return LOG;
    }
    
    
}
