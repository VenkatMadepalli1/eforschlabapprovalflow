package com.eforsch.repository;

import com.eforsch.entity.HPhrase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HPhraseRepository extends JpaRepository<HPhrase, Integer> {
    Optional<HPhrase> findByPhraseCode(String phraseCode);
    boolean existsByPhraseCode(String phraseCode);
}
