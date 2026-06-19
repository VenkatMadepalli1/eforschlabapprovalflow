package com.eforsch.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "archive_finechemicalinventory")
public class ArchiveFineChemicalInventory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long archiveId;

	private Long productId;
    private String productname;

    private String companyname;
    private String quantity;

    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    private String companyInternalNo;
    private String sapMaterialNo;
    private String wvsubqty;
    private String budgetno;
    private boolean visibleToUsers;

    @Temporal(TemporalType.DATE)
    private Date orderdate;

    private String orderedby;
    private String concentration;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;   // Maps to 'amount' in payload

    @Column(length = 1000)
    private String remarks;
    private String casnumber;
    private Boolean hazardousSubstance;
    private Boolean cmrSubstance;
    private Boolean skinResorptive;
    
    @Column(columnDefinition = "json")
    private String ghsSymbols;
   
    @Column(columnDefinition = "json")
    private String ghsSignalWord;
    
    private String hPhrases;
    private String pPhrases;
    private String substitutionCheck;
    private String substitutionOption;
    private String storageLocation;
    private String groupName;
    private String qtypriceordered;
    private String priority;
    private String received;
    private String catalogue; // (Optional, remove if typo)
    private Date createdAt;
    private double price;
    
    
    private boolean shared;
    @Column(name = "filename", length = 255)
    private String fileName;

    @Column(name = "filetype", length = 100)
    private String fileType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "filecontent", columnDefinition = "LONGBLOB")
    private byte[] fileContent;


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

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
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

	public String getWvsubqty() {
		return wvsubqty;
	}

	public void setWvsubqty(String wvsubqty) {
		this.wvsubqty = wvsubqty;
	}

	public String getBudgetno() {
		return budgetno;
	}

	public void setBudgetno(String budgetno) {
		this.budgetno = budgetno;
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

	public String getConcentration() {
		return concentration;
	}

	public void setConcentration(String concentration) {
		this.concentration = concentration;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getCasnumber() {
		return casnumber;
	}

	public void setCasnumber(String casnumber) {
		this.casnumber = casnumber;
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

	public String getGhsSymbols() {
		return ghsSymbols;
	}

	public void setGhsSymbols(String ghsSymbols) {
		this.ghsSymbols = ghsSymbols;
	}

	public String getGhsSignalWord() {
		return ghsSignalWord;
	}

	public void setGhsSignalWord(String ghsSignalWord) {
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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getQtypriceordered() {
		return qtypriceordered;
	}

	public void setQtypriceordered(String qtypriceordered) {
		this.qtypriceordered = qtypriceordered;
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

	
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Long getArchiveId() {
		return archiveId;
	}

	public void setArchiveId(Long archiveId) {
		this.archiveId = archiveId;
	}

	public boolean isVisibleToUsers() {
		return visibleToUsers;
	}

	public void setVisibleToUsers(boolean visibleToUsers) {
		this.visibleToUsers = visibleToUsers;
	}
	
	
}
