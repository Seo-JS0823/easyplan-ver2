package com.easyplan.finance.domain.account.exception;

import com.easyplan._shared.exception.GlobalErrorCode;
import com.easyplan._shared.exception.GlobalException;

public class AccountException extends GlobalException {

	public AccountException(GlobalErrorCode error) {
		super(error);
	}

}
