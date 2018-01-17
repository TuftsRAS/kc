/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.subaward.subawardrule;

import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.kra.subaward.bo.SubAwardContact;


public interface SubAwardContactRule extends
org.kuali.rice.krad.rules.rule.BusinessRule {
	/**.
	 *
	 * This method is for @param subAwardContact rule validation
	 * @param subAwardContact
	 * @param subAward
	 * @return boolean value
	 */
    public boolean processAddSubAwardContactBusinessRules(
    SubAwardContact subAwardContact, SubAward subAward);
}
