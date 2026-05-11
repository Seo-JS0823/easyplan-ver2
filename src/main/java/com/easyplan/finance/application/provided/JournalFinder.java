package com.easyplan.finance.application.provided;

import com.easyplan.finance.domain.journal.Journal;

public interface JournalFinder {
	Journal findByJournal(Long id);
}
