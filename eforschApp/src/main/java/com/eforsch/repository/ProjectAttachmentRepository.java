package com.eforsch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.ProjectAttachment;

@Repository
public interface ProjectAttachmentRepository extends JpaRepository<ProjectAttachment, Long> {

	List<ProjectAttachment> findByProjectIdOrderByAttachmentIdAsc(String projectId);

	Optional<ProjectAttachment> findByProjectIdAndFileName(String projectId, String fileName);

	void deleteByProjectId(String projectId);
}