/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.committee.impl.rules;

import org.kuali.coeus.common.committee.impl.bo.CommitteeScheduleBase;
import org.kuali.coeus.common.committee.impl.rule.event.CommitteeScheduleDateConflictEvent;
import org.kuali.coeus.common.committee.impl.rule.event.CommitteeScheduleEventBase.ErrorType;
import org.kuali.coeus.sys.framework.rule.KcBusinessRule;
import org.kuali.coeus.sys.framework.rule.KcTransactionalDocumentRuleBase;
import org.kuali.coeus.sys.framework.util.DateUtils;
import org.kuali.kra.infrastructure.KeyConstants;

import java.sql.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CommitteeScheduleDateConflictRule extends KcTransactionalDocumentRuleBase implements KcBusinessRule<CommitteeScheduleDateConflictEvent> {
    
    @SuppressWarnings("unused")
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(CommitteeScheduleDateConflictRule.class);
    
    public static final String ID = "document.committeeList[0].committeeSchedules[%1$s].scheduledDate";
    
    public static final String DATES_IN_CONFLICT_ERROR_KEY = "datesInConflict";
    

    @Override
    public boolean processRules(CommitteeScheduleDateConflictEvent addCommitteeScheduleEvent) {
        
        boolean rulePassed = true;   
        ErrorType type = addCommitteeScheduleEvent.getType();        
        switch (type) {
            case HARDERROR:
                rulePassed = processHardErrors(addCommitteeScheduleEvent);
                break;
            case SOFTERROR:
                rulePassed = processSoftErrors(addCommitteeScheduleEvent);
                break;
        }
        return rulePassed;
    }
    
    /**
     * This method process hard error in date conflict.
     * @param addCommitteeScheduleEvent
     * @return
     */
    private boolean processHardErrors(CommitteeScheduleDateConflictEvent addCommitteeScheduleEvent) {
        boolean rulePassed = true;
        List<CommitteeScheduleBase> committeeSchedules = addCommitteeScheduleEvent.getCommitteeSchedules();   
        List<Date> conflictDates = new LinkedList<Date>();
        
        rulePassed = parseUniqueDateSet(committeeSchedules, conflictDates);
        if(!rulePassed) {
            identifyPotentialConflicts(committeeSchedules, conflictDates);
        }
        return rulePassed;
    }
    
    /**
     * Helper method to find if dates in schedule are not conflicting.
     * @param committeeSchedules is list of CommitteeScheduleBase.
     * @param Date in conflict are added to conflictDates list. 
     * @return true if no conflict else false.
     */
    private boolean parseUniqueDateSet(List<CommitteeScheduleBase> committeeSchedules, List<Date> conflictDates){
        boolean retVal = true;
        boolean flag = true;
        Set<Date> set = new LinkedHashSet<Date>();
        for(CommitteeScheduleBase committeeSchedule : committeeSchedules) {
            flag = true;
            flag = set.add(committeeSchedule.getScheduledDate());
            if(!flag)
                conflictDates.add(committeeSchedule.getScheduledDate());
        }        
        if(conflictDates.size() > 0)
            retVal = false;
        return retVal;
    }
    
    /**
     * Helper method to identify potential conflicts and adds error message to map.
     * @param committeeSchedules
     * @param conflictDates
     */
    private void identifyPotentialConflicts(List<CommitteeScheduleBase> committeeSchedules, List<Date> conflictDates) {
        Date scheduleDate = null;
        int count = 0;
        for(Date date : conflictDates) {
            count = 0;
            for(CommitteeScheduleBase committeeSchedule : committeeSchedules) {
                scheduleDate = committeeSchedule.getScheduledDate();
                if(DateUtils.isSameDay(date, scheduleDate)){
                    reportError(String.format(ID, count), KeyConstants.ERROR_COMMITTEESCHEDULE_DATE_CONFLICT, scheduleDate.toString());
                }
                count++;
            }   
        }
    }
    
    /**
     * This method process date conflict for soft error message.
     * @param addCommitteeScheduleEvent
     * @return
     */
    private boolean processSoftErrors(CommitteeScheduleDateConflictEvent addCommitteeScheduleEvent) {
        boolean rulePassed = true;
        List<Date> datesInConflict = addCommitteeScheduleEvent.getScheduleData().getDatesInConflict();
        for(Date date: datesInConflict) {
            reportSoftError(DATES_IN_CONFLICT_ERROR_KEY, KeyConstants.ERROR_COMMITTEESCHEDULE_DATES_SKIPPED, date.toString());
        }
        return rulePassed;
    }
}
