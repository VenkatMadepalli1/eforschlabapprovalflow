package com.eforsch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.InventoryTimeSlot;

@Repository
public interface InventoryTimeSlotRepository extends JpaRepository<InventoryTimeSlot, Long> {
    List<InventoryTimeSlot> findBySharedInventory_SharedInventoryId(Long sharedInventoryId);
    void deleteBySharedInventory_SharedInventoryId(Long sharedInventoryId);
}
