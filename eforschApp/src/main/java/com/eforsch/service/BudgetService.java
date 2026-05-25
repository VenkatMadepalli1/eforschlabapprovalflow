package com.eforsch.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.eforsch.dto.BudgetRequestModel;
import com.eforsch.dto.BudgetVO;
import com.eforsch.dto.User;
import com.eforsch.entity.Budget;
import com.eforsch.repository.BudgetRepository;

@Service
public class BudgetService {

	@Autowired
	private BudgetRepository budgetRepository;

	public Map<String, Object> getBudgetList(int page, int size, String sortBy, String order, User user) {
		try {
			// 🔑 Validate token (pseudo code — you'd call a JWT utility or Spring Security)
			// validateAndParseToken(token);

			// Pagination + sorting
			Page<Budget> budgetPage = budgetRepository.findAll(PageRequest.of(page - 1, size));

			List<Map<String, Object>> listData = budgetPage.getContent().stream()
					.map(b -> Map.<String, Object>of("budgetId", b.getBudgetId(), "name", b.getName(), "budgetno",
							b.getBudgetno(), "budgetname", b.getBudgetname(), "moneyallocated", b.getMoneyallocated(), "moneyleft",  b.getMoneyleft(), "groupName", b.getGroupName(),
							"createddate", b.getCreateddate()))
					.collect(Collectors.toList());

			Map<String, Object> pagination = Map.of("currentPage", budgetPage.getNumber() + 1, "totalRecords",
					budgetPage.getTotalElements(), "totalPages", budgetPage.getTotalPages(), "pageSize",
					budgetPage.getSize());

			List<Map<String, Object>> columns = List.of(Map.of("key", "budgetId", "label", "Budget Id", "sortable", true),
					Map.of("key", "name", "label", "Name", "filterable", true),
					Map.of("key", "budgetno", "label", "Budget No", "filterable", true),
					Map.of("key", "budgetname", "label", "Budget Name", "filterable", true),
					Map.of("key", "moneyleft", "label", "Money Left", "sortable", true),
					Map.of("key", "moneyallocated", "label", "Money Allocated", "sortable", true),
					Map.of("key", "createddate", "label", "Created Date", "sortable", true, "filterable", true));

			Map<String, Object> data = Map.of("list", listData, "pagination", pagination, "columns", columns);

			return Map.of("status", "success", "message", "Data fetched successfully", "data", data);

		} catch (SecurityException se) {
			return Map.of("status", "error", "message", se.getMessage(), "code", 401);
		} catch (Exception e) {
			return Map.of("status", "error", "message", "An unexpected error occurred.", "code", 500);
		}
	}

	private void validateAndParseToken(String token) {
		if (token == null || token.isBlank()) {
			throw new SecurityException("Unauthorized. Invalid or missing token.");
		}

	}

	public Map<String, Object> addOrUpdateBudget(BudgetRequestModel requestModel) {
	    try {
	        BudgetVO budgetModel = requestModel.getBudget();
	        User user = requestModel.getUser();
			if (user == null && !"LABMGMT".equalsIgnoreCase(user.getRole())) {
				return Map.of("status", "error", "message",
						"Permission denied: Only LAB MGMT role can add or update budget.", "code", 403);
			}

			if (budgetModel.getBudgetno() == null || budgetModel.getBudgetno().isEmpty()) {
				return Map.of("status", "error", "message", "Budget number is required.", "code", 400);
			}
	        	
	        Optional<Budget> optionalBudget = budgetRepository.findByBudgetno(budgetModel.getBudgetno());

	        Budget budget = null;
	        if (optionalBudget.isPresent()) {
	            // Update existing budget
	            budget = optionalBudget.get();
	            budget.setName(budgetModel.getName());
	            budget.setBudgetno(budgetModel.getBudgetno());
	            budget.setBudgetname(budgetModel.getBudgetname());
	            budget.setMoneyleft(budgetModel.getMoneyleft());
	            budget.setMoneyallocated(budgetModel.getMoneyallocated());
	            budget.setCreateddate(new Date()); // Optional: update created date if needed
	            budget.setGroupName(budgetModel.getGroupName());
	        } else {
	            // Create new budget
	            budget = new Budget();
	            budget.setName(budgetModel.getName());
	            budget.setBudgetno(budgetModel.getBudgetno());
	            budget.setBudgetname(budgetModel.getBudgetname());
	            budget.setMoneyleft(budgetModel.getMoneyleft());
	            budget.setMoneyallocated(budgetModel.getMoneyallocated());
	            budget.setCreateddate(new Date());
	            budget.setGroupName(budgetModel.getGroupName());
	        }

	        // Save the budget (insert or update)
	        Budget saved = budgetRepository.save(budget);

	        // Prepare response data
	        Map<String, Object> data = Map.of(
	            "budgetId", saved.getBudgetId(),
	            "name", saved.getName(),
	            "budgetno", saved.getBudgetno(), 
	            "budgetname", saved.getBudgetname(),
	            "moneyleft", saved.getMoneyleft(),
	            "moneyallocated", saved.getMoneyallocated(),
	            "createdAt", saved.getCreateddate(),
	            "groupName", saved.getGroupName()
	        );

	        return Map.of(
	            "status", "success",
	            "message", optionalBudget.isPresent() ? "Budget updated successfully" : "Budget created successfully",
	            "data", data
	        );

	    } catch (Exception e) {
	    	e.printStackTrace();
	        return Map.of(
	            "status", "error",
	            "message", "An unexpected error occurred.",
	            "code", 500
	        );
	    }
	}


	public Map<String, Object> updateBudget(String role, BudgetRequestModel budgetRequestModel) {
		try {
			// Validate token (simulate)
			// validateAndParseToken(token);
			BudgetVO input = budgetRequestModel.getBudget();
			
			Optional<Budget> optionalBudget = budgetRepository.findById(input.getBudgetId());
			if (optionalBudget.isEmpty()  || optionalBudget.get().getBudgetno() == null) {
				return Map.of("status", "error", "message", "Budget not found.", "code", 404);
			}

			Budget budget = optionalBudget.get();
			budget.setName(input.getName());
			budget.setBudgetno(input.getBudgetno());
			budget.setBudgetname(input.getBudgetname());
			budget.setMoneyleft(input.getMoneyleft());
			budget.setMoneyallocated(input.getMoneyallocated());
		//	budget.setCreateddate(input.getCreateddate()); // Optional: update created date if intended
			budget.setGroupName(input.getGroupName());
			Date updatedAt = new Date();

			Budget saved = budgetRepository.save(budget);

			Map<String, Object> data = Map.of("budgetno", saved.getBudgetno(), "budgetname", saved.getBudgetname(),
					"moneyleft", saved.getMoneyleft(), "moneyallocated", saved.getMoneyallocated(),  "createdAt", saved.getCreateddate(), "updatedAt", updatedAt, "groupName", saved.getGroupName());

			return Map.of("status", "success", "message", "Budget updated successfully", "data", data);

		} catch (SecurityException se) {
			return Map.of("status", "error", "message", se.getMessage(), "code", 401);
		} catch (Exception e) {
			return Map.of("status", "error", "message", "An unexpected error occurred.", "code", 500);
		}
	}

	public Map<String, Object> deleteBudget(Integer budgetID, User user) {
		try {
			// Validate token
			// validateAndParseToken(token);
			if (user == null || !"LABMGMT".equalsIgnoreCase(user.getRole())
					) {
				return Map.of("status", "error", "message",
						"Permission denied: Only LAB MGMT role can delete budget.", "code", 403);
			}

			Optional<Budget> optionalBudget = budgetRepository.findById(budgetID);
			if (optionalBudget.isEmpty()) {
				return Map.of("status", "error", "message", "Budget not found.", "code", 404);
			}

			budgetRepository.deleteById(budgetID);

			return Map.of("code", 200, "message", "Data Deleted successfully", "status", "success");

		} catch (SecurityException se) {
			return Map.of("status", "error", "message", se.getMessage(), "code", 401);
		} catch (Exception e) {
			return Map.of("status", "error", "message", "An unexpected error occurred.", "code", 500);
		}
	}

}
