/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.s2s;

import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.coeus.propdev.impl.core.DevelopmentProposal;
import org.kuali.coeus.propdev.api.s2s.S2sOpportunityContract;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "S2S_OPPORTUNITY")
public class S2sOpportunity extends KcPersistableBusinessObjectBase implements S2sOpportunityContract {

	@Id
	@OneToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PROPOSAL_NUMBER", referencedColumnName = "PROPOSAL_NUMBER", insertable = true, updatable = true)
	private DevelopmentProposal developmentProposal;

    @Column(name = "CFDA_NUMBER")
    private String cfdaNumber;

    @Column(name = "CLOSING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar closingDate;

    @Column(name = "COMPETETION_ID")
    private String competetionId;

    @Column(name = "INSTRUCTION_URL")
    private String instructionUrl;

    @Column(name = "OPENING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar openingDate;

    @Column(name = "OPPORTUNITY")
    @Lob
    private String opportunity;

    // opportunityId was changed to fundingOpportunityNumber in V2 
    @Column(name = "OPPORTUNITY_ID")
    private String opportunityId;

    // this is fundingOpportunityTitle in V2 
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

    @Column(name = "CFDA_DESCRIPTION")
    private String cfdaDescription;

    @Column(name = "MULTI_PROJECT")
    @Convert(converter = BooleanYNConverter.class)
    private boolean multiProject;

    @Column(name = "PROVIDER")
    private String providerCode;

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

    @Override
    @OneToOne(cascade = { CascadeType.REFRESH })
    public String getCfdaNumber() {
        return cfdaNumber;
    }

    public void setCfdaNumber(String cfdaNumber) {
        this.cfdaNumber = cfdaNumber;
    }

    @Override
    public Calendar getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Calendar closingDate) {
        this.closingDate = closingDate;
    }

    @Override
    public String getCompetetionId() {
        return competetionId;
    }

    public void setCompetetionId(String competetionId) {
        this.competetionId = competetionId;
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
    public String getCfdaDescription() {
        return cfdaDescription;
    }

    public void setCfdaDescription(String cfdaDescription) {
        this.cfdaDescription = cfdaDescription;
    }

    @Override
    public boolean isMultiProject() {
        return multiProject;
    }

    public void setMultiProject(boolean multiProject) {
        this.multiProject = multiProject;
    }

    public DevelopmentProposal getDevelopmentProposal() {
        return developmentProposal;
    }

    public void setDevelopmentProposal(DevelopmentProposal developmentProposal) {
        this.developmentProposal = developmentProposal;
    }
    
    @Override
    public String getProposalNumber() {
    	return this.getDevelopmentProposal().getProposalNumber();
    }
}
