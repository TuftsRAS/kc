--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

UPDATE krew_typ_t
	SET nm='KC Advanced Peopleflow',
		srvc_nm='{http://kc.kuali.org/core/v5_0}advancedPeopleFlowTypeService'
WHERE typ_id='KC10000';

UPDATE krew_typ_t
	SET nm='Unit Specific PeopleFlow',
		srvc_nm='{http://kc.kuali.org/core/v5_0}unitSpecificPeopleFlowTypeService'
WHERE typ_id='RESBOOT1000';