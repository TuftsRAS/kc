--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

delete from KRIM_ROLE_PERM_T where PERM_ID = (select PERM_ID from KRIM_PERM_T where NMSPC_CD = 'KC-UNT' and nm = 'MAINTAIN CUSTOM REPORTS');
delete from KRIM_PERM_T where NMSPC_CD = 'KC-UNT' and nm = 'MAINTAIN CUSTOM REPORTS';
