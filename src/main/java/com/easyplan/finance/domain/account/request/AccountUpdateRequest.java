package com.easyplan.finance.domain.account.request;

import com.easyplan.finance.domain.account.AccountOptionTemplate;

public class AccountUpdateRequest {
	public record AccountInfoUpdate(String accountName, String accountDescription, AccountOptionTemplate optionCode) {}
}
