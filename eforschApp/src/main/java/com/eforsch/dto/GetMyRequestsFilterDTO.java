package com.eforsch.dto;

public class GetMyRequestsFilterDTO {

    private String status; // PENDING, APPROVED, REJECTED, etc.

    // Constructors
    public GetMyRequestsFilterDTO() {
    }

    public GetMyRequestsFilterDTO(String status) {
        this.status = status;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GetMyRequestsFilterDTO{" +
                "status='" + status + '\'' +
                '}';
    }
}
