package com.easyplan.finance.application.provided;

import java.util.Map;

import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.journal.Journal;
import com.easyplan.finance.domain.journal.request.JournalRequest.JournalCreateRequest;
import com.easyplan.finance.domain.journal.request.JournalRequest.JournalUpdateRequest;
import com.easyplan.finance.domain.ledger.Ledger;

public interface JournalCommand {
	Journal createJournal(Ledger ledger, Map<EntrySide, Account> accountMap, JournalCreateRequest journalCreate);
	
	Journal updateJournal(Ledger ledger, Map<EntrySide, Account> accountMap, JournalUpdateRequest journalUpdate);
	
	void deleteJournal(Ledger ledger, Long journalId);
}
