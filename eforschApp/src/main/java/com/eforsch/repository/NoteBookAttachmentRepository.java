package com.eforsch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.NoteBookAttachment;

@Repository
public interface NoteBookAttachmentRepository extends JpaRepository<NoteBookAttachment, Long> {

	List<NoteBookAttachment> findByNoteIdOrderByAttachmentIdAsc(String noteId);

	Optional<NoteBookAttachment> findByNoteIdAndFileName(String noteId, String fileName);

	void deleteByNoteId(String noteId);
}
