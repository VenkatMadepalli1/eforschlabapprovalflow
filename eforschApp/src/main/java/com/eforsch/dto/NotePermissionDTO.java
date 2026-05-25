package com.eforsch.dto;

import java.time.Instant;

public class NotePermissionDTO {

    private String userId;
    private String accessLevel;
    private Instant grantedAt;

    public NotePermissionDTO() {
    }

    public NotePermissionDTO(String userId, String accessLevel, Instant grantedAt) {
        this.userId = userId;
        this.accessLevel = accessLevel;
        this.grantedAt = grantedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public Instant getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(Instant grantedAt) {
        this.grantedAt = grantedAt;
    }
}
