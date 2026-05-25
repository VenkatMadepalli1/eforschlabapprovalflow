package com.eforsch.dto;

public class GetMyRequestsPayloadDTO {

    private User user;
    private GetMyRequestsFilterDTO filters;

    // Constructors
    public GetMyRequestsPayloadDTO() {
    }

    public GetMyRequestsPayloadDTO(User user, GetMyRequestsFilterDTO filters) {
        this.user = user;
        this.filters = filters;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GetMyRequestsFilterDTO getFilters() {
        return filters;
    }

    public void setFilters(GetMyRequestsFilterDTO filters) {
        this.filters = filters;
    }

    @Override
    public String toString() {
        return "GetMyRequestsPayloadDTO{" +
                "user=" + user +
                ", filters=" + filters +
                '}';
    }
}
