--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

UPDATE krcr_parm_t
	SET PARM_NM='Show_Full_Name_In_Header_Fields', PARM_DESC_TXT='Includes the person full name in the pertinent document header fields.'
	WHERE NMSPC_CD='KC-GEN' && CMPNT_CD='All' && PARM_NM='Show_Full_Name_In_Last_Updated_By' && APPL_ID='KC';
