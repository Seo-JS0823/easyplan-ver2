package com.easyplan.auth.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthExceptionCode {
	LOGIN_FAIL("아이디와 비밀번호가 일치하지 않습니다."),
	REFRESH_TOKEN_EMPTY("refresh token이 없습니다."),
	REFRESH_TOKEN_NOT_FOUND("유효한 인증 세션을 찾을 수 없습니다."),
	REFRESH_TOKEN_EXPIRED("refresh token이 만료되었습니다."),
	REFRESH_TOKEN_MISMATCH("권한 인증에 실패했습니다.")
	
	;
	private final String message;
}
