package com.eforsch;

import java.util.List;

public class RolesApiResponse {
    private String status;
    private List<RoleResponse> roles;

    public RolesApiResponse(String status, List<RoleResponse> roles) {
        this.status = status;
        this.roles = roles;
    }

    // Getters and setters
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<RoleResponse> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleResponse> roles) {
		this.roles = roles;
	}
		
}
