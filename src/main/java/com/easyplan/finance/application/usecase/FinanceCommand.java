package com.easyplan.finance.application.usecase;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.AccountCommand;
import com.easyplan.finance.application.provided.AccountFinder;
import com.easyplan.finance.application.provided.JournalCommand;
import com.easyplan.finance.application.provided.LedgerCommand;
import com.easyplan.finance.application.provided.LedgerFinder;
import com.easyplan.finance.application.usecase.exception.FinanceErrorCode;
import com.easyplan.finance.application.usecase.exception.FinanceException;
import com.easyplan.finance.application.usecase.response.AccountResponse.AccountCreateResponse;
import com.easyplan.finance.application.usecase.response.AccountResponse.AccountUpdateResponse;
import com.easyplan.finance.application.usecase.response.JournalResponse.JournalCreateResponse;
import com.easyplan.finance.application.usecase.response.JournalResponse.JournalUpdateResponse;
import com.easyplan.finance.application.usecase.response.LedgerResponse.LedgerCreateResponse;
import com.easyplan.finance.application.usecase.response.LedgerResponse.LedgerFiscalUpdateResponse;
import com.easyplan.finance.application.usecase.response.LedgerResponse.LedgerInfoUpdateResponse;
import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.Category;
import com.easyplan.finance.domain.account.request.AccountRequest.AccountCreateRequest;
import com.easyplan.finance.domain.account.request.AccountRequest.AccountUpdateRequest;
import com.easyplan.finance.domain.journal.Journal;
import com.easyplan.finance.domain.journal.request.JournalRequest.JournalCreateRequest;
import com.easyplan.finance.domain.journal.request.JournalRequest.JournalUpdateRequest;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerFiscalUpdate;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerInfoUpdate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FinanceCommand {
	
	private final LedgerCommand ledgerCommand;
	
	private final LedgerFinder ledgerFinder;
	
	private final AccountCommand accountCommand;
	
	private final AccountFinder accountFinder;
	
	private final JournalCommand journalCommand;
	
	// 가계부 생성
	@TraceTime
	public LedgerCreateResponse createLedger(PublicId memberPublicId, LedgerCreateRequest ledgerCreate) {
		// 가계부 생성
		Ledger ledger = ledgerCommand.createLedger(memberPublicId, ledgerCreate);
		
		// 위 가계부와 연결된 기본 5대 카테고리(대분류) 생성
		List<Category> categories = accountCommand.createCategories(ledger);
		
		// 사용자가 선택한 계정 항목만 생성
		List<Account> accounts = accountCommand.memberSelectedCreateAccounts(ledger, categories, ledgerCreate.selectedAccounts());
		
		return LedgerCreateResponse.of(ledger, accounts.size());
	}
	
	// 가계부 정보 수정
	@TraceTime
	public LedgerInfoUpdateResponse updateLedgerInfo(PublicId memberPublicId, PublicId ledgerPublicId, LedgerInfoUpdate ledgerInfo) {
		Ledger ledger = ledgerCommand.updateInfo(memberPublicId, ledgerPublicId, ledgerInfo);
		
		return LedgerInfoUpdateResponse.of(ledger);
	}
	
	// 가계부 회계 시작일 수정
	@TraceTime
	public LedgerFiscalUpdateResponse updateLedgerFiscal(PublicId memberPublicId, PublicId ledgerPublicId, LedgerFiscalUpdate ledgerFiscal) {
		Ledger ledger = ledgerCommand.updateFiscal(memberPublicId, ledgerPublicId, ledgerFiscal);
		
		return LedgerFiscalUpdateResponse.of(ledger);
	}
	
	// 가계부 삭제
	@TraceTime
	public void deleteLedger(PublicId memberPublicId, PublicId ledgerPublicId) {
		ledgerCommand.delete(memberPublicId, ledgerPublicId);
	}
	
	// 계정 항목 생성
	@TraceTime
	public AccountCreateResponse createAccount(PublicId memberPublicId, PublicId ledgerPublicId, AccountCreateRequest accountCreate) {
		Ledger ledger = ledgerFinder.findByLedgerOwner(memberPublicId, ledgerPublicId);
		
		Account account = accountCommand.createAccount(ledger, accountCreate);
		
		return AccountCreateResponse.of(account);
	}
	
	// 계정 항목 정보 변경
	@TraceTime
	public AccountUpdateResponse updateAccount(PublicId memberPublicId, PublicId ledgerPublicId, PublicId accountPublicId, AccountUpdateRequest accountUpdate) {
		Ledger ledger = ledgerFinder.findByLedgerOwner(memberPublicId, ledgerPublicId);
		
		Account account = accountCommand.updateAccount(ledger, accountPublicId, accountUpdate);
		
		return AccountUpdateResponse.of(account);
	}
	
	// 계정 항목 삭제
	@TraceTime
	public void deactivateAccount(PublicId memberPublicId, PublicId ledgerPublicId, PublicId accountPublicId)	{
		Ledger ledger = ledgerFinder.findByLedgerOwner(memberPublicId, ledgerPublicId);
		
		accountCommand.deactivate(ledger, accountPublicId);
	}
	
	// 거래 입력
	@TraceTime
	public JournalCreateResponse createJournal(PublicId memberPublicId, PublicId ledgerPublicId, JournalCreateRequest journalCreate) {
		validateJournalRequest(journalCreate.entries());
		
		Ledger ledger = ledgerFinder.findByLedgerOwner(memberPublicId, ledgerPublicId);
		
		Map<EntrySide, Account> accountMap = accountFinder.findAccountFromJournal(ledger, journalCreate.entries());
		
		Journal journal = journalCommand.createJournal(ledger, accountMap, journalCreate);
		
		return JournalCreateResponse.of(journal);
	}
	
	// 거래 내역 수정
	@TraceTime
	public JournalUpdateResponse updateJournal(PublicId memberPublicId, PublicId ledgerPublicId, JournalUpdateRequest journalUpdate) {
		validateJournalRequest(journalUpdate.entries());
		
		Ledger ledger = ledgerFinder.findByLedgerOwner(memberPublicId, ledgerPublicId);
		
		Map<EntrySide, Account> accountMap = accountFinder.findAccountFromJournal(ledger, journalUpdate.entries());
		
		Journal journal = journalCommand.updateJournal(ledger, accountMap, journalUpdate);
		
		return JournalUpdateResponse.of(journal);
	}
	
	// 거래 내역 삭제
	@TraceTime
	public void deleteJournal(PublicId memberPublicId, PublicId ledgerPublicId, Long journalId) {
		Ledger ledger = ledgerFinder.findByLedgerOwner(memberPublicId, ledgerPublicId);
		
		journalCommand.deleteJournal(ledger, journalId);
	}
	
	private void validateJournalRequest(Map<EntrySide, String> entriesFromRequest) {
		if(entriesFromRequest.size() != 2
				|| !entriesFromRequest.containsKey(EntrySide.CREDIT)
				|| !entriesFromRequest.containsKey(EntrySide.DEBIT)) {
			
			throw new FinanceException(FinanceErrorCode.INVALID_JOURNAL_ENTRY_COUNT);
		}
	}
}
