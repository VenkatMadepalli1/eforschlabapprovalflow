package com.eforsch;

public class ApiResponse {
    private String status;
    private String message;
    private int code;
    private Errors errors;
    private Object data;
    
 // Constructor
    public ApiResponse(int code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
    
	public ApiResponse() {
		super();
	}
	public ApiResponse(String status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
	public Errors getErrors() {
		return errors;
	}
	public void setErrors(Errors errors) {
		this.errors = errors;
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
	public Object getData() {
	    return data;
	}

	public void setData(Object data) {
	    this.data = data;
	}

    // Getters and Setters
}



