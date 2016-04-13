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
package org.kuali.coeus.s2sgen.impl.generate.support;

import org.kuali.coeus.common.framework.person.attr.CitizenshipType;

public class PHS398CareerDevelopmentAwardSupV3_0GeneratorTest extends PHS398CareerDevelopmentAwardSupBaseGeneratorTest {

	@Override
	protected String getFormGeneratorName() {
		return PHS398CareerDevelopmentAwardSupV3_0Generator.class.getSimpleName();
	}

	protected CitizenshipType getCitizenshipType() {
		CitizenshipType citizenshipType = new CitizenshipType();
		citizenshipType.setCode(16);
		return citizenshipType;
	}
}