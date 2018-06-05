/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.impl.krms;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kuali.coeus.common.framework.sponsor.Sponsor;
import org.kuali.coeus.common.framework.version.VersionStatus;
import org.kuali.coeus.common.framework.version.history.VersionHistory;
import org.kuali.coeus.common.framework.version.sequence.owner.SequenceOwner;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.rice.krad.service.BusinessObjectService;

public class KcKrmsJavaFunctionTermServiceBaseTest {

    public static final String NIH_SPONSOR_CODE = "000340";
    public static final String TEST_SPONSOR_CODE = "000100";
    public static final String STATE_SPONSOR_CODE = "001001";
    public static final String FEDERAL_SPONSOR_TYPE_CODE = "0";
    public static final String STATE_SPONSOR_TYPE_CODE = "1";
	private Mockery context;
	private BusinessObjectService businessObjectService;

	@Before()
	public void setUpMockery() {
		context = new JUnit4Mockery() {{setThreadingPolicy(new Synchroniser());}};
		businessObjectService = context.mock(BusinessObjectService.class);
	}
	
    class MockIP extends InstitutionalProposal {
        @Override
        protected void calculateFiscalMonthAndYearFields() {

        }
    }

    class MockAward extends Award {
        String versionNameFieldValue = "3";
        @Override
        public String getVersionNameFieldValue() {
            return versionNameFieldValue;
        }
        public void setVersionNameFieldValue(String value) {
            this.versionNameFieldValue = value;
        }

    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    @Test
    public void testHasPropertyChangedThisVersion() {
        class MockKrmsBase extends KcKrmsJavaFunctionTermServiceBase {
            @Override
            protected List<VersionHistory> getVersionHistories(SequenceOwner<?> currentVersion, String versionNumber) {
                return getVersionHistoriesSet1();
            }
        }

        MockAward award = new MockAward();
        award.setSequenceNumber(4);
        award.setSponsorCode("000340");
        MockKrmsBase krmsTermService = new MockKrmsBase();
        Assert.assertTrue(krmsTermService.hasPropertyChangedThisVersion(award, "sponsorCode"));
        exception.expect(RuntimeException.class);
        Assert.assertFalse(krmsTermService.hasPropertyChangedThisVersion(award, "sponso"));
        award.setPreAwardAuthorizedAmount(ScaleTwoDecimal.ZERO);
        Assert.assertTrue(krmsTermService.hasPropertyChangedThisVersion(award, "preAwardAuthorizedAmount"));
        award.setPreAwardAuthorizedAmount(null);
        Assert.assertTrue(krmsTermService.hasPropertyChangedThisVersion(award, "preAwardAuthorizedAmount"));
        award.setPreAwardInstitutionalAuthorizedAmount(ScaleTwoDecimal.ONE_HUNDRED);
        Assert.assertTrue(krmsTermService.hasPropertyChangedThisVersion(award, "preAwardInstitutionalAuthorizedAmount"));
        award.setPreAwardInstitutionalAuthorizedAmount(null);
        Assert.assertFalse(krmsTermService.hasPropertyChangedThisVersion(award, "preAwardInstitutionalAuthorizedAmount"));
        Assert.assertFalse(krmsTermService.hasPropertyChangedThisVersion(null, null));

        class MockKrmsBase1 extends KcKrmsJavaFunctionTermServiceBase {
            @Override
            protected SequenceOwner<?> getLastActiveVersion(SequenceOwner<?> currentVersion) {
                return null;
            }
        }
        MockKrmsBase1 krmsTermService2 = new MockKrmsBase1();
        Assert.assertFalse(krmsTermService2.hasPropertyChangedThisVersion(award, "preAwardInstitutionalAuthorizedAmount"));

    }

    private List<VersionHistory> getVersionHistoriesSet1() {
        List<VersionHistory> versionHistories = new ArrayList<>();
        VersionHistory versionHistory1 = new VersionHistory();
        versionHistory1.setSequenceOwnerSequenceNumber(1);
        versionHistory1.setStatus(VersionStatus.ARCHIVED );
        MockAward award1 = new MockAward();
        award1.setTitle("Award1");
        award1.setSponsorCode("000340");
        award1.setPreAwardAuthorizedAmount(ScaleTwoDecimal.ONE_HUNDRED);
        versionHistory1.setSequenceOwner(award1);

        VersionHistory versionHistory2 = new VersionHistory();
        versionHistory2.setSequenceOwnerSequenceNumber(2);
        MockAward award2 = new MockAward();
        award2.setTitle("Award2");
        award2.setSponsorCode("000001");
        versionHistory2.setSequenceOwner(award2);
        versionHistory2.setStatus(VersionStatus.CANCELED);

        VersionHistory versionHistory3 = new VersionHistory();
        versionHistory3.setSequenceOwnerSequenceNumber(3);
        MockAward award3 = new MockAward();
        award3.setTitle("Award3");
        award3.setSponsorCode("000001");
        award3.setPreAwardAuthorizedAmount(ScaleTwoDecimal.ONE_HUNDRED);
        award3.setPreAwardInstitutionalAuthorizedAmount(null);
        versionHistory3.setSequenceOwner(award3);
        versionHistory3.setStatus(VersionStatus.ACTIVE);

        versionHistories.add(versionHistory1);
        versionHistories.add(versionHistory2);
        versionHistories.add(versionHistory3);
        return versionHistories;
    }

    @Test
    public void testCheckPropertyValueForAnyPreviousVersion() {

        class MockKrmsBase extends KcKrmsJavaFunctionTermServiceBase {
            @Override
            protected List<VersionHistory> getVersionHistories(SequenceOwner<?> currentVersion, String versionNumber) {
                List<VersionHistory> versionHistories = new ArrayList<>();
                VersionHistory versionHistory1 = new VersionHistory();
                versionHistory1.setSequenceOwnerSequenceNumber(1);
                versionHistory1.setStatus(VersionStatus.ARCHIVED );
                MockAward award1 = new MockAward();
                award1.setSponsorCode("000340");
                award1.setPreAwardAuthorizedAmount(ScaleTwoDecimal.ONE_HUNDRED);
                versionHistory1.setSequenceOwner(award1);
                VersionHistory versionHistory2 = new VersionHistory();
                versionHistory2.setSequenceOwnerSequenceNumber(2);
                MockAward award2 = new MockAward();
                award2.setSponsorCode("000001");
                versionHistory2.setSequenceOwner(award2);
                versionHistory2.setStatus(VersionStatus.CANCELED);

                versionHistories.add(versionHistory1);
                versionHistories.add(versionHistory2);
                return versionHistories;
            }
        }


        MockAward award = new MockAward();
        award.setSequenceNumber(3);
        MockKrmsBase krmsTermService = new MockKrmsBase();
        Assert.assertFalse(krmsTermService.checkPropertyValueForAnyPreviousVersion(award, "sponsorCode", "000001"));
        Assert.assertTrue(krmsTermService.checkPropertyValueForAnyPreviousVersion(award, "sponsorCode", "000340"));

        Assert.assertFalse(krmsTermService.checkPropertyValueForAnyPreviousVersion(award, "sponso", "000340"));
        Assert.assertTrue(krmsTermService.checkPropertyValueForAnyPreviousVersion(award, "preAwardAuthorizedAmount", "100.000"));
        award.setSequenceNumber(1);
        Assert.assertFalse(krmsTermService.checkPropertyValueForAnyPreviousVersion(award, "sponsorCode", "000340"));
        award.setSequenceNumber(3);
        award.setVersionNameFieldValue(null);
        Assert.assertFalse(krmsTermService.checkPropertyValueForAnyPreviousVersion(award, "sponsorCode", "000340"));

    }

    @Test
    public void testGetLastActiveVersion() {
        class MockKrmsBase extends KcKrmsJavaFunctionTermServiceBase {
            @Override
            protected List<VersionHistory> getVersionHistories(SequenceOwner<?> currentVersion, String versionNumber) {
                List<VersionHistory> versionHistories = new ArrayList<>();
                VersionHistory versionHistory1 = new VersionHistory();
                versionHistory1.setSequenceOwnerSequenceNumber(1);
                versionHistory1.setStatus(VersionStatus.ARCHIVED );
                MockAward award1 = new MockAward();
                award1.setTitle("Award1");
                award1.setSponsorCode("000340");
                award1.setPreAwardAuthorizedAmount(ScaleTwoDecimal.ONE_HUNDRED);
                versionHistory1.setSequenceOwner(award1);

                VersionHistory versionHistory2 = new VersionHistory();
                versionHistory2.setSequenceOwnerSequenceNumber(2);
                MockAward award2 = new MockAward();
                award2.setTitle("Award2");
                award2.setSponsorCode("000001");
                versionHistory2.setSequenceOwner(award2);
                versionHistory2.setStatus(VersionStatus.CANCELED);

                VersionHistory versionHistory3 = new VersionHistory();
                versionHistory3.setSequenceOwnerSequenceNumber(3);
                MockAward award3 = new MockAward();
                award3.setTitle("Award3");
                award3.setSponsorCode("000001");
                versionHistory3.setSequenceOwner(award3);
                versionHistory3.setStatus(VersionStatus.ACTIVE);

                versionHistories.add(versionHistory1);
                versionHistories.add(versionHistory2);
                versionHistories.add(versionHistory3);
                return versionHistories;
            }
        }

        MockKrmsBase krmsTermService = new MockKrmsBase();
        MockAward award = new MockAward();
        award.setSequenceNumber(4);
        Award lastActiveVersion = (Award) krmsTermService.getLastActiveVersion(award);
        Assert.assertTrue(lastActiveVersion.getTitle().equalsIgnoreCase("Award3"));
    }

    @Test
    public void testDoSponsorAndPrimeSponsorMatch() {

        class MockKrmsBase extends KcKrmsJavaFunctionTermServiceBase {
            @Override
            protected List<VersionHistory> getVersionHistories(SequenceOwner<?> currentVersion, String versionNumber) {
                List<VersionHistory> versionHistories = new ArrayList<>();
                VersionHistory versionHistory1 = new VersionHistory();
                versionHistory1.setSequenceOwnerSequenceNumber(1);
                versionHistory1.setStatus(VersionStatus.ARCHIVED );
                MockAward award1 = new MockAward();
                award1.setSponsorCode("000340");
                award1.setPreAwardAuthorizedAmount(ScaleTwoDecimal.ONE_HUNDRED);
                versionHistory1.setSequenceOwner(award1);
                VersionHistory versionHistory2 = new VersionHistory();
                versionHistory2.setSequenceOwnerSequenceNumber(2);
                MockAward award2 = new MockAward();
                award2.setSponsorCode("000001");
                versionHistory2.setSequenceOwner(award2);
                versionHistory2.setStatus(VersionStatus.CANCELED);

                versionHistories.add(versionHistory1);
                versionHistories.add(versionHistory2);
                return versionHistories;
            }
        }

        Award award = new Award();
        Sponsor sponsor = new Sponsor();
        sponsor.setSponsorCode(NIH_SPONSOR_CODE);
        award.setSponsor(sponsor);
        award.setPrimeSponsor(sponsor);
        award.setPrimeSponsorCode(NIH_SPONSOR_CODE);

        MockKrmsBase termService = new MockKrmsBase();
        boolean result = termService.doSponsorAndPrimeSponsorMatch(award);
        Assert.assertTrue(result);

        award.setPrimeSponsorCode(NIH_SPONSOR_CODE);
        award.setSponsorCode(TEST_SPONSOR_CODE);
        result = termService.doSponsorAndPrimeSponsorMatch(award);
        Assert.assertFalse(result);

        award.setPrimeSponsorCode(NIH_SPONSOR_CODE);
        award.setSponsorCode(null);
        result = termService.doSponsorAndPrimeSponsorMatch(award);
        Assert.assertFalse(result);

        DevelopmentProposal developmentProposal = new DevelopmentProposal();
        sponsor = new Sponsor();
        sponsor.setSponsorCode(NIH_SPONSOR_CODE);
        developmentProposal.setSponsor(sponsor);
        developmentProposal.setPrimeSponsor(sponsor);
        developmentProposal.setSponsorCode(NIH_SPONSOR_CODE);
        developmentProposal.setPrimeSponsorCode(NIH_SPONSOR_CODE);

        result = termService.doSponsorAndPrimeSponsorMatch(developmentProposal);
        Assert.assertTrue(result);

        developmentProposal.setPrimeSponsorCode(NIH_SPONSOR_CODE);
        developmentProposal.setSponsorCode(TEST_SPONSOR_CODE);
        result = termService.doSponsorAndPrimeSponsorMatch(developmentProposal);
        Assert.assertFalse(result);

        developmentProposal.setPrimeSponsorCode(NIH_SPONSOR_CODE);
        developmentProposal.setSponsorCode(null);
        result = termService.doSponsorAndPrimeSponsorMatch(developmentProposal);
        Assert.assertFalse(result);

        MockIP ip = new MockIP();
        ip.setSponsorCode(NIH_SPONSOR_CODE);
        ip.setPrimeSponsorCode(NIH_SPONSOR_CODE);

        result = termService.doSponsorAndPrimeSponsorMatch(ip);
        Assert.assertTrue(result);

        ip.setPrimeSponsorCode(NIH_SPONSOR_CODE);
        ip.setSponsorCode(TEST_SPONSOR_CODE);
        result = termService.doSponsorAndPrimeSponsorMatch(ip);
        Assert.assertFalse(result);

        ip.setPrimeSponsorCode(NIH_SPONSOR_CODE);
        ip.setSponsorCode(null);
        result = termService.doSponsorAndPrimeSponsorMatch(ip);
        Assert.assertFalse(result);
    }

    @Test
    public void testDoesPrimeSponsorTypeMatch() {

        class MockKrmsBase extends KcKrmsJavaFunctionTermServiceBase {
            @Override
            protected List<VersionHistory> getVersionHistories(SequenceOwner<?> currentVersion, String versionNumber) {
                List<VersionHistory> versionHistories = new ArrayList<>();
                VersionHistory versionHistory1 = new VersionHistory();
                versionHistory1.setSequenceOwnerSequenceNumber(1);
                versionHistory1.setStatus(VersionStatus.ARCHIVED );
                MockAward award1 = new MockAward();
                award1.setSponsorCode("000340");
                award1.setPreAwardAuthorizedAmount(ScaleTwoDecimal.ONE_HUNDRED);
                versionHistory1.setSequenceOwner(award1);
                VersionHistory versionHistory2 = new VersionHistory();
                versionHistory2.setSequenceOwnerSequenceNumber(2);
                MockAward award2 = new MockAward();
                award2.setSponsorCode("000001");
                versionHistory2.setSequenceOwner(award2);
                versionHistory2.setStatus(VersionStatus.CANCELED);

                versionHistories.add(versionHistory1);
                versionHistories.add(versionHistory2);
                return versionHistories;
            }
        }

        MockKrmsBase termService = new MockKrmsBase();
        termService.setBusinessObjectService(businessObjectService);
        setSponsorExpectation();
        
        Award award = new Award();
        award.setPrimeSponsorCode(NIH_SPONSOR_CODE);
		assertEquals(true, termService.doesPrimeSponsorTypeMatch(award, FEDERAL_SPONSOR_TYPE_CODE));


        award.setPrimeSponsorCode(STATE_SPONSOR_CODE);
		assertEquals(false, termService.doesPrimeSponsorTypeMatch(award, FEDERAL_SPONSOR_TYPE_CODE));

        award.setPrimeSponsorCode(null);
		assertEquals(false, termService.doesPrimeSponsorTypeMatch(award, FEDERAL_SPONSOR_TYPE_CODE));
        
        DevelopmentProposal developmentProposal = new DevelopmentProposal();
        developmentProposal.setPrimeSponsorCode(NIH_SPONSOR_CODE);
		assertEquals(true, termService.doesPrimeSponsorTypeMatch(developmentProposal, FEDERAL_SPONSOR_TYPE_CODE));
		
		developmentProposal.setPrimeSponsorCode(STATE_SPONSOR_CODE);
		assertEquals(false, termService.doesPrimeSponsorTypeMatch(developmentProposal, FEDERAL_SPONSOR_TYPE_CODE));

		developmentProposal.setPrimeSponsorCode(null);
		assertEquals(false, termService.doesPrimeSponsorTypeMatch(developmentProposal, FEDERAL_SPONSOR_TYPE_CODE));

        MockIP ip = new MockIP();
        ip.setPrimeSponsorCode(NIH_SPONSOR_CODE);

        ip.setPrimeSponsorCode(NIH_SPONSOR_CODE);
		assertEquals(true, termService.doesPrimeSponsorTypeMatch(ip, FEDERAL_SPONSOR_TYPE_CODE));
		
		ip.setPrimeSponsorCode(STATE_SPONSOR_CODE);
		assertEquals(false, termService.doesPrimeSponsorTypeMatch(ip, FEDERAL_SPONSOR_TYPE_CODE));

		ip.setPrimeSponsorCode(null);
		assertEquals(false, termService.doesPrimeSponsorTypeMatch(ip, FEDERAL_SPONSOR_TYPE_CODE));
    }
        
    protected void setSponsorExpectation() {
		context.checking(new Expectations() {{
			{
				exactly(3).of(businessObjectService).findBySinglePrimaryKey(Sponsor.class, NIH_SPONSOR_CODE);
				will(returnValue(getSponsor(NIH_SPONSOR_CODE, FEDERAL_SPONSOR_TYPE_CODE)));
				exactly(3).of(businessObjectService).findBySinglePrimaryKey(Sponsor.class, STATE_SPONSOR_CODE);
				will(returnValue(getSponsor(STATE_SPONSOR_CODE, STATE_SPONSOR_TYPE_CODE)));
			}
		}});
    }
    
    protected Sponsor getSponsor(String sponsorCode, String sponsorTypeCode) {
    	Sponsor sponsor = new Sponsor();
    	sponsor.setSponsorCode(sponsorCode);
    	sponsor.setSponsorTypeCode(sponsorTypeCode);
    	return sponsor;
    }

}
