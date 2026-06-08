package com.eforsch.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "h_phrase")
public class HPhrase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "phrase_code", nullable = false, unique = true, length = 20)
    private String phraseCode;

    @Column(name = "phrase_description", nullable = false, length = 500)
    private String phraseDescription;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public HPhrase() {}

    public HPhrase(String phraseCode, String phraseDescription) {
        this.phraseCode = phraseCode;
        this.phraseDescription = phraseDescription;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getPhraseCode() { return phraseCode; }
    public void setPhraseCode(String phraseCode) { this.phraseCode = phraseCode; }
    public String getPhraseDescription() { return phraseDescription; }
    public void setPhraseDescription(String phraseDescription) { this.phraseDescription = phraseDescription; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
