package com.easyplan.finance.domain.account.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountExceptionCode {
	ACCOUNT_NOT_ACTIVE("비활성화된 계정입니다."),
	
	CATEGORY_NOT_FOUND("계정 그룹 정보를 찾을 수 없습니다. 관리자에게 문의하여주세요."),
	
	CATEGORY_WITHIN_ACCOUNT_NAME_DUPLICATE("같은 유형의 계정 항목중에 동일한 이름이 이미 존재합니다."),
	
	ACCOUNT_NOT_FOUND("해당 계정 항목을 찾을 수 없습니다."),
	
	ACCOUNT_NOT_SUPPORTED_OPTION("해당 계정에서 사용할 수 없은 계정 옵션입니다. 다시 선택해주세요."),
	
	
	;
	private final String message;
}
