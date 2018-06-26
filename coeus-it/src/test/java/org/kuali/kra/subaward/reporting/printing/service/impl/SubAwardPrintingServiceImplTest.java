/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.subaward.reporting.printing.service.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.coeus.common.framework.print.AttachmentDataSource;
import org.kuali.coeus.common.framework.sponsor.Sponsor;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.AwardCfda;
import org.kuali.kra.subaward.bo.*;
import org.kuali.kra.subaward.reporting.printing.service.SubAwardPrintingService;
import org.kuali.kra.test.infrastructure.KcIntegrationTestBase;

import java.sql.Date;


public class SubAwardPrintingServiceImplTest extends KcIntegrationTestBase {

    private SubAwardPrintingService subAwardPrintingService;

    @Before
    public void setupServices() {
        subAwardPrintingService = KcServiceLocator.getService(SubAwardPrintingService.class);
    }

    @Test
    public void test_fdp_agreement() {
        final SubAwardPrintAgreement agreement = new SubAwardPrintAgreement();

        agreement.setFundingSource("1");
        agreement.deselectAllItems();
        agreement.setAttachment4(false);
        agreement.setFdpType("fdpAgreement");

        final AttachmentDataSource pdf = subAwardPrintingService.printSubAwardFDPReport(agreement, createSubAward());
        Assert.assertTrue(pdf != null && ArrayUtils.isNotEmpty(pdf.getData()));
    }

    @Test
    public void test_fdp_modification() {
        final SubAwardPrintAgreement agreement = new SubAwardPrintAgreement();

        agreement.setFundingSource("1");
        agreement.deselectAllItems();
        agreement.setAttachment4(false);
        agreement.setFdpType("fdpModification");

        final AttachmentDataSource pdf = subAwardPrintingService.printSubAwardFDPReport(agreement, createSubAward());
        Assert.assertTrue(pdf != null && ArrayUtils.isNotEmpty(pdf.getData()));
    }

    @Test
    public void test_fdp_agreement_with_other_attachments() {
        final SubAwardPrintAgreement agreement = new SubAwardPrintAgreement();

        agreement.setFundingSource("1");
        agreement.setAfosrSponsor(true);
        agreement.setAmrmcSponsor(true);
        agreement.setAmraaSponsor(true);
        agreement.setAroSponsor(true);
        agreement.setAttachment3A(true);
        agreement.setAttachment3B(true);
        agreement.setAttachment3BPage2(true);
        agreement.setAttachment4(true);
        agreement.setDoeSponsor(true);
        agreement.setEpaSponsor(true);
        agreement.setNasaSponsor(true);
        agreement.setNihSponsor(true);
        agreement.setNsfSponsor(true);
        agreement.setOnrSponsor(true);
        agreement.setUsdaSponsor(true);
        agreement.setFdpType("fdpAgreement");

        final AttachmentDataSource pdf = subAwardPrintingService.printSubAwardFDPReport(agreement, createSubAward());
        Assert.assertTrue(pdf != null && ArrayUtils.isNotEmpty(pdf.getData()));
    }

    protected SubAward createSubAward() {
        final SubAward subAward = new SubAward();

        subAward.setOrganizationId("000140");
        subAward.setStatusCode(1);
        subAward.setSubAwardTypeCode(1);
        subAward.setRequisitionerId("1");
        subAward.setPurchaseOrderNum("1");
        subAward.setTotalAmountReleased(ScaleTwoDecimal.ZERO);
        subAward.setTotalAnticipatedAmount(ScaleTwoDecimal.ZERO);
        subAward.setTotalAvailableAmount(ScaleTwoDecimal.ZERO);
        subAward.setTotalObligatedAmount(ScaleTwoDecimal.ZERO);
        subAward.setFfataRequired(Boolean.TRUE);
        subAward.setStartDate(new Date(System.currentTimeMillis()));
        subAward.setEndDate(new Date(System.currentTimeMillis()));

        final SubAwardAmountInfo subAwardAmountInfo = new SubAwardAmountInfo();
        subAwardAmountInfo.setEffectiveDate(new Date(System.currentTimeMillis()));
        subAwardAmountInfo.setObligatedChange(new ScaleTwoDecimal(150));
        subAwardAmountInfo.setAnticipatedChange(new ScaleTwoDecimal(200));
        subAwardAmountInfo.setModificationTypeCode("RESBOOT1000");
        subAwardAmountInfo.refreshReferenceObject("modificationType");

        subAward.getSubAwardAmountInfoList().add(subAwardAmountInfo);

        SubAwardAmountReleased subAwardAmountReleased = new SubAwardAmountReleased();
        subAwardAmountReleased.setInvoiceNumber("1") ;
        subAwardAmountReleased.setStartDate(new Date(System.currentTimeMillis()));
        subAwardAmountReleased.setEndDate(new Date(System.currentTimeMillis()));
        subAwardAmountReleased.setEffectiveDate(new Date(System.currentTimeMillis()));
        subAwardAmountReleased.setAmountReleased(new ScaleTwoDecimal(100));

        subAward.getSubAwardAmountReleasedList().add(subAwardAmountReleased);

        final SubAwardContact subAwardContact = new SubAwardContact();
        subAwardContact.setRolodexId(1);
        subAwardContact.setContactTypeCode("1");

        subAward.setSubAwardContact(subAwardContact);
        subAward.getSubAwardContactsList().add(subAwardContact);

        final SubAwardCloseout subAwardCloseout = new SubAwardCloseout();
        subAwardCloseout.setCloseoutTypeCode(1);
        subAwardCloseout.setDateRequested(new Date(2012, 1, 1));
        subAwardCloseout.setDateFollowup(new Date(2012, 2, 15));

        subAward.setSubAwardCloseout(subAwardCloseout);
        subAward.getSubAwardCloseoutList().add(subAwardCloseout);

        final Sponsor sponsor = new Sponsor();
        sponsor.setSponsorName("A sponsor");

        final Award award = new Award();
        award.setAwardId(1183316613046L);
        award.setAwardNumber("1234");
        award.setTitle("A title");
        award.setSponsorAwardNumber("4567");
        award.setSponsor(sponsor);

        AwardCfda cfda = new AwardCfda();
        cfda.setCfdaNumber("cfda1234");
        cfda.setAwardId(award.getAwardId());
        cfda.setAward(award);
        cfda.setAwardNumber(award.getAwardNumber());

        award.getAwardCfdas().add(cfda);
        award.setFainId("A fain Id");
        award.setNoticeDate(new Date(1));
        award.setPrimeSponsor(sponsor);

        final SubAwardFundingSource subAwardFundingSource = new SubAwardFundingSource();
        subAwardFundingSource.setSubAwardFundingSourceId(1);
        subAwardFundingSource.setAwardId(1183316613046L);
        subAwardFundingSource.setAward(award);

        subAward.setSubAwardFundingSource(subAwardFundingSource);
        subAward.getSubAwardFundingSourceList().add(subAwardFundingSource);

        final SubAwardTemplateInfo subAwardTemplateInfo = new SubAwardTemplateInfo();
        subAwardTemplateInfo.setFcio(Boolean.TRUE);
        subAwardTemplateInfo.setIncludesCostSharing(Boolean.TRUE);
        subAwardTemplateInfo.setrAndD(Boolean.TRUE);

        subAward.getSubAwardTemplateInfo().add(subAwardTemplateInfo);

        return subAward;
    }
}
