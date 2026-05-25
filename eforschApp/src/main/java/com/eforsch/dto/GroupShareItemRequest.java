package com.eforsch.dto;

public class GroupShareItemRequest {

    private String itemId;
    private Integer requestedQuantity;
    private String requesterId;
    private String requesterRole;

    public GroupShareItemRequest() {
    }

    public GroupShareItemRequest(String itemId, Integer requestedQuantity,
                                  String requesterId, String requesterRole) {
        this.itemId = itemId;
        this.requestedQuantity = requestedQuantity;
        this.requesterId = requesterId;
        this.requesterRole = requesterRole;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterRole() {
        return requesterRole;
    }

    public void setRequesterRole(String requesterRole) {
        this.requesterRole = requesterRole;
    }
}