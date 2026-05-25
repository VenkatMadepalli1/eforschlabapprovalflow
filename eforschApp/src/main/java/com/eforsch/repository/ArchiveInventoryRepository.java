package com.eforsch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.ArchiveInventory;

@Repository
public interface ArchiveInventoryRepository extends JpaRepository<ArchiveInventory, Long> {

	Page<ArchiveInventory> findAll(Pageable pageable);

	Optional<ArchiveInventory> findByProductId(Long produID);

	//List<ArchiveInventory> findByArchivedYear(int year);
	
	//List<ArchiveInventory> findByArchivedYearAndGroupName(int year, String groupName);
}
