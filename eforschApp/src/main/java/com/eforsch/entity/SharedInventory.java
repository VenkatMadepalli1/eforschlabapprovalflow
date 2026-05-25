package com.eforsch.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "sharedinventory", schema = "eforsch", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"productId", "inventoryType"}, name = "uk_sharedinventory_productid_type")
})
public class SharedInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sharedInventoryId;

    @Column(nullable = false)
    private String productId; // Product identifier like "chem_456"

    @Column(nullable = false)
    private String inventoryType; // "generalInventory" or "fineChemicalInventory"

    private Long sharedByUserId; // FK to UserDetails.userId

    private String sharedByUserEmail;

    private String sharedByUserName;

    private String sharedByUserRole;

    private String sharedByGroupName;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalSharedQuantity; // Total quantity being shared

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer availableQuantity; // Remaining quantity available

    @CreationTimestamp
    private LocalDateTime sharedAt;

    private String addressLine1;
    private String addressLine2;
    private String addressCity;
    private String addressState;
    private String addressPostalCode;
    private String addressCountry;

    @OneToMany(mappedBy = "sharedInventory", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<InventoryTimeSlot> timeSlots;

    // Getters and Setters
    public Long getSharedInventoryId() {
        return sharedInventoryId;
    }

    public void setSharedInventoryId(Long sharedInventoryId) {
        this.sharedInventoryId = sharedInventoryId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public Long getSharedByUserId() {
        return sharedByUserId;
    }

    public void setSharedByUserId(Long sharedByUserId) {
        this.sharedByUserId = sharedByUserId;
    }

    public String getSharedByUserEmail() {
        return sharedByUserEmail;
    }

    public void setSharedByUserEmail(String sharedByUserEmail) {
        this.sharedByUserEmail = sharedByUserEmail;
    }

    public String getSharedByUserName() {
        return sharedByUserName;
    }

    public void setSharedByUserName(String sharedByUserName) {
        this.sharedByUserName = sharedByUserName;
    }

    public String getSharedByUserRole() {
        return sharedByUserRole;
    }

    public void setSharedByUserRole(String sharedByUserRole) {
        this.sharedByUserRole = sharedByUserRole;
    }

    public String getSharedByGroupName() {
        return sharedByGroupName;
    }

    public void setSharedByGroupName(String sharedByGroupName) {
        this.sharedByGroupName = sharedByGroupName;
    }

    public Integer getTotalSharedQuantity() {
        return totalSharedQuantity;
    }

    public void setTotalSharedQuantity(Integer totalSharedQuantity) {
        this.totalSharedQuantity = totalSharedQuantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }

    public List<InventoryTimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<InventoryTimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
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

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public String getAddressPostalCode() {
        return addressPostalCode;
    }

    public void setAddressPostalCode(String addressPostalCode) {
        this.addressPostalCode = addressPostalCode;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }
}
