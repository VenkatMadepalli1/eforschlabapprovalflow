package com.eforsch.service;

import com.eforsch.dto.PhraseDTO;
import com.eforsch.entity.HPhrase;
import com.eforsch.repository.HPhraseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HPhraseService {

    @Autowired
    private HPhraseRepository hPhraseRepository;

    private PhraseDTO toDTO(HPhrase entity) {
        PhraseDTO dto = new PhraseDTO();
        dto.setId(entity.getId());
        dto.setPhraseCode(entity.getPhraseCode());
        dto.setPhraseDescription(entity.getPhraseDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public List<PhraseDTO> getAll() {
        return hPhraseRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PhraseDTO getById(Integer id) {
        return hPhraseRepository.findById(id).map(this::toDTO).orElse(null);
    }

    @Transactional
    public PhraseDTO create(PhraseDTO dto) {
        HPhrase entity = new HPhrase();
        entity.setPhraseCode(dto.getPhraseCode().toUpperCase());
        entity.setPhraseDescription(dto.getPhraseDescription());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return toDTO(hPhraseRepository.save(entity));
    }

    @Transactional
    public PhraseDTO update(Integer id, PhraseDTO dto) {
        HPhrase entity = hPhraseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("H-Phrase not found with id: " + id));
        entity.setPhraseCode(dto.getPhraseCode().toUpperCase());
        entity.setPhraseDescription(dto.getPhraseDescription());
        entity.setUpdatedAt(LocalDateTime.now());
        return toDTO(hPhraseRepository.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        if (!hPhraseRepository.existsById(id)) {
            throw new RuntimeException("H-Phrase not found with id: " + id);
        }
        hPhraseRepository.deleteById(id);
    }
}
