/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.propdev.impl.budget.note;

import org.kuali.coeus.common.budget.framework.core.BudgetException;
import org.kuali.coeus.common.budget.framework.nonpersonnel.BudgetJustificationService;
import org.kuali.coeus.propdev.impl.budget.core.ProposalBudgetControllerBase;
import org.kuali.coeus.propdev.impl.budget.core.ProposalBudgetForm;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.controller.MethodAccessible;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class ProposalBudgetNoteController extends ProposalBudgetControllerBase {

    @Autowired
    @Qualifier("budgetJustificationService")
    private BudgetJustificationService budgetJustificationService;

    @Override
    public BudgetJustificationService getBudgetJustificationService() {
        return budgetJustificationService;
    }

    @Override
    public void setBudgetJustificationService(BudgetJustificationService budgetJustificationService) {
        this.budgetJustificationService = budgetJustificationService;
    }

    @MethodAccessible
    @Transactional @RequestMapping(value = "/proposalBudget", params={"methodToCall=consolidateExpenseJustifications"})
    public ModelAndView consolidateExpenseJustifications(@ModelAttribute("KualiForm") ProposalBudgetForm form) throws Exception {
        try {
            getBudgetJustificationService().consolidateExpenseJustifications(form.getBudget(), form.getBudgetJustificationWrapper());
        } catch (BudgetException exc) {
            GlobalVariables.getMessageMap().putError("budgetJustificationWrapper.justificationText", "error.custom", "There are no line item budget justifications");
        }
        return getRefreshControllerService().refresh(form);
    }

}


