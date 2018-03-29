--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

insert into krcr_parm_t (nmspc_cd, cmpnt_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, val, parm_desc_txt, eval_oprtr_cd, appl_id)
values ('KC-IP', 'Document', 'Excluded_Codes_CP_Report', sys_guid(), 1, 'CONFG', '4;6;501', 'List the Proposal Type Codes which should be excluded from the current and pending reports. List these by a semicolon. Ex, 3;4', 'A', 'KC');
