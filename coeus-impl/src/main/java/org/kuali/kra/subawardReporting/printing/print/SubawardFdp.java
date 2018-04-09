/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
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
import org.apache.xmlbeans.XmlObject;
import org.kuali.coeus.common.framework.print.AbstractPrint;
import org.kuali.coeus.common.framework.print.PrintingException;
import org.kuali.coeus.sys.framework.util.CollectionUtils;
import org.kuali.coeus.sys.framework.util.PdfBoxUtils;
import org.kuali.kra.award.printing.schema.AwardType;
import org.kuali.kra.subaward.bo.*;
import org.kuali.kra.subaward.printing.schema.SubContractDataDocument;
import org.kuali.kra.subaward.reporting.printing.SubAwardPrintType;
import org.kuali.kra.subaward.reporting.printing.service.SubAwardPrintingService;
import org.springframework.core.io.Resource;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.kuali.coeus.sys.framework.util.CollectionUtils.entriesToMap;
import static org.kuali.coeus.sys.framework.util.CollectionUtils.entry;
import static org.kuali.coeus.sys.framework.util.PdfBoxUtils.*;

public abstract class SubawardFdp extends AbstractPrint {

    private static final String COPYRIGHTS_GRANT_CODE = "1";
    private static final String COPYRIGHTS_SHALL_GRANT_CODE = "2";

    private static final String MM_DD_YYYY = "MM/dd/yyyy";

    private static final List<String> FORM_ORDER = Stream.of(AgreementForm.FDP_AGREEMENT.getId(), SupplementalFormsForAgreement.FDP_ATTACHMENT_CERTIFICATION.getId(),
            ModificationForm.FDP_MODIFICATION.getId(), "FDP Modification Unilateral",
            Attachment2Form.FDP_AFOSR.getId(), Attachment2Form.FDP_AMRAA.getId(), Attachment2Form.FDP_AMRMC.getId(), Attachment2Form.FDP_ARO.getId(),
            Attachment2Form.FDP_DOE.getId(), Attachment2Form.FDP_EPA.getId(), Attachment2Form.FDP_NASA.getId(), Attachment2Form.FDP_NIH.getId(),
            Attachment2Form.FDP_NSF.getId(), Attachment2Form.FDP_ONR.getId(), Attachment2Form.FDP_USDA.getId(),
            Attachment3aForm.ATTACHMENT_3A.getId(), Attachment3bForm.ATTACHMENT_3B.getId(), Attachment3bPage2Form.ATTACHMENT_3B_PAGE2.getId(), "FDP_ATT_4")
            .collect(Collectors.toList());


    private Map<String, Resource> pdfForms;

    @Override
    public Map<String,Source> getXSLTemplateWithBookmarks() {
        return getSelectedTemplates().stream()
                .map(s -> CollectionUtils.<String, Source>entry(s.getFormId(), new StreamSource(s.getAttachmentContent() != null ? new ByteArrayInputStream(s.getAttachmentContent()) :
                        new ByteArrayInputStream(new byte[] {}))))
                .collect(entriesToMap());
    }

    private Collection<SubAwardForms> getSelectedTemplates() {
        return (Collection<SubAwardForms> ) getReportParameters().get(SubAwardPrintingService.SELECTED_TEMPLATES);
    }

    @Override
    public Map<String, Resource> getPdfForms() {
        return pdfForms;
    }

    public void setPdfForms(Map<String, Resource> pdfForms) {
        this.pdfForms = pdfForms;
    }

    @Override
    public Map<String, byte[]> fillPdfForms(Map<String, Resource> pdfForms, Map<String, XmlObject> xml) {
        final Collection<String> selected = getSelectedTemplates().stream()
                .map(SubAwardForms::getFormId)
                .collect(Collectors.toList());

        if (selected.stream().anyMatch(form -> form.equalsIgnoreCase(AgreementForm.FDP_AGREEMENT.getId()))) {
            selected.add(SupplementalFormsForAgreement.FDP_ATTACHMENT_CERTIFICATION.getId());
        }

        return pdfForms.entrySet().stream()
                .filter(pdfForm -> selected.contains(pdfForm.getKey()))
                .map(selectedForm -> {
                    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                         PDDocument pdfDocument = PDDocument.load(selectedForm.getValue().getInputStream())) {

                        doMissingDefaultResourcesWorkaround(pdfDocument.getDocumentCatalog().getAcroForm().getDefaultResources());

                        final SubContractDataDocument xmlObject = (SubContractDataDocument) xml.get(SubAwardPrintType.SUB_AWARD_FDP_TEMPLATE.getSubAwardPrintType());
                        if (xmlObject != null) {
                            fillTemplateOrAttachment(selectedForm, pdfDocument, xmlObject);
                        }

                        PdfBoxUtils.flatten(pdfDocument);
                        pdfDocument.save(out);
                        return entry(selectedForm.getKey(), out.toByteArray());
                    } catch (IOException e1) {
                        throw new PrintingException(e1);
                    }
                }).collect(entriesToMap());
    }

    public void fillTemplateOrAttachment(Map.Entry<String, Resource> selectedForm, PDDocument pdfDocument, SubContractDataDocument xmlObject) {
        final boolean agreement = Stream.of(AgreementForm.values())
                .anyMatch(form -> form.getId().equals(selectedForm.getKey()));

        final boolean certification = Stream.of(SupplementalFormsForAgreement.values())
                .anyMatch(form -> form.getId().equals(selectedForm.getKey()));

        final boolean attachment3a = Stream.of(Attachment3aForm.values())
                .anyMatch(form -> form.getId().equals(selectedForm.getKey()));

        final boolean attachment3b = Stream.of(Attachment3bForm.values())
                .anyMatch(form -> form.getId().equals(selectedForm.getKey()));

        final boolean attachment3bPage2 = Stream.of(Attachment3bPage2Form.values())
                .anyMatch(form -> form.getId().equals(selectedForm.getKey()));

        final boolean modification = Stream.of(ModificationForm.values())
                .anyMatch(form -> form.getId().equals(selectedForm.getKey()));

        if (agreement) {
            fillAgreementForm(pdfDocument, xmlObject);
        } else if (certification) {
            fillCertificationForm(pdfDocument, xmlObject);
        } else if (attachment3a) {
            fillAttachment3aForm(pdfDocument, xmlObject);
        } else if (attachment3b) {
            fillAttachment3bForm(pdfDocument, xmlObject);
        } else if (attachment3bPage2) {
            fillAttachment3bPage2Form(pdfDocument, xmlObject);
        } else if (modification) {
            fillModificationForm(pdfDocument, xmlObject);
        }
        else {
            Stream.of(Attachment2Form.values())
                    .filter(attachment2Form -> attachment2Form.getId().equals(selectedForm.getKey()))
                    .findFirst()
                    .ifPresent(attachment2Form -> fillAttachment2Form(pdfDocument, xmlObject, attachment2Form));
        }
    }

    private void fillModificationForm(PDDocument pdfDocument, SubContractDataDocument xmlObject) {
        final SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail = xmlObject.getSubContractData().getSubcontractDetail() != null ? xmlObject.getSubContractData().getSubcontractDetail() :
                                                                                            SubContractDataDocument.SubContractData.SubcontractDetail.Factory.newInstance();
        final AwardType award = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getAwardArray()) ? xmlObject.getSubContractData().getAwardArray()[0] : AwardType.Factory.newInstance();
        
        setSubrecipientInfo(pdfDocument, subcontractDetail);
        setPteInfo(pdfDocument, xmlObject);
        setAwardSubawardNumbers(pdfDocument, subcontractDetail, award);
        setTitle(pdfDocument, award);
        setDates(pdfDocument, xmlObject);
        setAmounts(pdfDocument, xmlObject);
        setTermsAndConditions(pdfDocument, xmlObject);
        setMiscellaneousItems(pdfDocument, xmlObject);
    }

    protected abstract void setMiscellaneousItems(PDDocument pdfDocument, SubContractDataDocument xmlObject);

    private void fillAgreementForm(PDDocument document, SubContractDataDocument xmlObject) {
        final SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail = xmlObject.getSubContractData().getSubcontractDetail() != null ? xmlObject.getSubContractData().getSubcontractDetail() : SubContractDataDocument.SubContractData.SubcontractDetail.Factory.newInstance();
        final AwardType award = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getAwardArray()) ? xmlObject.getSubContractData().getAwardArray()[0] : AwardType.Factory.newInstance();

        setFederalAwardingAgency(document, award);
        setPteInfo(document, xmlObject);
        setSubrecipientInfo(document, subcontractDetail);
        setAwardSubawardNumbers(document, subcontractDetail, award);
        setTitle(document, award);
        setDates(document, xmlObject);
        setAmounts(document, xmlObject);
        setTermsAndConditions(document, xmlObject);
    }

    private void fillCertificationForm(PDDocument pdfDocument, SubContractDataDocument xmlObject) {
        final SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail = xmlObject.getSubContractData().getSubcontractDetail() != null ? xmlObject.getSubContractData().getSubcontractDetail() : SubContractDataDocument.SubContractData.SubcontractDetail.Factory.newInstance();
        setField(pdfDocument, AgreementCertificationPdf.Field.SUBAWARD_NO.getfName(), subcontractDetail.getFsrsSubawardNumber());
    }

    protected void setFederalAwardingAgency(PDDocument document, AwardType award) {

        if (award.getAwardDetails() != null && award.getAwardDetails().getAwardHeader() != null && StringUtils.isNotBlank(award.getAwardDetails().getAwardHeader().getSponsorDescription())) {
            setField(document, AgreementPdf.Field.FEDERAL_AWARDING_AGENCY.getfName(), award.getAwardDetails().getAwardHeader().getSponsorDescription());
        }
    }

    abstract protected void setPteInfo(PDDocument document, SubContractDataDocument xmlObject);

    abstract protected void setSubrecipientInfo(PDDocument document, SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail);

    abstract protected void setAwardSubawardNumbers(PDDocument document, SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail, AwardType award);

    abstract protected void setTitle(PDDocument document, AwardType award);

    abstract protected void setDates(PDDocument document, SubContractDataDocument xmlObject);

    abstract protected void setAmounts(PDDocument document, SubContractDataDocument xmlObject);

    abstract protected void setTermsAndConditions(PDDocument document, SubContractDataDocument xmlObject);

    private void fillAttachment2Form(PDDocument document, SubContractDataDocument xmlObject, Attachment2Form formId) {
        final Attachment2SponsorFormType type = formId.getSponsorFormType();
        final SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail = xmlObject.getSubContractData().getSubcontractDetail() != null ? xmlObject.getSubContractData().getSubcontractDetail() : SubContractDataDocument.SubContractData.SubcontractDetail.Factory.newInstance();
        final SubContractDataDocument.SubContractData.OtherConfigInfo configInfo = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getOtherConfigInfoArray()) ? xmlObject.getSubContractData().getOtherConfigInfoArray(0) : SubContractDataDocument.SubContractData.OtherConfigInfo.Factory.newInstance();
        final SubContractDataDocument.SubContractData.SubcontractTemplateInfo templateInfo = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getSubcontractTemplateInfoArray()) ? xmlObject.getSubContractData().getSubcontractTemplateInfoArray(0) : SubContractDataDocument.SubContractData.SubcontractTemplateInfo.Factory.newInstance();
        final AwardType award = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getAwardArray()) ? xmlObject.getSubContractData().getAwardArray(0) : AwardType.Factory.newInstance();

        final SubContractDataDocument.SubContractData.PrimeAuthorizedOfficial primeAuthorizedOfficial = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getPrimeAuthorizedOfficialArray()) ? xmlObject.getSubContractData().getPrimeAuthorizedOfficialArray()[0] : SubContractDataDocument.SubContractData.PrimeAuthorizedOfficial.Factory.newInstance();
        final SubContractDataDocument.SubContractData.PrimePrincipalInvestigator primePrincipalInvestigator = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getPrimePrincipalInvestigatorArray()) ? xmlObject.getSubContractData().getPrimePrincipalInvestigatorArray()[0] : SubContractDataDocument.SubContractData.PrimePrincipalInvestigator.Factory.newInstance();
        final SubContractDataDocument.SubContractData.PrimeAdministrativeContact primeAdministrativeContact = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getPrimeAdministrativeContactArray()) ? xmlObject.getSubContractData().getPrimeAdministrativeContactArray()[0] : SubContractDataDocument.SubContractData.PrimeAdministrativeContact.Factory.newInstance();
        final SubContractDataDocument.SubContractData.PrimeFinancialContact primeFinancialContact = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getPrimeFinancialContactArray()) ? xmlObject.getSubContractData().getPrimeFinancialContactArray()[0] : SubContractDataDocument.SubContractData.PrimeFinancialContact.Factory.newInstance();

        setHeaderInformation(document, subcontractDetail);
        setRequiredDataElements(document, subcontractDetail, templateInfo, award);
        setGenTermsAndConditions(document, configInfo, templateInfo, primeAuthorizedOfficial, primePrincipalInvestigator, primeAdministrativeContact, primeFinancialContact, type);
        setMpiInfo(document, templateInfo, type);
        setStcCopyrights(document, templateInfo);
        setDataSharingPubAccessPolicy(document, templateInfo);
        setPromotingObjectivityFcio(document, templateInfo, configInfo, type);
        setHumanAnimalSubjects(document, templateInfo, primeAuthorizedOfficial, primePrincipalInvestigator, primeAdministrativeContact, primeFinancialContact);
        setHumanPteVerification(document, templateInfo);
        setAnimalPteVerification(document, templateInfo);
        setHumanSubjectsDataExchange(document, templateInfo);
        setHumanSubjectsDataExchangeTerms(document, templateInfo);
        setAdditionalTerms(document, templateInfo);

    }

    private void setDataSharingPubAccessPolicy(PDDocument document, SubContractDataDocument.SubContractData.SubcontractTemplateInfo templateInfo) {
        if (DataSharing.ATTACHED.getCode().equals(templateInfo.getDataSharingCd())) {
            setField(document, Attachment2Pdf.Field.DATA_SHARING_PLAN.getfName(), true);
            setField(document, Attachment2Pdf.Field.DATA_SHARING_DROPDOWN.getfName(), Attachment2Pdf.Field.DATA_SHARING_ATTACHED);
        } else if (DataSharing.PROVIDED_UPON_REQUEST.getCode().equals(templateInfo.getDataSharingCd())) {
            setField(document, Attachment2Pdf.Field.DATA_SHARING_PLAN.getfName(), true);
            setField(document, Attachment2Pdf.Field.DATA_SHARING_DROPDOWN.getfName(), Attachment2Pdf.Field.DATA_SHARING_PROVIDED_UPON_REQUEST);
        } else {
            setField(document, Attachment2Pdf.Field.DATA_SHARING_DROPDOWN.getfName(), "");
        }
    }

    private void setPromotingObjectivityFcio(PDDocument document, SubContractDataDocument.SubContractData.SubcontractTemplateInfo templateInfo, SubContractDataDocument.SubContractData.OtherConfigInfo configInfo, Attachment2SponsorFormType type) {
        if (FcioSubrecipientPolicy.PTE.getCode().equals(templateInfo.getFcioSubrecPolicyCd())) {
            setField(document, Attachment2Pdf.Field.FCOI_PTE_DROPDOWN.getfName(), Attachment2Pdf.Field.FCOI_PTE);
        } else if (FcioSubrecipientPolicy.SUBRECIPIENT.getCode().equals(templateInfo.getFcioSubrecPolicyCd())) {
            setField(document, Attachment2Pdf.Field.FCOI_PTE_DROPDOWN.getfName(), Attachment2Pdf.Field.FCOI_SUBRECIPIENT);
        } else {
            setField(document, Attachment2Pdf.Field.FCOI_PTE_DROPDOWN.getfName(), "");
        }

        if (type == Attachment2SponsorFormType.NIH) {
            setField(document, Attachment2Pdf.Field.SPONSOR_AGENCY.getfName(), configInfo.getFdpNihFCoiGuidance());
        } else if (type == Attachment2SponsorFormType.NSF) {
            setField(document, Attachment2Pdf.Field.SPONSOR_AGENCY.getfName(), configInfo.getFdpNsfFCoiGuidance());
        } else if (type == Attachment2SponsorFormType.NASA) {
            setField(document, Attachment2Pdf.Field.SPONSOR_AGENCY.getfName(), configInfo.getFdpNasaFCoiGuidance());
        } else if (type == Attachment2SponsorFormType.ONR) {
            setField(document, Attachment2Pdf.Field.SPONSOR_AGENCY.getfName(), configInfo.getFdpOnrFCoiGuidance());
        } else if (type == Attachment2SponsorFormType.ARO) {
            setField(document, Attachment2Pdf.Field.SPONSOR_AGENCY.getfName(), configInfo.getFdpAroFCoiGuidance());
        } else if (type == Attachment2SponsorFormType.AFOSR) {
            setField(document, Attachment2Pdf.Field.SPONSOR_AGENCY.getfName(), configInfo.getFdpAfosrFCoiGuidance());
        } else if (type == Attachment2SponsorFormType.EPA) {
            setField(document, Attachment2Pdf.Field.SPONSOR_AGENCY.getfName(), configInfo.getFdpEpaFCoiGuidance());
        } else if (type == Attachment2SponsorFormType.AMRMC) {
            setField(document, Attachment2Pdf.Field.SPONSOR_AGENCY.getfName(), configInfo.getFdpAmrmcFCoiGuidance());
        } else if (type == Attachment2SponsorFormType.AMRAA) {
            setField(document, Attachment2Pdf.Field.SPONSOR_AGENCY.getfName(), configInfo.getFdpAmraaFCoiGuidance());
        } else if (type == Attachment2SponsorFormType.USDA) {
            setField(document, Attachment2Pdf.Field.SPONSOR_AGENCY.getfName(), configInfo.getFdpUsdaFCoiGuidance());
        }

        setField(document, Attachment2Pdf.Field.FCOI_OTHER_SPONSOR_AGENCY.getfName(), "");
    }

    private void setHumanSubjectsDataExchangeTerms(PDDocument document, SubContractDataDocument.SubContractData.SubcontractTemplateInfo templateInfo) {
        hideField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_TERMS_TIP.getfName());
        if (fromYN(templateInfo.getHumanFlag()) && (HumanDataExchangeAgreement.SUBRECIPIENT_TO_PTE.getCode().equals(templateInfo.getHumanDataExchangeAgreeCd()) ||
                HumanDataExchangeAgreement.PTE_TO_SUBRECIPIENT.getCode().equals(templateInfo.getHumanDataExchangeAgreeCd()) ||
                HumanDataExchangeAgreement.FROM_BOTH_PTE_AND_SUBRECIPIENT.getCode().equals(templateInfo.getHumanDataExchangeAgreeCd()))) {

            showField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_TERMS_LABEL.getfName());
            showField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_TERMS_DROPDOWN.getfName());

            if (HumanDataExchangeTerms.ADDITIONAL_TERMS.getCode().equals(templateInfo.getHumanDataExchangeTermsCd())) {
                setField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_TERMS_DROPDOWN.getfName(), Attachment2Pdf.Field.HUMAN_DATA_TERMS_BELOW);
            } else if (HumanDataExchangeTerms.SEPARATE_AGREEMENT.getCode().equals(templateInfo.getHumanDataExchangeTermsCd())) {
                setField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_TERMS_DROPDOWN.getfName(), Attachment2Pdf.Field.HUMAN_DATA_TERMS_SEPARATE_AGREEMENT);
            }
        }
    }

    private void setAdditionalTerms(PDDocument document, SubContractDataDocument.SubContractData.SubcontractTemplateInfo templateInfo) {
        if (StringUtils.isNotBlank(templateInfo.getAdditionalTerms())) {
            setField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_ADD_TERMS_TEXT.getfName(), templateInfo.getAdditionalTerms());
        }
    }

    private void setHumanSubjectsDataExchange(PDDocument document, SubContractDataDocument.SubContractData.SubcontractTemplateInfo templateInfo) {
        setField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_APPLICABLE.getfName(), Attachment2Pdf.Field.HUMAN_DATA_NOT_APPLICABLE);

        if (fromYN(templateInfo.getHumanFlag())) {
            hideField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_BLANK.getfName());
            showField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_APPLICABLE.getfName());
            showField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_EXCHANGE_LABEL.getfName());
            showField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_EXCHANGE2_LABEL.getfName());
            showField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_SUB_TO_PTE.getfName());
            showField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_PTE_TO_SUB.getfName());

            if (HumanDataExchangeAgreement.NOT_APPLICABLE.getCode().equals(templateInfo.getHumanDataExchangeAgreeCd())) {
                setField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_APPLICABLE.getfName(), Attachment2Pdf.Field.HUMAN_DATA_NOT_APPLICABLE);
            } else if (HumanDataExchangeAgreement.SUBRECIPIENT_TO_PTE.getCode().equals(templateInfo.getHumanDataExchangeAgreeCd()) ||
                    HumanDataExchangeAgreement.PTE_TO_SUBRECIPIENT.getCode().equals(templateInfo.getHumanDataExchangeAgreeCd()) ||
                    HumanDataExchangeAgreement.FROM_BOTH_PTE_AND_SUBRECIPIENT.getCode().equals(templateInfo.getHumanDataExchangeAgreeCd())) {
                setField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_APPLICABLE.getfName(), Attachment2Pdf.Field.HUMAN_DATA_APPLICABLE);
            }

            setField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_SUB_TO_PTE.getfName(), HumanDataExchangeAgreement.SUBRECIPIENT_TO_PTE.getCode().equals(templateInfo.getHumanDataExchangeAgreeCd()) || HumanDataExchangeAgreement.FROM_BOTH_PTE_AND_SUBRECIPIENT.getCode().equals(templateInfo.getHumanDataExchangeAgreeCd()));
            setField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_DATA_PTE_TO_SUB.getfName(), HumanDataExchangeAgreement.PTE_TO_SUBRECIPIENT.getCode().equals(templateInfo.getHumanDataExchangeAgreeCd()) || HumanDataExchangeAgreement.FROM_BOTH_PTE_AND_SUBRECIPIENT.getCode().equals(templateInfo.getHumanDataExchangeAgreeCd()));

        }
    }

    private void setAnimalPteVerification(PDDocument document, SubContractDataDocument.SubContractData.SubcontractTemplateInfo templateInfo) {
        if (fromYN(templateInfo.getAnimalFlag())) {
            showField(document, Attachment2Pdf.Field.PTE_IACUC_DROPDOWN.getfName());
            if (PteSend.UPON_REQUEST.getCode().equals(templateInfo.getAnimalPteSendCd())) {
                setField(document, Attachment2Pdf.Field.PTE_IACUC_DROPDOWN.getfName(), Attachment2Pdf.Field.PTE_UPON_REQUEST);
            } else if (PteSend.PRIOR_TO_EXECUTION.getCode().equals(templateInfo.getAnimalPteSendCd())) {
                setField(document, Attachment2Pdf.Field.PTE_IACUC_DROPDOWN.getfName(), Attachment2Pdf.Field.PTE_PRIOR_TO_EXECUTION_OF_THIS_AGREEMENT);
            } else if (PteSend.PRIOR_TO_EXECUTION_AND_ANNUALLY.getCode().equals(templateInfo.getAnimalPteSendCd())) {
                setField(document, Attachment2Pdf.Field.PTE_IACUC_DROPDOWN.getfName(), Attachment2Pdf.Field.PTE_PRIOR_TO_EXECUTION_OF_THIS_AGREEMENT_ANNUALLY);
            } else if (PteSend.NOT_REQUIRED.getCode().equals(templateInfo.getAnimalPteSendCd())) {
                setField(document, Attachment2Pdf.Field.PTE_IACUC_DROPDOWN.getfName(), Attachment2Pdf.Field.PTE_NOT_REQUIRED);
            } else {
                setField(document, Attachment2Pdf.Field.PTE_IACUC_DROPDOWN.getfName(), "");
            }

            //Work Involving Humans or Animal Subjects (PTE requires verification - Animal section) - Not Required Reason
            if (PteSend.NOT_REQUIRED.getCode().equals(templateInfo.getAnimalPteSendCd())) {
                showField(document, Attachment2Pdf.Field.PTE_IACUC_NOT_REQUIRED_REASON.getfName());
                final AnimalPteSendNotRequiredReason hReason = AnimalPteSendNotRequiredReason.fromCode(templateInfo.getAnimalPteNrCd());
                if (hReason != null) {
                    setField(document, Attachment2Pdf.Field.PTE_IACUC_NOT_REQUIRED_REASON.getfName(), hReason.getDescription());
                }
            }
        }
    }

    private void setHumanPteVerification(PDDocument document, SubContractDataDocument.SubContractData.SubcontractTemplateInfo templateInfo) {
        if (fromYN(templateInfo.getHumanFlag())) {
            showField(document, Attachment2Pdf.Field.PTE_IRB_DROPDOWN.getfName());
            if (PteSend.UPON_REQUEST.getCode().equals(templateInfo.getHumanPteSendCd())) {
                setField(document, Attachment2Pdf.Field.PTE_IRB_DROPDOWN.getfName(), Attachment2Pdf.Field.PTE_UPON_REQUEST);
            } else if (PteSend.PRIOR_TO_EXECUTION.getCode().equals(templateInfo.getHumanPteSendCd())) {
                setField(document, Attachment2Pdf.Field.PTE_IRB_DROPDOWN.getfName(), Attachment2Pdf.Field.PTE_PRIOR_TO_EXECUTION_OF_THIS_AGREEMENT);
            } else if (PteSend.PRIOR_TO_EXECUTION_AND_ANNUALLY.getCode().equals(templateInfo.getHumanPteSendCd())) {
                setField(document, Attachment2Pdf.Field.PTE_IRB_DROPDOWN.getfName(), Attachment2Pdf.Field.PTE_PRIOR_TO_EXECUTION_OF_THIS_AGREEMENT_ANNUALLY);
            } else if (PteSend.NOT_REQUIRED.getCode().equals(templateInfo.getHumanPteSendCd())) {
                setField(document, Attachment2Pdf.Field.PTE_IRB_DROPDOWN.getfName(), Attachment2Pdf.Field.PTE_NOT_REQUIRED);
            } else {
                setField(document, Attachment2Pdf.Field.PTE_IRB_DROPDOWN.getfName(), "");
            }

            //Work Involving Humans or Animal Subjects (PTE requires verification - Human section) - Not Required Reason
            if (PteSend.NOT_REQUIRED.getCode().equals(templateInfo.getHumanPteSendCd())) {
                showField(document, Attachment2Pdf.Field.PTE_IRB_NOT_REQUIRED_REASON.getfName());
                final HumanPteSendNotRequiredReason hReason = HumanPteSendNotRequiredReason.fromCode(templateInfo.getHumanPteNrCd());
                if (hReason != null) {
                    setField(document, Attachment2Pdf.Field.PTE_IRB_NOT_REQUIRED_REASON.getfName(), hReason.getDescription());
                }
            }
        }
    }

    private void setHumanAnimalSubjects(PDDocument document, SubContractDataDocument.SubContractData.SubcontractTemplateInfo templateInfo,
                                        SubContractDataDocument.SubContractData.PrimeAuthorizedOfficial primeAuthorizedOfficial, SubContractDataDocument.SubContractData.PrimePrincipalInvestigator primePrincipalInvestigator,
                                        SubContractDataDocument.SubContractData.PrimeAdministrativeContact primeAdministrativeContact, SubContractDataDocument.SubContractData.PrimeFinancialContact primeFinancialContact) {
        final boolean human = fromYN(templateInfo.getHumanFlag());
        final boolean animal = fromYN(templateInfo.getAnimalFlag());

        hideField(document, Attachment2Pdf.Field.IRB_IACUC_LABEL.getfName());
        showField(document, Attachment2Pdf.Field.IRB_IACUC_BLANK_LABEL.getfName());

        setField(document, Attachment2Pdf.Field.HUMAN_SUBJECTS_CHECKBOX.getfName(), human);
        setField(document, Attachment2Pdf.Field.ANIMAL_SUBJECTS_CHECKBOX.getfName(), animal);
        setField(document, Attachment2Pdf.Field.NO_HUMAN_ANIMAL_SUBJECTS_CHECKBOX.getfName(), !animal && !human);

        if (human || animal) {
            showField(document, Attachment2Pdf.Field.IRB_IACUC_LABEL.getfName());
            showField(document, Attachment2Pdf.Field.PTE_REQUIRES_VERIFICATON_LABEL.getfName());
            hideField(document, Attachment2Pdf.Field.IRB_IACUC_BLANK_LABEL.getfName());
        }

        if (human) {
            showField(document, Attachment2Pdf.Field.PTE_IRB_LABEL.getfName());
        }

        if (animal) {
            showField(document, Attachment2Pdf.Field.PTE_IACUC_LABEL.getfName());
        }

        if (human || animal) {
            showField(document, Attachment2Pdf.Field.PTE_VERIFICATION_CONTACT.getfName());
            setField(document, Attachment2Pdf.Field.PTE_VERIFICATION_CONTACT.getfName(), "");

            final String pteVerContact;
            if (StringUtils.isBlank(templateInfo.getIrbIacucContactCd())) {
                pteVerContact = "";
            } else if (templateInfo.getIrbIacucContactCd().equals(primeAuthorizedOfficial.getContactTypeCode())) {
                pteVerContact = Attachment2Pdf.Field.VERIFICATION_CONTACT_AUTHORIZED_OFFICIAL_VALUE;
            } else if (templateInfo.getIrbIacucContactCd().equals(primePrincipalInvestigator.getContactTypeCode())) {
                pteVerContact = Attachment2Pdf.Field.VERIFICATION_CONTACT_PI_VALUE;
            } else if (templateInfo.getIrbIacucContactCd().equals(primeAdministrativeContact.getContactTypeCode())) {
                pteVerContact = Attachment2Pdf.Field.VERIFICATION_CONTACT_ADMINISTRATIVE_CONTACT_VALUE;
            } else if (templateInfo.getIrbIacucContactCd().equals(primeFinancialContact.getContactTypeCode())) {
                pteVerContact = Attachment2Pdf.Field.VERIFICATION_CONTACT_FINANCIAL_CONTACT_VALUE;
            } else {
                pteVerContact = "";
            }

            setField(document, Attachment2Pdf.Field.PTE_VERIFICATION_CONTACT.getfName(), pteVerContact);
        }
    }

    private void setStcCopyrights(PDDocument document, SubContractDataDocument.SubContractData.SubcontractTemplateInfo templateInfo) {
        if (COPYRIGHTS_GRANT_CODE.equals(templateInfo.getCopyRights())) {
            setField(document, Attachment2Pdf.Field.COPYRIGHT.getfName(), Attachment2Pdf.Field.COPYRIGHT_SUBRECIPIENT_GRANTS);
        } else if (COPYRIGHTS_SHALL_GRANT_CODE.equals(templateInfo.getCopyRights())) {
            setField(document, Attachment2Pdf.Field.COPYRIGHT.getfName(), Attachment2Pdf.Field.COPYRIGHT_SUBRECIPIENT_SHALL_GRANT);
        } else {
            setField(document, Attachment2Pdf.Field.COPYRIGHT.getfName(), "");
        }
    }

    private void setMpiInfo(PDDocument document, SubContractDataDocument.SubContractData.SubcontractTemplateInfo templateInfo, Attachment2SponsorFormType type) {
        toggleFieldVisibility(document, Attachment2Pdf.Field.MPI_SECTION_LABEL.getfName(), type != Attachment2SponsorFormType.NIH);
        hideField(document, Attachment2Pdf.Field.MPI_SECTION_HELP.getfName());
        toggleFieldVisibility(document, Attachment2Pdf.Field.MPI_SECTION_SELECT.getfName(), type != Attachment2SponsorFormType.NIH);
        toggleFieldVisibility(document, Attachment2Pdf.Field.MPI_SECTION_BLANK.getfName(), type == Attachment2SponsorFormType.NIH);
        if (type == Attachment2SponsorFormType.NIH) {
            if (fromYN(templateInfo.getMpiAward())) {
                setField(document, Attachment2Pdf.Field.MPI_SECTION_SELECT.getfName(), Attachment2Pdf.Field.MPI_IS_SUBJECT);
                showField(document, Attachment2Pdf.Field.MPI_PLAN_SELECT.getfName());
                final MpiLeadershipPlan mpiLeadershipPlan = MpiLeadershipPlan.fromCode(templateInfo.getMpiLeadershipPlan());
                if (MpiLeadershipPlan.ATTACHED == mpiLeadershipPlan) {
                    setField(document, Attachment2Pdf.Field.MPI_PLAN_SELECT.getfName(), Attachment2Pdf.Field.MPI_PLAN_ATTACHED);
                } else if (MpiLeadershipPlan.REQUEST == mpiLeadershipPlan) {
                    setField(document, Attachment2Pdf.Field.MPI_PLAN_SELECT.getfName(), Attachment2Pdf.Field.MPI_PLAN_UPON_REQUEST);
                } else {
                    setField(document, Attachment2Pdf.Field.MPI_PLAN_SELECT.getfName(), "");
                }
            } else {
                setField(document, Attachment2Pdf.Field.MPI_SECTION_SELECT.getfName(), Attachment2Pdf.Field.MPI_NOT_SUBJECT);
            }
        }
    }

    private void setGenTermsAndConditions(PDDocument document, SubContractDataDocument.SubContractData.OtherConfigInfo configInfo, SubContractDataDocument.SubContractData.SubcontractTemplateInfo templateInfo,
                                          SubContractDataDocument.SubContractData.PrimeAuthorizedOfficial primeAuthorizedOfficial, SubContractDataDocument.SubContractData.PrimePrincipalInvestigator primePrincipalInvestigator,
                                          SubContractDataDocument.SubContractData.PrimeAdministrativeContact primeAdministrativeContact, SubContractDataDocument.SubContractData.PrimeFinancialContact primeFinancialContact,
                                          Attachment2SponsorFormType type) {

        final String nceContact;
        if (StringUtils.isBlank(templateInfo.getNoCostExtensionContactCd())) {
            nceContact = "";
        } else if (templateInfo.getNoCostExtensionContactCd().equals(primeAuthorizedOfficial.getContactTypeCode())) {
            nceContact = Attachment2Pdf.Field.NCE_CONTACT_AUTHORIZED_OFFICIAL;
        } else if (templateInfo.getNoCostExtensionContactCd().equals(primePrincipalInvestigator.getContactTypeCode())) {
            nceContact = Attachment2Pdf.Field.NCE_CONTACT_PI;
        } else if (templateInfo.getNoCostExtensionContactCd().equals(primeAdministrativeContact.getContactTypeCode())) {
            nceContact = Attachment2Pdf.Field.NCE_CONTACT_ADMINISTRATIVE;
        } else if (templateInfo.getNoCostExtensionContactCd().equals(primeFinancialContact.getContactTypeCode())) {
            nceContact = Attachment2Pdf.Field.NCE_CONTACT_FINANCIAL;
        } else {
            nceContact = "";
        }

        //General Terms and Conditions
        if (type == Attachment2SponsorFormType.NIH) {
            setGenTermsAndConditions1To4(document, configInfo.getFdpNihPolicy(), configInfo.getFdpNihCfr(), configInfo.getFdpNihGrantsPolicyStatement(), configInfo.getFdpNihInterimResearchTerms(), nceContact);
        } else if (type == Attachment2SponsorFormType.NSF) {
            setGenTermsAndConditions1To4(document, configInfo.getFdpNsfPolicy(), configInfo.getFdpNsfCfr(), configInfo.getFdpNsfGrantsPolicyStatement(), configInfo.getFdpNsfInterimResearchTerms(), nceContact);
        } else if (type == Attachment2SponsorFormType.NASA) {
            setGenTermsAndConditions1To4(document, configInfo.getFdpNasaPolicy(), configInfo.getFdpNasaCfr(), configInfo.getFdpNasaGrantsPolicyStatement(), configInfo.getFdpNasaInterimResearchTerms(), nceContact);
        } else if (type == Attachment2SponsorFormType.ONR) {
            setGenTermsAndConditions1To4(document, configInfo.getFdpOnrPolicy(), configInfo.getFdpOnrCfr(), configInfo.getFdpOnrGrantsPolicyStatement(), configInfo.getFdpOnrInterimResearchTerms(), nceContact);
        } else if (type == Attachment2SponsorFormType.ARO) {
            setGenTermsAndConditions1To4(document, configInfo.getFdpAroPolicy(), configInfo.getFdpAroCfr(), configInfo.getFdpAroGrantsPolicyStatement(), configInfo.getFdpAroInterimResearchTerms(), nceContact);
        } else if (type == Attachment2SponsorFormType.AFOSR) {
            setGenTermsAndConditions1To4(document, configInfo.getFdpAfosrPolicy(), configInfo.getFdpAfosrCfr(), configInfo.getFdpAfosrGrantsPolicyStatement(), configInfo.getFdpAfosrInterimResearchTerms(), nceContact);
        } else if (type == Attachment2SponsorFormType.EPA) {
            setGenTermsAndConditions1To4(document, configInfo.getFdpEpaPolicy(), configInfo.getFdpEpaCfr(), configInfo.getFdpEpaGrantsPolicyStatement(), configInfo.getFdpEpaInterimResearchTerms(), nceContact);
        } else if (type == Attachment2SponsorFormType.AMRMC) {
            setGenTermsAndConditions1To4(document, configInfo.getFdpAmrmcPolicy(), configInfo.getFdpAmrmcCfr(),  configInfo.getFdpAmrmcGrantsPolicyStatement(), configInfo.getFdpAmrmcInterimResearchTerms(), nceContact);
        } else if (type == Attachment2SponsorFormType.AMRAA) {
            setGenTermsAndConditions1To4(document, configInfo.getFdpAmraaPolicy(), configInfo.getFdpAmraaCfr(), configInfo.getFdpAmraaGrantsPolicyStatement(), configInfo.getFdpAmraaInterimResearchTerms(), nceContact);
        } else if (type == Attachment2SponsorFormType.USDA) {
            setGenTermsAndConditions1To4(document, configInfo.getFdpUsdaPolicy(), configInfo.getFdpUsdaCfr(), configInfo.getFdpUsdaGrantsPolicyStatement(), configInfo.getFdpUsdaInterimResearchTerms(), nceContact);
        } else {
            setGenTermsAndConditions1To4(document, "", "", "", "", "");
        }

        if (fromYN(templateInfo.getTreatmentPrgmIncomeAdditive())) {
            setField(document, Attachment2Pdf.Field.TREATMENT_OF_PROGRAM_INCOME.getfName(), Attachment2Pdf.Field.TPI_ADDITIVE_VALUE);
        } else {
            showField(document, Attachment2Pdf.Field.TREATMENT_OF_PROGRAM_INCOME_OTHER_SPECIFY.getfName());
            setField(document, Attachment2Pdf.Field.TREATMENT_OF_PROGRAM_INCOME.getfName(), Attachment2Pdf.Field.TPI_OTHER_VALUE);

            if (StringUtils.isNotBlank(templateInfo.getTreatmentOfIncome())) {
                setField(document, Attachment2Pdf.Field.TREATMENT_OF_PROGRAM_INCOME_OTHER_SPECIFY.getfName(), templateInfo.getTreatmentOfIncome());
            }
        }
    }

    private void setGenTermsAndConditions1To4(PDDocument document, String policy, String cfr, String policyStatement, String intTerms, String nceContact) {

        setFieldAppearance(document, Attachment2Pdf.Field.FEDERAL_AWARD_CONDITIONS.getfName(), StringUtils.length(policyStatement) < 100 ? Attachment2Pdf.Field.DEFAULT_APPEARANCE_STR_FONT_10 : StringUtils.length(policyStatement) > 100 && StringUtils.length(policyStatement) < 175 ? Attachment2Pdf.Field.DEFAULT_APPEARANCE_STR_FONT_8 : Attachment2Pdf.Field.DEFAULT_APPEARANCE_STR_FONT_5);
        setField(document, Attachment2Pdf.Field.FEDERAL_AWARD_CONDITIONS.getfName(), policyStatement);
        if (StringUtils.isNotBlank(cfr)) {
            showField(document, Attachment2Pdf.Field.AGENCY_IMPLEMENTATION.getfName());
            setFieldAppearance(document, Attachment2Pdf.Field.AGENCY_IMPLEMENTATION.getfName(), StringUtils.length(cfr) < 16 ? Attachment2Pdf.Field.DEFAULT_APPEARANCE_STR_FONT_10 : StringUtils.length(cfr) > 16 && StringUtils.length(cfr) < 20 ? Attachment2Pdf.Field.DEFAULT_APPEARANCE_STR_FONT_8 : Attachment2Pdf.Field.DEFAULT_APPEARANCE_STR_FONT_5);
            setField(document, Attachment2Pdf.Field.AGENCY_IMPLEMENTATION.getfName(), cfr);
        }
        setFieldAppearance(document, Attachment2Pdf.Field.GRANTS_POLICY_STATEMENT.getfName(), StringUtils.length(policy) < 100 ? Attachment2Pdf.Field.DEFAULT_APPEARANCE_STR_FONT_10 : StringUtils.length(policy) > 100 && StringUtils.length(policy) < 175 ? Attachment2Pdf.Field.DEFAULT_APPEARANCE_STR_FONT_8 : Attachment2Pdf.Field.DEFAULT_APPEARANCE_STR_FONT_5);
        setField(document, Attachment2Pdf.Field.GRANTS_POLICY_STATEMENT.getfName(), policy);

        setFieldAppearance(document, Attachment2Pdf.Field.RES_TERMS_COND.getfName(), StringUtils.length(intTerms) < 75 ? Attachment2Pdf.Field.DEFAULT_APPEARANCE_STR_FONT_10 : StringUtils.length(intTerms) > 75 && StringUtils.length(intTerms) < 150 ? Attachment2Pdf.Field.DEFAULT_APPEARANCE_STR_FONT_8 : Attachment2Pdf.Field.DEFAULT_APPEARANCE_STR_FONT_5);
        setField(document, Attachment2Pdf.Field.RES_TERMS_COND.getfName(), intTerms);

        setField(document, Attachment2Pdf.Field.NO_COST_EXT_CONTACT.getfName(), nceContact);
    }

    private void setRequiredDataElements(PDDocument document, SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail, SubContractDataDocument.SubContractData.SubcontractTemplateInfo templateInfo, AwardType award) {

        setField(document, Attachment2Pdf.Field.DATA_ELEMENTS_REQUIRED.getfName(), Attachment2Pdf.Field.DE_ATTACHED);
        hideField(document, Attachment2Pdf.Field.DATA_ELEMENTS_TIP.getfName());
        hideField(document, Attachment2Pdf.Field.NOA_TIP.getfName());
        setField(document, Attachment2Pdf.Field.RESEARCH_AND_DEVELOPMENT.getfName(), fromYN(templateInfo.getRAndD()));
        setField(document, Attachment2Pdf.Field.SUBJECT_TO_FFATA.getfName(), fromYN(subcontractDetail.getFFATA()));

        if (award.getAwardDetails() != null && award.getAwardDetails().getOtherHeaderDetails() != null) {
            if (award.getAwardDetails().getOtherHeaderDetails().getFAID() != null) {
                setField(document, Attachment2Pdf.Field.FAID.getfName(), formatDate(award.getAwardDetails().getOtherHeaderDetails().getFAID().getTime()));
            }

            if (StringUtils.isNotBlank(award.getAwardDetails().getOtherHeaderDetails().getFAIN())) {
                setField(document, Attachment2Pdf.Field.FAIN.getfName(), award.getAwardDetails().getOtherHeaderDetails().getFAIN());
            }

            if (StringUtils.isNotBlank(award.getAwardDetails().getOtherHeaderDetails().getCFDANumber())) {
                setField(document, Attachment2Pdf.Field.CFDA.getfName(), award.getAwardDetails().getOtherHeaderDetails().getCFDANumber());
            }
        }
    }

    private void setHeaderInformation(PDDocument document, SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail) {
        setField(document, Attachment2Pdf.Field.SUBAWARD_NUMBER.getfName(), subcontractDetail.getFsrsSubawardNumber());
    }

    private void fillAttachment3aForm(PDDocument document, SubContractDataDocument xmlObject) {
        final SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail = xmlObject.getSubContractData().getSubcontractDetail() != null ? xmlObject.getSubContractData().getSubcontractDetail() : SubContractDataDocument.SubContractData.SubcontractDetail.Factory.newInstance();
        final SubContractDataDocument.SubContractData.PrimeRecipientContacts primeRecipientContacts = xmlObject.getSubContractData().getPrimeRecipientContacts() != null ? xmlObject.getSubContractData().getPrimeRecipientContacts() : SubContractDataDocument.SubContractData.PrimeRecipientContacts.Factory.newInstance();
        final SubContractDataDocument.SubContractData.PrimeAuthorizedOfficial primeAuthorizedOfficial = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getPrimeAuthorizedOfficialArray()) ? xmlObject.getSubContractData().getPrimeAuthorizedOfficialArray()[0] : SubContractDataDocument.SubContractData.PrimeAuthorizedOfficial.Factory.newInstance();
        final SubContractDataDocument.SubContractData.PrimePrincipalInvestigator primePrincipalInvestigator = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getPrimePrincipalInvestigatorArray()) ? xmlObject.getSubContractData().getPrimePrincipalInvestigatorArray()[0] : SubContractDataDocument.SubContractData.PrimePrincipalInvestigator.Factory.newInstance();
        final SubContractDataDocument.SubContractData.PrimeAdministrativeContact primeAdministrativeContact = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getPrimeAdministrativeContactArray()) ? xmlObject.getSubContractData().getPrimeAdministrativeContactArray()[0] : SubContractDataDocument.SubContractData.PrimeAdministrativeContact.Factory.newInstance();
        final SubContractDataDocument.SubContractData.PrimeFinancialContact primeFinancialContact = ArrayUtils.isNotEmpty(xmlObject.getSubContractData().getPrimeFinancialContactArray()) ? xmlObject.getSubContractData().getPrimeFinancialContactArray()[0] : SubContractDataDocument.SubContractData.PrimeFinancialContact.Factory.newInstance();

        setHeaderInformationAtt3a(document, subcontractDetail);
        setPteInfoAtt3a(document, primeRecipientContacts);
        setPteContacts(document, primeAuthorizedOfficial, primePrincipalInvestigator, primeAdministrativeContact, primeFinancialContact);
        setPteAddresses(document, primePrincipalInvestigator, primeAdministrativeContact, primeFinancialContact);
    }

    private void setHeaderInformationAtt3a(PDDocument document, SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail) {
        setField(document, Attachment3aPdf.Field.SUBAWARD_NUMBER.getfName(), subcontractDetail.getFsrsSubawardNumber());
    }

    private void setPteInfoAtt3a(PDDocument document, SubContractDataDocument.SubContractData.PrimeRecipientContacts primeRecipientContacts) {
        if (primeRecipientContacts.getRequisitionerOrgDetails() != null) {
            if (StringUtils.isNotBlank(primeRecipientContacts.getRequisitionerOrgDetails().getOrganizationName())) {
                setField(document, Attachment3aPdf.Field.PTE_INFO_ENTITY_NAME.getfName(), primeRecipientContacts.getRequisitionerOrgDetails().getOrganizationName());
            }
        }

        if (primeRecipientContacts.getOrgRolodexDetails() != null) {
            final String address = createAddress(primeRecipientContacts.getOrgRolodexDetails().getAddress1(), primeRecipientContacts.getOrgRolodexDetails().getAddress2(),
                    primeRecipientContacts.getOrgRolodexDetails().getAddress3(), primeRecipientContacts.getOrgRolodexDetails().getCity(),
                    primeRecipientContacts.getOrgRolodexDetails().getStateDescription(), primeRecipientContacts.getOrgRolodexDetails().getPincode());

            if (StringUtils.isNotBlank(address)) {
                setField(document, Attachment3aPdf.Field.PTE_INFO_LEGAL_ADDRESS.getfName(), address);
            }
        }
    }

    private void setPteContacts(PDDocument document, SubContractDataDocument.SubContractData.PrimeAuthorizedOfficial primeAuthorizedOfficial,
                                SubContractDataDocument.SubContractData.PrimePrincipalInvestigator primePrincipalInvestigator, SubContractDataDocument.SubContractData.PrimeAdministrativeContact primeAdministrativeContact,
                                SubContractDataDocument.SubContractData.PrimeFinancialContact primeFinancialContact) {

        if (StringUtils.isNotBlank(primeAuthorizedOfficial.getCentralEmail())) {
            setField(document, Attachment3aPdf.Field.PTE_CONTACTS_CENTRAL_EMAIL.getfName(), primeAuthorizedOfficial.getCentralEmail());
        }

        if (primePrincipalInvestigator.getPersonDetailsType() != null) {
            if (StringUtils.isNotBlank(primePrincipalInvestigator.getPersonDetailsType().getFullName())) {
                setField(document, Attachment3aPdf.Field.PTE_CONTACTS_PI_NAME.getfName(), primePrincipalInvestigator.getPersonDetailsType().getFullName());
            }

            if (StringUtils.isNotBlank(primePrincipalInvestigator.getPersonDetailsType().getEmailAddress())) {
                setField(document, Attachment3aPdf.Field.PTE_CONTACTS_PI_EMAIL.getfName(), primePrincipalInvestigator.getPersonDetailsType().getEmailAddress());
            }

            if (StringUtils.isNotBlank(primePrincipalInvestigator.getPersonDetailsType().getMobilePhoneNumber())) {
                setField(document, Attachment3aPdf.Field.PTE_CONTACTS_PI_PHONE.getfName(), primePrincipalInvestigator.getPersonDetailsType().getMobilePhoneNumber());
            }
        }
        if (primeAdministrativeContact.getRolodexDetails() != null) {
            if (StringUtils.isNotBlank(primeAdministrativeContact.getRolodexDetails().getRolodexName())) {
                setField(document, Attachment3aPdf.Field.PTE_CONTACTS_ADMIN_NAME.getfName(), primeAdministrativeContact.getRolodexDetails().getRolodexName());
            }

            if (StringUtils.isNotBlank(primeAdministrativeContact.getRolodexDetails().getEmail())) {
                setField(document, Attachment3aPdf.Field.PTE_CONTACTS_ADMIN_EMAIL.getfName(), primeAdministrativeContact.getRolodexDetails().getEmail());
            }

            if (StringUtils.isNotBlank(primeAdministrativeContact.getRolodexDetails().getPhoneNumber())) {
                setField(document, Attachment3aPdf.Field.PTE_CONTACTS_ADMIN_PHONE.getfName(), primeAdministrativeContact.getRolodexDetails().getPhoneNumber());
            }
        }

        if (StringUtils.isNotBlank(primeAdministrativeContact.getCoiContactEmail())) {
            setField(document, Attachment3aPdf.Field.PTE_CONTACTS_COI_EMAIL.getfName(), primeAdministrativeContact.getCoiContactEmail());
        }

        if (primeFinancialContact.getRolodexDetails() != null) {
            if (StringUtils.isNotBlank(primeFinancialContact.getRolodexDetails().getRolodexName())) {
                setField(document, Attachment3aPdf.Field.PTE_CONTACTS_FIN_NAME.getfName(), primeFinancialContact.getRolodexDetails().getRolodexName());
            }

            if (StringUtils.isNotBlank(primeFinancialContact.getRolodexDetails().getEmail())) {
                setField(document, Attachment3aPdf.Field.PTE_CONTACTS_FIN_EMAIL.getfName(), primeFinancialContact.getRolodexDetails().getEmail());
            }

            if (StringUtils.isNotBlank(primeFinancialContact.getRolodexDetails().getPhoneNumber())) {
                setField(document, Attachment3aPdf.Field.PTE_CONTACTS_FIN_PHONE.getfName(), primeFinancialContact.getRolodexDetails().getPhoneNumber());
            }
        }

        setField(document, Attachment3aPdf.Field.PTE_CONTACTS_EMAIL_INVOICES.getfName(), fromYN(primeFinancialContact.getInvoicesEmailed()) ? Attachment3aPdf.Field.EMAIL_INVOICES_TRUE : Attachment3aPdf.Field.EMAIL_INVOICES_FALSE);

        if (StringUtils.isNotBlank(primeFinancialContact.getInvoiceEmailDifferent())) {
            setField(document, Attachment3aPdf.Field.PTE_CONTACTS_INVOICE_EMAIL.getfName(), primeFinancialContact.getInvoiceEmailDifferent());
        }

        if (primeAuthorizedOfficial.getRolodexDetails() != null) {
            if (StringUtils.isNotBlank(primeAuthorizedOfficial.getRolodexDetails().getRolodexName())) {
                setField(document, Attachment3aPdf.Field.PTE_CONTACTS_AUTH_OFFICIAL_NAME.getfName(), primeAuthorizedOfficial.getRolodexDetails().getRolodexName());
            }

            if (StringUtils.isNotBlank(primeAuthorizedOfficial.getRolodexDetails().getEmail())) {
                setField(document, Attachment3aPdf.Field.PTE_CONTACTS_AUTH_OFFICIAL_EMAIL.getfName(), primeAuthorizedOfficial.getRolodexDetails().getEmail());
            }

            if (StringUtils.isNotBlank(primeAuthorizedOfficial.getRolodexDetails().getPhoneNumber())) {
                setField(document, Attachment3aPdf.Field.PTE_CONTACTS_AUTH_OFFCIAL_PHONE.getfName(), primeAuthorizedOfficial.getRolodexDetails().getPhoneNumber());
            }
        }

    }

    private void setPteAddresses(PDDocument document, SubContractDataDocument.SubContractData.PrimePrincipalInvestigator primePrincipalInvestigator,
                                 SubContractDataDocument.SubContractData.PrimeAdministrativeContact primeAdministrativeContact,
                                 SubContractDataDocument.SubContractData.PrimeFinancialContact primeFinancialContact) {

        if (primePrincipalInvestigator.getPersonDetailsType() != null) {
            final String piAddress = createAddress(primePrincipalInvestigator.getPersonDetailsType().getAddressLine1(), primePrincipalInvestigator.getPersonDetailsType().getAddressLine2(),
                    primePrincipalInvestigator.getPersonDetailsType().getAddressLine3(), primePrincipalInvestigator.getPersonDetailsType().getCity(),
                    primePrincipalInvestigator.getPersonDetailsType().getState(), primePrincipalInvestigator.getPersonDetailsType().getPostalCode());

            if (StringUtils.isNotBlank(piAddress)) {
                setField(document, Attachment3aPdf.Field.PTE_CONTACTS_PI_ADDRESS.getfName(), piAddress);
            }
        }

        if (primeAdministrativeContact.getRolodexDetails() != null) {
            final String adminAddress = createAddress(primeAdministrativeContact.getRolodexDetails().getAddress1(), primeAdministrativeContact.getRolodexDetails().getAddress2(),
                    primeAdministrativeContact.getRolodexDetails().getAddress3(), primeAdministrativeContact.getRolodexDetails().getCity(),
                    primeAdministrativeContact.getRolodexDetails().getStateDescription(), primeAdministrativeContact.getRolodexDetails().getPincode());

            if (StringUtils.isNotBlank(adminAddress)) {
                setField(document, Attachment3aPdf.Field.PTE_CONTACTS_ADMIN_ADDRESS.getfName(), adminAddress);
            }
        }
        if (StringUtils.isNotBlank(primeFinancialContact.getInvoiceAddressDifferent())) {
            setField(document, Attachment3aPdf.Field.PTE_CONTACTS_INVOICE_ADDRESS.getfName(), primeFinancialContact.getInvoiceAddressDifferent());
        } else if (primeFinancialContact.getRolodexDetails() != null) {
            final String invoiceAddress = createAddress(primeFinancialContact.getRolodexDetails().getAddress1(), primeFinancialContact.getRolodexDetails().getAddress2(),
                    primeFinancialContact.getRolodexDetails().getAddress3(), primeFinancialContact.getRolodexDetails().getCity(),
                    primeFinancialContact.getRolodexDetails().getStateDescription(), primeFinancialContact.getRolodexDetails().getPincode());

            if (StringUtils.isNotBlank(invoiceAddress)) {
                setField(document, Attachment3aPdf.Field.PTE_CONTACTS_INVOICE_ADDRESS.getfName(), invoiceAddress);
            }
        }
    }

    private String createAddress(String address1, String address2, String address3, String city, String state, String zip) {
        final StringBuilder address = new StringBuilder();
        if (StringUtils.isNotBlank(address1)) {
            address.append(address1);
            address.append("\n");
        }

        if (StringUtils.isNotBlank(address2)) {
            address.append(address2);
            address.append("\n");
        }

        if (StringUtils.isNotBlank(address3)) {
            address.append(address3);
            address.append("\n");
        }

        if (StringUtils.isNotBlank(city)) {
            address.append(city);
            address.append(" ");
        }

        if (StringUtils.isNotBlank(state)) {
            address.append(state);
            address.append(" ");
        }

        if (StringUtils.isNotBlank(zip)) {
            address.append(zip);
        }
        return address.toString();
    }

    private void fillAttachment3bForm(PDDocument document, SubContractDataDocument xmlObject) {
        final SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail = xmlObject.getSubContractData().getSubcontractDetail() != null ? xmlObject.getSubContractData().getSubcontractDetail() :
                SubContractDataDocument.SubContractData.SubcontractDetail.Factory.newInstance();

        setField(document, Attachment3bPdf.Field.SUBAWARD_NUMBER.getfName(), subcontractDetail.getFsrsSubawardNumber());
    }

    private void fillAttachment3bPage2Form(PDDocument document, SubContractDataDocument xmlObject) {
        final SubContractDataDocument.SubContractData.SubcontractDetail subcontractDetail = xmlObject.getSubContractData().getSubcontractDetail() != null ? xmlObject.getSubContractData().getSubcontractDetail() :
                SubContractDataDocument.SubContractData.SubcontractDetail.Factory.newInstance();

        setField(document, Attachment3bPage2Pdf.Field.SUBAWARD_NUMBER.getfName(), subcontractDetail.getFsrsSubawardNumber());

        if (StringUtils.isNotBlank(subcontractDetail.getSubcontractorName())) {
            setField(document, Attachment3bPage2Pdf.Field.ENTITY_NAME.getfName(), subcontractDetail.getSubcontractorName());
        }

        if (StringUtils.isNotBlank(subcontractDetail.getSiteInvestigator())) {
            setField(document, Attachment3bPage2Pdf.Field.PI_NAME.getfName(), subcontractDetail.getSiteInvestigator());
        }
    }

    @Override
    public Map<String, byte[]> sortPdfForms(Map<String, byte[]> forms) {
        final TreeMap<String, byte[]> sorted = new TreeMap<>(Comparator.comparing(FORM_ORDER::indexOf));
        sorted.putAll(forms);
        return sorted;
    }

    protected boolean fromYN(String s) {
        return "Y".equals(s);
    }

    protected String formatDate(Date date) {
        return new SimpleDateFormat(MM_DD_YYYY).format(date);
    }

    private enum AgreementForm {
        FDP_AGREEMENT("FDP Template");

        private final String id;

        AgreementForm(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    private enum ModificationForm {
        FDP_MODIFICATION("FDP Modification");

        private final String id;

        ModificationForm(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    private enum SupplementalFormsForAgreement {
        FDP_ATTACHMENT_CERTIFICATION("FDP Attachment 1 Certification");

        private final String id;

        SupplementalFormsForAgreement(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    static final class ModificationPdf {

        enum Field implements PdfField {
            SUBRECIPIENT("SubEntityName"),
            SUBEMAIL("SubEmail"),
            SUBPI("SubPI"),
            PTE_ENTITY_NAME ("PTEEntityName"),
            PTEPI ("PTEPI"),
            PTE_EMAIL ("PTEEmail"),
            PTE_FEDERAL_AWARD_NO ("PTEFederalAwardNo"),
            FEDERAL_AWARDING_AGENCY ("FederalAwardingAgency"),
            PROJECT_TITLE ("ProjectTitle"),
            START_DATE ("StartDate"),
            END_DATE ("EndDate"),
            EFFECTIVE_DATE ("EffectiveDate"),
            AMENDMENT_NUMBER ("AmendmentNumber"),
            AMOUNT_FUNDED_THIS_ACTION ("AmountFundedThisAction"),
            TOTAL_FEDERAL_FUNDS_OBLIGATED ("TotalFederalFundsObligated"),
            ACTION ("Action"),
            SUBAWARD_NUMBER ("SubawardNumber"),
            SUBJECTO_FFATA ("SubjectoFFATA", Stream.of(Field.YES, Field.NO).collect(Collectors.toSet())),
            CARRYOVER_RADIO_BUTTON("Carryover", Stream.of(Field.CARRYOVER_YES, Field.CARRYOVER_NO).collect(Collectors.toSet()));

            protected static final String DEFAULT_APPEARANCE_STR_FONT_8 = "/ArialMT 8 Tf 0 g";

            protected static final String CARRYOVER_YES = "CarryoverYes";
            protected static final String CARRYOVER_NO = "CarryoverNo";
            protected static final String YES = "Yes";
            protected static final String NO = "No";

            private final String fName;
            private final Set<String> values;
            private final boolean readOnly;

            Field(String fName, Set<String> values) {
                this.fName = fName;
                this.values = values;
                this.readOnly = false;
            }

            Field(String fName) {
                this.fName = fName;
                this.values = Collections.emptySet();
                this.readOnly = false;
            }

            Field(String fName, boolean readOnly) {
                this.fName = fName;
                this.values = Collections.emptySet();
                this.readOnly = readOnly;
            }

            @Override
            public String getfName() {
                return fName;
            }

            @Override
            public Set<String> getValues() {
                return values;
            }

            @Override
            public boolean isReadOnly() {
                return readOnly;
            }
        }

        private ModificationPdf() {
            throw new UnsupportedOperationException("do not call");
        }
    }

    static final class AgreementPdf {

        enum Field implements PdfField {
            FEDERAL_AWARDING_AGENCY("FederalAwardingAgency"),
            PASS_THROUGH_ENTITY("PassThroughEntityPTE"),
            PTE_PI("PTEPI"),
            PTE_FEDERAL_AWARD_NO("PTEFederalAwardNo"),
            SUBRECIPIENT("SubrecipientEntityName"),
            SUB_PI("SubPI"),
            SUBAWARD_NO("SubawardNumber"),
            PROJECT_TITLE("ProjectTitle"),
            POP_START("POPStart"),
            POP_END("POPEnd"),
            PROJECT_START("ProjectStart"),
            PROJECT_START_TIP("StartDateTooltip", true),
            PROJECT_END("ProjectEnd"),
            PROJECT_END_TIP("EndDateTooltip", true),
            AMOUNT_FUNDED_THIS_ACTION("AmountFundedThisAction"),
            INCREMENTALLY_ESTIMATED_TOTAL("IncrementallyEstimatedTotal"),
            TERM_2_CONTACT("Term2Contact", Stream.of(Field.TERM_CONTACT_FINANCIAL, Field.TERM_CONTACT_ADMIN, Field.TERM_CONTACT_PI, Field.TERM_CONTACT_AUTH).collect(Collectors.toSet())),
            TERM_3_CONTACT("Term3Contact", Stream.of(Field.TERM_CONTACT_FINANCIAL, Field.TERM_CONTACT_ADMIN, Field.TERM_CONTACT_PI, Field.TERM_CONTACT_AUTH).collect(Collectors.toSet())),
            PROJECT_BUDGET_INVOICE_DROPDOWN("ProjectBudgetInvoiceDrop", Stream.of(Field.PROJECT_PERIOD_END_DATE_VALUE, Field.BUDGET_PERIOD_END_DATE_VALUE).collect(Collectors.toSet())),
            TERM_6_CONTACT("Term6Contact", Stream.of(Field.TERM_CONTACT_FINANCIAL, Field.TERM_CONTACT_ADMIN, Field.TERM_CONTACT_PI, Field.TERM_CONTACT_AUTH).collect(Collectors.toSet())),
            UNI_BI_MOD_DROPOWN("UniBiModDrop", Stream.of(Field.UNILATERALLY_VALUE, Field.BILATERALLY_VALUE).collect(Collectors.toSet())),
            TERM_7_CONTACT("Term7Contact", Stream.of(Field.TERM_CONTACT_FINANCIAL, Field.TERM_CONTACT_ADMIN, Field.TERM_CONTACT_PI, Field.TERM_CONTACT_AUTH).collect(Collectors.toSet())),
            TERM_9_CONTACT("Term9Contact", Stream.of(Field.TERM_CONTACT_FINANCIAL, Field.TERM_CONTACT_ADMIN, Field.TERM_CONTACT_PI, Field.TERM_CONTACT_AUTH).collect(Collectors.toSet()));

            protected static final String BUDGET_PERIOD_END_DATE_VALUE = "Budget Period end date.";
            protected static final String PROJECT_PERIOD_END_DATE_VALUE = "Project Period end date.";

            protected static final String UNILATERALLY_VALUE = "Unilaterally";
            protected static final String BILATERALLY_VALUE = "Bilaterally";

            protected static final String TERM_CONTACT_FINANCIAL = "Financial";
            protected static final String TERM_CONTACT_ADMIN = "Administrative";
            protected static final String TERM_CONTACT_PI = "Principal Investigator";
            protected static final String TERM_CONTACT_AUTH = "Authorized Official";


            private final String fName;
            private final Set<String> values;
            private final boolean readOnly;

            Field(String fName, Set<String> values) {
                this.fName = fName;
                this.values = values;
                this.readOnly = false;
            }

            Field(String fName) {
                this.fName = fName;
                this.values = Collections.emptySet();
                this.readOnly = false;
            }

            Field(String fName, boolean readOnly) {
                this.fName = fName;
                this.values = Collections.emptySet();
                this.readOnly = readOnly;
            }

            @Override
            public String getfName() {
                return fName;
            }

            @Override
            public Set<String> getValues() {
                return values;
            }

            @Override
            public boolean isReadOnly() {
                return readOnly;
            }
        }

        private AgreementPdf() {
            throw new UnsupportedOperationException("do not call");
        }
    }

    static final class AgreementCertificationPdf {

        enum Field implements PdfField {
            SUBAWARD_NO("SubawardNumber");

            private final String fName;
            private final Set<String> values;
            private final boolean readOnly;

            Field(String fName, Set<String> values) {
                this.fName = fName;
                this.values = values;
                this.readOnly = false;
            }

            Field(String fName) {
                this.fName = fName;
                this.values = Collections.emptySet();
                this.readOnly = false;
            }

            Field(String fName, boolean readOnly) {
                this.fName = fName;
                this.values = Collections.emptySet();
                this.readOnly = readOnly;
            }

            @Override
            public String getfName() {
                return fName;
            }

            @Override
            public Set<String> getValues() {
                return values;
            }

            @Override
            public boolean isReadOnly() {
                return readOnly;
            }
        }

        private AgreementCertificationPdf() {
            throw new UnsupportedOperationException("do not call");
        }
    }

    private enum Attachment2Form {
        FDP_AFOSR("FDP_AFOSR", Attachment2SponsorFormType.AFOSR), FDP_AMRMC("FDP_AMRMC", Attachment2SponsorFormType.AMRMC), FDP_AMRAA("FDP_AMRAA", Attachment2SponsorFormType.AMRAA), FDP_ARO("FDP_ARO", Attachment2SponsorFormType.ARO), FDP_DOE("FDP_DOE", Attachment2SponsorFormType.DOE), FDP_EPA("FDP_EPA", Attachment2SponsorFormType.EPA), FDP_NASA("FDP_NASA", Attachment2SponsorFormType.NASA), FDP_NIH("FDP_NIH", Attachment2SponsorFormType.NIH), FDP_NSF("FDP_NSF", Attachment2SponsorFormType.NSF), FDP_ONR("FDP_ONR", Attachment2SponsorFormType.ONR), FDP_USDA("FDP_USDA", Attachment2SponsorFormType.USDA);

        private final String id;
        private final Attachment2SponsorFormType sponsorFormType;

        Attachment2Form(String id, Attachment2SponsorFormType sponsorFormType) {
            this.id = id;
            this.sponsorFormType = sponsorFormType;
        }

        public String getId() {
            return id;
        }

        public Attachment2SponsorFormType getSponsorFormType() {
            return sponsorFormType;
        }
    }

    private enum Attachment2SponsorFormType {
        AFOSR, AMRMC, AMRAA, ARO, DOE, EPA, NASA, NIH, NSF, ONR, USDA
    }

    static final class Attachment2Pdf {

        enum Field implements PdfField {
            SUBAWARD_NUMBER("SubawardNumber"),
            DATA_ELEMENTS_REQUIRED("RequiredDataElements", Stream.of(Field.DE_ATTACHED, Field.DE_AS_ENTERED).collect(Collectors.toSet())),
            DATA_ELEMENTS_TIP("KPTooltip", true),
            NOA_TIP("NOATooltip", true),
            RESEARCH_AND_DEVELOPMENT("R&D"),
            SUBJECT_TO_FFATA("FFATAYes"),
            FAID("FAID"),
            FAIN("FAIN"),
            CFDA("CFDA"),
            COPYRIGHT("Copyright", Stream.of(Field.COPYRIGHT_SUBRECIPIENT_GRANTS, Field.COPYRIGHT_SUBRECIPIENT_SHALL_GRANT, "").collect(Collectors.toSet())),
            MPI_SECTION_LABEL("MPI"),
            MPI_SECTION_HELP("MPITooltip", true),
            MPI_SECTION_BLANK("BlankMPI"),
            MPI_SECTION_SELECT("MPIYesNo", Stream.of(Field.MPI_NOT_SUBJECT, Field.MPI_IS_SUBJECT, "").collect(Collectors.toSet())),
            MPI_PLAN_SELECT("MPIPlan", Stream.of(Field.MPI_PLAN_ATTACHED, Field.MPI_PLAN_UPON_REQUEST, "").collect(Collectors.toSet())),
            ANIMAL_SUBJECTS_CHECKBOX("VertebrateAnimals"),
            HUMAN_SUBJECTS_CHECKBOX("HumanSubjects"),
            NO_HUMAN_ANIMAL_SUBJECTS_CHECKBOX("NeitherHumanAnimal"),
            IRB_IACUC_LABEL("HSASLang"),
            IRB_IACUC_BLANK_LABEL("IntentionallyBlank"),
            PTE_REQUIRES_VERIFICATON_LABEL("HSASLang2"),
            PTE_VERIFICATION_CONTACT("HSVBDropdown", Stream.of(Field.VERIFICATION_CONTACT_ADMINISTRATIVE_CONTACT_VALUE, Field.VERIFICATION_CONTACT_AUTHORIZED_OFFICIAL_VALUE, Field.VERIFICATION_CONTACT_FINANCIAL_CONTACT_VALUE, Field.VERIFICATION_CONTACT_PI_VALUE).collect(Collectors.toSet())),
            PTE_IRB_LABEL("IRB"),
            PTE_IRB_DROPDOWN("IRBDrop", Stream.of(Field.PTE_UPON_REQUEST, Field.PTE_PRIOR_TO_EXECUTION_OF_THIS_AGREEMENT, Field.PTE_PRIOR_TO_EXECUTION_OF_THIS_AGREEMENT_ANNUALLY, Field.PTE_NOT_REQUIRED).collect(Collectors.toSet())),
            PTE_IRB_NOT_REQUIRED_REASON("IRBNotRequiredWhy", Stream.of(HumanPteSendNotRequiredReason.values())
                    .map(HumanPteSendNotRequiredReason::getDescription)
                    .collect(Collectors.toSet())),
            PTE_IACUC_LABEL("IACUC"),
            PTE_IACUC_DROPDOWN("IACUCDrop", Stream.of(Field.PTE_UPON_REQUEST, Field.PTE_PRIOR_TO_EXECUTION_OF_THIS_AGREEMENT, Field.PTE_PRIOR_TO_EXECUTION_OF_THIS_AGREEMENT_ANNUALLY, Field.PTE_NOT_REQUIRED).collect(Collectors.toSet())),
            PTE_IACUC_NOT_REQUIRED_REASON("IACUCNotRequiredWhy", Stream.of(AnimalPteSendNotRequiredReason.values())
                    .map(AnimalPteSendNotRequiredReason::getDescription)
                    .collect(Collectors.toSet())),
            HUMAN_SUBJECTS_DATA_BLANK("BlankHumanData"),
            HUMAN_SUBJECTS_DATA_EXCHANGE_LABEL("HSDexchange"),
            HUMAN_SUBJECTS_DATA_EXCHANGE2_LABEL("HSDExchange"),
            HUMAN_SUBJECTS_DATA_APPLICABLE("HumanDataYesNo", Stream.of(Field.HUMAN_DATA_NOT_APPLICABLE, Field.HUMAN_DATA_APPLICABLE, Field.HUMAN_DATA_NOT_ADDRESSED, "").collect(Collectors.toSet())),
            HUMAN_SUBJECTS_DATA_TERMS_LABEL("TermorDUA"),
            HUMAN_SUBJECTS_DATA_TERMS_TIP("AddTermTooltip", true),
            HUMAN_SUBJECTS_DATA_SUB_TO_PTE("SubToPTEYes"),
            HUMAN_SUBJECTS_DATA_PTE_TO_SUB("PTEToSubYes"),
            HUMAN_SUBJECTS_DATA_TERMS_DROPDOWN("TermDUA", Stream.of(Field.HUMAN_DATA_TERMS_SEPARATE_AGREEMENT, Field.HUMAN_DATA_TERMS_BELOW, "").collect(Collectors.toSet())),
            HUMAN_SUBJECTS_DATA_ADD_TERMS_TEXT("AdditionalTerms"),
            DATA_SHARING_PLAN("DataSharingPlan"),
            DATA_SHARING_DROPDOWN("DataSharingDropdown", Stream.of(Field.DATA_SHARING_ATTACHED, Field.DATA_SHARING_PROVIDED_UPON_REQUEST, "").collect(Collectors.toSet())),
            FCOI_PTE_DROPDOWN("COISubPTE", Stream.of(Field.FCOI_SUBRECIPIENT, Field.FCOI_PTE, "").collect(Collectors.toSet())),
            FCOI_OTHER_SPONSOR_AGENCY("FCOIOtherSponsor"),
            SPONSOR_AGENCY("SponsorAgency"),
            FEDERAL_AWARD_CONDITIONS("FederalAwardConditions"),
            AGENCY_IMPLEMENTATION("AgencyImplementation"),
            GRANTS_POLICY_STATEMENT("GrantsPolicy"),
            RES_TERMS_COND("RTC"),
            NO_COST_EXT_CONTACT("NCEContact", Stream.of(Field.NCE_CONTACT_ADMINISTRATIVE, Field.NCE_CONTACT_AUTHORIZED_OFFICIAL, Field.NCE_CONTACT_FINANCIAL, Field.NCE_CONTACT_PI).collect(Collectors.toSet())),
            TREATMENT_OF_PROGRAM_INCOME("ProgramIncome", Stream.of(Field.TPI_ADDITIVE_VALUE, Field.TPI_OTHER_VALUE, Field.TPI_UNSELECTED_VALUE).collect(Collectors.toSet())),
            TREATMENT_OF_PROGRAM_INCOME_OTHER_SPECIFY("ProgramIncomeText");

            private static final String VERIFICATION_CONTACT_ADMINISTRATIVE_CONTACT_VALUE = "Administrative Contact";
            private static final String VERIFICATION_CONTACT_FINANCIAL_CONTACT_VALUE = "Financial Contact";
            private static final String VERIFICATION_CONTACT_PI_VALUE = "Principal Investigator";
            private static final String VERIFICATION_CONTACT_AUTHORIZED_OFFICIAL_VALUE = "Authorized Official";

            private static final String TPI_ADDITIVE_VALUE = "Additive";
            private static final String TPI_OTHER_VALUE = "Other [enter text]";
            private static final String TPI_UNSELECTED_VALUE = "";

            private static final String DE_ATTACHED = "in the attached Federal Award.";
            private static final String DE_AS_ENTERED = "as entered.";

            private static final String COPYRIGHT_SUBRECIPIENT_GRANTS = "Subrecipient Grants";
            private static final String COPYRIGHT_SUBRECIPIENT_SHALL_GRANT = "Subrecipient Shall Grant";

            private static final String MPI_NOT_SUBJECT = "This subaward is not subject to an MPI Leadership Plan.";
            private static final String MPI_IS_SUBJECT = "This subaward is subject to an MPI Leadership Plan. Both parties will follow the finalized MPI Leadership Plan.";

            private static final String MPI_PLAN_ATTACHED = "The MPI plan is attached as part of Attachment 6.";
            private static final String MPI_PLAN_UPON_REQUEST = "The PTE will make the MPI plan available upon request.";

            private static final String PTE_UPON_REQUEST = "Upon Request";
            private static final String PTE_PRIOR_TO_EXECUTION_OF_THIS_AGREEMENT = "Prior to execution of this agreement";
            private static final String PTE_PRIOR_TO_EXECUTION_OF_THIS_AGREEMENT_ANNUALLY = "Prior to execution of this agreement and annually thereafter";
            private static final String PTE_NOT_REQUIRED = "Not required for the following reason:";

            private static final String HUMAN_DATA_NOT_APPLICABLE = "Not Applicable";
            private static final String HUMAN_DATA_APPLICABLE = "Applicable";
            private static final String HUMAN_DATA_NOT_ADDRESSED = "Human subjects data will not be addressed in this agreement";

            private static final String HUMAN_DATA_TERMS_SEPARATE_AGREEMENT = "Via a separate Data Use Agreement";
            private static final String HUMAN_DATA_TERMS_BELOW = "In the Additional Terms section below";

            private static final String DATA_SHARING_ATTACHED = "attached.";
            private static final String DATA_SHARING_PROVIDED_UPON_REQUEST = "provided upon request.";

            private static final String FCOI_SUBRECIPIENT = "Subrecipient";
            private static final String FCOI_PTE = "PTE";

            private static final String NCE_CONTACT_ADMINISTRATIVE = "Administrative";
            private static final String NCE_CONTACT_AUTHORIZED_OFFICIAL = "Authorized Official";
            private static final String NCE_CONTACT_FINANCIAL = "Financial";
            private static final String NCE_CONTACT_PI = "Principal Investigator";

            private static final String DEFAULT_APPEARANCE_STR_FONT_5 = "/ArialMT 5 Tf 0 g";
            private static final String DEFAULT_APPEARANCE_STR_FONT_8 = "/ArialMT 8 Tf 0 g";
            private static final String DEFAULT_APPEARANCE_STR_FONT_10 = "/ArialMT 10 Tf 0 g";


            private final String fName;
            private final Set<String> values;
            private final boolean readOnly;

            Field(String fName, Set<String> values) {
                this.fName = fName;
                this.values = values;
                this.readOnly = false;
            }

            Field(String fName) {
                this.fName = fName;
                this.values = Collections.emptySet();
                this.readOnly = false;
            }

            Field(String fName, boolean readOnly) {
                this.fName = fName;
                this.values = Collections.emptySet();
                this.readOnly = readOnly;
            }

            @Override
            public String getfName() {
                return fName;
            }

            @Override
            public Set<String> getValues() {
                return values;
            }

            @Override
            public boolean isReadOnly() {
                return readOnly;
            }
        }

        private Attachment2Pdf() {
            throw new UnsupportedOperationException("do not call");
        }
    }

    private enum Attachment3aForm {
        ATTACHMENT_3A("FDP_ATT_3A");

        private final String id;

        Attachment3aForm(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    static final class Attachment3aPdf {

        enum Field implements PdfField {
            SUBAWARD_NUMBER("SubawardNumber"),
            PTE_INFO_ENTITY_NAME("PassThroughEntityPTE"),
            PTE_INFO_LEGAL_ADDRESS("PTELegalAddress"),
            PTE_INFO_WEBSITE("PTEWebsite"),
            PTE_CONTACTS_CENTRAL_EMAIL("PTECentralEmail"),
            PTE_CONTACTS_PI_NAME("PTEPI"),
            PTE_CONTACTS_PI_EMAIL("PTEPIEmail"),
            PTE_CONTACTS_PI_PHONE("PTEPIPhone"),
            PTE_CONTACTS_ADMIN_NAME("PTEACName"),
            PTE_CONTACTS_ADMIN_EMAIL("PTEACEmail"),
            PTE_CONTACTS_ADMIN_PHONE("PTEACPhone"),
            PTE_CONTACTS_COI_EMAIL("PTECOIEmail"),
            PTE_CONTACTS_FIN_NAME("PTEFCName"),
            PTE_CONTACTS_FIN_EMAIL("PTEFCEmail"),
            PTE_CONTACTS_FIN_PHONE("PTEFCPhone"),
            PTE_CONTACTS_EMAIL_INVOICES("EmailInvoices", Stream.of(Attachment3aPdf.Field.EMAIL_INVOICES_TRUE, Attachment3aPdf.Field.EMAIL_INVOICES_FALSE).collect(Collectors.toSet())),
            PTE_CONTACTS_INVOICE_EMAIL("PTEInvoiceEmail"),
            PTE_CONTACTS_AUTH_OFFICIAL_NAME("PTEAOName"),
            PTE_CONTACTS_AUTH_OFFICIAL_EMAIL("PTEAOEmail"),
            PTE_CONTACTS_AUTH_OFFCIAL_PHONE("PTEAOPhone"),
            PTE_CONTACTS_PI_ADDRESS("PTEPIAddress"),
            PTE_CONTACTS_ADMIN_ADDRESS("PTEAdminAddress"),
            PTE_CONTACTS_INVOICE_ADDRESS("PTEInvoiceAddress");

            private static final String EMAIL_INVOICES_TRUE = "Yes";
            private static final String EMAIL_INVOICES_FALSE = "No";

            private final String fName;
            private final Set<String> values;
            private final boolean readOnly;

            Field(String fName, Set<String> values) {
                this.fName = fName;
                this.values = values;
                this.readOnly = false;
            }

            Field(String fName) {
                this.fName = fName;
                this.values = Collections.emptySet();
                this.readOnly = false;
            }

            Field(String fName, boolean readOnly) {
                this.fName = fName;
                this.values = Collections.emptySet();
                this.readOnly = readOnly;
            }

            @Override
            public String getfName() {
                return fName;
            }

            @Override
            public Set<String> getValues() {
                return values;
            }

            @Override
            public boolean isReadOnly() {
                return readOnly;
            }
        }

        private Attachment3aPdf() {
            throw new UnsupportedOperationException("do not call");
        }
    }

    private enum Attachment3bForm {
        ATTACHMENT_3B("FDP_ATT_3B");

        private final String id;

        Attachment3bForm(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    static final class Attachment3bPdf {

        enum Field implements PdfField {
            SUBAWARD_NUMBER("SubawardNumber");

            private final String fName;
            private final Set<String> values;
            private final boolean readOnly;

            Field(String fName, Set<String> values) {
                this.fName = fName;
                this.values = values;
                this.readOnly = false;
            }

            Field(String fName) {
                this.fName = fName;
                this.values = Collections.emptySet();
                this.readOnly = false;
            }

            Field(String fName, boolean readOnly) {
                this.fName = fName;
                this.values = Collections.emptySet();
                this.readOnly = readOnly;
            }

            @Override
            public String getfName() {
                return fName;
            }

            @Override
            public Set<String> getValues() {
                return values;
            }

            @Override
            public boolean isReadOnly() {
                return readOnly;
            }
        }

        private Attachment3bPdf() {
            throw new UnsupportedOperationException("do not call");
        }
    }

    private enum Attachment3bPage2Form {
        ATTACHMENT_3B_PAGE2("FDP_ATT_3B_2");

        private final String id;

        Attachment3bPage2Form(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    static final class Attachment3bPage2Pdf {

        enum Field implements PdfField {
            SUBAWARD_NUMBER("SubawardNumber"),
            ENTITY_NAME("SubEntityName"),
            PI_NAME("SubPIName");

            private final String fName;
            private final Set<String> values;
            private final boolean readOnly;

            Field(String fName, Set<String> values) {
                this.fName = fName;
                this.values = values;
                this.readOnly = false;
            }

            Field(String fName) {
                this.fName = fName;
                this.values = Collections.emptySet();
                this.readOnly = false;
            }

            Field(String fName, boolean readOnly) {
                this.fName = fName;
                this.values = Collections.emptySet();
                this.readOnly = readOnly;
            }

            @Override
            public String getfName() {
                return fName;
            }

            @Override
            public Set<String> getValues() {
                return values;
            }

            @Override
            public boolean isReadOnly() {
                return readOnly;
            }
            }

        private Attachment3bPage2Pdf() {
            throw new UnsupportedOperationException("do not call");
        }
    }

    public interface PdfField {
        String getfName();

        Set<String> getValues();

        boolean isReadOnly();
    }
}
