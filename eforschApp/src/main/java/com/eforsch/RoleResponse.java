package com.eforsch;

public class RoleResponse {
    private String key;
    private String label;
    private String description;

    public RoleResponse(String key, String label, String description) {
        this.key = key;
        this.label = label;
        this.description = description;
    }

    // Getters and setters
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}