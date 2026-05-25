package com.eforsch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.FineChemicalInventory;

@Repository
public interface FineChemicalInventoryRepository extends JpaRepository<FineChemicalInventory, Long> {
	
	 Page<FineChemicalInventory> findAll(Pageable pageable);

	Page<FineChemicalInventory> findBySharedAndGroupNameContainingIgnoreCase(boolean shared, String groupName, Pageable pageable);

	FineChemicalInventory findByProductId(Long intValue);
	
	Page<FineChemicalInventory> findByShared(boolean shared, Pageable pageable);
	
}
