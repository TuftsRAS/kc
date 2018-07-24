/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringEscapeUtils;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.api.s2s.S2sOpportunityContract;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

import javax.persistence.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.kuali.coeus.sys.framework.util.CollectionUtils.entry;

@Entity
@Table(name = "S2S_OPPORTUNITY")
public class S2sOpportunity extends KcPersistableBusinessObjectBase implements S2sOpportunityContract {

	@Id
	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PROPOSAL_NUMBER", referencedColumnName = "PROPOSAL_NUMBER", insertable = true, updatable = true)
	private DevelopmentProposal developmentProposal;

    @Column(name = "CLOSING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar closingDate;

    @Column(name = "COMPETETION_ID")
    private String competitionId;

    @Column(name = "COMPETITION_TITLE")
    private String competitionTitle;

    @Column(name = "PACKAGE_ID")
    private String packageId;

    @Column(name = "INSTRUCTION_URL")
    private String instructionUrl;

    @Column(name = "OPENING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar openingDate;

    @Column(name = "OPPORTUNITY")
    @Lob
    private String opportunity;

    @Column(name = "OPPORTUNITY_ID")
    private String opportunityId;

    @Column(name = "OPPORTUNITY_TITLE")
    private String opportunityTitle;

    @Column(name = "REVISION_CODE")
    private String revisionCode;

    @Column(name = "REVISION_OTHER_DESCRIPTION")
    private String revisionOtherDescription;

    @Column(name = "S2S_SUBMISSION_TYPE_CODE")
    private String s2sSubmissionTypeCode;

    @Column(name = "SCHEMA_URL")
    private String schemaUrl;

    @Column(name = "OFFERING_AGENCY")
    private String offeringAgency;

    @Column(name = "AGENCY_CONTACT_INFO")
    private String agencyContactInfo;

    @Column(name = "MULTI_PROJECT")
    @Convert(converter = BooleanYNConverter.class)
    private boolean multiProject;

    @Column(name = "PROVIDER")
    private String providerCode;

    @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL })
    @JoinColumn(name = "PROPOSAL_NUMBER", referencedColumnName = "PROPOSAL_NUMBER")
    private List<S2sOpportunityCfda> s2sOpportunityCfdas = new ArrayList<>();

    @OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL })
    @JoinColumn(name = "PROPOSAL_NUMBER", referencedColumnName = "PROPOSAL_NUMBER")
    private List<S2sOppForms> s2sOppForms;

    @ManyToOne(cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "S2S_SUBMISSION_TYPE_CODE", referencedColumnName = "S2S_SUBMISSION_TYPE_CODE", insertable = false, updatable = false)
    private S2sSubmissionType s2sSubmissionType;

    @ManyToOne(cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "REVISION_CODE", referencedColumnName = "S2S_REVISION_TYPE_CODE", insertable = false, updatable = false)
    private S2sRevisionType s2sRevisionType;

    @ManyToOne(cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "PROVIDER", referencedColumnName = "CODE", insertable = false, updatable = false)
    private S2sProvider s2sProvider;

    /**
     * @deprecated Included here for REST api compatibility.  Do not use.
     */
    @Deprecated
    public String getCfdaNumber() {
        return findFirstCfda().map(S2sOpportunityCfda::getCfdaNumber).orElse(null);
    }

    /**
     * @deprecated Included here for REST api compatibility.  Do not use.
     */
    @Deprecated
    public void setCfdaNumber(String cfdaNumber) {
        if (s2sOpportunityCfdas == null) {
            s2sOpportunityCfdas = new ArrayList<>();
        }

        final S2sOpportunityCfda cfda = findFirstCfda().orElseGet(S2sOpportunityCfda::new);
        s2sOpportunityCfdas.clear();
        cfda.setCfdaNumber(cfdaNumber);
        if (this.getDevelopmentProposal() != null) {
            cfda.setProposalNumber(this.getProposalNumber());
        }

        s2sOpportunityCfdas.add(cfda);
    }

    /**
     * @deprecated Included here for REST api compatibility.  Do not use.
     */
    @Deprecated
    public String getCfdaDescription() {
        return findFirstCfda().map(S2sOpportunityCfda::getCfdaDescription).orElse(null);
    }

    /**
     * @deprecated Included here for REST api compatibility.  Do not use.
     */
    @Deprecated
    public void setCfdaDescription(String cfdaDescription) {
        if (s2sOpportunityCfdas == null) {
            s2sOpportunityCfdas = new ArrayList<>();
        }

        final S2sOpportunityCfda cfda = findFirstCfda().orElseGet(S2sOpportunityCfda::new);
        s2sOpportunityCfdas.clear();
        cfda.setCfdaDescription(cfdaDescription);
        if (this.getDevelopmentProposal() != null) {
            cfda.setProposalNumber(this.getProposalNumber());
        }

        s2sOpportunityCfdas.add(cfda);
    }

    /**
     * @deprecated Included here for REST api compatibility.  Do not use.
     */
    @Deprecated
    private Optional<S2sOpportunityCfda> findFirstCfda() {
        if (s2sOpportunityCfdas != null) {
            return s2sOpportunityCfdas.stream().findFirst();
        }

        return Optional.empty();
    }

    @Override
    public Calendar getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Calendar closingDate) {
        this.closingDate = closingDate;
    }

    @Override
    public String getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(String competitionId) {
        this.competitionId = competitionId;
    }

    @Override
    public String getInstructionUrl() {
        return instructionUrl;
    }

    public void setInstructionUrl(String instructionUrl) {
        this.instructionUrl = instructionUrl;
    }

    @Override
    public Calendar getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Calendar openingDate) {
        this.openingDate = openingDate;
    }

    @Override
    public String getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(String opportunity) {
        this.opportunity = opportunity;
    }

    public String getRevisionCode() {
        return revisionCode;
    }

    public void setRevisionCode(String revisionCode) {
        this.revisionCode = revisionCode;
    }

    @Override
    public String getRevisionOtherDescription() {
        return revisionOtherDescription;
    }

    public void setRevisionOtherDescription(String revisionOtherDescription) {
        this.revisionOtherDescription = revisionOtherDescription;
    }

    public String getS2sSubmissionTypeCode() {
        return s2sSubmissionTypeCode;
    }

    public void setS2sSubmissionTypeCode(String s2sSubmissionTypeCode) {
        this.s2sSubmissionTypeCode = s2sSubmissionTypeCode;
    }

    @Override
    public String getSchemaUrl() {
        return schemaUrl;
    }

    public void setSchemaUrl(String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }

    @Override
    public List<S2sOppForms> getS2sOppForms() {
        return s2sOppForms;
    }

    public void setS2sOppForms(List<S2sOppForms> oppForms) {
        s2sOppForms = oppForms;
    }

    @Override
    public S2sRevisionType getS2sRevisionType() {
        return s2sRevisionType;
    }

    public void setS2sRevisionType(S2sRevisionType revisionType) {
        s2sRevisionType = revisionType;
    }

    @Override
    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(String opportunityId) {
        this.opportunityId = opportunityId;
    }

    @Override
    public String getOpportunityTitle() {
        return opportunityTitle;
    }

    public void setOpportunityTitle(String opportunityTitle) {
        this.opportunityTitle = opportunityTitle;
    }

    @Override
    public S2sSubmissionType getS2sSubmissionType() {
        return s2sSubmissionType;
    }

    public void setS2sSubmissionType(S2sSubmissionType submissionType) {
        s2sSubmissionType = submissionType;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    @Override
    public S2sProvider getS2sProvider() {
        return s2sProvider;
    }

    public void setS2sProvider(S2sProvider s2sProvider) {
        this.s2sProvider = s2sProvider;
    }

    @Override
    public String getFundingOpportunityNumber() {
        return opportunityId;
    }

    public void setFundingOpportunityNumber(String fundingOpportunityNumber) {
        this.opportunityId = fundingOpportunityNumber;
    }

    @Override
    public String getOfferingAgency() {
        return offeringAgency;
    }

    public void setOfferingAgency(String offeringAgency) {
        this.offeringAgency = offeringAgency;
    }

    @Override
    public String getAgencyContactInfo() {
        return agencyContactInfo;
    }

    public void setAgencyContactInfo(String agencyContactInfo) {
        this.agencyContactInfo = agencyContactInfo;
    }

    @Override
    public boolean isMultiProject() {
        return multiProject;
    }

    public void setMultiProject(boolean multiProject) {
        this.multiProject = multiProject;
    }

    @Override
    public String getCompetitionTitle() {
        return competitionTitle;
    }

    public void setCompetitionTitle(String competitionTitle) {
        this.competitionTitle = competitionTitle;
    }

    @Override
    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public DevelopmentProposal getDevelopmentProposal() {
        return developmentProposal;
    }

    public void setDevelopmentProposal(DevelopmentProposal developmentProposal) {
        this.developmentProposal = developmentProposal;
    }
    
    @Override
    public String getProposalNumber() {
    	return this.getDevelopmentProposal() != null ? this.getDevelopmentProposal().getProposalNumber() : null;
    }

    @Override
    public List<S2sOpportunityCfda> getS2sOpportunityCfdas() {
        return s2sOpportunityCfdas;
    }

    public void setS2sOpportunityCfdas(List<S2sOpportunityCfda> s2sOpportunityCfdas) {
        this.s2sOpportunityCfdas = s2sOpportunityCfdas;
    }

    /**
     *
     * Used to return one-to-many cfda lookup results.
     */
    public String getS2sOpportunityCfdasSerialized() {
        try {
            return StringEscapeUtils.escapeHtml(new ObjectMapper().writeValueAsString(getS2sOpportunityCfdas()
                    .stream()
                    .map(cfda -> entry(cfda.getCfdaNumber(), cfda.getCfdaDescription()))
                    .collect(Collectors.toList())));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Used to return one-to-many cfda lookup results.
     */
    public void setS2sOpportunityCfdasSerialized(String s2sOpportunityCfdasSerialized) {
        try {
            final List<Map.Entry<String, String>> cfdas = new ObjectMapper().readValue(StringEscapeUtils.unescapeHtml(s2sOpportunityCfdasSerialized), new TypeReference<List<Map.Entry<String, String>>>() {});
            setS2sOpportunityCfdas(cfdas.stream().map(e -> {
                final S2sOpportunityCfda cfda = new S2sOpportunityCfda();
                cfda.setCfdaNumber(e.getKey());
                cfda.setCfdaDescription(e.getValue());
                return cfda;
            }).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
