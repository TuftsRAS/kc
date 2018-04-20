--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

update award_cgb `ac` set ac.sequence_number = (select sequence_number from award a where a.award_id = ac.award_id);
update award_persons `ap` set ap.sequence_number = (select sequence_number from award a where a.award_id = ap.award_id);
update award_approved_equipment `aae` set aae.sequence_number = (select sequence_number from award a where a.award_id = aae.award_id);
update award_approved_foreign_travel `aaft` set aaft.sequence_number = (select sequence_number from award a where a.award_id = aaft.award_id);
update award_cost_share `acs` set acs.sequence_number = (select sequence_number from award a where a.award_id = acs.award_id);
update award_comment `ac` set ac.sequence_number = (select sequence_number from award a where a.award_id = ac.award_id);
update award_approved_subawards `aas` set aas.sequence_number = (select sequence_number from award a where a.award_id = aas.award_id);
update award_idc_rate `air` set air.sequence_number = (select sequence_number from award a where a.award_id = air.award_id);
update award_report_terms `art` set art.sequence_number = (select sequence_number from award a where a.award_id = art.award_id);
update award_sponsor_contacts `asc` set asc.sequence_number = (select sequence_number from award a where a.award_id = asc.award_id);
update award_unit_contacts `auc` set auc.sequence_number = (select sequence_number from award a where a.award_id = auc.award_id);
update award_sponsor_term `ast` set ast.sequence_number = (select sequence_number from award a where a.award_id = ast.award_id);
update award_payment_schedule `aps` set aps.sequence_number = (select sequence_number from award a where a.award_id = aps.award_id);
update award_transferring_sponsor `ats` set ats.sequence_number = (select sequence_number from award a where a.award_id = ats.award_id);
update award_amt_fna_distribution `aafd` set aafd.sequence_number = (select sequence_number from award a where a.award_id = aafd.award_id);
update award_amount_info `aai` set aai.sequence_number = (select sequence_number from award a where a.award_id = aai.award_id);
update award_custom_data `acd` set acd.sequence_number = (select sequence_number from award a where a.award_id = acd.award_id);
update award_closeout `ac` set ac.sequence_number = (select sequence_number from award a where a.award_id = ac.award_id);
update award_attachment `aa` set aa.sequence_number = (select sequence_number from award a where a.award_id = aa.award_id);