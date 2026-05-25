package com.eforsch.dto;


public class ApproveShareRequestDTO {
    private String requestId;
    private User approvedBy;
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public User getApprovedBy() {
		return approvedBy;
	}
	public void setApprovedBy(User approvedBy) {
		this.approvedBy = approvedBy;
	}
    
    
    
    
    
}
