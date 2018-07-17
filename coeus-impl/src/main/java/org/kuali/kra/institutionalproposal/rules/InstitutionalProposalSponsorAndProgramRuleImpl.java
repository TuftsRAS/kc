/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.rules;

import org.kuali.coeus.common.framework.sponsor.Sponsor;
import org.kuali.coeus.sys.framework.rule.KcTransactionalDocumentRuleBase;
import org.kuali.coeus.sys.framework.util.DateUtils;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InstitutionalProposalSponsorAndProgramRuleImpl extends KcTransactionalDocumentRuleBase implements
        InstitutionalProposalSponsorAndProgramRule {

    public static final String CFDA_NUMBER = "cfdaNumber";

    @Override
    public boolean processInstitutionalProposalSponsorAndProgramRules(
            InstitutionalProposalSponsorAndProgramRuleEvent institutionalProposalSponsorAndProgramRuleEvent) {
        return processCommonValidations(institutionalProposalSponsorAndProgramRuleEvent.getInstitutionalProposalForValidation());
    }

    public boolean processCommonValidations(InstitutionalProposal institutionalProposal) {
        boolean validSponsorCode = validateSponsorCodeExists(institutionalProposal.getSponsorCode());
        
        boolean validPrimeSponsorId = validatePrimeSponsorIdExists(institutionalProposal.getPrimeSponsorCode());
        
        boolean validSponsorDeadlineTime = validateSponsorDeadlineTime(institutionalProposal);
        
        return validSponsorCode && validSponsorDeadlineTime ;
    }
    
    private boolean validateSponsorDeadlineTime(InstitutionalProposal institutionalProposal) {
        boolean valid = true;
        if(!(institutionalProposal.getDeadlineTime() == null)) {

            String formatTime = DateUtils.formatFrom12Or24Str(institutionalProposal.getDeadlineTime());
            if (!formatTime.equalsIgnoreCase(Constants.INVALID_TIME)) {
                institutionalProposal.setDeadlineTime(formatTime);
            } else {
                GlobalVariables.getMessageMap().putError("document.institutionalProposal.deadlineTime", KeyConstants.INVALID_DEADLINE_TIME);
                valid = false;
            }
            
        }
        return valid;
    }

    private boolean validateSponsorCodeExists(String sponsorCode) {
        boolean valid = true;
        if(!(sponsorCode == null)) {
            Map<String, Object> fieldValues = new HashMap<>();
            fieldValues.put("sponsorCode", sponsorCode);
            List<Sponsor> sponsors = (List<Sponsor>)getBusinessObjectService().findMatching(Sponsor.class, fieldValues);
            if(sponsors.size() == 0) {
                this.reportError("document.institutionalProposalList[0].sponsorCode", KeyConstants.ERROR_INVALID_SPONSOR_CODE);
                valid = false;
            }
        }
       return valid;
        
    }

    private boolean validatePrimeSponsorIdExists(String primeSponsorId) {
        boolean valid = true;
        if (!(primeSponsorId == null)) {
            Map<String, Object> fieldValues = new HashMap<>();
            fieldValues.put("sponsorCode", primeSponsorId);
            List<Sponsor> sponsors = (List<Sponsor>)getBusinessObjectService().findMatching(Sponsor.class, fieldValues);
            if(sponsors.size() == 0) {
                this.reportError("document.institutionalProposalList[0].primeSponsorCode", KeyConstants.ERROR_INVALID_PRIME_SPONSOR_CODE);
                valid = false;
            }
        }
       return valid;
        
    }
}
