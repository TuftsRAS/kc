/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.propdev.impl.s2s.override;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.person.KcPerson;
import org.kuali.coeus.common.framework.person.KcPersonService;
import org.kuali.coeus.propdev.api.s2s.override.S2sOverrideContract;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

import javax.persistence.*;
import java.sql.Date;

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

    /*
     * Normally we store personId but in this case we are storing username.
     * This is to make things match ProposalAdminDetails.signedBy which also contains username.
     */
    @Column(name = "SIGNED_BY")
    private String signedBy;

    @Column(name = "SUBMITTED_DATE")
    private Date submittedDate;

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

    @Transient
    private transient KcPerson signedByPerson;

    @Transient
    private transient KcPersonService kcPersonService;

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
    public String getSignedBy() {
        return signedBy;
    }

    public void setSignedBy(String signedBy) {
        this.signedBy = signedBy;
    }

    @Override
    public KcPerson getSignedByPerson() {
        if (StringUtils.isNotBlank(signedBy) && (signedByPerson == null || !signedBy.equals(signedByPerson.getUserName()))) {
            setSignedByPerson(getKcPersonService().getKcPersonByUserName(signedBy));
        } else if (StringUtils.isBlank(signedBy) && signedByPerson != null) {
            setSignedByPerson(null);
        }

        return signedByPerson;
    }

    public void setSignedByPerson(KcPerson signedByPerson) {
        this.signedByPerson = signedByPerson;
    }

    @Override
    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
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

    public KcPersonService getKcPersonService() {
        if (kcPersonService == null) {
            kcPersonService = KcServiceLocator.getService(KcPersonService.class);
        }

        return kcPersonService;
    }

    public void setKcPersonService(KcPersonService kcPersonService) {
        this.kcPersonService = kcPersonService;
    }
}
