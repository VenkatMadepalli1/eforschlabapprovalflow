package com.eforsch.repository;

import com.eforsch.entity.PPhrase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PPhraseRepository extends JpaRepository<PPhrase, Integer> {
    Optional<PPhrase> findByPhraseCode(String phraseCode);
    boolean existsByPhraseCode(String phraseCode);
}
