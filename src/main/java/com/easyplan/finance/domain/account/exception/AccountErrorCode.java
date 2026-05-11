package com.easyplan.finance.domain.account.exception;

import com.easyplan._shared.exception.GlobalErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountErrorCode implements GlobalErrorCode {
	ACCOUNT_TYPE_MISMATH("계정 그룹에서 지원하지 않는 계정 옵션입니다. 다시 시도해주세요."),
	
	CATEGORY_MAPPING_ERROR("기본 카테고리 데이터 매핑 실패"),
	
	ACCOUNT_OPTION_MAPPING_ERROR("계정 옵션 마스터 데이터 매핑 실패"),
	
	ACCOUNT_NOT_FOUND("계정 항목을 찾을 수 없습니다."),
	
	ACCOUNT_DEACTIVATE("삭제된 계정입니다."),
	
	ACCOUNT_NAME_DUPLICATE("같은 그룹에 동일한 이름의 계정 항목이 존재합니다."),
	
	;
	private final String message;
}
