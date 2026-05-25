package com.eforsch.dto;

import java.time.Instant;

public class NoteVersionDetailDTO {

    private String noteId;
    private int version;
    private String experimentTitle;
    private NoteContentDTO content;
    private String editedBy;
    private Instant editedAt;

    public NoteVersionDetailDTO() {}

    public NoteVersionDetailDTO(String noteId, int version, String experimentTitle,
            NoteContentDTO content, String editedBy, Instant editedAt) {
        this.noteId = noteId;
        this.version = version;
        this.experimentTitle = experimentTitle;
        this.content = content;
        this.editedBy = editedBy;
        this.editedAt = editedAt;
    }

    public String getNoteId() { return noteId; }
    public void setNoteId(String noteId) { this.noteId = noteId; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public String getExperimentTitle() { return experimentTitle; }
    public void setExperimentTitle(String experimentTitle) { this.experimentTitle = experimentTitle; }

    public NoteContentDTO getContent() { return content; }
    public void setContent(NoteContentDTO content) { this.content = content; }

    public String getEditedBy() { return editedBy; }
    public void setEditedBy(String editedBy) { this.editedBy = editedBy; }

    public Instant getEditedAt() { return editedAt; }
    public void setEditedAt(Instant editedAt) { this.editedAt = editedAt; }
}
