package com.eforsch;

public class Column {

    private String key;
	private String label;
    private boolean sortable;
    private boolean filterable;
    
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
	public boolean isSortable() {
		return sortable;
	}
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}
	public boolean isFilterable() {
		return filterable;
	}
	public void setFilterable(boolean filterable) {
		this.filterable = filterable;
	}
	
	 public Column(String key, String label, boolean sortable, boolean filterable) {
			super();
			this.key = key;
			this.label = label;
			this.sortable = sortable;
			this.filterable = filterable;
		}
}
