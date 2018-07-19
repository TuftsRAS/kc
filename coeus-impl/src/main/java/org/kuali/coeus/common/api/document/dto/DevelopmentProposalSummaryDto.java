package org.kuali.coeus.common.api.document.dto;

import java.util.Set;

public class DevelopmentProposalSummaryDto {

    private String proposalNumber;
    private String piName;
    private String unitNumber;
    private String sponsorName;
    private String title;
    private Long lastActionTime;
    private String primaryApproverName;
    private Integer stopNumber;
    private Long dueDate;
    private String documentNumber;
    private Set<ApproverDto> allApprovers;
    private ApproverDto assignedApprover;

    public ApproverDto getAssignedApprover() {
        return assignedApprover;
    }

    public void setAssignedApprover(ApproverDto assignedApprover) {
        this.assignedApprover = assignedApprover;
    }

    public Set<ApproverDto> getAllApprovers() {
        return allApprovers;
    }

    public void setAllApprovers(Set<ApproverDto> allApprovers) {
        this.allApprovers = allApprovers;
    }

    public Integer getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(Integer stopNumber) {
        this.stopNumber = stopNumber;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
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

    public Long getLastActionTime() {
        return lastActionTime;
    }

    public void setLastActionTime(Long lastActionTime) {
        this.lastActionTime = lastActionTime;
    }

    public String getPrimaryApproverName() {
        return primaryApproverName;
    }

    public void setPrimaryApproverName(String primaryApproverName) {
        this.primaryApproverName = primaryApproverName;
    }
}
