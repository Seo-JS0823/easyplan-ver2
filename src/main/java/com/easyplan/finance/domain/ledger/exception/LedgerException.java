package com.easyplan.finance.domain.ledger.exception;

public class LedgerException extends RuntimeException {
	public LedgerException(LedgerExceptionCode code) {
		super(code.getMessage());
	}
}
