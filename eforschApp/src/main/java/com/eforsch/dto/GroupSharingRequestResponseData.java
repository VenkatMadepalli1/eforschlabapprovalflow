package com.eforsch.dto;

public class GroupSharingRequestResponseData {
    
    private String requestId;
    
    private String status;
    
    private String id;
    
    private String exchangeAddress;
    
    private Integer quantity;
    
    private String userEmail;
    
    private String userName;

    public GroupSharingRequestResponseData() {
    }

    public GroupSharingRequestResponseData(String requestId, String status, String id, String exchangeAddress, 
                                          Integer quantity, String userEmail, String userName) {
        this.requestId = requestId;
        this.status = status;
        this.id = id;
        this.exchangeAddress = exchangeAddress;
        this.quantity = quantity;
        this.userEmail = userEmail;
        this.userName = userName;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExchangeAddress() {
        return exchangeAddress;
    }

    public void setExchangeAddress(String exchangeAddress) {
        this.exchangeAddress = exchangeAddress;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
