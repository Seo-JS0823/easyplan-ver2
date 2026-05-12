package com.easyplan.finance.application.provided;

import com.easyplan.finance.domain.journal.Journal;
import com.easyplan.finance.domain.ledger.Ledger;

public interface JournalFinder {
	Journal findJournal(Ledger ledger, Long id);
	
	Journal findWithDetail(Ledger ledger, Long id);
}
