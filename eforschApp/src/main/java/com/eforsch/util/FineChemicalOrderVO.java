	
	// OrderVO.java
	package com.eforsch.util;

	import java.util.Date;
	import java.util.List;

	public class FineChemicalOrderVO {

		
		
		
	    private Long orderId;

	    private String productName;
	    private String catalogue;
	    private String companyName;
	    private Integer quantity;
	    private String budgetno;        // note: entity also has budgetNo (see below)
	    private Double price;

	    private String safetydatasheet;
	    private Date expiryDate;

	    private String companyinternalno;
	    private String sapmaterialno;
	    private String weightvolsubqty;

	    private String budgetNo;        // duplicate concept with budgetno in entity
	    private Date orderdate;
	    private String orderedby;
	    private String concentration;

	    private String remarks;
	    private String casNumber;
	    private String hazardousSubstance;
	    private String cmrSubstance;
	    private String skinResorptive;
	    private String inventoryType;
	    private List<String> ghsSymbols;
	    private String ghsCheckbox;
	    private String hPhrases;
	    private String pPhrases;
	    private String substitutionCheck;
	    private String storageLocation;

	    private Boolean adminApproved = Boolean.FALSE;
	    private Boolean labApproved   = Boolean.FALSE;

	    private Date adminApprovalStatusDate;
	    private Date labApprovalStatusDate;

	    private String adminName;
	    private String userName;
	    private String status;

	    private byte[] attachment;

	    private String createdAt;
	    private String updatedAt;
	    private String createdBy;
	    private String updatedBy;
	    private String groupName;
		public Long getOrderId() {
			return orderId;
		}
		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}

		public String getCatalogue() {
			return catalogue;
		}

		public void setCatalogue(String catalogue) {
			this.catalogue = catalogue;
		}
		
		public String getCompanyName() {
			return companyName;
		}
		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		public String getBudgetno() {
			return budgetno;
		}
		public void setBudgetno(String budgetno) {
			this.budgetno = budgetno;
		}
		public Double getPrice() {
			return price;
		}
		public void setPrice(Double price) {
			this.price = price;
		}
		public String getSafetydatasheet() {
			return safetydatasheet;
		}
		public void setSafetydatasheet(String safetydatasheet) {
			this.safetydatasheet = safetydatasheet;
		}
		public Date getExpiryDate() {
			return expiryDate;
		}
		public void setExpiryDate(Date expiryDate) {
			this.expiryDate = expiryDate;
		}
		
		public String getBudgetNo() {
			return budgetNo;
		}
		public void setBudgetNo(String budgetNo) {
			this.budgetNo = budgetNo;
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
		public String getCasNumber() {
			return casNumber;
		}
		public void setCasNumber(String casNumber) {
			this.casNumber = casNumber;
		}
		public String getHazardousSubstance() {
			return hazardousSubstance;
		}
		public void setHazardousSubstance(String hazardousSubstance) {
			this.hazardousSubstance = hazardousSubstance;
		}
		public String getCmrSubstance() {
			return cmrSubstance;
		}
		public void setCmrSubstance(String cmrSubstance) {
			this.cmrSubstance = cmrSubstance;
		}
		public String getSkinResorptive() {
			return skinResorptive;
		}
		public void setSkinResorptive(String skinResorptive) {
			this.skinResorptive = skinResorptive;
		}
		public List<String> getGhsSymbols() {
			return ghsSymbols;
		}
		public void setGhsSymbols(List<String> ghsSymbols) {
			this.ghsSymbols = ghsSymbols;
		}
		public String getGhsCheckbox() {
			return ghsCheckbox;
		}
		public void setGhsCheckbox(String ghsCheckbox) {
			this.ghsCheckbox = ghsCheckbox;
		}
		public String gethPhrases() {
			return hPhrases;
		}
		public void sethPhrases(String hPhrases) {
			this.hPhrases = hPhrases;
		}
		public String getpPhrases() {
			return pPhrases;
		}
		public void setpPhrases(String pPhrases) {
			this.pPhrases = pPhrases;
		}
		public String getSubstitutionCheck() {
			return substitutionCheck;
		}
		public void setSubstitutionCheck(String substitutionCheck) {
			this.substitutionCheck = substitutionCheck;
		}
		public String getStorageLocation() {
			return storageLocation;
		}
		public void setStorageLocation(String storageLocation) {
			this.storageLocation = storageLocation;
		}
		public Boolean getAdminApproved() {
			return adminApproved;
		}
		public void setAdminApproved(Boolean adminApproved) {
			this.adminApproved = adminApproved;
		}
		public Boolean getLabApproved() {
			return labApproved;
		}
		public void setLabApproved(Boolean labApproved) {
			this.labApproved = labApproved;
		}
		public Date getAdminApprovalStatusDate() {
			return adminApprovalStatusDate;
		}
		public void setAdminApprovalStatusDate(Date adminApprovalStatusDate) {
			this.adminApprovalStatusDate = adminApprovalStatusDate;
		}
		public Date getLabApprovalStatusDate() {
			return labApprovalStatusDate;
		}
		public void setLabApprovalStatusDate(Date labApprovalStatusDate) {
			this.labApprovalStatusDate = labApprovalStatusDate;
		}
		public String getAdminName() {
			return adminName;
		}
		public void setAdminName(String adminName) {
			this.adminName = adminName;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public byte[] getAttachment() {
			return attachment;
		}
		public void setAttachment(byte[] attachment) {
			this.attachment = attachment;
		}
		public String getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(String createdAt) {
			this.createdAt = createdAt;
		}
		public String getUpdatedAt() {
			return updatedAt;
		}
		public void setUpdatedAt(String updatedAt) {
			this.updatedAt = updatedAt;
		}
		public String getCreatedBy() {
			return createdBy;
		}
		public void setCreatedBy(String createdBy) {
			this.createdBy = createdBy;
		}
		public String getUpdatedBy() {
			return updatedBy;
		}
		public void setUpdatedBy(String updatedBy) {
			this.updatedBy = updatedBy;
		}
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
		
		public String getInventoryType() {
			return inventoryType;
		}

		public void setInventoryType(String inventoryType) {
			this.inventoryType = inventoryType;
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
		public Date getOrderdate() {
			return orderdate;
		}
		public void setOrderdate(Date orderdate) {
			this.orderdate = orderdate;
		}
		public String getOrderedby() {
			return orderedby;
		}
		public void setOrderedby(String orderedby) {
			this.orderedby = orderedby;
		}
		
		
		
	}
