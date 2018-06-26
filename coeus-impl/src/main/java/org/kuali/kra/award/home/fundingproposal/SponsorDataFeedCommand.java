/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.home.fundingproposal;

import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.AwardCfda;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;

import java.util.stream.Collectors;

class SponsorDataFeedCommand extends ProposalDataFeedCommandBase {

    public SponsorDataFeedCommand(Award award, InstitutionalProposal proposal, FundingProposalMergeType mergeType) {
        super(award, proposal, mergeType);
    }

    @Override
    void performDataFeed() {
        if (mergeType == FundingProposalMergeType.NEWAWARD) {
            award.setSponsor(proposal.getSponsor());
            award.setSponsorCode(proposal.getSponsorCode());
        } 
        if (mergeType == FundingProposalMergeType.NEWAWARD 
                || mergeType == FundingProposalMergeType.REPLACE) {
            award.setPrimeSponsor(proposal.getPrimeSponsor());
            award.setPrimeSponsorCode(proposal.getPrimeSponsorCode());
            award.setAwardCfdas(proposal.getProposalCfdas().stream().map(cfda -> {
                final AwardCfda awardCfda = new AwardCfda();
                awardCfda.setCfdaNumber(cfda.getCfdaNumber());
                awardCfda.setCfdaDescription(cfda.getCfdaDescription());
                awardCfda.setAwardId(award.getAwardId());
                awardCfda.setAwardNumber(award.getAwardNumber());
                awardCfda.setSequenceNumber(award.getSequenceNumber());
                awardCfda.setAward(award);

                return awardCfda;
            }).collect(Collectors.toList()));
            award.setNsfSequenceNumber(proposal.getNsfSequenceNumber());
        }
    }

}
