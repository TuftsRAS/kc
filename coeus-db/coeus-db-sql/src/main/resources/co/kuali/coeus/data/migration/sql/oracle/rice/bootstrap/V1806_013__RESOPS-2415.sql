--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

insert into krms_func_t (func_id, nmspc_cd, nm, desc_txt, rtrn_typ, typ_id, actv, ver_nbr)
values ('RES-BOOT10039', 'KC-PD', 'proposalPersonUnitBelowRule', 'Is Project Role Proposal Unit Below?', 'java.lang.Boolean', 'KC1006', 'Y', 1);

insert into krms_func_parm_t (func_parm_id, nm, desc_txt, typ, func_id, seq_no)
values ('RES-BOOT10066', 'DevelopmentProposal', 'Development Proposal BO', 'org.kuali.coeus.propdev.impl.core.DevelopmentProposal',  'RES-BOOT10039', 1);

insert into krms_func_parm_t (func_parm_id, nm, desc_txt, typ, func_id, seq_no)
values ('RES-BOOT10067', 'Project Role', 'Project Role', 'java.lang.String', 'RES-BOOT10039', 2);

insert into krms_func_parm_t (func_parm_id, nm, desc_txt, typ, func_id, seq_no)
values ('RES-BOOT10068', 'Proposal Unit', 'Proposal Unit', 'java.lang.String', 'RES-BOOT10039', 3);

insert into krms_term_spec_t (term_spec_id, nm, typ, actv, ver_nbr, desc_txt, nmspc_cd)
values ('RES-BOOT2122', 'RES-BOOT10039', 'java.lang.Boolean', 'Y', 1, 'Is Project Role Proposal Unit Below?', 'KC-PD');

insert into krms_term_rslvr_t (term_rslvr_id, nmspc_cd, nm, typ_id, output_term_spec_id, actv, ver_nbr)
values ('RES-BOOT2109', 'KC-PD', 'Project Role and Proposal Unit Resolver', 'KC1001', 'RES-BOOT2122', 'Y', 1);

insert into krms_term_rslvr_parm_spec_t (term_rslvr_parm_spec_id, term_rslvr_id, nm, ver_nbr)
values ('RES-BOOT2097', 'RES-BOOT2109', 'Project Role', 1);

insert into krms_term_rslvr_parm_spec_t (term_rslvr_parm_spec_id, term_rslvr_id, nm, ver_nbr)
values ('RES-BOOT2098', 'RES-BOOT2109', 'Proposal Unit', 1);

insert into krms_cntxt_vld_term_spec_t (cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('RES-BOOT1062', 'KC-PD-CONTEXT', 'RES-BOOT2122', 'Y');

insert into krms_term_spec_ctgry_t (term_spec_id, ctgry_id)
values ('RES-BOOT2122', 'KC1001');

insert into krms_func_t (func_id, nmspc_cd, nm, desc_txt, rtrn_typ, typ_id, actv, ver_nbr)
values ('RES-BOOT10040', 'KC-PD', 'proposalPersonUnitRule', 'Is Project Role Proposal Unit?', 'java.lang.Boolean', 'KC1006', 'Y', 1);

insert into krms_func_parm_t (func_parm_id, nm, desc_txt, typ, func_id, seq_no)
values ('RES-BOOT10069', 'DevelopmentProposal', 'Development Proposal BO', 'org.kuali.coeus.propdev.impl.core.DevelopmentProposal',  'RES-BOOT10040', 1);

insert into krms_func_parm_t (func_parm_id, nm, desc_txt, typ, func_id, seq_no)
values ('RES-BOOT10070', 'Project Role', 'Project Role', 'java.lang.String', 'RES-BOOT10040', 2);

insert into krms_func_parm_t (func_parm_id, nm, desc_txt, typ, func_id, seq_no)
values ('RES-BOOT10071', 'Proposal Unit', 'Proposal Unit', 'java.lang.String', 'RES-BOOT10040', 3);

insert into krms_term_spec_t (term_spec_id, nm, typ, actv, ver_nbr, desc_txt, nmspc_cd)
values ('RES-BOOT2123', 'RES-BOOT10040', 'java.lang.Boolean', 'Y', 1, 'Is Project Role Proposal Unit?', 'KC-PD');

insert into krms_term_rslvr_t (term_rslvr_id, nmspc_cd, nm, typ_id, output_term_spec_id, actv, ver_nbr)
values ('RES-BOOT2110', 'KC-PD', 'Project Role and Proposal Person Unit Resolver', 'KC1001', 'RES-BOOT2123', 'Y', 1);

insert into krms_term_rslvr_parm_spec_t (term_rslvr_parm_spec_id, term_rslvr_id, nm, ver_nbr)
values ('RES-BOOT2099', 'RES-BOOT2110', 'Project Role', 1);

insert into krms_term_rslvr_parm_spec_t (term_rslvr_parm_spec_id, term_rslvr_id, nm, ver_nbr)
values ('RES-BOOT2100', 'RES-BOOT2110', 'Proposal Unit', 1);

insert into krms_cntxt_vld_term_spec_t (cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('RES-BOOT1063', 'KC-PD-CONTEXT', 'RES-BOOT2123', 'Y');

insert into krms_term_spec_ctgry_t (term_spec_id, ctgry_id)
values ('RES-BOOT2123', 'KC1001');

