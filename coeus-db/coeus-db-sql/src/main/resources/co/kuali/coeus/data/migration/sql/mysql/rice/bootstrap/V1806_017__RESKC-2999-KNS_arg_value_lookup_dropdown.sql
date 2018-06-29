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
values ('KC-GEN', 'Document', 'Display_KNS_Arg_Value_Lookups_As_Dropdowns', uuid(), 1, 'CONFG', 'false', 'Determines whether to display Arg Value Lookup fields as a drop-down (true) or as a lookup (false). Default is false.', 'A', 'KC');
