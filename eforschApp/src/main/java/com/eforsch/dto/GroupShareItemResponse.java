package com.eforsch.dto;

public class GroupShareItemResponse {

    private String requestId;
    private String itemId;
    private Integer requestedQuantity;
    private String status;
    private String blockedUntil;   // ISO-8601 e.g. "2026-02-01T10:00:00Z"

    // ─── Constructors ───────────────────────────────────────
    public GroupShareItemResponse() {
    }

    public GroupShareItemResponse(String requestId, String itemId,
                                   Integer requestedQuantity, String status, String blockedUntil) {
        this.requestId = requestId;
        this.itemId = itemId;
        this.requestedQuantity = requestedQuantity;
        this.status = status;
        this.blockedUntil = blockedUntil;
    }

    // ─── Getters & Setters ──────────────────────────────────
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public Integer getRequestedQuantity() { return requestedQuantity; }
    public void setRequestedQuantity(Integer requestedQuantity) { this.requestedQuantity = requestedQuantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBlockedUntil() { return blockedUntil; }
    public void setBlockedUntil(String blockedUntil) { this.blockedUntil = blockedUntil; }
}