/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.iacuc.correspondence;

import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.protocol.correspondence.ProtocolCorrespondenceTemplateActionBase;
import org.kuali.kra.protocol.correspondence.ProtocolCorrespondenceTemplateAuthorizationService;
import org.kuali.kra.protocol.correspondence.ProtocolCorrespondenceTemplateFormBase;
import org.kuali.kra.protocol.correspondence.ProtocolCorrespondenceTemplateService;

public class IacucProtocolCorrespondenceTemplateAction extends ProtocolCorrespondenceTemplateActionBase{

    @Override
    protected Class<? extends ProtocolCorrespondenceTemplateAuthorizationService> getProtocolCorrespondenceTemplateAuthorizationServiceClassHook() {
        return IacucProtocolCorrespondenceTemplateAuthorizationService.class;
    }

    @Override
    protected ProtocolCorrespondenceTemplateFormBase getNewProtocolCorrespondenceTemplateFormInstanceHook() {
        return new IacucProtocolCorrespondenceTemplateForm();
    }

    @Override
    protected String getViewCorrespondenceTemplatePermissionNameHook() {
        return PermissionConstants.VIEW_IACUC_CORRESPONDENCE_TEMPLATE;
    }

    @Override
    protected Class<? extends ProtocolCorrespondenceTemplateService> getProtocolCorrespondenceTemplateServiceClassHook() {
        return IacucProtocolCorrespondenceTemplateService.class;
    }

    @Override
    protected String getModifyCorrespondenceTemplatePermissionNameHook() {
        return PermissionConstants.MODIFY_IACUC_CORRESPONDENCE_TEMPLATE;
    }

}
