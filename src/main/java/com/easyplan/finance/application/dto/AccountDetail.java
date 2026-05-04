package com.easyplan.finance.application.dto;

import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountOption;
import com.easyplan.finance.domain.account.AccountType;

public record AccountDetail(AccountType accountType, String accountPublicId, String accountName, String accountDescription) {
	public static AccountDetail of(AccountOption option, Account account) {
		return new AccountDetail(
				option.getAccountType(),
				account.getAccountPublicId().publicId(),
				account.getAccountName(),
				account.getAccountDescription()
		);
	}
}
