package com.easyplan.finance.application.service;

import org.springframework.stereotype.Service;

import com.easyplan.finance.application.provided.JournalFinder;
import com.easyplan.finance.application.required.JournalRepository;
import com.easyplan.finance.domain.journal.Journal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalFinderService implements JournalFinder {
	
	private final JournalRepository journalRepo;
	
	@Override
	public Journal findByJournal(Long id) {
		return journalRepo.findById(id)
				.orElseThrow();
	}

}
