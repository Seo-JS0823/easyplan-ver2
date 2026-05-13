package com.easyplan.application.finance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.AccountFinder;
import com.easyplan.finance.application.provided.JournalFinder;
import com.easyplan.finance.application.provided.LedgerFinder;
import com.easyplan.finance.application.required.repository.EntryLineRepository;
import com.easyplan.finance.application.usecase.FinanceCommand;
import com.easyplan.finance.application.usecase.response.command.AccountResponse.AccountCreateResponse;
import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountOptionTemplate;
import com.easyplan.finance.domain.account.AccountStatus;
import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.account.exception.AccountErrorCode;
import com.easyplan.finance.domain.account.exception.AccountException;
import com.easyplan.finance.domain.account.request.AccountRequest.AccountCreateRequest;
import com.easyplan.finance.domain.account.request.AccountRequest.AccountUpdateRequest;
import com.easyplan.finance.domain.journal.Journal;
import com.easyplan.finance.domain.journal.Money;
import com.easyplan.finance.domain.journal.TransactionType;
import com.easyplan.finance.domain.journal.exception.JournalErrorCode;
import com.easyplan.finance.domain.journal.exception.JournalException;
import com.easyplan.finance.domain.journal.request.JournalRequest.JournalCreateRequest;
import com.easyplan.finance.domain.journal.request.JournalRequest.JournalUpdateRequest;
import com.easyplan.finance.domain.ledger.FiscalDay;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.LedgerType;
import com.easyplan.finance.domain.ledger.exception.LedgerErrorCode;
import com.easyplan.finance.domain.ledger.exception.LedgerException;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerFiscalUpdate;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerInfoUpdate;
import com.easyplan.fixture.MemberFix;
import com.easyplan.member.application.provided.MemberCommand;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.request.MemberRegisterRequest;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class FinanceCommandUsecaseTest {
	
	@Autowired
	EntityManager em;
	
	@Autowired
	FinanceCommand financeCommand;
	
	@Autowired
	MemberCommand memberCommand;
	
	@Autowired
	LedgerFinder ledgerFinder;
	
	@Autowired
	AccountFinder accountFinder;
	
	@Autowired
	JournalFinder journalFinder;
	
	@Autowired
	EntryLineRepository entryRepo;
	
	Member member;
	
	PublicId memberPID;
	
	MemberRegisterRequest memberRegisterRequest;
	
	LedgerCreateRequest ledgerCreateRequest;
	
	@BeforeEach
	void setUp() {
		memberRegisterRequest = MemberFix.memberRegisterRequest();
		member = memberCommand.register(memberRegisterRequest);
		
		member.activate();
		
		memberPID = member.getMemberPublicId();
		
		em.flush();
		em.clear();
		
		ledgerCreateRequest = new LedgerCreateRequest(
				LedgerType.PERSONAL,
				"가계부 이름",
				"가계부 설명",
				List.of()
		);
	}
	
	// 가계부 생성 테스트
	@Test
	@DisplayName("가계부 생성")
	void createLedger() {
		// 준비 및 실행
		Ledger ledger = createLedger(memberPID);
		List<Account> account = accountFinder.findByLedger(ledger);
		
		// 결과
		assertThat(ledger.getId()).isNotNull();
		assertThat(ledger.getFiscalDay()).isEqualTo(new FiscalDay(1));
		assertThat(ledger.getCreatedAt()).isNotNull();
		assertThat(ledger.getLedgerDescription()).isEqualTo(ledgerCreateRequest.description());
		assertThat(ledger.getLedgerName()).isEqualTo(ledgerCreateRequest.name());
		assertThat(ledger.getLedgerType()).isEqualTo(LedgerType.PERSONAL);
		assertThat(ledger.getLedgerPublicId()).isNotNull();
		assertThat(ledger.getLedgerPublicId().publicId().length()).isEqualTo(36);
		
		assertThat(account).hasSize(1);
		
	}
	
	// 가계부 정보 수정 테스트
	@Test
	@DisplayName("가계부 정보 수정")
	void updateLedgerInfo() {
		// 준비
		Ledger ledger = createLedger(memberPID);
		
		LedgerInfoUpdate ledgerInfoUpdate = new LedgerInfoUpdate(
				"가계부 이름 update",
				"가계부 설명 update"
		);
		
		// 실행
		financeCommand.updateLedgerInfo(memberPID, ledger.getLedgerPublicId(), ledgerInfoUpdate);
		
		em.flush();
		em.clear();
		
		Ledger updatedLedger = ledgerFinder.findByLedger(ledger.getLedgerPublicId());
		
		// 결과
		assertThat(updatedLedger.getId()).isEqualTo(ledger.getId());
		assertThat(updatedLedger.getFiscalDay()).isEqualTo(ledger.getFiscalDay());
		assertThat(updatedLedger.getLedgerName()).isEqualTo(ledgerInfoUpdate.name());
		assertThat(updatedLedger.getLedgerDescription()).isEqualTo(ledgerInfoUpdate.description());
	}
	
	// 가계부 회계 시작일 수정 테스트
	@Test
	@DisplayName("가계부 회계 시작일 수정")
	void updateLedgerFiscal() {
		// 준비
		Ledger ledger = createLedger(memberPID);
		
		LedgerFiscalUpdate ledgerFiscalUpdate = new LedgerFiscalUpdate(10);
		
		// 실행
		financeCommand.updateLedgerFiscal(memberPID, ledger.getLedgerPublicId(), ledgerFiscalUpdate);
		
		em.flush();
		em.clear();
		
		Ledger updatedLedger = ledgerFinder.findByLedger(ledger.getLedgerPublicId());
		
		// 결과
		assertThat(updatedLedger.getId()).isEqualTo(ledger.getId());
		assertThat(updatedLedger.getLedgerName()).isEqualTo(ledgerCreateRequest.name());
		assertThat(updatedLedger.getLedgerDescription()).isEqualTo(ledgerCreateRequest.description());
		assertThat(updatedLedger.getFiscalDay()).isEqualTo(new FiscalDay(10));
	}
	
	// 가계부 삭제 테스트
	@Test
	@DisplayName("가계부 삭제")
	void deleteLedger() {
		// 준비
		Ledger ledger = createLedger(memberPID);
		
		// 실행
		financeCommand.deleteLedger(memberPID, ledger.getLedgerPublicId());
		
		em.flush();
		em.clear();
		
		// 결과
		assertThatThrownBy(() -> ledgerFinder.findByLedger(ledger.getLedgerPublicId()))
		.isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerErrorCode.LEDGER_NOT_FOUND.getMessage());
	}
	
	// 계정 생성 테스트
	@Test
	@DisplayName("거래 계정 생성")
	void createAccount() {
		// 준비
		Ledger ledger = createLedger(memberPID);
		
		AccountCreateRequest assetReq = FinanceFix.assetAccountCreateRequest();
		AccountCreateRequest expenseReq = FinanceFix.expenseAccountCreateRequest();
		AccountCreateRequest incomeReq = FinanceFix.incomeAccountCreateRequest();
		AccountCreateRequest liaReq = FinanceFix.liabilitiesAccountCreateRequest();
		
		// 실행
		Account asset = createAccount(memberPID, ledger, assetReq);
		Account expense = createAccount(memberPID, ledger, expenseReq);
		Account income = createAccount(memberPID, ledger, incomeReq);
		Account lia = createAccount(memberPID, ledger, liaReq);
		
		Account assetFound = accountFinder.findAccount(ledger, asset.getAccountPublicId());
		Account expenseFound = accountFinder.findAccount(ledger, expense.getAccountPublicId());
		Account incomeFound = accountFinder.findAccount(ledger, income.getAccountPublicId());
		Account liaFound = accountFinder.findAccount(ledger, lia.getAccountPublicId());
		
		// 결과
		assertThat(assetFound.getId()).isEqualTo(asset.getId());
		assertThat(expenseFound.getId()).isEqualTo(expense.getId());
		assertThat(incomeFound.getId()).isEqualTo(income.getId());
		assertThat(liaFound.getId()).isEqualTo(lia.getId());
		
		assertThat(assetFound.getAccountName()).isEqualTo(assetReq.accountName());
		assertThat(expenseFound.getAccountName()).isEqualTo(expenseReq.accountName());
		assertThat(incomeFound.getAccountName()).isEqualTo(incomeReq.accountName());
		assertThat(liaFound.getAccountName()).isEqualTo(liaReq.accountName());
		
		assertThat(assetFound.getAccountDescription()).isEqualTo(assetReq.accountDescription());
		assertThat(expenseFound.getAccountDescription()).isEqualTo(expenseReq.accountDescription());
		assertThat(incomeFound.getAccountDescription()).isEqualTo(incomeReq.accountDescription());
		assertThat(liaFound.getAccountDescription()).isEqualTo(liaReq.accountDescription());
		
		assertThat(assetFound.getAccountType()).isEqualTo(assetReq.accountType());
		assertThat(expenseFound.getAccountType()).isEqualTo(expenseReq.accountType());
		assertThat(incomeFound.getAccountType()).isEqualTo(incomeReq.accountType());
		assertThat(liaFound.getAccountType()).isEqualTo(liaReq.accountType());
		
		assertThat(assetFound.getOption().getOptionCode()).isEqualTo(assetReq.optionCode());
		assertThat(expenseFound.getOption().getOptionCode()).isEqualTo(expenseReq.optionCode());
		assertThat(incomeFound.getOption().getOptionCode()).isEqualTo(incomeReq.optionCode());
		assertThat(liaFound.getOption().getOptionCode()).isEqualTo(liaReq.optionCode());
		
		assertThat(assetFound.getStatus()).isEqualTo(AccountStatus.ACTIVE);
		assertThat(expenseFound.getStatus()).isEqualTo(AccountStatus.ACTIVE);
		assertThat(incomeFound.getStatus()).isEqualTo(AccountStatus.ACTIVE);
		assertThat(liaFound.getStatus()).isEqualTo(AccountStatus.ACTIVE);
		
		assertThat(assetFound.getLedger().getId()).isEqualTo(ledger.getId());
		assertThat(expenseFound.getLedger().getId()).isEqualTo(ledger.getId());
		assertThat(incomeFound.getLedger().getId()).isEqualTo(ledger.getId());
		assertThat(liaFound.getLedger().getId()).isEqualTo(ledger.getId());
	}
	
	// 계정 정보 수정 테스트
	@Test
	@DisplayName("계정 정보 수정")
	void updateAccount() {
		// 준비
		Ledger ledger = createLedger(memberPID);
		AccountCreateRequest assetReq = FinanceFix.assetAccountCreateRequest();
		Account asset = createAccount(memberPID, ledger, assetReq);
		
		AccountUpdateRequest assetUpdateReq = new AccountUpdateRequest(
				"AccountName",
				"AccountDescription",
				AccountOptionTemplate.LIQUID
		);
		
		// 실행
		financeCommand.updateAccount(memberPID, ledger.getLedgerPublicId(), asset.getAccountPublicId(), assetUpdateReq);
		
		em.flush();
		em.clear();
		
		Account updatedAsset = accountFinder.findAccount(ledger, asset.getAccountPublicId());
		
		// 결과
		assertThat(updatedAsset.getId()).isEqualTo(asset.getId());
		assertThat(updatedAsset.getAccountName()).isEqualTo(assetUpdateReq.accountName());
		assertThat(updatedAsset.getAccountDescription()).isEqualTo(assetUpdateReq.accountDescription());
		assertThat(updatedAsset.getAccountType()).isEqualTo(AccountType.ASSET);
		assertThat(updatedAsset.getOption().getOptionCode()).isEqualTo(assetUpdateReq.optionCode());
		assertThat(updatedAsset.getStatus()).isEqualTo(AccountStatus.ACTIVE);
		assertThat(updatedAsset.getLedger().getId()).isEqualTo(ledger.getId());
	}
	
	// 계정 삭제 테스트
	@Test
	@DisplayName("계정 삭제")
	void deactivateAccount() {
		// 준비
		Ledger ledger = createLedger(memberPID);
		AccountCreateRequest assetReq = FinanceFix.assetAccountCreateRequest();
		Account asset = createAccount(memberPID, ledger, assetReq);
		
		// 실행
		financeCommand.deactivateAccount(memberPID, ledger.getLedgerPublicId(), asset.getAccountPublicId());
		
		em.flush();
		em.clear();
		
		Account deactivateAsset = accountFinder.findAccount(ledger, asset.getAccountPublicId());
		
		// 결과
		assertThat(deactivateAsset.getId()).isEqualTo(asset.getId());
		assertThat(deactivateAsset.getStatus()).isEqualTo(AccountStatus.DEACTIVATE);
		
		assertThatThrownBy(() -> accountFinder.findActiveAccount(ledger, asset.getAccountPublicId()))
		.isInstanceOf(AccountException.class)
		.hasMessageContaining(AccountErrorCode.ACCOUNT_NOT_FOUND.getMessage());
	}
	
	// 거래 입력 테스트
	@Test
	@DisplayName("거래 입력 수입 거래")
	void createJournalIncome() {
		// 준비
		Ledger ledger = createLedger(memberPID);
		AccountCreateRequest assetReq = FinanceFix.assetAccountCreateRequest();
		AccountCreateRequest incomeReq = FinanceFix.incomeAccountCreateRequest();		
		Account asset = createAccount(memberPID, ledger, assetReq);
		Account income = createAccount(memberPID, ledger, incomeReq);
		
		JournalCreateRequest incomeJournalCreate = FinanceFix.journalCreate(
				TransactionType.INCOME,
				asset,
				income
		);
		
		Money money = new Money(incomeJournalCreate.amount());
		
		// 실행
		Long journalId = financeCommand.createJournal(memberPID, ledger.getLedgerPublicId(), incomeJournalCreate).journalId();
		
		em.flush();
		em.clear();
		
		Journal journal = journalFinder.findWithDetail(ledger, journalId);
		
		// 결과
		// 관계 매핑 검증
		assertThat(journal.getId()).isEqualTo(journalId);
		assertThat(journal.getLedger().getId()).isEqualTo(ledger.getId());
		
		// 차변 대변 이름 검증
		assertThat(journal.getAccountName(EntrySide.DEBIT)).isEqualTo(asset.getAccountName());
		assertThat(journal.getAccountName(EntrySide.CREDIT)).isEqualTo(income.getAccountName());
		
		// 거래 타입 및 거래 금액 검증
		assertThat(journal.getTransactionType()).isEqualTo(incomeJournalCreate.transactionType());
		assertThat(journal.getAmount()).isEqualTo(money);
		assertThat(journal.getAmount(EntrySide.DEBIT)).isEqualTo(journal.getAmount(EntrySide.CREDIT));
		
		// 차변 대변 검증
		assertThat(journal.getEntries()).hasSize(2);
		assertThat(journal.getAccount(EntrySide.DEBIT)).isEqualTo(asset);
		assertThat(journal.getAccount(EntrySide.CREDIT)).isEqualTo(income);
		assertThat(journal.getAccountType(EntrySide.DEBIT)).isEqualTo(assetReq.accountType());
		assertThat(journal.getAccountType(EntrySide.CREDIT)).isEqualTo(incomeReq.accountType());
		
		
		assertThat(journal.getEntryLine(EntrySide.DEBIT).getJournal()).isEqualTo(journal);
		assertThat(journal.getEntryLine(EntrySide.CREDIT).getJournal()).isEqualTo(journal);
	}
	
	@Test
	@DisplayName("거래 입력 지출 거래")
	void createJournalExpense() {
		// 준비
		Ledger ledger = createLedger(memberPID);
		AccountCreateRequest assetReq = FinanceFix.assetAccountCreateRequest();
		AccountCreateRequest expenseReq = FinanceFix.expenseAccountCreateRequest();		
		Account asset = createAccount(memberPID, ledger, assetReq);
		Account expense = createAccount(memberPID, ledger, expenseReq);
		
		JournalCreateRequest incomeJournalCreate = FinanceFix.journalCreate(
				TransactionType.EXPENSE,
				expense,
				asset
		);
		
		Money money = new Money(incomeJournalCreate.amount());
		
		// 실행
		Long journalId = financeCommand.createJournal(memberPID, ledger.getLedgerPublicId(), incomeJournalCreate).journalId();
		
		em.flush();
		em.clear();
		
		Journal journal = journalFinder.findWithDetail(ledger, journalId);
		
		// 결과
		assertThat(journal.getId()).isEqualTo(journalId);
		assertThat(journal.getLedger().getId()).isEqualTo(ledger.getId());
		
		// 차변 대변 이름 검증
		assertThat(journal.getAccountName(EntrySide.DEBIT)).isEqualTo(expense.getAccountName());
		assertThat(journal.getAccountName(EntrySide.CREDIT)).isEqualTo(asset.getAccountName());
		
		// 거래 타입 및 거래 금액 검증
		assertThat(journal.getTransactionType()).isEqualTo(incomeJournalCreate.transactionType());
		assertThat(journal.getAmount()).isEqualTo(money);
		assertThat(journal.getAmount(EntrySide.DEBIT)).isEqualTo(journal.getAmount(EntrySide.CREDIT));
		
		// 차변 대변 검증
		assertThat(journal.getEntries()).hasSize(2);
		assertThat(journal.getAccount(EntrySide.DEBIT)).isEqualTo(expense);
		assertThat(journal.getAccount(EntrySide.CREDIT)).isEqualTo(asset);
		assertThat(journal.getAccountType(EntrySide.DEBIT)).isEqualTo(expenseReq.accountType());
		assertThat(journal.getAccountType(EntrySide.CREDIT)).isEqualTo(assetReq.accountType());
		
		assertThat(journal.getEntryLine(EntrySide.DEBIT).getJournal()).isEqualTo(journal);
		assertThat(journal.getEntryLine(EntrySide.CREDIT).getJournal()).isEqualTo(journal);
	}
	
	@Test
	@DisplayName("거래 입력 이체 거래")
	void createJournalTransfer() {
		// 준비
		Ledger ledger = createLedger(memberPID);
		AccountCreateRequest debitAssetReq = FinanceFix.assetAccountCreateRequest("debit account");
		AccountCreateRequest creditAssetReq = FinanceFix.assetAccountCreateRequest("credit account");		
		Account debit = createAccount(memberPID, ledger, debitAssetReq);
		Account credit = createAccount(memberPID, ledger, creditAssetReq);
		
		JournalCreateRequest incomeJournalCreate = FinanceFix.journalCreate(
				TransactionType.TRANSFER,
				debit,
				credit
		);
		
		Money money = new Money(incomeJournalCreate.amount());
		
		// 실행
		Long journalId = financeCommand.createJournal(memberPID, ledger.getLedgerPublicId(), incomeJournalCreate).journalId();
		
		em.flush();
		em.clear();
		
		Journal journal = journalFinder.findWithDetail(ledger, journalId);
		
		// 결과
		assertThat(journal.getId()).isEqualTo(journalId);
		assertThat(journal.getLedger().getId()).isEqualTo(ledger.getId());
		
		// 차변 대변 이름 검증
		assertThat(journal.getAccountName(EntrySide.DEBIT)).isEqualTo(debit.getAccountName());
		assertThat(journal.getAccountName(EntrySide.CREDIT)).isEqualTo(credit.getAccountName());
		
		// 거래 타입 및 거래 금액 검증
		assertThat(journal.getTransactionType()).isEqualTo(incomeJournalCreate.transactionType());
		assertThat(journal.getAmount()).isEqualTo(money);
		assertThat(journal.getAmount(EntrySide.DEBIT)).isEqualTo(journal.getAmount(EntrySide.CREDIT));
		
		// 차변 대변 검증
		assertThat(journal.getEntries()).hasSize(2);
		assertThat(journal.getAccount(EntrySide.DEBIT)).isEqualTo(debit);
		assertThat(journal.getAccount(EntrySide.CREDIT)).isEqualTo(credit);
		assertThat(journal.getAccountType(EntrySide.DEBIT)).isEqualTo(debitAssetReq.accountType());
		assertThat(journal.getAccountType(EntrySide.CREDIT)).isEqualTo(creditAssetReq.accountType());
		
		assertThat(journal.getEntryLine(EntrySide.DEBIT).getJournal()).isEqualTo(journal);
		assertThat(journal.getEntryLine(EntrySide.CREDIT).getJournal()).isEqualTo(journal);
	}
	
	@Test
	@DisplayName("기초잔액 설정 자산계정")
	void balanceSettingAsset() {
		// 준비
		Ledger ledger = createLedger(memberPID);
		AccountCreateRequest assetReq = FinanceFix.assetAccountCreateRequest();
		Account asset = createAccount(memberPID, ledger, assetReq);
		Account equity = accountFinder.findEquity(ledger);
		
		JournalCreateRequest balanceSettingRequest = FinanceFix.journalCreate(
				TransactionType.BALANCE,
				asset,
				equity
		);
		
		Money money = new Money(balanceSettingRequest.amount());
		
		// 실행
		Long journalId = financeCommand.createJournal(memberPID, ledger.getLedgerPublicId(), balanceSettingRequest).journalId();
		
		em.flush();
		em.clear();
		
		Journal journal = journalFinder.findWithDetail(ledger, journalId);
		
		// 결과
		assertThat(journal.getId()).isEqualTo(journalId);
		assertThat(journal.getLedger().getId()).isEqualTo(ledger.getId());
		
		// 차변 대변 이름 검증
		assertThat(journal.getAccountName(EntrySide.DEBIT)).isEqualTo(asset.getAccountName());
		assertThat(journal.getAccountName(EntrySide.CREDIT)).isEqualTo(equity.getAccountName());
		
		// 거래 타입 및 거래 금액 검증
		assertThat(journal.getTransactionType()).isEqualTo(balanceSettingRequest.transactionType());
		assertThat(journal.getAmount()).isEqualTo(money);
		assertThat(journal.getAmount(EntrySide.DEBIT)).isEqualTo(journal.getAmount(EntrySide.CREDIT));
		
		// 차변 대변 검증
		assertThat(journal.getEntries()).hasSize(2);
		assertThat(journal.getAccount(EntrySide.DEBIT)).isEqualTo(asset);
		assertThat(journal.getAccount(EntrySide.CREDIT)).isEqualTo(equity);
		assertThat(journal.getAccountType(EntrySide.DEBIT)).isEqualTo(assetReq.accountType());
		assertThat(journal.getAccountType(EntrySide.CREDIT)).isEqualTo(AccountType.EQUITY);
		
		assertThat(journal.getEntryLine(EntrySide.DEBIT).getJournal()).isEqualTo(journal);
		assertThat(journal.getEntryLine(EntrySide.CREDIT).getJournal()).isEqualTo(journal);
	}
	
	@Test
	@DisplayName("기초잔액 설정 부채계정")
	void balanceSettingLiabilities() {
		// 준비
		Ledger ledger = createLedger(memberPID);
		AccountCreateRequest liaReq = FinanceFix.liabilitiesAccountCreateRequest();
		Account lia = createAccount(memberPID, ledger, liaReq);
		Account equity = accountFinder.findEquity(ledger);
		
		JournalCreateRequest balanceSettingRequest = FinanceFix.journalCreate(
				TransactionType.BALANCE,
				equity,
				lia
		);
		
		Money money = new Money(balanceSettingRequest.amount());
		
		// 실행
		Long journalId = financeCommand.createJournal(memberPID, ledger.getLedgerPublicId(), balanceSettingRequest).journalId();
		
		em.flush();
		em.clear();
		
		Journal journal = journalFinder.findWithDetail(ledger, journalId);
		
		// 결과
		assertThat(journal.getId()).isEqualTo(journalId);
		assertThat(journal.getLedger().getId()).isEqualTo(ledger.getId());
		
		// 차변 대변 이름 검증
		assertThat(journal.getAccountName(EntrySide.DEBIT)).isEqualTo(equity.getAccountName());
		assertThat(journal.getAccountName(EntrySide.CREDIT)).isEqualTo(lia.getAccountName());
		
		// 거래 타입 및 거래 금액 검증
		assertThat(journal.getTransactionType()).isEqualTo(balanceSettingRequest.transactionType());
		assertThat(journal.getAmount()).isEqualTo(money);
		assertThat(journal.getAmount(EntrySide.DEBIT)).isEqualTo(journal.getAmount(EntrySide.CREDIT));
		
		// 차변 대변 검증
		assertThat(journal.getEntries()).hasSize(2);
		assertThat(journal.getAccount(EntrySide.DEBIT)).isEqualTo(equity);
		assertThat(journal.getAccount(EntrySide.CREDIT)).isEqualTo(lia);
		assertThat(journal.getAccountType(EntrySide.DEBIT)).isEqualTo(AccountType.EQUITY);
		assertThat(journal.getAccountType(EntrySide.CREDIT)).isEqualTo(liaReq.accountType());
		
		assertThat(journal.getEntryLine(EntrySide.DEBIT).getJournal()).isEqualTo(journal);
		assertThat(journal.getEntryLine(EntrySide.CREDIT).getJournal()).isEqualTo(journal);
	}
	
	// 거래 내역 수정 테스트
	@Test
	@DisplayName("거래 내역 수정")
	void updateJournal() {
		Ledger ledger = createLedger(memberPID);
		AccountCreateRequest assetReq = FinanceFix.assetAccountCreateRequest();
		AccountCreateRequest expenseReq = FinanceFix.expenseAccountCreateRequest();		
		Account asset = createAccount(memberPID, ledger, assetReq);
		Account expense = createAccount(memberPID, ledger, expenseReq);
		
		JournalCreateRequest incomeJournalCreate = FinanceFix.journalCreate(
				TransactionType.EXPENSE,
				expense,
				asset
		);
		
		Long journalId = financeCommand.createJournal(memberPID, ledger.getLedgerPublicId(), incomeJournalCreate).journalId();
		
		em.flush();
		em.clear();
		
		JournalUpdateRequest journalUpdateRequest = new JournalUpdateRequest(
				journalId,
				LocalDate.of(2026, 5, 2),
				150000L,
				"journal update",
				Map.of(
						EntrySide.DEBIT, expense.getAccountPublicId().publicId(),
						EntrySide.CREDIT, asset.getAccountPublicId().publicId()
				)
		);
		
		Money money = new Money(journalUpdateRequest.amount());
		
		// 실행
		financeCommand.updateJournal(memberPID, ledger.getLedgerPublicId(), journalUpdateRequest);
		
		em.flush();
		em.clear();
		
		Journal journal = journalFinder.findJournal(ledger, journalId);
		
		// 결과
		assertThat(journal.getId()).isEqualTo(journalId);
		assertThat(journal.getLedger().getId()).isEqualTo(ledger.getId());
		
		// 차변 대변 이름 검증
		assertThat(journal.getAccountName(EntrySide.DEBIT)).isEqualTo(expense.getAccountName());
		assertThat(journal.getAccountName(EntrySide.CREDIT)).isEqualTo(asset.getAccountName());
		
		// 거래 타입 및 거래 금액 검증
		assertThat(journal.getTransactionType()).isEqualTo(TransactionType.EXPENSE);
		assertThat(journal.getAmount()).isEqualTo(money);
		assertThat(journal.getAmount(EntrySide.DEBIT)).isEqualTo(journal.getAmount(EntrySide.CREDIT));
		
		// 차변 대변 검증
		assertThat(journal.getEntries()).hasSize(2);
		assertThat(journal.getAccount(EntrySide.DEBIT)).isEqualTo(expense);
		assertThat(journal.getAccount(EntrySide.CREDIT)).isEqualTo(asset);
		assertThat(journal.getAccountType(EntrySide.DEBIT)).isEqualTo(expenseReq.accountType());
		assertThat(journal.getAccountType(EntrySide.CREDIT)).isEqualTo(assetReq.accountType());
		
		assertThat(journal.getEntryLine(EntrySide.DEBIT).getJournal()).isEqualTo(journal);
		assertThat(journal.getEntryLine(EntrySide.CREDIT).getJournal()).isEqualTo(journal);
	}
	
	// 거래 내역 삭제 테스트
	@Test
	@DisplayName("거래 내역 삭제")
	void deleteJournal() {
		Ledger ledger = createLedger(memberPID);
		AccountCreateRequest assetReq = FinanceFix.assetAccountCreateRequest();
		AccountCreateRequest incomeReq = FinanceFix.incomeAccountCreateRequest();		
		Account asset = createAccount(memberPID, ledger, assetReq);
		Account income = createAccount(memberPID, ledger, incomeReq);
		
		JournalCreateRequest incomeJournalCreate = FinanceFix.journalCreate(
				TransactionType.INCOME,
				asset,
				income
		);
		
		Long journalId = financeCommand.createJournal(memberPID, ledger.getLedgerPublicId(), incomeJournalCreate).journalId();
		
		em.flush();
		em.clear();
		
		// 실행
		financeCommand.deleteJournal(memberPID, ledger.getLedgerPublicId(), journalId);
		
		em.flush();
		em.clear();
		
		// 결과
		assertThatThrownBy(() -> journalFinder.findJournal(ledger, journalId))
		.isInstanceOf(JournalException.class)
		.hasMessageContaining(JournalErrorCode.JOURNAL_NOT_FOUND.getMessage());
	}
	
	
	
	private Account createAccount(PublicId memberPID, Ledger ledger, AccountCreateRequest accountCreate) {
		AccountCreateResponse response = financeCommand.createAccount(memberPID, ledger.getLedgerPublicId(), accountCreate);
		
		em.flush();
		em.clear();
		
		PublicId accountPID = new PublicId(response.accountPublicId());
		
		Account account = accountFinder.findAccount(ledger, accountPID);
		
		return account;
	}
	
	private Ledger createLedger(PublicId memberPID) {
		PublicId ledgerPID = new PublicId(financeCommand.createLedger(memberPID, ledgerCreateRequest).ledgerPublicId());
		
		em.flush();
		em.clear();
		
		Ledger ledger = ledgerFinder.findByLedgerOwner(memberPID, ledgerPID);
		
		return ledger;
	}
	
}
