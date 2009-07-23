CREATE TABLE AWARD_METHOD_OF_PAYMENT ( 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	METHOD_OF_PAYMENT_CODE VARCHAR2(3) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL);

CREATE TABLE AWARD_PERSON_CREDIT_SPLITS (
   AWARD_PERSON_CREDIT_SPLIT_ID number(22,0) NOT NULL,
   AWARD_PERSON_ID number(22,0) NOT NULL,
   INV_CREDIT_TYPE_CODE varchar2(3) NOT NULL,
   CREDIT decimal(5,2),
   UPDATE_TIMESTAMP date NOT NULL,
   UPDATE_USER varchar2(60) NOT NULL,
   VER_NBR decimal(8) NOT NULL,
   OBJ_ID varchar2(36) NOT NULL
);

CREATE TABLE AWARD_PERSON_UNITS (
   AWARD_PERSON_UNIT_ID number(22,0) NOT NULL,
   AWARD_PERSON_ID number(22,0) NOT NULL,
   UNIT_NUMBER VARCHAR2(8) NOT NULL,
   LEAD_UNIT_FLAG CHAR(1) NOT NULL,
   UPDATE_TIMESTAMP date NOT NULL,
   UPDATE_USER varchar2(60) NOT NULL,
   VER_NBR decimal(8) NOT NULL,
   OBJ_ID varchar2(36) NOT NULL
);

CREATE TABLE AWARD_PERS_UNIT_CRED_SPLITS (
   APU_CREDIT_SPLIT_ID number(22,0) NOT NULL,
   AWARD_PERSON_UNIT_ID number(22,0) NOT NULL,
   INV_CREDIT_TYPE_CODE varchar2(3) NOT NULL,
   CREDIT decimal(5,2),
   UPDATE_TIMESTAMP date NOT NULL,
   UPDATE_USER varchar2(60) NOT NULL,
   VER_NBR decimal(8) NOT NULL,
   OBJ_ID varchar2(36) NOT NULL
);

CREATE TABLE AWARD_REP_TERMS_RECNT (
    AWARD_REP_TERMS_RECNT_ID NUMBER(12,0) NOT NULL,
	AWARD_REPORT_TERMS_ID NUMBER(12,0) NOT NULL,	 
	CONTACT_TYPE_CODE VARCHAR2(3) NOT NULL, 
	CONTACT_ID NUMBER(12,0),
	ROLODEX_ID NUMBER(6,0) NOT NULL,
	NUMBER_OF_COPIES NUMBER(2,0),
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL);

CREATE TABLE AWARD_SCIENCE_KEYWORD ( 
	AWARD_SCIENCE_KEYWORD_ID NUMBER(12,0) NOT NULL, 
	AWARD_ID NUMBER(12,0) NOT NULL, 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	SCIENCE_KEYWORD_CODE VARCHAR2(15) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL);

CREATE TABLE AWARD_SPECIAL_REVIEW ( 
	AWARD_SPECIAL_REVIEW_ID NUMBER(12,0) NOT NULL, 
	AWARD_ID NUMBER(12,0) NOT NULL, 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	SPECIAL_REVIEW_NUMBER NUMBER(3,0) NOT NULL, 
	SPECIAL_REVIEW_CODE NUMBER(3,0) NOT NULL, 
	APPROVAL_TYPE_CODE NUMBER(3,0) NOT NULL, 
	PROTOCOL_NUMBER VARCHAR2(20), 
	APPLICATION_DATE DATE, 
	APPROVAL_DATE DATE,
	EXPIRATION_DATE DATE, 
	COMMENTS CLOB, 
	UPDATE_USER VARCHAR2(60) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL);

CREATE TABLE AWARD_STATUS ( 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	STATUS_CODE VARCHAR2(3) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL);

CREATE TABLE AWARD_TEMPLATE ( 
	AWARD_TEMPLATE_CODE NUMBER(5,0) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	STATUS_CODE VARCHAR2(3) NOT NULL, 
	PRIME_SPONSOR_CODE CHAR(6), 
	NON_COMPETING_CONT_PRPSL_DUE VARCHAR2(3), 
	COMPETING_RENEWAL_PRPSL_DUE VARCHAR2(3), 
	BASIS_OF_PAYMENT_CODE VARCHAR2(3) NOT NULL, 
	METHOD_OF_PAYMENT_CODE VARCHAR2(3) NOT NULL, 
	PAYMENT_INVOICE_FREQ_CODE VARCHAR2(3), 
	INVOICE_NUMBER_OF_COPIES NUMBER(3,0), 
	FINAL_INVOICE_DUE NUMBER(3,0), 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL,
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE AWARD_TEMPLATE_CONTACT ( 
	AWARD_TEMPLATE_CONTACT_ID NUMBER(12,0) NOT NULL, 
	AWARD_TEMPLATE_CODE NUMBER(5,0) NOT NULL, 
	CONTACT_TYPE_CODE VARCHAR2(3) NOT NULL, 
	ROLODEX_ID NUMBER(6,0) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL,
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE AWARD_TEMPLATE_COMMENTS ( 
	AWARD_TEMPLATE_COMMENTS_ID NUMBER(12,0) NOT NULL, 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	AWARD_TEMPLATE_CODE NUMBER(5,0) NOT NULL, 
	COMMENT_TYPE_CODE VARCHAR2(3) NOT NULL, 
	CHECKLIST_PRINT_FLAG CHAR(1) NOT NULL, 
	COMMENTS CLOB, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL);
	
CREATE TABLE AWARD_TEMPLATE_REPORT_TERMS ( 
	TEMPLATE_REPORT_TERMS_ID NUMBER(12,0) NOT NULL, 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	AWARD_TEMPLATE_CODE NUMBER(5,0) NOT NULL, 
	REPORT_CLASS_CODE VARCHAR2(3) NOT NULL, 
	REPORT_CODE VARCHAR2(3) NOT NULL, 
	FREQUENCY_CODE VARCHAR2(3) NOT NULL, 
	FREQUENCY_BASE_CODE VARCHAR2(3) NOT NULL, 
	OSP_DISTRIBUTION_CODE VARCHAR2(3) NOT NULL, 
	DUE_DATE DATE NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL);

CREATE TABLE AWARD_TEMPL_REP_TERMS_RECNT(	
	TEMPL_REP_TERMS_RECNT_ID NUMBER(12,0) NOT NULL ENABLE, 
	TEMPLATE_REPORT_TERMS_ID NUMBER(12,0) NOT NULL ENABLE, 
	CONTACT_TYPE_CODE VARCHAR2(3 BYTE) NOT NULL ENABLE, 
	ROLODEX_ID NUMBER(6,0) NOT NULL ENABLE, 
	NUMBER_OF_COPIES NUMBER(2,0), 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL ENABLE, 
	OBJ_ID VARCHAR2(36 BYTE) DEFAULT SYS_GUID() NOT NULL ENABLE, 
	UPDATE_TIMESTAMP DATE NOT NULL ENABLE, 
	UPDATE_USER VARCHAR2(60 BYTE) NOT NULL ENABLE
	);

CREATE TABLE AWARD_TEMPLATE_TERMS ( 
	AWARD_TEMPLATE_TERMS_ID NUMBER(12,0) NOT NULL, 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	AWARD_TEMPLATE_CODE NUMBER(5,0) NOT NULL, 
	SPONSOR_TERM_ID NUMBER(12,0) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL);

CREATE TABLE AWARD_TYPE ( 
	AWARD_TYPE_CODE NUMBER(3,0) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL, 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
	
CREATE TABLE CLOSEOUT_REPORT_TYPE (
	CLOSEOUT_REPORT_CODE VARCHAR2(3) NOT NULL,
	DESCRIPTION VARCHAR2(200) NOT NULL,
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE COEUS_MODULE ( 
    MODULE_CODE NUMBER(3,0) NOT NULL, 
    DESCRIPTION VARCHAR2(200) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE COEUS_SUB_MODULE ( 
    COEUS_SUB_MODULE_ID NUMBER(12,0) NOT NULL, 
    MODULE_CODE NUMBER(3,0) NOT NULL, 
    SUB_MODULE_CODE NUMBER(3,0) NOT NULL, 
    DESCRIPTION VARCHAR2(200) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE COMMENT_TYPE ( 
	COMMENT_TYPE_CODE			VARCHAR2(3),
	DESCRIPTION					VARCHAR2(200),
	TEMPLATE_FLAG				VARCHAR2(1),
	CHECKLIST_FLAG				VARCHAR2(1),
	AWARD_COMMENT_SCREEN_FLAG	VARCHAR2(1),
    UPDATE_TIMESTAMP       		DATE,
    UPDATE_USER            		VARCHAR2(60),
    VER_NBR 					NUMBER(8,0) DEFAULT 1,
	OBJ_ID 						VARCHAR2(36) DEFAULT SYS_GUID()
);

CREATE TABLE COMMITTEE ( 
    ID NUMBER(12,0) NOT NULL,
    SEQUENCE_NUMBER NUMBER(4,0) NOT NULL,
    DOCUMENT_NUMBER NUMBER(10) NOT NULL,
    COMMITTEE_ID VARCHAR2(15) NOT NULL,
    COMMITTEE_NAME VARCHAR2(60) NOT NULL,
    HOME_UNIT_NUMBER VARCHAR2(8) NOT NULL,
    DESCRIPTION VARCHAR2(2000),
    SCHEDULE_DESCRIPTION VARCHAR2(2000),
    COMMITTEE_TYPE_CODE VARCHAR2(3) NOT NULL,
    MINIMUM_MEMBERS_REQUIRED NUMBER(3,0),
    MAX_PROTOCOLS NUMBER(4,0),
    ADV_SUBMISSION_DAYS_REQ NUMBER(3,0),
    DEFAULT_REVIEW_TYPE_CODE VARCHAR2(3),
    APPLICABLE_REVIEW_TYPE_CODE VARCHAR2(3) NOT NULL,
    CREATE_TIMESTAMP DATE, 
    CREATE_USER VARCHAR2(8),
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE COMMITTEE_DOCUMENT ( 
    DOCUMENT_NUMBER NUMBER(10) NOT NULL,
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE COMMITTEE_TYPE ( 
    COMMITTEE_TYPE_CODE VARCHAR2(3) NOT NULL, 
    DESCRIPTION VARCHAR2(200) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE COMM_MEMBERSHIP_TYPE (
    MEMBERSHIP_TYPE_CODE VARCHAR2(3) NOT NULL,
    DESCRIPTION VARCHAR2(200) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

create table comm_schedule_frequency (
  frequency_code    number (3)    not null PRIMARY KEY,
  description       varchar2 (200)  not null,
  no_of_days        number (3),
  UPDATE_TIMESTAMP DATE NOT NULL, 
  UPDATE_USER VARCHAR2(60) NOT NULL,
  VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
  OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);  

CREATE TABLE CONTACT_TYPE ( 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	CONTACT_TYPE_CODE VARCHAR2(3) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL);

CREATE TABLE COST_SHARE_TYPE ( 
	COST_SHARE_TYPE_CODE	NUMBER(3,0),
	DESCRIPTION				VARCHAR2(200),
    UPDATE_TIMESTAMP       	DATE,
    UPDATE_USER            	VARCHAR2(60),
    VER_NBR 				NUMBER(8,0),
	OBJ_ID 					VARCHAR2(36) DEFAULT SYS_GUID()
);

CREATE TABLE DISTRIBUTION ( 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	OSP_DISTRIBUTION_CODE VARCHAR2(3) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL);

CREATE TABLE EXEMPT_STUDIES_CHECKLIST ( 
    EXEMPT_STUDIES_CHECKLIST_CODE VARCHAR2(4) NOT NULL, 
    DESCRIPTION VARCHAR2(2000) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
    
CREATE TABLE EXPEDITED_REVIEW_CHECKLIST ( 
    EXPEDITED_REV_CHKLST_CODE VARCHAR2(4) NOT NULL, 
    DESCRIPTION VARCHAR2(2000) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
    
CREATE TABLE FREQUENCY ( 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	FREQUENCY_CODE VARCHAR2(3) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	NUMBER_OF_DAYS NUMBER(3,0), 
	NUMBER_OF_MONTHS NUMBER(2,0), 
	REPEAT_FLAG CHAR(1) NOT NULL, 
	PROPOSAL_DUE_FLAG CHAR(1) NOT NULL, 
	INVOICE_FLAG CHAR(1) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL, 
	ADVANCE_NUMBER_OF_DAYS NUMBER(3,0), 
	ADVANCE_NUMBER_OF_MONTHS NUMBER(2,0));

CREATE TABLE FREQUENCY_BASE ( 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	FREQUENCY_BASE_CODE VARCHAR2(3) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL);

CREATE TABLE FUNDING_SOURCE_TYPE ( 
    FUNDING_SOURCE_TYPE_CODE NUMBER(3,0) NOT NULL, 
    DESCRIPTION VARCHAR2(200) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    FUNDING_SOURCE_TYPE_FLAG VARCHAR2(1), 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE GROUP_TYPES ( 
    GROUP_TYPE_CODE NUMBER(3,0) NOT NULL, 
    GROUP_NAME VARCHAR2(200) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE IDC_RATE_TYPE ( 	
	IDC_RATE_TYPE_CODE NUMBER(3,0) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL,
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE MEMBERSHIP_ROLE ( 
    MEMBERSHIP_ROLE_CODE VARCHAR2(3) NOT NULL,
    DESCRIPTION VARCHAR2(200) NOT NULL,
    UPDATE_TIMESTAMP DATE NOT NULL,
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL,
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE PERSON_TRAINING ( 
	PERSON_TRAINING_ID NUMBER(12,0) NOT NULL, 
	PERSON_ID VARCHAR2(9) NOT NULL, 
	TRAINING_NUMBER NUMBER(4,0) NOT NULL, 
	TRAINING_CODE NUMBER(4,0) NOT NULL, 
	DATE_REQUESTED DATE, 
	DATE_SUBMITTED DATE, 
	DATE_ACKNOWLEDGED DATE, 
	FOLLOWUP_DATE DATE, 
	SCORE VARCHAR2(9), 
	COMMENTS CLOB, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL, 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE PROTO_AMEND_RENEW_MODULES ( 
    PROTO_AMEND_RENEW_MODULES_ID NUMBER(12) NOT NULL, 
    PROTO_AMEND_RENEWAL_NUMBER VARCHAR2(20) NOT NULL, 
    PROTO_AMEND_RENEWAL_ID NUMBER(12) NOT NULL,
    PROTOCOL_NUMBER VARCHAR2(20) NOT NULL,
    PROTOCOL_MODULE_CODE VARCHAR2(5) NOT NULL,
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
    
CREATE TABLE PROTO_AMEND_RENEWAL ( 
    PROTO_AMEND_RENEWAL_ID NUMBER(12) NOT NULL,
    PROTO_AMEND_REN_NUMBER VARCHAR(20) NOT NULL,
    DATE_CREATED DATE NOT NULL,
    SUMMARY CLOB,
    PROTOCOL_ID NUMBER(12) NOT NULL,
    PROTOCOL_NUMBER VARCHAR2(20),
    SEQUENCE_NUMBER NUMBER(4),
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

create table proto_corresp_type (
  proto_corresp_type_code  varchar2 (3)    not null,
  description              varchar2 (200)  not null,
  module_id 			   varchar2(1) default 'Y' not null,
  update_timestamp         date          not null,
  update_user              varchar2 (8)  not null,
  ver_nbr 				  	 number(8,0) DEFAULT 1 NOT NULL, 
  obj_id				  	 varchar2(36) DEFAULT SYS_GUID() NOT NULL);
  
CREATE TABLE PROTOCOL ( 
	PROTOCOL_ID NUMBER(12,0) NOT NULL,
	DOCUMENT_NUMBER NUMBER(10) NOT NULL,
	PROTOCOL_NUMBER VARCHAR2(20) NOT NULL, 
	SEQUENCE_NUMBER NUMBER(4,0) NOT NULL, 
	PROTOCOL_TYPE_CODE VARCHAR2(3) NOT NULL, 
	PROTOCOL_STATUS_CODE VARCHAR2(3) NOT NULL, 
	TITLE VARCHAR2(2000) NOT NULL, 
	DESCRIPTION VARCHAR2(2000), 
	APPLICATION_DATE DATE, 
	APPROVAL_DATE DATE, 
	EXPIRATION_DATE DATE, 
	LAST_APPROVAL_DATE DATE, 
	FDA_APPLICATION_NUMBER VARCHAR2(15), 
	REFERENCE_NUMBER_1 VARCHAR2(50), 
	REFERENCE_NUMBER_2 VARCHAR2(50), 
	IS_BILLABLE VARCHAR2(1) DEFAULT 'n' NOT NULL, 
	SPECIAL_REVIEW_INDICATOR VARCHAR2(2) DEFAULT 'n0' NOT NULL, 
	VULNERABLE_SUBJECT_INDICATOR VARCHAR2(2) DEFAULT 'n0' NOT NULL, 
	KEY_STUDY_PERSON_INDICATOR VARCHAR2(2) DEFAULT 'n0' NOT NULL, 
	FUNDING_SOURCE_INDICATOR VARCHAR2(2) DEFAULT 'n0' NOT NULL, 
	CORRESPONDENT_INDICATOR VARCHAR2(2) DEFAULT 'n0' NOT NULL, 
	REFERENCE_INDICATOR VARCHAR2(2) DEFAULT 'n0' NOT NULL, 
	RELATED_PROJECTS_INDICATOR VARCHAR2(2) DEFAULT 'n0' NOT NULL, 
	CREATE_TIMESTAMP DATE, 
	CREATE_USER VARCHAR2(8), 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL,
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE PROTOCOL_ACTIONS (
    PROTOCOL_ACTION_ID NUMBER(12,0) NOT NULL,
    PROTOCOL_NUMBER    VARCHAR2(20) NOT NULL,
    SEQUENCE_NUMBER    NUMBER(4) NOT NULL,
    SUBMISSION_NUMBER  NUMBER(4),
    ACTION_ID          NUMBER(6) NOT NULL,
    PROTOCOL_ACTION_TYPE_CODE VARCHAR2(3) NOT NULL,
    PROTOCOL_ID        NUMBER(12,0) NOT NULL,
    SUBMISSION_ID_FK   NUMBER(12,0),
    COMMENTS           VARCHAR2 (2000),
    ACTION_DATE        DATE,
    UPDATE_TIMESTAMP   DATE,
    UPDATE_USER        VARCHAR2(60),
    VER_NBR            NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID             VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE PROTOCOL_ATTACHMENT_FILE
   (PA_FILE_ID NUMBER NOT NULL ENABLE, 
    FILE_NAME VARCHAR2(150) NOT NULL ENABLE,
    CONTENT_TYPE VARCHAR2(250) NOT NULL ENABLE,
    FILE_DATA BLOB NOT NULL ENABLE, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL,
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID()  NOT NULL,
    UPDATE_TIMESTAMP DATE NOT NULL,
    UPDATE_USER VARCHAR2(10) NOT NULL);

CREATE TABLE PROTOCOL_ATTACHMENT_GROUP
   (GROUP_CD VARCHAR2(3) NOT NULL ENABLE,
    DESCRIPTION VARCHAR2(300) NOT NULL ENABLE, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL ENABLE,
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID()  NOT NULL ENABLE,
    UPDATE_TIMESTAMP DATE NOT NULL ENABLE,
    UPDATE_USER VARCHAR2(10) NOT NULL ENABLE);

CREATE TABLE PROTOCOL_ATTACHMENT_STATUS
   (STATUS_CD VARCHAR2(3) NOT NULL ENABLE,
    DESCRIPTION VARCHAR2(300) NOT NULL ENABLE,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL ENABLE,
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL ENABLE,
    UPDATE_TIMESTAMP DATE NOT NULL ENABLE,
    UPDATE_USER VARCHAR2(10) NOT NULL ENABLE);

CREATE TABLE PROTOCOL_ATTACHMENT_TYPE
   (TYPE_CD VARCHAR2(3) NOT NULL ENABLE,
    DESCRIPTION VARCHAR2(300) NOT NULL ENABLE,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL ENABLE,
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL ENABLE,
    UPDATE_TIMESTAMP DATE NOT NULL ENABLE,
    UPDATE_USER VARCHAR2(10) NOT NULL ENABLE);

CREATE TABLE PROTOCOL_ATTACHMENT_TYPE_GROUP
   (TYPE_GROUP_ID NUMBER(12,0) NOT NULL ENABLE, 
    TYPE_CD VARCHAR2(3) NOT NULL ENABLE,
    GROUP_CD VARCHAR2(3) NOT NULL ENABLE,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL ENABLE,
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL ENABLE,
    UPDATE_TIMESTAMP DATE NOT NULL ENABLE,
    UPDATE_USER VARCHAR2(10) NOT NULL ENABLE);

CREATE TABLE PROTOCOL_CORRESPONDENCE (
    ID                       NUMBER(12,0) NOT NULL,
    PROTOCOL_NUMBER          VARCHAR2(20) NOT NULL,
    SEQUENCE_NUMBER          NUMBER(4) NOT NULL,
    ACTION_ID                NUMBER(6) NOT NULL,
    PROTOCOL_ID              NUMBER(12,0) NOT NULL,
    ACTION_ID_FK             NUMBER(12,0) NOT NULL,
    PROTO_CORRESP_TYPE_CODE  VARCHAR2(3) NOT NULL,
    CORRESPONDENCE           BLOB DEFAULT EMPTY_BLOB(),
    FINAL_FLAG               CHAR(1) NOT NULL,
    UPDATE_TIMESTAMP         DATE,
    UPDATE_USER              VARCHAR2(60),
    VER_NBR                  NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID                   VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE PROTOCOL_DOCUMENT(
DOCUMENT_NUMBER NUMBER(10) NOT NULL,
VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL,
OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID()  NOT NULL,
UPDATE_TIMESTAMP DATE NOT NULL,
UPDATE_USER VARCHAR2(10) NOT NULL);

CREATE TABLE PROTOCOL_EXEMPT_CHKLST ( 
    PROTOCOL_EXEMPT_CHKLST_ID NUMBER(12,0) NOT NULL,
    PROTOCOL_ID NUMBER(12,0) NOT NULL,
    SUBMISSION_ID_FK NUMBER(12,0) NOT NULL,
    PROTOCOL_NUMBER VARCHAR2(20) NOT NULL,
    SEQUENCE_NUMBER NUMBER(4,0) NOT NULL,
    SUBMISSION_NUMBER NUMBER(4,0) NOT NULL,
    EXEMPT_STUDIES_CHECKLIST_CODE VARCHAR2(3) NOT NULL,
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
    
CREATE TABLE PROTOCOL_EXEMPT_NUMBER (
    PROTOCOL_EXEMPT_NUMBER_ID  NUMBER(12,0) NOT NULL,
    PROTOCOL_SPECIAL_REVIEW_ID NUMBER(12,0) NOT NULL,
    EXEMPTION_TYPE_CODE VARCHAR2(3 BYTE) NOT NULL ENABLE,
    UPDATE_USER VARCHAR2(60 BYTE) NOT NULL ENABLE,
    UPDATE_TIMESTAMP DATE NOT NULL ENABLE,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL ENABLE,
    OBJ_ID VARCHAR2(36 BYTE) DEFAULT SYS_GUID() NOT NULL ENABLE);

CREATE TABLE PROTOCOL_EXPIDITED_CHKLST ( 
    PROTOCOL_EXPEDITED_CHKLST_ID NUMBER(12,0) NOT NULL,
    PROTOCOL_ID NUMBER(12,0) NOT NULL,
    SUBMISSION_ID_FK NUMBER(12,0) NOT NULL,
    PROTOCOL_NUMBER VARCHAR2(20) NOT NULL,
    SEQUENCE_NUMBER NUMBER(4,0) NOT NULL,
    SUBMISSION_NUMBER NUMBER(4,0) NOT NULL,
    EXPEDITED_REV_CHKLST_CODE VARCHAR2(3) NOT NULL,
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
    
CREATE TABLE PROTOCOL_FUNDING_SOURCE ( 
    PROTOCOL_FUNDING_SOURCE_ID NUMBER(12,0) NOT NULL, 
    PROTOCOL_ID NUMBER(12,0) NOT NULL, 
    PROTOCOL_NUMBER VARCHAR2(20)  NULL, 
    SEQUENCE_NUMBER NUMBER(4,0)  NULL, 
    FUNDING_SOURCE_TYPE_CODE NUMBER(3,0) NOT NULL, 
    FUNDING_SOURCE VARCHAR2(200) NOT NULL, 
    FUNDING_SOURCE_NAME VARCHAR2(200) NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE PROTOCOL_LOCATION ( 
	PROTOCOL_LOCATION_ID NUMBER(12,0) NOT NULL, 
	PROTOCOL_ID NUMBER(12,0) NOT NULL, 
	PROTOCOL_NUMBER VARCHAR2(20) NOT NULL, 
	SEQUENCE_NUMBER NUMBER(4,0) NOT NULL, 
	PROTOCOL_ORG_TYPE_CODE VARCHAR2(3) NOT NULL, 
	ORGANIZATION_ID VARCHAR2(8) NOT NULL, 
	ROLODEX_ID NUMBER(6,0), 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL, 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE PROTOCOL_MODULES ( 
    PROTOCOL_MODULE_CODE VARCHAR2(5) NOT NULL, 
    DESCRIPTION VARCHAR2(200) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
    
CREATE TABLE PROTOCOL_ORG_TYPE ( 
	PROTOCOL_ORG_TYPE_CODE VARCHAR2(3) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL, 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE PROTOCOL_PERSONS ( 
	PROTOCOL_PERSON_ID NUMBER(12,0) NOT NULL, 
	PROTOCOL_ID NUMBER(12,0) NOT NULL, 
	PROTOCOL_NUMBER VARCHAR2(20) NOT NULL, 
	SEQUENCE_NUMBER NUMBER(4,0) NOT NULL, 
	PERSON_ID VARCHAR2(9) NULL, 
	PERSON_NAME VARCHAR2(90) NOT NULL, 
	PROTOCOL_PERSON_ROLE_ID VARCHAR2(12), 
	ROLODEX_ID NUMBER(12,0) NULL, 
	AFFILIATION_TYPE_CODE NUMBER(3,0), 
	UPDATE_TIMESTAMP DATE, 
	UPDATE_USER VARCHAR2(60),
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE PROTOCOL_PERSON_ROLES(
  PROTOCOL_PERSON_ROLE_ID VARCHAR2(12) NOT NULL,
  DESCRIPTION       VARCHAR2(250) NOT NULL,
  UNIT_DETAILS_REQUIRED CHAR(1) DEFAULT 'N' NOT NULL,
  UPDATE_TIMESTAMP DATE, 
  UPDATE_USER VARCHAR2(60),
  AFFILIATION_DETAILS_REQUIRED CHAR(1) DEFAULT 'N' NOT NULL,
  TRAINING_DETAILS_REQUIRED CHAR(1) DEFAULT 'N' NOT NULL,
  VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL,
  OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE PROTOCOL_PERSON_ROLE_MAPPING(
  ROLE_MAPPING_ID NUMBER(12) NOT NULL,
  SOURCE_ROLE_ID VARCHAR2(12) NOT NULL,
  TARGET_ROLE_ID VARCHAR2(12) NOT NULL,
  UPDATE_TIMESTAMP DATE, 
  UPDATE_USER VARCHAR2(60),
  VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL,
  OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

create table protocol_reference_type (
  protocol_reference_type_code  number (3)    not null,
  description                   varchar2 (200)  not null,
  update_timestamp              date          not null,
  update_user                   varchar2 (60)  not null,
  ver_nbr 						number(8) default 1 not null,
  obj_id						varchar2 (36) default sys_guid() not null);
  
create table protocol_references (
  protocol_reference_id         number (4)	    not null,
  protocol_id 					number (12) 	not null,
  protocol_number               varchar2 (20)  	not null,
  sequence_number               number (4)    	not null,
  protocol_reference_number     number (4)    	not null,
  protocol_reference_type_code  number (3)    	not null,
  reference_key                 varchar2 (50)  	not null,
  application_date              date,
  approval_date                 date,
  comments                      long,
  update_timestamp              date          	not null,
  update_user                   varchar2 (60)  	not null,
  ver_nbr 						number(8) default 1 not null,
  obj_id						varchar2 (36) default sys_guid() not null);

CREATE TABLE PROTOCOL_RESEARCH_AREAS (
  ID NUMBER(12) NOT null,
  PROTOCOL_ID NUMBER(12,0) NOT NULL,
  PROTOCOL_NUMBER     varchar2 (20)  NOT null,
  SEQUENCE_NUMBER     number (4, 0)    NOT null,
  RESEARCH_AREA_CODE  varchar2 (8)  NOT null,
  UPDATE_TIMESTAMP DATE NOT NULL, 
  UPDATE_USER VARCHAR2(60) NOT NULL,
  VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
  OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE PROTOCOL_REVIEWER_TYPE ( 
    REVIEWER_TYPE_CODE VARCHAR2(3) NOT NULL, 
    DESCRIPTION VARCHAR2(200) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
    
CREATE TABLE PROTOCOL_REVIEWERS ( 
    PROTOCOL_REVIEWER_ID NUMBER(12,0) NOT NULL,
    PROTOCOL_ID NUMBER(12,0) NOT NULL,
    SUBMISSION_ID_FK NUMBER(12,0) NOT NULL,
    PROTOCOL_NUMBER VARCHAR2(20) NOT NULL,
    SEQUENCE_NUMBER NUMBER(4,0) NOT NULL,
    SUBMISSION_NUMBER NUMBER(4,0) NOT NULL,
    PERSON_ID VARCHAR2(60) NOT NULL,
    NON_EMPLOYEE_FLAG CHAR(1) NOT NULL,
    REVIEWER_TYPE_CODE VARCHAR2(3) NOT NULL,
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
    
CREATE TABLE PROTOCOL_REVIEW_TYPE ( 
    PROTOCOL_REVIEW_TYPE_CODE VARCHAR2(3) NOT NULL, 
    DESCRIPTION VARCHAR2(200) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE PROTOCOL_RISK_LEVELS ( 
	PROTOCOL_RISK_LEVELS_ID NUMBER(12,0) NOT NULL, 
	PROTOCOL_ID NUMBER(12,0) NOT NULL, 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	PROTOCOL_NUMBER VARCHAR2(20) NOT NULL, 
	SEQUENCE_NUMBER NUMBER(4,0) NOT NULL, 
	RISK_LEVEL_CODE VARCHAR2(3) NOT NULL, 
	COMMENTS VARCHAR2(2000), 
	DATE_ASSIGNED DATE NOT NULL, 
	DATE_UPDATED DATE, 
	STATUS CHAR(1) NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL);

CREATE TABLE PROTOCOL_SPECIAL_REVIEW (
    PROTOCOL_SPECIAL_REVIEW_ID NUMBER(12,0) NOT NULL,
    PROTOCOL_ID NUMBER(12,0) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL,
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL,
    SPECIAL_REVIEW_NUMBER NUMBER(3,0) NOT NULL,
    SPECIAL_REVIEW_CODE NUMBER(3,0) NOT NULL,
    APPROVAL_TYPE_CODE NUMBER(3,0) NOT NULL,
    PROTOCOL_NUMBER VARCHAR2(20),
    APPLICATION_DATE DATE,
    APPROVAL_DATE DATE,
    EXPIRATION_DATE DATE,
    COMMENTS CLOB,
    UPDATE_USER VARCHAR2(60) NOT NULL,
    UPDATE_TIMESTAMP DATE NOT NULL);

CREATE TABLE PROTOCOL_STATUS ( 
	PROTOCOL_STATUS_CODE VARCHAR2(3) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL,
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE PROTOCOL_SUBMISSION_DOC (
    SUBMISSION_DOC_ID          NUMBER(12) NOT NULL,
    PROTOCOL_NUMBER            VARCHAR2(20) NOT NULL,
    SEQUENCE_NUMBER            NUMBER(4) NOT NULL,
    SUBMISSION_NUMBER          NUMBER(4) NOT NULL,
    PROTOCOL_ID                NUMBER(12,0) NOT NULL,
    SUBMISSION_ID_FK           NUMBER(12,0) NOT NULL,
    DOCUMENT_ID                NUMBER(3) NOT NULL,
    FILE_NAME                  VARCHAR2(300),
    DOCUMENT                   BLOB,
    UPDATE_TIMESTAMP           DATE,
    UPDATE_USER                VARCHAR2(60),
    VER_NBR                    NUMBER(8,0) DEFAULT 1 NOT NULL,
    OBJ_ID                     VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE PROTOCOL_TYPE ( 
	PROTOCOL_TYPE_CODE VARCHAR2(3) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL,
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE PROTOCOL_UNITS ( 
	PROTOCOL_UNITS_ID NUMBER(12,0) NOT NULL, 
	PROTOCOL_PERSON_ID NUMBER(12,0) NOT NULL,
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	PROTOCOL_NUMBER VARCHAR2(20) NULL, 
	SEQUENCE_NUMBER NUMBER(4,0) NULL, 
	UNIT_NUMBER VARCHAR2(8) NOT NULL, 
	LEAD_UNIT_FLAG VARCHAR2(1) NOT NULL, 
	PERSON_ID VARCHAR2(9) NULL, 
	UPDATE_TIMESTAMP DATE, 
	UPDATE_USER VARCHAR2(60));

CREATE TABLE PROTOCOL_VULNERABLE_SUB (
    PROTOCOL_VULNERABLE_SUB_ID NUMBER(12,0) NOT NULL,
    PROTOCOL_ID NUMBER(12,0) NOT NULL,
    PROTOCOL_NUMBER VARCHAR2(20) NOT NULL,  
    SEQUENCE_NUMBER NUMBER(4,0) NOT NULL, 
    VULNERABLE_SUBJECT_TYPE_CODE VARCHAR2(3) NOT NULL, 
    SUBJECT_COUNT NUMBER(6,0), 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
    
CREATE TABLE QUESTION ( 
    QUESTION_ID NUMBER(6,0) NOT NULL, 
    QUESTION VARCHAR2(2000) NOT NULL, 
    MAX_ANSWERS NUMBER(2,0) NOT NULL,
    DISPLAYED_ANSWERS NUMBER(2,0) NOT NULL,
    VALID_ANSWER VARCHAR2(20) NOT NULL, 
    LOOKUP_NAME VARCHAR2(50), 
    QUESTION_TYPE_ID NUMBER(12,0) NOT NULL, 
    ANSWER_MAX_LENGTH NUMBER(4,0), 
    LOOKUP_GUI VARCHAR2(50), 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    GROUP_TYPE_CODE NUMBER(3,0) NOT NULL, 
    STATUS CHAR(1) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE QUESTIONNAIRE ( 
    QUESTIONNAIRE_ID NUMBER(6,0) NOT NULL, 
    NAME VARCHAR2(50) NOT NULL, 
    DESCRIPTION VARCHAR2(2000), 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    IS_FINAL VARCHAR2(1) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE QUESTIONNAIRE_ANSWER ( 
    QUESTIONNAIRE_ANSWER_ID NUMBER(12,0) NOT NULL, 
    QUESTIONNAIRE_AH_ID_FK NUMBER(12,0) NOT NULL, 
    QUESTION_ID_FK NUMBER(6,0) NOT NULL, 
    QUESTION_NUMBER NUMBER(6,0) NOT NULL, 
    ANSWER_NUMBER NUMBER(3,0) NOT NULL, 
    ANSWER VARCHAR2(2000), 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
 
CREATE TABLE QUESTIONNAIRE_ANSWER_HEADER ( 
    QUESTIONNAIRE_ANSWER_HEADER_ID NUMBER(12,0) NOT NULL,
    QUESTIONNAIRE_ID_FK NUMBER(12,0) NOT NULL, 
    MODULE_ITEM_CODE NUMBER(3,0) NOT NULL, 
    MODULE_ITEM_KEY VARCHAR2(20) NOT NULL, 
    MODULE_SUB_ITEM_CODE NUMBER(3,0) NOT NULL, 
    MODULE_SUB_ITEM_KEY VARCHAR2(20) NOT NULL, 
    QUESTIONNAIRE_COMPLETED_FLAG VARCHAR2(1) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE QUESTION_EXPLANATION ( 
    QUESTION_EXPLANATION_ID NUMBER(12,0) NOT NULL, 
    QUESTION_ID NUMBER(6,0) NOT NULL, 
    EXPLANATION_TYPE CHAR(1) NOT NULL, 
    EXPLANATION CLOB, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE QUESTIONNAIRE_QUESTIONS ( 
    QUESTIONNAIRE_QUESTIONS_ID NUMBER(12,0) NOT NULL, 
    QUESTIONNAIRE_ID NUMBER(6,0) NOT NULL, 
    QUESTION_ID NUMBER(6,0) NOT NULL, 
    QUESTION_NUMBER NUMBER(6,0) NOT NULL, 
    PARENT_QUESTION_NUMBER NUMBER(6,0) NOT NULL, 
    CONDITION_FLAG VARCHAR2(1) NOT NULL, 
    CONDITION VARCHAR2(50), 
    CONDITION_VALUE VARCHAR2(2000), 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    QUESTION_SEQ_NUMBER NUMBER(3,0), 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE QUESTIONNAIRE_USAGE ( 
    QUESTIONNAIRE_USAGE_ID NUMBER(12,0) NOT NULL, 
    MODULE_ITEM_CODE NUMBER(3,0) NOT NULL, 
    MODULE_SUB_ITEM_CODE NUMBER(3,0) NOT NULL, 
    QUESTIONNAIRE_ID NUMBER(6,0) NOT NULL, 
    RULE_ID NUMBER(6,0), 
    QUESTIONNAIRE_LABEL VARCHAR2(50), 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE QUESTION_TYPES ( 
    QUESTION_TYPE_ID NUMBER(12,0) NOT NULL,
    QUESTION_TYPE_NAME VARCHAR2(30) NOT NULL, 
    UPDATE_TIMESTAMP DATE, 
    UPDATE_USER VARCHAR2(60), 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE REPORT ( 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	REPORT_CODE VARCHAR2(3) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	FINAL_REPORT_FLAG CHAR(1) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL);

CREATE TABLE REPORT_CLASS ( 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	REPORT_CLASS_CODE VARCHAR2(3) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL, 
	GENERATE_REPORT_REQUIREMENTS VARCHAR2(1) DEFAULT 'N' NOT NULL);

CREATE TABLE "RESEARCH_AREAS" 
   (	"RESEARCH_AREA_CODE" VARCHAR2(8) constraint RESEARCH_AREASN1 NOT NULL ENABLE, 
	"PARENT_RESEARCH_AREA_CODE" VARCHAR2(8) constraint RESEARCH_AREASN2 NOT NULL ENABLE, 
	"HAS_CHILDREN_FLAG" VARCHAR2(1) constraint RESEARCH_AREASN3 NOT NULL ENABLE, 
	"DESCRIPTION" VARCHAR2(200) constraint RESEARCH_AREASN4 NOT NULL ENABLE, 
	"UPDATE_TIMESTAMP" DATE constraint RESEARCH_AREASN5 NOT NULL ENABLE, 
	"UPDATE_USER" VARCHAR2(8) constraint RESEARCH_AREASN6 NOT NULL ENABLE, 
	 "VER_NBR" NUMBER(8,0) DEFAULT 1  constraint  RESEARCH_AREASN7  NOT NULL ENABLE,
	"OBJ_ID" VARCHAR2(36) DEFAULT SYS_GUID()  constraint  RESEARCH_AREASN8  NOT NULL ENABLE,
	CONSTRAINT "PK_RESEARCH_AREAS_KRA" PRIMARY KEY ("RESEARCH_AREA_CODE") ENABLE
);

CREATE TABLE RISK_LEVEL ( 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	RISK_LEVEL_CODE VARCHAR2(3) NOT NULL, 
	DESCRIPTION VARCHAR2(200), 
	UPDATE_TIMESTAMP DATE, 
	UPDATE_USER VARCHAR2(60));

CREATE TABLE SPONSOR_TERM ( 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	SPONSOR_TERM_ID NUMBER(12,0) NOT NULL, 
	SPONSOR_TERM_CODE VARCHAR2(3) NOT NULL, 
	SPONSOR_TERM_TYPE_CODE VARCHAR2(3) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL);
	
CREATE TABLE SPONSOR_TERM_TYPE ( 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	SPONSOR_TERM_TYPE_CODE VARCHAR2(3) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL);
	
create table schedule_status (
  schedule_status_code  number (3)    not null,
  description           varchar2 (200)  not null,
  UPDATE_TIMESTAMP DATE NOT NULL, 
  UPDATE_USER VARCHAR2(60) NOT NULL,
  VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
  OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

create table submission_status (
  submission_status_code  varchar2 (3)    not null,
  description             varchar2 (200)  not null,
  update_timestamp        date          not null,
  update_user             varchar2 (8)  not null,
  ver_nbr 				  number(8,0) DEFAULT 1 NOT NULL, 
  obj_id				  varchar2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE SUBMISSION_TYPE ( 
    SUBMISSION_TYPE_CODE VARCHAR2(3) NOT NULL, 
    DESCRIPTION VARCHAR2(200) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
    
CREATE TABLE SUBMISSION_TYPE_QUALIFIER ( 
    SUBMISSION_TYPE_QUAL_CODE VARCHAR2(3) NOT NULL, 
    DESCRIPTION VARCHAR2(200) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL,
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);
    
CREATE TABLE TRAINING ( 
    TRAINING_CODE NUMBER(4,0) NOT NULL, 
    DESCRIPTION VARCHAR2(200) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE VALID_BASIS_METHOD_PMT ( 
    VALID_BASIS_METHOD_PMT_ID NUMBER(12,0) NOT NULL, 
    BASIS_OF_PAYMENT_CODE VARCHAR2(3) NOT NULL, 
    METHOD_OF_PAYMENT_CODE VARCHAR2(3) NOT NULL, 
    FREQUENCY_INDICATOR CHAR(1) NOT NULL, 
    INV_INSTRUCTIONS_INDICATOR CHAR(1) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    UPDATE_USER VARCHAR2(60) NOT NULL, 
    VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE VALID_CLASS_REPORT_FREQ ( 
	VALID_CLASS_REPORT_FREQ_ID NUMBER(12,0) NOT NULL, 
	REPORT_CLASS_CODE VARCHAR2(3) NOT NULL, 
	REPORT_CODE VARCHAR2(3) NOT NULL, 
	FREQUENCY_CODE VARCHAR2(3) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL, 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE VALID_FREQUENCY_BASE ( 
	VALID_FREQUENCY_BASE_ID NUMBER(12,0) NOT NULL, 
	FREQUENCY_CODE VARCHAR2(3) NOT NULL, 
	FREQUENCY_BASE_CODE VARCHAR2(3) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL, 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

CREATE TABLE VALID_RATES ( 
	VALID_RATES_ID NUMBER(12,0) NOT NULL, 
	ON_CAMPUS_RATE NUMBER(5,2) NOT NULL, 
	OFF_CAMPUS_RATE NUMBER(5,2) NOT NULL, 
	RATE_CLASS_TYPE VARCHAR2(1) NOT NULL, 
	ADJUSTMENT_KEY VARCHAR2(6) NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, 
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL);

CREATE TABLE VULNERABLE_SUBJECT_TYPE ( 
	VULNERABLE_SUBJECT_TYPE_CODE VARCHAR2(3) NOT NULL, 
	DESCRIPTION VARCHAR2(200) NOT NULL, 
	UPDATE_TIMESTAMP DATE NOT NULL, 
	UPDATE_USER VARCHAR2(60) NOT NULL,
	VER_NBR NUMBER(8,0) DEFAULT 1 NOT NULL, 
	OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL);

