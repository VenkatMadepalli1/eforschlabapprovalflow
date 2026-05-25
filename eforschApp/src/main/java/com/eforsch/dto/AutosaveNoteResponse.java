package com.eforsch.dto;

import java.time.Instant;

public class AutosaveNoteResponse {
    private String noteId;
    private Instant autosavedAt;

    public AutosaveNoteResponse() {}

    public AutosaveNoteResponse(String noteId, Instant autosavedAt) {
        this.noteId = noteId;
        this.autosavedAt = autosavedAt;
    }

    public String getNoteId() { return noteId; }
    public void setNoteId(String noteId) { this.noteId = noteId; }

    public Instant getAutosavedAt() { return autosavedAt; }
    public void setAutosavedAt(Instant autosavedAt) { this.autosavedAt = autosavedAt; }
}
