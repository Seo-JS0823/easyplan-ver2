package com.easyplan.finance.domain.account.exception;

public class AccountException extends RuntimeException {
	public AccountException(AccountExceptionCode code) {
		super(code.getMessage());
	}
}
