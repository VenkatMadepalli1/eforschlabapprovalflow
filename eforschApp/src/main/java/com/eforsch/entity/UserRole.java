package com.eforsch.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "userrole", schema = "eforsch")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleid;

    // Role name column
    @Column(name = "role", nullable = false, length = 45)
    private String role;
 
 // Role name column
    @Column(name = "description", nullable = false, length = 1000)
    private String description;
 
    
    @Column(name = "rolename", nullable = false, length = 45)
    private String rolename;

    // Constructors
    public UserRole() {}

    public UserRole(String rolename) {
        this.rolename = rolename;
    }

    // Getters and Setters
    public Integer getRoleid() {
        return roleid;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }
    public String getRole() {
	return role;
    }

	public void setRole(String role) {
		this.role = role;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
		
}
