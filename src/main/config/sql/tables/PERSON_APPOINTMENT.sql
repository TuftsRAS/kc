-
- $Id: PERSON_APPOINTMENT.sql,v 1.0 2010-01-26 19:16:50 dpace Exp $
-
CREATE TABLE PERSON_APPOINTMENT (
  APPOINTMENT_ID NUMBER NOT NULL PRIMARY KEY,
  PERSON_ID VARCHAR2(40) NOT NULL,
  UNIT_NUMBER VARCHAR2(8) NOT NULL,
  APPOINTMENT_START_DATE DATE,
  APPOINTMENT_END_DATE DATE,
  APPOINTMENT_TYPE_CODE VARCHAR2(3),
  JOB_TITLE VARCHAR2(50),
  PREFERED_JOB_TITLE VARCHAR2(51),
  JOB_CODE VARCHAR2(6) NOT NULL,
  SALARY NUMBER(12,2),
  update_timestamp  date         not null,
  update_user       varchar2 (60) not null,
  VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL,
  OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL
);
CREATE SEQUENCE SEQ_PERSON_APPOINTMENT INCREMENT BY 1 START WITH 1000 NOCYCLE;
ALTER TABLE PERSON_APPOINTMENT
  ADD ( CONSTRAINT PERSON_APPOINTMENT_C0
      UNIQUE (OBJ_ID) 
      NOT DEFERRABLE INITIALLY IMMEDIATE);