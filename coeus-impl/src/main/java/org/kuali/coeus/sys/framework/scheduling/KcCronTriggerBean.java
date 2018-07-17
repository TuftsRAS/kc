/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.sys.framework.scheduling;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.quartz.CronTrigger;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The KC Cron Trigger Bean is needed because we can't inject
 * the Cron Expression from the SpringBeans.xml file.  Rather,
 * we have to retrieve the Cron Expression from the System Parameters.
 */
public class KcCronTriggerBean extends CronTriggerFactoryBean {

    private static final Logger LOG = LogManager.getLogger(KcCronTriggerBean.class);
    
    private static final int defaultMisfireInstruction = CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW;
    
    private String defaultCronExpression = Constants.DEFAULT_CRON_EXPRESSION;
    private String parameterNamespace;
    private String parameterComponent;
    private String cronExpressionParameterName;
    private String triggerEnabledParameterName;
    private String startTimeParameterName;
    private String misfireSkipParameterName;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;

    /**
     * We need to set the Cron Expression based upon the value in the system parameters.
     */
    @Override
    public void afterPropertiesSet() throws ParseException {
        setCronExpression(getSystemCronExpression());
        setStartTime(getCronStartTime());
        setMisfireInstruction(getMisfireInstruction());
        super.afterPropertiesSet();
    }
    
    /**
     * Get the Cron Expression from the system parameters.
     * @return the Cron Expression
     */
    protected String getSystemCronExpression() {
        if (StringUtils.isNotBlank(cronExpressionParameterName)
                && getParameterService().parameterExists(parameterNamespace, parameterComponent, cronExpressionParameterName)) {
            final String param = getParameterService().getParameterValueAsString(parameterNamespace, parameterComponent, cronExpressionParameterName);
            if (param != null) {
                return param;
            } 
            LOG.warn("parameter [" + cronExpressionParameterName + "] not found using default value of [" + defaultCronExpression + "].");
    
        }
        return defaultCronExpression;
    }
    
    /**
     * If the trigger is disabled this will return a date 2 years in the future.
     * If the parm doesn't exist or is empty will return today's date.
     * If the parm does exist and has a parsable date in it, it will return that date.
     * If the parm does exist but the value cannot be parsed a date 2 years in the future will be returned.
     * @return
     */
    protected Date getCronStartTime() {
        Calendar yearInAdvance = dateTimeService.getCurrentCalendar();
        yearInAdvance.add(Calendar.YEAR, 2);
        Date disabledStartTime = yearInAdvance.getTime();
        Date cronStartTime = dateTimeService.getCurrentDate();
        if (!isTriggerEnabled()) {
            return disabledStartTime;
        } else if (!StringUtils.isBlank(startTimeParameterName) 
                && getParameterService().parameterExists(parameterNamespace, parameterComponent, startTimeParameterName)) {
            String CUSTOM_DATE_FORMAT = "dd-MMM-yyyy hh:mm a";
            SimpleDateFormat dateFormat = new SimpleDateFormat(CUSTOM_DATE_FORMAT);
            try {
                String parmStartTime = getParameterService().getParameterValueAsString(parameterNamespace, parameterComponent, startTimeParameterName);
                if (!StringUtils.isBlank(parmStartTime)) {
                    try {
                        cronStartTime = dateTimeService.convertToDate(parmStartTime);
                    } catch (ParseException e) {
                        cronStartTime = dateFormat.parse(parmStartTime);
                    }
                }
            } catch (Exception e) {
                //if we got an exception while getting or parsing the start time, use the disabled start time and log an error.
                cronStartTime = disabledStartTime;
                String defaultDateStr = dateFormat.format(cronStartTime);
                LOG.error("Not able to get the starttime for scheduler from system param table. Set it to " + defaultDateStr, e);
            }
        }
        return cronStartTime;
    }
    
    protected boolean isTriggerEnabled() {
        if (StringUtils.isNotBlank(triggerEnabledParameterName)) {
            if (getParameterService().parameterExists(parameterNamespace, parameterComponent, triggerEnabledParameterName)) {
                return getParameterService().getParameterValueAsBoolean(parameterNamespace, parameterComponent, triggerEnabledParameterName);
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    
    protected int getMisfireInstruction() {
    	if (StringUtils.isNotBlank(misfireSkipParameterName)
    			&& getParameterService().parameterExists(parameterNamespace, parameterComponent, misfireSkipParameterName)) {
    		if (getParameterService().getParameterValueAsBoolean(parameterNamespace, parameterComponent, misfireSkipParameterName)) {
    			return CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING;
    		}
    	}
    	return defaultMisfireInstruction;
    }

    public String getDefaultCronExpression() {
        return defaultCronExpression;
    }

    public void setDefaultCronExpression(String defaultCronExpression) {
        this.defaultCronExpression = defaultCronExpression;
    }

    public String getCronExpressionParameterName() {
        return cronExpressionParameterName;
    }

    public void setCronExpressionParameterName(String cronExpressionParameterName) {
        this.cronExpressionParameterName = cronExpressionParameterName;
    }

    public String getTriggerEnabledParameterName() {
        return triggerEnabledParameterName;
    }

    public void setTriggerEnabledParameterName(String triggerEnabledParameterName) {
        this.triggerEnabledParameterName = triggerEnabledParameterName;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public String getStartTimeParameterName() {
        return startTimeParameterName;
    }

    public void setStartTimeParameterName(String startTimeParameterName) {
        this.startTimeParameterName = startTimeParameterName;
    }

    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public String getParameterNamespace() {
        return parameterNamespace;
    }

    public void setParameterNamespace(String parameterNamespace) {
        this.parameterNamespace = parameterNamespace;
    }

    public String getParameterComponent() {
        return parameterComponent;
    }

    public void setParameterComponent(String parameterComponent) {
        this.parameterComponent = parameterComponent;
    }

	public String getMisfireSkipParameterName() {
		return misfireSkipParameterName;
	}

	public void setMisfireSkipParameterName(String misfireSkipParameterName) {
		this.misfireSkipParameterName = misfireSkipParameterName;
	}
}
