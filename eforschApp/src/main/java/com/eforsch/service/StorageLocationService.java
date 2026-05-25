package com.eforsch.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eforsch.entity.StorageLocation;
import com.eforsch.repository.StorageLocationRepository;
import com.eforsch.util.StorageLocationVO;

@Service
public class StorageLocationService {

    @Autowired
    private StorageLocationRepository storageLocationRepository;

    // Convert Entity to VO
    private StorageLocationVO entityToVO(StorageLocation storageLocation) {
        StorageLocationVO storageLocationVO = new StorageLocationVO();
        storageLocationVO.setId(storageLocation.getId());
        storageLocationVO.setStorageLocation(storageLocation.getStorageLocation());
        storageLocationVO.setCreatedAt(storageLocation.getCreatedAt());
        storageLocationVO.setUpdatedAt(storageLocation.getUpdatedAt());
        return storageLocationVO;
    }

    // Convert VO to Entity
    private StorageLocation voToEntity(StorageLocationVO storageLocationVO) {
        StorageLocation storageLocation = new StorageLocation();
        if (storageLocationVO.getId() != null && storageLocationVO.getId() > 0) {
            storageLocation.setId(storageLocationVO.getId());
        }
        storageLocation.setStorageLocation(storageLocationVO.getStorageLocation());
        if (storageLocationVO.getCreatedAt() != null) {
            storageLocation.setCreatedAt(storageLocationVO.getCreatedAt());
        } else {
            storageLocation.setCreatedAt(LocalDateTime.now());
        }
        storageLocation.setUpdatedAt(LocalDateTime.now());
        return storageLocation;
    }

    // GET - Get all storage locations
    public List<StorageLocationVO> getAllStorageLocations() {
        return storageLocationRepository.findAll().stream()
                .map(this::entityToVO)
                .collect(Collectors.toList());
    }

    // GET - Get storage location by ID
    public StorageLocationVO getStorageLocationById(Integer id) {
        return storageLocationRepository.findById(id)
                .map(this::entityToVO)
                .orElse(null);
    }

    // GET - Get storage location by name
    public StorageLocationVO getStorageLocationByName(String storageLocation) {
        return storageLocationRepository.findByStorageLocation(storageLocation)
                .map(this::entityToVO)
                .orElse(null);
    }

    // GET - Search storage locations by search term
    public List<StorageLocationVO> searchStorageLocations(String searchTerm) {
        return storageLocationRepository.findByStorageLocationContainingIgnoreCase(searchTerm).stream()
                .map(this::entityToVO)
                .collect(Collectors.toList());
    }

    // POST - Create new storage location
    @Transactional
    public StorageLocationVO createStorageLocation(StorageLocationVO storageLocationVO) {
        StorageLocation storageLocation = voToEntity(storageLocationVO);
        storageLocation = storageLocationRepository.save(storageLocation);
        return entityToVO(storageLocation);
    }

    // PUT - Update existing storage location
    @Transactional
    public StorageLocationVO updateStorageLocation(StorageLocationVO storageLocationVO) {
        if (storageLocationVO.getId() == null) {
            throw new IllegalArgumentException("Storage Location ID is required for update");
        }

        StorageLocation existingStorageLocation = storageLocationRepository.findById(storageLocationVO.getId())
                .orElseThrow(() -> new RuntimeException("Storage Location not found with ID: " + storageLocationVO.getId()));

        existingStorageLocation.setStorageLocation(storageLocationVO.getStorageLocation());
        existingStorageLocation.setUpdatedAt(LocalDateTime.now());

        StorageLocation updatedStorageLocation = storageLocationRepository.save(existingStorageLocation);
        return entityToVO(updatedStorageLocation);
    }

    // DELETE - Delete storage location by ID
    @Transactional
    public void deleteStorageLocation(Integer id) {
        if (!storageLocationRepository.existsById(id)) {
            throw new RuntimeException("Storage Location not found with ID: " + id);
        }
        storageLocationRepository.deleteById(id);
    }

    // Check if storage location exists
    public boolean isStorageLocationExists(Integer id) {
        return storageLocationRepository.existsById(id);
    }

    // Check if storage location name exists
    public boolean isStorageLocationNameExists(String storageLocation) {
        return storageLocationRepository.findByStorageLocation(storageLocation).isPresent();
    }
}
