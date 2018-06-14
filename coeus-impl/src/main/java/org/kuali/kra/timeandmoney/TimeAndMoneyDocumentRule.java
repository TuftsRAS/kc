/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.timeandmoney;

import java.util.Objects;
import org.kuali.coeus.sys.framework.rule.KcTransactionalDocumentRuleBase;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.AwardAmountInfo;
import org.kuali.kra.award.paymentreports.awardreports.reporting.service.ReportTrackingService;
import org.kuali.kra.award.timeandmoney.AwardDirectFandADistribution;
import org.kuali.kra.award.timeandmoney.AwardDirectFandADistributionRule;
import org.kuali.kra.award.timeandmoney.AwardDirectFandADistributionRuleEvent;
import org.kuali.kra.award.timeandmoney.AwardDirectFandADistributionRuleImpl;
import org.kuali.kra.award.version.service.AwardVersionService;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.timeandmoney.document.TimeAndMoneyDocument;
import org.kuali.kra.timeandmoney.rule.event.TimeAndMoneyAwardAmountTransactionSaveEvent;
import org.kuali.kra.timeandmoney.rule.event.TimeAndMoneyAwardDateSaveEvent;
import org.kuali.kra.timeandmoney.rules.TimeAndMoneyAwardAmountTransactionRuleImpl;
import org.kuali.kra.timeandmoney.rules.TimeAndMoneyAwardDateSaveRuleImpl;
import org.kuali.kra.timeandmoney.transactions.AddTransactionRuleEvent;
import org.kuali.kra.timeandmoney.transactions.TransactionRule;
import org.kuali.kra.timeandmoney.transactions.TransactionRuleEvent;
import org.kuali.kra.timeandmoney.transactions.TransactionRuleImpl;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Main Business Rule class for <code>{@link TimeAndMoneyDocument}</code>. 
 * Responsible for delegating rules to independent rule classes.
 *
 */
public class TimeAndMoneyDocumentRule extends KcTransactionalDocumentRuleBase implements TransactionRule, AwardDirectFandADistributionRule {
    
    public static final String DOCUMENT_ERROR_PATH = "document";
    public static final String AWARD_ERROR_PATH = "award";
    private transient ReportTrackingService reportTrackingService;


    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean retval = true;
        MessageMap errorMap = GlobalVariables.getMessageMap();
        if (!(document instanceof TimeAndMoneyDocument)) {
            return false;
        }
        
        retval &= processAwardDirectFandADistributionBusinessRules(document);
        retval &= processTimeAndMoneyAwardAmountTransactionBusinessRules(document);
        retval &= processTimeAndMoneySaveAwardDateBusinessRules(document);
        reportAwardReportTrackingError(document);
        return retval;
    }
    
    /**
     * 
     * This method reports a warning when a change occurred that might change report tracking.
     * @param document
     */
    protected void reportAwardReportTrackingError(Document document) {
        TimeAndMoneyDocument timeAndMoneyDocument = (TimeAndMoneyDocument) document;
        for (Entry<String, AwardHierarchyNode> awardHierarchyNode : timeAndMoneyDocument.getAwardHierarchyNodes().entrySet()) {
            Award award = getAwardVersionService().getWorkingAwardVersion(awardHierarchyNode.getValue().getAwardNumber()); 
            if (!this.getReportTrackingService().getReportTracking(award).isEmpty() && checkReportTrackingValueChanges(timeAndMoneyDocument)) {
                KNSGlobalVariables.getMessageList().add(KeyConstants.REPORT_TRACKING_WARNING_UPDATE_FROM_DATE_CHANGE, "");
                return;
            }
        }
    }
    
    /**
     * 
     * This method determines if a change has occurred that might change report tracking records.
     * @param timeAndMoneyDocument
     * @return
     */
    protected boolean checkReportTrackingValueChanges(TimeAndMoneyDocument timeAndMoneyDocument) {
        Map<String, AwardHierarchyNode> formAwardHierarchyNodes = timeAndMoneyDocument.getAwardHierarchyNodes();
        for (String key : formAwardHierarchyNodes.keySet()) {
            AwardHierarchyNode formNode = formAwardHierarchyNodes.get(key);
            AwardAmountInfo awardAmountInfo = formNode.getAward().getAwardAmountInfos().get(formNode.getAward().getAwardAmountInfos().size() - 1);
            if (!Objects.equals(formNode.getCurrentFundEffectiveDate(), awardAmountInfo.getCurrentFundEffectiveDate())
                    || !Objects.equals(formNode.getObligationExpirationDate(), awardAmountInfo.getObligationExpirationDate())
                    || !Objects.equals(formNode.getFinalExpirationDate(), awardAmountInfo.getFinalExpirationDate())
                    || !Objects.equals(formNode.getAmountObligatedToDate(), awardAmountInfo.getAmountObligatedToDate())
                    || !Objects.equals(formNode.getAnticipatedTotalAmount(), awardAmountInfo.getAnticipatedTotalAmount())) {
                return true;
            }
        }
        return false;
    }
    
    /**
    *
    * process Direct F and A Distribution business rules.
    */
    private boolean processTimeAndMoneyAwardAmountTransactionBusinessRules(Document document) {
        boolean valid = true;
        MessageMap errorMap = GlobalVariables.getMessageMap();
        TimeAndMoneyDocument timeAndMoneyDocument = (TimeAndMoneyDocument) document;
        errorMap.addToErrorPath(DOCUMENT_ERROR_PATH);
        errorMap.addToErrorPath(AWARD_ERROR_PATH);
        String errorPath = "timeAndMoneyAwardAmountTransaction";
        errorMap.addToErrorPath(errorPath);
        TimeAndMoneyAwardAmountTransactionSaveEvent event = new TimeAndMoneyAwardAmountTransactionSaveEvent(errorPath, 
                                                            timeAndMoneyDocument);
        valid &= new TimeAndMoneyAwardAmountTransactionRuleImpl().processSaveAwardAmountTransactionBusinessRules(event);
        errorMap.removeFromErrorPath(errorPath);
        errorMap.removeFromErrorPath(AWARD_ERROR_PATH);
        errorMap.removeFromErrorPath(DOCUMENT_ERROR_PATH);
        return valid;
    }
    
    /**
    *
    * process Direct F and A Distribution business rules.
    */
    private boolean processTimeAndMoneySaveAwardDateBusinessRules(Document document) {
        boolean valid = true;
        MessageMap errorMap = GlobalVariables.getMessageMap();
        TimeAndMoneyDocument timeAndMoneyDocument = (TimeAndMoneyDocument) document;
        errorMap.addToErrorPath(DOCUMENT_ERROR_PATH);
        errorMap.addToErrorPath(AWARD_ERROR_PATH);
        String errorPath = "timeAndMoneyAwardDates";
        errorMap.addToErrorPath(errorPath);
        TimeAndMoneyAwardDateSaveEvent event = new TimeAndMoneyAwardDateSaveEvent(errorPath, 
                                                            timeAndMoneyDocument);
        valid &= new TimeAndMoneyAwardDateSaveRuleImpl().processSaveAwardDatesBusinessRules(event);
        errorMap.removeFromErrorPath(errorPath);
        errorMap.removeFromErrorPath(AWARD_ERROR_PATH);
        errorMap.removeFromErrorPath(DOCUMENT_ERROR_PATH);
        return valid;
    }
    
    
    /**
    *
    * process Direct F and A Distribution business rules.
    */
    private boolean processAwardDirectFandADistributionBusinessRules(Document document) {
        boolean valid = true;
        MessageMap errorMap = GlobalVariables.getMessageMap();
        TimeAndMoneyDocument timeAndMoneyDocument = (TimeAndMoneyDocument) document;
        int i = 0;
        List<AwardDirectFandADistribution> awardDirectFandADistributions = timeAndMoneyDocument.getAward().getAwardDirectFandADistributions();
        errorMap.addToErrorPath(DOCUMENT_ERROR_PATH);
        errorMap.addToErrorPath(AWARD_ERROR_PATH);
        String errorPath = "awardDirectFandADistribution[" + i + Constants.RIGHT_SQUARE_BRACKET;
        errorMap.addToErrorPath(errorPath);
        AwardDirectFandADistributionRuleEvent event = new AwardDirectFandADistributionRuleEvent(errorPath, 
                                                            timeAndMoneyDocument, 
                                                                   awardDirectFandADistributions);
        valid &= new AwardDirectFandADistributionRuleImpl().processAwardDirectFandADistributionBusinessRules(event);
        errorMap.removeFromErrorPath(errorPath);
        errorMap.removeFromErrorPath(AWARD_ERROR_PATH);
        errorMap.removeFromErrorPath(DOCUMENT_ERROR_PATH);
        return valid;
    }

    public boolean processRunAuditBusinessRules(Document document){
        boolean retval = true;
        return retval;
    }


    @Override
    public boolean processAddPendingTransactionBusinessRules(AddTransactionRuleEvent event) {
        return new TransactionRuleImpl().processAddPendingTransactionBusinessRules(event);
    }


    @Override
    public boolean processPendingTransactionBusinessRules(TransactionRuleEvent event) {
        return new TransactionRuleImpl().processPendingTransactionBusinessRules(event);
    }

    /**
     * @see org.kuali.kra.award.detailsdates.AwardDetailsAndDatesRule#processAddAwardTransferringSponsorEvent
     * (org.kuali.kra.award.rule.event.AddAwardTransferringSponsorEvent)
     */
    @Override
    public boolean processAddAwardDirectFandADistributionBusinessRules(AwardDirectFandADistributionRuleEvent
                                                                                        awardDirectFandADistributionRuleEvent) {
        return new AwardDirectFandADistributionRuleImpl().processAddAwardDirectFandADistributionBusinessRules(awardDirectFandADistributionRuleEvent);
    }

    @Override
    public boolean processAwardDirectFandADistributionBusinessRules(
            AwardDirectFandADistributionRuleEvent awardDirectFandADistributionRuleEvent) {
        return new AwardDirectFandADistributionRuleImpl().processAwardDirectFandADistributionBusinessRules(awardDirectFandADistributionRuleEvent);
    }
    
    public ReportTrackingService getReportTrackingService() {
        if (reportTrackingService == null) {
            reportTrackingService = KcServiceLocator.getService(ReportTrackingService.class);
        }
        return reportTrackingService;
    }
    
    public AwardVersionService getAwardVersionService() {
        return KcServiceLocator.getService(AwardVersionService.class);
    }
    
}
