package com.eforsch.dto;

public class NoteAttachmentUploadRequest {

	private UploadedByDTO uploadedBy;
	private String description;

	public NoteAttachmentUploadRequest() {
	}

	public UploadedByDTO getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(UploadedByDTO uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
