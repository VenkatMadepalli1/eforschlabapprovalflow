package com.eforsch.dto;

import java.time.LocalDateTime;

public class TimeSlotDTO {

    private Long timeSlotId;
    private Integer slotNumber;
    private String day;
    private String fromTime;
    private String toTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    // Legacy fields for backward compatibility
    private String date;
    private String time;

    // Constructors
    public TimeSlotDTO() {
    }

    public TimeSlotDTO(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TimeSlotDTO(Long timeSlotId, LocalDateTime startTime, LocalDateTime endTime) {
        this.timeSlotId = timeSlotId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public TimeSlotDTO(String date, String time) {
        this.date = date;
        this.time = time;
    }
    
    public TimeSlotDTO(Integer slotNumber, String day, String fromTime, String toTime) {
        this.slotNumber = slotNumber;
        this.day = day;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }
    
    public TimeSlotDTO(Long timeSlotId, Integer slotNumber, String day, String fromTime, String toTime, 
                       LocalDateTime startTime, LocalDateTime endTime) {
        this.timeSlotId = timeSlotId;
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
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
}
