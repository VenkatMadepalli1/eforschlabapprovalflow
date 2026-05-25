package com.eforsch.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.eforsch.dto.AddressDTO;
import com.eforsch.dto.TimeSlotDTO;

public class ShareInventoryVO {

    private Long productId;

    private String productName;          // merged from productname
    private String catalogue;            // keep only one spelling
    private String companyName;          // merged
    private Integer quantity;            // keep as Integer (numeric)
    private String groupName;
    private String companyInternalNo;
    private String sapMaterialNo;
    private String weightVolSubQty;      // renamed wvsubqty to readable form
    private String budgetNo;             // keep one field instead of budgetno/catalogue confusion
    private String concentration;
    private String remarks;              // keep one (instead of remark/remarks)

    private Date orderDate;
    private Date expiryDate;
    private Date createdAt;

    private String addedBy;
    private String orderedBy;
    private boolean shared;

    private BigDecimal amount;
    private String casNumber;
    private Boolean hazardousSubstance;
    private Boolean cmrSubstance;
    private Boolean skinResorptive;

    private String[] ghsSymbols;
    private String[] ghsSignalWord;
    private String hPhrases;
    private String pPhrases;
    private String substitutionCheck;
    private String substitutionOption;
    private String storageLocation;
    private String inventoryType;

    private String qtyPriceOrdered;
    private String priority;
    private String received;
    
    private List<TimeSlotDTO> timeSlots;
    private AddressDTO address;
    
    // Shared by information
    private Long sharedByUserId;
    private String sharedByUserEmail;
    private String sharedByUserName;
    private String sharedByUserRole;
    private String sharedByGroupName;
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
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
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getCompanyInternalNo() {
		return companyInternalNo;
	}
	public void setCompanyInternalNo(String companyInternalNo) {
		this.companyInternalNo = companyInternalNo;
	}
	public String getSapMaterialNo() {
		return sapMaterialNo;
	}
	public void setSapMaterialNo(String sapMaterialNo) {
		this.sapMaterialNo = sapMaterialNo;
	}
	public String getWeightVolSubQty() {
		return weightVolSubQty;
	}
	public void setWeightVolSubQty(String weightVolSubQty) {
		this.weightVolSubQty = weightVolSubQty;
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
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getAddedBy() {
		return addedBy;
	}
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}
	public String getOrderedBy() {
		return orderedBy;
	}
	public void setOrderedBy(String orderedBy) {
		this.orderedBy = orderedBy;
	}
	public boolean isShared() {
		return shared;
	}
	public void setShared(boolean shared) {
		this.shared = shared;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getCasNumber() {
		return casNumber;
	}
	public void setCasNumber(String casNumber) {
		this.casNumber = casNumber;
	}
	public Boolean getHazardousSubstance() {
		return hazardousSubstance;
	}
	public void setHazardousSubstance(Boolean hazardousSubstance) {
		this.hazardousSubstance = hazardousSubstance;
	}
	public Boolean getCmrSubstance() {
		return cmrSubstance;
	}
	public void setCmrSubstance(Boolean cmrSubstance) {
		this.cmrSubstance = cmrSubstance;
	}
	public Boolean getSkinResorptive() {
		return skinResorptive;
	}
	public void setSkinResorptive(Boolean skinResorptive) {
		this.skinResorptive = skinResorptive;
	}
	public String[] getGhsSymbols() {
		return ghsSymbols;
	}
	public void setGhsSymbols(String[] ghsSymbols) {
		this.ghsSymbols = ghsSymbols;
	}
	public String[] getGhsSignalWord() {
		return ghsSignalWord;
	}
	public void setGhsSignalWord(String[] ghsSignalWord) {
		this.ghsSignalWord = ghsSignalWord;
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
	public String getSubstitutionOption() {
		return substitutionOption;
	}
	public void setSubstitutionOption(String substitutionOption) {
		this.substitutionOption = substitutionOption;
	}
	public String getStorageLocation() {
		return storageLocation;
	}
	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}
	public String getQtyPriceOrdered() {
		return qtyPriceOrdered;
	}
	public void setQtyPriceOrdered(String qtyPriceOrdered) {
		this.qtyPriceOrdered = qtyPriceOrdered;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getReceived() {
		return received;
	}
	public void setReceived(String received) {
		this.received = received;
	}
		public String getInventoryType() {
			return inventoryType;
		}
		
		public void setInventoryType(String inventoryType) {
			this.inventoryType = inventoryType;
		}

		public List<TimeSlotDTO> getTimeSlots() {
			return timeSlots;
		}

		public void setTimeSlots(List<TimeSlotDTO> timeSlots) {
			this.timeSlots = timeSlots;
		}

		public AddressDTO getAddress() {
			return address;
		}

		public void setAddress(AddressDTO address) {
			this.address = address;
		}

		public Long getSharedByUserId() {
			return sharedByUserId;
		}

		public void setSharedByUserId(Long sharedByUserId) {
			this.sharedByUserId = sharedByUserId;
		}

		public String getSharedByUserEmail() {
			return sharedByUserEmail;
		}

		public void setSharedByUserEmail(String sharedByUserEmail) {
			this.sharedByUserEmail = sharedByUserEmail;
		}

		public String getSharedByUserName() {
			return sharedByUserName;
		}

		public void setSharedByUserName(String sharedByUserName) {
			this.sharedByUserName = sharedByUserName;
		}

		public String getSharedByUserRole() {
			return sharedByUserRole;
		}

		public void setSharedByUserRole(String sharedByUserRole) {
			this.sharedByUserRole = sharedByUserRole;
		}

		public String getSharedByGroupName() {
			return sharedByGroupName;
		}

		public void setSharedByGroupName(String sharedByGroupName) {
			this.sharedByGroupName = sharedByGroupName;
		}

    // --- getters & setters omitted for brevity ---
}
