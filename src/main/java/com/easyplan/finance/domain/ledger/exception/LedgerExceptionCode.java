package com.easyplan.finance.domain.ledger.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LedgerExceptionCode {
	MEMBER_NOT_VERIFIED("이메일 인증이 완료되지 않아 가계부를 생성할 수 없습니다. 인증을 진행해주세요."),
	
	LEDGER_LIMIT_EXCEED("가계부 생성 한도가 초과되었습니다."),
	
	LEDGER_NOT_ACTIVE("사용 중지된 가계부입니다. 활성화후 다시 시도해주세요."),
	
	LEDGER_NOT_FOUND("가계부를 찾을 수 없습니다."),
	
	LEDGER_NAME_DUPLICATE("동일한 이름의 가계부가 이미 존재합니다."),
	;
	private final String message;
}
