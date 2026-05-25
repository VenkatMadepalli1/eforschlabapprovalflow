package com.eforsch.repository;

import com.eforsch.entity.NoteBookArchive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteBookArchiveRepository extends JpaRepository<NoteBookArchive, String> {

    Page<NoteBookArchive> findAllByOrderByArchivedAtDesc(Pageable pageable);

    Page<NoteBookArchive> findByCreatedByUserIdOrderByArchivedAtDesc(String createdByUserId, Pageable pageable);

    Page<NoteBookArchive> findByGroupNameOrderByArchivedAtDesc(String groupName, Pageable pageable);

    Page<NoteBookArchive> findByCreatedByUserIdAndGroupNameOrderByArchivedAtDesc(String createdByUserId, String groupName, Pageable pageable);
}
