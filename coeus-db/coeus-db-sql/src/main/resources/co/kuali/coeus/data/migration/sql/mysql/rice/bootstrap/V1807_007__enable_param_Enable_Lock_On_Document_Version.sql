--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

UPDATE `krcr_parm_t` SET VAL='Y'
	WHERE `NMSPC_CD`='KC-GEN' AND `CMPNT_CD`='Document' AND `PARM_NM`='Enable_Lock_On_Document_Version';
