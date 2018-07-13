/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.impl.auth;

import org.kuali.coeus.sys.framework.controller.rest.SimpleCrudMapBasedRestController;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.kim.impl.permission.PermissionTemplateBo;

public class PermissionController extends SimpleCrudMapBasedRestController<PermissionBo> {

    @Override
	protected void validateBusinessObject(PermissionBo dataObject) {
		if (dataObject.getTemplateId() != null) {
			dataObject.setTemplate(
					PermissionTemplateBo.from(getPermissionService().getPermissionTemplate(dataObject.getTemplateId())));
		}
		super.validateBusinessObject(dataObject);
	}

}
