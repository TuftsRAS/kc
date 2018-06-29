--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

CREATE SEQUENCE SEQ_ARG_VALUE_LOOKUP_ID INCREMENT BY 1 START WITH 10000 NOMAXVALUE NOCYCLE NOCACHE ORDER;

ALTER TABLE ARG_VALUE_LOOKUP ADD ACTV_IND CHAR(1) default 'N' ;

update ARG_VALUE_LOOKUP set ACTV_IND = 'Y';

INSERT INTO CUSTOM_ATTRIBUTE_DATA_TYPE (DATA_TYPE_CODE,DESCRIPTION,UPDATE_USER,UPDATE_TIMESTAMP,OBJ_ID,VER_NBR)
VALUES ('4','Boolean','KRADEV',SYSDATE,SYS_GUID(),1);
