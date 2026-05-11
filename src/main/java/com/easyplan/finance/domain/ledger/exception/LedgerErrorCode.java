package com.easyplan.finance.domain.ledger.exception;

import com.easyplan._shared.exception.GlobalErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LedgerErrorCode implements GlobalErrorCode {
	FISCAL_OVER_DAY("회계 시작일은 1일부터 31일 사이여야 합니다."),
	
	LEDGER_NOT_FOUND("해당 가계부를 찾지 못했습니다."),
	
	LEDGER_REGISTER_FAIL("가계부 생성 중 알 수 없는 오류가 발생했습니다. 다시 시도해주세요."),
	
	LEDGER_NAME_DUPLICATE("이미 동일한 이름의 가계부를 사용하고 있습니다."),
	
	
	
	;
	private final String message;
}
