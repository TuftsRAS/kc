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
\. ./kc/bootstrap/V602_001__RESKC-1.sql
\. ./kc/bootstrap/V602_002__RESKC-2.sql
\. ./kc/bootstrap/V602_007__RESKC-229.sql
\. ./kc/bootstrap/V602_010__RESKC-204.sql
\. ./kc/bootstrap/V602_012__RESKC-290.sql
\. ./kc/bootstrap/V602_013__KC_DML_01_KRACOEUS-7120_B000.sql

commit;
exit