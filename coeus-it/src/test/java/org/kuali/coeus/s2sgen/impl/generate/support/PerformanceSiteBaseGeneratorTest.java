/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.s2sgen.impl.generate.support;


import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.propdev.impl.location.ProposalSite;

import java.util.ArrayList;
import java.util.List;

public abstract class PerformanceSiteBaseGeneratorTest extends S2STestBase {

	@Override
	protected void prepareData(ProposalDevelopmentDocument document)
			throws Exception {
		List<ProposalSite> proposalSites = new ArrayList<>();
		int siteNumber = 0;

		proposalSites.add(createProposalSite(document, ++siteNumber, ProposalSite.PROPOSAL_SITE_PERFORMING_ORGANIZATION));
		document.getDevelopmentProposal().setProposalSites(proposalSites);
	}

}
