package com.easyplan.application.finance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

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
import com.easyplan.finance.application.usecase.FinanceCommandUsecase;
import com.easyplan.finance.application.usecase.response.AccountResponse.AccountCreateResponse;
import com.easyplan.finance.application.usecase.response.LedgerResponse.LedgerCreateResponse;
import com.easyplan.finance.application.usecase.response.LedgerResponse.LedgerFiscalUpdateResponse;
import com.easyplan.finance.application.usecase.response.LedgerResponse.LedgerInfoUpdateResponse;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountBasicTemplate;
import com.easyplan.finance.domain.account.AccountOptionTemplate;
import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.account.exception.AccountErrorCode;
import com.easyplan.finance.domain.account.exception.AccountException;
import com.easyplan.finance.domain.account.request.AccountRequest.AccountCreateRequest;
import com.easyplan.finance.domain.account.request.AccountRequest.AccountUpdateRequest;
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
	private FinanceCommandUsecase financeCommand;
	
	@Autowired
	private LedgerFinder ledgerFinder;
	
	@Autowired
	private AccountFinder accountFinder;
	
	@Autowired
	private MemberCommand memberCommand;
	
	@Autowired
	private EntityManager em;
	
	Member member;
	MemberRegisterRequest memberRegisterRequest;
	
	@BeforeEach
	void setUp() {
		memberRegisterRequest = MemberFix.memberRegisterRequest();
		member = memberCommand.register(memberRegisterRequest);
		
		member.activate();
		
		em.flush();
		em.clear();
	}
	
	@Test
	@DisplayName("가계부 생성 테스트")
	void createLedger() {
		LedgerCreateRequest ledgerCreateRequest = new LedgerCreateRequest(
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
		
		LedgerCreateResponse accounts = financeCommand.createLedger(member.getMemberPublicId(), ledgerCreateRequest);
		
		assertThat(accounts.accountCount()).isEqualTo(5);
	}
	
	@Test
	@DisplayName("가계부 정보 수정 테스트")
	void updateLedgerInfo() {
		LedgerCreateRequest ledgerCreateRequest = new LedgerCreateRequest(
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
		
		LedgerCreateResponse accounts = financeCommand.createLedger(member.getMemberPublicId(), ledgerCreateRequest);
		
		String ledgerName = "Ledger_Rename";
		String ledgerDescription = "Ledger_Redescription";
		
		LedgerInfoUpdate ledgerInfoUpdateRequest = new LedgerInfoUpdate(ledgerName, ledgerDescription);
		
		LedgerInfoUpdateResponse response = financeCommand.updateLedgerInfo(
				member.getMemberPublicId(),
				new PublicId(accounts.ledgerPublicId()),
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
		LedgerCreateRequest ledgerCreateRequest = new LedgerCreateRequest(
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
		
		LedgerCreateResponse accounts = financeCommand.createLedger(member.getMemberPublicId(), ledgerCreateRequest);
		
		LedgerFiscalUpdate ledgerFiscalUpdateRequest = new LedgerFiscalUpdate(10);
		
		LedgerFiscalUpdateResponse response = financeCommand.updateLedgerFiscal(
				member.getMemberPublicId(),
				new PublicId(accounts.ledgerPublicId()),
				ledgerFiscalUpdateRequest
		);
		
		em.flush();
		em.clear();
		
		assertThat(response.fiscalDay()).isEqualTo(ledgerFiscalUpdateRequest.fiscalDay());
	}
	
	@Test
	@DisplayName("가계부 삭제 테스트")
	void deleteLedger() {
		LedgerCreateRequest ledgerCreateRequest = new LedgerCreateRequest(
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
		
		LedgerCreateResponse ledger = financeCommand.createLedger(member.getMemberPublicId(), ledgerCreateRequest);
		
		em.flush();
		em.clear();
		
		financeCommand.deleteLedger(member.getMemberPublicId(), new PublicId(ledger.ledgerPublicId()));
		
		em.flush();
		em.clear();
		
		assertThatThrownBy(() -> ledgerFinder.findByLedger(new PublicId(ledger.ledgerPublicId())))
		.isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerErrorCode.LEDGER_NOT_FOUND.getMessage());
	}
	
	@Test
	@DisplayName("계정 항목 생성")
	void createAccount() {
		LedgerCreateRequest ledgerCreateRequest = new LedgerCreateRequest(
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
		
		LedgerCreateResponse ledger = financeCommand.createLedger(member.getMemberPublicId(), ledgerCreateRequest);
		
		em.flush();
		em.clear();
		
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
				new PublicId(ledger.ledgerPublicId()),
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
		LedgerCreateRequest ledgerCreateRequest = new LedgerCreateRequest(
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
		
		LedgerCreateResponse ledger = financeCommand.createLedger(member.getMemberPublicId(), ledgerCreateRequest);
		
		em.flush();
		em.clear();
		
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
				new PublicId(ledger.ledgerPublicId()),
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
				new PublicId(ledger.ledgerPublicId()),
				new PublicId(account.accountPublicId()),
				accountUpdateRequest
		);
		
		em.flush();
		em.clear();
		
		Account result = accountFinder.findActiveAccount(
				ledgerFinder.findByLedger(new PublicId(ledger.ledgerPublicId())),
				new PublicId(account.accountPublicId())
		);
		
		assertThat(result.getAccountName()).isEqualTo(newAccountName);
		assertThat(result.getAccountDescription()).isEqualTo(newDescription);
		assertThat(result.getOption().getOptionName()).isEqualTo(AccountOptionTemplate.BANK_ACCOUNT.getOptionName());
	}
	
	@Test
	@DisplayName("계정 항목 삭제 (Deactivate)")
	void deactivateAccount() {
		LedgerCreateRequest ledgerCreateRequest = new LedgerCreateRequest(
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
		
		LedgerCreateResponse ledger = financeCommand.createLedger(member.getMemberPublicId(), ledgerCreateRequest);
		
		em.flush();
		em.clear();
		
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
				new PublicId(ledger.ledgerPublicId()),
				accountCreateRequest
		);
		
		em.flush();
		em.clear();
		
		financeCommand.deactivateAccount(member.getMemberPublicId(),
				new PublicId(ledger.ledgerPublicId()),
				new PublicId(account.accountPublicId())
		);
		
		em.flush();
		em.clear();
		
		assertThatThrownBy(() -> accountFinder.findActiveAccount(
				ledgerFinder.findByLedger(new PublicId(ledger.ledgerPublicId())),
				new PublicId(account.accountPublicId())))
		.isInstanceOf(AccountException.class)
		.hasMessageContaining(AccountErrorCode.ACCOUNT_NOT_FOUND.getMessage());
	}
	
}
