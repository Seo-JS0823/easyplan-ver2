package com.easyplan.finance.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan.finance.application.provided.AccountFinder;
import com.easyplan.finance.application.provided.JournalFinder;
import com.easyplan.finance.application.provided.LedgerFinder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinanceQuery {
	private final LedgerFinder ledgerFinder;
	
	private final AccountFinder accountFinder;
	
	private final JournalFinder journalFinder;
	
	
}
