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
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentPersonnelBase;
import org.kuali.rice.krad.document.Document;

public abstract class ProtocolAttachmentPersonnelEventBase  extends KcDocumentEventBase implements ProtocolAttachmentPersonnelEvent {
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager
    .getLogger(ProtocolAttachmentPersonnelEventBase.class);

    private ProtocolAttachmentPersonnelBase protocolAttachmentPersonnel;
    private int personIndex;

    protected ProtocolAttachmentPersonnelEventBase(String description, String errorPathPrefix, Document document,
            ProtocolAttachmentPersonnelBase protocolAttachmentPersonnel, int personIndex) {
        super(description, errorPathPrefix, document);
        setProtocolAttachmentPersonnel(protocolAttachmentPersonnel);
        setPersonIndex(personIndex);
    }

    @Override
    public ProtocolAttachmentPersonnelBase getProtocolAttachmentPersonnel() {
        return protocolAttachmentPersonnel;
    }

    public void setProtocolAttachmentPersonnel(ProtocolAttachmentPersonnelBase protocolAttachmentPersonnel) {
        this.protocolAttachmentPersonnel = protocolAttachmentPersonnel;
    }

    @Override
    public int getPersonIndex() {
        return personIndex;
    }

    public void setPersonIndex(int personIndex) {
        this.personIndex = personIndex;
    }

    /**
     * Logs the event type and some information about the associated unit
     */
    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (getProtocolAttachmentPersonnel() == null) {
            logMessage.append("null protocolAttachmentPersonnel");
        }
        else {
            logMessage.append(getProtocolAttachmentPersonnel().toString());
        }

        LOG.debug(logMessage);
    }

}
