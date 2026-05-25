package com.eforsch;

import java.util.List;

import com.eforsch.dto.PaginationMeta;
import com.eforsch.dto.ProjectVO;

public class ProjectResponse {

	private int code;
	private String status;
	private String message;

	private List<ProjectVO> data;

	private PaginationMeta pagination;

	private List<Column> columns;

	public ProjectResponse(int code, String status, String message, List<ProjectVO> data, PaginationMeta pagination,
			List<Column> columns) {
		this.code = code;
		this.status = status;
		this.message = message;
		this.data = data;
		this.pagination = pagination;
		this.columns = columns;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

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

	public List<ProjectVO> getData() {
		return data;
	}

	public void setData(List<ProjectVO> data) {
		this.data = data;
	}

	public PaginationMeta getPagination() {
		return pagination;
	}

	public void setPagination(PaginationMeta pagination) {
		this.pagination = pagination;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

}
