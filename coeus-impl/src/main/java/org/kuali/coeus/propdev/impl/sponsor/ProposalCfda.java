/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.propdev.impl.sponsor;

import org.kuali.coeus.propdev.api.sponsor.ProposalCfdaContract;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.*;

@Entity
@Table(name = "EPS_PROP_CFDA")
public class ProposalCfda extends KcPersistableBusinessObjectBase implements ProposalCfdaContract {

    @PortableSequenceGenerator(name = "SEQ_EPS_PROP_CFDA_ID")
    @GeneratedValue(generator = "SEQ_EPS_PROP_CFDA_ID")
    @Id
    @Column(name = "EPS_PROP_CFDA_ID")
    private Long id;

    @Column(name = "PROPOSAL_NUMBER")
    private String proposalNumber;

    @Column(name = "CFDA_NUMBER")
    private String cfdaNumber;

    @Column(name = "CFDA_DESCRIPTION")
    private String cfdaDescription;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    @Override
    public String getCfdaNumber() {
        return cfdaNumber;
    }

    public void setCfdaNumber(String cfdaNumber) {
        this.cfdaNumber = cfdaNumber;
    }

    @Override
    public String getCfdaDescription() {
        return cfdaDescription;
    }

    public void setCfdaDescription(String cfdaDescription) {
        this.cfdaDescription = cfdaDescription;
    }
}
