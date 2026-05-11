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
import com.easyplan.finance.application.provided.LedgerFinder;
import com.easyplan.finance.application.usecase.FinanceCommand;
import com.easyplan.finance.application.usecase.response.AccountResponse.AccountCreateResponse;
import com.easyplan.finance.application.usecase.response.JournalResponse.JournalCreateResponse;
import com.easyplan.finance.application.usecase.response.JournalResponse.JournalUpdateResponse;
import com.easyplan.finance.application.usecase.response.LedgerResponse.LedgerCreateResponse;
import com.easyplan.finance.application.usecase.response.LedgerResponse.LedgerFiscalUpdateResponse;
import com.easyplan.finance.application.usecase.response.LedgerResponse.LedgerInfoUpdateResponse;
import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountBasicTemplate;
import com.easyplan.finance.domain.account.AccountOptionTemplate;
import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.account.exception.AccountErrorCode;
import com.easyplan.finance.domain.account.exception.AccountException;
import com.easyplan.finance.domain.account.request.AccountRequest.AccountCreateRequest;
import com.easyplan.finance.domain.account.request.AccountRequest.AccountUpdateRequest;
import com.easyplan.finance.domain.journal.TransactionType;
import com.easyplan.finance.domain.journal.request.JournalRequest.JournalCreateRequest;
import com.easyplan.finance.domain.journal.request.JournalRequest.JournalUpdateRequest;
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
	private FinanceCommand financeCommand;
	
	@Autowired
	private LedgerFinder ledgerFinder;
	
	@Autowired
	private AccountFinder accountFinder;
	
	@Autowired
	private MemberCommand memberCommand;
	
	@Autowired
	private EntityManager em;
	
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
				"Ledger_Name",
				"Ledger_Description",
				List.of(
						AccountBasicTemplate.ASS01,
						AccountBasicTemplate.LIA01,
						AccountBasicTemplate.INC01,
						AccountBasicTemplate.EXP01
				)
		);
	}
	
	@Test
	@DisplayName("가계부 생성 테스트")
	void createLedger() {
		LedgerCreateResponse accounts = financeCommand.createLedger(member.getMemberPublicId(), ledgerCreateRequest);
		
		assertThat(accounts.accountCount()).isEqualTo(5);
	}
	
	@Test
	@DisplayName("가계부 정보 수정 테스트")
	void updateLedgerInfo() {
		PublicId ledgerPID = createLedger(memberPID, ledgerCreateRequest);
		
		String ledgerName = "Ledger_Rename";
		String ledgerDescription = "Ledger_Redescription";
		
		LedgerInfoUpdate ledgerInfoUpdateRequest = new LedgerInfoUpdate(ledgerName, ledgerDescription);
		
		LedgerInfoUpdateResponse response = financeCommand.updateLedgerInfo(
				member.getMemberPublicId(),
				ledgerPID,
				ledgerInfoUpdateRequest
		);
		
		em.flush();
		em.clear();
		
		assertThat(response.ledgerName()).isEqualTo(ledgerName);
		assertThat(response.ledgerDescription()).isEqualTo(ledgerDescription);
	}
	
	@Test
	@DisplayName("가계부 회계 시작일 수정 테스트")
	void updateLedgerFiscal() {
		PublicId ledgerPID = createLedger(memberPID, ledgerCreateRequest);
		
		LedgerFiscalUpdate ledgerFiscalUpdateRequest = new LedgerFiscalUpdate(10);
		
		LedgerFiscalUpdateResponse response = financeCommand.updateLedgerFiscal(
				member.getMemberPublicId(),
				ledgerPID,
				ledgerFiscalUpdateRequest
		);
		
		em.flush();
		em.clear();
		
		assertThat(response.fiscalDay()).isEqualTo(ledgerFiscalUpdateRequest.fiscalDay());
	}
	
	@Test
	@DisplayName("가계부 삭제 테스트")
	void deleteLedger() {
		PublicId ledgerPID = createLedger(memberPID, ledgerCreateRequest);
		
		financeCommand.deleteLedger(member.getMemberPublicId(), ledgerPID);
		
		em.flush();
		em.clear();
		
		assertThatThrownBy(() -> ledgerFinder.findByLedger(ledgerPID))
		.isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerErrorCode.LEDGER_NOT_FOUND.getMessage());
	}
	
	@Test
	@DisplayName("계정 항목 생성")
	void createAccount() {
		PublicId ledgerPID = createLedger(memberPID, ledgerCreateRequest);
		
		// AccountType accountType, String accountName, String accountDescription, AccountOptionTemplate option
		String accountName = "Name";
		String accountDescription = "Memo";
		
		AccountCreateRequest accountCreateRequest = new AccountCreateRequest(
				AccountType.ASSET,
				accountName,
				accountDescription,
				AccountOptionTemplate.BANK_ACCOUNT
		);
		
		AccountCreateResponse response = financeCommand.createAccount(
				member.getMemberPublicId(),
				ledgerPID,
				accountCreateRequest
		);
		
		assertThat(response.accountName()).isEqualTo(accountName);
		assertThat(response.accountDescription()).isEqualTo(accountDescription);
		assertThat(response.categoryName()).isEqualTo(AccountType.ASSET.getCategoryName());
		assertThat(response.optionName()).isEqualTo(AccountOptionTemplate.BANK_ACCOUNT.getOptionName());
	}
	
	@Test
	@DisplayName("계정 항목 수정")
	void updateAccount() {
		PublicId ledgerPID = createLedger(memberPID, ledgerCreateRequest);
		
		// AccountType accountType, String accountName, String accountDescription, AccountOptionTemplate option
		String accountName = "Name";
		String accountDescription = "Memo";
		
		AccountCreateRequest accountCreateRequest = new AccountCreateRequest(
				AccountType.ASSET,
				accountName,
				accountDescription,
				AccountOptionTemplate.BANK_ACCOUNT
		);
		
		AccountCreateResponse account = financeCommand.createAccount(
				member.getMemberPublicId(),
				ledgerPID,
				accountCreateRequest
		);
		
		em.flush();
		em.clear();
		
		String newAccountName = "New Name";
		String newDescription = "New Memo";
		
		AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest(
				newAccountName, newDescription, AccountOptionTemplate.BANK_ACCOUNT
		);
		
		financeCommand.updateAccount(
				member.getMemberPublicId(),
				ledgerPID,
				new PublicId(account.accountPublicId()),
				accountUpdateRequest
		);
		
		em.flush();
		em.clear();
		
		Account result = accountFinder.findActiveAccount(
				ledgerFinder.findByLedger(ledgerPID),
				new PublicId(account.accountPublicId())
		);
		
		assertThat(result.getAccountName()).isEqualTo(newAccountName);
		assertThat(result.getAccountDescription()).isEqualTo(newDescription);
		assertThat(result.getOption().getOptionName()).isEqualTo(AccountOptionTemplate.BANK_ACCOUNT.getOptionName());
	}
	
	@Test
	@DisplayName("계정 항목 삭제 (Deactivate)")
	void deactivateAccount() {
		PublicId ledgerPID = createLedger(memberPID, ledgerCreateRequest);
		
		// AccountType accountType, String accountName, String accountDescription, AccountOptionTemplate option
		String accountName = "Name";
		String accountDescription = "Memo";
		
		AccountCreateRequest accountCreateRequest = new AccountCreateRequest(
				AccountType.ASSET,
				accountName,
				accountDescription,
				AccountOptionTemplate.BANK_ACCOUNT
		);
		
		AccountCreateResponse account = financeCommand.createAccount(
				member.getMemberPublicId(),
				ledgerPID,
				accountCreateRequest
		);
		
		em.flush();
		em.clear();
		
		financeCommand.deactivateAccount(member.getMemberPublicId(),
				ledgerPID,
				new PublicId(account.accountPublicId())
		);
		
		em.flush();
		em.clear();
		
		assertThatThrownBy(() -> accountFinder.findActiveAccount(
				ledgerFinder.findByLedger(ledgerPID),
				new PublicId(account.accountPublicId())))
		.isInstanceOf(AccountException.class)
		.hasMessageContaining(AccountErrorCode.ACCOUNT_NOT_FOUND.getMessage());
	}
	
	@Test
	@DisplayName("거래 입력_지출거래")
	void createJournalExpense() {
		PublicId ledgerPID = createLedger(memberPID, ledgerCreateRequest);
		
		AccountCreateRequest debitCreateRequest = new AccountCreateRequest(
				AccountType.EXPENSE,
				"생활용품",
				"Memo",
				AccountOptionTemplate.VARIABLE_EXPENSE
		);
		
		AccountCreateRequest creditCreateRequest = new AccountCreateRequest(
				AccountType.ASSET,
				"체크카드",
				"Memo",
				AccountOptionTemplate.BANK_ACCOUNT
		);
		
		PublicId debitPID = createAccount(memberPID, ledgerPID, debitCreateRequest);
		PublicId creditPID = createAccount(memberPID, ledgerPID, creditCreateRequest);
		
		JournalCreateRequest journalCreateRequest = new JournalCreateRequest(
				LocalDate.of(2026, 5, 1),
				55000L,
				"Journal Memo",
				TransactionType.EXPENSE,
				Map.of(
						EntrySide.DEBIT, debitPID.publicId(),
						EntrySide.CREDIT, creditPID.publicId()
				)
		);
		
		JournalCreateResponse journal = financeCommand.createJournal(memberPID, ledgerPID, journalCreateRequest);
		
		assertThat(journal.message()).isEqualTo(
				"[2026-05-01 / 지출거래] 체크카드 계좌에서 생활용품(으)로 55,000원이 지출되었습니다."
		);
		
		assertThat(journal.journalId()).isNotNull();
	}
	
	@Test
	@DisplayName("거래 입력_수입거래")
	void createJournalIncome() {
		PublicId ledgerPID = createLedger(memberPID, ledgerCreateRequest);
		
		AccountCreateRequest creditCreateRequest = new AccountCreateRequest(
				AccountType.INCOME,
				"아르바이트 월급",
				"Memo",
				AccountOptionTemplate.FIXED_INCOME
		);
		
		AccountCreateRequest debitCreateRequest = new AccountCreateRequest(
				AccountType.ASSET,
				"체크카드",
				"Memo",
				AccountOptionTemplate.BANK_ACCOUNT
		);
		
		PublicId debitPID = createAccount(memberPID, ledgerPID, debitCreateRequest);
		PublicId creditPID = createAccount(memberPID, ledgerPID, creditCreateRequest);
		
		JournalCreateRequest journalCreateRequest = new JournalCreateRequest(
				LocalDate.of(2026, 5, 1),
				55000L,
				"Journal Memo",
				TransactionType.INCOME,
				Map.of(
						EntrySide.DEBIT, debitPID.publicId(),
						EntrySide.CREDIT, creditPID.publicId()
				)
		);
		
		JournalCreateResponse journal = financeCommand.createJournal(memberPID, ledgerPID, journalCreateRequest);
		
		assertThat(journal.message()).isEqualTo(
				"[2026-05-01 / 수입거래] 체크카드 계좌에 아르바이트 월급(으)로 55,000원이 입금되었습니다."
		);
		
		assertThat(journal.journalId()).isNotNull();
	}
	
	@Test
	@DisplayName("거래 입력_이체거래")
	void createJournalTransfer() {
		PublicId ledgerPID = createLedger(memberPID, ledgerCreateRequest);
		
		AccountCreateRequest creditCreateRequest = new AccountCreateRequest(
				AccountType.ASSET,
				"국민체크카드",
				"Memo",
				AccountOptionTemplate.BANK_ACCOUNT
		);
		
		AccountCreateRequest debitCreateRequest = new AccountCreateRequest(
				AccountType.ASSET,
				"우리체크카드",
				"Memo",
				AccountOptionTemplate.BANK_ACCOUNT
		);
		
		PublicId debitPID = createAccount(memberPID, ledgerPID, debitCreateRequest);
		PublicId creditPID = createAccount(memberPID, ledgerPID, creditCreateRequest);
		
		JournalCreateRequest journalCreateRequest = new JournalCreateRequest(
				LocalDate.of(2026, 5, 1),
				55000L,
				"Journal Memo",
				TransactionType.TRANSFER,
				Map.of(
						EntrySide.DEBIT, debitPID.publicId(),
						EntrySide.CREDIT, creditPID.publicId()
				)
		);
		
		JournalCreateResponse journal = financeCommand.createJournal(memberPID, ledgerPID, journalCreateRequest);
		
		assertThat(journal.message()).isEqualTo(
				"[2026-05-01 / 이체거래] 국민체크카드 계좌에서 우리체크카드 계좌로 55,000원이 이체되었습니다."
		);
		
		assertThat(journal.journalId()).isNotNull();
	}
	
	@Test
	@DisplayName("거래 내역 수정")
	void updateJournal() {
		PublicId ledgerPID = createLedger(memberPID, ledgerCreateRequest);
		
		AccountCreateRequest debitCreateRequest = new AccountCreateRequest(
				AccountType.EXPENSE,
				"생활용품",
				"Memo",
				AccountOptionTemplate.VARIABLE_EXPENSE
		);
		
		AccountCreateRequest creditCreateRequest = new AccountCreateRequest(
				AccountType.ASSET,
				"체크카드",
				"Memo",
				AccountOptionTemplate.BANK_ACCOUNT
		);
		
		AccountCreateRequest creditCreateRequest2 = new AccountCreateRequest(
				AccountType.LIABILITIES,
				"신한신용카드",
				"Memo",
				AccountOptionTemplate.CREDIT_CARD
		);
		
		PublicId debitPID = createAccount(memberPID, ledgerPID, debitCreateRequest);
		PublicId creditPID = createAccount(memberPID, ledgerPID, creditCreateRequest);
		PublicId credit2PID = createAccount(memberPID, ledgerPID, creditCreateRequest2);
		
		JournalCreateRequest journalCreateRequest = new JournalCreateRequest(
				LocalDate.of(2026, 5, 1),
				55000L,
				"Journal Memo",
				TransactionType.EXPENSE,
				Map.of(
						EntrySide.DEBIT, debitPID.publicId(),
						EntrySide.CREDIT, creditPID.publicId()
				)
		);
		
		JournalCreateResponse journal = financeCommand.createJournal(memberPID, ledgerPID, journalCreateRequest);
		
		em.flush();
		em.clear();
		
		JournalUpdateRequest journalUpdateRequest = new JournalUpdateRequest(
				journal.journalId(),
				LocalDate.of(2026, 5, 2),
				50000L,
				"Journal Memo2",
				Map.of(
						EntrySide.DEBIT, debitPID.publicId(),
						EntrySide.CREDIT, credit2PID.publicId()
				)
		);
		
		JournalUpdateResponse updatedJournal = financeCommand.updateJournal(memberPID, ledgerPID, journalUpdateRequest);
		
		assertThat(updatedJournal.message()).isEqualTo(
				"[거래번호: " + journalUpdateRequest.journalId() + " / 지출거래] 신한신용카드 계좌에서 생활용품(으)로 50,000원이 지출된 내역으로 수정 완료되었습니다."
		);
		
		assertThat(updatedJournal.journalId()).isEqualTo(journal.journalId());
	}
	
	private PublicId createLedger(PublicId memberPublicId, LedgerCreateRequest ledgerCreateRequest) {
		PublicId createdLedgerPID = new PublicId(financeCommand.createLedger(memberPublicId, ledgerCreateRequest).ledgerPublicId());
		
		em.flush();
		em.clear();
		
		return createdLedgerPID;
	}
	
	private PublicId createAccount(PublicId memberPublicId, PublicId ledgerPublicId, AccountCreateRequest accountCreateRequest) {
		AccountCreateResponse account = financeCommand.createAccount(memberPublicId, ledgerPublicId, accountCreateRequest);
		
		em.flush();
		em.clear();
		
		return new PublicId(account.accountPublicId());
	}
	
}
