package com.eforsch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.StorageLocation;
import java.util.List;
import java.util.Optional;

@Repository
public interface StorageLocationRepository extends JpaRepository<StorageLocation, Integer> {

    // Find storage location by name
    Optional<StorageLocation> findByStorageLocation(String storageLocation);

    // Find all storage locations containing a search term
    List<StorageLocation> findByStorageLocationContainingIgnoreCase(String searchTerm);
}
