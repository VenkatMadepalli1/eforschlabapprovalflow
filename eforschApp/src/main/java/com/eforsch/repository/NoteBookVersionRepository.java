package com.eforsch.repository;

import com.eforsch.entity.NoteBookVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NoteBookVersionRepository extends JpaRepository<NoteBookVersion, Long> {

    Page<NoteBookVersion> findByNoteIdOrderByVersionAsc(String noteId, Pageable pageable);

    Optional<NoteBookVersion> findByNoteIdAndVersion(String noteId, int version);

    void deleteByNoteId(String noteId);

    @Query("SELECT COALESCE(MAX(v.version), 0) FROM NoteBookVersion v WHERE v.noteId = :noteId")
    int findMaxVersionByNoteId(String noteId);
}
