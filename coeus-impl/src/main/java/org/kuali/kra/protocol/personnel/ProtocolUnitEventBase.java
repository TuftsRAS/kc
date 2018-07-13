/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.personnel;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.protocol.ProtocolDocumentBase;
import org.kuali.rice.krad.util.ObjectUtils;

public abstract class ProtocolUnitEventBase extends KcDocumentEventBase implements ProtocolUnitEvent {

    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager
            .getLogger(ProtocolUnitEventBase.class);

    private ProtocolUnitBase protocolUnit;
    private int personIndex;

    protected ProtocolUnitEventBase(String description, String errorPathPrefix, ProtocolDocumentBase document,
            ProtocolUnitBase protocolUnit, int personIndex) {
        super(description, errorPathPrefix, document);
        setProtocolUnit(protocolUnit);
        this.personIndex = personIndex;
        logEvent();
    }

    /**
     * @return <code>{@link ProtocolUnitBase}</code> that triggered this event.
     */
    @Override
    public ProtocolUnitBase getProtocolUnit() {
        return this.protocolUnit;
    }

    /**
     * @return <code>{@link PersonIndex}</code> that triggered this event.
     */
    @Override
    public int getPersonIndex() {
        return this.personIndex;
    }
    
    /**
     * This method is to deep copy protocol unit
     * by doing a deep copy, we are ensuring that the business rule class can't update
     * the original object by reference
     */
    private void setProtocolUnit(ProtocolUnitBase protocolUnit) {
        this.protocolUnit = (ProtocolUnitBase) ObjectUtils.deepCopy(protocolUnit);
    }

    /**
     * Logs the event type and some information about the associated unit
     */
    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (getProtocolUnit() == null) {
            logMessage.append("null protocolUnit");
        }
        else {
            logMessage.append(getProtocolUnit().toString());
        }

        LOG.debug(logMessage);
    }
}
