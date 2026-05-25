package com.eforsch.entity;

import com.eforsch.util.StringListConverter;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "notebook_notes")
public class NoteBook {

	@Id
	@Column(name = "note_id", nullable = false, length = 50)
	private String noteId;

	@Column(name = "project_id", nullable = false, length = 50)
	private String projectId;

	@Column(name = "experiment_title", nullable = false, length = 255)
	private String experimentTitle;

	// ✅ Stored as comma-separated in DB via converter
	@Column(name = "budget_ids", columnDefinition = "TEXT")
	@Convert(converter = StringListConverter.class)
	private List<String> budgetIds;

	@Column(name = "note_date")
	private LocalDate noteDate;

	@Lob
	@Column(name = "content_html")
	private String contentHtml;

	@Lob
	@Column(name = "content_plain_text")
	private String contentPlainText;

	@Column(name = "group_name", length = 100)
	private String groupName;

	@Column(name = "role", length = 50)
	private String role;

	@Column(name = "created_by", length = 150)
	private String createdBy;

	@Column(name = "created_by_user_id", length = 50)
	private String createdByUserId;

	@Column(name = "created_at")
	private Instant createdAt;

	@Column(name = "autosaved_at")
	private Instant autosavedAt;

	@PrePersist
	public void prePersist() {
		if (createdAt == null)
			createdAt = Instant.now();
	}

	// Getters/Setters
	public String getNoteId() {
		return noteId;
	}

	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getExperimentTitle() {
		return experimentTitle;
	}

	public void setExperimentTitle(String experimentTitle) {
		this.experimentTitle = experimentTitle;
	}

	public List<String> getBudgetIds() {
		return budgetIds;
	}

	public void setBudgetIds(List<String> budgetIds) {
		this.budgetIds = budgetIds;
	}

	public LocalDate getNoteDate() {
		return noteDate;
	}

	public void setNoteDate(LocalDate noteDate) {
		this.noteDate = noteDate;
	}

	public String getContentHtml() {
		return contentHtml;
	}

	public void setContentHtml(String contentHtml) {
		this.contentHtml = contentHtml;
	}

	public String getContentPlainText() {
		return contentPlainText;
	}

	public void setContentPlainText(String contentPlainText) {
		this.contentPlainText = contentPlainText;
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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedByUserId() {
		return createdByUserId;
	}

	public void setCreatedByUserId(String createdByUserId) {
		this.createdByUserId = createdByUserId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getAutosavedAt() {
		return autosavedAt;
	}

	public void setAutosavedAt(Instant autosavedAt) {
		this.autosavedAt = autosavedAt;
	}
}
