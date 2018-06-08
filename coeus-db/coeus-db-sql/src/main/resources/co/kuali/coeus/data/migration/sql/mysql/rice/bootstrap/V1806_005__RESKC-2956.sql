--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--


insert into krcr_parm_t (nmspc_cd, cmpnt_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, val, parm_desc_txt, eval_oprtr_cd, appl_id)
values ('KC-COMMON', 'Document', 'WORKFLOW_SIMULATION_BATCH_JOB_CRON_EXPRESSION', UUID(), 1, 'CONFG', '0 0 0 * * ?', 'The cron expression to determine the schedule of the workflow documents simulator job.', 'A', 'KC');


insert into krcr_parm_t (nmspc_cd, cmpnt_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, val, parm_desc_txt, eval_oprtr_cd, appl_id)
values ('KC-COMMON', 'Document', 'WORKFLOW_SIMULATION_JOB_ON', UUID(), 1, 'CONFG', 'false', 'Determines if the workflow documents simulator job in enabled.', 'A', 'KC');

insert into krcr_parm_t (nmspc_cd, cmpnt_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, val, parm_desc_txt, eval_oprtr_cd, appl_id)
values ('KC-COMMON', 'Document', 'SAVE_WORKFLOW_SIMULATION_RESULT_ON_WORKFLOW_ACTION', UUID(), 1, 'CONFG', 'false', 'Determines if the workflow documents simulation runs on workflow actions are taken on the document.', 'A', 'KC');

insert into krcr_parm_t (nmspc_cd, cmpnt_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, val, parm_desc_txt, eval_oprtr_cd, appl_id)
values ('KC-COMMON', 'Document', 'WORKFLOW_SIMULATION_BATCH_JOB_CRON_START_TIME', UUID(), 1, 'CONFG', '', 'Start time of the workflow documents simulator job.', 'A', 'KC');
