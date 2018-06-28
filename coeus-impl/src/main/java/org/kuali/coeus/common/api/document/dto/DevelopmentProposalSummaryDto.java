package org.kuali.coeus.common.api.document.dto;

import java.util.Set;

public class DevelopmentProposalSummaryDto {

    private String proposalNumber;
    private String piName;
    private String unitNumber;
    private String sponsorName;
    private String title;
    private Long lastActionTaken;
    private String primaryApprover;
    private Integer stopNumber;
    private Set<String> allApprovers;

    public Set<String> getAllApprovers() {
        return allApprovers;
    }

    public void setAllApprovers(Set<String> allApprovers) {
        this.allApprovers = allApprovers;
    }

    public Integer getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(Integer stopNumber) {
        this.stopNumber = stopNumber;
    }

    public String getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public String getPiName() {
        return piName;
    }

    public void setPiName(String piName) {
        this.piName = piName;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getLastActionTaken() {
        return lastActionTaken;
    }

    public void setLastActionTaken(Long lastActionTaken) {
        this.lastActionTaken = lastActionTaken;
    }

    public String getPrimaryApprover() {
        return primaryApprover;
    }

    public void setPrimaryApprover(String primaryApprover) {
        this.primaryApprover = primaryApprover;
    }
}
