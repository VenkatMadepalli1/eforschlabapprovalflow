package com.eforsch.controller;

import com.eforsch.dto.PhraseDTO;
import com.eforsch.service.HPhraseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hphrases")
public class HPhraseController {

    @Autowired
    private HPhraseService hPhraseService;

    @GetMapping("/getAll")
    public ResponseEntity<List<PhraseDTO>> getAll() {
        return ResponseEntity.ok(hPhraseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhraseDTO> getById(@PathVariable Integer id) {
        PhraseDTO dto = hPhraseService.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<PhraseDTO> create(@RequestBody PhraseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hPhraseService.create(dto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PhraseDTO> update(@PathVariable Integer id, @RequestBody PhraseDTO dto) {
        try {
            return ResponseEntity.ok(hPhraseService.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            hPhraseService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
