/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.budget.editable;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class BudgetDataOverrideEvent extends KcDocumentEventBase {
private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(BudgetDataOverrideEvent.class);
    
    private BudgetChangedData budgetChangedData;

    public BudgetDataOverrideEvent(ProposalDevelopmentDocument document, BudgetChangedData budgetChangedData) {
        super("Override Budget Data " + getDocumentId(document), "", document);
        this.setBudgetChangedData(budgetChangedData);
        logEvent();
    }
    
    @Override
    public void validate() {
        super.validate();
    }
    
    @Override
    protected void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        LOG.debug(logMessage);
    }

    @Override
    public Class<? extends BudgetDataOverrideRule> getRuleInterfaceClass() {
        return BudgetDataOverrideRule.class;
    }

    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((BudgetDataOverrideRule) rule).processBudgetDataOverrideRules(this);
    }

        public void setBudgetChangedData(BudgetChangedData budgetChangedData) {
        this.budgetChangedData = budgetChangedData;
    }

    public BudgetChangedData getBudgetChangedData() {
        return budgetChangedData;
    }
}
