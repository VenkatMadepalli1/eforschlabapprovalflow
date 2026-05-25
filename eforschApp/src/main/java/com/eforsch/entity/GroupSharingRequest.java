package com.eforsch.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "groupsharingrequests", schema = "eforsch")
public class GroupSharingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    private String productId;  // Product ID like "chem_456"
    
    private String itemId;
    
    private String inventoryType; // "generalInventory" or "fineChemicalInventory"
    
    private String exchangeAddress;
    
    // Address fields
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    
    // Time slot fields (only one slot needed)
    private String slotDate;
    private String slotTime;
    
    private Integer quantity;
    
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED
    
    private Long userId;
    
    private String userEmail;
    
    private String userName;
    
    private String requesterGroup;  // The group/lab of the requester
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    private String selectedProposalDate;
    
    private String selectedProposalTime;
    
    // Fields for marking as received
    private LocalDateTime receivedAt;
    private String receivedNotes;
    
    // Field for rejection reason
    private String rejectionReason;
    
    @OneToMany(mappedBy = "sharingRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupSharingRequestProposal> proposals;

    // Getters and Setters
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public String getExchangeAddress() {
        return exchangeAddress;
    }

    public void setExchangeAddress(String exchangeAddress) {
        this.exchangeAddress = exchangeAddress;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(String slotDate) {
        this.slotDate = slotDate;
    }

    public String getSlotTime() {
        return slotTime;
    }

    public void setSlotTime(String slotTime) {
        this.slotTime = slotTime;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRequesterGroup() {
        return requesterGroup;
    }

    public void setRequesterGroup(String requesterGroup) {
        this.requesterGroup = requesterGroup;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSelectedProposalDate() {
        return selectedProposalDate;
    }

    public void setSelectedProposalDate(String selectedProposalDate) {
        this.selectedProposalDate = selectedProposalDate;
    }

    public String getSelectedProposalTime() {
        return selectedProposalTime;
    }

    public void setSelectedProposalTime(String selectedProposalTime) {
        this.selectedProposalTime = selectedProposalTime;
    }

    public List<GroupSharingRequestProposal> getProposals() {
        return proposals;
    }

    public void setProposals(List<GroupSharingRequestProposal> proposals) {
        this.proposals = proposals;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getReceivedNotes() {
        return receivedNotes;
    }

    public void setReceivedNotes(String receivedNotes) {
        this.receivedNotes = receivedNotes;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
