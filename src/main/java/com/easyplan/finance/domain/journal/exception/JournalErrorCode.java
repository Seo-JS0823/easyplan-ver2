package com.easyplan.finance.domain.journal.exception;

import com.easyplan._shared.exception.GlobalErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JournalErrorCode implements GlobalErrorCode {
	AMOUNT_ZERO_ERROR("거래 금액은 0보다 큰 금액이어야 합니다."),
	
	INVALID_ENTRY_COUNT("거래는 차변과 대변 각각 1개로 구성되어야 합니다."),
	
	INVALID_ENTRY_PAIR("차변과 대변의 계정 구성이 올바르지 않습니다."),
	
	INVALID_ENTRY_AMOUNT("차변과 대변의 금액은 항상 같아야 합니다."),
	
	INVALID_JOURNAL_AMOUNT("거래 금액과 분개의 금액이 일치하지 않습니다."),
	
	JOURNAL_SYSTEM_ERROR("거래 처리중 오류가 발생하였습니다. 관리자에게 문의해주세요."),
	
	;
	private final String message;
}
