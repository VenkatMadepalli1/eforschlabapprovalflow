package com.eforsch.service;

import com.eforsch.dto.PhraseDTO;
import com.eforsch.entity.PPhrase;
import com.eforsch.repository.PPhraseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PPhraseService {

    @Autowired
    private PPhraseRepository pPhraseRepository;

    private PhraseDTO toDTO(PPhrase entity) {
        PhraseDTO dto = new PhraseDTO();
        dto.setId(entity.getId());
        dto.setPhraseCode(entity.getPhraseCode());
        dto.setPhraseDescription(entity.getPhraseDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public List<PhraseDTO> getAll() {
        return pPhraseRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PhraseDTO getById(Integer id) {
        return pPhraseRepository.findById(id).map(this::toDTO).orElse(null);
    }

    @Transactional
    public PhraseDTO create(PhraseDTO dto) {
        PPhrase entity = new PPhrase();
        entity.setPhraseCode(dto.getPhraseCode().toUpperCase());
        entity.setPhraseDescription(dto.getPhraseDescription());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return toDTO(pPhraseRepository.save(entity));
    }

    @Transactional
    public PhraseDTO update(Integer id, PhraseDTO dto) {
        PPhrase entity = pPhraseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("P-Phrase not found with id: " + id));
        entity.setPhraseCode(dto.getPhraseCode().toUpperCase());
        entity.setPhraseDescription(dto.getPhraseDescription());
        entity.setUpdatedAt(LocalDateTime.now());
        return toDTO(pPhraseRepository.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        if (!pPhraseRepository.existsById(id)) {
            throw new RuntimeException("P-Phrase not found with id: " + id);
        }
        pPhraseRepository.deleteById(id);
    }
}
