/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.timeandmoney.service;

public interface AwardFnaDistributionService {
    
    /**
     * 
     * This method returns true if the Award F and A Distribution panel equality validation should be displayed as a warning.
     * @return
     */
    boolean displayAwardFAndADistributionEqualityValidationAsWarning();
    
    /**
     * 
     * This method returns true if the Award F and A Distribution panel equality validation should be displayed as an error.
     * @return
     */
    boolean displayAwardFAndADistributionEqualityValidationAsError();
    
    /**
     * 
     * This method returns true if the F and A Distribution Equality validation should NOT be run.
     * @return
     */
    boolean disableFAndADistributionEqualityValidation();
}
