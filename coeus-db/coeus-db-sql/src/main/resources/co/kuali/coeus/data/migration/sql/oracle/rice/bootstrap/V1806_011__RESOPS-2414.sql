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
values ('RES-BOOT10037', 'KC-PD', 'performanceSiteLocationExists', 'Check if Performance Site Location Exists', 'java.lang.Boolean', 'KC1006', 'Y', 1);

insert into krms_func_parm_t (func_parm_id, nm, desc_txt, typ, func_id, seq_no)
values ('RES-BOOT10064', 'DevelopmentProposal', 'Development Proposal BO', 'org.kuali.coeus.propdev.impl.core.DevelopmentProposal',  'RES-BOOT10037', 1);

insert into krms_term_spec_t (term_spec_id, nm, typ, actv, ver_nbr, desc_txt, nmspc_cd)
values ('RES-BOOT2120', 'RES-BOOT10037', 'java.lang.Boolean', 'Y', 1, 'Check if Performance Site Location Exists', 'KC-PD');

insert into krms_term_t (term_id, term_spec_id, ver_nbr, desc_txt)
values ('RES-BOOT10022', 'RES-BOOT2120', '1', 'Check if Performance Site Location Exists');

insert into krms_term_rslvr_t (term_rslvr_id, nmspc_cd, nm, typ_id, output_term_spec_id, actv, ver_nbr)
values ('RES-BOOT2107', 'KC-PD', 'Performance Site Location Resolver', 'KC1001', 'RES-BOOT2120', 'Y', 1);

insert into krms_cntxt_vld_term_spec_t (cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('RES-BOOT1060', 'KC-PD-CONTEXT', 'RES-BOOT2120', 'Y');

insert into krms_term_spec_ctgry_t (term_spec_id, ctgry_id)
values ('RES-BOOT2120', 'KC1001');

insert into krms_func_t (func_id, nmspc_cd, nm, desc_txt, rtrn_typ, typ_id, actv, ver_nbr)
values ('RES-BOOT10038', 'KC-PD', 'otherOrganizationExists', 'Check if Other Organization Exists', 'java.lang.Boolean', 'KC1006', 'Y', 1);

insert into krms_func_parm_t (func_parm_id, nm, desc_txt, typ, func_id, seq_no)
values ('RES-BOOT10065', 'DevelopmentProposal', 'Development Proposal BO', 'org.kuali.coeus.propdev.impl.core.DevelopmentProposal',  'RES-BOOT10038', 1);

insert into krms_term_spec_t (term_spec_id, nm, typ, actv, ver_nbr, desc_txt, nmspc_cd)
values ('RES-BOOT2121', 'RES-BOOT10038', 'java.lang.Boolean', 'Y', 1, 'Check if Other Organization Exists', 'KC-PD');

insert into krms_term_t (term_id, term_spec_id, ver_nbr, desc_txt)
values ('RES-BOOT10023', 'RES-BOOT2121', '1', 'Check if Other Organization Exists');

insert into krms_term_rslvr_t (term_rslvr_id, nmspc_cd, nm, typ_id, output_term_spec_id, actv, ver_nbr)
values ('RES-BOOT2108', 'KC-PD', 'Other Organization Resolver', 'KC1001', 'RES-BOOT2121', 'Y', 1);

insert into krms_cntxt_vld_term_spec_t (cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('RES-BOOT1061', 'KC-PD-CONTEXT', 'RES-BOOT2121', 'Y');

insert into krms_term_spec_ctgry_t (term_spec_id, ctgry_id)
values ('RES-BOOT2121', 'KC1001');


