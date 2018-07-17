/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.kra.institutionalproposal.home;

import org.kuali.coeus.common.framework.version.sequence.associate.SequenceAssociate;
import org.kuali.kra.institutionalproposal.InstitutionalProposalAssociate;

public class InstitutionalProposalCfda extends InstitutionalProposalAssociate implements SequenceAssociate<InstitutionalProposal> {
    private Long proposalCfdaId;

    private Long proposalId;

    private String cfdaNumber;

    private String cfdaDescription;

    public Long getProposalCfdaId() {
        return proposalCfdaId;
    }

    public void setProposalCfdaId(Long proposalCfdaId) {
        this.proposalCfdaId = proposalCfdaId;
    }

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public String getCfdaNumber() {
        return cfdaNumber;
    }

    public void setCfdaNumber(String cfdaNumber) {
        this.cfdaNumber = cfdaNumber;
    }

    public String getCfdaDescription() {
        return cfdaDescription;
    }

    public void setCfdaDescription(String cfdaDescription) {
        this.cfdaDescription = cfdaDescription;
    }

    @Override
    public void setSequenceOwner(InstitutionalProposal newlyVersionedOwner) {
        setInstitutionalProposal(newlyVersionedOwner);
    }

    @Override
    public InstitutionalProposal getSequenceOwner() {
        return getInstitutionalProposal();
    }

    @Override
    public void resetPersistenceState() {
        proposalCfdaId = null;
    }
}
