/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.commitments;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;


public class AwardCostShareRuleEvent extends KcDocumentEventBase {
    private static final Logger LOG = LogManager.getLogger(AwardCostShareRuleEvent.class);
    
    private AwardCostShare awardCostShare;
    private String fieldName;

    public AwardCostShareRuleEvent(String errorPathPrefix, 
                                           AwardDocument awardDocument,
                                           AwardCostShare awardCostShare) {
        super("Cost Share", errorPathPrefix, awardDocument);
        this.awardCostShare = awardCostShare;
    }
    
    /**
     * Convenience method to return an AwardDocument
     * @return
     */
    public AwardDocument getAwardDocument() {
        return (AwardDocument) getDocument();
    }
    
    /**
     * This method returns the equipment item for validation
     * @return
     */
    public AwardCostShare getCostShareForValidation() {
        return awardCostShare;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    protected void logEvent() {
        LOG.info("Logging AwardCostShareRuleEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return AwardCostShareRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AwardCostShareRule)rule).processCostShareBusinessRules(this, 0);
    }

}
