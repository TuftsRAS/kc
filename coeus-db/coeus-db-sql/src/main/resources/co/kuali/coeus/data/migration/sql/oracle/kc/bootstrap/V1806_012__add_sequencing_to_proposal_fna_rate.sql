--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

ALTER TABLE PROPOSAL_FNA_RATE
ADD (PROPOSAL_NUMBER VARCHAR(8),
	SEQUENCE_NUMBER DECIMAL(4,0));

UPDATE PROPOSAL_FNA_RATE PFNA
	SET PROPOSAL_NUMBER = (SELECT PROPOSAL_NUMBER FROM PROPOSAL WHERE PROPOSAL_ID = PFNA.PROPOSAL_ID),
	SEQUENCE_NUMBER = (SELECT SEQUENCE_NUMBER FROM PROPOSAL WHERE PROPOSAL_ID = PFNA.PROPOSAL_ID);

ALTER TABLE PROPOSAL_FNA_RATE
MODIFY (PROPOSAL_NUMBER VARCHAR(8) NOT NULL,
	SEQUENCE_NUMBER DECIMAL(4,0) NOT NULL);