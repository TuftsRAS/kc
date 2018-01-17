/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.subcontracting.reporting;

import java.sql.Date;

public interface SubcontractingExpenditureCategoryService {
    
    public void populateAllAvailableCategoryExpenses();
    
    public void populateCategoryExpensesInDateRange(Date rangeStartDate, Date rangeEndDate);

}
