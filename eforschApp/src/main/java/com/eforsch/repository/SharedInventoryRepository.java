package com.eforsch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.SharedInventory;

@Repository
public interface SharedInventoryRepository extends JpaRepository<SharedInventory, Long> {
    
    Optional<SharedInventory> findByProductIdAndInventoryType(String productId, String inventoryType);
    
    Optional<SharedInventory> findByProductId(String productId);
    
    List<SharedInventory> findBySharedByGroupName(String groupName);
    
    List<SharedInventory> findBySharedByUserId(Long userId);
    
    void deleteByProductIdAndInventoryType(String productId, String inventoryType);
}
