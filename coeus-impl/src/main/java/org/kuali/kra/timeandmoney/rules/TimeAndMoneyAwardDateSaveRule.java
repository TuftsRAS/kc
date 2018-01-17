/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.timeandmoney.rules;

import org.kuali.kra.timeandmoney.rule.event.TimeAndMoneyAwardDateSaveEvent;
import org.kuali.rice.krad.rules.rule.BusinessRule;


public interface TimeAndMoneyAwardDateSaveRule extends BusinessRule {

    /**
     * This method processes must be implemented and enforces all add method business rules.
     * @param timeAndMoneyAwardAmountTransactionSaveEvent
     * @return
     */
    boolean processSaveAwardDatesBusinessRules(TimeAndMoneyAwardDateSaveEvent 
            timeAndMoneyAwardDateSaveEvent);
}
