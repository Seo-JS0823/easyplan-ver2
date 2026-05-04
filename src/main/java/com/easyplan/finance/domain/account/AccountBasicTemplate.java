package com.easyplan.finance.domain.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountBasicTemplate {
	ASS01(AccountType.ASSET, "현금", AccountOptionTemplate.LIQUID),
	ASS02(AccountType.ASSET, "입출금통장", AccountOptionTemplate.BANK_ACCOUNT),
	
	LIA01(AccountType.LIABILITIES, "신용카드", AccountOptionTemplate.CREDIT_CARD),
	LIA02(AccountType.LIABILITIES, "대출금", AccountOptionTemplate.CREDIT_CARD),
	
	EQU01(AccountType.EQUITY, "기초 잔액", AccountOptionTemplate.EQUITY),
	
	INC01(AccountType.INCOME, "월급", AccountOptionTemplate.VARIABLE_INCOME),
	INC02(AccountType.INCOME, "상여금", AccountOptionTemplate.VARIABLE_INCOME),
	INC03(AccountType.INCOME, "사업 소득", AccountOptionTemplate.VARIABLE_INCOME),
	INC04(AccountType.INCOME, "판매 수익", AccountOptionTemplate.VARIABLE_INCOME),
	INC05(AccountType.INCOME, "기타 수익", AccountOptionTemplate.VARIABLE_INCOME),
	
	EXP01(AccountType.EXPENSE, "식비", AccountOptionTemplate.VARIABLE_EXPENSE),
	EXP03(AccountType.EXPENSE, "월세", AccountOptionTemplate.FIXED_EXPENSE),
	EXP04(AccountType.EXPENSE, "통신비", AccountOptionTemplate.FIXED_EXPENSE),
	EXP05(AccountType.EXPENSE, "공과금", AccountOptionTemplate.FIXED_EXPENSE),
	EXP06(AccountType.EXPENSE, "구독료", AccountOptionTemplate.FIXED_EXPENSE),
	EXP07(AccountType.EXPENSE, "보험료", AccountOptionTemplate.FIXED_EXPENSE),
	EXP08(AccountType.EXPENSE, "교통비", AccountOptionTemplate.VARIABLE_EXPENSE),
	EXP09(AccountType.EXPENSE, "생활용품", AccountOptionTemplate.VARIABLE_EXPENSE),
	EXP10(AccountType.EXPENSE, "취미", AccountOptionTemplate.VARIABLE_EXPENSE),
	EXP11(AccountType.EXPENSE, "학업", AccountOptionTemplate.VARIABLE_EXPENSE),
	EXP12(AccountType.EXPENSE, "의류 및 미용", AccountOptionTemplate.VARIABLE_EXPENSE),
	EXP13(AccountType.EXPENSE, "의료 및 건강", AccountOptionTemplate.VARIABLE_EXPENSE),
	EXP14(AccountType.EXPENSE, "이자", AccountOptionTemplate.VARIABLE_EXPENSE),
	
	;
	private final AccountType accountType;
	
	private final String accountName;
	
	private final AccountOptionTemplate accountOptionTemplate;
}
