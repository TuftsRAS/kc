/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.budget.nonpersonnel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.propdev.impl.budget.ProposalBudgetCategoryValueFinder;
import org.kuali.coeus.propdev.impl.budget.core.ProposalBudgetForm;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("budgetNonPersonnelBudgetCategoryValuesFinder")
public class BudgetNonPersonnelCategoryValuesFinder extends ProposalBudgetCategoryValueFinder {

    @Autowired
    @Qualifier("parameterService")
    private ParameterService parameterService;
    
    @Override
    protected List<Predicate> getPredicates(ProposalBudgetForm model) {
        String budgetCategoryTypeCode = model.getAddProjectBudgetLineItemHelper().getBudgetCategoryTypeCode();
        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotEmpty(budgetCategoryTypeCode)) {
            predicates.add(PredicateFactory.equal(BUDGET_CATEGORY_TYPE_CODE, budgetCategoryTypeCode));
        } else {
            predicates.add(PredicateFactory.notEqual(BUDGET_CATEGORY_TYPE_CODE,getPersonnelBudgetCategoryTypeCode()));
        }
        return predicates;
    }

    private String getPersonnelBudgetCategoryTypeCode() {
        return this.getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_BUDGET, ParameterConstants.DOCUMENT_COMPONENT,Constants.BUDGET_CATEGORY_TYPE_PERSONNEL);
    }
    
	public ParameterService getParameterService() {
		return parameterService;
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

}
