package com.eforsch.dto;


public class RejectShareRequestDTO {
    private String requestId;
    private User rejectedBy;
    private String reason;
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public User getRejectedBy() {
		return rejectedBy;
	}
	public void setRejectedBy(User rejectedBy) {
		this.rejectedBy = rejectedBy;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
    
    
    
}
