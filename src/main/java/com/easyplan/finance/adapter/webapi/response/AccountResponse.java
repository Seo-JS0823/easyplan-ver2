package com.easyplan.finance.adapter.webapi.response;

import com.easyplan.finance.application.dto.AccountDetail;
import com.easyplan.finance.domain.account.AccountType;

public record AccountResponse(AccountType accountType, String accountPublicId, String accountName, String accountDescription) {
	public static AccountResponse of(AccountDetail account) {
		return new AccountResponse(
				account.accountType(),
				account.accountPublicId(),
				account.accountName(),
				account.accountDescription()
		);
	}
}
