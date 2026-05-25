package com.eforsch.util;

import java.time.LocalDateTime;

public class CompanyVO {

    private Integer id;
    private String companyNo;
    private String companyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public CompanyVO() {
    }

    public CompanyVO(String companyNo, String companyName) {
        this.companyNo = companyNo;
        this.companyName = companyName;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyNo() {
        return companyNo;
    }

    public void setCompanyNo(String companyNo) {
        this.companyNo = companyNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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
