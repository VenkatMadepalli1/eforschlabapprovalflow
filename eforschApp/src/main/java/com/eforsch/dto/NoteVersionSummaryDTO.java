package com.eforsch.dto;

import java.time.Instant;

public class NoteVersionSummaryDTO {

    private int version;
    private String editedBy;
    private Instant editedAt;

    public NoteVersionSummaryDTO() {}

    public NoteVersionSummaryDTO(int version, String editedBy, Instant editedAt) {
        this.version = version;
        this.editedBy = editedBy;
        this.editedAt = editedAt;
    }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public String getEditedBy() { return editedBy; }
    public void setEditedBy(String editedBy) { this.editedBy = editedBy; }

    public Instant getEditedAt() { return editedAt; }
    public void setEditedAt(Instant editedAt) { this.editedAt = editedAt; }
}
