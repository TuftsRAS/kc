--
-- Kuali Coeus, a comprehensive research administration system for higher education.
-- 
-- Copyright 2005-2015 Kuali, Inc.
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

INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-SUBAWARD','Document','FDP_NASA_Policy', UUID(),1,'CONFG','https://www.nssc.nasa.gov/grants','The value entered here will populate the FDP form Attachment 2 for NASA for the General Terms and Conditions 1','A','KC');

INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-SUBAWARD','Document','FDP_NASA_Grants_Policy_Statement', UUID(),1,'CONFG','https://prod.nais.nasa.gov/pub/pub_library/srba/index.html','The value entered here will populate the FDP form Attachment 2 for NASA for the General Terms and Conditions 3','A','KC');

INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-SUBAWARD','Document','FDP_NASA_Interim_Research_Terms', UUID(),1,'CONFG','https://www.nsf.gov/awards/managing/rtc.jsp','The value entered here will populate the FDP form Attachment 2 for NASA for the General Terms and Conditions 4 part 1','A','KC');

INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-SUBAWARD','Document','FDP_NASA_Agency_Requirements', UUID(),1,'CONFG','http://www.nsf.gov/pubs/policydocs/rtc/nasa_708.pdf','The value entered here will populate the FDP form Attachment 2 for NASA for the General Terms and Conditions 4 part 2','A','KC');

INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-SUBAWARD','Document','FDP_NASA_FCOI_Guidance', UUID(),1,'CONFG','NASA - If Applicable','The value entered here will populate the FDP form Attachment 2 for NASA for the Promoting Objectivity in Research','A','KC');