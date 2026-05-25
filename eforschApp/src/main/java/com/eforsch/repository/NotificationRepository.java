package com.eforsch.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.NotificationEntity;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
	Optional<NotificationEntity> findByEntityId(Long entityId);
	
	List<NotificationEntity> findByGroupName(String groupName);
	
	List<NotificationEntity> findByGroupNameAndRole(String groupName, String role);
	
	List<NotificationEntity> findByRole(String role);
	
}
