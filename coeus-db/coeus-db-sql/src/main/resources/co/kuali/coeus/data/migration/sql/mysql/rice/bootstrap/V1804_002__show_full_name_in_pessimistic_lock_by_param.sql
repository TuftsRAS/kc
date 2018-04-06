--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

INSERT INTO krcr_parm_t (NMSPC_CD, CMPNT_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, VAL, PARM_DESC_TXT, EVAL_OPRTR_CD, APPL_ID)
  VALUES('KC-GEN', 'All', 'Show_Full_Name_In_Pessimistic_Lock', UUID(), 1, 'CONFG', 'false', 'Includes the person full name in the pessimistic lock message.', 'A', 'KC');
