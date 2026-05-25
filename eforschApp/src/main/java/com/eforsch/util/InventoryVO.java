package com.eforsch.util;

import java.util.Date;

public class InventoryVO {

	    private Long productId;
	    private String productname;
	    private String catalogue;
	    private String companyname;
	    private Integer quantity;
	    private String groupName;
	    private String companyinternalno;
	    private String sapmaterialno;
	    private String weightvolsubqty;
	    private String budgetno;
	    private String concentration;
	    private String remarks;
	    private double price;
	    
	    private Date orderdate;
	    private Date expirydate;
	    private String addedby;
	    private boolean shared;

	    // File details
	    private String fileName;
	    private String fileType;
	    
	    // Instead of raw byte[], expose Base64 string for JSON APIs
	    private byte[] fileContent;

	    // --- Getters & Setters ---
	    public Long getProductId() {
	        return productId;
	    }
	    public void setProductId(Long productId) {
	        this.productId = productId;
	    }
	    public String getProductname() {
	        return productname;
	    }
	    public void setProductname(String productname) {
	        this.productname = productname;
	    }
	   
		public String getCatalogue() {
			return catalogue;
		}

		public void setCatalogue(String catalogue) {
			this.catalogue = catalogue;
		}
	    
		public String getCompanyname() {
	        return companyname;
	    }
	    public void setCompanyname(String companyname) {
	        this.companyname = companyname;
	    }
	    public Integer getQuantity() {
	        return quantity;
	    }
	    public void setQuantity(Integer quantity) {
	        this.quantity = quantity;
	    }
	    public String getGroupName() {
	        return groupName;
	    }
	    public void setGroupName(String groupName) {
	        this.groupName = groupName;
	    }
	    public String getCompanyinternalno() {
	        return companyinternalno;
	    }
	    public void setCompanyinternalno(String companyinternalno) {
	        this.companyinternalno = companyinternalno;
	    }
	    public String getSapmaterialno() {
	        return sapmaterialno;
	    }
	    public void setSapmaterialno(String sapmaterialno) {
	        this.sapmaterialno = sapmaterialno;
	    }
	    public String getWeightvolsubqty() {
	        return weightvolsubqty;
	    }
	    public void setWeightvolsubqty(String weightvolsubqty) {
	        this.weightvolsubqty = weightvolsubqty;
	    }
	    public String getBudgetno() {
	        return budgetno;
	    }
	    public void setBudgetno(String budgetno) {
	        this.budgetno = budgetno;
	    }
	    public String getConcentration() {
	        return concentration;
	    }
	    public void setConcentration(String concentration) {
	        this.concentration = concentration;
	    }
	    public String getRemarks() {
	        return remarks;
	    }
	    public void setRemarks(String remarks) {
	        this.remarks = remarks;
	    }
	    public Date getOrderdate() {
	        return orderdate;
	    }
	    public void setOrderdate(Date orderdate) {
	        this.orderdate = orderdate;
	    }
	    public Date getExpirydate() {
	        return expirydate;
	    }
	    public void setExpirydate(Date expirydate) {
	        this.expirydate = expirydate;
	    }
	    public String getAddedby() {
	        return addedby;
	    }
	    public void setAddedby(String addedby) {
	        this.addedby = addedby;
	    }
	    public boolean isShared() {
	        return shared;
	    }
	    public void setShared(boolean shared) {
	        this.shared = shared;
	    }
	    public String getFileName() {
	        return fileName;
	    }
	    public void setFileName(String fileName) {
	        this.fileName = fileName;
	    }
	    public String getFileType() {
	        return fileType;
	    }
	    public void setFileType(String fileType) {
	        this.fileType = fileType;
	    }
	    
	    public byte[] getFileContent() {
			return fileContent;
		}
		
		public void setFileContent(byte[] fileContent) {
			this.fileContent = fileContent;
		}
		
		
		public double getPrice() {
			return price;
		}	

		public void setPrice(double price) {
			this.price = price;
		}
		
	}

	
	

