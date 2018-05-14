/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.propdev.impl.s2s.override;

import org.kuali.coeus.propdev.api.s2s.override.S2sOverrideContract;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

import javax.persistence.*;

@Entity
@Table(name = "S2S_OVERRIDE")
public class S2sOverride extends KcPersistableBusinessObjectBase implements S2sOverrideContract, org.kuali.rice.core.api.mo.common.Identifiable, MutableInactivatable {

    @PortableSequenceGenerator(name = "SEQ_S2S_OVERRIDE_ID")
    @GeneratedValue(generator = "SEQ_S2S_OVERRIDE_ID")
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ACTIVE")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

    @OneToOne(cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "PROPOSAL_NUMBER")
    private DevelopmentProposal developmentProposal;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL )
    @JoinColumn(name = "S2S_APPL_DATA_ID", referencedColumnName = "ID")
    private S2sOverrideApplicationData application;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL )
    @JoinColumn(name = "S2S_APPL_DATA_OVERRIDE_ID", referencedColumnName = "ID")
    private S2sOverrideApplicationData applicationOverride;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DevelopmentProposal getDevelopmentProposal() {
        return developmentProposal;
    }

    public void setDevelopmentProposal(DevelopmentProposal developmentProposal) {
        this.developmentProposal = developmentProposal;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public S2sOverrideApplicationData getApplication() {
        return application;
    }

    public void setApplication(S2sOverrideApplicationData application) {
        this.application = application;
    }

    @Override
    public S2sOverrideApplicationData getApplicationOverride() {
        return applicationOverride;
    }

    public void setApplicationOverride(S2sOverrideApplicationData applicationOverride) {
        this.applicationOverride = applicationOverride;
    }
}
