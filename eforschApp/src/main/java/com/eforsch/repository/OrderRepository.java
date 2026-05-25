package com.eforsch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAll(Pageable pageable);
    Page<Order> findByGroupName(String groupName, Pageable pageable);
    Page<Order> findByGroupNameAndLabApproved(String groupName, boolean approved,  Pageable pageable);
    // write a method fetch the order by status
    Page<Order> findByStatus(String status, Pageable pageable);
    // write a method to fetch the order by Approved
    Page<Order> findByLabApproved(boolean approved, Pageable pageable);
    Page<Order> findByAdminApproved(boolean approved, Pageable pageable);
    	
}
 