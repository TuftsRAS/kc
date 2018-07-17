/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.subaward.reporting.printing.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.print.AbstractPrint;
import org.kuali.coeus.common.framework.print.PrintingException;
import org.kuali.coeus.common.framework.print.PrintingService;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.kra.award.awardhierarchy.AwardHierarchy;
import org.kuali.kra.award.awardhierarchy.AwardHierarchyService;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.AwardCfda;
import org.kuali.kra.award.version.service.AwardVersionService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.coeus.common.framework.print.AttachmentDataSource;
import org.kuali.kra.subaward.bo.*;

import org.kuali.kra.subaward.reporting.printing.SubAwardPrintType;
import org.kuali.kra.subaward.reporting.printing.print.SubAwardSF294Print;
import org.kuali.kra.subaward.reporting.printing.print.SubAwardSF295Print;
import org.kuali.kra.subaward.reporting.printing.service.SubAwardPrintingService;
import org.kuali.kra.subawardReporting.printing.print.SubAwardFDPAgreement;
import org.kuali.kra.subawardReporting.printing.print.SubAwardFDPModification;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.*;
import java.util.stream.Collectors;

import static org.kuali.coeus.sys.framework.util.CollectionUtils.entriesToMap;
import static org.kuali.coeus.sys.framework.util.CollectionUtils.entry;


public class SubAwardPrintingServiceImpl implements SubAwardPrintingService {
    
    private static final String SF_295_REPORT = "SF295";
    private static final String SF_294_REPORT = "SF294";
    private static final String SUB_AWARD_FDP_TEMPLATE = "fdpAgreement";
    private static final String SUB_AWARD_FDP_MODIFICATION = "fdpModification";
    private static final String FDP_FROM_PARENT_AWARD = "FDP_FROM_PARENT_AWARD";

    private SubAwardSF294Print subAwardSF294Print;
    private SubAwardSF295Print subAwardSF295Print;
    private PrintingService printingService;       
    private SubAwardFDPModification subAwardFDPModification;
    private SubAwardFDPAgreement subAwardFDPAgreement;
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private AwardVersionService awardVersionService;
    private AwardHierarchyService awardHierarchyService;


    public SubAwardFDPModification getSubAwardFDPModification() {
        return subAwardFDPModification;
    }

    public void setSubAwardFDPModification(SubAwardFDPModification subAwardFDPModification) {
        this.subAwardFDPModification = subAwardFDPModification;
    }

    public SubAwardFDPAgreement getSubAwardFDPAgreement() {
        return subAwardFDPAgreement;
    }

    public void setSubAwardFDPAgreement(SubAwardFDPAgreement subAwardFDPAgreement) {
        this.subAwardFDPAgreement = subAwardFDPAgreement;
    }


    @Override
    public AttachmentDataSource printSubAwardReport(KcPersistableBusinessObjectBase awardDocument,
            SubAwardPrintType subAwardPrintType, Map<String, Object> reportParameters) throws PrintingException {
        AttachmentDataSource source;
        AbstractPrint printable = null;         
        if (reportParameters.get("printType") != null) {
            if(reportParameters.get("printType").equals(SF_295_REPORT)){
                printable = getSubAwardSF295Print();                
            } else {
                printable = getSubAwardSF294Print();                
            }
        }  

        printable.setPrintableBusinessObject(awardDocument);
        printable.setReportParameters(reportParameters);       
        source = getPrintingService().print(printable);        
        source.setType(Constants.PDF_REPORT_CONTENT_TYPE);
        
        if(reportParameters.get("printType").equals(SF_295_REPORT)){
            source.setName(SF_295_REPORT + Constants.PDF_FILE_EXTENSION);
        } else {
            source.setName(SF_294_REPORT + Constants.PDF_FILE_EXTENSION);
        }
        return source;   
    }
    
    
    public PrintingService getPrintingService() {
        return printingService;
    }


    public void setPrintingService(PrintingService printingService) {
        this.printingService = printingService;
    }

    public SubAwardSF294Print getSubAwardSF294Print() {
        return subAwardSF294Print;
    }


    public void setSubAwardSF294Print(SubAwardSF294Print subAwardSF294Print) {
        this.subAwardSF294Print = subAwardSF294Print;
    }    


    public SubAwardSF295Print getSubAwardSF295Print() {
        return subAwardSF295Print;
    }


    public void setSubAwardSF295Print(SubAwardSF295Print subAwardSF295Print) {
        this.subAwardSF295Print = subAwardSF295Print;
    }
    

    protected AttachmentDataSource printSubAwardFDPReport(KcPersistableBusinessObjectBase subAwardDoc, Map<String, Object> reportParameters) throws PrintingException {
        AttachmentDataSource source;
        AbstractPrint printable = null;         
        if (reportParameters.get("fdpType") != null) {
            if(reportParameters.get("fdpType").equals(SUB_AWARD_FDP_TEMPLATE)){
                printable = getSubAwardFDPAgreement();                
            } else {
                printable = getSubAwardFDPModification();                
            }
        }  
        SubAward subAward = (SubAward)subAwardDoc;

        final Map<String, byte[]> formAttachments = subAward.getSubAwardAttachmentsForPrint()
                .stream()
                .filter(SubAwardAttachments::getSelectToPrint)
                .map(attach -> entry(attach.getAttachmentId().toString(), attach.getAttachmentContent()))
                .collect(entriesToMap(LinkedHashMap::new));

        resetSelectedFormList(subAward.getSubAwardAttachments());

        printable.setAttachments(formAttachments);
        printable.setPrintableBusinessObject(subAwardDoc);
        printable.setReportParameters(reportParameters);       
        source = getPrintingService().print(printable); 
        source.setType(Constants.PDF_REPORT_CONTENT_TYPE);
        
        if(reportParameters.get("fdpType").equals(SUB_AWARD_FDP_TEMPLATE)){
            source.setName(SUB_AWARD_FDP_TEMPLATE + Constants.PDF_FILE_EXTENSION);
        } else {
            source.setName(SUB_AWARD_FDP_MODIFICATION + Constants.PDF_FILE_EXTENSION);
        }
        return source;   
    }

    @Override
    public AttachmentDataSource printSubAwardFDPReport(SubAwardPrintAgreement agreement, SubAward subAward) {
        Map<String, Object> reportParameters = new HashMap<>();
        List<SubAwardForms> printFormTemplates = getSponsorFormTemplates(agreement,subAward.getSubAwardForms());

        if(agreement.getFundingSource() != null){
            for (SubAwardFundingSource subAwardFunding : subAward.getSubAwardFundingSourceList()) {
                if(agreement.getFundingSource().equals(subAwardFunding.getSubAwardFundingSourceId().toString())){

                    final Award workingAward = getAwardVersionService().getWorkingAwardVersion(subAwardFunding.getAward().getAwardNumber());
                    final Award award = (workingAward != null) ? workingAward : subAwardFunding.getAward();

                    reportParameters.put("awardNumber",award.getAwardNumber());
                    reportParameters.put("awardTitle",award.getParentTitle());
                    reportParameters.put("sponsorAwardNumber",award.getSponsorAwardNumber());
                    if (award.getSponsor() != null) {
                        reportParameters.put("sponsorName", award.getSponsor().getSponsorName());
                    }
                    reportParameters.put("cfdaNumber",award.getAwardCfdas().stream().map(AwardCfda::getCfdaNumber).collect(Collectors.joining(",")));
                    reportParameters.put("awardID",award.getAwardId());

                    if (getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_SUBAWARD, Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, FDP_FROM_PARENT_AWARD)) {
                        final AwardHierarchy hierarchy = getAwardHierarchyService().loadAwardHierarchy(subAwardFunding.getAward().getAwardNumber());
                        if (hierarchy != null) {
                            reportParameters.put("fain", hierarchy.getRoot().getAward().getFainId());

                            if (hierarchy.getRoot().getAward().getObligatedTotal() != null) {
                                reportParameters.put("obligatedTotal", hierarchy.getRoot().getAward().getObligatedTotal().bigDecimalValue());
                            }

                            if (hierarchy.getRoot().getAward().getAnticipatedTotal() != null) {
                                reportParameters.put("anticipatedTotal", hierarchy.getRoot().getAward().getAnticipatedTotal().bigDecimalValue());
                            }
                        }
                    } else {
                        reportParameters.put("fain", award.getFainId());

                        if (award.getObligatedTotal() != null) {
                            reportParameters.put("obligatedTotal", award.getObligatedTotal().bigDecimalValue());
                        }

                        if (award.getAnticipatedTotal() != null) {
                            reportParameters.put("anticipatedTotal", award.getAnticipatedTotal().bigDecimalValue());
                        }
                    }


                    if (award.getPrimeSponsor() != null) {
                        reportParameters.put("primeSponsorName", award.getPrimeSponsor().getSponsorName());
                    } else {
                        reportParameters.put("primeSponsorName", award.getSponsor().getSponsorName());
                    }

                    if (award.getNoticeDate() != null) {
                        final Calendar noticeDate = Calendar.getInstance();
                        noticeDate.setTime(award.getNoticeDate());
                        reportParameters.put("noticeDate", noticeDate);
                    }

                    if (award.getFedAwardDate() != null) {
                        final Calendar fedAwardDate = Calendar.getInstance();
                        fedAwardDate.setTime(award.getFedAwardDate());
                        reportParameters.put("fedAwardDate", fedAwardDate);
                    }

                    break;
                }
            }
        }

        if (!subAward.getSubAwardAmountInfoList().isEmpty()) {
            final int index = subAward.getSubAwardAmountInfoList().size() - 1;
            final String modificationTypeCode = subAward.getSubAwardAmountInfoList().get(index).getModificationTypeCode();
            if (StringUtils.isNotEmpty(modificationTypeCode)) {
                reportParameters.put("modificationType", modificationTypeCode);
            }
        }

        if (!subAward.getSubAwardTemplateInfo().isEmpty()) {
            reportParameters.put("fcoi", subAward.getSubAwardTemplateInfo().get(0).getFcio());
            reportParameters.put("invoicesEmailed", subAward.getSubAwardTemplateInfo().get(0).getInvoicesEmailed());
            reportParameters.put("invoiceAddressDifferent", subAward.getSubAwardTemplateInfo().get(0).getInvoiceAddressDifferent());
            reportParameters.put("invoiceEmailDifferent", subAward.getSubAwardTemplateInfo().get(0).getInvoiceEmailDifferent());
        }

        reportParameters.put("ffata",subAward.getFfataRequired());

        reportParameters.put(SubAwardPrintingService.SELECTED_TEMPLATES, printFormTemplates);
        reportParameters.put("fdpType",agreement.getFdpType());
        return printSubAwardFDPReport(subAward, reportParameters);
    }


    protected void resetsubAwardFormList(
            List<SubAwardForms> subAwardFormList) {
        for (SubAwardForms subAwardFormValues : subAwardFormList) {
            subAwardFormValues.setSelectToPrint(false);
        }
    }


    public boolean isCostSplitEnabled() {
        return getParameterService().getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_SUBAWARD, Constants.KC_ALL_PARAMETER_DETAIL_TYPE_CODE, Constants.ENABLE_SUBAWARD_DC_IDC);
    }

    protected List<SubAwardForms> getSponsorFormTemplates(SubAwardPrintAgreement subAwardPrint, List<SubAwardForms> subAwardFormList) {
        List<SubAwardForms> printFormTemplates = new ArrayList<>();
        if(subAwardPrint.getFdpType().equals(SUB_AWARD_FDP_TEMPLATE)){
            printFormTemplates.add(getBusinessObjectService().findBySinglePrimaryKey(SubAwardForms.class, "FDP Template"));
        } else if(subAwardPrint.getFdpType().equals(SUB_AWARD_FDP_MODIFICATION)) {
            if (isCostSplitEnabled()) {
                printFormTemplates.add(getBusinessObjectService().findBySinglePrimaryKey(SubAwardForms.class, "FDP Modification Unilateral"));
            } else {
                printFormTemplates.add(getBusinessObjectService().findBySinglePrimaryKey(SubAwardForms.class, "FDP Modification"));
            }
        }
        if(subAwardPrint.getAttachment3A()){
            printFormTemplates.add(getBusinessObjectService().findBySinglePrimaryKey(SubAwardForms.class, "FDP_ATT_3A"));
        }
        if(subAwardPrint.getAttachment3B()){
            printFormTemplates.add(getBusinessObjectService().findBySinglePrimaryKey(SubAwardForms.class, "FDP_ATT_3B"));
        }
        if(subAwardPrint.getAttachment3BPage2()){
            printFormTemplates.add(getBusinessObjectService().findBySinglePrimaryKey(SubAwardForms.class, "FDP_ATT_3B_2"));
        }
        if(subAwardPrint.getAttachment4()){
            printFormTemplates.add(getBusinessObjectService().findBySinglePrimaryKey(SubAwardForms.class, "FDP_ATT_4"));
        }
        if(subAwardFormList != null) {
        for(SubAwardForms subAwardFormValues : subAwardFormList){
            if(subAwardFormValues.getSelectToPrint()){
                final String description = subAwardFormValues.getDescription();
                final String[] token = description.split("\\s");
                final SubAwardForms forms = getBusinessObjectService().findBySinglePrimaryKey(SubAwardForms.class, "FDP_"+token[0]);
                if (forms != null) {
                    printFormTemplates.add(forms);
                }
            }
        }
        resetsubAwardFormList(subAwardFormList);
        }

        return printFormTemplates;
    }
    
    protected void resetSelectedFormList(List<SubAwardAttachments> subAwardFormList) {
        for (SubAwardAttachments subAwardFormValues : subAwardFormList) {
            subAwardFormValues.setSelectToPrint(false);
        }
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public AwardVersionService getAwardVersionService() {
        return awardVersionService;
    }

    public void setAwardVersionService(AwardVersionService awardVersionService) {
        this.awardVersionService = awardVersionService;
    }

    public AwardHierarchyService getAwardHierarchyService() {
        return awardHierarchyService;
    }

    public void setAwardHierarchyService(AwardHierarchyService awardHierarchyService) {
        this.awardHierarchyService = awardHierarchyService;
    }
}
