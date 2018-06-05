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
values ('RES-BOOT10034', 'KC-PD', 'doesPrimeSponsorTypeMatch', 'Prime Sponsor Type Rule', 'java.lang.Boolean', 'KC1006', 'Y', 1);

insert into krms_func_parm_t (func_parm_id, nm, desc_txt, typ, func_id, seq_no)
values ('RES-BOOT10058', 'DevelopmentProposal', 'Development Proposal BO', 'org.kuali.coeus.common.framework.sponsor.Sponsorable',  'RES-BOOT10034', 1);

insert into krms_func_parm_t (func_parm_id, nm, desc_txt, typ, func_id, seq_no)
values ('RES-BOOT10059', 'Sponsor Type Code', 'Prime Sponsor Type', 'java.lang.String', 'RES-BOOT10034', 2);

insert into krms_term_spec_t (term_spec_id, nm, typ, actv, ver_nbr, desc_txt, nmspc_cd)
values ('RES-BOOT2117', 'RES-BOOT10034', 'java.lang.Boolean', 'Y', 1, 'Prime Sponsor Type Rule. Does prime sponsor type match with input sponsor type.', 'KC-PD');

insert into krms_term_rslvr_t (term_rslvr_id, nmspc_cd, nm, typ_id, output_term_spec_id, actv, ver_nbr)
values ('RES-BOOT2104', 'KC-PD', 'Prime Sponsor and Sponsor Type Resolver', 'KC1001', 'RES-BOOT2117', 'Y', 1);

insert into krms_term_rslvr_parm_spec_t (term_rslvr_parm_spec_id, term_rslvr_id, nm, ver_nbr)
values ('RES-BOOT2094', 'RES-BOOT2104', 'Sponsor Type Code', 1);

insert into krms_cntxt_vld_term_spec_t (cntxt_term_spec_prereq_id, cntxt_id, term_spec_id, prereq)
values ('RES-BOOT1057', 'KC-PD-CONTEXT', 'RES-BOOT2117', 'Y');

insert into krms_term_spec_ctgry_t (term_spec_id, ctgry_id)
values ('RES-BOOT2117', 'KC1001');

insert into krms_func_t (func_id, nmspc_cd, nm, desc_txt, rtrn_typ, typ_id, actv, ver_nbr)
values ('RES-BOOT10035', 'KC-AWARD', 'doesPrimeSponsorTypeMatch', 'Prime Sponsor Type Rule', 'java.lang.Boolean', 'KC10001', 'Y', 1);

insert into krms_func_parm_t (func_parm_id, nm, desc_txt, typ, func_id, seq_no)
values ('RES-BOOT10060', 'award', 'Award BO', 'org.kuali.coeus.common.framework.sponsor.Sponsorable', 'RES-BOOT10035', 1);

insert into krms_func_t (func_id, nmspc_cd, nm, desc_txt, rtrn_typ, typ_id, actv, ver_nbr)
values ('RES-BOOT10036', 'KC-IP', 'doesPrimeSponsorTypeMatch', 'Prime Sponsor Type Rule', 'java.lang.Boolean', 'RES-BOOT10002', 'Y', 1);

insert into krms_func_parm_t (func_parm_id, nm, desc_txt, typ, func_id, seq_no)
values ('RES-BOOT10061', 'InstitutionalProposal', 'Institutional Proposal BO', 'org.kuali.coeus.common.framework.sponsor.Sponsorable', 'RES-BOOT10036', 1);
