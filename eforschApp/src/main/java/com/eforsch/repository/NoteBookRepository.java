package com.eforsch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.eforsch.entity.NoteBook;

@Repository
public interface NoteBookRepository extends JpaRepository<NoteBook, String>, JpaSpecificationExecutor<NoteBook> {
	boolean existsByNoteId(String noteId);

	Optional<NoteBook> findById(String noteId);

}
