package com.eforsch.dto;

public class BorrowedItemDTO {
    private String requestId;
    private Long productId;
    private String productName;
    private Integer borrowedQuantity;
    private String donorGroup;
    private String status;
    private String receivedAt;  // ISO 8601 format
    private SelectedSlotDTO selectedSlot;
    private AddressSimpleDTO address;

    public BorrowedItemDTO() {
    }

    public BorrowedItemDTO(String requestId, Long productId, String productName, Integer borrowedQuantity,
                           String donorGroup, String status, String receivedAt,
                           SelectedSlotDTO selectedSlot, AddressSimpleDTO address) {
        this.requestId = requestId;
        this.productId = productId;
        this.productName = productName;
        this.borrowedQuantity = borrowedQuantity;
        this.donorGroup = donorGroup;
        this.status = status;
        this.receivedAt = receivedAt;
        this.selectedSlot = selectedSlot;
        this.address = address;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getBorrowedQuantity() {
        return borrowedQuantity;
    }

    public void setBorrowedQuantity(Integer borrowedQuantity) {
        this.borrowedQuantity = borrowedQuantity;
    }

    public String getDonorGroup() {
        return donorGroup;
    }

    public void setDonorGroup(String donorGroup) {
        this.donorGroup = donorGroup;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public SelectedSlotDTO getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(SelectedSlotDTO selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    public AddressSimpleDTO getAddress() {
        return address;
    }

    public void setAddress(AddressSimpleDTO address) {
        this.address = address;
    }
}
