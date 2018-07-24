--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

ALTER TABLE `eps_prop_sites`
	ADD COLUMN `ADDRESS_LINE_1` varchar(80) DEFAULT NULL AFTER `ROLODEX_ID`;
ALTER TABLE `eps_prop_sites`
	ADD COLUMN `ADDRESS_LINE_2` varchar(80) DEFAULT NULL AFTER `ADDRESS_LINE_1`;
ALTER TABLE `eps_prop_sites`
	ADD COLUMN `ADDRESS_LINE_3` varchar(80) DEFAULT NULL AFTER `ADDRESS_LINE_2`;
ALTER TABLE `eps_prop_sites`
	ADD COLUMN `CITY` varchar(30) DEFAULT NULL AFTER `ADDRESS_LINE_3`;
ALTER TABLE `eps_prop_sites`
	ADD COLUMN `COUNTY` varchar(30) DEFAULT NULL AFTER `CITY`;
ALTER TABLE `eps_prop_sites`
	ADD COLUMN `STATE` varchar(30) DEFAULT NULL AFTER `COUNTY`;
ALTER TABLE `eps_prop_sites`
	ADD COLUMN `POSTAL_CODE` varchar(15) DEFAULT NULL AFTER `STATE`;
ALTER TABLE `eps_prop_sites`
	ADD COLUMN `COUNTRY_CODE` char(3) DEFAULT NULL AFTER `POSTAL_CODE`;

UPDATE `eps_prop_sites` ps
	SET ps.`ROLODEX_ID` = (SELECT `CONTACT_ADDRESS_ID` FROM `organization` o WHERE o.`ORGANIZATION_ID` = ps.`ORGANIZATION_ID`)
	WHERE ps.`ORGANIZATION_ID` IS NOT NULL AND ps.`ROLODEX_ID` is NULL;

UPDATE `eps_prop_sites` ps
	SET ps.`ADDRESS_LINE_1` = (SELECT `ADDRESS_LINE_1` FROM `rolodex` r WHERE r.`ROLODEX_ID` = ps.`ROLODEX_ID`),
      ps.`ADDRESS_LINE_2` = (SELECT `ADDRESS_LINE_2` FROM `rolodex` r WHERE r.`ROLODEX_ID` = ps.`ROLODEX_ID`),
      ps.`ADDRESS_LINE_3` = (SELECT `ADDRESS_LINE_3` FROM `rolodex` r WHERE r.`ROLODEX_ID` = ps.`ROLODEX_ID`),
      ps.`CITY` = (SELECT `CITY` FROM `rolodex` r WHERE r.`ROLODEX_ID` = ps.`ROLODEX_ID`),
      ps.`COUNTY` = (SELECT `COUNTY` FROM `rolodex` r WHERE r.`ROLODEX_ID` = ps.`ROLODEX_ID`),
      ps.`STATE` = (SELECT `STATE` FROM `rolodex` r WHERE r.`ROLODEX_ID` = ps.`ROLODEX_ID`),
      ps.`POSTAL_CODE` = (SELECT `POSTAL_CODE` FROM `rolodex` r WHERE r.`ROLODEX_ID` = ps.`ROLODEX_ID`),
      ps.`COUNTRY_CODE` = (SELECT `COUNTRY_CODE` FROM `rolodex` r WHERE r.`ROLODEX_ID` = ps.`ROLODEX_ID`)
	WHERE ps.`ROLODEX_ID` IS NOT NULL;