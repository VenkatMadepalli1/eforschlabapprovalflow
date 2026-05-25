package com.eforsch.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.eforsch.entity.GroupEntity;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    GroupEntity findByGroupName(String groupName);
}
