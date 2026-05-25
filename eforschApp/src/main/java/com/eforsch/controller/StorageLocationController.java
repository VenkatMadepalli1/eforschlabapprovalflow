package com.eforsch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eforsch.service.StorageLocationService;
import com.eforsch.util.StorageLocationVO;

@RestController
@RequestMapping("/storageLocations")
public class StorageLocationController {

    @Autowired
    private StorageLocationService storageLocationService;

    // GET - Retrieve all storage locations
    @GetMapping("/getAllStorageLocations")
    public ResponseEntity<List<StorageLocationVO>> getAllStorageLocations() {
        List<StorageLocationVO> storageLocations = storageLocationService.getAllStorageLocations();
        return ResponseEntity.ok(storageLocations);
    }

    // GET - Retrieve storage location by ID
    @GetMapping("/{id}")
    public ResponseEntity<StorageLocationVO> getStorageLocationById(@PathVariable Integer id) {
        StorageLocationVO storageLocation = storageLocationService.getStorageLocationById(id);
        if (storageLocation != null) {
            return ResponseEntity.ok(storageLocation);
        }
        return ResponseEntity.notFound().build();
    }

    // GET - Retrieve storage location by name
    @GetMapping("/byName/{storageLocation}")
    public ResponseEntity<StorageLocationVO> getStorageLocationByName(@PathVariable String storageLocation) {
        StorageLocationVO result = storageLocationService.getStorageLocationByName(storageLocation);
        if (result != null) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    // GET - Search storage locations
    @GetMapping("/search")
    public ResponseEntity<List<StorageLocationVO>> searchStorageLocations(@RequestParam String searchTerm) {
        List<StorageLocationVO> results = storageLocationService.searchStorageLocations(searchTerm);
        if (results != null && !results.isEmpty()) {
            return ResponseEntity.ok(results);
        }
        return ResponseEntity.notFound().build();
    }

    // POST - Create a new storage location
    @PostMapping("/createStorageLocation")
    public ResponseEntity<StorageLocationVO> createStorageLocation(@RequestBody StorageLocationVO storageLocationVO) {
        if (storageLocationVO.getStorageLocation() == null || storageLocationVO.getStorageLocation().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        StorageLocationVO createdStorageLocation = storageLocationService.createStorageLocation(storageLocationVO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStorageLocation);
    }

    // PUT - Update an existing storage location
    @PutMapping("/updateStorageLocation")
    public ResponseEntity<StorageLocationVO> updateStorageLocation(@RequestBody StorageLocationVO storageLocationVO) {
        if (storageLocationVO.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (storageLocationVO.getStorageLocation() == null || storageLocationVO.getStorageLocation().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            StorageLocationVO updatedStorageLocation = storageLocationService.updateStorageLocation(storageLocationVO);
            return ResponseEntity.ok(updatedStorageLocation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Delete a storage location by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStorageLocation(@PathVariable Integer id) {
        try {
            storageLocationService.deleteStorageLocation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
