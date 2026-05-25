package com.eforsch.util;

import java.util.List;

public class GroupHierarchyResponse {
    private String status;
    private String role;
    private List<GroupData> data;

    public GroupHierarchyResponse(String status, String role, List<GroupData> data) {
        this.status = status;
        this.role = role;
        this.data = data;
    }

    public GroupHierarchyResponse() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<GroupData> getData() {
        return data;
    }

    public void setData(List<GroupData> data) {
        this.data = data;
    }

    public static class GroupData {
        private String groupName;
        private GroupLeader groupLeader;
        private List<Member> members;

        public GroupData() {}

        public GroupData(String groupName, GroupLeader groupLeader, List<Member> members) {
            this.groupName = groupName;
            this.groupLeader = groupLeader;
            this.members = members;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public GroupLeader getGroupLeader() {
            return groupLeader;
        }

        public void setGroupLeader(GroupLeader groupLeader) {
            this.groupLeader = groupLeader;
        }

        public List<Member> getMembers() {
            return members;
        }

        public void setMembers(List<Member> members) {
            this.members = members;
        }
    }

    public static class GroupLeader {
        private String userId;
        private String name;
        private String email;

        public GroupLeader() {}

        public GroupLeader(String userId, String name, String email) {
            this.userId = userId;
            this.name = name;
            this.email = email;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
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
    }

    public static class Member {
        private String userId;
        private String name;
        private String email;
        private String role;
        private String status;

        public Member() {}

        public Member(String userId, String name, String email, String role, String status) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.role = role;
            this.status = status;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
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

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
