/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.external.award;

import org.kuali.kra.external.service.KcDtoServiceBase;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposalCfda;

public class ProposalDtoService extends KcDtoServiceBase<ProposalDTO, InstitutionalProposal> {

	@Override
	public ProposalDTO buildDto(InstitutionalProposal proposal) {
		if (proposal != null) {
			ProposalDTO dto = new ProposalDTO();
			dto.setProposalNumber(proposal.getProposalNumber());
			dto.setRequestedStartDateTotal(proposal.getRequestedStartDateTotal());
			dto.setRequestedEndDateTotal(proposal.getRequestedEndDateTotal());
			dto.setProposalTotalAmount(proposal.getTotalCost());
			dto.setTotalDirectCostTotal(proposal.getTotalDirectCostTotal());
			dto.setTotalIndirectCostTotal(proposal.getTotalIndirectCostTotal());
			dto.setProposalLastUpdateDate(proposal.getUpdateTimestamp());
			dto.setAwardTypeCode(proposal.getAwardTypeCode());
			dto.setSponsorCode(proposal.getSponsorCode());
			dto.setStatusCode(proposal.getStatusCode());
			proposal.getProposalCfdas().stream().map(InstitutionalProposalCfda::getCfdaNumber).findFirst().ifPresent(dto::setCfdaNumber);
			dto.setTitle(proposal.getTitle());
			dto.setSponsorAwardNumber(proposal.getSponsorAwardNumber());
			return dto;
		} else {
			return null;
		}
	}
}
