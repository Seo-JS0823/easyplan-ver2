package com.easyplan.finance.application.policy;

import org.springframework.stereotype.Component;

import com.easyplan.finance.application.required.AccountRepository;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.account.Category;
import com.easyplan.finance.domain.account.exception.AccountErrorCode;
import com.easyplan.finance.domain.account.exception.AccountException;
import com.easyplan.finance.domain.account.request.AccountRequest.AccountCreateRequest;
import com.easyplan.finance.domain.account.request.AccountRequest.AccountUpdateRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountPolicy {
	private final AccountRepository accountRepo;
	
	public void validateAccountName(Category category, String accountName) {
		if(accountRepo.existsByCategoryAndAccountName(category, accountName)) {
			throw new AccountException(AccountErrorCode.ACCOUNT_NAME_DUPLICATE);
		}
	}
	
	public void validateAccountNameForUpdate(Category category, String accountName, Long accountId) {
		if(accountRepo.existsByCategoryAndAccountNameAndIdNot(category, accountName, accountId)) {
			throw new AccountException(AccountErrorCode.ACCOUNT_NAME_DUPLICATE);
		}
	}
	
	public void validateAccountTypeMatch(AccountCreateRequest accountCreate) {
		if(!accountCreate.accountType().equals(accountCreate.option().getAccountType())) {
			throw new AccountException(AccountErrorCode.ACCOUNT_TYPE_MISMATH);
		}
	}
	
	public void validateAccountTypeMatch(AccountUpdateRequest accountUpdate, Account account) {
		AccountType a = accountUpdate.option().getAccountType();
		AccountType b = account.getCategory().getAccountType();
		
		if(!a.equals(b)) {
			throw new AccountException(AccountErrorCode.ACCOUNT_TYPE_MISMATH);
		}
	}
	
	
}
