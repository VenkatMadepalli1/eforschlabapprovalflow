package com.eforsch.dto;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

public class ProjectWithAttachmentsRequest {
    
    @Valid
    private ProjectRequest project;
    
    @Valid
    private User userDetails;
    
    private MultipartFile[] attachments;

    // Constructors
    public ProjectWithAttachmentsRequest() {}

    public ProjectWithAttachmentsRequest(ProjectRequest project, User userDetails, MultipartFile[] attachments) {
        this.project = project;
        this.userDetails = userDetails;
        this.attachments = attachments;
    }

    // Getters & Setters
    public ProjectRequest getProject() {
        return project;
    }

    public void setProject(ProjectRequest project) {
        this.project = project;
    }

    public User getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(User userDetails) {
        this.userDetails = userDetails;
    }

    public MultipartFile[] getAttachments() {
        return attachments;
    }

    public void setAttachments(MultipartFile[] attachments) {
        this.attachments = attachments;
    }
}
