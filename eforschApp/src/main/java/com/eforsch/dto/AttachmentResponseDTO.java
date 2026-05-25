package com.eforsch.dto;

import java.time.Instant;

public class AttachmentResponseDTO {

	private String attachmentId;
	private String fileName;
	private String fileType;
	private UploadedByDTO uploadedBy;
	private Instant uploadedAt;
	private String downloadUrl;

	public AttachmentResponseDTO() {}

	public AttachmentResponseDTO(String attachmentId, String fileName, String fileType,
			UploadedByDTO uploadedBy, Instant uploadedAt, String downloadUrl) {
		this.attachmentId = attachmentId;
		this.fileName = fileName;
		this.fileType = fileType;
		this.uploadedBy = uploadedBy;
		this.uploadedAt = uploadedAt;
		this.downloadUrl = downloadUrl;
	}

	public String getAttachmentId() { return attachmentId; }
	public void setAttachmentId(String attachmentId) { this.attachmentId = attachmentId; }

	public String getFileName() { return fileName; }
	public void setFileName(String fileName) { this.fileName = fileName; }

	public String getFileType() { return fileType; }
	public void setFileType(String fileType) { this.fileType = fileType; }

	public UploadedByDTO getUploadedBy() { return uploadedBy; }
	public void setUploadedBy(UploadedByDTO uploadedBy) { this.uploadedBy = uploadedBy; }

	public Instant getUploadedAt() { return uploadedAt; }
	public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }

	public String getDownloadUrl() { return downloadUrl; }
	public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
}
