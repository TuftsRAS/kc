--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

update krcr_parm_t set VAL = 'https://s3.amazonaws.com/falextracts/Assistance%20Listings/datagov/AssistanceListings_DataGov_PUBLIC_CURRENT.csv'
where APPL_ID = 'KC' and PARM_TYP_CD = 'CONFG' and NMSPC_CD = 'KC-AWARD' and PARM_NM = 'CFDA_GOV_URL';

update krcr_parm_t set val = '0 0 6 ? * SUN'
where appl_id = 'KC' and parm_typ_cd = 'CONFG' and nmspc_cd = 'KC-AWARD' and parm_nm = 'CFDA_BATCH_JOB_CRON_EXPRESSION' and VAL = '0 0 6 * * ?';