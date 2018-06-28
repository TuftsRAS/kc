--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--


CREATE TABLE SEQ_DOCUMENT_WORKLOAD_DET_ID
(
	id bigint(19) not null auto_increment, primary key (id)
) ENGINE MyISAM;

ALTER TABLE SEQ_DOCUMENT_WORKLOAD_DET_ID auto_increment = 1;

CREATE TABLE DOCUMENT_WORKLOAD_DETAILS (
ID BIGINT(19) NOT NULL PRIMARY KEY,
	DOCUMENT_NUMBER VARCHAR(40) NOT NULL,
	LAST_ACTION_TIME DATE,
    CURRENT_PPL_FLW_STOP DECIMAL(8,0),
	UPDATE_TIMESTAMP DATE NOT NULL,
	UPDATE_USER VARCHAR(60) NOT NULL,
 	VER_NBR DECIMAL(8,0) DEFAULT 1 NOT NULL,
 	OBJ_ID VARCHAR(36) NOT NULL
    );

ALTER TABLE DOCUMENT_WORKLOAD_DETAILS
ADD CONSTRAINT UQ_WORKLOAD_DOC
UNIQUE (DOCUMENT_NUMBER);

ALTER TABLE DOCUMENT_WORKLOAD_DETAILS
ADD CONSTRAINT UQ_WORKLOAD_OBJ_ID
UNIQUE (OBJ_ID);