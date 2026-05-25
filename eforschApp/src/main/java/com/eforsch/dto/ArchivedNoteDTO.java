package com.eforsch.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class ArchivedNoteDTO {

    private String noteId;
    private String experimentTitle;
    private String projectId;
    private LocalDate noteDate;
    private String createdBy;
    private String createdByUserId;
    private List<String> attachment;
    private Instant archivedAt;

    public ArchivedNoteDTO() {
    }

    public ArchivedNoteDTO(String noteId, String experimentTitle, String projectId, LocalDate noteDate,
            String createdBy, String createdByUserId, List<String> attachment, Instant archivedAt) {
        this.noteId = noteId;
        this.experimentTitle = experimentTitle;
        this.projectId = projectId;
        this.noteDate = noteDate;
        this.createdBy = createdBy;
        this.createdByUserId = createdByUserId;
        this.attachment = attachment;
        this.archivedAt = archivedAt;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getExperimentTitle() {
        return experimentTitle;
    }

    public void setExperimentTitle(String experimentTitle) {
        this.experimentTitle = experimentTitle;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public LocalDate getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(LocalDate noteDate) {
        this.noteDate = noteDate;
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

    public List<String> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<String> attachment) {
        this.attachment = attachment;
    }

    public Instant getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(Instant archivedAt) {
        this.archivedAt = archivedAt;
    }
}
