package com.eforsch.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory_time_slot", schema = "eforsch")
public class InventoryTimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timeSlotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_inventory_id", nullable = false)
    private SharedInventory sharedInventory;

    @Column(name = "slot_number")
    private Integer slotNumber;

    @Column(name = "day", length = 20)
    private String day;

    @Column(name = "from_time", length = 20)
    private String fromTime;

    @Column(name = "to_time", length = 20)
    private String toTime;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Constructors
    public InventoryTimeSlot() {
    }

    public InventoryTimeSlot(SharedInventory sharedInventory, LocalDateTime startTime, LocalDateTime endTime) {
        this.sharedInventory = sharedInventory;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public InventoryTimeSlot(SharedInventory sharedInventory, Integer slotNumber, String day, 
                            String fromTime, String toTime, LocalDateTime startTime, LocalDateTime endTime) {
        this.sharedInventory = sharedInventory;
        this.slotNumber = slotNumber;
        this.day = day;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public Long getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(Long timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public SharedInventory getSharedInventory() {
        return sharedInventory;
    }

    public void setSharedInventory(SharedInventory sharedInventory) {
        this.sharedInventory = sharedInventory;
    }

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
