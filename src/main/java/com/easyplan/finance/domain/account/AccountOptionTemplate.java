package com.easyplan.finance.domain.account;

import java.util.List;

import io.jsonwebtoken.lang.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountOptionTemplate {
	EQUITY("자본", AccountType.EQUITY),
	
	LIQUID("유동성 자산", AccountType.ASSET),
	SAVINGS("예적금", AccountType.ASSET),
	BANK_ACCOUNT("보통예금", AccountType.ASSET),
	RECEIVABLE("미수금", AccountType.ASSET),
	
	CREDIT_CARD("신용카드", AccountType.LIABILITIES),
	LOAN("대출금", AccountType.LIABILITIES),
	OVERDRAFT("마이너스 통장", AccountType.LIABILITIES),
	
	VARIABLE_INCOME("유동 수익", AccountType.INCOME),
	FIXED_INCOME("고정 수익", AccountType.INCOME),
	
	VARIABLE_EXPENSE("유동 비용", AccountType.EXPENSE),
	FIXED_EXPENSE("고정 비용", AccountType.EXPENSE),
	
	;
	private final String optionName;
	
	private final AccountType accountType;
	
	public static List<AccountOption> defaultOptions() {
		return Arrays.asList(AccountOptionTemplate.values()).stream()
				.map(option -> AccountOption.create(option))
				.toList();
	}
}
