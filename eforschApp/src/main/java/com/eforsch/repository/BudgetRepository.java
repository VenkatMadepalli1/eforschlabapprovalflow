package com.eforsch.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.Budget;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {
	
	 Page<Budget> findAll(Pageable pageable);
	 
	 Page<Budget> findByGroupName(String groupName, Pageable pageable);
	 
	 Optional<Budget> findByGroupName(String groupName);
	 
	 Optional<Budget> findByBudgetno(String budgetno);

	 
}
