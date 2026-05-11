package com.easyplan.member.domain.exception;

import com.easyplan._shared.exception.GlobalException;

public class MemberException extends GlobalException {
	
	public MemberException(MemberExceptionCode code) {
		super(code);
	}
}
