--
-- Kuali Coeus, a comprehensive research administration system for higher education.
--
-- Copyright 2005-2016 Kuali, Inc.
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
--
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
--

insert into VALID_NARR_FORMS (VALID_NARR_FORMS_ID,FORM_NAME,NARRATIVE_TYPE_CODE,MANDATORY,UPDATE_TIMESTAMP,UPDATE_USER,OBJ_ID)
values (SEQ_VALID_NARR_FORMS_ID.nextval,'SF424Mandatory1_2_V1_2',136,null,sysdate,'admin',sys_guid());