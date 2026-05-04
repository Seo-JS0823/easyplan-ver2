package com.easyplan.member.domain.exception;

public class MemberException extends RuntimeException {
	public MemberException(MemberExceptionCode code) {
		super(code.getMessage());
	}
}
