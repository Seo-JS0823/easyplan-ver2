package com.easyplan.auth.domain.exception;

public class AuthException extends RuntimeException {
	public AuthException(AuthExceptionCode code) {
		super(code.getMessage());
	}
}
