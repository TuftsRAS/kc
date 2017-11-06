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

import org.kuali.coeus.sys.api.model.Coded;
import org.kuali.coeus.sys.api.model.Describable;

import java.util.Arrays;

public enum SourceUnit implements org.kuali.rice.core.api.mo.common.Coded, Coded, Describable {
    PERSON_APPOINTMENTS("RESBOOT1", "Person Appointments"),
    PERSON_PRIMARY_DEPARTMENTS("RESBOOT2", "Person Primary Departments"),
    PERSON_SECONDARY_DEPARTMENTS("RESBOOT3", "Person Secondary Departments");
    private final String code;
    private final String description;

    SourceUnit(String code, String description) {
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

    public static SourceUnit fromCode(String code) {
        return Arrays.stream(values())
                .filter(v -> v.getCode().equals(code))
                .findFirst()
                .<IllegalArgumentException>orElseThrow(() -> {
                    throw new IllegalArgumentException("invalid code" + code);
                });
    }
}