--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

-- PD
update krms_term_spec_t set desc_txt = 'Prime Sponsor Type' where term_spec_id = 'RES-BOOT2117';

-- Award
insert into krms_func_parm_t (func_parm_id, nm, desc_txt, typ, func_id, seq_no)
values ('RES-BOOT10062', 'Sponsor Type Code', 'Prime Sponsor Type', 'java.lang.String', 'RES-BOOT10035', 2);

insert into krms_term_spec_t (term_spec_id, nm, typ, actv, ver_nbr, desc_txt, nmspc_cd)
values ('RES-BOOT2118', 'RES-BOOT10035', 'java.lang.Boolean', 'Y', 1, 'Prime Sponsor Type', 'KC-PD');

insert into krms_term_rslvr_t (term_rslvr_id, nmspc_cd, nm, typ_id, output_term_spec_id, actv, ver_nbr)
values ('RES-BOOT2105', 'KC-AWARD', 'Prime Sponsor and Sponsor Type Resolver', 'KC1001', 'RES-BOOT2118', 'Y', 1);

insert into krms_term_rslvr_parm_spec_t (term_rslvr_parm_spec_id, term_rslvr_id, nm, ver_nbr)
values ('RES-BOOT2095', 'RES-BOOT2105', 'Sponsor Type Code', 1);

insert into krms_cntxt_vld_term_spec_t (cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('RES-BOOT1058', 'KC-AWARD-CONTEXT', 'RES-BOOT2118', 'Y');

insert into krms_term_spec_ctgry_t (term_spec_id, ctgry_id)
values ('RES-BOOT2118', 'KC1010');

-- IP
insert into krms_func_parm_t (func_parm_id, nm, desc_txt, typ, func_id, seq_no)
values ('RES-BOOT10063', 'Sponsor Type Code', 'Prime Sponsor Type', 'java.lang.String', 'RES-BOOT10036', 2);

insert into krms_term_spec_t (term_spec_id, nm, typ, actv, ver_nbr, desc_txt, nmspc_cd)
values ('RES-BOOT2119', 'RES-BOOT10036', 'java.lang.Boolean', 'Y', 1, 'Prime Sponsor Type', 'KC-PD');

insert into krms_term_rslvr_t (term_rslvr_id, nmspc_cd, nm, typ_id, output_term_spec_id, actv, ver_nbr)
values ('RES-BOOT2106', 'KC-IP', 'Prime Sponsor and Sponsor Type Resolver', 'KC1001', 'RES-BOOT2119', 'Y', 1);

insert into krms_term_rslvr_parm_spec_t (term_rslvr_parm_spec_id, term_rslvr_id, nm, ver_nbr)
values ('RES-BOOT2096', 'RES-BOOT2106', 'Sponsor Type Code', 1);

insert into krms_cntxt_vld_term_spec_t (cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('RES-BOOT1059', 'KC-IP-CONTEXT', 'RES-BOOT2119', 'Y');

insert into krms_term_spec_ctgry_t (term_spec_id, ctgry_id)
values ('RES-BOOT2119', 'KC1016');

