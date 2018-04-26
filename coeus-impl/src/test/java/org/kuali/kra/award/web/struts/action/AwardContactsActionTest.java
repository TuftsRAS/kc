/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.web.struts.action;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.coeus.common.api.sponsor.hierarchy.SponsorHierarchyService;
import org.kuali.coeus.common.framework.auth.UnitAuthorizationService;
import org.kuali.coeus.common.framework.person.PropAwardPersonRole;
import org.kuali.coeus.common.framework.person.PropAwardPersonRoleService;
import org.kuali.kra.award.contacts.AwardPerson;
import org.kuali.kra.award.home.Award;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

import static org.mockito.Mockito.*;

public class AwardContactsActionTest {

    @Before
    public void setUp() {
    	GlobalVariables.setMessageMap(new MessageMap());
    }

    @Test
    public void testIsValidSave() {
        AwardContactsAction contactsAction = new AwardContactsAction();
        Award award = new Award();
        award.setSponsorCode("000340");
        AwardPerson awardPerson1 = new AwardPerson();
        award.add(awardPerson1);
        Assert.assertFalse(contactsAction.isValidSave(award));
    }

    @Test
    public void test_is_valid_save_with_lead_unit() {
        SponsorHierarchyService sponsorHierarchyService = mock(SponsorHierarchyService.class);
        PropAwardPersonRoleService propAwardPersonRoleService = mock(PropAwardPersonRoleService.class);
        UserSession userSession = mock(UserSession.class);
        UnitAuthorizationService unitAuthorizationService = mock(UnitAuthorizationService.class);
        GlobalVariables.setUserSession(userSession);
        when(unitAuthorizationService.hasPermission(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        when(userSession.getPrincipalId()).thenReturn("admin");
        when(propAwardPersonRoleService.areAllSponsorsMultiPi()).thenReturn(false);
        when(sponsorHierarchyService.isSponsorNihMultiplePi(anyString())).thenReturn(false);

        AwardContactsAction contactsAction = new AwardContactsAction();
        contactsAction.setPropAwardPersonRoleService(propAwardPersonRoleService);
        contactsAction.setSponsorHierarchyService(sponsorHierarchyService);
        contactsAction.setAuthService(unitAuthorizationService);
        Award award = new Award();
        award.setSponsorCode("000340");
        award.setUnitNumber("000001");
        AwardPerson awardPerson1 = new AwardPerson();
        awardPerson1.setContactRoleCode("PI");
        award.add(awardPerson1);
        Assert.assertTrue(contactsAction.isValidSave(award));
        Assert.assertFalse(GlobalVariables.getMessageMap().hasErrors());
    }

    @Test
    public void test_is_valid_save_with_multiple_pis() {
        SponsorHierarchyService sponsorHierarchyService = mock(SponsorHierarchyService.class);
        PropAwardPersonRoleService propAwardPersonRoleService = mock(PropAwardPersonRoleService.class);
        UserSession userSession = mock(UserSession.class);
        UnitAuthorizationService unitAuthorizationService = mock(UnitAuthorizationService.class);
        GlobalVariables.setUserSession(userSession);
        when(unitAuthorizationService.hasPermission(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        when(userSession.getPrincipalId()).thenReturn("admin");
        when(propAwardPersonRoleService.areAllSponsorsMultiPi()).thenReturn(false);
        when(sponsorHierarchyService.isSponsorNihMultiplePi(anyString())).thenReturn(false);

        AwardContactsAction contactsAction = new AwardContactsAction();
        contactsAction.setPropAwardPersonRoleService(propAwardPersonRoleService);
        contactsAction.setSponsorHierarchyService(sponsorHierarchyService);
        contactsAction.setAuthService(unitAuthorizationService);
        Award award = new Award();
        award.setSponsorCode("000340");
        award.setUnitNumber("000001");
        AwardPerson awardPerson1 = new AwardPerson();
        awardPerson1.setContactRoleCode("PI");
        AwardPerson awardPerson2 = new AwardPerson();
        awardPerson2.setContactRoleCode("PI");
        award.add(awardPerson1);
        award.add(awardPerson2);

        Assert.assertFalse(contactsAction.isValidSave(award));
        Assert.assertTrue(GlobalVariables.getMessageMap().hasErrors());
    }

    @Test
    public void test_is_valid_save_with_multiple_pis_2() {
        SponsorHierarchyService sponsorHierarchyService = mock(SponsorHierarchyService.class);
        PropAwardPersonRoleService propAwardPersonRoleService = mock(PropAwardPersonRoleService.class);
        UserSession userSession = mock(UserSession.class);
        UnitAuthorizationService unitAuthorizationService = mock(UnitAuthorizationService.class);
        GlobalVariables.setUserSession(userSession);
        when(unitAuthorizationService.hasPermission(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        when(userSession.getPrincipalId()).thenReturn("admin");
        when(propAwardPersonRoleService.areAllSponsorsMultiPi()).thenReturn(false);
        when(sponsorHierarchyService.isSponsorNihMultiplePi(anyString())).thenReturn(false);

        AwardContactsAction contactsAction = new AwardContactsAction();
        contactsAction.setPropAwardPersonRoleService(propAwardPersonRoleService);
        contactsAction.setSponsorHierarchyService(sponsorHierarchyService);
        contactsAction.setAuthService(unitAuthorizationService);
        Award award = new Award();
        award.setUnitNumber("000001");
        award.setSponsorCode("000340");
        AwardPerson awardPerson1 = new AwardPerson();
        awardPerson1.setContactRoleCode("PI");
        AwardPerson awardPerson2 = new AwardPerson();
        awardPerson2.setContactRoleCode("MPI");
        award.add(awardPerson1);
        award.add(awardPerson2);

        Assert.assertFalse(contactsAction.isValidSave(award));
        Assert.assertTrue(GlobalVariables.getMessageMap().hasErrors());
    }

    @Test
     public void test_is_valid_save_with_multiple_pis_mpi_valid() {
        SponsorHierarchyService sponsorHierarchyService = mock(SponsorHierarchyService.class);
        PropAwardPersonRoleService propAwardPersonRoleService = mock(PropAwardPersonRoleService.class);
        UserSession userSession = mock(UserSession.class);
        UnitAuthorizationService unitAuthorizationService = mock(UnitAuthorizationService.class);
        GlobalVariables.setUserSession(userSession);
        when(unitAuthorizationService.hasPermission(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        when(userSession.getPrincipalId()).thenReturn("admin");
        when(propAwardPersonRoleService.areAllSponsorsMultiPi()).thenReturn(false);
        when(sponsorHierarchyService.isSponsorNihMultiplePi(anyString())).thenReturn(true);

        AwardContactsAction contactsAction = new AwardContactsAction();
        contactsAction.setPropAwardPersonRoleService(propAwardPersonRoleService);
        contactsAction.setSponsorHierarchyService(sponsorHierarchyService);
        contactsAction.setAuthService(unitAuthorizationService);
        Award award = new Award();
        award.setSponsorCode("000340");
        award.setUnitNumber("000001");
        AwardPerson awardPerson1 = new AwardPerson();
        awardPerson1.setContactRoleCode("PI");
        AwardPerson awardPerson2 = new AwardPerson();
        awardPerson2.setContactRoleCode("MPI");
        award.add(awardPerson1);
        award.add(awardPerson2);

        Assert.assertTrue(contactsAction.isValidSave(award));
        Assert.assertFalse(GlobalVariables.getMessageMap().hasErrors());
    }

    @Test
    public void test_is_valid_save_with_multiple_pis_mpi_valid_non_nih_sponsor() {
        SponsorHierarchyService sponsorHierarchyService = mock(SponsorHierarchyService.class);
        PropAwardPersonRoleService propAwardPersonRoleService = mock(PropAwardPersonRoleService.class);
        UserSession userSession = mock(UserSession.class);
        UnitAuthorizationService unitAuthorizationService = mock(UnitAuthorizationService.class);
        GlobalVariables.setUserSession(userSession);
        when(unitAuthorizationService.hasPermission(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        when(userSession.getPrincipalId()).thenReturn("admin");
        when(propAwardPersonRoleService.areAllSponsorsMultiPi()).thenReturn(false);
        when(propAwardPersonRoleService.getRole(PropAwardPersonRole.MULTI_PI, PropAwardPersonRoleService.DEFAULT_SPONSOR_HIERARCHY_NAME)).thenReturn(new PropAwardPersonRole());
        when(sponsorHierarchyService.isSponsorNihMultiplePi(anyString())).thenReturn(false);

        AwardContactsAction contactsAction = new AwardContactsAction();
        contactsAction.setPropAwardPersonRoleService(propAwardPersonRoleService);
        contactsAction.setSponsorHierarchyService(sponsorHierarchyService);
        contactsAction.setAuthService(unitAuthorizationService);
        Award award = new Award();
        award.setSponsorCode("000100");
        award.setUnitNumber("000001");
        AwardPerson awardPerson1 = new AwardPerson();
        awardPerson1.setContactRoleCode("PI");
        AwardPerson awardPerson2 = new AwardPerson();
        awardPerson2.setContactRoleCode("MPI");
        award.add(awardPerson1);
        award.add(awardPerson2);

        Assert.assertTrue(contactsAction.isValidSave(award));
        Assert.assertFalse(GlobalVariables.getMessageMap().hasErrors());
    }

    @Test
    public void test_is_not_valid_save_with_multiple_pis_mpi_valid_non_nih_sponsor() {
        SponsorHierarchyService sponsorHierarchyService = mock(SponsorHierarchyService.class);
        PropAwardPersonRoleService propAwardPersonRoleService = mock(PropAwardPersonRoleService.class);
        UserSession userSession = mock(UserSession.class);
        UnitAuthorizationService unitAuthorizationService = mock(UnitAuthorizationService.class);
        GlobalVariables.setUserSession(userSession);
        when(unitAuthorizationService.hasPermission(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        when(userSession.getPrincipalId()).thenReturn("admin");
        when(propAwardPersonRoleService.areAllSponsorsMultiPi()).thenReturn(false);
        when(propAwardPersonRoleService.getRole(PropAwardPersonRole.MULTI_PI, PropAwardPersonRoleService.DEFAULT_SPONSOR_HIERARCHY_NAME)).thenReturn(null);
        when(sponsorHierarchyService.isSponsorNihMultiplePi(anyString())).thenReturn(false);

        AwardContactsAction contactsAction = new AwardContactsAction();
        contactsAction.setPropAwardPersonRoleService(propAwardPersonRoleService);
        contactsAction.setSponsorHierarchyService(sponsorHierarchyService);
        contactsAction.setAuthService(unitAuthorizationService);
        Award award = new Award();
        award.setSponsorCode("000100");
        award.setUnitNumber("000001");
        AwardPerson awardPerson1 = new AwardPerson();
        awardPerson1.setContactRoleCode("PI");
        AwardPerson awardPerson2 = new AwardPerson();
        awardPerson2.setContactRoleCode("MPI");
        award.add(awardPerson1);
        award.add(awardPerson2);

        Assert.assertFalse(contactsAction.isValidSave(award));
        Assert.assertTrue(GlobalVariables.getMessageMap().hasErrors());
    }

    @Test
    public void test_is_valid_save_invalid_permission() {
        SponsorHierarchyService sponsorHierarchyService = mock(SponsorHierarchyService.class);
        PropAwardPersonRoleService propAwardPersonRoleService = mock(PropAwardPersonRoleService.class);
        UserSession userSession = mock(UserSession.class);
        UnitAuthorizationService unitAuthorizationService = mock(UnitAuthorizationService.class);
        GlobalVariables.setUserSession(userSession);
        when(unitAuthorizationService.hasPermission(anyString(), anyString(), anyString(), anyString())).thenReturn(false);
        when(userSession.getPrincipalId()).thenReturn("admin");
        when(propAwardPersonRoleService.areAllSponsorsMultiPi()).thenReturn(false);
        when(sponsorHierarchyService.isSponsorNihMultiplePi(anyString())).thenReturn(true);

        AwardContactsAction contactsAction = new AwardContactsAction();
        contactsAction.setPropAwardPersonRoleService(propAwardPersonRoleService);
        contactsAction.setSponsorHierarchyService(sponsorHierarchyService);
        contactsAction.setAuthService(unitAuthorizationService);
        Award award = new Award();
        award.setSponsorCode("000340");
        award.setUnitNumber("000001");
        AwardPerson awardPerson1 = new AwardPerson();
        awardPerson1.setContactRoleCode("PI");
        AwardPerson awardPerson2 = new AwardPerson();
        awardPerson2.setContactRoleCode("MPI");
        award.add(awardPerson1);
        award.add(awardPerson2);

        Assert.assertFalse(contactsAction.isValidSave(award));
        Assert.assertTrue(GlobalVariables.getMessageMap().hasErrors());
    }


}
