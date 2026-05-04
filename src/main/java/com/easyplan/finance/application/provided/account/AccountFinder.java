package com.easyplan.finance.application.provided.account;

import java.util.List;

import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountCategory;

public interface AccountFinder {
	List<Account> findByLedgerId(Long ledgerId);
	
	List<AccountCategory> findCategoryByLedgerId(Long ledgerId);
}
