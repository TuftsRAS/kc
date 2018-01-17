/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.rules;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.sys.framework.rule.KcBusinessRule;
import org.kuali.coeus.sys.framework.rule.KcTransactionalDocumentRuleBase;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.coeus.sys.framework.validation.ErrorReporter;
import org.kuali.rice.core.api.util.RiceKeyConstants;

/**
 * 
 * This class implements the business rule for adding institutional proposal note.
 */
public class InstitutionalProposalNoteAddRule extends KcTransactionalDocumentRuleBase implements KcBusinessRule<InstitutionalProposalNoteAddEvent> {

    private ErrorReporter errorReporter;

    /**
     * 
     * This method is to validate that new Institutional Proposal note exist
     * @param event
     * @return
     */
    @Override
    public boolean processRules(InstitutionalProposalNoteAddEvent event) {
        boolean rulePassed = true;
        errorReporter = KcServiceLocator.getService(ErrorReporter.class);
        if (StringUtils.isBlank(event.getInstitutionalProposalNotepad().getNoteTopic())) {
            errorReporter.reportError("institutionalProposalNotepadBean.newInstitutionalProposalNotepad.noteTopic", RiceKeyConstants.ERROR_REQUIRED, new String[] {"Note Topic"});
                                      
            rulePassed = false;
        } 
        if (StringUtils.isBlank(event.getInstitutionalProposalNotepad().getComments())) {
            errorReporter.reportError("institutionalProposalNotepadBean.newInstitutionalProposalNotepad.comments", RiceKeyConstants.ERROR_REQUIRED, new String[] {"Note Text"});
            rulePassed = false;
        } 
        return rulePassed;
    }
}
