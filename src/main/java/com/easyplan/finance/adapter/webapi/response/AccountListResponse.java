package com.easyplan.finance.adapter.webapi.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountType;

public record AccountListResponse(Map<AccountType, List<AccountInfo>> accountInfoList) {
	
	public static AccountListResponse of(Map<AccountType, List<Account>> accounts) {
		Map<AccountType, List<AccountInfo>> accountInfoList = accounts.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						entry -> entry.getValue().stream()
								.map(AccountInfo::of)
								.toList()
				));
		
		return new AccountListResponse(accountInfoList);
	}
	
	public static record AccountInfo(String accountPublicId, String accountName, String accountDescription) {
		public static AccountInfo of(Account account) {
			return new AccountInfo(
					account.getAccountPublicId().publicId(),
					account.getAccountName(),
					account.getAccountDescription()
			);
		}
	}
}
