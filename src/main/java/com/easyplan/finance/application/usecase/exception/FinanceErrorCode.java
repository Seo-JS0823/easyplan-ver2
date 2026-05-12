package com.easyplan.finance.application.usecase.exception;

import com.easyplan._shared.exception.GlobalErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FinanceErrorCode implements GlobalErrorCode {
	INVALID_JOURNAL_ENTRY_COUNT("거래 입력은 차변과 대변에 각각 하나씩 계정을 선택해야 합니다."),
	;
	private final String message;
}
