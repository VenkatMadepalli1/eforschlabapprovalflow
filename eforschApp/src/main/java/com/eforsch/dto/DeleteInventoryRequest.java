package com.eforsch.dto;

public class DeleteInventoryRequest {

	private Long produID;
	private String inventoryType; // e.g., "FineChemicalInventory", "ArchiveInventory"
	private User user;

	public Long getProduID() {
		return produID;
	}

	public void setProduID(Long produID) {
		this.produID = produID;
	}

	public String getInventoryType() {
		return inventoryType;
	}

	public void setInventoryType(String inventoryType) {
		this.inventoryType = inventoryType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}