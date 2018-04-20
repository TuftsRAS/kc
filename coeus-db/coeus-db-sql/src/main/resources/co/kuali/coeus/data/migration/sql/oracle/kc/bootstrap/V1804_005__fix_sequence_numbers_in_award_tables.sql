--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

update award_cgb set award_cgb.sequence_number = (select sequence_number from award a where a.award_id = award_cgb.award_id);
update award_persons set award_persons.sequence_number = (select sequence_number from award a where a.award_id = award_persons.award_id);
update award_approved_equipment set award_approved_equipment.sequence_number = (select sequence_number from award a where a.award_id = award_approved_equipment.award_id);
update award_approved_foreign_travel set award_approved_foreign_travel.sequence_number = (select sequence_number from award a where a.award_id = award_approved_foreign_travel.award_id);
update award_cost_share set award_cost_share.sequence_number = (select sequence_number from award a where a.award_id = award_cost_share.award_id);
update award_comment set award_comment.sequence_number = (select sequence_number from award a where a.award_id = award_comment.award_id);
update award_approved_subawards set award_approved_subawards.sequence_number = (select sequence_number from award a where a.award_id = award_approved_subawards.award_id);
update award_idc_rate set award_idc_rate.sequence_number = (select sequence_number from award a where a.award_id = award_idc_rate.award_id);
update award_report_terms set award_report_terms.sequence_number = (select sequence_number from award a where a.award_id = award_report_terms.award_id);
update award_sponsor_contacts set award_sponsor_contacts.sequence_number = (select sequence_number from award a where a.award_id = award_sponsor_contacts.award_id);
update award_unit_contacts set award_unit_contacts.sequence_number = (select sequence_number from award a where a.award_id = award_unit_contacts.award_id);
update award_sponsor_term set award_sponsor_term.sequence_number = (select sequence_number from award a where a.award_id = award_sponsor_term.award_id);
update award_payment_schedule set award_payment_schedule.sequence_number = (select sequence_number from award a where a.award_id = award_payment_schedule.award_id);
update award_transferring_sponsor set award_transferring_sponsor.sequence_number = (select sequence_number from award a where a.award_id = award_transferring_sponsor.award_id);
update award_amt_fna_distribution set award_amt_fna_distribution.sequence_number = (select sequence_number from award a where a.award_id = award_amt_fna_distribution.award_id);
update award_amount_info set award_amount_info.sequence_number = (select sequence_number from award a where a.award_id = award_amount_info.award_id);
update award_custom_data set award_custom_data.sequence_number = (select sequence_number from award a where a.award_id = award_custom_data.award_id);
update award_closeout set award_closeout.sequence_number = (select sequence_number from award a where a.award_id = award_closeout.award_id);
update award_attachment set award_attachment.sequence_number = (select sequence_number from award a where a.award_id = award_attachment.award_id);