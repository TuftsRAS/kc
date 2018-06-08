--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--



CREATE TABLE SEQ_WORKFLOW_USER_DET_ID
(
	id bigint(19) not null auto_increment, primary key (id)
) ENGINE MyISAM;

ALTER TABLE SEQ_WORKFLOW_USER_DET_ID auto_increment = 1;

CREATE TABLE DOCUMENT_WORKFLOW_USER_DETAILS (
ID BIGINT(19) NOT NULL PRIMARY KEY,
	PRINCIPAL_ID VARCHAR(12) NOT NULL,
	DOCUMENT_NUMBER VARCHAR(40) NOT NULL,
	STEPS DECIMAL(3,0) NOT NULL,
	UPDATE_TIMESTAMP DATE NOT NULL,
	UPDATE_USER VARCHAR(60) NOT NULL,
 	VER_NBR DECIMAL(8,0) DEFAULT 1 NOT NULL,
 	OBJ_ID VARCHAR(36) NOT NULL
    );
