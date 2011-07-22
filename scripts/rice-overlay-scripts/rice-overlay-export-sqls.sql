-- EXPORT SQLS
SELECT * FROM KRNS_NMSPC_T WHERE NMSPC_CD LIKE 'KC%';
SELECT * FROM KRNS_PARM_DTL_TYP_T WHERE NMSPC_CD LIKE 'KC%';
SELECT * FROM KRNS_PARM_T WHERE NMSPC_CD LIKE 'KC%';

SELECT PHONE_TYP_CD, PHONE_TYP_NM, ACTV_IND, DISPLAY_SORT_CD, OBJ_ID, VER_NBR FROM KRIM_PHONE_TYP_T WHERE PHONE_TYP_CD = 'FAX';
SELECT ENTITY_ID, ACTV_IND, OBJ_ID, VER_NBR FROM KRIM_ENTITY_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;
SELECT ENTITY_ID, ENT_TYP_CD, ACTV_IND, OBJ_ID, VER_NBR FROM KRIM_ENTITY_ENT_TYP_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;
SELECT ENTITY_NM_ID, ENTITY_ID, NM_TYP_CD, FIRST_NM, MIDDLE_NM, LAST_NM, SUFFIX_NM, TITLE_NM, DFLT_IND, ACTV_IND, OBJ_ID, VER_NBR FROM KRIM_ENTITY_NM_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;
SELECT ENTITY_ADDR_ID, ENTITY_ID, ENT_TYP_CD, ADDR_TYP_CD, ADDR_LINE_1, ADDR_LINE_2, ADDR_LINE_3, CITY_NM, POSTAL_STATE_CD, POSTAL_CD, POSTAL_CNTRY_CD, DFLT_IND, ACTV_IND, OBJ_ID, VER_NBR FROM KRIM_ENTITY_ADDR_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;
SELECT ENTITY_EMP_ID, ENTITY_ID, DECODE(ENTITY_AFLTN_ID, NULL, 'NULL', '', 'NULL', ENTITY_AFLTN_ID) AS ENTITY_AFLTN_ID, DECODE(EMP_STAT_CD, NULL, 'NULL', '', 'NULL', EMP_STAT_CD) AS EMP_STAT_CD, DECODE(EMP_TYP_CD, NULL, 'NULL', '', 'NULL', EMP_TYP_CD) AS EMP_TYP_CD, PRMRY_IND, ACTV_IND, PRMRY_DEPT_CD, EMP_ID, EMP_REC_ID, OBJ_ID, VER_NBR FROM KRIM_ENTITY_EMP_INFO_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;
SELECT ENTITY_EMAIL_ID, ENTITY_ID, ENT_TYP_CD, EMAIL_TYP_CD, EMAIL_ADDR, DFLT_IND, ACTV_IND, OBJ_ID, VER_NBR FROM KRIM_ENTITY_EMAIL_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;
SELECT ENTITY_PHONE_ID, ENTITY_ID, ENT_TYP_CD, PHONE_TYP_CD, PHONE_NBR, PHONE_EXTN_NBR, POSTAL_CNTRY_CD, DFLT_IND, ACTV_IND, OBJ_ID, VER_NBR FROM KRIM_ENTITY_PHONE_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;
SELECT PRNCPL_ID, DECODE(PRNCPL_NM, 'kc', PRNCPL_NM, 'kc'||PRNCPL_NM) AS PRNCPL_NM, ENTITY_ID, PRNCPL_PSWD, ACTV_IND, OBJ_ID, VER_NBR FROM KRIM_PRNCPL_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;

SELECT * FROM KRIM_ATTR_DEFN_T WHERE NMSPC_CD LIKE 'KC%';
SELECT * FROM KRIM_TYP_T WHERE NMSPC_CD LIKE 'KC%';
SELECT * FROM KRIM_TYP_ATTR_T WHERE KIM_TYP_ID IN (SELECT KIM_TYP_ID FROM KRIM_TYP_T WHERE NMSPC_CD LIKE 'KC%');
SELECT * FROM KRIM_PERM_TMPL_T WHERE NMSPC_CD LIKE 'KC%';
SELECT * FROM KRIM_PERM_T  WHERE NMSPC_CD LIKE 'KC%';
SELECT * FROM KRIM_PERM_ATTR_DATA_T WHERE PERM_ID IN (SELECT PERM_ID FROM KRIM_PERM_T WHERE NMSPC_CD LIKE 'KC%');
SELECT ROLE_ID, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND, OBJ_ID, VER_NBR FROM KRIM_ROLE_T WHERE NMSPC_CD LIKE 'KC%';
SELECT ROLE_MBR_ID, ROLE_ID, MBR_ID, MBR_TYP_CD, OBJ_ID, VER_NBR FROM KRIM_ROLE_MBR_T  WHERE ROLE_ID IN (SELECT ROLE_ID FROM KRIM_ROLE_T WHERE NMSPC_CD LIKE 'KC%');
SELECT * FROM KRIM_ROLE_MBR_ATTR_DATA_T WHERE ROLE_MBR_ID IN (SELECT DISTINCT ROLE_MBR_ID FROM KRIM_ROLE_MBR_T WHERE ROLE_ID IN (SELECT ROLE_ID FROM KRIM_ROLE_T WHERE NMSPC_CD LIKE 'KC%'));
SELECT * FROM KRIM_ROLE_PERM_T WHERE ROLE_ID IN (SELECT ROLE_ID FROM KRIM_ROLE_T WHERE NMSPC_CD LIKE 'KC%');
SELECT GRP_ID, GRP_NM, NMSPC_CD, GRP_DESC, KIM_TYP_ID, ACTV_IND, OBJ_ID, VER_NBR FROM KRIM_GRP_T WHERE NMSPC_CD LIKE 'KC%';
SELECT GRP_MBR_ID, GRP_ID, MBR_ID, MBR_TYP_CD, OBJ_ID, VER_NBR FROM KRIM_GRP_MBR_T WHERE GRP_ID IN (SELECT GRP_ID FROM KRIM_GRP_T WHERE NMSPC_CD LIKE 'KC%');
SELECT * FROM KRIM_RSP_T WHERE NMSPC_CD LIKE 'KC%';
SELECT * FROM KRIM_ROLE_RSP_T WHERE (ROLE_ID IN (SELECT ROLE_ID FROM KRIM_ROLE_T WHERE NMSPC_CD LIKE 'KC%')) OR (RSP_ID IN (SELECT RSP_ID FROM KRIM_RSP_T WHERE NMSPC_CD LIKE 'KC%'));
SELECT * FROM KRIM_RSP_ATTR_DATA_T WHERE RSP_ID IN (SELECT RSP_ID FROM KRIM_RSP_T WHERE NMSPC_CD LIKE 'KC%');
SELECT * FROM KRIM_ROLE_RSP_ACTN_T WHERE ROLE_RSP_ID IN (SELECT ROLE_RSP_ID FROM KRIM_ROLE_RSP_T WHERE RSP_ID IN (SELECT RSP_ID FROM KRIM_RSP_T WHERE NMSPC_CD LIKE 'KC%'));
SELECT * FROM KREN_CHNL_T WHERE NM LIKE 'KC%';
SELECT * FROM KREN_CHNL_PRODCR_T WHERE CHNL_ID IN (SELECT CHNL_ID FROM KREN_CHNL_T WHERE NM LIKE 'KC%');

-- CLEANUP SQLS
DELETE FROM KRNS_PARM_T WHERE NMSPC_CD LIKE 'KC%';
DELETE FROM KRNS_PARM_DTL_TYP_T WHERE NMSPC_CD LIKE 'KC%';
DELETE FROM KRNS_NMSPC_T WHERE NMSPC_CD LIKE 'KC%';
DELETE FROM KRIM_ENTITY_EMAIL_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;
DELETE FROM KRIM_ENTITY_EMP_INFO_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;
DELETE FROM KRIM_ENTITY_PHONE_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;
DELETE FROM KRIM_ENTITY_ADDR_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;
DELETE FROM KRIM_ENTITY_NM_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;
DELETE FROM KRIM_ENTITY_ENT_TYP_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;
DELETE FROM KRIM_PRNCPL_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;
DELETE FROM KRIM_ENTITY_T WHERE ENTITY_ID > 1200 AND ENTITY_ID < 1270;
DELETE FROM KRIM_PHONE_TYP_T WHERE PHONE_TYP_CD = 'FAX';
DELETE FROM KRIM_GRP_MBR_T WHERE GRP_ID IN (SELECT GRP_ID FROM KRIM_GRP_T WHERE NMSPC_CD LIKE 'KC%');
DELETE FROM KRIM_GRP_T WHERE NMSPC_CD LIKE 'KC%';
DELETE FROM KRIM_ROLE_PERM_T WHERE ROLE_ID IN (SELECT ROLE_ID FROM KRIM_ROLE_T WHERE NMSPC_CD LIKE 'KC%');
DELETE FROM KRIM_ROLE_MBR_ATTR_DATA_T WHERE ROLE_MBR_ID IN (SELECT DISTINCT ROLE_MBR_ID FROM KRIM_ROLE_MBR_T WHERE ROLE_ID IN (SELECT ROLE_ID FROM KRIM_ROLE_T WHERE NMSPC_CD LIKE 'KC%'));
DELETE FROM KRIM_ROLE_MBR_T  WHERE ROLE_ID IN (SELECT ROLE_ID FROM KRIM_ROLE_T WHERE NMSPC_CD LIKE 'KC%');
DELETE FROM KRIM_ROLE_T WHERE NMSPC_CD LIKE 'KC%';
DELETE FROM KRIM_ROLE_RSP_ACTN_T WHERE ROLE_RSP_ID IN (SELECT ROLE_RSP_ID FROM KRIM_ROLE_RSP_T WHERE RSP_ID IN (SELECT RSP_ID FROM KRIM_RSP_T WHERE NMSPC_CD LIKE 'KC%'));
DELETE FROM KRIM_ROLE_RSP_T WHERE (ROLE_ID IN (SELECT ROLE_ID FROM KRIM_ROLE_T WHERE NMSPC_CD LIKE 'KC%')) OR (RSP_ID IN (SELECT RSP_ID FROM KRIM_RSP_T WHERE NMSPC_CD LIKE 'KC%'));
DELETE FROM KRIM_RSP_ATTR_DATA_T WHERE RSP_ID IN (SELECT RSP_ID FROM KRIM_RSP_T WHERE NMSPC_CD LIKE 'KC%');
DELETE FROM KRIM_RSP_T WHERE NMSPC_CD LIKE 'KC%';
DELETE FROM KRIM_PERM_ATTR_DATA_T WHERE PERM_ID IN (SELECT PERM_ID FROM KRIM_PERM_T WHERE NMSPC_CD LIKE 'KC%');
DELETE FROM KRIM_PERM_T  WHERE NMSPC_CD LIKE 'KC%';
DELETE FROM KRIM_PERM_TMPL_T WHERE NMSPC_CD LIKE 'KC%';
DELETE FROM KRIM_TYP_ATTR_T WHERE KIM_TYP_ID IN (SELECT KIM_TYP_ID FROM KRIM_TYP_T WHERE NMSPC_CD LIKE 'KC%');
DELETE FROM KRIM_TYP_T WHERE NMSPC_CD LIKE 'KC%';
DELETE FROM KRIM_ATTR_DEFN_T WHERE NMSPC_CD LIKE 'KC%';
DELETE FROM KREN_CHNL_PRODCR_T WHERE CHNL_ID IN (SELECT CHNL_ID FROM KREN_CHNL_T WHERE NM LIKE 'KC%');
DELETE FROM KREN_CHNL_T WHERE NM LIKE 'KC%';
COMMIT;
