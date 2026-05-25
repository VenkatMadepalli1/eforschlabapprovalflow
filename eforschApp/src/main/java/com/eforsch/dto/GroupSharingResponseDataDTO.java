package com.eforsch.dto;

public class GroupSharingResponseDataDTO {
    
    private String requestId;
    private String status;
    private Integer requestedQuantity;
    
    public GroupSharingResponseDataDTO() {
    }
    
    public GroupSharingResponseDataDTO(String requestId, String status, Integer requestedQuantity) {
        this.requestId = requestId;
        this.status = status;
        this.requestedQuantity = requestedQuantity;
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
    
    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }
    
    public void setRequestedQuantity(Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }
}
