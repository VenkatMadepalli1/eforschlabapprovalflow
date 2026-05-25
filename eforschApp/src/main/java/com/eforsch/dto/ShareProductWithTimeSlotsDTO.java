package com.eforsch.dto;

import java.util.List;

public class ShareProductWithTimeSlotsDTO {

    private Long productId;
    private Integer quantity;
    private String inventoryType;
    private List<TimeSlotDTO> timeSlots;
    private User user;
    private AddressDTO address;

    // Constructors
    public ShareProductWithTimeSlotsDTO() {
    }

    public ShareProductWithTimeSlotsDTO(Long productId, String inventoryType, List<TimeSlotDTO> timeSlots) {
        this.productId = productId;
        this.inventoryType = inventoryType;
        this.timeSlots = timeSlots;
    }

    public ShareProductWithTimeSlotsDTO(Long productId, String inventoryType, List<TimeSlotDTO> timeSlots, User user) {
        this.productId = productId;
        this.inventoryType = inventoryType;
        this.timeSlots = timeSlots;
        this.user = user;
    }

    public ShareProductWithTimeSlotsDTO(Long productId, String inventoryType, List<TimeSlotDTO> timeSlots, User user, AddressDTO address) {
        this.productId = productId;
        this.inventoryType = inventoryType;
        this.timeSlots = timeSlots;
        this.user = user;
        this.address = address;
    }

    public ShareProductWithTimeSlotsDTO(Long productId, Integer quantity, String inventoryType, List<TimeSlotDTO> timeSlots, User user, AddressDTO address) {
        this.productId = productId;
        this.quantity = quantity;
        this.inventoryType = inventoryType;
        this.timeSlots = timeSlots;
        this.user = user;
        this.address = address;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public List<TimeSlotDTO> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlotDTO> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }
}
