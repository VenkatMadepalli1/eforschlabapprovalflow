package com.eforsch.dto;

public class BudgetRequestModel {

	    private BudgetVO budget;
	    private User user;

	    // Getters and Setters
	    public BudgetVO getBudget() {
	        return budget;
	    }

	    public void setBudget(BudgetVO budget) {
	        this.budget = budget;
	    }

	    public User getUser() {
	        return user;
	    }

	    public void setUser(User user) {
	        this.user = user;
	    }
	}