package com.eforsch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eforsch.entity.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	Page<Inventory> findAll(Pageable pageable);
	
	 Inventory findByProductId(Long productId);

	Page<Inventory> findByShared(boolean shared, Pageable pageable);

	// write a method fetch the inventory by group name
	Page<Inventory> findByGroupNameContainingIgnoreCase(String groupname, Pageable pageable);

	Page<Inventory> findByGroupNameContainingIgnoreCaseAndSharedFalse(String groupName, Pageable pageable);

	Page<Inventory> findByGroupNameContainingIgnoreCaseAndShared(String groupName, boolean shared, Pageable pageable);
}
