package com.easyplan.finance.domain.journal.exception;

import com.easyplan._shared.exception.GlobalException;

public class JournalException extends GlobalException {

	public JournalException(JournalErrorCode error) {
		super(error);
	}

}
