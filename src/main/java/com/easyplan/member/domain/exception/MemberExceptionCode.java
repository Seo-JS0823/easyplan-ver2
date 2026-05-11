package com.easyplan.member.domain.exception;

import com.easyplan._shared.exception.GlobalErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberExceptionCode implements GlobalErrorCode {
	MEMBER_CANNOT_USE_SERVICE("서비스를 이용할 수 없는 회원입니다."),
	MEMBER_CANNOT_USE_SERVICE_VERIFY_EMAIL("이메일 인증이 완료되지 않아 서비스를 이용할 수 없습니다."),
	
	MEMBER_NOT_FOUND("존재하지 않는 사용자입니다."),
	
	DUPLICATE_EMAIL("이미 사용중인 이메일입니다."),
	DUPLICATE_NICKNAME("이미 사용중인 닉네임입니다."),
	
	FORBIDDEN_NICKNAME("사용이 금지된 닉네임입니다."),
	FORBIDDEN_PASSWORD_UPDATE("현재 비밀번호가 일치하지 않습니다."),
	
	VERIFY_PASSWORD_FAIL("현재 비밀번호와 일치하지 않습니다."),
	;
	private final String message;
}
