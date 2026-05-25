package com.eforsch.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "notebook_archive_attachment")
public class NoteBookArchiveAttachment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "attachment_id")
	private Long attachmentId;

	@Column(name = "note_id", nullable = false, length = 50)
	private String noteId;

	@Column(name = "file_name", nullable = false)
	private String fileName;

	@Column(name = "content_type")
	private String contentType;

	@Column(name = "file_size")
	private Long fileSize;

	@Lob
	@Column(name = "file_data", columnDefinition = "LONGBLOB")
	private byte[] fileData;

	@Column(name = "uploaded_by_user_id")
	private String uploadedByUserId;

	@Column(name = "uploaded_by_name")
	private String uploadedByName;

	@Column(name = "uploaded_at")
	private Instant uploadedAt;

	@Column(name = "archived_at")
	private Instant archivedAt;

	@PrePersist
	public void prePersist() {
		if (archivedAt == null) {
			archivedAt = Instant.now();
		}
	}

	public Long getAttachmentId() { return attachmentId; }
	public void setAttachmentId(Long attachmentId) { this.attachmentId = attachmentId; }

	public String getNoteId() { return noteId; }
	public void setNoteId(String noteId) { this.noteId = noteId; }

	public String getFileName() { return fileName; }
	public void setFileName(String fileName) { this.fileName = fileName; }

	public String getContentType() { return contentType; }
	public void setContentType(String contentType) { this.contentType = contentType; }

	public Long getFileSize() { return fileSize; }
	public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

	public byte[] getFileData() { return fileData; }
	public void setFileData(byte[] fileData) { this.fileData = fileData; }

	public String getUploadedByUserId() { return uploadedByUserId; }
	public void setUploadedByUserId(String uploadedByUserId) { this.uploadedByUserId = uploadedByUserId; }

	public String getUploadedByName() { return uploadedByName; }
	public void setUploadedByName(String uploadedByName) { this.uploadedByName = uploadedByName; }

	public Instant getUploadedAt() { return uploadedAt; }
	public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }

	public Instant getArchivedAt() { return archivedAt; }
	public void setArchivedAt(Instant archivedAt) { this.archivedAt = archivedAt; }
}