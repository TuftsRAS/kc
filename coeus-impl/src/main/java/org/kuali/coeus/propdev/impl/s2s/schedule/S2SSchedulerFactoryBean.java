/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s.schedule;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * This class is required in order to stop scheduling of tasks whose cron triggers are either invalid or are those that will not
 * fire ever again.
 */
public class S2SSchedulerFactoryBean extends SchedulerFactoryBean {
    private static final Logger LOG = LogManager.getLogger(S2SSchedulerFactoryBean.class);
    private List<CronTrigger> triggers;

    /**
     * Gets the triggers attribute.
     * 
     * @return Returns the triggers.
     */
    public List<CronTrigger> getTriggers() {
        return triggers;
    }

    /**
     * This method checks if cron expressions are valid and will run. Only then, they are passed on to super class for scheduling
     * 
     * @param triggers The triggers to set.
     */
    public void setTriggers(List<CronTrigger> triggers) {
        this.triggers = triggers;

        CronExpression cronExpression;
        List<CronTrigger> schedulableTriggers = new ArrayList<CronTrigger>();

        for (CronTrigger cronTrigger : triggers) {
            try {
                cronExpression = new CronExpression(cronTrigger.getCronExpression());
                if (cronExpression.getNextValidTimeAfter(new Date()) == null) {
                    // The cron expression is valid, but will never trigger.
                    LOG.info(cronTrigger.getCronExpression() + " not valid cronexpression. The job will not be scheduled.");
                    continue;
                }
            }
            catch (ParseException e) {
                LOG.info(cronTrigger.getCronExpression() + " not valid cronexpression. The job will not be scheduled.");
                continue;
            }
            schedulableTriggers.add(cronTrigger);
        }
        super.setTriggers(schedulableTriggers.toArray(new CronTrigger[0]));
    }
}
