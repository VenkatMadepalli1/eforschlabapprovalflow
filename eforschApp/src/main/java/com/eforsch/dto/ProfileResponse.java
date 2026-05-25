package com.eforsch.dto;

import java.time.LocalDateTime;

public class ProfileResponse {
    private String userId;
    private String title;
    private String firstName;
    private String secondName;
    private String email;
    private String labName;
    private String groupLeader;
    private String roomNumber;
    private String addressLine1;
    private String addressLine2;
    private String buildingNumber;
    private String streetName;
    private String city;
    private String role;
    private String groupName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ProfileResponse() {
    }

    public ProfileResponse(String userId, String title, String firstName, String secondName, String email,
                          String labName, String groupLeader, String roomNumber, String addressLine1,
                          String addressLine2, String buildingNumber, String streetName, String city,
                          String role, String groupName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.title = title;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.labName = labName;
        this.groupLeader = groupLeader;
        this.roomNumber = roomNumber;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.buildingNumber = buildingNumber;
        this.streetName = streetName;
        this.city = city;
        this.role = role;
        this.groupName = groupName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public String getGroupLeader() {
        return groupLeader;
    }

    public void setGroupLeader(String groupLeader) {
        this.groupLeader = groupLeader;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
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

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
}
