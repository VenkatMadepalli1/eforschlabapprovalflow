package com.eforsch;

import com.eforsch.entity.UserDetails;

public class GroupLeaderApprovalRequest {
    private Long approverId;
    private UserDetails user;
    private boolean approve;

    // Getters and setters
	public Long getApproverId() {
		return approverId;
	}

	public void setApproverId(Long approverId) {
		this.approverId = approverId;
	}

	public UserDetails getUser() {
		return user;
	}

	public void setUser(UserDetails user) {
		this.user = user;
	}

	public boolean isApprove() {
		return approve;
	}

	public void setApprove(boolean approve) {
		this.approve = approve;
	}

	// Constructors
	

	
    

}
