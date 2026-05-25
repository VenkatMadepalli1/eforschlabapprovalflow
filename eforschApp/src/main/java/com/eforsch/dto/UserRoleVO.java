package com.eforsch.dto;

public class UserRoleVO {

	private int roleid;
	private String role;
	private String rolename;
	private String description;	
	
	
	// setters and getters
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public int getRoleid() {
		return roleid;
	}

	public void setRoleid(int roleid) {
		this.roleid = roleid;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    // Constructors
	
	public UserRoleVO() {
		// Default constructor
	}
	
}
