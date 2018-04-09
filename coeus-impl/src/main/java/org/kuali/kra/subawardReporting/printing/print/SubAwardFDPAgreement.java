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
import org.kuali.kra.subaward.bo.FinalStatementDue;
import org.kuali.kra.subaward.printing.schema.SubContractDataDocument;

import static org.kuali.coeus.sys.framework.util.PdfBoxUtils.hideField;
import static org.kuali.coeus.sys.framework.util.PdfBoxUtils.setField;

public class SubAwardFDPAgreement extends SubawardFdp {


    @Override
    protected void setSubrecipientInfo(PDDocument document, SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail) {
        if (StringUtils.isNotBlank(subcontractDetail.getSubcontractorName())) {
            setField(document, AgreementPdf.Field.SUBRECIPIENT.getfName(), subcontractDetail.getSubcontractorName());
        }

        if (StringUtils.isNotBlank(subcontractDetail.getSiteInvestigator())) {
            setField(document, AgreementPdf.Field.SUB_PI.getfName(), subcontractDetail.getSiteInvestigator());
        }
    }

    @Override
    protected void setMiscellaneousItems(PDDocument pdfDocument, SubContractDataDocument xmlObject) {

    }

    @Override
    protected void setPteInfo(PDDocument document, SubContractDataDocument xmlObject) {

        final SubContractDataDocument.SubContractData.PrimeRecipientContacts primeRecipientContacts = xmlObject.getSubContractData().getPrimeRecipientContacts() != null ? xmlObject.getSubContractData().getPrimeRecipientContacts() : SubContractDataDocument.SubContractData.PrimeRecipientContacts.Factory.newInstance();
        final SubContractDataDocument.SubContractData.PrimePrincipalInvestigator primePrincipalInvestigator = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getPrimePrincipalInvestigatorArray()) ? xmlObject.getSubContractData().getPrimePrincipalInvestigatorArray()[0] : SubContractDataDocument.SubContractData.PrimePrincipalInvestigator.Factory.newInstance();

        if (primeRecipientContacts.getRequisitionerOrgDetails() != null) {
            setField(document, AgreementPdf.Field.PASS_THROUGH_ENTITY.getfName(), primeRecipientContacts.getRequisitionerOrgDetails().getOrganizationName());
        }

        if (primePrincipalInvestigator.getPersonDetailsType() != null && StringUtils.isNotBlank(primePrincipalInvestigator.getPersonDetailsType().getFullName())) {
            setField(document, AgreementPdf.Field.PTE_PI.getfName(), primePrincipalInvestigator.getPersonDetailsType().getFullName());
        }
    }

    @Override
    protected void setAwardSubawardNumbers(PDDocument document, SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail, AwardType award) {

        if (award.getAwardDetails() != null && award.getAwardDetails().getAwardHeader() != null && StringUtils.isNotBlank(award.getAwardDetails().getAwardHeader().getSponsorAwardNumber())) {
            setField(document, AgreementPdf.Field.PTE_FEDERAL_AWARD_NO.getfName(), award.getAwardDetails().getAwardHeader().getSponsorAwardNumber());
        }

        if (StringUtils.isNotBlank(subcontractDetail.getFsrsSubawardNumber())) {
            setField(document, AgreementPdf.Field.SUBAWARD_NO.getfName(), subcontractDetail.getFsrsSubawardNumber());
        }
    }

    @Override
    protected void setTitle(PDDocument document, AwardType award) {
        if (award.getAwardDetails() != null && award.getAwardDetails().getAwardHeader() != null && StringUtils.isNotBlank(award.getAwardDetails().getAwardHeader().getTitle())) {
            setField(document, AgreementPdf.Field.PROJECT_TITLE.getfName(), award.getAwardDetails().getAwardHeader().getTitle());
        }
    }

    @Override
    protected void setDates(PDDocument document, SubContractDataDocument xmlObject) {
        final SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail = xmlObject.getSubContractData().getSubcontractDetail() != null ? xmlObject.getSubContractData().getSubcontractDetail() : SubContractDataDocument.SubContractData.SubcontractDetail.Factory.newInstance();
        final SubContractDataDocument.SubContractData.SubcontractAmountInfo amountInfo = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getSubcontractAmountInfoArray()) ? xmlObject.getSubContractData().getSubcontractAmountInfoArray()[0] : SubContractDataDocument.SubContractData.SubcontractAmountInfo.Factory.newInstance();

        //purposefully mapping the period of performance start date to the subaward start date
        if (subcontractDetail.getStartDate() != null) {
            setField(document, AgreementPdf.Field.POP_START.getfName(), formatDate(subcontractDetail.getStartDate().getTime()));
        }

        if (amountInfo.getPerformanceEndDate() != null) {
            setField(document, AgreementPdf.Field.POP_END.getfName(), formatDate(amountInfo.getPerformanceEndDate().getTime()));
        }

        hideField(document, AgreementPdf.Field.PROJECT_START_TIP.getfName());
        if (subcontractDetail.getStartDate() != null) {
            setField(document, AgreementPdf.Field.PROJECT_START.getfName(), formatDate(subcontractDetail.getStartDate().getTime()));
        }

        hideField(document, AgreementPdf.Field.PROJECT_END_TIP.getfName());
        if (subcontractDetail.getEndDate() != null) {
            setField(document, AgreementPdf.Field.PROJECT_END.getfName(), formatDate(subcontractDetail.getEndDate().getTime()));
        }
    }

    @Override
    protected void setAmounts(PDDocument document, SubContractDataDocument xmlObject) {
        final SubContractDataDocument.SubContractData.SubcontractAmountInfo amountInfo = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getSubcontractAmountInfoArray()) ? xmlObject.getSubContractData().getSubcontractAmountInfoArray()[0] : SubContractDataDocument.SubContractData.SubcontractAmountInfo.Factory.newInstance();

        if (amountInfo.getObligatedAmount() != null) {
            setField(document, AgreementPdf.Field.AMOUNT_FUNDED_THIS_ACTION.getfName(), amountInfo.getObligatedAmount().toPlainString());
        }

        if (amountInfo.getAnticipatedAmount() != null) {
            setField(document, AgreementPdf.Field.INCREMENTALLY_ESTIMATED_TOTAL.getfName(), amountInfo.getAnticipatedAmount().toPlainString());
        }
    }

    @Override
    protected void setTermsAndConditions(PDDocument document, SubContractDataDocument xmlObject) {
        final SubContractDataDocument.SubContractData.OtherConfigInfo configInfo = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getOtherConfigInfoArray()) ? xmlObject.getSubContractData().getOtherConfigInfoArray(0) : SubContractDataDocument.SubContractData.OtherConfigInfo.Factory.newInstance();
        final SubContractDataDocument.SubContractData.SubcontractTemplateInfo templateInfo = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getSubcontractTemplateInfoArray()) ? xmlObject.getSubContractData().getSubcontractTemplateInfoArray(0) : SubContractDataDocument.SubContractData.SubcontractTemplateInfo.Factory.newInstance();

        final SubContractDataDocument.SubContractData.PrimeAuthorizedOfficial primeAuthorizedOfficial = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getPrimeAuthorizedOfficialArray()) ? xmlObject.getSubContractData().getPrimeAuthorizedOfficialArray()[0] : SubContractDataDocument.SubContractData.PrimeAuthorizedOfficial.Factory.newInstance();
        final SubContractDataDocument.SubContractData.PrimePrincipalInvestigator primePrincipalInvestigator = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getPrimePrincipalInvestigatorArray()) ? xmlObject.getSubContractData().getPrimePrincipalInvestigatorArray()[0] : SubContractDataDocument.SubContractData.PrimePrincipalInvestigator.Factory.newInstance();
        final SubContractDataDocument.SubContractData.PrimeAdministrativeContact primeAdministrativeContact = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getPrimeAdministrativeContactArray()) ? xmlObject.getSubContractData().getPrimeAdministrativeContactArray()[0] : SubContractDataDocument.SubContractData.PrimeAdministrativeContact.Factory.newInstance();
        final SubContractDataDocument.SubContractData.PrimeFinancialContact primeFinancialContact = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getPrimeFinancialContactArray()) ? xmlObject.getSubContractData().getPrimeFinancialContactArray()[0] : SubContractDataDocument.SubContractData.PrimeFinancialContact.Factory.newInstance();

        setField(document, AgreementPdf.Field.TERM_2_CONTACT.getfName(), getTermContactFromCode(templateInfo.getInvoiceOrPaymentContactCd(), primeAuthorizedOfficial, primePrincipalInvestigator, primeAdministrativeContact, primeFinancialContact));
        setField(document, AgreementPdf.Field.TERM_3_CONTACT.getfName(), getTermContactFromCode(templateInfo.getFinalStmtOfCostsContactCd(), primeAuthorizedOfficial, primePrincipalInvestigator, primeAdministrativeContact, primeFinancialContact));

        setField(document, AgreementPdf.Field.PROJECT_BUDGET_INVOICE_DROPDOWN.getfName(), "");
        if (StringUtils.isNotBlank(templateInfo.getFinalStatementDueCd())) {
            setField(document, AgreementPdf.Field.PROJECT_BUDGET_INVOICE_DROPDOWN.getfName(), FinalStatementDue.PTE.getCode().equals(templateInfo.getFinalStatementDueCd()) ? AgreementPdf.Field.PROJECT_PERIOD_END_DATE_VALUE : AgreementPdf.Field.BUDGET_PERIOD_END_DATE_VALUE);
        }

        setField(document, AgreementPdf.Field.TERM_6_CONTACT.getfName(), getTermContactFromCode(templateInfo.getChangeRequestsContactCd(), primeAuthorizedOfficial, primePrincipalInvestigator, primeAdministrativeContact, primeFinancialContact));
        setField(document, AgreementPdf.Field.UNI_BI_MOD_DROPOWN.getfName(), configInfo.getFdpSubawardBilateralAgreements() ? AgreementPdf.Field.BILATERALLY_VALUE : AgreementPdf.Field.UNILATERALLY_VALUE);
        setField(document, AgreementPdf.Field.TERM_7_CONTACT.getfName(), getTermContactFromCode(templateInfo.getChangeRequestsContactCd(), primeAuthorizedOfficial, primePrincipalInvestigator, primeAdministrativeContact, primeFinancialContact));
        setField(document, AgreementPdf.Field.TERM_9_CONTACT.getfName(), getTermContactFromCode(templateInfo.getTerminationContactCd(), primeAuthorizedOfficial, primePrincipalInvestigator, primeAdministrativeContact, primeFinancialContact));
    }


    private String getTermContactFromCode(String contactCode,
                                          SubContractDataDocument.SubContractData.PrimeAuthorizedOfficial primeAuthorizedOfficial, SubContractDataDocument.SubContractData.PrimePrincipalInvestigator primePrincipalInvestigator,
                                          SubContractDataDocument.SubContractData.PrimeAdministrativeContact primeAdministrativeContact, SubContractDataDocument.SubContractData.PrimeFinancialContact primeFinancialContact) {
        final String termContact;
        if (StringUtils.isBlank(contactCode)) {
            termContact = "";
        } else if (contactCode.equals(primeAuthorizedOfficial.getContactTypeCode())) {
            termContact = AgreementPdf.Field.TERM_CONTACT_AUTH;
        } else if (contactCode.equals(primePrincipalInvestigator.getContactTypeCode())) {
            termContact = AgreementPdf.Field.TERM_CONTACT_PI;
        } else if (contactCode.equals(primeAdministrativeContact.getContactTypeCode())) {
            termContact = AgreementPdf.Field.TERM_CONTACT_ADMIN;
        } else if (contactCode.equals(primeFinancialContact.getContactTypeCode())) {
            termContact = AgreementPdf.Field.TERM_CONTACT_FINANCIAL;
        } else {
            termContact = "";
        }
        return termContact;
    }

}
