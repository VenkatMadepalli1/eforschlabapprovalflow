package com.eforsch.dto;

public class ApproveShareResponseDataDTO {
    private String requestId;
    private String status;
    private Integer approvedQuantity;
    private Integer remainingQuantity;

    public ApproveShareResponseDataDTO() {
    }

    public ApproveShareResponseDataDTO(String requestId, String status, Integer approvedQuantity, Integer remainingQuantity) {
        this.requestId = requestId;
        this.status = status;
        this.approvedQuantity = approvedQuantity;
        this.remainingQuantity = remainingQuantity;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getApprovedQuantity() {
        return approvedQuantity;
    }

    public void setApprovedQuantity(Integer approvedQuantity) {
        this.approvedQuantity = approvedQuantity;
    }

    public Integer getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(Integer remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }
}

