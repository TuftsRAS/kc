--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

CREATE SEQUENCE SEQ_WORKFLOW_USER_DET_ID INCREMENT BY 1 START WITH 1 NOCACHE;

CREATE TABLE DOCUMENT_WORKFLOW_USER_DETAILS (
ID NUMBER(19,0) NOT NULL PRIMARY KEY,
	PRINCIPAL_ID VARCHAR2(12) NOT NULL,
	DOCUMENT_NUMBER VARCHAR2(40) NOT NULL,
	STEPS NUMBER(3,0) NOT NULL,
	UPDATE_TIMESTAMP DATE NOT NULL,
	UPDATE_USER VARCHAR2(60) NOT NULL,
 	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL,
 	OBJ_ID VARCHAR2(36) NOT NULL
    );
