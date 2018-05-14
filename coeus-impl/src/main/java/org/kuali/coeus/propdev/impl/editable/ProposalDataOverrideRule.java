/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.editable;

import org.kuali.rice.krad.rules.rule.BusinessRule;

public interface ProposalDataOverrideRule extends BusinessRule {
    
    /**
     * Validates the Overriding of a proposal data.  
     *
     * @return true if the update request is valid; otherwise false
     */
    boolean processProposalDataOverrideRules(ProposalDataOverrideEvent proposalDataOverrideEvent);
}
