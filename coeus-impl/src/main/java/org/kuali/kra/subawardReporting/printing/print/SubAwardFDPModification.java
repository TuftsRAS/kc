/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.subawardReporting.printing.print;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.kuali.kra.award.printing.schema.AwardType;
import org.kuali.kra.subaward.printing.schema.SubContractDataDocument;

import static org.kuali.coeus.sys.framework.util.PdfBoxUtils.setField;
import static org.kuali.coeus.sys.framework.util.PdfBoxUtils.setFieldAppearance;

public class SubAwardFDPModification extends SubawardFdp {

    private static final String NO = "N";

    @Override
    protected void setSubrecipientInfo(PDDocument document, SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail) {
        if (StringUtils.isNotBlank(subcontractDetail.getSubcontractorName())) {
            setFieldAppearance(document, ModificationPdf.Field.SUBRECIPIENT.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
            setField(document, ModificationPdf.Field.SUBRECIPIENT.getfName(), subcontractDetail.getSubcontractorName());
        }

        if (StringUtils.isNotBlank(subcontractDetail.getSiteInvestigatorEmail())) {
            setFieldAppearance(document, ModificationPdf.Field.SUBEMAIL.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
            setField(document, ModificationPdf.Field.SUBEMAIL.getfName(), subcontractDetail.getSiteInvestigatorEmail());
        }

        if (StringUtils.isNotBlank(subcontractDetail.getSiteInvestigator())) {
            setFieldAppearance(document, ModificationPdf.Field.SUBPI.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
            setField(document, ModificationPdf.Field.SUBPI.getfName(), subcontractDetail.getSiteInvestigator());
        }
    }

    @Override
    protected void setPteInfo(PDDocument document, SubContractDataDocument xmlObject) {
        final SubContractDataDocument.SubContractData.PrimeRecipientContacts primeRecipientContacts = xmlObject.getSubContractData().getPrimeRecipientContacts() != null ? xmlObject.getSubContractData().getPrimeRecipientContacts() : SubContractDataDocument.SubContractData.PrimeRecipientContacts.Factory.newInstance();
        final SubContractDataDocument.SubContractData.PrimePrincipalInvestigator primePrincipalInvestigator = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getPrimePrincipalInvestigatorArray()) ? xmlObject.getSubContractData().getPrimePrincipalInvestigatorArray()[0] : SubContractDataDocument.SubContractData.PrimePrincipalInvestigator.Factory.newInstance();

        if (primeRecipientContacts.getRequisitionerOrgDetails() != null) {
            setFieldAppearance(document, ModificationPdf.Field.PTE_ENTITY_NAME.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
            setField(document, ModificationPdf.Field.PTE_ENTITY_NAME.getfName(), primeRecipientContacts.getRequisitionerOrgDetails().getOrganizationName());
        }

        if (primePrincipalInvestigator.getPersonDetailsType() != null) {
            if (StringUtils.isNotBlank(primePrincipalInvestigator.getPersonDetailsType().getFullName())) {
                setFieldAppearance(document, ModificationPdf.Field.PTEPI.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
                setField(document, ModificationPdf.Field.PTEPI.getfName(), primePrincipalInvestigator.getPersonDetailsType().getFullName());
            }

            if (StringUtils.isNotBlank(primePrincipalInvestigator.getPersonDetailsType().getFullName())) {
                setFieldAppearance(document, ModificationPdf.Field.PTE_EMAIL.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
                setField(document, ModificationPdf.Field.PTE_EMAIL.getfName(), primePrincipalInvestigator.getPersonDetailsType().getEmailAddress());
            }
        }
    }

    @Override
    protected void setAwardSubawardNumbers(PDDocument document, SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail, AwardType award) {
        setFieldAppearance(document, ModificationPdf.Field.PTE_FEDERAL_AWARD_NO.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
        setField(document, ModificationPdf.Field.PTE_FEDERAL_AWARD_NO.getfName(), award.getAwardDetails().getAwardHeader().getSponsorAwardNumber());

        if (award.getAwardDetails().getAwardHeader().getSponsorDescription() != null) {
            setFieldAppearance(document, ModificationPdf.Field.FEDERAL_AWARDING_AGENCY.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
            setField(document, ModificationPdf.Field.FEDERAL_AWARDING_AGENCY.getfName(), award.getAwardDetails().getAwardHeader().getSponsorDescription());
        } else {
            setField(document, ModificationPdf.Field.FEDERAL_AWARDING_AGENCY.getfName(), "");
        }
    }

    @Override
    protected void setTitle(PDDocument pdfDocument, AwardType award) {
        setFieldAppearance(pdfDocument, ModificationPdf.Field.PROJECT_TITLE.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
        setField(pdfDocument, ModificationPdf.Field.PROJECT_TITLE.getfName(), award.getAwardDetails().getAwardHeader().getTitle());
    }

    @Override
    protected void setDates(PDDocument document, SubContractDataDocument xmlObject) {
        final SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail = xmlObject.getSubContractData().getSubcontractDetail() != null ?
                xmlObject.getSubContractData().getSubcontractDetail() : SubContractDataDocument.SubContractData.SubcontractDetail.Factory.newInstance();
        final SubContractDataDocument.SubContractData.SubcontractAmountInfo amountInfo = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getSubcontractAmountInfoArray()) ?
                xmlObject.getSubContractData().getSubcontractAmountInfoArray()[0] : SubContractDataDocument.SubContractData.SubcontractAmountInfo.Factory.newInstance();

        //purposefully mapping the period of performance start date to the subaward start date
        if (subcontractDetail.getStartDate() != null) {
            setFieldAppearance(document, ModificationPdf.Field.START_DATE.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
            setField(document, ModificationPdf.Field.START_DATE.getfName(), formatDate(subcontractDetail.getStartDate().getTime()));
        }

        if (amountInfo.getPerformanceEndDate() != null) {
            setFieldAppearance(document, ModificationPdf.Field.END_DATE.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
            setField(document, ModificationPdf.Field.END_DATE.getfName(), formatDate(amountInfo.getPerformanceEndDate().getTime()));
        }

        if (amountInfo.getModificationEffectiveDate() != null) {
            setFieldAppearance(document, ModificationPdf.Field.EFFECTIVE_DATE.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
            setField(document, ModificationPdf.Field.EFFECTIVE_DATE.getfName(), formatDate(amountInfo.getModificationEffectiveDate().getTime()));
        }

        if (amountInfo.getModificationNumber() != null) {
            setFieldAppearance(document, ModificationPdf.Field.AMENDMENT_NUMBER.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
            setField(document, ModificationPdf.Field.AMENDMENT_NUMBER.getfName(), amountInfo.getModificationNumber());
        }
    }

    @Override
    protected void setAmounts(PDDocument document, SubContractDataDocument xmlObject) {
        final SubContractDataDocument.SubContractData.SubcontractAmountInfo amountInfo = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getSubcontractAmountInfoArray()) ? xmlObject.getSubContractData().getSubcontractAmountInfoArray()[0] : SubContractDataDocument.SubContractData.SubcontractAmountInfo.Factory.newInstance();

        if (amountInfo.getObligatedChange() != null) {
            setFieldAppearance(document, ModificationPdf.Field.AMOUNT_FUNDED_THIS_ACTION.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
            setField(document, ModificationPdf.Field.AMOUNT_FUNDED_THIS_ACTION.getfName(), amountInfo.getObligatedChange().toPlainString());
        }

        if (amountInfo.getObligatedAmount() != null) {
            setFieldAppearance(document, ModificationPdf.Field.TOTAL_FEDERAL_FUNDS_OBLIGATED.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
            setField(document, ModificationPdf.Field.TOTAL_FEDERAL_FUNDS_OBLIGATED.getfName(), amountInfo.getObligatedAmount().toPlainString());
        }
    }

    @Override
    protected void setTermsAndConditions(PDDocument document, SubContractDataDocument xmlObject) {
        final SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail = xmlObject.getSubContractData().getSubcontractDetail() != null ?
                xmlObject.getSubContractData().getSubcontractDetail() : SubContractDataDocument.SubContractData.SubcontractDetail.Factory.newInstance();
        if (subcontractDetail.getComments() != null) {
            setFieldAppearance(document, ModificationPdf.Field.ACTION.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
            setField(document, ModificationPdf.Field.ACTION.getfName(), subcontractDetail.getComments());
        }
    }

    @Override
    protected void setMiscellaneousItems(PDDocument document, SubContractDataDocument xmlObject) {
        final SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail = xmlObject.getSubContractData().getSubcontractDetail() != null ?
                xmlObject.getSubContractData().getSubcontractDetail() : SubContractDataDocument.SubContractData.SubcontractDetail.Factory.newInstance();
        final SubContractDataDocument.SubContractData.SubcontractTemplateInfo templateInfo = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getSubcontractTemplateInfoArray()) ? xmlObject.getSubContractData().getSubcontractTemplateInfoArray(0) : SubContractDataDocument.SubContractData.SubcontractTemplateInfo.Factory.newInstance();

        if (subcontractDetail.getFsrsSubawardNumber() != null) {
            setFieldAppearance(document, ModificationPdf.Field.SUBAWARD_NUMBER.getfName(), ModificationPdf.Field.DEFAULT_APPEARANCE_STR_FONT_8);
            setField(document, ModificationPdf.Field.SUBAWARD_NUMBER.getfName(), subcontractDetail.getFsrsSubawardNumber());
        }

        setField(document, ModificationPdf.Field.SUBJECTO_FFATA.getfName(), subcontractDetail.getFFATA() == null ||
                subcontractDetail.getFFATA().equalsIgnoreCase(NO) ? ModificationPdf.Field.NO : ModificationPdf.Field.YES);
        setField(document, ModificationPdf.Field.CARRYOVER_RADIO_BUTTON.getfName(), templateInfo.getAutomaticCarryForward() == null ||
                templateInfo.getAutomaticCarryForward().equalsIgnoreCase(NO) ? ModificationPdf.Field.CARRYOVER_NO : ModificationPdf.Field.CARRYOVER_YES);

    }
}
