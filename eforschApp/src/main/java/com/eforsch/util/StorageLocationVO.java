package com.eforsch.util;

import java.time.LocalDateTime;

public class StorageLocationVO {

    private Integer id;
    private String storageLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public StorageLocationVO() {
    }

    public StorageLocationVO(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
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
