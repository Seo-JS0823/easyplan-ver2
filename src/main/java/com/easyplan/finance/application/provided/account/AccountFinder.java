package com.easyplan.finance.application.provided.account;

import java.util.List;
import java.util.Map;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.dto.AccountDetail;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountCategory;
import com.easyplan.finance.domain.account.AccountType;

public interface AccountFinder {
	Map<AccountType, List<Account>> findByLedgerPublicId(PublicId memberPublicId, PublicId ledgerPublicId);
	
	List<Account> findByLedgerId(Long ledgerId);
	
	List<AccountCategory> findCategoryByLedgerId(Long ledgerId);
	
	Map<AccountType, List<Account>> findByLedgerIdAndDeactivate(PublicId memberPublicId, PublicId ledgerPublicId);
	
	boolean existsByCategoryIdAndAccountName(Long categoryId, String accountName);
	
	Account findByAccountPublicId(PublicId accountPublicId);
	
	AccountDetail findOneByAccountPublicId(PublicId memberPublicId, PublicId accountPublicId);

	boolean existsByLedgerIdAndAccountPublicId(Long ledgerId, PublicId accountPublicId);
}
