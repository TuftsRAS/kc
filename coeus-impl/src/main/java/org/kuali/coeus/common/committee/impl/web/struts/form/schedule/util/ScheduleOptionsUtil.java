/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.committee.impl.web.struts.form.schedule.util;

import org.kuali.coeus.common.committee.impl.web.struts.form.schedule.DayOfWeek;
import org.kuali.coeus.common.committee.impl.web.struts.form.schedule.Months;
import org.kuali.coeus.common.committee.impl.web.struts.form.schedule.WeekOfMonth;
import org.kuali.coeus.sys.framework.scheduling.util.CronSpecialChars;

public class ScheduleOptionsUtil {

    @SuppressWarnings("unused")
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(ScheduleOptionsUtil.class);

    /**
     * This method returns CronSpecialChars of a month.
     * @param month
     * @return
     */
    public static CronSpecialChars getMonthOfWeek(String month) {
        String abbr = Months.valueOf(month).getAbbr();        
        return CronSpecialChars.valueOf(abbr);
    }
    
    /**
     * This method returns CronSpecialChars of a day of week.
     * @param dayOfWeek
     * @return
     */
    public static CronSpecialChars getDayOfWeek(String dayOfWeek) {
        String abbr = DayOfWeek.valueOf(dayOfWeek).getAbbr();
        return CronSpecialChars.valueOf(abbr);
    } 
    
    /**
     * This method returns CronSpecialChars of month of week.
     * @param monthsWeek
     * @return
     */
    public static CronSpecialChars getWeekOfMonth(String monthsWeek) {
        String number = WeekOfMonth.valueOf(monthsWeek).getNumber();
        return CronSpecialChars.valueOf(number);
    }
    
    /**
     * This method returns CronSpecialChars[] of days of week.
     * @param daysOfWeek
     * @return
     */
    public static CronSpecialChars[] convertToWeekdays(String [] daysOfWeek){        
        CronSpecialChars [] weekdays = new CronSpecialChars[daysOfWeek.length];
        int i = 0;
        for(String str: daysOfWeek) {
            if(null != str){
                String abbr = DayOfWeek.valueOf(str).getAbbr();
                weekdays[i++] = CronSpecialChars.valueOf(abbr);
            }
        }
        return weekdays;
    }
}
