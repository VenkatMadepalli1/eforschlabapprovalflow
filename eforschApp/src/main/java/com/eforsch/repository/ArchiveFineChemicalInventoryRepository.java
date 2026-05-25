package com.eforsch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.ArchiveFineChemicalInventory;

@Repository
public interface ArchiveFineChemicalInventoryRepository extends JpaRepository<ArchiveFineChemicalInventory, Integer> {
	
	 Page<ArchiveFineChemicalInventory> findAll(Pageable pageable);

	void deleteByArchiveId(Long archiveId);

	Optional<ArchiveFineChemicalInventory> findByProductId(int intValue);
	
	//List<ArchiveFineChemicalInventory> findByArchivedYear(int year);
	
	//List<ArchiveFineChemicalInventory> findByArchivedYearAndGroupName(int year, String groupName);

	
}
