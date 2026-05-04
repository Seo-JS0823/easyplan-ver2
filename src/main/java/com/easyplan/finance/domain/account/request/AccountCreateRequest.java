package com.easyplan.finance.domain.account.request;

import com.easyplan.finance.domain.account.AccountOptionTemplate;
import com.easyplan.finance.domain.account.AccountType;

public record AccountCreateRequest(
		String ledgerPublicId,
		AccountType accountType,
		AccountOptionTemplate optionCode,
		String accountName,
		String accountDescription) {
	
}
