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
package org.kuali.kra.subaward.bo;

import org.kuali.coeus.sys.api.model.Coded;
import org.kuali.coeus.sys.api.model.Describable;


public enum HumanPteSendNotRequiredReason implements org.kuali.rice.core.api.mo.common.Coded, Coded, Describable {
    PTE_AS_IRB("PI", "PTE is acting as the IRB"),
    IRB_DESIGNATED("ID","There is an IRB designated"),
    EXEMPT("EX", "Exempt"),
    APPROVAL_YEAR_ONE("AYO", "Approval will be sought after year 1");

    private final String code;
    private final String description;

    HumanPteSendNotRequiredReason(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
