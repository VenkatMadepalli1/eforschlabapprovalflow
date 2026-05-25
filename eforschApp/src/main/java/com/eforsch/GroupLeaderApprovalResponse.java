package com.eforsch;

public class GroupLeaderApprovalResponse {
    private String status;
    private String message;
    private String userStatus;

    public GroupLeaderApprovalResponse(String status, String message, String userStatus) {
        this.status = status;
        this.message = message;
        this.userStatus = userStatus;
    }

       // Getters and setters
		public String getStatus() {
        return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getUserStatus() {
			return userStatus;
		}

		public void setUserStatus(String userStatus) {
			this.userStatus = userStatus;
		}
		// Constructors
}
