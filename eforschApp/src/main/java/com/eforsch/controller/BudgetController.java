package com.eforsch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eforsch.dto.BudgetRequestModel;
import com.eforsch.dto.User;
import com.eforsch.entity.Budget;
import com.eforsch.service.BudgetService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

	@Autowired
	private BudgetService budgetService;

	@PostMapping(path = "/getBudgetList", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get Budget List")
	public ResponseEntity<?> getBudgetList(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int limit, 
			@RequestParam(defaultValue = "createddate") String sortBy,
			@RequestParam(defaultValue = "desc") String order, @RequestBody User user) {
		return ResponseEntity.ok(budgetService.getBudgetList(page, limit, sortBy, order, user));
	}

	@PostMapping(path = "/addBudget")
	@Operation(summary = "Add a new Budget")
	public ResponseEntity<?> addBudget(@RequestBody BudgetRequestModel request) {
		return ResponseEntity.ok(budgetService.addOrUpdateBudget(request));
	}

	@PutMapping(path = "/updateBudget")
	@Operation(summary = "Update Budget by ID")
	public ResponseEntity<?> updateBudget(@RequestParam String role, @RequestBody BudgetRequestModel budget) {

		return ResponseEntity.ok(budgetService.updateBudget(role, budget));
	}

	@DeleteMapping(path = "/delecteBudget")
	@Operation(summary = "Delete Budget by ID")
	public ResponseEntity<?> deleteBudget(@RequestParam Integer budgetID, @RequestBody User user) {

		return ResponseEntity.ok(budgetService.deleteBudget(budgetID, user));
	}

}
