package com.easyplan.finance.domain.budget.exception;

import com.easyplan._shared.exception.GlobalException;

public class BudgetException extends GlobalException {

	public BudgetException(BudgetErrorCode error) {
		super(error);
	}
	
}
