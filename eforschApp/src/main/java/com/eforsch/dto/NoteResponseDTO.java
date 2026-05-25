package com.eforsch.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class NoteResponseDTO {
    private String noteId;
    private String projectId;
    private String experimentTitle;
    private List<String> budgetIds;
    private NoteContentDTO content;
    private LocalDate noteDate;
    private Instant createdAt;
    private String createdBy;

    public NoteResponseDTO() {}

    public NoteResponseDTO(String noteId, String projectId, String experimentTitle, List<String> budgetIds,
                           NoteContentDTO content, LocalDate noteDate, Instant createdAt, String createdBy) {
        this.noteId = noteId;
        this.projectId = projectId;
        this.experimentTitle = experimentTitle;
        this.budgetIds = budgetIds;
        this.content = content;
        this.noteDate = noteDate;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    private String groupName;
    private Instant lastModifiedAt;
    private List<String> attachment;

    public NoteResponseDTO(String noteId, String projectId, String experimentTitle, List<String> budgetIds,
                           NoteContentDTO content, LocalDate noteDate, Instant createdAt, String createdBy,
                           String groupName, Instant lastModifiedAt, List<String> attachment) {
        this.noteId = noteId;
        this.projectId = projectId;
        this.experimentTitle = experimentTitle;
        this.budgetIds = budgetIds;
        this.content = content;
        this.noteDate = noteDate;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.groupName = groupName;
        this.lastModifiedAt = lastModifiedAt;
        this.attachment = attachment;
    }

    public String getNoteId() { return noteId; }
    public void setNoteId(String noteId) { this.noteId = noteId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getExperimentTitle() { return experimentTitle; }
    public void setExperimentTitle(String experimentTitle) { this.experimentTitle = experimentTitle; }

    public List<String> getBudgetIds() { return budgetIds; }
    public void setBudgetIds(List<String> budgetIds) { this.budgetIds = budgetIds; }

    public NoteContentDTO getContent() { return content; }
    public void setContent(NoteContentDTO content) { this.content = content; }

    public LocalDate getNoteDate() { return noteDate; }
    public void setNoteDate(LocalDate noteDate) { this.noteDate = noteDate; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Instant getLastModifiedAt() { return lastModifiedAt; }
    public void setLastModifiedAt(Instant lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; }

    public List<String> getAttachment() { return attachment; }
    public void setAttachment(List<String> attachment) { this.attachment = attachment; }
}
