package com.easyplan.finance.application.required;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountStatus;

public interface AccountRepository extends JpaRepository<Account, Long> {
	List<Account> findByLedgerId(Long ledgerId);
	
	boolean existsByCategoryIdAndAccountName(Long categoryId, String accountName);
	
	Account findByAccountPublicId(PublicId accountPublicId);

	boolean existsByLedgerIdAndAccountPublicId(Long ledgerId, PublicId accountPublicId);

	List<Account> findByLedgerIdAndStatus(Long ledgerId, AccountStatus active);
	
	Account findByAccountPublicIdAndStatus(PublicId accountPublicId, AccountStatus active);
}
