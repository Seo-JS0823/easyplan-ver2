package com.easyplan.application.finance;

import java.time.LocalDate;
import java.util.Map;

import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountOptionTemplate;
import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.account.request.AccountRequest.AccountCreateRequest;
import com.easyplan.finance.domain.journal.TransactionType;
import com.easyplan.finance.domain.journal.request.JournalRequest.JournalCreateRequest;

public class FinanceFix {
	
	public static AccountCreateRequest assetAccountCreateRequest() {
		return new AccountCreateRequest(
				AccountType.ASSET,
				"자산계정",
				"메모",
				AccountOptionTemplate.BANK_ACCOUNT
		);
	}
	
	public static AccountCreateRequest assetAccountCreateRequest(String accountName) {
		return new AccountCreateRequest(
				AccountType.ASSET,
				accountName,
				"메모",
				AccountOptionTemplate.BANK_ACCOUNT
				);
	}
	
	public static AccountCreateRequest expenseAccountCreateRequest() {
		return new AccountCreateRequest(
				AccountType.EXPENSE,
				"지출계정",
				"메모",
				AccountOptionTemplate.VARIABLE_EXPENSE
		);
	}
	
	public static AccountCreateRequest incomeAccountCreateRequest() {
		return new AccountCreateRequest(
				AccountType.INCOME,
				"수입계정",
				"메모",
				AccountOptionTemplate.VARIABLE_INCOME
		);
	}
	
	public static AccountCreateRequest liabilitiesAccountCreateRequest() {
		return new AccountCreateRequest(
				AccountType.LIABILITIES,
				"부채계정",
				"메모",
				AccountOptionTemplate.CREDIT_CARD
		);
	}
	
	public static JournalCreateRequest journalCreate(TransactionType type, Account debit, Account credit) {
		return new JournalCreateRequest(
				LocalDate.of(2026, 5, 1),
				100000L,
				"거래 입력 테스트",
				type,
				Map.of(
						EntrySide.DEBIT, debit.getAccountPublicId().publicId(),
						EntrySide.CREDIT, credit.getAccountPublicId().publicId()
				)
		);
	}
}
