package org.kuali.coeus.propdev.impl.sponsor;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.api.sponsor.SponsorService;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.infrastructure.KeyConstants;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.DocumentAuditRule;
import org.kuali.rice.krad.util.AuditCluster;
import org.kuali.rice.krad.util.AuditError;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.ArrayList;
import java.util.List;

import static org.kuali.coeus.propdev.impl.datavalidation.ProposalDevelopmentDataValidationConstants.*;

public class ProposalDevelopmentSponsorAuditRule implements DocumentAuditRule {
    private SponsorService sponsorService;

    @Override
    public boolean processRunAuditBusinessRules(Document document) {
        boolean valid = true;

        ProposalDevelopmentDocument proposalDevelopmentDocument = (ProposalDevelopmentDocument)document;

        if (!getSponsorService().isValidSponsor(proposalDevelopmentDocument.getDevelopmentProposal().getSponsor())) {
            valid = false;
            getAuditErrors(DETAILS_PAGE_NAME, AUDIT_ERRORS).add(new AuditError(SPONSOR_KEY, KeyConstants.WARNING_PROPOSAL_INACTIVE_SPONSOR, DETAILS_PAGE_ID));
        }

        if (!StringUtils.isEmpty(proposalDevelopmentDocument.getDevelopmentProposal().getPrimeSponsorCode()) &&
                !getSponsorService().isValidSponsor(proposalDevelopmentDocument.getDevelopmentProposal().getPrimeSponsor())) {
            valid = false;
            getAuditErrors(DETAILS_PAGE_NAME, AUDIT_ERRORS).add(new AuditError(PRIME_SPONSOR_KEY, KeyConstants.WARNING_PROPOSAL_INACTIVE_PRIMESPONSOR, DETAILS_PAGE_ID));
        }

        return valid;
    }

    private List<AuditError> getAuditErrors(String areaName, String severity) {
        List<AuditError> auditErrors = new ArrayList<>();
        String clusterKey = areaName;

        if (!GlobalVariables.getAuditErrorMap().containsKey(clusterKey + severity)) {
            GlobalVariables.getAuditErrorMap().put(clusterKey + severity, new AuditCluster(clusterKey, auditErrors, severity));
        } else {
            auditErrors = GlobalVariables.getAuditErrorMap().get(clusterKey + severity).getAuditErrorList();
        }

        return auditErrors;
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
