package com.easyplan.finance.application.provided;

import java.util.List;
import java.util.Map;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.ledger.Ledger;

public interface AccountFinder {
	Account findAccount(Ledger ledger, PublicId accountPublicId);
	
	/**
	 * Account & Category Fetch Join Select
	 */
	Account findActiveAccount(Ledger ledger, PublicId accountPublicId);
	
	/**
	 * 
	 */
	Map<EntrySide, Account> findAccountFromJournal(Ledger ledger, Map<EntrySide, String> entries);
	
	List<Account> findByLedger(Ledger ledger);
}
