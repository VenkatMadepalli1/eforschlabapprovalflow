package com.eforsch;

public class UserDetailsResponse {

	 private Long userId;
     private String firstname;
     private String lastname;
     private String email;
     private String status;
     private String role;
     private String groupName;
     private Boolean action;
     
	public UserDetailsResponse(Long userId2, String firstname2, String lastname2, String email2, Object object,
			String groupName2, String status2, boolean action) {
		this.userId = userId2;
			this.firstname = firstname2;
			this.lastname = lastname2;
			this.email = email2;
			this.role = (String) object;
			this.groupName = groupName2;
			this.status = status2;
			this.action = action;
				
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Boolean getAction() {
		return action;
	}
	public void setAction(Boolean action) {
		this.action = action;
	}
	 
}
