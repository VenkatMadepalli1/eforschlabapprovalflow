package com.eforsch.dto;

import java.util.List;

public class ProjectRequest {

    private String projectName;
    private String longDescription;
    private List<String> budgetNos;
    private String groupName;
    private String role;
    private String name;
    private String userId;

    // Getters & Setters
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public List<String> getBudgetNos() {
        return budgetNos;
    }

    public void setBudgetNos(List<String> budgetNos) {
        this.budgetNos = budgetNos;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
