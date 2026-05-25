package com.eforsch.dto;

import java.math.BigDecimal;
import java.util.Date;

public class BudgetVO {

    private Integer budgetId;
    private String name;
    private String budgetno;
    private String budgetname;
    private BigDecimal moneyleft;
    private BigDecimal moneyallocated;
    private Date createddate;
    private String groupName;

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

    public BigDecimal getMoneyallocated() {
        return moneyallocated;
    }

    public void setMoneyallocated(BigDecimal moneyallocated) {
        this.moneyallocated = moneyallocated;
    }

    public Date getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}