package com.eforsch.dto;

import jakarta.validation.Valid;

public class ProjectCreateRequest {
    
    @Valid
    private ProjectRequest request;
    
    @Valid
    private User userDetails;

    // Constructors
    public ProjectCreateRequest() {}

    public ProjectCreateRequest(ProjectRequest request, User userDetails) {
        this.request = request;
        this.userDetails = userDetails;
    }

    // Getters & Setters
    public ProjectRequest getRequest() {
        return request;
    }

    public void setRequest(ProjectRequest request) {
        this.request = request;
    }

    public User getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(User userDetails) {
        this.userDetails = userDetails;
    }
}
