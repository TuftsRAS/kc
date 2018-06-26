/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.institutionalproposal.customdata;

import org.kuali.coeus.common.framework.custom.DocumentCustomData;
import org.kuali.coeus.common.framework.custom.attr.CustomAttribute;
import org.kuali.coeus.common.framework.version.sequence.associate.SequenceAssociate;
import org.kuali.coeus.sys.api.model.IdentifiableNumeric;
import org.kuali.kra.institutionalproposal.InstitutionalProposalAssociate;
import org.kuali.kra.institutionalproposal.home.InstitutionalProposal;

/**
 * This is Business Object class for IP custom data BO.
 */
public class InstitutionalProposalCustomData extends InstitutionalProposalAssociate implements SequenceAssociate<InstitutionalProposal>, DocumentCustomData, IdentifiableNumeric {

    private static final long serialVersionUID = 1L;

    private Long proposalCustomDataId;

    private Long customAttributeId;

    private String value;

    private CustomAttribute customAttribute;


    public InstitutionalProposalCustomData() {
    }


    public Long getProposalCustomDataId() {
        return proposalCustomDataId;
    }


    public void setProposalCustomDataId(Long proposalCustomDataId) {
        this.proposalCustomDataId = proposalCustomDataId;
    }


    @Override
    public Long getCustomAttributeId() {
        return customAttributeId;
    }


    @Override
    public void setCustomAttributeId(Long customAttributeId) {
        this.customAttributeId = customAttributeId;
    }


    @Override
    public String getValue() {
        return value;
    }


    @Override
    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public CustomAttribute getCustomAttribute() {
        return customAttribute;
    }


    @Override
    public void setCustomAttribute(CustomAttribute customAttribute) {
        this.customAttribute = customAttribute;
    }

    @Override
    public InstitutionalProposal getSequenceOwner() {
        return getInstitutionalProposal();
    }

    @Override
    public void setSequenceOwner(InstitutionalProposal newlyVersionedOwner) {
        setInstitutionalProposal(newlyVersionedOwner);
    }

    @Override
    public void resetPersistenceState() {
        this.proposalCustomDataId = null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customAttribute == null) ? 0 : customAttribute.hashCode());
        result = prime * result + ((customAttributeId == null) ? 0 : customAttributeId.hashCode());
        result = prime * result + ((proposalCustomDataId == null) ? 0 : proposalCustomDataId.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        InstitutionalProposalCustomData other = (InstitutionalProposalCustomData) obj;
        if (customAttribute == null) {
            if (other.customAttribute != null) return false;
        } else if (!customAttribute.equals(other.customAttribute)) return false;
        if (customAttributeId == null) {
            if (other.customAttributeId != null) return false;
        } else if (!customAttributeId.equals(other.customAttributeId)) return false;
        if (proposalCustomDataId == null) {
            if (other.proposalCustomDataId != null) return false;
        } else if (!proposalCustomDataId.equals(other.proposalCustomDataId)) return false;
        if (value == null) {
            if (other.value != null) return false;
        } else if (!value.equals(other.value)) return false;
        return true;
    }
    
	@Override
	public Long getId() {
		return customAttributeId;
	}

}
