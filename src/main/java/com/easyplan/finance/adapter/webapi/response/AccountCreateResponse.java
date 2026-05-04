package com.easyplan.finance.adapter.webapi.response;

import com.easyplan.finance.domain.account.Account;

public record AccountCreateResponse(String accountName, String accountDescription) {
	public static AccountCreateResponse of(Account account) {
		return new AccountCreateResponse(account.getAccountName(), account.getAccountDescription());
	}
}
