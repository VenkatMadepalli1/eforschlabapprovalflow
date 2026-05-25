package com.eforsch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eforsch.entity.ProjectArchive;

public interface ProjectArchiveRepository extends JpaRepository<ProjectArchive, String> {

	Page<ProjectArchive> findAllByOrderByArchivedAtDesc(Pageable pageable);

	Page<ProjectArchive> findByUserIdOrderByArchivedAtDesc(String userId, Pageable pageable);

	Page<ProjectArchive> findByGroupNameOrderByArchivedAtDesc(String groupName, Pageable pageable);

	Page<ProjectArchive> findByUserIdAndGroupNameOrderByArchivedAtDesc(String userId, String groupName, Pageable pageable);
}