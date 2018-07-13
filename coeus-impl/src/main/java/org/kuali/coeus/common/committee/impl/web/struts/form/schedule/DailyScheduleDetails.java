/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.committee.impl.web.struts.form.schedule;

/**
 * This class holds daily recurrence UI data.  
 */
public class DailyScheduleDetails extends ScheduleDetails {
    
    @SuppressWarnings("unused")
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(DailyScheduleDetails.class);
    
    private int defaultDay;

    private Integer day;
    
    private String dayOption;
    
    private String[] daysOfWeek; 
    
    public static enum optionValues {XDAY,WEEKDAY};
    
    public DailyScheduleDetails() {
        super();
        this.setDefaultDay(1);
        this.setDay(this.getDefaultDay());
        this.setDayOption(optionValues.XDAY.toString());
        this.daysOfWeek = new String[5];
        this.getDaysOfWeek()[0] = DayOfWeek.Monday.name();
        this.getDaysOfWeek()[1] = DayOfWeek.Tuesday.name();
        this.getDaysOfWeek()[2] = DayOfWeek.Wednesday.name();
        this.getDaysOfWeek()[3] = DayOfWeek.Thursday.name();
        this.getDaysOfWeek()[4] = DayOfWeek.Friday.name();
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getDay() {
        return day;
    }

    public int getDefaultDay() {
        return defaultDay;
    }

    public void setDefaultDay(int defaultDay) {
        this.defaultDay = defaultDay;
    }
    
    public void setDayOption(String dayOption) {
        this.dayOption = dayOption;
    }

    public String getDayOption() {
        return dayOption;
    }
    
    public String[] getDaysOfWeek() {
        return daysOfWeek;
    }
}
