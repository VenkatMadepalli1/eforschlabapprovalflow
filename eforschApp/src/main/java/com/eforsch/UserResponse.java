package com.eforsch;

import java.util.List;

public class UserResponse {
    private String status;
    private List<UserInfo> users;

    public UserResponse(String status, List<UserInfo> users) {
        this.status = status;
        this.users = users;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }

    public static class UserInfo {
        private Long userId;
        private String name;
        private String email;
        private String status;

        public UserInfo(Long long1, String name, String email, String status) {
            this.userId = long1;
            this.name = name;
            this.email = email;
            this.status = status;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
