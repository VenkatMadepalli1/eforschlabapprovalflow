package com.eforsch.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notebook_version")
public class NoteBookVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "note_id", nullable = false, length = 50)
    private String noteId;

    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "experiment_title", length = 255)
    private String experimentTitle;

    @Lob
    @Column(name = "content_html")
    private String contentHtml;

    @Lob
    @Column(name = "content_plain_text")
    private String contentPlainText;

    @Column(name = "edited_by", length = 150)
    private String editedBy;

    @Column(name = "edited_at")
    private Instant editedAt;

    @PrePersist
    public void prePersist() {
        if (editedAt == null) editedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNoteId() { return noteId; }
    public void setNoteId(String noteId) { this.noteId = noteId; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public String getExperimentTitle() { return experimentTitle; }
    public void setExperimentTitle(String experimentTitle) { this.experimentTitle = experimentTitle; }

    public String getContentHtml() { return contentHtml; }
    public void setContentHtml(String contentHtml) { this.contentHtml = contentHtml; }

    public String getContentPlainText() { return contentPlainText; }
    public void setContentPlainText(String contentPlainText) { this.contentPlainText = contentPlainText; }

    public String getEditedBy() { return editedBy; }
    public void setEditedBy(String editedBy) { this.editedBy = editedBy; }

    public Instant getEditedAt() { return editedAt; }
    public void setEditedAt(Instant editedAt) { this.editedAt = editedAt; }
}
