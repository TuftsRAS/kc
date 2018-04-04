--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

INSERT INTO KRCR_PARM_T (nmspc_cd, cmpnt_cd, parm_nm, OBJ_ID, VER_NBR, parm_typ_cd, val, parm_desc_txt, eval_oprtr_cd, appl_id)
  VALUES('KC-GEN', 'All', 'Show_Full_Name_In_Last_Updated_By', SYS_GUID(), 1, 'CONFG', 'false', 'Includes the person full name in the document header last updated by field.', 'A', 'KC');
