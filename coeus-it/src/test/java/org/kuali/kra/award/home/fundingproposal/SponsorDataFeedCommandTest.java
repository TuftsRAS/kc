/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.award.home.fundingproposal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposalCfda;

public class SponsorDataFeedCommandTest extends BaseDataFeedCommandTest {
    
    private ProposalDataFeedCommandBase command;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        initializeProposal();
    }
    
    @Test
    public void testSponsorFeed() {
        command = new SponsorDataFeedCommand(award, proposal, FundingProposalMergeType.NEWAWARD);
        command.performDataFeed();
        Assert.assertEquals(proposal.getSponsorCode(), award.getSponsorCode());
        Assert.assertEquals(proposal.getSponsor().getSponsorCode(), award.getSponsor().getSponsorCode());
        Assert.assertEquals(proposal.getSponsor().getSponsorName(), award.getSponsor().getSponsorName());
        Assert.assertEquals(proposal.getPrimeSponsorCode(), award.getPrimeSponsorCode());
        Assert.assertEquals(proposal.getPrimeSponsor().getSponsorName(), award.getPrimeSponsor().getSponsorName());
    }
    
    @Test
    public void testReplaceSponsorFeed() {
        command = new SponsorDataFeedCommand(award, proposal, FundingProposalMergeType.REPLACE);
        command.performDataFeed();
        Assert.assertNotSame(proposal.getSponsorCode(), award.getSponsorCode());
        Assert.assertEquals(proposal.getPrimeSponsorCode(), award.getPrimeSponsorCode());
        Assert.assertEquals(proposal.getPrimeSponsor().getSponsorName(), award.getPrimeSponsor().getSponsorName());        
    }
    
    private void initializeProposal() {
        proposal.setSponsorCode("000107");
        proposal.setPrimeSponsorCode("000107");
        proposal.refreshReferenceObject("sponsor");
        proposal.refreshReferenceObject("primeSponsor");

        InstitutionalProposalCfda proposalCfda = new InstitutionalProposalCfda();
        proposalCfda.setCfdaNumber("abc.123");
        proposalCfda.setProposalNumber(proposal.getProposalNumber());
        proposalCfda.setProposalId(proposal.getProposalId());
        proposalCfda.setSequenceNumber(proposal.getSequenceNumber());
        proposal.getProposalCfdas().add(proposalCfda);
        proposal.setNsfSequenceNumber(1);
    }
}
