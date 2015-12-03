--
-- Kuali Coeus, a comprehensive research administration system for higher education.
--
-- Copyright 2005-2015 Kuali, Inc.
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

alter table PROPOSAL_ADMIN_DETAILS modify INST_PROP_CREATE_USER varchar(60);

update PROPOSAL_ADMIN_DETAILS set INST_PROP_CREATE_DATE = CREATE_TIMESTAMP, INST_PROP_CREATE_USER = CREATE_USER;

alter table PROPOSAL_ADMIN_DETAILS drop CREATE_TIMESTAMP;
alter table PROPOSAL_ADMIN_DETAILS drop CREATE_USER;