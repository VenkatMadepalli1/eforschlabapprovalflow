package com.eforsch.dto;

import java.util.List;

public class GroupSharingRequestDTO {
    
    private String productId;  // Product ID like "chem_456"
    
    private Integer quantity;  // Requested quantity
    
    private User user;  // User requesting the share
    
    private AddressDTO address;  // Exchange address
    
    private List<TimeSlotDTO> timeSlots;  // Time slots for exchange (date and time)

    public GroupSharingRequestDTO() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public List<TimeSlotDTO> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlotDTO> timeSlots) {
        this.timeSlots = timeSlots;
    }
}

