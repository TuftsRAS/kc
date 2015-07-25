/*
 * Kuali Coeus, a comprehensive research administration system for higher education.
 * 
 * Copyright 2005-2015 Kuali, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.coeus.common.budget.impl.calculator;

import org.apache.commons.collections4.CollectionUtils;
import org.kuali.coeus.common.budget.api.rate.RateClassType;
import org.kuali.coeus.common.budget.framework.calculator.*;
import org.kuali.coeus.common.budget.framework.query.*;
import org.kuali.coeus.common.budget.framework.query.operator.*;
import org.kuali.coeus.common.budget.framework.rate.BudgetRate;
import org.kuali.coeus.common.budget.framework.rate.BudgetRatesService;
import org.kuali.coeus.common.budget.framework.rate.RateClass;
import org.kuali.coeus.common.budget.framework.rate.RateType;
import org.kuali.coeus.common.budget.framework.rate.AbstractBudgetRate;
import org.kuali.coeus.common.budget.framework.rate.BudgetLaRate;
import org.kuali.coeus.common.budget.framework.rate.ValidCeRateType;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.award.commitments.FandaRateType;
import org.kuali.coeus.common.budget.framework.core.Budget;
import org.kuali.coeus.common.budget.framework.nonpersonnel.AbstractBudgetCalculatedAmount;
import org.kuali.coeus.common.budget.framework.nonpersonnel.BudgetLineItem;
import org.kuali.coeus.common.budget.framework.nonpersonnel.BudgetLineItemBase;
import org.kuali.coeus.common.budget.framework.personnel.BudgetPersonnelCalculatedAmount;
import org.kuali.coeus.common.budget.framework.personnel.BudgetPersonnelDetails;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.LegacyDataAdapter;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 
 * Base class for <code>LineItemCalculator</code> and <code>PersonnelLineItemCalculator</code>.
 */
public abstract class AbstractBudgetCalculator {
    public static final String RATE_CLASS_CODE = "rateClassCode";
    public static final String ON_OFF_CAMPUS_FLAG = "onOffCampusFlag";
    public static final String RATE_TYPE_CODE = "rateTypeCode";
    public static final String START_DATE = "startDate";
    private LegacyDataAdapter legacyDataAdapter;
    private DateTimeService dateTimeService;
    protected Budget budget;
    protected BudgetLineItemBase budgetLineItem;
    private QueryList<BudgetLaRate> lineItemPropLaRates;
    private QueryList<BudgetRate> lineItemPropRates;
    private List<BreakUpInterval> breakupIntervals;
    private QueryList<ValidCeRateType> infltionValidCalcCeRates;
    private QueryList<BudgetRate> underrecoveryRates;
    private QueryList<BudgetRate> inflationRates;
    private ParameterService parameterService;


    public AbstractBudgetCalculator(Budget budget, BudgetLineItemBase budgetLineItem) {
        this.budget = budget;
        this.budgetLineItem = budgetLineItem;
        legacyDataAdapter = KcServiceLocator.getService(LegacyDataAdapter.class);
        dateTimeService = CoreApiServiceLocator.getDateTimeService();
        breakupIntervals = new ArrayList<BreakUpInterval>();
    }
    /**
     * 
     * Abstract method to populate the applicable cost and applicable cost sharing in boundary.
     * For LineItemCalculator, applicableCost would be the line item cost and
     * for PersonnelLineItemCalculator applicable cost would be cumulative salary of all personnel line item
     * @param boundary
     */
    public abstract void populateApplicableCosts(Boundary boundary);

    /**
     * This method is for filtering rates and lab allocation rates. 
     * @param rates
     * @return
     */
    public QueryList filterRates(List rates) {
        String activityTypeCode = budget.getBudgetParent().getActivityTypeCode();
        if (!rates.isEmpty() && rates.get(0) instanceof BudgetRate) {
            QueryList qList = filterRates(rates, budgetLineItem.getStartDate(), budgetLineItem.getEndDate(), activityTypeCode);
            if (qList.isEmpty() && !budget.getActivityTypeCode().equals(activityTypeCode)) {
                qList = filterRates(rates, budgetLineItem.getStartDate(), budgetLineItem.getEndDate(), budget.getActivityTypeCode());                
            }
            return qList;
        }
        else {
            return filterRates(rates, budgetLineItem.getStartDate(), budgetLineItem.getEndDate(), null);
        }
    }
    /**
     * 
     * This method is for filtering rates between startdate and enddate. 
     * <code>activityTypeCode</code> will be null if its a lab allocation rates
     * @param rates
     * @param startDate
     * @param endDate
     * @param activityTypeCode
     * @return list of filtered rates
     */
    private QueryList filterRates(List rates, Date startDate, Date endDate, String activityTypeCode) {
        List<AbstractBudgetCalculatedAmount> lineItemCalcAmts = budgetLineItem.getBudgetCalculatedAmounts();
        QueryList qlRates = new QueryList(rates);
        QueryList budgetProposalRates = new QueryList();

        /*
         * Get all rates from Proposal Rates &amp; Proposal LA Rates which matches with the rates in line item cal amts
         */
        for (AbstractBudgetCalculatedAmount calAmtsBean : lineItemCalcAmts) {
            String rateClassCode = calAmtsBean.getRateClassCode();
            String rateTypeCode = calAmtsBean.getRateTypeCode();
            Equals equalsRC = new Equals(RATE_CLASS_CODE, rateClassCode);
            Equals equalsRT = new Equals(RATE_TYPE_CODE, rateTypeCode);
            Equals equalsOnOff = new Equals(ON_OFF_CAMPUS_FLAG, budgetLineItem.getOnOffCampusFlag());

            And RCandRT = new And(equalsRC, equalsRT);
            And RCRTandOnOff = new And(RCandRT, equalsOnOff);
            QueryList filteredRates = qlRates.filter(RCRTandOnOff);
            if (filteredRates != null && !filteredRates.isEmpty()) {
                budgetProposalRates.addAll(qlRates.filter(RCRTandOnOff));
            }
        }
        if (activityTypeCode != null) {
            // Add inflation rates separately because, calculated amount list will not have inflation rates listed
            if (infltionValidCalcCeRates != null && !infltionValidCalcCeRates.isEmpty()) {
                for (ValidCeRateType inflationValidceRate : infltionValidCalcCeRates) {
                    Equals equalsRC = new Equals(RATE_CLASS_CODE, inflationValidceRate.getRateClassCode());
                    Equals equalsRT = new Equals(RATE_TYPE_CODE, inflationValidceRate.getRateTypeCode());
                    Equals equalsOnOff = new Equals(ON_OFF_CAMPUS_FLAG, budgetLineItem.getOnOffCampusFlag());
                    And RCandRT = new And(equalsRC, equalsRT);
                    And RCRTandOnOff = new And(RCandRT, equalsOnOff);
                    Equals eActType = new Equals("activityTypeCode", activityTypeCode);
                    And RCRTandOnOffandActType = new And(RCRTandOnOff,eActType);
                    QueryList<BudgetRate> filteredRates = qlRates.filter(RCRTandOnOffandActType);
                    if (filteredRates != null && !filteredRates.isEmpty()) {
                        setInflationRates(filteredRates);
                        budgetProposalRates.addAll(filteredRates);
                    }
                }
            }
            // Add underrecovery rates
            if(!isUndercoveryMatchesOverhead()) {
                Equals equalsRC = new Equals(RATE_CLASS_CODE, budget.getUrRateClassCode());
                Equals equalsOnOff = new Equals(ON_OFF_CAMPUS_FLAG, budgetLineItem.getOnOffCampusFlag());
                And RCRTandOnOff = new And(equalsRC, equalsOnOff);
                budgetProposalRates.addAll(qlRates.filter(RCRTandOnOff));
            }

            Equals eActType = new Equals("activityTypeCode", activityTypeCode);
            budgetProposalRates = budgetProposalRates.filter(eActType);
        }

        if (budgetProposalRates != null && !budgetProposalRates.isEmpty()) {
            LesserThan lesserThan = new LesserThan(START_DATE, endDate);
            Equals equals = new Equals(START_DATE, endDate);
            Or or = new Or(lesserThan, equals);
            budgetProposalRates = budgetProposalRates.filter(or);
        }

        return budgetProposalRates;
    }

    private boolean isUndercoveryMatchesOverhead() {
        return budget.getOhRateClassCode().equals(budget.getUrRateClassCode());
    }

    /**
     * 
     * This method does the calculation for each line item. Both BudgetLineItem and BudgetPersonnelLineItem invokes this method to do the calculation.
     */
    public void calculate() {
        if (budget.getBudgetParent() instanceof DevelopmentProposal && ((DevelopmentProposal) budget.getBudgetParent()).isParent()) {
            budgetLineItem.setDirectCost(budgetLineItem.getLineItemCost());
            budgetLineItem.setTotalCostSharingAmount(budgetLineItem.getCostSharingAmount());
            budgetLineItem.setIndirectCost(ScaleTwoDecimal.ZERO);
            if (budgetLineItem instanceof BudgetPersonnelDetails) {
                BudgetPersonnelDetails budgetPersonnelLineItem = (BudgetPersonnelDetails)budgetLineItem;
                SalaryCalculator salaryCalculator = new SalaryCalculator(budget, budgetPersonnelLineItem);
                salaryCalculator.calculate();
                budgetPersonnelLineItem.setLineItemCost(budgetPersonnelLineItem.getSalaryRequested());
            }
            QueryList<AbstractBudgetCalculatedAmount> calcAmts = new QueryList<AbstractBudgetCalculatedAmount>();
            calcAmts.addAll(budgetLineItem.getBudgetCalculatedAmounts());
            for (AbstractBudgetCalculatedAmount calcAmt : calcAmts) {
                calcAmt.refreshReferenceObject("rateClass");
                calcAmt.setRateClassType(calcAmt.getRateClass().getRateClassTypeCode());
            }
            NotEquals notEqualsOH = new NotEquals("rateClassType", RateClassType.OVERHEAD.getRateClassType());
            Equals equalsOH = new Equals("rateClassType", RateClassType.OVERHEAD.getRateClassType());
            String calculatedCostString = "calculatedCost";
            budgetLineItem.setDirectCost(budgetLineItem.getDirectCost().add(calcAmts.sumObjects(calculatedCostString, notEqualsOH)));
            budgetLineItem.setIndirectCost(budgetLineItem.getIndirectCost().add(calcAmts.sumObjects(calculatedCostString, equalsOH)));
            budgetLineItem.setTotalCostSharingAmount(budgetLineItem.getTotalCostSharingAmount().add(
                    calcAmts.sumObjects("calculatedCostSharing")));
            return;
        }
        budgetLineItem.setDirectCost(budgetLineItem.getLineItemCost());
        budgetLineItem.setTotalCostSharingAmount(budgetLineItem.getCostSharingAmount());
        budgetLineItem.setIndirectCost(ScaleTwoDecimal.ZERO);
        budgetLineItem.setUnderrecoveryAmount(ScaleTwoDecimal.ZERO);
        createAndCalculateBreakupIntervals();
        updateBudgetLineItemCalculatedAmounts();
        populateBudgetRateBaseList();
    }
    /**
     * This abstract method should be implemented by LineItemCalculator and PersonnelLineItemCalculator.
     * It is for populating the RateAndBase amounts. 
     */
    protected abstract void populateBudgetRateBaseList();
    /**
     * 
     * This method is for populating the calculated amounts by looking at the rate class and rate type for each line item gets applied to.
     */
    @SuppressWarnings("unchecked")
    protected void updateBudgetLineItemCalculatedAmounts() {
        
        List<AbstractBudgetCalculatedAmount> lineItemCalcAmts = budgetLineItem.getBudgetCalculatedAmounts();
        List<BreakUpInterval> cvLIBreakupIntervals = getBreakupIntervals();
        if (lineItemCalcAmts != null && lineItemCalcAmts.size() > 0 && cvLIBreakupIntervals != null
                && cvLIBreakupIntervals.size() > 0) {
            /*
             * Sum up all the calculated costs for each breakup interval and then update the line item cal amts.
             */
            String rateClassCode = "0";
            String rateTypeCode = "0";
            ScaleTwoDecimal totalCalculatedCost = ScaleTwoDecimal.ZERO;
            ScaleTwoDecimal totalCalculatedCostSharing = ScaleTwoDecimal.ZERO;
            ScaleTwoDecimal totalUnderRecovery = ScaleTwoDecimal.ZERO;
            ScaleTwoDecimal directCost = ScaleTwoDecimal.ZERO;
            ScaleTwoDecimal indirectCost = ScaleTwoDecimal.ZERO;
            Equals equalsRC;
            Equals equalsRT;
            And RCandRT = null;
            QueryList<RateAndCost> cvCombinedAmtDetails = new QueryList<RateAndCost>();
            for (BreakUpInterval brkUpInterval : cvLIBreakupIntervals) {
                cvCombinedAmtDetails.addAll(brkUpInterval.getRateAndCosts());
            }
            for (AbstractBudgetCalculatedAmount calculatedAmount : lineItemCalcAmts) {
                rateClassCode = calculatedAmount.getRateClassCode();
                rateTypeCode = calculatedAmount.getRateTypeCode();
                equalsRC = new Equals(RATE_CLASS_CODE, rateClassCode);
                equalsRT = new Equals(RATE_TYPE_CODE, rateTypeCode);
                RCandRT = new And(equalsRC, equalsRT);
                totalCalculatedCost = cvCombinedAmtDetails.sumObjects("calculatedCost", RCandRT);

                calculatedAmount.setCalculatedCost(totalCalculatedCost);

                totalCalculatedCostSharing = cvCombinedAmtDetails.sumObjects("calculatedCostSharing", RCandRT);
                calculatedAmount.setCalculatedCostSharing(totalCalculatedCostSharing);
            }

            /*
             * Sum up all the underRecovery costs for each breakup interval and then update the line item details.
             */
            totalUnderRecovery = new QueryList<BreakUpInterval>(cvLIBreakupIntervals).sumObjects("underRecovery");
            budgetLineItem.setUnderrecoveryAmount(totalUnderRecovery);

            /*
             * Sum up all direct costs ie, rates for RateClassType <> 'O', for each breakup interval plus the line item cost, and
             * then update the line item details.
             */
            NotEquals notEqualsOH = new NotEquals("rateClassType", RateClassType.OVERHEAD.getRateClassType());
            boolean directCostRolledUp = false;
            boolean resetTotalUnderRecovery = false;
            ScaleTwoDecimal newTotalUrAmount = ScaleTwoDecimal.ZERO;
            ScaleTwoDecimal newTotalCostSharing = ScaleTwoDecimal.ZERO;
            if (budgetLineItem instanceof BudgetLineItem && CollectionUtils.isNotEmpty(((BudgetLineItem)budgetLineItem).getBudgetPersonnelDetailsList())) {
                for (BudgetPersonnelDetails budgetPersonnelDetail : ((BudgetLineItem)budgetLineItem).getBudgetPersonnelDetailsList()) {
                    List<BudgetPersonnelCalculatedAmount> personnelCalAmts = budgetPersonnelDetail.getBudgetCalculatedAmounts();
                    newTotalUrAmount = newTotalUrAmount.add(budgetPersonnelDetail.getUnderrecoveryAmount());
                    resetTotalUnderRecovery = true;
                    if (CollectionUtils.isNotEmpty(personnelCalAmts)) {
                        for (BudgetPersonnelCalculatedAmount personnelCalAmt : personnelCalAmts) {
                            if (personnelCalAmt.getRateClass() == null) {
                                personnelCalAmt.refreshReferenceObject("rateClass");
                            }
                            if (!personnelCalAmt.getRateClass().getRateClassTypeCode().equals("O")) {
                                directCost = directCost.add(personnelCalAmt.getCalculatedCost());
                            } else {
                                indirectCost = indirectCost.add(personnelCalAmt.getCalculatedCost());
                                
                            }
                            newTotalCostSharing = newTotalCostSharing.add(personnelCalAmt.getCalculatedCostSharing());
                            directCostRolledUp = true;

                            
                        }
                    }
                }
            }
            if (resetTotalUnderRecovery) {
                budgetLineItem.setUnderrecoveryAmount(newTotalUrAmount);
            }
            if (!directCostRolledUp) {
                directCost = cvCombinedAmtDetails.sumObjects("calculatedCost", notEqualsOH);
            }
            budgetLineItem.setDirectCost(directCost.add(budgetLineItem.getLineItemCost()));
            /*
             * Sum up all Indirect costs ie, rates for RateClassType = 'O', for each breakup interval and then update the line item
             * details.
             */
            Equals equalsOH = new Equals("rateClassType", RateClassType.OVERHEAD.getRateClassType());
            if (!directCostRolledUp) {
                indirectCost = cvCombinedAmtDetails.sumObjects("calculatedCost", equalsOH);
            }
            budgetLineItem.setIndirectCost(indirectCost);

            /*
             * Sum up all Cost Sharing amounts ie, rates for RateClassType <> 'O' and set in the calculatedCostSharing field of line
             * item details
             */
            if (!directCostRolledUp) {
                totalCalculatedCostSharing = cvCombinedAmtDetails.sumObjects("calculatedCostSharing");
            } 
            else {
                totalCalculatedCostSharing = newTotalCostSharing;
            }

            budgetLineItem.setTotalCostSharingAmount(budgetLineItem.getCostSharingAmount() == null ? 
                        totalCalculatedCostSharing : 
                        budgetLineItem.getCostSharingAmount().add(totalCalculatedCostSharing));

        } else if (lineItemCalcAmts != null && lineItemCalcAmts.size() > 0 && 
                (budgetLineItem.getLineItemCost().equals(ScaleTwoDecimal.ZERO) ||
                        CollectionUtils.isEmpty(cvLIBreakupIntervals))) {
            for (AbstractBudgetCalculatedAmount calculatedAmount : lineItemCalcAmts) {
                  calculatedAmount.setCalculatedCost(ScaleTwoDecimal.ZERO);
                  calculatedAmount.setCalculatedCostSharing(ScaleTwoDecimal.ZERO);
          }
        } 
    }


    protected void createAndCalculateBreakupIntervals() {
        populateCalculatedAmountLineItems();
        setQlLineItemPropLaRates(filterRates(budget.getBudgetLaRates()));
        setQlLineItemPropRates(filterRates(budget.getBudgetRates()));
        createBreakUpInterval();
        calculateBreakUpInterval();
    }

    /**
     * Combine the sorted Prop &amp; LA rates, which should be in sorted order(asc). Now create the breakup boundaries and use it to
     * create breakup intervals and set all the values required for calculation. Then call calculateBreakupInterval method for each
     * AmountBean for setting the calculated cost &amp; calculated cost sharing ie for each rate class &amp; rate type.
     */
    protected void createBreakUpInterval() {
        String messageTemplate = "";
        String multipleRatesMesgTemplate = "";
        String message = "";
        if (breakupIntervals == null)
            breakupIntervals = new ArrayList<BreakUpInterval>();

        if (budgetLineItem.getOnOffCampusFlag()) {
            messageTemplate = "On-Campus rate information not available for Rate Class - \'";
            multipleRatesMesgTemplate = "Multiple On-Campus rates found for the period ";
        }
        else {
            messageTemplate = "Off-Campus rate information not available for Rate Class - \'";
            multipleRatesMesgTemplate = "Multiple Off-Campus rates found for the period ";
        }

        QueryList<BudgetLaRate> qlLineItemPropLaRates = getQlLineItemPropLaRates();
        QueryList<BudgetRate> qlLineItemPropRates = getQlLineItemPropRates();

        // combine the sorted Prop & LA Rates
        QueryList qlCombinedRates = new QueryList();
        qlCombinedRates.addAll(qlLineItemPropRates);
        qlCombinedRates.addAll(qlLineItemPropLaRates);

        qlCombinedRates.sort(START_DATE, true);
        Date liStartDate = budgetLineItem.getStartDate();
        Date liEndDate = budgetLineItem.getEndDate();
        List<Boundary> boundaries = createBreakupBoundaries(qlCombinedRates, liStartDate, liEndDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-dd-yyyy");
        // create breakup intervals based on the breakup boundaries
        if (boundaries != null && boundaries.size() > 0) {
            for (Boundary boundary : boundaries) {
                BreakUpInterval breakUpInterval = new BreakUpInterval();
                breakUpInterval.setBoundary(boundary);
                breakUpInterval.setBudgetId(budgetLineItem.getBudgetId());
                breakUpInterval.setBudgetPeriod(budgetLineItem.getBudgetPeriod());
                breakUpInterval.setLineItemNumber(budgetLineItem.getLineItemNumber());
                QueryList<RateAndCost> qlRateAndCosts = new QueryList<RateAndCost>();
                QueryList<BudgetRate> qlBreakupPropRates = new QueryList<BudgetRate>();
                QueryList<BudgetLaRate> qlBreakupPropLARates = new QueryList<BudgetLaRate>();
                QueryList qlTempRates = new QueryList();
                QueryList qlMultipleRates = new QueryList();
                String rateClassType;
                String rateClassCode;
                String rateTypeCode;
                Boolean applyRateFlag;
                populateApplicableCosts(boundary);
                breakUpInterval.setApplicableAmt(boundary.getApplicableCost());
                breakUpInterval.setApplicableAmtCostSharing(boundary.getApplicableCostSharing());
                
                List<AbstractBudgetCalculatedAmount> qlLineItemCalcAmts = budgetLineItem.getBudgetCalculatedAmounts();
                
                List<String> warningMessages = new ArrayList<String>();

                for (AbstractBudgetCalculatedAmount budgetLineItemCalculatedAmount : qlLineItemCalcAmts) {
                    applyRateFlag = budgetLineItemCalculatedAmount.getApplyRateFlag();
                    rateClassCode = budgetLineItemCalculatedAmount.getRateClassCode();
                    rateTypeCode = budgetLineItemCalculatedAmount.getRateTypeCode();
                    // form the rate not available message
                    // These two statements have to move to the populate method of calculatedAmount later.
                    if (budgetLineItemCalculatedAmount.getRateClass() == null && rateClassCode != null) {
                    	budgetLineItemCalculatedAmount.setRateClass(getLegacyDataAdapter().findBySinglePrimaryKey(RateClass.class, rateClassCode));
                    }
                    rateClassType = budgetLineItemCalculatedAmount.getRateClass().getRateClassTypeCode();
                    // end block to be moved
                    message = messageTemplate + budgetLineItemCalculatedAmount.getRateClass().getDescription()
                            + "\'  Rate Type - \'" + budgetLineItemCalculatedAmount.getRateTypeDescription()
                            + "\' for Period - ";

                    // if apply flag is false and rate class type is not Overhead then skip
                    if ((applyRateFlag==null || !applyRateFlag) && !rateClassType.equals(RateClassType.OVERHEAD.getRateClassType())) {
                        continue;
                    }

                    RateAndCost rateCost = new RateAndCost();
                    rateCost.setApplyRateFlag(applyRateFlag);
                    rateCost.setRateClassType(rateClassType);
                    rateCost.setRateClassCode(rateClassCode);
                    rateCost.setRateTypeCode(rateTypeCode);
                    rateCost.setCalculatedCost(ScaleTwoDecimal.ZERO);
                    rateCost.setCalculatedCostSharing(ScaleTwoDecimal.ZERO);

                    qlRateAndCosts.add(rateCost);

                    // filter & store the rates applicable for this rate class / rate type
                    Equals equalsRC = new Equals(RATE_CLASS_CODE, rateClassCode);
                    Equals equalsRT = new Equals(RATE_TYPE_CODE, rateTypeCode);
                    LesserThan ltEndDate = new LesserThan(START_DATE, boundary.getEndDate());
                    Equals equalsEndDate = new Equals(START_DATE, boundary.getEndDate());
                    GreaterThan gtStartDate = new GreaterThan(START_DATE, boundary.getStartDate());
                    Equals equalsStartDate = new Equals(START_DATE, boundary.getStartDate());
                    Or gtStartDateOrEqStartDate = new Or(gtStartDate, equalsStartDate);
                    Or ltEndDateOrEqEndDate = new Or(ltEndDate, equalsEndDate);
                    And gtOrEqStartDateAndltOrEqEndDate = new And(gtStartDateOrEqStartDate, ltEndDateOrEqEndDate);
                    And RCandRT = new And(equalsRC, equalsRT);
                    And RCRTandLtStartDate = new And(RCandRT, ltEndDateOrEqEndDate);
                    And RCRTandgtStartDateAndltEndDate = new And(RCandRT, gtOrEqStartDateAndltOrEqEndDate);

                    if (rateClassType.equalsIgnoreCase(RateClassType.LAB_ALLOCATION.getRateClassType())
                            || rateClassType.equalsIgnoreCase(RateClassType.LA_SALARIES.getRateClassType())) {
                        qlTempRates = qlLineItemPropLaRates.filter(RCRTandLtStartDate);
                        if (qlTempRates != null && qlTempRates.size() > 0) {
                            /*
                             * Check if multiple rates are present got this period. If there, then show message and don't add any
                             * rates.
                             */
                            List cvMultipleRates = qlLineItemPropLaRates.filter(RCRTandgtStartDateAndltEndDate);
                            if (qlMultipleRates != null && qlMultipleRates.size() > 1) {
                                // Store the multiple rates available message in a message vector
                                message = multipleRatesMesgTemplate + simpleDateFormat.format(boundary.getStartDate()) + " to "
                                        + simpleDateFormat.format(boundary.getEndDate()) + " for Rate Class - \'"
                                        + budgetLineItemCalculatedAmount.getRateClass().getDescription() + "\'  Rate Type - \'"
                                        + budgetLineItemCalculatedAmount.getRateTypeDescription();
                                warningMessages.add(message);

                            }
                            else {
                                /**
                                 * sort the rates in desc order and take the first rate which is the latest
                                 */
                                qlTempRates.sort(START_DATE, false);
                                BudgetLaRate tempPropLaRate = (BudgetLaRate) qlTempRates.get(0);
                                qlBreakupPropLARates.add(tempPropLaRate);
                            }
                        }
                        else {
                            // Store the rate not available message in a message vector

                            message = message + simpleDateFormat.format(boundary.getStartDate()) + " to "
                                    + simpleDateFormat.format(boundary.getEndDate());
                            warningMessages.add(message);
                        }
                    }
                    else {
                        qlTempRates = qlLineItemPropRates.filter(RCRTandLtStartDate);

                        if (qlTempRates != null && qlTempRates.size() > 0) {

                            /**
                             * Check if multiple rates are present for this boundary. If there, then show message and don't add any
                             * rates.
                             */
                            qlMultipleRates = qlLineItemPropRates.filter(RCRTandgtStartDateAndltEndDate);
                            if (qlMultipleRates != null && qlMultipleRates.size() > 1) {

                                // Store the multiple rates available message in a message vector

                                message = multipleRatesMesgTemplate + simpleDateFormat.format(boundary.getStartDate()) + " to "
                                        + simpleDateFormat.format(boundary.getEndDate()) + " for Rate Class - \'"
                                        + budgetLineItemCalculatedAmount.getRateClass().getDescription() + "\'  Rate Type - \'"
                                        + budgetLineItemCalculatedAmount.getRateTypeDescription();
                                warningMessages.add(message);
                            }
                            else {
                                /**
                                 * sort the rates in desc order and take the first rate which is the latest
                                 */
                                qlTempRates.sort(START_DATE, false);
                                qlBreakupPropRates.add((BudgetRate) qlTempRates.get(0));
                            }
                        }
                        else {
                            // Store the rate not available message in a message vector
                            message = message + simpleDateFormat.format(boundary.getStartDate()) + " to "
                                    + simpleDateFormat.format(boundary.getEndDate());
                            warningMessages.add(message);
                        }
                    }

                }// breakup interval data setting loop ends here

                // set the values for the breakup interval in the BreakupInterval bean
                if (qlRateAndCosts != null && qlRateAndCosts.size() > 0) {
                    breakUpInterval.setRateAndCosts(qlRateAndCosts);
                    breakUpInterval.setBudgetProposalRates(qlBreakupPropRates);
                    breakUpInterval.setBudgetProposalLaRates(qlBreakupPropLARates);
                    breakupIntervals.add(breakUpInterval);
                }

                QueryList<ValidCeRateType> underrecoveryRates = getRateMappedToCostElement();
                // get the rate class, rate type from the cost element and use that.
                if (!isUndercoveryMatchesOverhead() && !underrecoveryRates.isEmpty()) {
                    // you cannot have more than one rate mapped to this cost element, so always use the first.
                    // if there's more than one mapped, it is wrong.
                    Equals equalsRC = new Equals(RATE_CLASS_CODE, underrecoveryRates.get(0).getRateClassCode());
                    Equals equalsOnOff = new Equals(ON_OFF_CAMPUS_FLAG, budgetLineItem.getOnOffCampusFlag());
                    Equals equalsRT = new Equals(RATE_TYPE_CODE, underrecoveryRates.get(0).getRateTypeCode());
                    And RCandRT = new And(equalsRC, equalsRT);
                    And RCRTandOnOff = new And(RCandRT, equalsOnOff);

                    QueryList<BudgetRate> qlUnderRecoveryRates = qlLineItemPropRates.filter(RCRTandOnOff);
                    if (qlUnderRecoveryRates != null && qlUnderRecoveryRates.size() > 0) {
                        LesserThan ltEndDate = new LesserThan(START_DATE, boundary.getEndDate());
                        Equals equalsEndDate = new Equals(START_DATE, boundary.getEndDate());
                        Or ltEndDateOrEqEndDate = new Or(ltEndDate, equalsEndDate);
                        qlTempRates = qlUnderRecoveryRates.filter(ltEndDateOrEqEndDate);
                        if (qlTempRates != null && qlTempRates.size() > 0) {
                            /*
                             * sort the rates in desc order and take the first rate which is the latest
                             */
                            qlTempRates.sort(START_DATE, false);
                            breakUpInterval.setURRatesBean((BudgetRate) qlTempRates.get(0));
                        }
                    }
                }
            }
        }
    }

    private QueryList<ValidCeRateType> getRateMappedToCostElement() {
        Equals equalsRC = new Equals(RATE_CLASS_CODE, budget.getUrRateClassCode());
        Equals equalsRCT = new Equals("rateClassType", RateClassType.OVERHEAD.getRateClassType());
        And RCRTandRCT = new And(equalsRC, equalsRCT);
        if(budgetLineItem.getCostElementBO()!=null && budgetLineItem.getCostElementBO().getValidCeRateTypes().isEmpty() ){
            budgetLineItem.getCostElementBO().refreshReferenceObject("validCeRateTypes");
        }
        QueryList<ValidCeRateType> validCeRateTypes = new QueryList<>(budgetLineItem.getCostElementBO().getValidCeRateTypes());
        return validCeRateTypes.filter(RCRTandRCT);
    }

    public String getPersonnelBudgetCategoryTypeCode() {
        return getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_BUDGET, ParameterConstants.DOCUMENT_COMPONENT,Constants.BUDGET_CATEGORY_TYPE_PERSONNEL);
    }




    /**
     * Use the combined &amp; sorted Prop &amp; LA rates to create Boundary objects. Each Boundary will contain start date &amp; end date. Check
     * whether any rate changes, and break at this point to create a new boundary.
     * 
     * @return List of boundary objects
     */
    public List<Boundary> createBreakupBoundaries(QueryList<AbstractBudgetRate> qlCombinedRates, Date liStartDate,
            Date liEndDate) {
        List<Boundary> boundaries = new ArrayList<Boundary>();
        if (qlCombinedRates != null && qlCombinedRates.size() > 0) {
            Date tempStartDate = liStartDate;
            Date tempEndDate = liEndDate;
            Date rateChangeDate;
            GreaterThan greaterThan = new GreaterThan(START_DATE, liStartDate);
            qlCombinedRates = qlCombinedRates.filter(greaterThan);
            qlCombinedRates.sort(START_DATE, true);
            for (AbstractBudgetRate laRate : qlCombinedRates) {
                rateChangeDate = laRate.getStartDate();
                if (rateChangeDate.after(tempStartDate)) {
                    Calendar temEndCal = dateTimeService.getCalendar(rateChangeDate);
                    temEndCal.add(Calendar.DAY_OF_MONTH, -1);
                    try {
                        tempEndDate = dateTimeService.convertToSqlDate(temEndCal.get(Calendar.YEAR)+"-"+(temEndCal.get(Calendar.MONTH)+1)+"-"+temEndCal.get(Calendar.DAY_OF_MONTH));
                    }
                    catch (ParseException e) {
                        tempEndDate = new Date(rateChangeDate.getTime() - 86400000);
                    }
                    Boundary boundary = new Boundary(tempStartDate, tempEndDate);
                    boundaries.add(boundary);
                    tempStartDate = rateChangeDate;
                }
            }
            /**
             * add one more boundary if no rate change on endDate and atleast one boundary is present
             */
            if (boundaries.size() > 0) {
                Boundary boundary = new Boundary(tempStartDate, liEndDate);
                boundaries.add(boundary);
            }
            /**
             * if no rate changes during the period create one boundary with startDate &amp; endDate same as that for line item
             */
            if (boundaries.size() == 0) {
                Boundary boundary = new Boundary(liStartDate, liEndDate);
                boundaries.add(boundary);
            }
        }
        return boundaries;
    } // end createBreakupBoundaries

    protected void calculateBreakUpInterval() {
        int rateNumber = 0;
        List<BreakUpInterval> cvLIBreakupIntervals = getBreakupIntervals();
        for (BreakUpInterval breakUpInterval : cvLIBreakupIntervals) {
            breakUpInterval.setRateNumber(rateNumber);
            getBreakupIntervalService().calculate(breakUpInterval);
        }
    }

    private BreakupIntervalService getBreakupIntervalService() {
        return KcServiceLocator.getService(BreakupIntervalService.class);
    }
    protected List<ValidCalcType> getValidCalcTypes() {
        return (List<ValidCalcType>) legacyDataAdapter.findAll(ValidCalcType.class);
    }

    protected abstract void populateCalculatedAmountLineItems();

    private <T> QueryList<T> createQueryList(List<T> immutableList) {
        if (immutableList == null) {
            return new QueryList();
        }

        return new QueryList(immutableList);
    }

    private void setInflationRateOnLineItem(BudgetLineItemBase lineItem) {
        QueryList<ValidCeRateType> qValidCeRateTypes = createQueryList(budgetLineItem.getCostElementBO().getValidCeRateTypes());

        // Check whether it contains Inflation Rate
        QueryList<ValidCeRateType> inflationValidCeRates = 
            qValidCeRateTypes.filter(new Equals("rateClassType", RateClassType.INFLATION.getRateClassType()));
        if (!inflationValidCeRates.isEmpty()) {
            if (lineItem.getApplyInRateFlag()) {
                setInfltionValidCalcCeRates(inflationValidCeRates);
            }
        }
        else {
            lineItem.setApplyInRateFlag(false);
        }
        
    }

    private Equals equalsOverHeadRateClassType() {
        return new Equals("rateClassType", RateClassType.OVERHEAD.getRateClassType());
    }

    private NotEquals notEqualsInflationRateClassType() {
        return new NotEquals("rateClassType", RateClassType.INFLATION.getRateClassType());
    }

    private Equals equalsOverHeadRateClassCode() {
        return new Equals(RATE_CLASS_CODE, "" + budget.getOhRateClassCode());
    }

    private NotEquals notEqualsOverHeadRateClassType() {
        return new NotEquals("rateClassType", RateClassType.OVERHEAD.getRateClassType());
    }

    private And notEqualsLabAllocationRateClassType() {
        return new NotEquals("rateClassType", RateClassType.LAB_ALLOCATION.getRateClassType())
            .and(new NotEquals("rateClassType", RateClassType.LA_SALARIES.getRateClassType()));
    }

    
    private void setValidCeRateTypeCalculatedAmounts(BudgetLineItemBase lineItem) {
        QueryList<ValidCeRateType> qValidCeRateTypes = createQueryList(budgetLineItem.getCostElementBO().getValidCeRateTypes());
        qValidCeRateTypes = qValidCeRateTypes.filter(equalsOverHeadRateClassType()
                                                     .and(equalsOverHeadRateClassCode())
                                                     .or(notEqualsOverHeadRateClassType())
                                                     .and(notEqualsInflationRateClassType()));

        List<BudgetLaRate> budgetLaRates = budget.getBudgetLaRates();
        if (budgetLaRates == null || budgetLaRates.size() == 0) {
            qValidCeRateTypes = qValidCeRateTypes.filter(notEqualsLabAllocationRateClassType());
        }

        addBudgetLineItemCalculatedAmountsForRateTypes(qValidCeRateTypes);
    }

    private void addBudgetLineItemCalculatedAmountsForRateTypes(List<ValidCeRateType> rateTypes) {
        if (CollectionUtils.isEmpty(rateTypes)) {
            return;
        }

        for (ValidCeRateType validCeRateType : rateTypes) {
            validCeRateType.refreshNonUpdateableReferences();
            String rateClassType = validCeRateType.getRateClass().getRateClassTypeCode();
            if(rateClassType.equals(RateClassType.OVERHEAD.getRateClassType()) && 
                    !budget.getBudgetParent().isProposalBudget()){
                addOHBudgetLineItemCalculatedAmountForAward( validCeRateType.getRateClassCode(), validCeRateType.getRateType(), 
                        validCeRateType.getRateClass().getRateClassTypeCode());
            }else{
                addBudgetLineItemCalculatedAmount( validCeRateType.getRateClassCode(), validCeRateType.getRateType(), 
                                            validCeRateType.getRateClass().getRateClassTypeCode());
            }
        }
    }

    private void addOHBudgetLineItemCalculatedAmountForAward(String rateClassCode, 
            RateType rateType, String rateClassType) {
        QueryList<BudgetRate> budgetRates = new QueryList<BudgetRate>(budget.getBudgetRates());
        Equals eqOhRateClassType = new Equals("rateClassType",rateClassType);
        Equals eqOhRateClassOnCampusFlag = new Equals(ON_OFF_CAMPUS_FLAG,budgetLineItem.getOnOffCampusFlag());
        And eqRateClassTypeAndOhCampusFlag = new And(eqOhRateClassType,eqOhRateClassOnCampusFlag);
        List<BudgetRate> filteredBudgetRates = budgetRates.filter(eqRateClassTypeAndOhCampusFlag);
        if(!filteredBudgetRates.isEmpty()){
            BudgetRate awardBudgetRate = filteredBudgetRates.get(0);
            awardBudgetRate.setBudget(budget);
            if(awardBudgetRate.getNonEditableRateFlag()){
                AbstractBudgetCalculatedAmount budgetCalculatedAmount = getNewCalculatedAmountInstance();
                budgetCalculatedAmount.setBudgetId(budgetLineItem.getBudgetId());
                budgetCalculatedAmount.setBudgetPeriod(budgetLineItem.getBudgetPeriod());
                budgetCalculatedAmount.setBudgetPeriodId(budgetLineItem.getBudgetPeriodId());
                budgetCalculatedAmount.setLineItemNumber(budgetLineItem.getLineItemNumber());
                budgetCalculatedAmount.setRateClassType(rateClassType);
                budgetCalculatedAmount.setRateClassCode(awardBudgetRate.getRateClassCode());
                budgetCalculatedAmount.setRateTypeCode(awardBudgetRate.getRateTypeCode());
                budgetCalculatedAmount.setApplyRateFlag(true);
                budgetCalculatedAmount.setRateTypeDescription(getAwardRateTypeDescription(awardBudgetRate.getRateTypeCode()));
                budgetCalculatedAmount.setRateClass(awardBudgetRate.getRateClass());
                addCalculatedAmount(budgetCalculatedAmount);
            }else{
                addBudgetLineItemCalculatedAmount( rateClassCode, rateType, rateClassType);
            }
        }

    }
    private String getAwardRateTypeDescription(String rateTypeCode) {
        return getLegacyDataAdapter().findBySinglePrimaryKey(FandaRateType.class, rateTypeCode).getDescription();
        
    }
    private Equals equalsEmployeeBenefitsRateClassType() {
        return new Equals("rateClassType", RateClassType.EMPLOYEE_BENEFITS.getRateClassType());
    }

    private Equals equalsVacationRateClassType() {
        return new Equals("rateClassType", RateClassType.VACATION.getRateClassType());
    }

    private Equals equalsLabAllocationSalariesRateClassType() {
        return new Equals("rateClassType", RateClassType.LA_SALARIES.getRateClassType());
    }

    private void setLabAllocationSalariesCalculatedAmounts(BudgetLineItemBase lineItem) {
        QueryEngine queryEngine = new QueryEngine();
        queryEngine.addDataCollection(ValidCalcType.class, getValidCalcTypes());

        QueryList<ValidCeRateType> qValidCeRateTypes = createQueryList(budgetLineItem.getCostElementBO().getValidCeRateTypes());
        QueryList<ValidCeRateType> qLabAllocSalRates = qValidCeRateTypes.filter(equalsLabAllocationSalariesRateClassType());

        if (CollectionUtils.isNotEmpty(qLabAllocSalRates)) {
            List<ValidCalcType> validCalCTypes = queryEngine.executeQuery(ValidCalcType.class, equalsEmployeeBenefitsRateClassType());
            if (CollectionUtils.isNotEmpty(validCalCTypes)) {
                ValidCalcType validCalcType = validCalCTypes.get(0);
                if (validCalcType.getDependentRateClassType().equals(RateClassType.LA_SALARIES.getRateClassType())) {
                    addBudgetLineItemCalculatedAmount(validCalcType.getRateClassCode(), validCalcType
                            .getRateType(), validCalcType.getRateClassType());
                }
            }
            validCalCTypes = queryEngine.executeQuery(ValidCalcType.class, equalsVacationRateClassType());
            if (!validCalCTypes.isEmpty()) {
                ValidCalcType validCalcType = (ValidCalcType) validCalCTypes.get(0);
                if (validCalcType.getDependentRateClassType().equals(RateClassType.LA_SALARIES.getRateClassType())) {
                    addBudgetLineItemCalculatedAmount(validCalcType.getRateClassCode(), validCalcType
                            .getRateType(), validCalcType.getRateClassType());
                }
            }
        }
    }
    
    public final void setCalculatedAmounts(Budget budget, BudgetLineItemBase budgetLineItem) {
        if (budgetLineItem.getCostElementBO() == null) {
        	budgetLineItem.refreshReferenceObject("costElementBO");
        }

        if (budgetLineItem.getCostElementBO().getValidCeRateTypes().isEmpty()) {
        	budgetLineItem.getCostElementBO().refreshReferenceObject("validCeRateTypes");
        }

        setInflationRateOnLineItem(budgetLineItem);

        setValidCeRateTypeCalculatedAmounts(budgetLineItem);

        setLabAllocationSalariesCalculatedAmounts(budgetLineItem);
    }

    protected void setInfltionValidCalcCeRates(QueryList<ValidCeRateType> infltionValidCalcCeRates) {
        this.infltionValidCalcCeRates = infltionValidCalcCeRates;
    }
    
    private void addBudgetLineItemCalculatedAmount(String rateClassCode, RateType rateType, String rateClassType){
        
        QueryList<BudgetRate> budgetRates = new QueryList<BudgetRate>(budget.getBudgetRates());
        QueryList<BudgetLaRate> qlBudgetLaRates = new QueryList<BudgetLaRate>(budget.getBudgetLaRates());
        Equals eqValidRateClassCode = new Equals(RATE_CLASS_CODE,rateClassCode);
        Equals eqValidRateTypeCode = new Equals(RATE_TYPE_CODE,rateType.getRateTypeCode());
        And eqRateClassCodeAndRateTypeCode = new And(eqValidRateClassCode,eqValidRateTypeCode);
        List<BudgetRate> filteredBudgetRates = budgetRates.filter(eqRateClassCodeAndRateTypeCode);
        List<BudgetLaRate> filteredBudgetLaRates = qlBudgetLaRates.filter(eqRateClassCodeAndRateTypeCode);
        
        if(filteredBudgetRates.isEmpty() && filteredBudgetLaRates.isEmpty()) return;

        AbstractBudgetCalculatedAmount budgetCalculatedAmount = getNewCalculatedAmountInstance();
        budgetCalculatedAmount.setBudgetId(budgetLineItem.getBudgetId());
        budgetCalculatedAmount.setBudgetPeriod(budgetLineItem.getBudgetPeriod());
        budgetCalculatedAmount.setBudgetPeriodId(budgetLineItem.getBudgetPeriodId());
        budgetCalculatedAmount.setLineItemNumber(budgetLineItem.getLineItemNumber());
        budgetCalculatedAmount.setRateClassType(rateClassType);
        budgetCalculatedAmount.setRateClassCode(rateClassCode);
        budgetCalculatedAmount.setRateTypeCode(rateType.getRateTypeCode());
        budgetCalculatedAmount.setApplyRateFlag(true);
        budgetCalculatedAmount.refreshReferenceObject("rateClass");
        budgetCalculatedAmount.setRateTypeDescription(rateType.getDescription());
        addCalculatedAmount(budgetCalculatedAmount);
    }

    protected abstract AbstractBudgetCalculatedAmount getNewCalculatedAmountInstance();
    
    protected abstract void addCalculatedAmount(AbstractBudgetCalculatedAmount budgetCalculatedAmount);

    public LegacyDataAdapter getLegacyDataAdapter() {
        return legacyDataAdapter;
    }

    public void setLegacyDataAdapter(LegacyDataAdapter legacyDataAdapter) {
        this.legacyDataAdapter = legacyDataAdapter;
    }

    protected List<BreakUpInterval> getBreakupIntervals() {
        return breakupIntervals;
    }

    protected void setBreakupIntervals(List<BreakUpInterval> breakupIntervals) {
        this.breakupIntervals = breakupIntervals;
    }

    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public QueryList<BudgetRate> getUnderrecoveryRates() {
        return underrecoveryRates;
    }

    public void setUnderrecoveryRates(QueryList<BudgetRate> underrecoveryRates) {
        this.underrecoveryRates = underrecoveryRates;
    }

    public QueryList<BudgetRate> getInflationRates() {
        return inflationRates;
    }

    public void setInflationRates(QueryList<BudgetRate> inflationRates) {
        this.inflationRates = inflationRates;
    }

    public QueryList<BudgetLaRate> getQlLineItemPropLaRates() {
        return lineItemPropLaRates;
    }

    public void setQlLineItemPropLaRates(QueryList<BudgetLaRate> qlLineItemPropLaRates) {
        this.lineItemPropLaRates = qlLineItemPropLaRates;
    }

    public QueryList<BudgetRate> getQlLineItemPropRates() {
        return lineItemPropRates;
    }

    public void setQlLineItemPropRates(QueryList<BudgetRate> qlLineItemPropRates) {
        this.lineItemPropRates = qlLineItemPropRates;
    }
    
    protected BudgetRatesService getBudgetRateService() {
        return KcServiceLocator.getService(BudgetRatesService.class);
    }


    public ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = KcServiceLocator.getService(ParameterService.class);
        }
        return parameterService;
    }
}
