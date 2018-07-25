--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

UPDATE krim_entity_email_t set EMAIL_ADDR = 'res-admin@kuali.co' WHERE entity_id = (select entity_id from krim_prncpl_t where PRNCPL_NM = 'admin') and
EMAIL_ADDR = 'kcnotification+admin@gmail.com';

