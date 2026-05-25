package com.eforsch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.GroupSharingRequest;

@Repository
public interface GroupSharingRequestRepository extends JpaRepository<GroupSharingRequest, Long> {
    
    Optional<GroupSharingRequest> findByItemIdAndInventoryTypeAndUserIdAndStatusNot(String itemId, String inventoryType, Long userId, String status);
    
    Optional<GroupSharingRequest> findByProductIdAndUserIdAndStatusNot(String productId, Long userId, String status);
    
    List<GroupSharingRequest> findByItemIdAndInventoryType(String itemId, String inventoryType);
    
    List<GroupSharingRequest> findByUserId(Long userId);
    
    List<GroupSharingRequest> findByUserIdAndStatus(Long userId, String status);
    
    List<GroupSharingRequest> findByStatus(String status);
    
    @Query("SELECT gsr FROM GroupSharingRequest gsr " +
           "WHERE gsr.productId IN (SELECT si.productId FROM SharedInventory si WHERE si.sharedByGroupName = :groupName) " +
           "ORDER BY gsr.createdAt DESC")
    Page<GroupSharingRequest> findRequestsForSharedItemsByGroup(@Param("groupName") String groupName, Pageable pageable);
    
    @Query("SELECT gsr FROM GroupSharingRequest gsr " +
           "WHERE gsr.productId IN (SELECT si.productId FROM SharedInventory si WHERE si.sharedByGroupName = :groupName) " +
           "AND gsr.status = :status " +
           "ORDER BY gsr.createdAt DESC")
    Page<GroupSharingRequest> findRequestsForSharedItemsByGroupAndStatus(@Param("groupName") String groupName, @Param("status") String status, Pageable pageable);
    
    @Query("SELECT gsr FROM GroupSharingRequest gsr " +
           "WHERE gsr.userId = :userId " +
           "ORDER BY gsr.createdAt DESC")
    Page<GroupSharingRequest> findMyRequests(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT gsr FROM GroupSharingRequest gsr " +
           "WHERE gsr.userId = :userId " +
           "AND gsr.status = :status " +
           "ORDER BY gsr.createdAt DESC")
    Page<GroupSharingRequest> findMyRequestsByStatus(@Param("userId") Long userId, @Param("status") String status, Pageable pageable);
    
    Optional<GroupSharingRequest> findByRequestId(Long requestId);
    
    List<GroupSharingRequest> findByProductIdAndStatus(String productId, String status);
    
    List<GroupSharingRequest> findByProductId(String productId);
    
    @Query("SELECT gsr FROM GroupSharingRequest gsr " +
           "WHERE gsr.requesterGroup = :requesterGroup " +
           "AND gsr.status = :status " +
           "ORDER BY gsr.createdAt DESC")
    Page<GroupSharingRequest> findBorrowedItemsByRequesterGroupAndStatus(
        @Param("requesterGroup") String requesterGroup,
        @Param("status") String status,
        Pageable pageable
    );
}
