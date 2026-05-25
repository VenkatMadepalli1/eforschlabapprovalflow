package com.eforsch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.NoteBookArchiveAttachment;

@Repository
public interface NoteBookArchiveAttachmentRepository extends JpaRepository<NoteBookArchiveAttachment, Long> {

	List<NoteBookArchiveAttachment> findByNoteIdOrderByAttachmentIdAsc(String noteId);
}