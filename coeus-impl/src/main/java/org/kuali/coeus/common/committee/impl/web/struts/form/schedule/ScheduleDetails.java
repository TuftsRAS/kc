/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.committee.impl.web.struts.form.schedule;

import java.io.Serializable;
import java.sql.Date;


/**
 * This class holds common UI data of all types of recurrence.
 */
public class ScheduleDetails implements Serializable {
    
    @SuppressWarnings("unused")
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(ScheduleDetails.class);
 
    private Date scheduleEndDate;

    public ScheduleDetails() {
        super();
        setScheduleEndDate(new Date(new java.util.Date().getTime()));
    }

    public Date getScheduleEndDate() {
        return scheduleEndDate;
    }

    public void setScheduleEndDate(Date scheduleEndDate) {
        this.scheduleEndDate = scheduleEndDate;
    }

}
