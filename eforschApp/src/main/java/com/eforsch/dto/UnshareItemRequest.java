package com.eforsch.dto;

public class UnshareItemRequest {
    private Long productId;
    private String inventoryType;  // "generalInventory" or "fineChemicalInventory"
    private User user;  // The user/group performing the unshare action
    private String reason;  // Explanation for unsharing

    public UnshareItemRequest() {
    }

    public UnshareItemRequest(Long productId, String inventoryType, User user, String reason) {
        this.productId = productId;
        this.inventoryType = inventoryType;
        this.user = user;
        this.reason = reason;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
