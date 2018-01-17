--
-- Kuali Coeus, a comprehensive research administration system for higher education.
--
-- Copyright 2005-2017 Kuali, Inc.
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
--
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
--


INSERT INTO QUESTION (QUESTION_REF_ID, QUESTION_ID, SEQUENCE_NUMBER, SEQUENCE_STATUS, QUESTION, STATUS, GROUP_TYPE_CODE, QUESTION_TYPE_ID, LOOKUP_CLASS, LOOKUP_RETURN, DISPLAYED_ANSWERS, MAX_ANSWERS, ANSWER_MAX_LENGTH, UPDATE_TIMESTAMP, UPDATE_USER, VER_NBR, OBJ_ID, DOCUMENT_NUMBER)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, -10134, 1, 'C', 'This type of submission is', 'A', 2, 6,
                                          'org.kuali.coeus.common.framework.custom.arg.ArgValueLookup', 'TypeOfSubmission', NULL, 1, 15, SYSDATE,
        'admin', 1, SYS_GUID(), NULL);


INSERT INTO QUESTIONNAIRE_QUESTIONS (QUESTIONNAIRE_QUESTIONS_ID, QUESTIONNAIRE_REF_ID_FK, QUESTION_REF_ID_FK, QUESTION_NUMBER, PARENT_QUESTION_NUMBER, QUESTION_SEQ_NUMBER, CONDITION_FLAG, CONDITION_TYPE, CONDITION_VALUE, UPDATE_USER, UPDATE_TIMESTAMP, OBJ_ID, VER_NBR, RULE_ID)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, (SELECT QUESTIONNAIRE_REF_ID
                                           FROM QUESTIONNAIRE
                                           WHERE NAME = 'GG S2S Forms' AND SEQUENCE_NUMBER = (SELECT max(SEQUENCE_NUMBER)
                                                                                              FROM QUESTIONNAIRE
                                                                                              WHERE NAME = 'GG S2S Forms')),
                                          (SELECT max(QUESTION_REF_ID)
                                           FROM QUESTION
                                           WHERE QUESTION_ID = -10134), 296, 0, 1, 'N', NULL, NULL, 'admin', SYSDATE, SYS_GUID(), 1, 'KC152');


INSERT INTO QUESTION (QUESTION_REF_ID, QUESTION_ID, SEQUENCE_NUMBER, SEQUENCE_STATUS, QUESTION, STATUS, GROUP_TYPE_CODE, QUESTION_TYPE_ID, LOOKUP_CLASS, LOOKUP_RETURN, DISPLAYED_ANSWERS, MAX_ANSWERS, ANSWER_MAX_LENGTH, UPDATE_TIMESTAMP, UPDATE_USER, VER_NBR, OBJ_ID, DOCUMENT_NUMBER)
VALUES
  (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, -10135, 1, 'C', 'Please specify', 'A', 2, 5, NULL, NULL, NULL, 1, 50, SYSDATE, 'admin', 1, SYS_GUID(), NULL);


INSERT INTO QUESTIONNAIRE_QUESTIONS (QUESTIONNAIRE_QUESTIONS_ID, QUESTIONNAIRE_REF_ID_FK, QUESTION_REF_ID_FK, QUESTION_NUMBER, PARENT_QUESTION_NUMBER, QUESTION_SEQ_NUMBER, CONDITION_FLAG, CONDITION_TYPE, CONDITION_VALUE, UPDATE_USER, UPDATE_TIMESTAMP, OBJ_ID, VER_NBR, RULE_ID)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, (SELECT QUESTIONNAIRE_REF_ID
                                           FROM QUESTIONNAIRE
                                           WHERE NAME = 'GG S2S Forms' AND SEQUENCE_NUMBER = (SELECT max(SEQUENCE_NUMBER)
                                                                                              FROM QUESTIONNAIRE
                                                                                              WHERE NAME = 'GG S2S Forms')),
                                          (SELECT max(QUESTION_REF_ID)
                                           FROM QUESTION
                                           WHERE QUESTION_ID = -10135), 297, 296, 1, 'Y', '4', 'Other', 'admin', SYSDATE, SYS_GUID(), 1, 'KC152');


INSERT INTO QUESTION (QUESTION_REF_ID, QUESTION_ID, SEQUENCE_NUMBER, SEQUENCE_STATUS, QUESTION, STATUS, GROUP_TYPE_CODE, QUESTION_TYPE_ID, LOOKUP_CLASS, LOOKUP_RETURN, DISPLAYED_ANSWERS, MAX_ANSWERS, ANSWER_MAX_LENGTH, UPDATE_TIMESTAMP, UPDATE_USER, VER_NBR, OBJ_ID, DOCUMENT_NUMBER)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, -10136, 1, 'C', 'Select an applicable frequency for this type of submission', 'A', 2, 6,
                                          'org.kuali.coeus.common.framework.custom.arg.ArgValueLookup', 'Frequency', NULL, 1, 10, SYSDATE, 'admin', 1,
        SYS_GUID(), NULL);


INSERT INTO QUESTIONNAIRE_QUESTIONS (QUESTIONNAIRE_QUESTIONS_ID, QUESTIONNAIRE_REF_ID_FK, QUESTION_REF_ID_FK, QUESTION_NUMBER, PARENT_QUESTION_NUMBER, QUESTION_SEQ_NUMBER, CONDITION_FLAG, CONDITION_TYPE, CONDITION_VALUE, UPDATE_USER, UPDATE_TIMESTAMP, OBJ_ID, VER_NBR, RULE_ID)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, (SELECT QUESTIONNAIRE_REF_ID
                                           FROM QUESTIONNAIRE
                                           WHERE NAME = 'GG S2S Forms' AND SEQUENCE_NUMBER = (SELECT max(SEQUENCE_NUMBER)
                                                                                              FROM QUESTIONNAIRE
                                                                                              WHERE NAME = 'GG S2S Forms')),
                                          (SELECT max(QUESTION_REF_ID)
                                           FROM QUESTION
                                           WHERE QUESTION_ID = -10136), 298, 296, 1, 'N', NULL, NULL, 'admin', SYSDATE, SYS_GUID(), 1, 'KC152');


INSERT INTO QUESTION (QUESTION_REF_ID, QUESTION_ID, SEQUENCE_NUMBER, SEQUENCE_STATUS, QUESTION, STATUS, GROUP_TYPE_CODE, QUESTION_TYPE_ID, LOOKUP_CLASS, LOOKUP_RETURN, DISPLAYED_ANSWERS, MAX_ANSWERS, ANSWER_MAX_LENGTH, UPDATE_TIMESTAMP, UPDATE_USER, VER_NBR, OBJ_ID, DOCUMENT_NUMBER)
VALUES
  (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, -10137, 1, 'C', 'Please specify', 'A', 2, 5, NULL, NULL, NULL, 1, 50, SYSDATE, 'admin', 1, SYS_GUID(), NULL);


INSERT INTO QUESTIONNAIRE_QUESTIONS (QUESTIONNAIRE_QUESTIONS_ID, QUESTIONNAIRE_REF_ID_FK, QUESTION_REF_ID_FK, QUESTION_NUMBER, PARENT_QUESTION_NUMBER, QUESTION_SEQ_NUMBER, CONDITION_FLAG, CONDITION_TYPE, CONDITION_VALUE, UPDATE_USER, UPDATE_TIMESTAMP, OBJ_ID, VER_NBR, RULE_ID)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, (SELECT QUESTIONNAIRE_REF_ID
                                           FROM QUESTIONNAIRE
                                           WHERE NAME = 'GG S2S Forms' AND SEQUENCE_NUMBER = (SELECT max(SEQUENCE_NUMBER)
                                                                                              FROM QUESTIONNAIRE
                                                                                              WHERE NAME = 'GG S2S Forms')),
                                          (SELECT max(QUESTION_REF_ID)
                                           FROM QUESTION
                                           WHERE QUESTION_ID = -10137), 299, 298, 1, 'Y', '4', 'Other', 'admin', SYSDATE, SYS_GUID(), 1, 'KC152');


INSERT INTO QUESTION (QUESTION_REF_ID, QUESTION_ID, SEQUENCE_NUMBER, SEQUENCE_STATUS, QUESTION, STATUS, GROUP_TYPE_CODE, QUESTION_TYPE_ID, LOOKUP_CLASS, LOOKUP_RETURN, DISPLAYED_ANSWERS, MAX_ANSWERS, ANSWER_MAX_LENGTH, UPDATE_TIMESTAMP, UPDATE_USER, VER_NBR, OBJ_ID, DOCUMENT_NUMBER)
VALUES
  (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, -10138, 1, 'C', 'Is this submission a consolidated application/plan/funding request?', 'A', 2, 1, NULL, NULL,
                                     NULL, 1, 1, SYSDATE, 'admin', 1, SYS_GUID(), NULL);


INSERT INTO QUESTIONNAIRE_QUESTIONS (QUESTIONNAIRE_QUESTIONS_ID, QUESTIONNAIRE_REF_ID_FK, QUESTION_REF_ID_FK, QUESTION_NUMBER, PARENT_QUESTION_NUMBER, QUESTION_SEQ_NUMBER, CONDITION_FLAG, CONDITION_TYPE, CONDITION_VALUE, UPDATE_USER, UPDATE_TIMESTAMP, OBJ_ID, VER_NBR, RULE_ID)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, (SELECT QUESTIONNAIRE_REF_ID
                                           FROM QUESTIONNAIRE
                                           WHERE NAME = 'GG S2S Forms' AND SEQUENCE_NUMBER = (SELECT max(SEQUENCE_NUMBER)
                                                                                              FROM QUESTIONNAIRE
                                                                                              WHERE NAME = 'GG S2S Forms')),
                                          (SELECT max(QUESTION_REF_ID)
                                           FROM QUESTION
                                           WHERE QUESTION_ID = -10138), 300, 298, 1, 'N', NULL, NULL, 'admin', SYSDATE, SYS_GUID(), 1, 'KC152');


INSERT INTO QUESTION (QUESTION_REF_ID, QUESTION_ID, SEQUENCE_NUMBER, SEQUENCE_STATUS, QUESTION, STATUS, GROUP_TYPE_CODE, QUESTION_TYPE_ID, LOOKUP_CLASS, LOOKUP_RETURN, DISPLAYED_ANSWERS, MAX_ANSWERS, ANSWER_MAX_LENGTH, UPDATE_TIMESTAMP, UPDATE_USER, VER_NBR, OBJ_ID, DOCUMENT_NUMBER)
VALUES
  (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, -10139, 1, 'C', 'Provide a consolidated application/plan/funding request explanation', 'A', 2, 5, NULL, NULL,
                                     NULL, 1, 4000, SYSDATE, 'admin', 1, SYS_GUID(), NULL);


INSERT INTO QUESTIONNAIRE_QUESTIONS (QUESTIONNAIRE_QUESTIONS_ID, QUESTIONNAIRE_REF_ID_FK, QUESTION_REF_ID_FK, QUESTION_NUMBER, PARENT_QUESTION_NUMBER, QUESTION_SEQ_NUMBER, CONDITION_FLAG, CONDITION_TYPE, CONDITION_VALUE, UPDATE_USER, UPDATE_TIMESTAMP, OBJ_ID, VER_NBR, RULE_ID)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, (SELECT QUESTIONNAIRE_REF_ID
                                           FROM QUESTIONNAIRE
                                           WHERE NAME = 'GG S2S Forms' AND SEQUENCE_NUMBER = (SELECT max(SEQUENCE_NUMBER)
                                                                                              FROM QUESTIONNAIRE
                                                                                              WHERE NAME = 'GG S2S Forms')),
                                          (SELECT max(QUESTION_REF_ID)
                                           FROM QUESTION
                                           WHERE QUESTION_ID = -10139), 301, 300, 1, 'Y', '4', 'Y', 'admin', SYSDATE, SYS_GUID(), 1, 'KC152');


INSERT INTO QUESTION (QUESTION_REF_ID, QUESTION_ID, SEQUENCE_NUMBER, SEQUENCE_STATUS, QUESTION, STATUS, GROUP_TYPE_CODE, QUESTION_TYPE_ID, LOOKUP_CLASS, LOOKUP_RETURN, DISPLAYED_ANSWERS, MAX_ANSWERS, ANSWER_MAX_LENGTH, UPDATE_TIMESTAMP, UPDATE_USER, VER_NBR, OBJ_ID, DOCUMENT_NUMBER)
VALUES
  (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, -10140, 1, 'C', 'Does the funding agency request a list of areas affected by funding?', 'A', 2, 1, NULL, NULL,
                                     NULL, 1, 1, SYSDATE, 'admin', 1, SYS_GUID(), NULL);


INSERT INTO QUESTIONNAIRE_QUESTIONS (QUESTIONNAIRE_QUESTIONS_ID, QUESTIONNAIRE_REF_ID_FK, QUESTION_REF_ID_FK, QUESTION_NUMBER, PARENT_QUESTION_NUMBER, QUESTION_SEQ_NUMBER, CONDITION_FLAG, CONDITION_TYPE, CONDITION_VALUE, UPDATE_USER, UPDATE_TIMESTAMP, OBJ_ID, VER_NBR, RULE_ID)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, (SELECT QUESTIONNAIRE_REF_ID
                                           FROM QUESTIONNAIRE
                                           WHERE NAME = 'GG S2S Forms' AND SEQUENCE_NUMBER = (SELECT max(SEQUENCE_NUMBER)
                                                                                              FROM QUESTIONNAIRE
                                                                                              WHERE NAME = 'GG S2S Forms')),
                                          (SELECT max(QUESTION_REF_ID)
                                           FROM QUESTION
                                           WHERE QUESTION_ID = -10140), 302, 300, 1, 'N', NULL, NULL, 'admin', SYSDATE, SYS_GUID(), 1, 'KC152');


INSERT INTO QUESTION (QUESTION_REF_ID, QUESTION_ID, SEQUENCE_NUMBER, SEQUENCE_STATUS, QUESTION, STATUS, GROUP_TYPE_CODE, QUESTION_TYPE_ID, LOOKUP_CLASS, LOOKUP_RETURN, DISPLAYED_ANSWERS, MAX_ANSWERS, ANSWER_MAX_LENGTH, UPDATE_TIMESTAMP, UPDATE_USER, VER_NBR, OBJ_ID, DOCUMENT_NUMBER)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, -10141, 1, 'C',
                                          'Provide a list of areas affected, using the categories specified in the opportunity instructions', 'A', 2,
                                          5, NULL, NULL, NULL, 1, 250, SYSDATE, 'admin', 1, SYS_GUID(), NULL);


INSERT INTO QUESTIONNAIRE_QUESTIONS (QUESTIONNAIRE_QUESTIONS_ID, QUESTIONNAIRE_REF_ID_FK, QUESTION_REF_ID_FK, QUESTION_NUMBER, PARENT_QUESTION_NUMBER, QUESTION_SEQ_NUMBER, CONDITION_FLAG, CONDITION_TYPE, CONDITION_VALUE, UPDATE_USER, UPDATE_TIMESTAMP, OBJ_ID, VER_NBR, RULE_ID)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, (SELECT QUESTIONNAIRE_REF_ID
                                           FROM QUESTIONNAIRE
                                           WHERE NAME = 'GG S2S Forms' AND SEQUENCE_NUMBER = (SELECT max(SEQUENCE_NUMBER)
                                                                                              FROM QUESTIONNAIRE
                                                                                              WHERE NAME = 'GG S2S Forms')),
                                          (SELECT max(QUESTION_REF_ID)
                                           FROM QUESTION
                                           WHERE QUESTION_ID = -10141), 303, 302, 1, 'Y', '4', 'Y', 'admin', SYSDATE, SYS_GUID(), 1, 'KC152');


INSERT INTO QUESTIONNAIRE_QUESTIONS (QUESTIONNAIRE_QUESTIONS_ID, QUESTIONNAIRE_REF_ID_FK, QUESTION_REF_ID_FK, QUESTION_NUMBER, PARENT_QUESTION_NUMBER, QUESTION_SEQ_NUMBER, CONDITION_FLAG, CONDITION_TYPE, CONDITION_VALUE, UPDATE_USER, UPDATE_TIMESTAMP, OBJ_ID, VER_NBR, RULE_ID)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, (SELECT QUESTIONNAIRE_REF_ID
                                           FROM QUESTIONNAIRE
                                           WHERE NAME = 'GG S2S Forms' AND SEQUENCE_NUMBER = (SELECT max(SEQUENCE_NUMBER)
                                                                                              FROM QUESTIONNAIRE
                                                                                              WHERE NAME = 'GG S2S Forms')),
                                          (SELECT max(QUESTION_REF_ID)
                                           FROM QUESTION
                                           WHERE QUESTION = 'Is the proposal subject to review by state executive order 12372 process?' AND
                                                 SEQUENCE_NUMBER = 1), 304, 302, 1, 'N', NULL, NULL, 'admin', SYSDATE, SYS_GUID(), 1, 'KC152');


INSERT INTO QUESTIONNAIRE_QUESTIONS (QUESTIONNAIRE_QUESTIONS_ID, QUESTIONNAIRE_REF_ID_FK, QUESTION_REF_ID_FK, QUESTION_NUMBER, PARENT_QUESTION_NUMBER, QUESTION_SEQ_NUMBER, CONDITION_FLAG, CONDITION_TYPE, CONDITION_VALUE, UPDATE_USER, UPDATE_TIMESTAMP, OBJ_ID, VER_NBR, RULE_ID)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, (SELECT QUESTIONNAIRE_REF_ID
                                           FROM QUESTIONNAIRE
                                           WHERE NAME = 'GG S2S Forms' AND SEQUENCE_NUMBER = (SELECT max(SEQUENCE_NUMBER)
                                                                                              FROM QUESTIONNAIRE
                                                                                              WHERE NAME = 'GG S2S Forms')),
                                          (SELECT max(QUESTION_REF_ID)
                                           FROM QUESTION
                                           WHERE QUESTION =
                                                 'If Yes: Please provide the date the application was made available for review (submitted to the state). Enter in MM/DD/YYYY format.'
                                                 AND SEQUENCE_NUMBER = 1), 305, 304, 1, 'Y', '4', 'Y', 'admin', SYSDATE, SYS_GUID(), 1, 'KC152');


INSERT INTO QUESTIONNAIRE_QUESTIONS (QUESTIONNAIRE_QUESTIONS_ID, QUESTIONNAIRE_REF_ID_FK, QUESTION_REF_ID_FK, QUESTION_NUMBER, PARENT_QUESTION_NUMBER, QUESTION_SEQ_NUMBER, CONDITION_FLAG, CONDITION_TYPE, CONDITION_VALUE, UPDATE_USER, UPDATE_TIMESTAMP, OBJ_ID, VER_NBR, RULE_ID)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.NEXTVAL, (SELECT QUESTIONNAIRE_REF_ID
                                           FROM QUESTIONNAIRE
                                           WHERE NAME = 'GG S2S Forms' AND SEQUENCE_NUMBER = (SELECT max(SEQUENCE_NUMBER)
                                                                                              FROM QUESTIONNAIRE
                                                                                              WHERE NAME = 'GG S2S Forms')),
                                          (SELECT max(QUESTION_REF_ID)
                                           FROM QUESTION
                                           WHERE QUESTION =
                                                 'If No: Is the program not selected for review or not covered by E.O. 12372? Select a response.' AND
                                                 SEQUENCE_NUMBER = 1), 306, 304, 1, 'Y', '4', 'N', 'admin', SYSDATE, SYS_GUID(), 1, 'KC152');