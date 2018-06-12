--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

update krcr_parm_t set nmspc_cd = 'KC-GEN' where parm_nm = 'WORKFLOW_SIMULATION_BATCH_JOB_CRON_EXPRESSION' and nmspc_cd = 'KC-COMMON' and cmpnt_cd = 'Document';
update krcr_parm_t set nmspc_cd = 'KC-GEN' where parm_nm = 'WORKFLOW_SIMULATION_JOB_ON' and nmspc_cd = 'KC-COMMON' and cmpnt_cd = 'Document';
update krcr_parm_t set nmspc_cd = 'KC-GEN' where parm_nm = 'SAVE_WORKFLOW_SIMULATION_RESULT_ON_WORKFLOW_ACTION' and nmspc_cd = 'KC-COMMON' and cmpnt_cd = 'Document';
update krcr_parm_t set nmspc_cd = 'KC-GEN' where parm_nm = 'WORKFLOW_SIMULATION_BATCH_JOB_CRON_START_TIME' and nmspc_cd = 'KC-COMMON' and cmpnt_cd = 'Document';
