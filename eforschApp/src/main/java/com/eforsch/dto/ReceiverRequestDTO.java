package com.eforsch.dto;

import java.time.LocalDateTime;

public class ReceiverRequestDTO {
    
    private String requestId;
    private String productName;
    private Integer requestedQuantity;
    private String donorGroup;
    private String status;
    private SelectedSlotDTO selectedSlot;
    private AddressSimpleDTO address;
    private LocalDateTime createdAt;
    private String rejectionReason;
    
    public ReceiverRequestDTO() {
    }
    
    public ReceiverRequestDTO(String requestId, String productName, Integer requestedQuantity, 
                             String donorGroup, String status, SelectedSlotDTO selectedSlot, 
                             AddressSimpleDTO address, LocalDateTime createdAt) {
        this.requestId = requestId;
        this.productName = productName;
        this.requestedQuantity = requestedQuantity;
        this.donorGroup = donorGroup;
        this.status = status;
        this.selectedSlot = selectedSlot;
        this.address = address;
        this.createdAt = createdAt;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }
    
    public void setRequestedQuantity(Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
