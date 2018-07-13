/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.protocol.location;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.protocol.ProtocolDocumentBase;
import org.kuali.rice.krad.util.ObjectUtils;

public abstract class ProtocolLocationEventBase extends KcDocumentEventBase implements ProtocolLocationEvent {

    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager
            .getLogger(ProtocolLocationEventBase.class);

    private ProtocolLocationBase protocolLocation;

    protected ProtocolLocationEventBase(String description, String errorPathPrefix, ProtocolDocumentBase document,
            ProtocolLocationBase protocolLocation) {
        super(description, errorPathPrefix, document);

        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        this.protocolLocation = (ProtocolLocationBase) ObjectUtils.deepCopy(protocolLocation);

        logEvent();
    }

    protected ProtocolLocationEventBase(String description, String errorPathPrefix, ProtocolDocumentBase document) {
        super(description, errorPathPrefix, document);
        logEvent();
    }
    
    /**
     * @return <code>{@link ProtocolLocationBase}</code> that triggered this event.
     */
    @Override
    public ProtocolLocationBase getProtocolLocation() {
        return this.protocolLocation;
    }

    /**
     * Logs the event type and some information about the associated location
     */
    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (getProtocolLocation() == null) {
            logMessage.append("null protocolLocation");
        }
        else {
            logMessage.append(getProtocolLocation().toString());
        }

        LOG.debug(logMessage);
    }
}
