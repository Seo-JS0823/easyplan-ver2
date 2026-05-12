package com.easyplan.finance.application.usecase.response.command;

import com.easyplan.finance.domain.account.Account;

public class AccountResponse {
	public record AccountCreateResponse(String accountPublicId, String accountName, String accountDescription, String categoryName, String optionName) {
		public static AccountCreateResponse of(Account account) {
			return new AccountCreateResponse(
					account.getAccountPublicId().publicId(),
					account.getAccountName(),
					account.getAccountDescription(),
					account.getCategory().getCategoryName(),
					account.getOption().getOptionName()
			);
		}
	}
	
	public record AccountUpdateResponse(String accountName, String accountDescription, String optionName) {
		public static AccountUpdateResponse of(Account account) {
			return new AccountUpdateResponse(
					account.getAccountName(),
					account.getAccountDescription(),
					account.getOption().getOptionName()
			);
		}
	}
}
