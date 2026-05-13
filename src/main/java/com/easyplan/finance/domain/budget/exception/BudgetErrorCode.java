package com.easyplan.finance.domain.budget.exception;

import com.easyplan._shared.exception.GlobalErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BudgetErrorCode implements GlobalErrorCode {
	INVALID_BUDGET_TARGET("예산은 지출 계정에만 설정이 가능합니다."),
	
	INVALID_BUDGET_LIMIT_AMOUNT("예산을 0원보다 적게 설정할 수 없습니다."),
	
	BUDGET_NOT_FOUND("예산 데이터를 찾을 수 없습니다."),
	
	;
	private final String message;
}
