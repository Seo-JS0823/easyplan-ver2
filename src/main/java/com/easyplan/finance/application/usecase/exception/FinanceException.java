package com.easyplan.finance.application.usecase.exception;

import com.easyplan._shared.exception.GlobalException;

public class FinanceException extends GlobalException {

	public FinanceException(FinanceErrorCode error) {
		super(error);
	}

}
