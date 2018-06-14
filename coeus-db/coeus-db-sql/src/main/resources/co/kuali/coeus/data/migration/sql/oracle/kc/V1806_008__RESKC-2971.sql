--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

ALTER TABLE AWARD_BUDGET_PERIOD_EXT ADD FRINGE_OVERRIDDEN CHAR(1);
ALTER TABLE AWARD_BUDGET_PERIOD_EXT ADD F_AND_A_OVERRIDDEN CHAR(1);

UPDATE AWARD_BUDGET_PERIOD_EXT SET FRINGE_OVERRIDDEN = RATE_OVERRIDE_FLAG;
UPDATE AWARD_BUDGET_PERIOD_EXT SET F_AND_A_OVERRIDDEN = RATE_OVERRIDE_FLAG;
