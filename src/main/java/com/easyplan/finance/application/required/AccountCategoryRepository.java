package com.easyplan.finance.application.required;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan.finance.domain.account.AccountCategory;
import com.easyplan.finance.domain.account.AccountType;

public interface AccountCategoryRepository extends JpaRepository<AccountCategory, Long> {
	List<AccountCategory> findByLedgerId(Long ledgerId);
	
	AccountCategory findByLedgerIdAndAccountType(Long ledgerId, AccountType accountType);
}
