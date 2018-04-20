-- noinspection SqlNoDataSourceInspectionForFile

--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

update award_cgb `ac` inner join award a on a.award_id = ac.award_id set ac.sequence_number = a.sequence_number where a.sequence_number != ac.sequence_number;
update award_persons `ap` inner join award a on a.award_id = ap.award_id set ap.sequence_number = a.sequence_number where a.sequence_number != ap.sequence_number;
update award_approved_equipment `aae` inner join award a on a.award_id = aae.award_id set aae.sequence_number = a.sequence_number where a.sequence_number != aae.sequence_number;
update award_approved_foreign_travel `aaft` inner join award a on a.award_id = aaft.award_id set aaft.sequence_number = a.sequence_number where a.sequence_number != aaft.sequence_number;
update award_cost_share `acs` inner join award a on a.award_id = acs.award_id set acs.sequence_number = a.sequence_number where a.sequence_number != acs.sequence_number;
update award_comment `ac` inner join award a on a.award_id = ac.award_id set ac.sequence_number = a.sequence_number where a.sequence_number != ac.sequence_number;
update award_approved_subawards `aas` inner join award a on a.award_id = aas.award_id set aas.sequence_number = a.sequence_number where a.sequence_number != aas.sequence_number;
update award_idc_rate `air` inner join award a on a.award_id = air.award_id set air.sequence_number = a.sequence_number where a.sequence_number != air.sequence_number;
update award_report_terms `art` inner join award a on a.award_id = art.award_id set art.sequence_number = a.sequence_number where a.sequence_number != art.sequence_number;
update award_sponsor_contacts `asc` inner join award a on a.award_id = asc.award_id set asc.sequence_number = a.sequence_number where a.sequence_number != asc.sequence_number;
update award_unit_contacts `auc` inner join award a on a.award_id = auc.award_id set auc.sequence_number = a.sequence_number where a.sequence_number != auc.sequence_number;
update award_sponsor_term `ast` inner join award a on a.award_id = ast.award_id set ast.sequence_number = a.sequence_number where a.sequence_number != ast.sequence_number;
update award_payment_schedule `aps` inner join award a on a.award_id = aps.award_id set aps.sequence_number = a.sequence_number where a.sequence_number != aps.sequence_number;
update award_transferring_sponsor `ats` inner join award a on a.award_id = ats.award_id set ats.sequence_number = a.sequence_number where a.sequence_number != ats.sequence_number;
update award_amt_fna_distribution `aafd` inner join award a on a.award_id = aafd.award_id set aafd.sequence_number = a.sequence_number where a.sequence_number != aafd.sequence_number;
update award_amount_info `aai` inner join award a on a.award_id = aai.award_id set aai.sequence_number = a.sequence_number where a.sequence_number != aai.sequence_number;
update award_custom_data `acd` inner join award a on a.award_id = acd.award_id set acd.sequence_number = a.sequence_number where a.sequence_number != acd.sequence_number;
update award_closeout `ac` inner join award a on a.award_id = ac.award_id set ac.sequence_number = a.sequence_number where a.sequence_number != ac.sequence_number;
update award_attachment `aa` inner join award a on a.award_id = aa.award_id set aa.sequence_number = a.sequence_number where a.sequence_number != aa.sequence_number;