/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.notification;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.notification.impl.NotificationRendererBase;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposalCfda;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Renders fields for the Institutional Proposal notifications.
 */
public class InstitutionalProposalNotificationRenderer extends NotificationRendererBase implements Serializable {

    private static final long serialVersionUID = 451541228341893685L;
    private static final String MM_DD_YYYY = "MM/dd/yyyy";

    private InstitutionalProposal institutionalProposal;
    
    public InstitutionalProposalNotificationRenderer() {
        super();
    }

    public InstitutionalProposalNotificationRenderer(InstitutionalProposal institutionalProposal) {
        this.institutionalProposal = institutionalProposal;
    }

    @Override
    public Map<String, String> getDefaultReplacementParameters() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(MM_DD_YYYY);
        Map<String, String> result = super.getDefaultReplacementParameters();
        result.put("{PROPOSAL_NUMBER}", institutionalProposal.getProposalNumber());
        result.put("{PROPOSAL_TITLE}", institutionalProposal.getTitle());
        result.put("{PI_NAME}", institutionalProposal.getPiName());
        result.put("{LEAD_UNIT}", institutionalProposal.getLeadUnitNumber());
        result.put("{LEAD_UNIT_NAME}", institutionalProposal.getLeadUnitName());
        result.put("{SPONSOR_CODE}", institutionalProposal.getSponsorCode());
        result.put("{SPONSOR_NAME}", institutionalProposal.getSponsorName());
        result.put("{ACTIVITY_TYPE_CODE}", institutionalProposal.getActivityTypeCode());
        result.put("{ACTIVITY_TYPE_NAME}", institutionalProposal.getActivityTypeFromCode().getDescription());
        if (institutionalProposal.getDeadlineDate() != null) {
            result.put("{DEADLINE_DATE}", dateFormatter.format(institutionalProposal.getDeadlineDate()));
        } else {
            result.put("{DEADLINE_DATE}", StringUtils.EMPTY);
        }
        result.put("{DEADLINE_TIME}", institutionalProposal.getDeadlineTime());
        result.put("{CFDA_NUMBER}", institutionalProposal.getProposalCfdas().stream().map(InstitutionalProposalCfda::getCfdaNumber).collect(Collectors.joining(",")));
        result.put("{OPPORTUNITY}", institutionalProposal.getOpportunity());
        return result;
    }

    public InstitutionalProposal getInstitutionalProposal() {
        return institutionalProposal;
    }

    public void setInstitutionalProposal(InstitutionalProposal institutionalProposal) {
        this.institutionalProposal = institutionalProposal;
    }
    
}
