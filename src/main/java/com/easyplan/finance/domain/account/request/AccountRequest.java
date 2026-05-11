package com.easyplan.finance.domain.account.request;

import com.easyplan.finance.domain.account.AccountOptionTemplate;
import com.easyplan.finance.domain.account.AccountType;

public class AccountRequest {
	public record AccountCreateRequest(AccountType accountType, String accountName, String accountDescription, AccountOptionTemplate option) {}
	
	public record AccountUpdateRequest(String accountName, String accountDescription, AccountOptionTemplate option) {}
}
