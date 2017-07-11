--
-- Kuali Coeus, a comprehensive research administration system for higher education.
--
-- Copyright 2005-2016 Kuali, Inc.
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


INSERT INTO VALID_NARR_FORMS (VALID_NARR_FORMS_ID,FORM_NAME,NARRATIVE_TYPE_CODE,MANDATORY,UPDATE_USER,UPDATE_TIMESTAMP, OBJ_ID)
 VALUES (SEQ_VALID_NARR_FORMS_ID.nextval,'ED_SF424_Supplement_1_3-V1.3',(SELECT NARRATIVE_TYPE_CODE FROM NARRATIVE_TYPE WHERE DESCRIPTION = 'ED_SF424_Supplement_Attachment'),'N','admin',SYSDATE, SYS_GUID());

Insert into QUESTIONNAIRE (QUESTIONNAIRE_REF_ID,QUESTIONNAIRE_ID,SEQUENCE_NUMBER,NAME,DESCRIPTION,UPDATE_TIMESTAMP,UPDATE_USER,IS_FINAL,VER_NBR,OBJ_ID,FILE_NAME) values
(SEQ_QUESTIONNAIRE_REF_ID.nextval,SEQ_QUESTIONNAIRE_ID.nextval,1,'EDSF424 Supplement 1.3 Questions','These questions support EDSF424 Supplement 1.3 Grants.gov forms.',SYSDATE, 'admin','Y',1,SYS_GUID(),null);

INSERT INTO QUESTIONNAIRE_QUESTIONS (QUESTIONNAIRE_QUESTIONS_ID,QUESTIONNAIRE_REF_ID_FK,QUESTION_REF_ID_FK,QUESTION_NUMBER,PARENT_QUESTION_NUMBER,QUESTION_SEQ_NUMBER,CONDITION_FLAG,CONDITION_TYPE,CONDITION_VALUE,UPDATE_USER,UPDATE_TIMESTAMP,OBJ_ID,VER_NBR)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.nextval,(SELECT QUESTIONNAIRE_REF_ID FROM QUESTIONNAIRE WHERE NAME = 'EDSF424 Supplement 1.3 Questions' AND SEQUENCE_NUMBER = 1),
(SELECT max(QUESTION_REF_ID) FROM QUESTION WHERE QUESTION = 'Is assistance being requested under a program that gives special consideration to novice applicants? Yes, No, or Not Applicable?'
AND SEQUENCE_NUMBER = 1),1,0,1,'N',null,null,'admin',SYSDATE,SYS_GUID(),1);

INSERT INTO QUESTIONNAIRE_USAGE (QUESTIONNAIRE_USAGE_ID,MODULE_ITEM_CODE,MODULE_SUB_ITEM_CODE,QUESTIONNAIRE_REF_ID_FK,QUESTIONNAIRE_SEQUENCE_NUMBER,RULE_ID,QUESTIONNAIRE_LABEL,UPDATE_TIMESTAMP,UPDATE_USER,VER_NBR,OBJ_ID,IS_MANDATORY)
values (SEQ_QUESTIONNAIRE_REF_ID.nextval ,3,(SELECT SUB_MODULE_CODE FROM COEUS_SUB_MODULE WHERE DESCRIPTION = 'S2S Questionnaires'),
(SELECT QUESTIONNAIRE_REF_ID FROM QUESTIONNAIRE WHERE NAME = 'EDSF424 Supplement 1.3 Questions' AND SEQUENCE_NUMBER = 1),
1,null,'EDSF424 Supplement 1.3 Questions',SYSDATE,'admin',1,SYS_GUID(),'N');

insert into S2S_FORM_TO_QUESTIONNAIRE
(S2S_FORM_TO_QUESTIONNAIRE_ID, OPP_NAME_SPACE, FORM_NAME, QUESTIONNAIRE_ID, UPDATE_TIMESTAMP, UPDATE_USER, OBJ_ID, VER_NBR)
VALUES (SEQ_QUESTIONNAIRE_REF_ID.nextval, 'http://apply.grants.gov/forms/ED_SF424_Supplement_1_3-V1.3',
'ED_SF424_Supplement_1_3-V1.3', (select QUESTIONNAIRE_ID from QUESTIONNAIRE where name='EDSF424 Supplement 1.3 Questions'), SYSDATE, 'admin', SYS_GUID(), '1');