package com.eforsch.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import com.eforsch.util.StringListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "project_archive")
public class ProjectArchive {

	@Id
	@Column(name = "project_id")
	private String projectId;

	@Column(name = "project_name")
	private String projectName;

	@Column(name = "long_description", columnDefinition = "LONGTEXT")
	private String longDescription;

	@Column(name = "budget_nos")
	@Convert(converter = StringListConverter.class)
	private List<String> budgetNos;

	@Column(name = "group_name")
	private String groupName;

	@Column(name = "role")
	private String role;

	@Column(name = "name")
	private String name;

	@Column(name = "user_id", length = 50)
	private String userId;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "archived_at")
	private Instant archivedAt;

	@PrePersist
	public void prePersist() {
		if (archivedAt == null) {
			archivedAt = Instant.now();
		}
	}

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

	public Instant getArchivedAt() {
		return archivedAt;
	}

	public void setArchivedAt(Instant archivedAt) {
		this.archivedAt = archivedAt;
	}
}