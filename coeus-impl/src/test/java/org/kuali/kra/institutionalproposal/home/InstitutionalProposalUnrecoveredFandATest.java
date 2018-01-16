/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.home;

import org.kuali.kra.bo.AbstractBoTest;

public class InstitutionalProposalUnrecoveredFandATest extends AbstractBoTest<InstitutionalProposalUnrecoveredFandA> {
    private static final int IP_UNRECOVERED_FNA_ATTRIBUTES_COUNT = 9;

    @Override
    protected Class<InstitutionalProposalUnrecoveredFandA> getBoClass() {
        return InstitutionalProposalUnrecoveredFandA.class;
    }

    @Override
    protected int getAttributeCount() {
        return IP_UNRECOVERED_FNA_ATTRIBUTES_COUNT;
    }
}
