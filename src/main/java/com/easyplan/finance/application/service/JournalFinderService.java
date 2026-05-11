package com.easyplan.finance.application.service;

import org.springframework.stereotype.Service;

import com.easyplan.finance.application.provided.JournalFinder;
import com.easyplan.finance.application.required.JournalRepository;
import com.easyplan.finance.domain.journal.Journal;
import com.easyplan.finance.domain.journal.exception.JournalErrorCode;
import com.easyplan.finance.domain.journal.exception.JournalException;
import com.easyplan.finance.domain.ledger.Ledger;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalFinderService implements JournalFinder {
	
	private final JournalRepository journalRepo;
	
	@Override
	public Journal findJournal(Ledger ledger, Long id) {
		return journalRepo.findByLedgerAndId(ledger, id)
				.orElseThrow(() -> new JournalException(JournalErrorCode.JOURNAL_NOT_FOUND));
	}

}
