package com.easyplan.finance.domain.account;

import com.easyplan.finance.domain.EntrySide;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountType {
	ASSET(EntrySide.DEBIT, "자산"),
	LIABILITIES(EntrySide.CREDIT, "부채"),
	EQUITY(EntrySide.CREDIT, "기초 자산"),
	INCOME(EntrySide.CREDIT, "수입"),
	EXPENSE(EntrySide.DEBIT, "지출")
	;
	
	private final EntrySide side;
	
	private final String categoryName;
}
