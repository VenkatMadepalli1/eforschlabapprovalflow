package com.eforsch.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.eforsch.entity.Project;

public class ProjectVO {

    private String projectId;
    private String projectName;
    private String longDescription;
    private List<String> budgetNos;
    private String groupName;
    private String role;
    private String name;
    private String userId;
    private List<String> attachment;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public ProjectVO() {
    }

    public ProjectVO(String projectId,
                     String projectName,
                     String longDescription,
                     List<String> budgetNos,
                     String groupName,
                     String role,
                     String name,
                     String userId,
                     List<String> attachment,
                     LocalDateTime createdDate,
                     LocalDateTime updatedDate) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.longDescription = longDescription;
        this.budgetNos = budgetNos;
        this.groupName = groupName;
        this.role = role;
        this.name = name;
        this.userId = userId;
        this.attachment = attachment;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public static ProjectVO fromEntity(Project project) {
        return fromEntity(project, Collections.emptyList());
    }

    public static ProjectVO fromEntity(Project project, List<String> attachment) {

        List<String> budgets = project.getBudgetNos() != null
                ? project.getBudgetNos()
                : Collections.emptyList();

        return new ProjectVO(
                project.getProjectId(),
                project.getProjectName(),
                project.getLongDescription(),
                budgets,
                project.getGroupName(),
                project.getRole(),
                project.getName(),
                project.getUserId(),
                attachment != null ? attachment : Collections.emptyList(),
                project.getCreatedDate(),
                project.getUpdatedDate()
        );
    }

    // Getters and Setters

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

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

    public List<String> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<String> attachment) {
        this.attachment = attachment;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public String toString() {
        return "ProjectVO{" +
                "projectId='" + projectId + '\'' +
                ", projectName='" + projectName + '\'' +
                ", budgetNos=" + budgetNos +
                ", attachment=" + attachment +
                '}';
    }
}
