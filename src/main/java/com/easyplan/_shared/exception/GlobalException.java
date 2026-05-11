package com.easyplan._shared.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
	
	private GlobalErrorCode error;
	
	public GlobalException(GlobalErrorCode error) {
		super(error.getMessage());
	}
	
}
