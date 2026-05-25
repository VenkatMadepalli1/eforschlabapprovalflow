package com.eforsch.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "groupsharingrequest_proposals", schema = "eforsch")
public class GroupSharingRequestProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proposalId;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private GroupSharingRequest sharingRequest;

    private String proposalDate;

    private String proposalTime;

    private String status; // PENDING, ACCEPTED, REJECTED

    // Getters and Setters
    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public GroupSharingRequest getSharingRequest() {
        return sharingRequest;
    }

    public void setSharingRequest(GroupSharingRequest sharingRequest) {
        this.sharingRequest = sharingRequest;
    }

    public String getProposalDate() {
        return proposalDate;
    }

    public void setProposalDate(String proposalDate) {
        this.proposalDate = proposalDate;
    }

    public String getProposalTime() {
        return proposalTime;
    }

    public void setProposalTime(String proposalTime) {
        this.proposalTime = proposalTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
