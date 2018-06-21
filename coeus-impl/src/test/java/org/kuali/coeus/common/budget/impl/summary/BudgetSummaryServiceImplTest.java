/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.budget.impl.summary;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.impl.datetime.DateTimeServiceImpl;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BudgetSummaryServiceImplTest {
    BudgetSummaryServiceImpl budgetSummaryService;
    DateTimeService dateTimeService;

    public List<Date> createStartEndDates(String start, String end) throws Exception {
        return new ArrayList<Date>() {{
            add(0, createDateFromString(start));
            add(1, createDateFromString(end));
        }};

    }

    public Date createDateFromString(String date) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return new Date(dateFormat.parse(date).getTime());
    }

    @Before
    public void setup() {
        budgetSummaryService = new BudgetSummaryServiceImpl();
    }

    @Test
    public void getNewStartEndDates_gapNo_gapLeapNo_initPeriodLeapNo_currentPeriodLeapNo_Yearly() throws Exception {
        List<Date> dates = createStartEndDates("01/01/2015", "12/31/2015");
        List<Date> newDates = budgetSummaryService.getNewStartEndDates(dates, 0, 364, null, false, false);
        Assert.assertEquals("start date should be 01/01/2015", dates.get(0), newDates.get(0));
        Assert.assertEquals("end date should be 12/31/2015", dates.get(1), newDates.get(1));
    }

    @Test
    public void getNewStartEndDates_gapNo_gapLeapNo_initPeriodLeapNo_currentPeriodLeapYes_Yearly() throws Exception {
        List<Date> dates = createStartEndDates("01/01/2016", "12/31/2016");
        List<Date> newDates = budgetSummaryService.getNewStartEndDates(dates, 0, 364, null, false, false);
        Assert.assertEquals("start date should be 01/01/2016", dates.get(0), newDates.get(0));
        Assert.assertEquals("end date should be 12/31/2016", dates.get(1), newDates.get(1));
    }

    @Test
    public void getNewStartEndDates_gapNo_gapLeapNo_initPeriodLeapYes_currentPeriodLeapNo_Yearly() throws Exception {
        List<Date> dates = createStartEndDates("01/01/2015", "12/31/2015");
        List<Date> newDates = budgetSummaryService.getNewStartEndDates(dates, 0, 365, null, true, false);
        Assert.assertEquals("start date should be 01/01/2015", dates.get(0), newDates.get(0));
        Assert.assertEquals("end date should be 12/31/2015", dates.get(1), newDates.get(1));
    }

    @Test
    public void getNewStartEndDates_gapNo_gapLeapNo_initPeriodLeapYes_currentPeriodLeapYes_Yearly() throws Exception {
        List<Date> dates = createStartEndDates("01/01/2016", "12/31/2016");
        List<Date> newDates = budgetSummaryService.getNewStartEndDates(dates, 0, 365, null, true, false);
        Assert.assertEquals("start date should be 01/01/2016", dates.get(0), newDates.get(0));
        Assert.assertEquals("end date should be 12/31/2016", dates.get(1), newDates.get(1));
    }

    @Test
    public void getNewStartEndDates_gapYes_gapLeapNo_initPeriodLeapNo_currentPeriodLeapNo_Yearly() throws Exception {
        List<Date> dates = createStartEndDates("01/01/2015", "12/31/2015");
        List<Date> newDates = budgetSummaryService.getNewStartEndDates(dates, 19, 345, null, false, false);
        Assert.assertEquals("start date should be 01/20/2015", createDateFromString("01/20/2015"), newDates.get(0));
        Assert.assertEquals("end date should be 12/31/2015", dates.get(1), newDates.get(1));
    }

    @Test
    public void getNewStartEndDates_gapYes_gapLeapNo_initPeriodLeapNo_currentPeriodLeapYes_Yearly() throws Exception {
        List<Date> dates = createStartEndDates("01/01/2016", "12/31/2016");
        List<Date> newDates = budgetSummaryService.getNewStartEndDates(dates, 19, 345, null, false, false);
        Assert.assertEquals("start date should be 01/20/2016", createDateFromString("01/20/2016"), newDates.get(0));
        Assert.assertEquals("end date should be 12/31/2016", dates.get(1), newDates.get(1));
    }

    @Test
    public void getNewStartEndDates_gapYes_gapLeapNo_initPeriodLeapYes_currentPeriodLeapNo_Yearly() throws Exception {
        List<Date> dates = createStartEndDates("01/01/2015", "12/31/2015");
        List<Date> newDates = budgetSummaryService.getNewStartEndDates(dates, 19, 346, null, true, false);
        Assert.assertEquals("start date should be 01/20/2015", createDateFromString("01/20/2015"), newDates.get(0));
        Assert.assertEquals("end date should be 12/31/2015", dates.get(1), newDates.get(1));
    }

    @Test
    public void getNewStartEndDates_gapYes_gapLeapNo_initPeriodLeapYes_currentPeriodLeapYes_Yearly() throws Exception {
        List<Date> dates = createStartEndDates("01/01/2016", "12/31/2016");
        List<Date> newDates = budgetSummaryService.getNewStartEndDates(dates, 19, 346, null, true, false);
        Assert.assertEquals("start date should be 01/20/2016", createDateFromString("01/20/2016"), newDates.get(0));
        Assert.assertEquals("end date should be 12/31/2015", dates.get(1), newDates.get(1));
    }

    @Test
    public void getNewStartEndDates_gapYes_gapLeapYes_initPeriodLeapNo_currentPeriodLeapNo_Yearly() throws Exception {
        List<Date> dates = createStartEndDates("02/15/2015", "02/14/2016");
        List<Date> newDates = budgetSummaryService.getNewStartEndDates(dates, 19, 346, null, false, true);
        Assert.assertEquals("start date should be 03/05/2015", createDateFromString("03/05/2015"), newDates.get(0));
        Assert.assertEquals("end date should be 02/14/2016", dates.get(1), newDates.get(1));
    }

    @Test
    public void getNewStartEndDates_gapYes_gapLeapYes_initPeriodLeapNo_currentPeriodLeapYes_Yearly() throws Exception {
        List<Date> dates = createStartEndDates("02/15/2016", "02/14/2017");
        List<Date> newDates = budgetSummaryService.getNewStartEndDates(dates, 19, 346, null, false, true);
        Assert.assertEquals("start date should be 03/05/2016", createDateFromString("03/05/2016"), newDates.get(0));
        Assert.assertEquals("end date should be 02/14/2017", dates.get(1), newDates.get(1));
    }

    @Test
    public void getNewStartEndDates_gapNo_gapLeapNo_initPeriodLeapNo_currentPeriodLeapNo_Yearly_EndBeforeLeap() throws Exception {
        List<Date> dates = createStartEndDates("07/01/2015", "06/31/2016");
        List<Date> newDates = budgetSummaryService.getNewStartEndDates(dates, 0, 183, null, false, false);
        Assert.assertEquals("start date should be 07/01/2015", dates.get(0), newDates.get(0));
        Assert.assertEquals("end date should be 12/31/2015", createDateFromString("12/31/2015"), newDates.get(1));
    }

    @Test
    public void getNewStartEndDates_gapNo_gapLeapNo_initPeriodLeapNo_currentPeriodLeapNo_BiYearly() throws Exception {
        List<Date> dates = createStartEndDates("01/01/2015", "06/30/2015");
        List<Date> newDates = budgetSummaryService.getNewStartEndDates(dates, 0, 180, null, false, false);
        Assert.assertEquals("start date should be 01/01/2015", dates.get(0), newDates.get(0));
        Assert.assertEquals("end date should be 06/30/2015", dates.get(1), newDates.get(1));
    }

    @Test
    public void getNewStartEndDates_gapYes_gapLeapYes_initPeriodLeapNo_currentPeriodLeapYes() throws Exception {
        List<Date> dates = createStartEndDates("02/01/2016", "01/31/2017");
        List<Date> newDates = budgetSummaryService.getNewStartEndDates(dates, 213, 152, null, false, true);
        Assert.assertEquals("start date should be 09/01/2016", createDateFromString("09/01/2016"), newDates.get(0));
        Assert.assertEquals("end date should be 01/31/2017", dates.get(1), newDates.get(1));
    }

    @Test
    public void getNewStartEndDates_gapYes_gapLeapYes_initPeriodLeapNo_currentPeriodLeapNo() throws Exception {
        List<Date> dates = createStartEndDates("02/01/2017", "01/31/2018");
        List<Date> newDates = budgetSummaryService.getNewStartEndDates(dates, 213, 152, null, false, true);
        Assert.assertEquals("start date should be 09/01/2017", createDateFromString("09/01/2017"), newDates.get(0));
        Assert.assertEquals("end date should be 01/31/2018", dates.get(1), newDates.get(1));
    }

    @Test
    public void getNewStartEndDates_gapYes_gapLeapNo_initPeriodLeapNo_currentPeriodLeapYes() throws Exception {
        List<Date> dates = createStartEndDates("01/01/2020", "12/31/2020");
        List<Date> newDates = budgetSummaryService.getNewStartEndDates(dates, 181, 30, null, false, false);
        Assert.assertEquals("start date should be 07/01/2020", createDateFromString("07/01/2020"), newDates.get(0));
        Assert.assertEquals("end date should be 07/31/2020", createDateFromString("07/31/2020"), newDates.get(1));
    }

    @Test
    public void getNewStartEndDates_lastFebruaryOnNonLeapToLeap() throws Exception {
        List<Date> periodDates = createStartEndDates("03/01/2019", "02/29/2020");
        List<Date> lineItemDates = createStartEndDates("09/01/2019", "02/28/2020");

        int gap = getDateTimeService().dateDiff(periodDates.get(0), lineItemDates.get(0), false);
        int lineDuration = getDateTimeService().dateDiff(lineItemDates.get(0), lineItemDates.get(1), false);
        boolean isLeapDayInGap = budgetSummaryService.isLeapDaysInPeriod(periodDates.get(0), lineItemDates.get(0));
        boolean isLeapDateInPeriod = budgetSummaryService.isLeapDaysInPeriod(lineItemDates.get(0), lineItemDates.get(1));

        List <Date> dates = budgetSummaryService.getNewStartEndDates(periodDates, gap, lineDuration, lineItemDates.get(0), isLeapDateInPeriod, isLeapDayInGap);

        Assert.assertEquals(createDateFromString("09/01/2019"), dates.get(0));
        Assert.assertEquals(createDateFromString("02/29/2020"), dates.get(1));
    }

    private DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = new DateTimeServiceImpl();
        }

        return dateTimeService;
    }
}
