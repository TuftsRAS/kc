/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.rules;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.api.sponsor.SponsorService;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.kra.institutionalproposal.document.InstitutionalProposalDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.DocumentAuditRule;
import org.kuali.rice.krad.util.AuditCluster;
import org.kuali.rice.krad.util.AuditError;

import java.util.ArrayList;
import java.util.List;

public class InstitutionalProposalSponsorAndProgramInformationAuditRule implements DocumentAuditRule {

    private static final String SPONSOR_WARNINGS = "sponsorWarnings";
    public static final String PROGRAM_INFO_WARNINGS = "programInfoWarnings";
    public static final String PROGRAM_INFORMATION = "Program Information";
    public static final String SPONSORS = "Sponsors";
    private SponsorService sponsorService;
    private GlobalVariableService globalVariableService;

    @Override
    public boolean processRunAuditBusinessRules(Document document) {
        boolean valid = true;
        final List<AuditError> sponsorAuditWarnings = new ArrayList<>();
        final List<AuditError> programInfoAuditWarnings = new ArrayList<>();

        final InstitutionalProposalDocument institutionalProposalDocument = (InstitutionalProposalDocument) document;

        if (!getSponsorService().isValidSponsor(institutionalProposalDocument.getInstitutionalProposal().getSponsor())) {
            sponsorAuditWarnings.add(new AuditError("document.institutionalProposalList[0].sponsorCode", KeyConstants.WARNING_INSTITUTIONALPROPOSAL_INACTIVE_SPONSOR,
                    Constants.MAPPING_INSTITUTIONAL_PROPOSAL_HOME_PAGE + "." + Constants.INSTITUTIONAL_PROPOSAL_IP_PANEL_ANCHOR));
            valid = false;
        }

        if (!StringUtils.isEmpty(institutionalProposalDocument.getInstitutionalProposal().getPrimeSponsorCode()) &&
                !getSponsorService().isValidSponsor(institutionalProposalDocument.getInstitutionalProposal().getPrimeSponsor())) {
            sponsorAuditWarnings.add(new AuditError("document.institutionalProposalList[0].primeSponsorCode", KeyConstants.WARNING_INSTITUTIONALPROPOSAL_INACTIVE_PRIMESPONSOR,
                    Constants.MAPPING_INSTITUTIONAL_PROPOSAL_HOME_PAGE + "." + Constants.INSTITUTIONAL_PROPOSAL_IP_PANEL_ANCHOR ));
            valid = false;
        }

        for (int i = 0; i < institutionalProposalDocument.getInstitutionalProposal().getProposalCfdas().size(); i ++) {
            final String cfdaNumber = institutionalProposalDocument.getInstitutionalProposal().getProposalCfdas().get(i).getCfdaNumber();
            if (!isValidCfda(cfdaNumber)) {
                programInfoAuditWarnings.add(new AuditError(String.format(Constants.INSTITUTIONAL_PROPOSAL_CFDA_NUMBER, i), KeyConstants.CFDA_INVALID,
                        Constants.MAPPING_INSTITUTIONAL_PROPOSAL_HOME_PAGE + "." + Constants.INSTITUTIONAL_PROPOSAL_IP_PANEL_ANCHOR,
                        new String[]{cfdaNumber} ));
            }
        }

        if (!sponsorAuditWarnings.isEmpty()) {
            getGlobalVariableService().getAuditErrorMap().put(SPONSOR_WARNINGS, new AuditCluster(SPONSORS, sponsorAuditWarnings, Constants.AUDIT_WARNINGS));
        }

        if (!programInfoAuditWarnings.isEmpty()) {
            getGlobalVariableService().getAuditErrorMap().put(PROGRAM_INFO_WARNINGS, new AuditCluster(PROGRAM_INFORMATION, programInfoAuditWarnings, Constants.AUDIT_WARNINGS));
        }

        return valid;
    }

    public boolean isValidCfda(String cfdaNumber) {
        return StringUtils.isBlank(cfdaNumber) || cfdaNumber.matches(Constants.CFDA_REGEX);
    }

    public GlobalVariableService getGlobalVariableService() {
        if (globalVariableService == null) {
            globalVariableService = KcServiceLocator.getService(GlobalVariableService.class);
        }

        return globalVariableService;
    }

    public void setGlobalVariableService(GlobalVariableService globalVariableService) {
        this.globalVariableService = globalVariableService;
    }

    private SponsorService getSponsorService() {
        if (sponsorService == null) {
            sponsorService = KcServiceLocator.getService(SponsorService.class);
        }
        return sponsorService;
    }

    public void setSponsorService(SponsorService sponsorService) {
        this.sponsorService = sponsorService;
    }
}
