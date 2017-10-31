/*
 * Kuali Coeus, a comprehensive research administration system for higher education.
 *
 * Copyright 2005-2016 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.coeus.common.framework.unit.sync;


import org.apache.commons.lang3.StringUtils;
import org.kuali.rice.kim.impl.role.RoleBoLite;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.Map;

public class UnitRoleInquirable extends KualiInquirableImpl {

    @Override
    public BusinessObject getBusinessObject(@SuppressWarnings("unchecked") Map fieldValues) {
        final String id = (String) fieldValues.get("id");

        return StringUtils.isNotBlank(id) ? UnitRole.fromRoleBoLite(this.getDataObjectService().find(RoleBoLite.class, id)) : null;
    }
}
