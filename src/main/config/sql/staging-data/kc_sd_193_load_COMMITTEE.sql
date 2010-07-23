REM INSERTING INTO COMMITTEE
INSERT INTO COMMITTEE (ID, DOCUMENT_NUMBER, COMMITTEE_ID, COMMITTEE_NAME, HOME_UNIT_NUMBER, DESCRIPTION, SCHEDULE_DESCRIPTION, COMMITTEE_TYPE_CODE, MINIMUM_MEMBERS_REQUIRED,
    MAX_PROTOCOLS, ADV_SUBMISSION_DAYS_REQ, DEFAULT_REVIEW_TYPE_CODE, APPLICABLE_REVIEW_TYPE_CODE, CREATE_TIMESTAMP, CREATE_USER, UPDATE_TIMESTAMP, UPDATE_USER
    , VER_NBR, SEQUENCE_NUMBER, OBJ_ID)
VALUES (1, 3094, 'KC001', 'KC IRB 1', '000001', 'Test IRB Committee for Kuali Coeus', 'Meets monthly', '1', 2, 
    22, 2, NULL, '1', '19-JUL-10', 'KCDBA', '19-JUL-10', 'KCDBA', 
    0, 1, SYS_GUID()) ;
