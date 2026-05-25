package com.eforsch.dto;

import java.util.List;
import java.util.Map;

public class SharedItemsRequestListResponse {
    
    private Map<String, Object> data;
    private String message;
    private String status;
    
    public SharedItemsRequestListResponse() {
    }
    
    public SharedItemsRequestListResponse(Map<String, Object> data, String message, String status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
