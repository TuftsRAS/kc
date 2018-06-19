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

set define off
set sqlblanklines on

spool 1806_oracle_kc_rice_server_upgrade.sql.log
@./rice/bootstrap/V1806_001__RESOPS-2413.sql
@./rice/bootstrap/V1806_003__RESOPS-2413.sql
@./rice/bootstrap/V1806_004__RESOPS-2413.sql
@./rice/bootstrap/V1806_005__RESKC-2956.sql
@./rice/bootstrap/V1806_007__RESKC-2956.sql
@./rice/bootstrap/V1806_008__cfdaParameterUpdates.sql
@./rice/bootstrap/V1806_010__excluded_ipstatuses_pending.sql
commit;
