package com.eforsch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eforsch.entity.ProjectArchiveAttachment;

public interface ProjectArchiveAttachmentRepository extends JpaRepository<ProjectArchiveAttachment, Long> {

	List<ProjectArchiveAttachment> findByProjectIdOrderByAttachmentIdAsc(String projectId);
}