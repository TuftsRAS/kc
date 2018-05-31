--
-- Copyright © 2005-2018 Kuali, Inc.
-- All Rights Reserved
-- You may use and modify this code under the terms of the Kuali, Inc.
-- Pre-Release License Agreement. You may not distribute it.
--
-- You should have received a copy of the Kuali, Inc. Pre-Release License
-- Agreement with this file. If not, please write to license@kuali.co.
--

update DASH_BOARD_MENU_ITEMS set MENU_ACTION ='/kc-krad/landingPage?methodToCall=start&href=<<APPLICATION_URL>>%2Fkr-krad%2Flookup%3FviewName%3DS2sOpportunity-LookupViewName%26hideCriteriaOnSearch%3Dtrue%26baseLookupUrl%3D<<APPLICATION_URL>>%2Fkr-krad%2Flookup%26dataObjectClassName%3Dorg.kuali.coeus.propdev.impl.s2s.S2sOpportunity%26returnLocation%3D<<APPLICATION_URL>>%252Fkc-krad%252FlandingPage%253FviewId%253DKc-LandingPage-RedirectView%26hideReturnLink%3Dtrue&viewId=Kc-Header-IframeView' where MENU_ITEM = 'Search For Grants.gov Opportunity';
