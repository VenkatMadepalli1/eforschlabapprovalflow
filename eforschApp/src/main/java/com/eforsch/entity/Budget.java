package com.eforsch.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "budgetsummary")
public class Budget {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer budgetId;

	@Column(length = 255)
	private String name;

	@Column(length = 100)
	private String budgetno;

	@Column(length = 255)
	private String budgetname;

	@Column(precision = 12, scale = 2)
	private BigDecimal moneyleft;
	
	@Column(precision = 12, scale = 2)
	private BigDecimal moneyallocated;
	
	public BigDecimal getMoneyallocated() {
		return moneyallocated;
	}

	public void setMoneyallocated(BigDecimal moneyallocated) {
		this.moneyallocated = moneyallocated;
	}

	private Date createddate;
	
	// New field for group name
	@Column(length = 255)
	private String groupName;
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public Integer getBudgetId() {
		return budgetId;
	}

	public void setBudgetId(Integer budgetId) {
		this.budgetId = budgetId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBudgetno() {
		return budgetno;
	}

	public void setBudgetno(String budgetno) {
		this.budgetno = budgetno;
	}

	public String getBudgetname() {
		return budgetname;
	}

	public void setBudgetname(String budgetname) {
		this.budgetname = budgetname;
	}

	public BigDecimal getMoneyleft() {
		return moneyleft;
	}

	public void setMoneyleft(BigDecimal moneyleft) {
		this.moneyleft = moneyleft;
	}

	public Date getCreateddate() {
		return createddate;
	}

	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}
}
