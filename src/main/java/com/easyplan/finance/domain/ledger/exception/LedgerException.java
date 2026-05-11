package com.easyplan.finance.domain.ledger.exception;

import com.easyplan._shared.exception.GlobalErrorCode;
import com.easyplan._shared.exception.GlobalException;

public class LedgerException extends GlobalException {

	public LedgerException(GlobalErrorCode error) {
		super(error);
	}

}
