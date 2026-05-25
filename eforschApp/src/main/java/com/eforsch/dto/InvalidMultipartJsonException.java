package com.eforsch.dto;

public class InvalidMultipartJsonException extends RuntimeException {

	public InvalidMultipartJsonException(String message, Throwable cause) {
		super(message, cause);
	}
}
