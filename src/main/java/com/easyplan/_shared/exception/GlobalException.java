package com.easyplan._shared.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
	
	private final GlobalErrorCode error;
	
	public GlobalException(GlobalErrorCode error) {
		super(error.getMessage());
		this.error = error;
	}
	
}
