package com.eforsch.repository;

import com.eforsch.entity.NoteBookPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteBookPermissionRepository extends JpaRepository<NoteBookPermission, Long> {

    Page<NoteBookPermission> findByNoteIdOrderByGrantedAtDesc(String noteId, Pageable pageable);
}
