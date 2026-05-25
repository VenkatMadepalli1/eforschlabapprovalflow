package com.eforsch.entity;



import java.util.Date;
import java.util.List;

import com.eforsch.util.StringListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    
    private Long productId;

    private String productName;
    private String catalogue;
    private String companyName;
    private Integer quantity;
    private String budgetno;
    private Double price;

    private String safetydatasheet;
    
    private Date expiryDate;

    // Internal company reference number
    private String companyInternalNo;

    // SAP material number
    private String sapMaterialNo;
    
    private String inventoryType;

    // Weight/volume sub quantity
    private String weightVolSubQty;

    private Date orderDate;

    // Person who ordered it
    private String orderedBy;

    // Chemical concentration
    private String concentration;

 
    // Remarks or notes
    @Column(length = 1000)
    private String remarks;

    // CAS number
    private String casNumber;

    // Hazardous substance indicator
    private String hazardousSubstance;

    // CMR (Carcinogenic, Mutagenic, Reprotoxic) substance indicator
    private String cmrSubstance;

    // Skin resorptive indicator
    private String skinResorptive;

    // List of GHS symbols (stored in separate table)
    @Convert(converter = StringListConverter.class)
    private List<String> ghsSymbols;
    
 // List of GHS symbols (stored in separate table)
    @Convert(converter = StringListConverter.class)
    private List<String> ghsSignalWord;

    // GHS checkbox selection
    private String ghsCheckbox;

    // Hazard statements (H-phrases)
    private String hPhrases;

    // Precautionary statements (P-phrases)
    private String pPhrases;

    // Substitution check indicator
    private String substitutionCheck;

    // Storage location
    private String storageLocation;
    
    private String substitutionOption;
    
    
    @Column(nullable = false) 
    private Boolean adminApproved;
    
    @Column(nullable = false) 
    private Boolean labApproved;
    
    
    private Date adminApprovalStatusDate;
    private Date labApprovalStatusDate;
    
    
    private String adminName;
    private String userName;
    private String status;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] fileContent;
    
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
	
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public String getOrderedBy() {
		return orderedBy;
	}
	public void setOrderedBy(String orderedBy) {
		this.orderedBy = orderedBy;
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
	
	public List<String> getGhsSignalWord() {
		return ghsSignalWord;
	}
	public void setGhsSignalWord(List<String> ghsSignalWord) {
		this.ghsSignalWord = ghsSignalWord;
	}
	public String getSubstitutionOption() {
		return substitutionOption;
	}
	public void setSubstitutionOption(String substitutionOption) {
		this.substitutionOption = substitutionOption;
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
	
	public byte[] getFileContent() {
		return fileContent;
	}
	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
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

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}
}
