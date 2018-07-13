/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.custom.KcDocumentBaseAuditRule;
import org.kuali.coeus.common.framework.person.KcPerson;
import org.kuali.coeus.common.framework.person.KcPersonService;
import org.kuali.coeus.common.permissions.impl.bo.PermissionsUser;
import org.kuali.coeus.common.permissions.impl.bo.PermissionsUserEditRoles;
import org.kuali.coeus.common.permissions.impl.rule.PermissionsRule;
import org.kuali.coeus.common.permissions.impl.web.bean.User;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.rule.KcBusinessRule;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBaseExtension;
import org.kuali.coeus.sys.framework.rule.KcTransactionalDocumentRuleBase;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.award.commitments.*;
import org.kuali.kra.award.contacts.*;
import org.kuali.kra.award.detailsdates.AddAwardTransferringSponsorEvent;
import org.kuali.kra.award.detailsdates.AwardDetailsAndDatesRule;
import org.kuali.kra.award.detailsdates.AwardDetailsAndDatesRuleImpl;
import org.kuali.kra.award.detailsdates.AwardDetailsAndDatesSaveEvent;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.approvedsubawards.AwardApprovedSubaward;
import org.kuali.kra.award.home.approvedsubawards.AwardApprovedSubawardRuleEvent;
import org.kuali.kra.award.home.approvedsubawards.AwardApprovedSubawardRuleImpl;
import org.kuali.kra.award.home.keywords.AwardScienceKeyword;
import org.kuali.kra.award.home.rules.AwardAddCfdaEvent;
import org.kuali.kra.award.home.rules.AwardCfdaAuditRule;
import org.kuali.kra.award.home.rules.AwardCfdaRule;
import org.kuali.kra.award.home.rules.impl.AwardCfdaRuleImpl;
import org.kuali.kra.award.notesandattachments.attachments.AwardAttachment;
import org.kuali.kra.award.paymentreports.awardreports.*;
import org.kuali.kra.award.paymentreports.awardreports.reporting.ReportTracking;
import org.kuali.kra.award.paymentreports.awardreports.reporting.ReportTrackingBean;
import org.kuali.kra.award.paymentreports.closeout.AddAwardCloseoutRuleEvent;
import org.kuali.kra.award.paymentreports.closeout.AwardCloseoutRule;
import org.kuali.kra.award.paymentreports.closeout.AwardCloseoutRuleEvent;
import org.kuali.kra.award.paymentreports.closeout.AwardCloseoutRuleImpl;
import org.kuali.kra.award.paymentreports.paymentschedule.AddAwardPaymentScheduleRuleEvent;
import org.kuali.kra.award.paymentreports.paymentschedule.AwardPaymentScheduleRule;
import org.kuali.kra.award.paymentreports.paymentschedule.AwardPaymentScheduleRuleEvent;
import org.kuali.kra.award.paymentreports.paymentschedule.AwardPaymentScheduleRuleImpl;
import org.kuali.kra.award.paymentreports.specialapproval.approvedequipment.AwardApprovedEquipmentRule;
import org.kuali.kra.award.paymentreports.specialapproval.approvedequipment.AwardApprovedEquipmentRuleEvent;
import org.kuali.kra.award.paymentreports.specialapproval.approvedequipment.AwardApprovedEquipmentRuleImpl;
import org.kuali.kra.award.paymentreports.specialapproval.approvedequipment.EquipmentCapitalizationMinimumLoader;
import org.kuali.kra.award.paymentreports.specialapproval.foreigntravel.AwardApprovedForeignTravelRule;
import org.kuali.kra.award.paymentreports.specialapproval.foreigntravel.AwardApprovedForeignTravelRuleEvent;
import org.kuali.kra.award.paymentreports.specialapproval.foreigntravel.AwardApprovedForeignTravelRuleImpl;
import org.kuali.kra.award.permissions.AwardPermissionsRule;
import org.kuali.kra.award.rule.AddAwardAttachmentRule;
import org.kuali.kra.award.rule.AwardCommentsRule;
import org.kuali.kra.award.rule.AwardCommentsRuleImpl;
import org.kuali.kra.award.rule.event.AddAwardAttachmentEvent;
import org.kuali.kra.award.rule.event.AwardCommentsRuleEvent;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.timeandmoney.TimeAndMoneyForm;
import org.kuali.kra.timeandmoney.rule.event.TimeAndMoneyAwardDateSaveEvent;
import org.kuali.kra.timeandmoney.rules.TimeAndMoneyAwardDateSaveRuleImpl;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.DocumentAuditRule;
import org.kuali.rice.krad.util.MessageMap;

import java.sql.Date;
import java.util.List;

import static org.kuali.kra.infrastructure.KeyConstants.AWARD_ATTACHMENT_FILE_REQUIRED;
import static org.kuali.kra.infrastructure.KeyConstants.AWARD_ATTACHMENT_TYPE_CODE_REQUIRED;


public class AwardDocumentRule extends KcTransactionalDocumentRuleBase implements AwardPaymentScheduleRule,
                                                                            AwardApprovedEquipmentRule, 
                                                                            AwardApprovedForeignTravelRule, 
                                                                            AddFandaRateRule,
                                                                            AwardFandaRateSaveRule,
                                                                            AwardDetailsAndDatesRule,
                                                                            AwardProjectPersonsSaveRule,
                                                                            PermissionsRule,
                                                                            AwardReportTermRule,
                                                                            AwardReportTermRecipientRule,
                                                                            AwardCloseoutRule,
                                                                            AwardTemplateSyncRule,
                                                                            AwardCommentsRule,
                                                                            KcBusinessRule,
                                                                            AwardCfdaRule,
                                                                            AddAwardAttachmentRule,
                                                                            DocumentAuditRule{
    
    public static final String DOCUMENT_ERROR_PATH = "document";
    public static final String AWARD_ERROR_PATH = "awardList[0]";
    private static final String AWARD_ERROR_PATH_PREFIX = "document.awardList[0].";
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(AwardDocumentRule.class);
    public static final String TYPE_CODE = ".typeCode";

    private ParameterService parameterService;
    private KcPersonService kcPersonService;

    public KcPersonService getKcPersonService() {
        if (kcPersonService == null) {
            kcPersonService = KcServiceLocator.getService(KcPersonService.class);
        }
        return kcPersonService;
    }

    @Override
    public boolean processAwardApprovedEquipmentBusinessRules(AwardApprovedEquipmentRuleEvent event) {
        return processApprovedEquipmentBusinessRules(GlobalVariables.getMessageMap(), event.getAwardDocument());
    }

    @Override
    public boolean processAwardApprovedForeignTravelBusinessRules(AwardApprovedForeignTravelRuleEvent event) {
            return processApprovedForeignTravelBusinessRules(GlobalVariables.getMessageMap(), event.getAwardDocument());
    }

    @Override
    public boolean processAwardPaymentScheduleBusinessRules(AwardPaymentScheduleRuleEvent event) {
        return true;
    }

    @Override
    public boolean processAddAwardPaymentScheduleBusinessRules(AddAwardPaymentScheduleRuleEvent event) {
        return processAddPaymentScheduleBusinessRules(GlobalVariables.getMessageMap(), event);
    }

    @Override
    public boolean processAddAwardTransferringSponsorEvent(AddAwardTransferringSponsorEvent addAwardTransferringSponsorEvent) {
        return new AwardDetailsAndDatesRuleImpl().processAddAwardTransferringSponsorEvent(addAwardTransferringSponsorEvent);
    }

    @Override
    public boolean processAddAwardCloseoutBusinessRules(AddAwardCloseoutRuleEvent addAwardCloseoutRuleEvent) {
        return new AwardCloseoutRuleImpl().processAddAwardCloseoutBusinessRules(addAwardCloseoutRuleEvent);
    }

    @Override
    public boolean processAwardCloseoutBusinessRules(AwardCloseoutRuleEvent awardCloseoutRuleEvent) {
        return new AwardCloseoutRuleImpl().processAwardCloseoutBusinessRules(awardCloseoutRuleEvent);
    }

    @Override
    public boolean processSaveAwardProjectPersonsBusinessRules(SaveAwardProjectPersonsRuleEvent event) {
        return processSaveAwardProjectPersonsBusinessRules(GlobalVariables.getMessageMap(), (AwardDocument) event.getDocument());
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        return super.processCustomRouteDocumentBusinessRules(document);
    }

    @Override
    public boolean processAddPermissionsUserBusinessRules(Document document, List<User> users, PermissionsUser newUser) {
        return new AwardPermissionsRule().processAddPermissionsUserBusinessRules(document, users, newUser);
    }

    @Override
    public boolean processDeletePermissionsUserBusinessRules(Document document, List<User> users, int index) {
        return new AwardPermissionsRule().processDeletePermissionsUserBusinessRules(document, users, index);     
    }

    @Override
    public boolean processEditPermissionsUserRolesBusinessRules(Document document, List<User> users,
            PermissionsUserEditRoles editRoles) {
        return new AwardPermissionsRule().processEditPermissionsUserRolesBusinessRules(document, users, editRoles);
    }
    
    @Override
    public boolean processSaveAwardDetailsAndDates(AwardDetailsAndDatesSaveEvent awardDetailsAndDatesSaveEvent) {
        return new AwardDetailsAndDatesRuleImpl().processSaveAwardDetailsAndDates(awardDetailsAndDatesSaveEvent);
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        if(skipRuleProcessing(document)) {
            return true;
        }
        
        boolean retval = true;
        MessageMap errorMap = GlobalVariables.getMessageMap();
        if (!(document instanceof AwardDocument)) {
            return false;
        }
        
        AwardDocument awardDocument = (AwardDocument) document;

        retval &= processUnitNumberBusinessRule(errorMap, awardDocument);
        retval &= processCostShareBusinessRules(document);
        retval &= processBenefitsRatesBusinessRules(document);
        retval &= processApprovedSubawardBusinessRules(document);
        retval &= processApprovedEquipmentBusinessRules(errorMap, awardDocument);
        retval &= processApprovedForeignTravelBusinessRules(errorMap, awardDocument);
        retval &= processSaveAwardProjectPersonsBusinessRules(errorMap, awardDocument);
        retval &= processAwardCommentsBusinessRules(awardDocument);
        retval &= processAwardDetailsAndDatesSaveRules(document);
        retval &= processDateBusinessRule(errorMap, awardDocument);
        retval &=processKeywordBusinessRule(awardDocument);
        retval &=processAwardAttachmentBusinessRule(awardDocument);
        return retval;
    }

    private boolean skipRuleProcessing(Document document) {
        return AwardDocument.PLACEHOLDER_DOC_DESCRIPTION.equals(document.getDocumentHeader().getDocumentDescription());
    }    
    
    private boolean processApprovedEquipmentBusinessRules(MessageMap errorMap, AwardDocument awardDocument) {
        boolean success = true;
        errorMap.addToErrorPath(DOCUMENT_ERROR_PATH);
        errorMap.addToErrorPath(AWARD_ERROR_PATH);
        Award award = awardDocument.getAward();
        EquipmentCapitalizationMinimumLoader helper = new EquipmentCapitalizationMinimumLoader();
        AwardApprovedEquipmentRuleImpl rule = new AwardApprovedEquipmentRuleImpl();
        if (award.getApprovedEquipmentItems() != null) {
            int count = award.getApprovedEquipmentItems().size();
            for (int i = 0; i < count; i++) {
                String errorPath = String.format("approvedEquipmentItems[%d]", i);
                errorMap.addToErrorPath(errorPath);
                String errorKey = AWARD_ERROR_PATH_PREFIX + errorPath;
                AwardApprovedEquipmentRuleEvent event = new AwardApprovedEquipmentRuleEvent(errorKey, awardDocument, awardDocument.getAward(),
                        award.getApprovedEquipmentItems().get(i),
                        helper.getMinimumCapitalization());
                success &= rule.processAwardApprovedEquipmentBusinessRules(event);
                errorMap.removeFromErrorPath(errorPath);
            }
        }
        errorMap.removeFromErrorPath(AWARD_ERROR_PATH);
        errorMap.removeFromErrorPath(DOCUMENT_ERROR_PATH);
        
        return success;
    }
    
    private boolean processAwardAttachmentBusinessRule(AwardDocument awardDocument) {
       boolean valid=true;
       List<AwardAttachment> awardAttachments= awardDocument.getAwardList().get(0).getAwardAttachments();
       for ( AwardAttachment awardAttachment : awardAttachments ) {
           if (awardAttachment.getTypeCode() == null) {
                   valid = false;
           }
      }
       if(valid) {
           for (AwardAttachment awardattachment : awardAttachments) {
        	   awardattachment.setModifyAttachment(false); 
           }
       }
        return valid;
    }
    
    private boolean processKeywordBusinessRule(AwardDocument awardDocument) {
        
       List<AwardScienceKeyword> keywords= awardDocument.getAward().getKeywords();

       for ( AwardScienceKeyword keyword : keywords ) {
            for ( AwardScienceKeyword keyword2 : keywords ) {
                if (keyword != keyword2) {
                    if ( StringUtils.equalsIgnoreCase(keyword.getScienceKeywordCode(), keyword2.getScienceKeywordCode()) ) {
                        GlobalVariables.getMessageMap().putError("document.awardList[0].keywords", "error.proposalKeywords.duplicate");

                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private boolean processAddPaymentScheduleBusinessRules(MessageMap errorMap, AddAwardPaymentScheduleRuleEvent event) {
        return new AwardPaymentScheduleRuleImpl().processAddAwardPaymentScheduleBusinessRules(event);
    }

    private boolean processCostShareBusinessRules(Document document) {
        boolean valid = true;
        MessageMap errorMap = GlobalVariables.getMessageMap();
        AwardDocument awardDocument = (AwardDocument) document;
        int i = 0;
        List<AwardCostShare> awardCostShares = awardDocument.getAward().getAwardCostShares();
        errorMap.addToErrorPath(DOCUMENT_ERROR_PATH);
        errorMap.addToErrorPath(AWARD_ERROR_PATH);
        for (AwardCostShare awardCostShare : awardCostShares) {
                String errorPath = "awardCostShares[" + i + Constants.RIGHT_SQUARE_BRACKET;
                errorMap.addToErrorPath(errorPath);
                AwardCostShareRuleEvent event = new AwardCostShareRuleEvent(errorPath,
                        awardDocument,
                        awardCostShare);
            event.setFieldName("document.awardList[0].awardCostShares[" + i + "].source");
                valid &= new AwardCostShareRuleImpl().processCostShareBusinessRules(event, i);
                errorMap.removeFromErrorPath(errorPath);
                i++;
        }
        errorMap.removeFromErrorPath(AWARD_ERROR_PATH);
        errorMap.removeFromErrorPath(DOCUMENT_ERROR_PATH);
        return valid;
    }

    private boolean processAwardDetailsAndDatesSaveRules(Document document) {
        boolean valid = true;
        MessageMap errorMap = GlobalVariables.getMessageMap();
        AwardDocument awardDocument = (AwardDocument) document;
        errorMap.addToErrorPath(DOCUMENT_ERROR_PATH);
        errorMap.addToErrorPath(AWARD_ERROR_PATH);
        AwardDetailsAndDatesSaveEvent event = new AwardDetailsAndDatesSaveEvent(awardDocument, awardDocument.getAward());
        
        valid &= new AwardDetailsAndDatesRuleImpl().processSaveAwardDetailsAndDates(event);
        errorMap.removeFromErrorPath(AWARD_ERROR_PATH);
        errorMap.removeFromErrorPath(DOCUMENT_ERROR_PATH);
        return valid;
    }

    private boolean processAwardCommentsBusinessRules(AwardDocument awardDocument) {
        AwardCommentsRuleEvent ruleEvent = new AwardCommentsRuleEvent(DOCUMENT_ERROR_PATH + "." + AWARD_ERROR_PATH, awardDocument);
        return processAwardCommentsBusinessRules(ruleEvent);
    }

    @Override
    public boolean processAwardCommentsBusinessRules(AwardCommentsRuleEvent ruleEvent) {
        return new AwardCommentsRuleImpl().processAwardCommentsBusinessRules(ruleEvent);
    }
    
      
    private boolean processBenefitsRatesBusinessRules(Document document) {
        boolean valid = true;
        MessageMap errorMap = GlobalVariables.getMessageMap();
        AwardDocument awardDocument = (AwardDocument) document;
        Award award = awardDocument.getAward();
        errorMap.addToErrorPath(DOCUMENT_ERROR_PATH);
        errorMap.addToErrorPath(AWARD_ERROR_PATH);
        if(StringUtils.equalsIgnoreCase(
                getParameterService().getParameterValueAsString(Constants.PARAMETER_MODULE_AWARD, 
                        ParameterConstants.DOCUMENT_COMPONENT,
                        KeyConstants.ENABLE_AWARD_FNA_VALIDATION),
                        KeyConstants.ENABLED_PARAMETER_VALUE_ONE) || 
                        StringUtils.equalsIgnoreCase(
                                getParameterService().getParameterValueAsString(Constants.PARAMETER_MODULE_AWARD, 
                                        ParameterConstants.DOCUMENT_COMPONENT,
                                        KeyConstants.ENABLE_AWARD_FNA_VALIDATION),
                                        KeyConstants.ENABLED_PARAMETER_VALUE_TWO)){
            String errorPath = "benefitsRates.rates";
            errorMap.addToErrorPath(errorPath);
            AwardBenefitsRatesRuleEvent event = new AwardBenefitsRatesRuleEvent(errorPath, 
                                                                                    award, 
                                                                                    awardDocument); 
            valid &= new AwardBenefitsRatesRuleImpl().processBenefitsRatesBusinessRules(event);
            errorMap.removeFromErrorPath(errorPath);
        }        
        
        errorMap.removeFromErrorPath(AWARD_ERROR_PATH);
        errorMap.removeFromErrorPath(DOCUMENT_ERROR_PATH);
        return valid;
    }

    public boolean processApprovedSubawardBusinessRules(Document document) {
        boolean valid = true;
        MessageMap errorMap = GlobalVariables.getMessageMap();
        AwardDocument awardDocument = (AwardDocument) document;
        int i = 0;
        List<AwardApprovedSubaward> awardApprovedSubawards = 
                            awardDocument.getAward().getAwardApprovedSubawards();
        errorMap.addToErrorPath(DOCUMENT_ERROR_PATH);
        errorMap.addToErrorPath(AWARD_ERROR_PATH);
        for (AwardApprovedSubaward awardApprovedSubaward : awardApprovedSubawards) {
            String errorPath = "awardApprovedSubawards[" + i + Constants.RIGHT_SQUARE_BRACKET;
            errorMap.addToErrorPath(errorPath);
            AwardApprovedSubawardRuleEvent event = new AwardApprovedSubawardRuleEvent(errorPath, 
                                                                                      awardDocument, 
                                                                                      awardApprovedSubaward,
                                                                                      awardApprovedSubawards);
            valid &= new AwardApprovedSubawardRuleImpl().processApprovedSubawardBusinessRules(event);
            errorMap.removeFromErrorPath(errorPath);
            i++;
        }
        errorMap.removeFromErrorPath(AWARD_ERROR_PATH);
        errorMap.removeFromErrorPath(DOCUMENT_ERROR_PATH);
        return valid;
    }

    @Override
    public boolean processRunAuditBusinessRules(Document document){
        boolean retval = true;
        
        retval &= new KcDocumentBaseAuditRule().processRunAuditBusinessRules(document);
        retval &= new AwardReportAuditRule().processRunAuditBusinessRules(document);
        retval &= new AwardTermsAuditRule().processRunAuditBusinessRules(document);
        retval &= new AwardPaymentAndInvoicesAuditRule().processRunAuditBusinessRules(document);
        retval &= new AwardCostShareAuditRule().processRunAuditBusinessRules(document);
        retval &= new AwardFandARateAuditRule().processRunAuditBusinessRules(document);
        retval &= new AwardProjectPersonsAuditRule().processRunAuditBusinessRules(document);
        retval &= new AwardPersonCreditSplitAuditRule().processRunAuditBusinessRules(document);
        retval &= new AwardSubawardAuditRule().processRunAuditBusinessRules(document);
        retval &= new AwardSyncAuditRule().processRunAuditBusinessRules(document);
        retval &= new AwardSponsorContactAuditRule().processRunAuditBusinessRules(document);
        retval &= new AwardBudgetLimitsAuditRule().processRunAuditBusinessRules(document);
        retval &= new AwardDetailsAndDatesAuditRule().processRunAuditBusinessRules(document);
        retval &= new AwardCfdaAuditRule().processRunAuditBusinessRules(document);

        return retval;
    }

    @Override
    public boolean processAddFandaRateBusinessRules(AddAwardFandaRateEvent 
            addAwardFandaRateEvent) {        
        return new AwardFandaRateRule().processAddFandaRateBusinessRules(
                addAwardFandaRateEvent);            
    }

    @Override
    public boolean processSaveFandaRateBusinessRules(AwardFandaRateSaveEvent awardFandaRateSaveEvent) {
        return new AwardFandaRateRule().processSaveFandaRateBusinessRules(awardFandaRateSaveEvent);
    }

    public boolean processAwardReportTermBusinessRules(Document document) {
        AwardDocument awardDocument = (AwardDocument) document;
        AwardReportTerm awardReportTermItem = awardDocument.getAward().getAwardReportTermItems().isEmpty() ? null : awardDocument.getAward().getAwardReportTermItems().get(0);
        AwardReportTermRuleEvent event = new AwardReportTermRuleEvent(AWARD_ERROR_PATH_PREFIX, awardDocument, awardDocument.getAward(), awardReportTermItem);
        return processAwardReportTermBusinessRules(event);
    }
    
    public boolean processAwardReportTermSaveRules(AwardForm awardForm) {
        boolean isValid = true;

        int reportTrackingBeanscount=0;
        if(awardForm.getReportTrackingBeans()!=null && !(awardForm.getReportTrackingBeans().isEmpty())) {
            List<ReportTrackingBean> reportTrackingBeanList=awardForm.getReportTrackingBeans();
            for(ReportTrackingBean reportTrackingBeans : reportTrackingBeanList ) {                 
                if(reportTrackingBeans.getPreparerName()!=null) {
                    KcPerson preparerPerson = getKcPersonService().getKcPersonByUserName(reportTrackingBeans.getPreparerName());
                    if (preparerPerson == null ) {
                        isValid = false; 
                        GlobalVariables.getMessageMap().putError("reportTrackingBeans["+reportTrackingBeanscount+"].preparerName", "error.preparername.duplicate");
                    }
                }
                reportTrackingBeanscount++;
            }
            if(awardForm.getAwardDocument().getAward().getAwardReportTermItems()!=null) {
                List<AwardReportTerm> awardReportTrackingItemList=awardForm.getAwardDocument().getAward().getAwardReportTermItems();
                int awardReportTermItemscount=0;
                for(AwardReportTerm awardReportTrackingTerm : awardReportTrackingItemList ) {
                    if(awardReportTrackingTerm.getReportTrackings()!=null) {
                        List<ReportTracking> reportTrackingsList = awardReportTrackingTerm.getReportTrackings();
                        for (ReportTracking reportTrackingsItem : reportTrackingsList) {
                            if(reportTrackingsItem.getPreparerName() != null) {
                                KcPerson preparerPerson = getKcPersonService().getKcPersonByUserName(reportTrackingsItem.getPreparerName());
                                if (preparerPerson == null ) {
                                    isValid = false; 
                                    GlobalVariables.getMessageMap().putError("document.award.awardReportTermItems["+awardReportTermItemscount+"].reportTrackings[0].preparerName", "error.preparername.duplicate");
                                }
                            }
                        }
                    }
                    awardReportTermItemscount++;
                } 
            }
        }
        return isValid;
    }

    @Override
    public boolean processAwardReportTermBusinessRules(AwardReportTermRuleEvent event){
        return new AwardReportTermRuleImpl().processAwardReportTermBusinessRules(event);
    }
    
    protected boolean isValidReportCode(AwardReportTerm awardReportTerm, List<KeyValue> reportCodes){
        boolean isValid = false;
        for(KeyValue KeyValue:reportCodes){
            if(StringUtils.equalsIgnoreCase(KeyValue.getKey(),
                    awardReportTerm.getReportCode())) {
                isValid = true;                    
            }
        }        
        return isValid;    
    }

    protected boolean isValidFrequencyBase(
            AwardReportTerm awardReportTerm, List<KeyValue> frequencyBaseCodes){
        boolean isValid = false;
        
        for(KeyValue KeyValue:frequencyBaseCodes){
            if(StringUtils.equalsIgnoreCase(KeyValue.getKey(),
                    awardReportTerm.getFrequencyBaseCode())) {
                isValid = true;                    
            }
        }
        return isValid;
    }

    @Override
    public boolean processAddAwardReportTermBusinessRules(AddAwardReportTermRuleEvent event){
        return new AwardReportTermRuleImpl().processAddAwardReportTermBusinessRules(event);
    }

    @Override
    public boolean processAwardReportTermRecipientBusinessRules(AwardReportTermRecipientRuleEvent event){
        return new AwardReportTermRecipientRuleImpl().processAwardReportTermRecipientBusinessRules(event);
    }

    @Override
    public boolean processAddAwardReportTermRecipientBusinessRules(AddAwardReportTermRecipientRuleEvent event){
        return new AwardReportTermRecipientRuleImpl().processAddAwardReportTermRecipientBusinessRules(event);
    }
    
    private boolean processApprovedForeignTravelBusinessRules(MessageMap errorMap, AwardDocument awardDocument) {
        boolean success = true;
        errorMap.addToErrorPath(DOCUMENT_ERROR_PATH);
        errorMap.addToErrorPath(AWARD_ERROR_PATH);
        Award award = awardDocument.getAward();
        AwardApprovedForeignTravelRule rule = new AwardApprovedForeignTravelRuleImpl();
        int count = award.getApprovedForeignTravelTrips().size();
        for (int i = 0; i < count; i++) {
            String errorPath = String.format("approvedForeignTravelTrips[%d]", i);
            errorMap.addToErrorPath(errorPath);
            String errorKey = AWARD_ERROR_PATH_PREFIX + errorPath;
            AwardApprovedForeignTravelRuleEvent event = new AwardApprovedForeignTravelRuleEvent(errorKey, awardDocument, awardDocument.getAward(),
                        award.getApprovedForeignTravelTrips().get(i));
            success &= rule.processAwardApprovedForeignTravelBusinessRules(event);
            errorMap.removeFromErrorPath(errorPath);
        }
        errorMap.removeFromErrorPath(AWARD_ERROR_PATH);
        errorMap.removeFromErrorPath(DOCUMENT_ERROR_PATH);
        
        return success;
    }
    
    private boolean processSaveAwardProjectPersonsBusinessRules(MessageMap errorMap, AwardDocument document) {
        errorMap.addToErrorPath(DOCUMENT_ERROR_PATH);
        errorMap.addToErrorPath(AWARD_ERROR_PATH);
        SaveAwardProjectPersonsRuleEvent event = new SaveAwardProjectPersonsRuleEvent("Project Persons", "projectPersons", document);
        boolean success = new AwardProjectPersonsSaveRuleImpl().processSaveAwardProjectPersonsBusinessRules(event);
        errorMap.removeFromErrorPath(AWARD_ERROR_PATH);
        errorMap.removeFromErrorPath(DOCUMENT_ERROR_PATH);
        
        return success;
    }

    @Override
    public boolean processAwardTemplateSyncRules(AwardTemplateSyncEvent awardTemplateSyncEvent) {
        return new AwardTemplateSyncRuleImpl().processAwardTemplateSyncRules(awardTemplateSyncEvent);
    }

    private boolean processUnitNumberBusinessRule(MessageMap errorMap, AwardDocument awardDocument) {
        Award award = awardDocument.getAward();
        errorMap.addToErrorPath(DOCUMENT_ERROR_PATH);
        errorMap.addToErrorPath(AWARD_ERROR_PATH);

        boolean success = award.getUnitNumber() != null && award.getUnit() != null;
        if(!success) {
            errorMap.putError("unitNumber", KeyConstants.ERROR_UNIT_INVALID, award.getUnitNumber());
        }

        errorMap.removeFromErrorPath(AWARD_ERROR_PATH);
        errorMap.removeFromErrorPath(DOCUMENT_ERROR_PATH);
        return success;
    }

    /*
     * This method is to add bus rule check proj beg date <= proj end date
     * and obligation end date >= obligation start date.
     */
    private boolean processDateBusinessRule(MessageMap errorMap, AwardDocument awardDocument) {
        
        boolean isTimeAndMoneyDocument = KNSGlobalVariables.getKualiForm() instanceof TimeAndMoneyForm;
        
        if (isTimeAndMoneyDocument) {
            TimeAndMoneyForm timeAndMoneyForm = (TimeAndMoneyForm) KNSGlobalVariables.getKualiForm();
            TimeAndMoneyAwardDateSaveEvent event = new TimeAndMoneyAwardDateSaveEvent("", timeAndMoneyForm.getTimeAndMoneyDocument());
            TimeAndMoneyAwardDateSaveRuleImpl rule = new TimeAndMoneyAwardDateSaveRuleImpl();
            return rule.processSaveAwardDatesBusinessRules(event);
        } else {
            Award award = awardDocument.getAward();
            errorMap.addToErrorPath(DOCUMENT_ERROR_PATH);
            errorMap.addToErrorPath(AWARD_ERROR_PATH);
    
            boolean success = true;
            int lastIndex = award.getIndexOfLastAwardAmountInfo();
            // make sure start dates are before end dates
            Date effStartDate = award.getAwardEffectiveDate(); 
            Date effEndDate = award.getAwardAmountInfos().get(lastIndex).getFinalExpirationDate();
            String awardId = award.getAwardNumber();
            
            
            Date oblStartDate = award.getAwardAmountInfos().get(lastIndex).getCurrentFundEffectiveDate(); 
            Date oblEndDate = award.getAwardAmountInfos().get(lastIndex).getObligationExpirationDate();
            
            String fieldStarter = "awardAmountInfos["+lastIndex;
            //String fieldStarter = "awardHierarchyNodeItems["+(lastIndex-1);
            success = AwardDateRulesHelper.validateProjectStartBeforeProjectEnd(errorMap, effStartDate, effEndDate, fieldStarter+"].finalExpirationDate", awardId) && success;
            success = AwardDateRulesHelper.validateObligationStartBeforeObligationEnd(errorMap, oblStartDate, oblEndDate, fieldStarter+"].currentFundEffectiveDate", awardId) && success;
            success = AwardDateRulesHelper.validateProjectStartBeforeObligationStart(errorMap, effStartDate, oblStartDate, fieldStarter+"].currentFundEffectiveDate", awardId) && success;
            success = AwardDateRulesHelper.validateProjectStartBeforeObligationEnd(errorMap, effStartDate, oblEndDate, fieldStarter+"].obligationExpirationDate", awardId) && success;
            success = AwardDateRulesHelper.validateObligationStartBeforeProjectEnd(errorMap, oblStartDate, effEndDate, fieldStarter+"].currentFundEffectiveDate", awardId) && success;
            success = AwardDateRulesHelper.validateObligationEndBeforeProjectEnd(errorMap, oblEndDate, effEndDate, fieldStarter+"].obligationExpirationDate", awardId) && success;

            errorMap.removeFromErrorPath(AWARD_ERROR_PATH);
            errorMap.removeFromErrorPath(DOCUMENT_ERROR_PATH);
            return success;
        }
    }



    @Override
    public boolean processRules(KcDocumentEventBaseExtension event) {
        boolean retVal = false;
        retVal = event.getRule().processRules(event);
        return retVal;
    }

    @Override
    public boolean processsAddAttachmentRule(AddAwardAttachmentEvent event) {
        boolean valid = true;

        if( StringUtils.isBlank(event.getAwardAttachment().getTypeCode() )) {
            valid = false;
            LOG.debug(AWARD_ATTACHMENT_TYPE_CODE_REQUIRED);
            reportError("awardAttachmentFormBean.newAttachment.typeCode", AWARD_ATTACHMENT_TYPE_CODE_REQUIRED);
            
        }
        
        
        if( event.getAwardAttachment().getNewFile() == null || StringUtils.isEmpty(event.getAwardAttachment().getNewFile().getFileName()) ) {
            valid = false;
            LOG.debug(AWARD_ATTACHMENT_FILE_REQUIRED);
            reportError("awardAttachmentFormBean.newAttachment.newFile", AWARD_ATTACHMENT_FILE_REQUIRED);
        }
        
        return valid;
    }

    @Override
    public boolean processApplyModifiedAttachmentRule( AddAwardAttachmentEvent event) {
        if( StringUtils.isBlank(event.getAwardAttachment().getTypeCode() )) {
            LOG.debug(AWARD_ATTACHMENT_TYPE_CODE_REQUIRED);
            reportError(event.getErrorPathPrefix() + TYPE_CODE, AWARD_ATTACHMENT_TYPE_CODE_REQUIRED);
            return false;

        }
        return true;
    }

    protected GlobalVariableService getGlobalVariableService() {
        return KcServiceLocator.getService(GlobalVariableService.class);
    }

    protected ParameterService getParameterService() {
        if (this.parameterService == null) {
            this.parameterService = KcServiceLocator.getService(ParameterService.class);
        }
        return this.parameterService;
    }

    @Override
    public boolean processAddCfdaRules(AwardAddCfdaEvent awardAddCfdaEvent) {
        return new AwardCfdaRuleImpl().processAddCfdaRules(awardAddCfdaEvent);
    }
}
