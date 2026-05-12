package com.easyplan.finance.application.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.easyplan.finance.application.provided.JournalCommand;
import com.easyplan.finance.application.provided.JournalFinder;
import com.easyplan.finance.application.required.repository.EntryLineRepository;
import com.easyplan.finance.application.required.repository.JournalRepository;
import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.journal.EntryLine;
import com.easyplan.finance.domain.journal.Journal;
import com.easyplan.finance.domain.journal.request.JournalRequest.JournalCreateRequest;
import com.easyplan.finance.domain.journal.request.JournalRequest.JournalUpdateRequest;
import com.easyplan.finance.domain.ledger.Ledger;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalCommandService implements JournalCommand {
	private final JournalRepository journalRepo;
	
	private final EntryLineRepository entryRepo;
	
	private final JournalFinder journalFinder;

	// 거래 입력
	@Override
	public Journal createJournal(Ledger ledger, Map<EntrySide, Account> accountMap, JournalCreateRequest journalCreate) {
		Journal journal = Journal.create(ledger, journalCreate);
		
		List<EntryLine> entries = accountMap.entrySet().stream()
				.map(entry -> EntryLine.create(entry.getValue(), journal.getAmount(), entry.getKey()))
				.toList();
		
		for (EntryLine entry : entries) {
			journal.addEntryLine(entry);
		}
		
		journal.validateSavable();
		
		return journalRepo.save(journal);
	}

	// 거래 내역 수정
	@Override
	public Journal updateJournal(Ledger ledger, Map<EntrySide, Account> accountMap, JournalUpdateRequest journalUpdate) {
		Journal journal = journalFinder.findJournal(ledger, journalUpdate.journalId());
		
		List<EntryLine> entries = entryRepo.findByJournal(journal);
		
		journal.changeEntryLineWithAmount(entries, accountMap, journalUpdate);
		
		journal.validateSavable();
		
		return journalRepo.save(journal);
	}

	// 거래 내역 삭제
	@Override
	public void deleteJournal(Ledger ledger, Long journalId) {
		Journal journal = journalFinder.findJournal(ledger, journalId);
		
		journalRepo.delete(journal);
	}
	
}
