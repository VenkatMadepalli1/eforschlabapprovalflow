package com.eforsch.dto;

public class MarkAsReceivedRequest {
    private User receivedBy;
    private String receivedAt;  // ISO 8601 timestamp format
    private String notes;  // Optional notes about the received material

    public MarkAsReceivedRequest() {
    }

    public MarkAsReceivedRequest(User receivedBy, String receivedAt, String notes) {
        this.receivedBy = receivedBy;
        this.receivedAt = receivedAt;
        this.notes = notes;
    }

    public User getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(User receivedBy) {
        this.receivedBy = receivedBy;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
