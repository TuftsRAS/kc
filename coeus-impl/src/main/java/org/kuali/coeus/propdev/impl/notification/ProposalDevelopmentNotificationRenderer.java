/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.notification;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.notification.impl.NotificationRendererBase;
import org.kuali.coeus.propdev.impl.attachment.Narrative;
import org.kuali.coeus.propdev.impl.auth.perm.ProposalDevelopmentPermissionsService;
import org.kuali.coeus.propdev.impl.budget.editable.BudgetChangedData;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentService;
import org.kuali.coeus.propdev.impl.docperm.ProposalUserRoles;
import org.kuali.coeus.propdev.impl.editable.ProposalChangedData;
import org.kuali.coeus.propdev.impl.person.ProposalPerson;
import org.kuali.coeus.propdev.impl.sponsor.ProposalCfda;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.kra.infrastructure.RoleConstants;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Renders fields for the Proposal Development notifications.
 */

@Component("proposalDevelopmentNotificationRenderer")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProposalDevelopmentNotificationRenderer extends NotificationRendererBase {

    private static final long serialVersionUID = 1143944858168503090L;
    private static final String MM_DD_YYYY = "MM/dd/yyyy";
    private static final String DELIMITER = ",";

    private DevelopmentProposal developmentProposal;
    private ProposalChangedData proposalChangedData;
    private BudgetChangedData budgetChangedData;
    private Narrative modifiedNarrative;
    private ProposalPerson proposalPerson;
    private static final String COI_URL = "coi.standalone.base.url";
    
    @Autowired
    @Qualifier("proposalDevelopmentService")
    private transient ProposalDevelopmentService proposalDevelopmentService;
    
    @Autowired
    @Qualifier("parameterService")
    private transient ParameterService parameterService;
    
    @Autowired
    @Qualifier("proposalDevelopmentPermissionsService")
    private ProposalDevelopmentPermissionsService proposalDevelopmentPermissionsService;

    @Autowired
    @Qualifier("globalVariableService")
    private GlobalVariableService globalVariableService;


    public ProposalDevelopmentNotificationRenderer() {
        super();
    }

    public ProposalDevelopmentNotificationRenderer(DevelopmentProposal developmentProposal) {
        this.developmentProposal = developmentProposal;
    }

    @Override
    public Map<String, String> getDefaultReplacementParameters() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(MM_DD_YYYY);
        Map<String, String> result = super.getDefaultReplacementParameters();
        result.put("{DOCUMENT_NUMBER}", developmentProposal.getProposalDocument().getDocumentNumber());
        result.put("{PROPOSAL_NUMBER}", developmentProposal.getProposalNumber());
        result.put("{PROPOSAL_TITLE}", developmentProposal.getTitle());
        result.put("{PRINCIPAL INVESTIGATOR}", developmentProposal.getPrincipalInvestigatorName());
        result.put("{SPONSOR_CODE}", developmentProposal.getSponsorCode());
        result.put("{SPONSOR_NAME}", developmentProposal.getSponsorName());
        result.put("{START_DATE}", developmentProposal.getRequestedStartDateInitial().toString());
        result.put("{END_DATE}", developmentProposal.getRequestedEndDateInitial().toString());
        result.put("{PROGRAM_ANNOUNCEMENT_NUMBER}", developmentProposal.getProgramAnnouncementNumber() == null ? StringUtils.EMPTY : developmentProposal.getProgramAnnouncementNumber());
        result.put("{PROGRAM_ANNOUNCEMENT_TITLE}", developmentProposal.getProgramAnnouncementTitle() == null ? StringUtils.EMPTY : developmentProposal.getProgramAnnouncementTitle());
        result.put("{CFDA_NUMBER}", developmentProposal.getProposalCfdas().stream().map(ProposalCfda::getCfdaNumber).collect(Collectors.joining(",")));
        if (developmentProposal.getDeadlineDate() != null) {
            result.put("{DEADLINE_DATE}", dateFormatter.format(developmentProposal.getDeadlineDate()));
        } else {
            result.put("{DEADLINE_DATE}", StringUtils.EMPTY);
        }
        result.put("{PI_NAME}", developmentProposal.getPrincipalInvestigatorName());
        result.put("{LEAD_UNIT}", developmentProposal.getUnitNumber());
        result.put("{LEAD_UNIT_NAME}", developmentProposal.getUnit().getUnitName());
        result.put("{PRIME_SPONSOR_CODE}", developmentProposal.getPrimeSponsorCode());
        result.put("{PRIME_SPONSOR_NAME}", developmentProposal.getPrimeSponsor() != null ? developmentProposal.getPrimeSponsor().getSponsorName() : StringUtils.EMPTY);
        InstitutionalProposal instProp = getProposalDevelopmentService().getInstitutionalProposal(developmentProposal.getProposalNumber());
        result.put("{INSTITUTIONAL_PROPOSAL_NUMBER}", instProp != null ? instProp.getProposalNumber() : StringUtils.EMPTY);
        result.put("{INSTITUTIONAL_PROPOSAL_DOC_NBR}", instProp != null ? instProp.getInstitutionalProposalDocument().getDocumentNumber() : StringUtils.EMPTY);
        if (proposalChangedData != null) {
            result.put("{OVERRIDE_FIELD_NAME}", proposalChangedData.getEditableColumn().getColumnLabel());
            result.put("{OVERRIDE_FIELD_VALUE}", proposalChangedData.getDisplayValue());
        }
        if (budgetChangedData != null) {
            result.put("{OVERRIDE_FIELD_NAME}", budgetChangedData.getEditableColumn().getColumnLabel());
            result.put("{OVERRIDE_FIELD_VALUE}", budgetChangedData.getDisplayValue());
        }
        if (modifiedNarrative != null) {
            result.put("{NARRATIVE_MODULE_NUM}", modifiedNarrative.getModuleNumber().toString());
            result.put("{NARRATIVE_TYPE}", modifiedNarrative.getNarrativeType().getDescription());
            result.put("{NARRATIVE_MODULE_DESCRIPTION}", modifiedNarrative.getModuleTitle() == null ? StringUtils.EMPTY : modifiedNarrative.getModuleTitle());
        }
        if (proposalPerson != null) {
            result.put("{USER_NAME}", proposalPerson.getUserName());
            if (proposalPerson.getCertificationDetails() != null) {
                result.put("{PROPOSAL_CERTIFY_USER}", proposalPerson.getCertificationDetails().getCertifiedPersonName());
                result.put("{PROPOSAL_CERTIFY_TIME_STAMP}", proposalPerson.getCertificationDetails().getCertifiedTimeStamp());
            }
            result.put("{AGGREGATOR}", getAggregators());
            String certificatioPage =result.get("{APP_LINK_PREFIX}") + "/kc-pd-krad/proposalDevelopment?methodToCall=viewUtility&" +
        			"viewId=PropDev-CertificationView&docId=" + developmentProposal.getProposalDocument().getDocumentNumber() + "&userName="+proposalPerson.getUserName();
        	result.put("{CERT_PAGE}", certificatioPage);
        	String coiLink =   getKualiConfigurationService().getPropertyValueAsString(COI_URL);
            result.put("{LINK_TO_COI}", coiLink);
        }

        final UserSession userSession = globalVariableService.getUserSession();
        if (userSession != null && userSession.getPerson() != null) {
            result.put("{PROPOSAL_INITIATOR_NAME}", userSession.getPerson().getFirstName() + StringUtils.SPACE + userSession.getPerson().getLastName());
        }
        
        return result;
    }

    private String getAggregators() {
    	List<ProposalUserRoles> proposalUserRoles =  getProposalDevelopmentPermissionsService().getPermissions(developmentProposal.getProposalDocument());
    	return proposalUserRoles.stream().filter(proposalUserRole -> proposalUserRole.getRoleNames().
    			contains(RoleConstants.AGGREGATOR_DOCUMENT_LEVEL)).map(ProposalUserRoles::getUsername).collect(Collectors.joining(DELIMITER));
    }
    
    public DevelopmentProposal getDevelopmentProposal() {
        return developmentProposal;
    }

    public void setDevelopmentProposal(DevelopmentProposal developmentProposal) {
        this.developmentProposal = developmentProposal;
    }

    public ProposalDevelopmentService getProposalDevelopmentService() {
        return proposalDevelopmentService;
    }

    public void setProposalDevelopmentService(ProposalDevelopmentService proposalDevelopmentService) {
        this.proposalDevelopmentService = proposalDevelopmentService;
    }

    public ProposalChangedData getProposalChangedData() {
        return proposalChangedData;
    }

    public void setProposalChangedData(ProposalChangedData proposalChangedData) {
        this.proposalChangedData = proposalChangedData;
    }

    public BudgetChangedData getBudgetChangedData() {
        return budgetChangedData;
    }

    public void setBudgetChangedData(BudgetChangedData budgetChangedData) {
        this.budgetChangedData = budgetChangedData;
    }

    public Narrative getModifiedNarrative() {
        return modifiedNarrative;
    }

    public void setModifiedNarrative(Narrative modifiedNarrative) {
        this.modifiedNarrative = modifiedNarrative;
    }

    public ProposalPerson getProposalPerson() {
        return proposalPerson;
    }

    public void setProposalPerson(ProposalPerson proposalPerson) {
        this.proposalPerson = proposalPerson;
    }
    
    public ParameterService getParameterService() {
		return parameterService;
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}
	
	public ProposalDevelopmentPermissionsService getProposalDevelopmentPermissionsService() {
		return proposalDevelopmentPermissionsService;
	}

	public void setProposalDevelopmentPermissionsService(
			ProposalDevelopmentPermissionsService proposalDevelopmentPermissionsService) {
		this.proposalDevelopmentPermissionsService = proposalDevelopmentPermissionsService;
	}
}
