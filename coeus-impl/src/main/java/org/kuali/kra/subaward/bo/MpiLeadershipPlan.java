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

import java.util.Arrays;


public enum MpiLeadershipPlan implements org.kuali.rice.core.api.mo.common.Coded, Coded, Describable {
    REQUEST("0", "The PTE will make the MPI plan available upon request"),
    ATTACHED("1", "The MPI plan is attached as part of Attachment 2");

    private final String code;
    private final String description;

    MpiLeadershipPlan(String code, String description) {
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

    public static MpiLeadershipPlan fromCode(String code) {
        return Arrays.stream(MpiLeadershipPlan.values()).filter(v -> v.getCode().equals(code)).findFirst().orElse(null);
    }
}
