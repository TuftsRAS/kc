--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

INSERT INTO S2S_ERROR (S2S_ERROR_ID, MESSAGE_KEY, MESSAGE, FIX_LINK, UPDATE_TIMESTAMP, UPDATE_USER, VER_NBR, OBJ_ID)
VALUES (SEQ_S2S_ERROR_ID.NEXTVAL, '/GrantApplication/Forms/RR_Budget_1_4/BudgetYear/Equipment/EquipmentList/EquipmentItem', 'Equipment line item description must not exceed the 64 character limit allowed by the budget form.','budgetVersions',SYSDATE,'admin',1,SYS_GUID());