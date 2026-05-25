package com.eforsch.dto;

public class PaginationDTO {
    
    private long totalPages;
    private int pageSize;
    private int currentPage;
    private long totalRecords;
    
    public PaginationDTO() {
    }
    
    public PaginationDTO(long totalPages, int pageSize, int currentPage, long totalRecords) {
        this.totalPages = totalPages;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.totalRecords = totalRecords;
    }
    
    public long getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
    public long getTotalRecords() {
        return totalRecords;
    }
    
    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }
}
