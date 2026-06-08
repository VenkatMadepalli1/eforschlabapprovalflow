package com.eforsch.controller;

import com.eforsch.dto.PhraseDTO;
import com.eforsch.service.PPhraseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pphrases")
public class PPhraseController {

    @Autowired
    private PPhraseService pPhraseService;

    @GetMapping("/getAll")
    public ResponseEntity<List<PhraseDTO>> getAll() {
        return ResponseEntity.ok(pPhraseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhraseDTO> getById(@PathVariable Integer id) {
        PhraseDTO dto = pPhraseService.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<PhraseDTO> create(@RequestBody PhraseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pPhraseService.create(dto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PhraseDTO> update(@PathVariable Integer id, @RequestBody PhraseDTO dto) {
        try {
            return ResponseEntity.ok(pPhraseService.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            pPhraseService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
