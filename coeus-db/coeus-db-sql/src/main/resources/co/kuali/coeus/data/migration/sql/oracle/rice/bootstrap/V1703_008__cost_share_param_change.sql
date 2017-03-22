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

update krcr_parm_t set nmspc_cd = 'KC-GEN' where parm_nm = 'ENABLE_COST_SHARE_ACCOUNT_VALIDATION' and nmspc_cd = 'KC-PD';
update krcr_parm_t set nmspc_cd = 'KC-GEN' where parm_nm = 'Cost_Share_Account_Validation_Message' and nmspc_cd = 'KC-PD';
