package com.eforsch.dto;

public class NoteContentDTO {
    private String html;
    private String plainText;

    public NoteContentDTO() {}

    public NoteContentDTO(String html, String plainText) {
        this.html = html;
        this.plainText = plainText;
    }

    public String getHtml() { return html; }
    public void setHtml(String html) { this.html = html; }

    public String getPlainText() { return plainText; }
    public void setPlainText(String plainText) { this.plainText = plainText; }
}
