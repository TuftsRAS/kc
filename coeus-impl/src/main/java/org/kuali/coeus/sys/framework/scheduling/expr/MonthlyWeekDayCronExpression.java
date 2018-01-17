/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.framework.scheduling.expr;

import org.kuali.coeus.sys.framework.scheduling.util.CronSpecialChars;
import org.kuali.coeus.sys.framework.scheduling.util.Time24HrFmt;

import java.text.ParseException;
import java.util.Date;

/**
 * This class extends CronExpression and provides MonthlyWeekDayCronExpression implementation.
 * <p>
 * This implementation generates expression for monthly scheduling dates using week of month, day of week and month.
 * 
 * week of month: CronSpecialChars.FIRST, CronSpecialChars.SECOND, CronSpecialChars.THIRD, CronSpecialChars.FOURTH,
 * CronSpecialChars.FIFTH 
 * 
 * day of week: CronSpecialChars.SUN, CronSpecialChars.MON, CronSpecialChars.TUE, CronSpecialChars.WED,
 * CronSpecialChars.THU, CronSpecialChars.FRI, CronSpecialChars.SAT 
 * 
 * month: 1,2,3,4,5,6,7,8,9,10,11,12
 * 
 * Note: Start day is skipped, date boundary is handled by ScheduleSequence implementation  during schedule generation.
 * 
 * e.g. Start Date : 02/24/09, Time : 10:10 (hh:mm) Format (second minute hour day month year) Generated Expression :0 10 10 ? 2/1 TUE#2
 * Explanation: Generate dates for every SECOND Tuesday of a month at 10:10 (hh:mm), starting from month of February.  
 * 
 * e.g. Start Date : 02/24/09, Time : 10:10 (hh:mm) Format (second minute hour day month year) Generated Expression :0 10 10 ? 2/2 TUE#2
 * Explanation: Generate dates for every SECOND Tuesday of a every other month at 10:10 (hh:mm), starting from month of February. 
 */
public class MonthlyWeekDayCronExpression extends CronExpression {

    private Integer frequencyInMonth;

    private CronSpecialChars dayOfWeek;

    private CronSpecialChars weekOfMonth;

    public MonthlyWeekDayCronExpression(Date startDate, Time24HrFmt time, CronSpecialChars dayOfWeek, CronSpecialChars weekOfMonth,
            Integer frequencyInMonth) throws ParseException {
        super(startDate, time);
        this.frequencyInMonth = frequencyInMonth;
        this.dayOfWeek = dayOfWeek;
        this.weekOfMonth = weekOfMonth;
    }

    @Override
    public String getExpression() {
        StringBuilder exp = new StringBuilder();
        exp.append(SECONDS).append(CronSpecialChars.SPACE);
        exp.append(getTime().getMinutes()).append(CronSpecialChars.SPACE);
        exp.append(getTime().getHours()).append(CronSpecialChars.SPACE);
        exp.append(CronSpecialChars.QUESTION).append(CronSpecialChars.SPACE);
        exp.append(CronSpecialChars.FIRST).append(CronSpecialChars.SLASH).append(frequencyInMonth).append(CronSpecialChars.SPACE);
        if (!(weekOfMonth == CronSpecialChars.LAST))
            exp.append(dayOfWeek).append(CronSpecialChars.HASH).append(weekOfMonth);
        else
            exp.append(dayOfWeek).append(CronSpecialChars.LAST);
        return exp.toString();
    }

}
