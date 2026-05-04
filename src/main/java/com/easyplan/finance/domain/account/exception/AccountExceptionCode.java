package com.easyplan.finance.domain.account.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountExceptionCode {
	ACCOUNT_NOT_ACTIVE("비활성화된 계정입니다."),
	;
	private final String message;
}
