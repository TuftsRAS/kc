/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.iacuc.personnel;

import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentService;
import org.kuali.kra.protocol.personnel.ProtocolAttachmentPersonnelRuleBase;

public class IacucProtocolAttachmentPersonnelRule extends ProtocolAttachmentPersonnelRuleBase {

    @Override
    public ProtocolAttachmentService getProtocolAttachmentService() {
        return KcServiceLocator.getService("iacucProtocolAttachmentService");
    }

}
