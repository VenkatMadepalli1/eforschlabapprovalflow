package com.eforsch.dto;

import java.util.Map;

public class BorrowedInventoryRequest {
    private User user;
    private Map<String, String> filters;  // search, donorGroup, fromDate, toDate, itemName

    public BorrowedInventoryRequest() {
    }

    public BorrowedInventoryRequest(User user, Map<String, String> filters) {
        this.user = user;
        this.filters = filters;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, String> filters) {
        this.filters = filters;
    }
}
