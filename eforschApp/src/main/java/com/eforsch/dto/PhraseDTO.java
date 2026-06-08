package com.eforsch.dto;

import java.time.LocalDateTime;

public class PhraseDTO {
    private Integer id;
    private String phraseCode;
    private String phraseDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PhraseDTO() {}

    public PhraseDTO(Integer id, String phraseCode, String phraseDescription) {
        this.id = id;
        this.phraseCode = phraseCode;
        this.phraseDescription = phraseDescription;
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
