/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.budget.impl.calculator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.budget.api.rate.RateClassType;
import org.kuali.coeus.common.budget.framework.core.Budget;
import org.kuali.coeus.common.budget.framework.core.CostElement;
import org.kuali.coeus.common.budget.framework.core.DateSortable;
import org.kuali.coeus.common.budget.framework.personnel.BudgetPerson;
import org.kuali.coeus.common.budget.framework.personnel.BudgetPersonnelDetails;
import org.kuali.coeus.common.budget.framework.query.QueryList;
import org.kuali.coeus.common.budget.framework.query.operator.*;
import org.kuali.coeus.common.budget.framework.rate.BudgetRate;
import org.kuali.coeus.common.budget.framework.rate.ValidCeRateType;
import org.kuali.coeus.sys.api.model.ScaleTwoDecimal;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SalaryCalculator {
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(SalaryCalculator.class);
    private static final String STRING_1 = "1";
    private static final ScaleTwoDecimal DEFAULT_WORKING_MONTHS = new ScaleTwoDecimal(12);
    private static final String APPOINTMENT_TYPE = "appointmentType";

    private final Budget budget;
    private final BudgetPersonnelDetails personnelLineItem;

    private Date startDate;
    private Date endDate;
    private QueryList<BudgetRate> inflationRates;

    private DateTimeService dateTimeService;
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;

    public SalaryCalculator(Budget budget, BudgetPersonnelDetails personnelLineItem) {
        this.budget = budget;
        this.personnelLineItem = personnelLineItem;
        this.startDate = personnelLineItem.getStartDate();
        this.endDate = personnelLineItem.getEndDate();
    }

    /**
     *
     * This method is for calculating the salary for a personnel line item
     */
    public void calculate() {
        Boundary boundary = new Boundary(personnelLineItem.getStartDate(), personnelLineItem.getEndDate());
        calculate(boundary);
        personnelLineItem.setSalaryRequested(boundary.getApplicableCost());
        personnelLineItem.setCostSharingAmount(boundary.getApplicableCostSharing());
        personnelLineItem.setCostSharingPercent(calculateCostSharingPercentage());
    }

    /**
     *
     * This method is for calculating the salary for a personnel line item within a boundary. This is used mainly for calculating
     * the breakup interval.
     *
     */
    final void calculate(Boundary boundary) {
        this.startDate = boundary.getStartDate();
        this.endDate = boundary.getEndDate();
        ScaleTwoDecimal totalSalary = ScaleTwoDecimal.ZERO;
        List<SalaryDetails> brkupSalaryDetails = createSalBreakupIntervals();
        for (SalaryDetails salaryDetails : brkupSalaryDetails) {
            this.startDate = salaryDetails.getBoundary().getStartDate();
            this.endDate = salaryDetails.getBoundary().getEndDate();
            totalSalary = totalSalary.add(salaryDetails.calculateSalary(startDate, endDate));
        }
        ScaleTwoDecimal charged = personnelLineItem.getPercentCharged();
        ScaleTwoDecimal costSharing = totalSalary.percentage(calculateCostSharingPercentage());
        ScaleTwoDecimal requestedSalary = totalSalary.percentage(charged);
        boundary.setApplicableCost(requestedSalary);
        boundary.setApplicableCostSharing(costSharing);
    }

    protected QueryList<BudgetRate> filterInflationRates(CostElement costElement, boolean applyInflationRate) {
        final ValidCeRateType inflationRateType = costElement.getValidCeRateTypes().stream().filter(t -> t.getRateClassType().equals(RateClassType.INFLATION.getRateClassType())).findFirst().orElse(null);

        if (applyInflationRate && inflationRateType != null) {
            final Predicate<BudgetRate> dateAndRateAndOnOffCampusFlag = budgetRate -> {
                final boolean iInflationRCEquals = StringUtils.equals(inflationRateType.getRateClassCode(), budgetRate.getRateClassCode());
                final boolean iInflationRTEquals = StringUtils.equals(inflationRateType.getRateTypeCode(), budgetRate.getRateTypeCode());
                final boolean startDateLtEqualsEndDate = Objects.compare(budgetRate.getStartDate(), this.endDate, Comparator.naturalOrder()) <= 0;
                final boolean startDateGtEqualsStartDate = Objects.compare(budgetRate.getStartDate(), this.startDate, Comparator.naturalOrder()) >= 0;
                final boolean onOffCampusEquals = Objects.equals(costElement.getOnOffCampusFlag(), budgetRate.getOnOffCampusFlag());
                return iInflationRCEquals && iInflationRTEquals && startDateLtEqualsEndDate && startDateGtEqualsStartDate && onOffCampusEquals;
            };

            return (getInflationRates() == null ? getBudgetRates() : getInflationRates()).stream()
                    .filter(dateAndRateAndOnOffCampusFlag)
                    .collect(Collectors.toCollection(QueryList::new));
        } else {
            return new QueryList<>();
        }

    }

    private List<BudgetRate> getBudgetRates() {
        if (StringUtils.isNotEmpty(personnelLineItem.getBudgetLineItem().getHierarchyProposalNumber())) {
            return personnelLineItem.getBudgetLineItem().getHierarchyProposal().getHierarchySummaryBudget().getBudgetRates();
        }
        return budget.getBudgetRates();
    }

    protected CostElement getCostElement(CostElement costElementBO, String costElementCode) {
        if (costElementBO == null) {
            Map<String, String> pkMap = new HashMap<>();
            pkMap.put("costElement", costElementCode);
            costElementBO = getBusinessObjectService().findByPrimaryKey(CostElement.class, pkMap);
        }
        List<ValidCeRateType> costElementRates = costElementBO.getValidCeRateTypes();
        if (costElementRates == null || costElementRates.isEmpty()) {
            costElementBO.refreshReferenceObject("validCeRateTypes");
        }
        return costElementBO;
    }

    private Optional<ValidCeRateType> getInflationRateType(CostElement costElement) {
        return costElement.getValidCeRateTypes().stream().filter(t -> t.getRateClassType().equals(RateClassType.INFLATION.getRateClassType())).findFirst();
    }

    private QueryList<BudgetPerson> filterBudgetPersons() {

        final List<BudgetPerson> persons = budget.getBudgetPersons();
        if (persons.isEmpty()) {
            return new QueryList<>();
        }

        final Optional<BudgetPerson> first = persons.stream().filter(
                person -> person.getPersonSequenceNumber().equals(personnelLineItem.getPersonSequenceNumber())).
                findFirst();
        if (!first.isPresent()) {
            return new QueryList<>();
        }

        final List<BudgetPerson> filteredPersons = persons.stream().filter(person -> {
            final boolean personIdEquals = StringUtils.equals(person.getPersonId(), first.get().getPersonId());
            final boolean jobCodeEquals = StringUtils.equals(person.getJobCode(),first.get().getJobCode());
            final boolean rolodexIdEquals = Objects.equals(person.getRolodexId(), first.get().getRolodexId());
            final boolean nameEquals = StringUtils.equals(person.getPersonName(), first.get().getPersonName());

            final boolean effectiveDateLtEqualsEndDate = Objects.compare(person.getEffectiveDate(), this.endDate, Comparator.naturalOrder()) <= 0;
            return personIdEquals && jobCodeEquals && rolodexIdEquals && effectiveDateLtEqualsEndDate && nameEquals;
        }).collect(Collectors.toList());

        final Optional<BudgetPerson> tmpFirst = filteredPersons.stream().filter(person -> {
            final boolean effectiveDateLtEqualsStartDate = Objects.compare(person.getEffectiveDate(), this.startDate, Comparator.naturalOrder()) <= 0;
            final boolean effectiveDateEquals = Objects.compare(person.getEffectiveDate(), first.get().getEffectiveDate(), Comparator.naturalOrder()) == 0;
            final boolean seqNotEquals = person.getPersonSequenceNumber().intValue() != first.get().getPersonSequenceNumber();
            return effectiveDateLtEqualsStartDate && !(effectiveDateEquals && seqNotEquals);
        }).max(Comparator.comparing(BudgetPerson::getEffectiveDate));

        if (tmpFirst.isPresent()) {
            filteredPersons.clear();
            filteredPersons.add(tmpFirst.get());
        } else {
            StringBuilder warningMsg = new StringBuilder("Base salary information is not available for the person ");
            StringBuilder errMsg = new StringBuilder("Error finding the calculation base for the person ");
            errMsg.append(this.personnelLineItem.getPersonId());
            errMsg.append(" with Job Code ");
            errMsg.append(this.personnelLineItem.getJobCode());

            warningMsg.append(this.personnelLineItem.getPersonId());
            warningMsg.append(" with Job Code ");
            warningMsg.append(this.personnelLineItem.getJobCode());
            warningMsg.append(" for the period ");
            warningMsg.append(getDateTimeService().toDateString(startDate));
            warningMsg.append(" to ");
            if (!filteredPersons.isEmpty()) {
                warningMsg.append(getDateTimeService().toDateString(add(filteredPersons.get(0).getEffectiveDate(), -1)));
            } else {
                warningMsg.append(getDateTimeService().toDateString(personnelLineItem.getEndDate()));
            }
            warningMsg.append("\n");
            warningMsg.append("Salary for this period will be set to 0\n");
            warningMsg.append("Please make changes to budget person details and recalculate the budget");
            LOG.warn(warningMsg.toString());
            LOG.error(errMsg.toString());
        }

        return new QueryList<>(filteredPersons);
    }

    /**
     * Subtract no of days from the given date
     */
    private Date add(Date date, int days) {
        Calendar cal = getDateTimeService().getCalendar(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    private ScaleTwoDecimal calculateCostSharingPercentage() {
        ScaleTwoDecimal charged = personnelLineItem.getPercentCharged();
        ScaleTwoDecimal effort = personnelLineItem.getPercentEffort();
        return effort.subtract(charged);
    }

    /**
     * -Uses sorted Budget Persons list and Inflation rates list to create salary breakup periods, each period consisting of a
     * SalaryDetails -Call calculate method of each bean to calculate salary
     * 
     */
    private QueryList<SalaryDetails> createSalBreakupIntervals() {
        QueryList<DateSortable> combinedList = new QueryList<>();
        combinedList.addAll(filterBudgetPersons());
        final CostElement costElement = getCostElement(personnelLineItem.getCostElementBO(), personnelLineItem.getCostElement());
        combinedList.addAll(filterInflationRates(costElement, personnelLineItem.getApplyInRateFlag()));
        combinedList.sort("sortableDate");
        if (isAnniversarySalaryDateEnabled()) {
            combinedList = processAnniversarySalaryDateInflationRates(combinedList);
        }
        QueryList<SalaryDetails> breakUpIntervals = new QueryList<>();
        BudgetPerson budgetPerson = null;
        BudgetRate budgetRate = null;
        BudgetRate prevBudgetProposalRate = null;
        Date tempStartDate = startDate;
        Date tempEndDate = endDate;
        Date rateChangeDate;
        SalaryDetails salaryDetails;
        SalaryDetails prevSalaryDetails = new SalaryDetails();
        for (DateSortable changedObject : combinedList) {
            boolean personFlag = changedObject instanceof BudgetPerson;
            if (personFlag) {
                budgetPerson = (BudgetPerson) changedObject;
                rateChangeDate = budgetPerson.getStartDate();
                prevSalaryDetails.setActualBaseSalary(budgetPerson.getCalculationBase());
                updateBudgetPerson(budgetPerson);
                prevSalaryDetails.setWorkingMonths(budgetPerson.getAppointmentType().getDuration());

            } else {
                budgetRate = (BudgetRate) changedObject;
                rateChangeDate = budgetRate.getStartDate();
            }
            if (budgetPerson == null) {
                continue;
            }
            int compareDateChange = rateChangeDate.compareTo(tempStartDate);
            if (compareDateChange >= 0) {
                Calendar rateChangeCal = getDateTimeService().getCalendar(rateChangeDate);
                rateChangeCal.add(Calendar.DATE, -1);
                tempEndDate = rateChangeCal.getTime();
                Boundary boundary = new Boundary(tempStartDate, tempEndDate);
                salaryDetails = new SalaryDetails();
                salaryDetails.setBoundary(boundary);
                if (!personFlag && budgetRate != null) {
                    salaryDetails.setActualBaseSalary(getPrevSalaryBase(budgetPerson, boundary, budgetRate));
                    if (prevBudgetProposalRate != null
                            && budgetPerson.getEffectiveDate().before(prevBudgetProposalRate.getStartDate())
                            && budgetPerson.getEffectiveDate().before(boundary.getStartDate())
                            && prevBudgetProposalRate.getStartDate().before(boundary.getEndDate())
                            && (prevBudgetProposalRate.getStartDate().equals(boundary.getStartDate()) || budget.getBudgetPeriods()
                            .get(0).getEndDate().before(startDate))) {
                        salaryDetails.calculateActualBaseSalary(budgetRate.getApplicableRate());
                    } else {
                        if (budgetRate != null
                                && budgetPerson.getEffectiveDate().before(budgetRate.getStartDate())
                                && budgetPerson.getEffectiveDate().before(startDate)
                                && budgetRate.getStartDate().before(boundary.getEndDate())
                                && (budgetRate.getStartDate().compareTo(startDate) <= 0 || budget.getBudgetPeriods().get(0)
                                .getEndDate().before(startDate))) {
                            salaryDetails.calculateActualBaseSalary(budgetRate.getApplicableRate());
                        }
                    }
                    salaryDetails.setWorkingMonths(prevSalaryDetails.getWorkingMonths());
                    salaryDetails.setAltBudgetPerson(getSamePersonWithSameJobAndDifferentEffectiveDates(boundary, budgetPerson));
                }
                if (personFlag && budgetPerson != null) {
                    salaryDetails.setActualBaseSalary(budgetPerson.getCalculationBase());
                    salaryDetails.setWorkingMonths(budgetPerson.getAppointmentType().getDuration());
                    salaryDetails.setAltBudgetPerson(getSamePersonWithSameJobAndDifferentEffectiveDates(boundary, budgetPerson));
                }
                if (budgetPerson.getStartDate().compareTo(tempStartDate) <= 0) {
                    breakUpIntervals.add(salaryDetails);
                }
                prevBudgetProposalRate = budgetRate;
                prevSalaryDetails = salaryDetails;
                tempStartDate = rateChangeDate;
            }
        }
        Boundary boundary = new Boundary(tempStartDate, endDate);
        salaryDetails = new SalaryDetails();
        salaryDetails.setBoundary(boundary);
        if (budgetRate != null && budgetPerson != null && budgetPerson.getEffectiveDate().before(budgetRate.getStartDate())) {
            salaryDetails.calculateActualBaseSalary(budgetRate.getApplicableRate());
            salaryDetails.setWorkingMonths(prevSalaryDetails.getWorkingMonths());
        }
        if (budgetPerson != null) {
            salaryDetails.setActualBaseSalary(getPrevSalaryBase(budgetPerson, boundary, budgetRate));

            BudgetPerson newBudgetPerson = getBudgetPersonApplied(budgetPerson, boundary);
            if (budgetRate != null
                    && ((newBudgetPerson == null && budgetPerson.getEffectiveDate().before(budgetRate.getStartDate()))
                            || (newBudgetPerson != null && newBudgetPerson
                            .getEffectiveDate().before(budgetRate.getStartDate())))) {
                salaryDetails.calculateActualBaseSalary(budgetRate.getApplicableRate());
            }

            if (newBudgetPerson != null) {
                updateBudgetPerson(newBudgetPerson);
                salaryDetails.setWorkingMonths(newBudgetPerson.getAppointmentType() == null ? DEFAULT_WORKING_MONTHS
                        : newBudgetPerson.getAppointmentType().getDuration());
            }
            else {
                salaryDetails.setWorkingMonths(budgetPerson.getAppointmentType() == null ? DEFAULT_WORKING_MONTHS : budgetPerson
                        .getAppointmentType().getDuration());
            }
            salaryDetails.setAltBudgetPerson(getSamePersonWithSameJobAndDifferentEffectiveDates(boundary, budgetPerson));
        }
        breakUpIntervals.add(salaryDetails);
        return breakUpIntervals;

    }

    private void updateBudgetPerson(BudgetPerson budgetPerson) {
        if (budgetPerson.getAppointmentType() == null) {
            budgetPerson.refreshReferenceObject(APPOINTMENT_TYPE);
        }
    }

    private QueryList<DateSortable> processAnniversarySalaryDateInflationRates(QueryList<DateSortable> combinedList) {
        QueryList<DateSortable> filteredCombinedList = new QueryList<>();
        for (DateSortable dateSortable : combinedList) {
            if (dateSortable instanceof BudgetPerson) {
                BudgetPerson budgetPerson = (BudgetPerson) dateSortable;
                if (budgetPerson.getSalaryAnniversaryDate() == null) {
                    filteredCombinedList = combinedList;
                }
                else {
                    filteredCombinedList.add(dateSortable);
                    filteredCombinedList.addAll(createAnnualInflationRates(budgetPerson, endDate));
                }
            }
        }
        return filteredCombinedList;
    }

    private List<BudgetRate> createAnnualInflationRates(BudgetPerson budgetPerson, Date endDate) {
        List<BudgetRate> budgetRates = new ArrayList<>();
        List<BudgetRate> inflationRatesList = filterInflationRates(budgetPerson.getEffectiveDate(), endDate);
        if (inflationRatesList.isEmpty()) {
            return budgetRates;
        }
        BudgetRate inflationRate = getInflationRateToBeApplied(inflationRatesList, budgetPerson.getSalaryAnniversaryDate());
        BudgetRate budgetRate;
        if (inflationRate != null) {
            budgetRate = (BudgetRate) ObjectUtils.deepCopy(inflationRate);
            if (!budgetPerson.getSalaryAnniversaryDate().before(budgetPerson.getEffectiveDate())) {
                budgetRate.setStartDate(budgetPerson.getSalaryAnniversaryDate());
            }
            if (budgetRate.getStartDate().before(endDate) || budgetRate.getStartDate().equals(endDate)) {
                budgetRates.add(budgetRate);
            }
        }
        Calendar salaryDateCalendar = getDateTimeService().getCalendar(budgetPerson.getSalaryAnniversaryDate());
        Calendar endCalendar = getDateTimeService().getCalendar(endDate);

        int startYear = salaryDateCalendar.get(Calendar.YEAR);
        int endYear = endCalendar.get(Calendar.YEAR);
        if (startYear != endYear) {
            while (salaryDateCalendar.get(Calendar.YEAR) <= endYear) {
                salaryDateCalendar.add(Calendar.YEAR, 1);
                Date nextInflationDate = salaryDateCalendar.getTime();
                if (nextInflationDate.after(endDate)) {
                    break;
                }
                BudgetRate inflationRateToBeApplied = getInflationRateToBeApplied(inflationRatesList, nextInflationDate);
                if (inflationRateToBeApplied != null) {
                    BudgetRate nextBudgetRate = (BudgetRate) ObjectUtils.deepCopy(inflationRateToBeApplied);
                    try {
                        nextBudgetRate.setStartDate(getDateTimeService().convertToSqlDate(getDateTimeService()
                                .toDateString(nextInflationDate)));
                        budgetRates.add(nextBudgetRate);
                    }
                    catch (ParseException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
        }
        return budgetRates;
    }

    private BudgetRate getInflationRateToBeApplied(List<BudgetRate> inflationRatesList, Date effectiveDate) {
        Equals eqEffectiveDate = new Equals("startDate", effectiveDate);
        LesserThan ltEffectiveDate = new LesserThan("startDate", effectiveDate);
        Or eqOrltEffectiveDate = new Or(eqEffectiveDate, ltEffectiveDate);
        QueryList<BudgetRate> qlInflationRates = new QueryList<>(inflationRatesList);
        QueryList<BudgetRate> qlFilteredRates = qlInflationRates.filter(eqOrltEffectiveDate);
        qlFilteredRates.sort("startDate", false);
        return qlFilteredRates.isEmpty() ? null : qlFilteredRates.get(0);
    }

    protected boolean isAnniversarySalaryDateEnabled() {
        return getParameterService().getParameterValueAsString(Budget.class, Constants.ENABLE_SALARY_INFLATION_ANNIV_DATE)
                .equals(STRING_1);
    }

    private boolean isSameTbn(BudgetPerson curBudgetPerson, BudgetPerson budgetPerson) {
        return (curBudgetPerson.getTbnId() != null && curBudgetPerson.getTbnId().equals(budgetPerson.getTbnId()) &&
                (curBudgetPerson.getPersonName() != null && curBudgetPerson.getPersonName().equalsIgnoreCase(budgetPerson.getPersonName())));
    }
    /**
     * 
     * This inner class is for calculating the salary
     */
     private static class SalaryDetails {
        private Boundary boundary;
        private ScaleTwoDecimal workingMonths;
        private ScaleTwoDecimal actualBaseSalary = ScaleTwoDecimal.ZERO;
        private ScaleTwoDecimal calculatedSalary = ScaleTwoDecimal.ZERO;
        private BudgetPerson altBudgetPerson;

        /**
         * 
         * This method is to calculate salary for a personnel line item
         * @return Calculated Salary
         */
        ScaleTwoDecimal calculateSalary(Date startDate, Date endDate) {
            ScaleTwoDecimal paidMonths = (workingMonths == null) ? DEFAULT_WORKING_MONTHS : workingMonths;
            double perMonthSalary = this.getActualBaseSalary().doubleValue() / paidMonths.doubleValue();
            Calendar startDateCalendar = new Calendar.Builder().setInstant(startDate).build();
            int startMonth = startDateCalendar.get(Calendar.MONTH);
            Calendar endDateCalendar = new Calendar.Builder().setInstant(endDate).build();
            double totalSalary = 0d;
            boolean salaryReset = false;
            while (startDateCalendar.compareTo(endDateCalendar) <= 0) {
                int noOfDaysInMonth = startDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                int noOfActualDays;
                if (altBudgetPerson != null && !salaryReset) {
                    Calendar effdtCalendar = new Calendar.Builder().setInstant(altBudgetPerson.getEffectiveDate()).build();
                    Calendar nextStartDateCalendar = new Calendar.Builder().setInstant(startDateCalendar.getTime()).build();
                    nextStartDateCalendar.add(Calendar.MONTH, 1);
                    nextStartDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                    if (effdtCalendar.compareTo(startDateCalendar) >= 0 && effdtCalendar.compareTo(nextStartDateCalendar) < 0) {
                        setActualBaseSalary(altBudgetPerson.getCalculationBase());
                        if (effdtCalendar.compareTo(startDateCalendar) > 0) {
                            noOfActualDays = effdtCalendar.get(Calendar.DAY_OF_MONTH)
                                    - startDateCalendar.get(Calendar.DAY_OF_MONTH);
                            totalSalary += (perMonthSalary / noOfDaysInMonth * noOfActualDays);
                            startDateCalendar.set(Calendar.DAY_OF_MONTH, effdtCalendar.get(Calendar.DAY_OF_MONTH));
                        }
                        updateAltBudgetPerson();
                        paidMonths = altBudgetPerson.getAppointmentType().getDuration() == null ? DEFAULT_WORKING_MONTHS : altBudgetPerson
                                .getAppointmentType().getDuration();
                        perMonthSalary = this.getActualBaseSalary().doubleValue() / paidMonths.doubleValue();
                        salaryReset = true;
                    }
                }
                if (startDateCalendar.get(Calendar.MONTH) == endDateCalendar.get(Calendar.MONTH)
                        && startDateCalendar.get(Calendar.YEAR) == endDateCalendar.get(Calendar.YEAR)) {
                    noOfActualDays = endDateCalendar.get(Calendar.DAY_OF_MONTH) - startDateCalendar.get(Calendar.DAY_OF_MONTH) + 1;
                }
                else if (startDateCalendar.get(Calendar.MONTH) == startMonth || startDateCalendar.get(Calendar.DAY_OF_MONTH) != 1) {
                    noOfActualDays = noOfDaysInMonth - startDateCalendar.get(Calendar.DAY_OF_MONTH) + 1;
                }
                else {
                    noOfActualDays = noOfDaysInMonth;
                }
                startDateCalendar.add(Calendar.MONTH, 1);
                startDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                totalSalary += (perMonthSalary / noOfDaysInMonth * noOfActualDays);
            }
            return calculatedSalary.add(new ScaleTwoDecimal(totalSalary));
        }

        private void updateAltBudgetPerson() {
            altBudgetPerson.refreshReferenceObject(APPOINTMENT_TYPE);
        }


        /**
         * Calculate the salary by using base salary and applicable rate
         */
        public void calculateActualBaseSalary(ScaleTwoDecimal applicableRate) {
            ScaleTwoDecimal actualBaseSal = getActualBaseSalary();
            setActualBaseSalary(actualBaseSal.percentage(applicableRate).add(actualBaseSal));
        }

        public Boundary getBoundary() {
            return boundary;
        }

        public void setBoundary(Boundary boundary) {
            this.boundary = boundary;
        }

        public ScaleTwoDecimal getActualBaseSalary() {
            return actualBaseSalary;
        }

        public void setActualBaseSalary(ScaleTwoDecimal actualBaseSalary) {
            this.actualBaseSalary = actualBaseSalary;
        }

        public ScaleTwoDecimal getCalculatedSalary() {
            return calculatedSalary;
        }

        public void setCalculatedSalary(ScaleTwoDecimal calculatedSalary) {
            this.calculatedSalary = calculatedSalary;
        }

        @Override
        public String toString() {
            StringBuilder strBffr = new StringBuilder("");
            strBffr.append("Actual Base Salary=>" + actualBaseSalary);
            strBffr.append(";");
            strBffr.append("Duration=>" + workingMonths);
            strBffr.append(";");
            strBffr.append("Boundary=>" + String.valueOf(boundary));
            strBffr.append(";");
            strBffr.append("Calculated salary=>" + calculatedSalary);
            strBffr.append("\n");
            return strBffr.toString();
        }

        public ScaleTwoDecimal getWorkingMonths() {
            return workingMonths;
        }

        public void setWorkingMonths(ScaleTwoDecimal workingMonths) {
            this.workingMonths = workingMonths;
        }

        public BudgetPerson getAltBudgetPerson() {
            return altBudgetPerson;
        }

        public void setAltBudgetPerson(BudgetPerson altBudgetPerson) {
            this.altBudgetPerson = altBudgetPerson;
        }
    }

    public void setInflationRates(QueryList<BudgetRate> inflationRates) {
        this.inflationRates = inflationRates;
    }

    public QueryList<BudgetRate> getInflationRates() {
        return inflationRates;
    }


    private BudgetPerson getBudgetPersonApplied(BudgetPerson budgetPerson, Boundary boundary) {
        return budget.getBudgetPersons().stream().filter(budgetPerson1 ->
                doesPersonHaveOtherEffectiveDates(budgetPerson1, boundary, budgetPerson)).findFirst().
                orElse(null);
    }

    private BudgetPerson getSamePersonWithSameJobAndDifferentEffectiveDates(Boundary boundary, BudgetPerson curBudgetPerson) {
        for (BudgetPerson budgetPerson : budget.getBudgetPersons()) {
            if ((isSamePerson(budgetPerson, curBudgetPerson))
                    && !budgetPerson.getPersonSequenceNumber().equals(curBudgetPerson.getPersonSequenceNumber())
                    && StringUtils.equals(budgetPerson.getJobCode(), curBudgetPerson.getJobCode())
                    && (budgetPerson.getEffectiveDate() != null
                    && budgetPerson.getEffectiveDate().after(curBudgetPerson.getEffectiveDate())
                    && budgetPerson.getEffectiveDate().compareTo(boundary.getStartDate()) >= 0 && budgetPerson.getEffectiveDate().compareTo(boundary.getEndDate()) <= 0)) {
                return budgetPerson;
            }
        }
        return null;
    }

    private boolean doesPersonHaveOtherEffectiveDates(BudgetPerson budgetPerson1, Boundary boundary, BudgetPerson budgetPerson2) {
        return (isSamePerson(budgetPerson1, budgetPerson2))
                && !budgetPerson2.getPersonSequenceNumber().equals(budgetPerson1.getPersonSequenceNumber())
                && StringUtils.equals(budgetPerson2.getJobCode(),budgetPerson1.getJobCode())
                && (budgetPerson2.getEffectiveDate() != null && budgetPerson2.getEffectiveDate().after(budgetPerson1.getEffectiveDate()))
                && budgetPerson2.getEffectiveDate().compareTo(boundary.getStartDate()) <= 0;
    }

    private ScaleTwoDecimal getPrevSalaryBase(BudgetPerson budgetPerson, Boundary boundary, BudgetRate currentRate) {

        BudgetPerson newBudgetPerson = budget.getBudgetPersons().stream().filter(budgetPerson1 ->
                doesPersonHaveOtherEffectiveDates(budgetPerson1, boundary, budgetPerson)).findFirst().
                orElse(budgetPerson);

        Date p1StartDate = budget.getBudgetPeriods().get(0).getStartDate();
        BigDecimal calBase = newBudgetPerson.getCalculationBase().bigDecimalValue();
        if (budgetPerson.getEffectiveDate().before(p1StartDate)) {
            p1StartDate = budgetPerson.getEffectiveDate();
        }
        QueryList<BudgetRate> qlist = new QueryList<>();

        Date previousEndDate = getPreviousPersonnelLineItemEndDate();

        if (isAnniversarySalaryDateEnabled() && budgetPerson.getSalaryAnniversaryDate() != null) {
            qlist.addAll(createAnnualInflationRates(budgetPerson, previousEndDate).stream()
                    // Filter out generated dates that are the same as the rate for the current breakup interval
                    // so that it doesn't get applied twice
                    .filter(rate -> !isSameRate(rate, currentRate))
                    .collect(Collectors.toList()));
        } else {
            qlist.addAll(filterInflationRates(p1StartDate, add(boundary.getStartDate(), -1)));
        }
        for (BudgetRate budgetProposalrate : qlist) {
            if (budgetProposalrate.getStartDate().after(budgetPerson.getEffectiveDate())) {
                calBase = calBase.add(calBase.multiply(budgetProposalrate.getApplicableRate().bigDecimalValue()).divide(new ScaleTwoDecimal(100.00).bigDecimalValue(), RoundingMode.HALF_UP));
            }
        }
        return new ScaleTwoDecimal(calBase);

    }

    private boolean isSamePerson(BudgetPerson budgetPerson, BudgetPerson budgetPerson1) {
        return (budgetPerson1.getPersonId() != null && budgetPerson1.getPersonId().equals(budgetPerson.getPersonId()))
                || (budgetPerson1.getRolodexId() != null && budgetPerson1.getRolodexId().equals(budgetPerson.getRolodexId()))
                ||  isSameTbn(budgetPerson1, budgetPerson);
    }

    private boolean isSameRate(BudgetRate rate1, BudgetRate rate2) {
         if (rate1 == null && rate2 == null) {
             return true;
         } else if (rate1 == null || rate2 == null) {
             return false;
         }
         return StringUtils.equals(rate1.getRateClassCode(), rate2.getRateClassCode()) &&
                StringUtils.equals(rate1.getRateTypeCode(), rate2.getRateTypeCode()) &&
                StringUtils.equals(rate1.getActivityTypeCode(), rate2.getActivityTypeCode()) &&
                StringUtils.equals(rate1.getFiscalYear(), rate2.getFiscalYear()) &&
                Objects.equals(rate1.getOnOffCampusFlag(), rate2.getOnOffCampusFlag()) &&
                Objects.equals(rate1.getStartDate(), rate2.getStartDate()) &&
                Objects.equals(rate1.getApplicableRate(), rate2.getApplicableRate()) &&
                Objects.equals(rate1.getInstituteRate(), rate2.getInstituteRate());
    }

    protected Date getPreviousPersonnelLineItemEndDate() {
        List<BudgetPersonnelDetails> previousPeriodsPersonnelDetails = budget.getBudgetPeriods()
                .stream()
                .flatMap(l -> l.getBudgetLineItems().stream())
                .filter(l -> StringUtils.equalsIgnoreCase(l.getCostElement(), personnelLineItem.getCostElement()))
                .flatMap(l -> l.getBudgetPersonnelDetailsList().stream())
                .filter(budgetPersonnelDetail -> (
                        budgetPersonnelDetail.getEndDate().compareTo(personnelLineItem.getEndDate()) < 0 &&
                                budgetPersonnelDetail.getBudgetPerson() != null &&
                                personnelLineItem.getBudgetPerson() != null &&
                                StringUtils.equals(budgetPersonnelDetail.getBudgetPerson().getPersonRolodexTbnId(), personnelLineItem.getBudgetPerson().getPersonRolodexTbnId())
                ))
                .sorted(Comparator.comparing(BudgetPersonnelDetails::getEndDate))
                .collect(Collectors.toList());

        Date previousEndDate = budget.getStartDate();

        if (!CollectionUtils.isEmpty(previousPeriodsPersonnelDetails)) {
            previousEndDate = previousPeriodsPersonnelDetails.get(previousPeriodsPersonnelDetails.size()-1).getEndDate();
        }

        return previousEndDate;
    }

    private QueryList<BudgetRate> filterInflationRates(Date sDate, Date eDate) {
        if (personnelLineItem.getApplyInRateFlag()) {

            final CostElement costElement = getCostElement(personnelLineItem.getCostElementBO(), personnelLineItem.getCostElement());
            final Optional<ValidCeRateType> inflationRateType = getInflationRateType(costElement);

            final And inflRCandRT;
            if (inflationRateType.isPresent()) {
                final Equals eInflationRC = new Equals("rateClassCode", inflationRateType.get().getRateClassCode());
                final Equals eInflationRT = new Equals("rateTypeCode", inflationRateType.get().getRateTypeCode());
                inflRCandRT = new And(eInflationRC, eInflationRT);
            } else {
                inflRCandRT = new And(Operator.FALSE, Operator.FALSE);
            }

            final LesserThan ltStartDate = new LesserThan("startDate", sDate);
            final And ltStartDateAndRate = new And(inflRCandRT, ltStartDate);
            final Equals onOffCampus = new Equals("onOffCampusFlag", costElement.getOnOffCampusFlag());
            final And ltStartDateAndRateAndOnOffCampusFlag = new And(ltStartDateAndRate, onOffCampus);

            final LesserThan ltEndDate = new LesserThan("startDate", eDate);
            final Equals eEndDate = new Equals("startDate", eDate);
            final Or ltOrEqEndDate = new Or(ltEndDate, eEndDate);

            final GreaterThan gtStartDate = new GreaterThan("startDate", sDate);
            final Equals eStartDate = new Equals("startDate", sDate);
            final Or gtOrEqStartDate = new Or(gtStartDate, eStartDate);

            final And gtOrEqStartDateAndltOrEqEndDate = new And(gtOrEqStartDate, ltOrEqEndDate);
            final And dateAndRate = new And(inflRCandRT, gtOrEqStartDateAndltOrEqEndDate);

            final And dateAndRateAndOnOffCampusFlag = new And(dateAndRate, onOffCampus);

            final QueryList<BudgetRate> inflationRatesList;
            final QueryList<BudgetRate> prevInflationRatesList;
            if (getInflationRates() == null) {
                inflationRatesList = new QueryList<>(getBudgetRates()).filter(dateAndRateAndOnOffCampusFlag);
                prevInflationRatesList = new QueryList<>(getBudgetRates()).filter(ltStartDateAndRateAndOnOffCampusFlag);
            } else {
                inflationRatesList = getInflationRates().filter(dateAndRateAndOnOffCampusFlag);
                prevInflationRatesList = getInflationRates().filter(ltStartDateAndRateAndOnOffCampusFlag);
            }

            if (!prevInflationRatesList.isEmpty()) {
                prevInflationRatesList.sort("startDate", false);
                final BudgetRate prevInflationRate = prevInflationRatesList.get(0);
                inflationRatesList.add(prevInflationRate);
                inflationRatesList.sort("startDate");
            }
            return inflationRatesList;
        } else {
            return new QueryList<>();
        }
    }

    protected DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = KcServiceLocator.getService(DateTimeService.class);
        }
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = KcServiceLocator.getService(BusinessObjectService.class);
        }

        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = KcServiceLocator.getService(ParameterService.class);
        }

        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
