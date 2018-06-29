--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

CREATE TABLE SEQ_ARG_VALUE_LOOKUP_ID (
  id BIGINT(19) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (id)
) ENGINE MyISAM;

ALTER TABLE SEQ_ARG_VALUE_LOOKUP_ID AUTO_INCREMENT = 10000;

ALTER TABLE ARG_VALUE_LOOKUP ADD ACTV_IND CHAR(1) default 'N';

update ARG_VALUE_LOOKUP set ACTV_IND = 'Y';

INSERT INTO CUSTOM_ATTRIBUTE_DATA_TYPE (DATA_TYPE_CODE,DESCRIPTION,UPDATE_USER,UPDATE_TIMESTAMP,OBJ_ID,VER_NBR)
VALUES ('4','Boolean','KRADEV',NOW(),UUID(),1);
