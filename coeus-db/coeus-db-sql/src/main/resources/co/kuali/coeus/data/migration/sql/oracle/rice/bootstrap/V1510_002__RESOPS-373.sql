INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-PD','Document','PROPOSAL_TYPE_CODE_PRE_PROPOSAL',SYS_GUID(),1,'CONFG','14','Code corresponding to Proposal Type:PreProposal','A','KC');
INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-S2S','All','PROPOSAL_TYPE_CODE_PRE_PROPOSAL',SYS_GUID(),1,'CONFG','@{#param("KC-PD", "Document", "PROPOSAL_TYPE_CODE_PRE_PROPOSAL")}','Code corresponding to Proposal Type:PreProposal','A','KC');
INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-PD','Document','PROPOSAL_TYPE_CODE_BUDGET_SOW_UPDATE',SYS_GUID(),1,'CONFG','17','Code corresponding to Proposal Type:Budget SOW Update','A','KC');
INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-S2S','All','PROPOSAL_TYPE_CODE_BUDGET_SOW_UPDATE',SYS_GUID(),1,'CONFG','@{#param("KC-PD", "Document", "PROPOSAL_TYPE_CODE_BUDGET_SOW_UPDATE")}','Code corresponding to Proposal Type:Budget SOW Update','A','KC');
INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-PD','Document','PROPOSAL_TYPE_CODE_SUPPLEMENT_CHANGE_CORRECTED',SYS_GUID(),1,'CONFG','15','Code corresponding to Proposal Type:Supplement Changed/Corrected','A','KC');
INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-S2S','All','PROPOSAL_TYPE_CODE_SUPPLEMENT_CHANGE_CORRECTED',SYS_GUID(),1,'CONFG','@{#param("KC-PD", "Document", "PROPOSAL_TYPE_CODE_SUPPLEMENT_CHANGE_CORRECTED")}','Code corresponding to Proposal Type:Supplement Changed/Corrected','A','KC');
INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-PD','Document','PROPOSAL_TYPE_CODE_RENEWAL_CHANGE_CORRECTED',SYS_GUID(),1,'CONFG','18','Code corresponding to Proposal Type:Renewal Changed/Corrected','A','KC');
INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-S2S','All','PROPOSAL_TYPE_CODE_RENEWAL_CHANGE_CORRECTED',SYS_GUID(),1,'CONFG','@{#param("KC-PD", "Document", "PROPOSAL_TYPE_CODE_RENEWAL_CHANGE_CORRECTED")}','Code corresponding to Proposal Type:Renewal Changed/Corrected','A','KC');
INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-PD','Document','PROPOSAL_TYPE_CODE_RESUBMISSION_CHANGE_CORRECTED',SYS_GUID(),1,'CONFG','16','Code corresponding to Proposal Type:PreProposal','A','KC');
INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-S2S','All','PROPOSAL_TYPE_CODE_RESUBMISSION_CHANGE_CORRECTED',SYS_GUID(),1,'CONFG','@{#param("KC-PD", "Document", "PROPOSAL_TYPE_CODE_RESUBMISSION_CHANGE_CORRECTED")}','Code corresponding to Proposal Type:Resubmission Changed/Corrected','A','KC');
INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-PD','Document','PROPOSAL_TYPE_CODE_NEW_CHANGE_CORRECTED',SYS_GUID(),1,'CONFG','10','Code corresponding to Proposal Type:New Changed/Corrected','A','KC');
INSERT INTO KRCR_PARM_T(NMSPC_CD,CMPNT_CD,PARM_NM,OBJ_ID,VER_NBR,PARM_TYP_CD,VAL,PARM_DESC_TXT,EVAL_OPRTR_CD,APPL_ID)
VALUES ('KC-S2S','All','PROPOSAL_TYPE_CODE_NEW_CHANGE_CORRECTED',SYS_GUID(),1,'CONFG','@{#param("KC-PD", "Document", "PROPOSAL_TYPE_CODE_NEW_CHANGE_CORRECTED")}','Code corresponding to Proposal Type:New Changed/Corrected','A','KC');
UPDATE KRCR_PARM_T SET VAL = '1,10,14' WHERE PARM_NM='PROPOSAL_TYPE_CODE_NEW' AND NMSPC_CD = 'KC-S2S' AND VAL='@{#param("KC-PD", "Document", "PROPOSAL_TYPE_CODE_NEW")}';
UPDATE KRCR_PARM_T SET VAL = '4,15' WHERE PARM_NM='PROPOSAL_TYPE_CODE_REVISION' AND NMSPC_CD = 'KC-S2S' AND VAL='@{#param("KC-PD", "Document", "PROPOSAL_TYPE_CODE_REVISION")}';
UPDATE KRCR_PARM_T SET VAL = '5,18' WHERE PARM_NM='PROPOSAL_TYPE_CODE_RENEWAL' AND NMSPC_CD = 'KC-S2S' AND VAL='@{#param("KC-PD", "Document", "PROPOSAL_TYPE_CODE_RENEWAL")}';
UPDATE KRCR_PARM_T SET VAL = '6,16' WHERE PARM_NM='PROPOSAL_TYPE_CODE_RESUBMISSION' AND NMSPC_CD = 'KC-S2S' AND VAL='@{#param("KC-PD", "Document", "PROPOSAL_TYPE_CODE_RESUBMISSION")}';
UPDATE KRCR_PARM_T SET VAL = '3' WHERE PARM_NM='PROPOSAL_TYPE_CODE_CONTINUATION' AND NMSPC_CD = 'KC-PD' AND VAL='3';
UPDATE KRCR_PARM_T SET VAL = '3' WHERE PARM_NM='PROPOSAL_TYPE_CODE_CONTINUATION' AND NMSPC_CD = 'KC-S2S' AND VAL='@{#param("KC-PD", "Document", "PROPOSAL_TYPE_CODE_CONTINUATION")}';