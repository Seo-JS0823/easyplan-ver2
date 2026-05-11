package com.easyplan.application.finance;

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
import com.easyplan.member.domain.exception.MemberException;
import com.easyplan.member.domain.exception.MemberExceptionCode;
import com.easyplan.member.domain.request.MemberAccountRequest.MemberDeactivate;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class FinanceCommandUsecaseFailTest {

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
	
	Member member1;
	Member member2;
	
	PublicId member1PID;
	PublicId member2PID;
	
	LedgerCreateRequest ledgerCreateRequest;
	LedgerCreateRequest ledgerCreateRequest2;
	
	LedgerInfoUpdate ledgerInfoUpdate;
	
	String password;
	
	@BeforeEach
	void setUp() {
		password = "password01@";
		
		member1 = memberCommand.register(MemberFix.memberRegisterRequest());
		member2 = memberCommand.register(MemberFix.memberRegisterRequest2());
		
		member1.activate();
		member2.activate();
		
		member1PID = member1.getMemberPublicId();
		member2PID = member2.getMemberPublicId();
		
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
		
		ledgerCreateRequest2 = new LedgerCreateRequest(
				LedgerType.PERSONAL,
				"Jubu",
				"Ledger_Description",
				List.of(
						AccountBasicTemplate.ASS01,
						AccountBasicTemplate.LIA01,
						AccountBasicTemplate.INC01,
						AccountBasicTemplate.EXP01
						)
				);
		
		ledgerInfoUpdate = new LedgerInfoUpdate("Ledger Name", "Ledger Memo");
	}
	
	@Test
	@DisplayName("가계부 생성 실패_회원 서비스 사용 상태 아님")
	void createLedger_NotUseService() {
		memberCommand.deactivate(member1PID, new MemberDeactivate(password));
		
		em.flush();
		em.clear();
		
		assertThatThrownBy(() -> financeCommand.createLedger(member1PID, ledgerCreateRequest))
		.isInstanceOf(MemberException.class)
		.hasMessageContaining(MemberExceptionCode.MEMBER_CANNOT_USE_SERVICE.getMessage());
	}
	
	@Test
	@DisplayName("가계부 생성 실패_동일 이름의 가계부 존재")
	void createLedger_DuplicateLedgerName() {
		financeCommand.createLedger(member1PID, ledgerCreateRequest);
		
		em.flush();
		em.clear();
		
		assertThatThrownBy(() -> financeCommand.createLedger(member1PID, ledgerCreateRequest))
		.isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerErrorCode.LEDGER_NAME_DUPLICATE.getMessage());
	}
	
	@Test
	@DisplayName("가계부 정보 수정 실패_내 소유가 아닌 가계부")
	void updateLedgerInfo_NotOwner() {
		PublicId createdLedgerPIDMember1 = createLedger(member1PID, ledgerCreateRequest);
		PublicId createdLedgerPIDMember2 = createLedger(member2PID, ledgerCreateRequest);
		
		assertThatThrownBy(() -> financeCommand.updateLedgerInfo(member1PID, createdLedgerPIDMember2, ledgerInfoUpdate))
		.isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerErrorCode.LEDGER_NOT_FOUND.getMessage());
		
		assertThatThrownBy(() -> financeCommand.updateLedgerInfo(member2PID, createdLedgerPIDMember1, ledgerInfoUpdate))
		.isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerErrorCode.LEDGER_NOT_FOUND.getMessage());
	}
	
	@Test
	@DisplayName("가계부 정보 수정 실패_동일 이름의 가계부 존재")
	void updateLedgerInfo_DuplicateLedgerName() {
		PublicId createdLedgerPIDLedger1 = createLedger(member1PID, ledgerCreateRequest);
		createLedger(member1PID, ledgerCreateRequest2);
		
		LedgerInfoUpdate failLedgerInfo = new LedgerInfoUpdate(ledgerCreateRequest2.name(), ledgerCreateRequest2.description());
		
		assertThatThrownBy(() -> financeCommand.updateLedgerInfo(member1PID, createdLedgerPIDLedger1, failLedgerInfo))
		.isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerErrorCode.LEDGER_NAME_DUPLICATE.getMessage());
	}
	
	@Test
	@DisplayName("가계부 회계 시작일 수정 실패_지원하지 않는 날짜")
	void updateLedgerFiscal_DayError() {
		PublicId createdLedgerPIDLedger1 = createLedger(member1PID, ledgerCreateRequest);
		
		LedgerFiscalUpdate failFiscalUpdate = new LedgerFiscalUpdate(32);
		
		assertThatThrownBy(() -> financeCommand.updateLedgerFiscal(member1PID, createdLedgerPIDLedger1, failFiscalUpdate))
		.isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerErrorCode.FISCAL_OVER_DAY.getMessage());
	}
	
	@Test
	@DisplayName("가계부 삭제 실패_내 소유가 아닌 가계부")
	void deleteLedger_NotOwner() {
		PublicId createdLedgerPIDMember1 = createLedger(member1PID, ledgerCreateRequest);
		PublicId createdLedgerPIDMember2 = createLedger(member2PID, ledgerCreateRequest);
		
		assertThatThrownBy(() -> financeCommand.deleteLedger(member1PID, createdLedgerPIDMember2))
		.isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerErrorCode.LEDGER_NOT_FOUND.getMessage());
		
		assertThatThrownBy(() -> financeCommand.deleteLedger(member2PID, createdLedgerPIDMember1))
		.isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerErrorCode.LEDGER_NOT_FOUND.getMessage());
	}
	
	@Test
	@DisplayName("계정 항목 생성 실패_지원하지 않는 계정 옵션")
	void createAccount_TypeMismatch() {
		PublicId createdLedgerPIDMember1 = createLedger(member1PID, ledgerCreateRequest);
		
		AccountCreateRequest failAccountCreateRequest = new AccountCreateRequest(
				AccountType.ASSET,
				"Account",
				"Memo",
				AccountOptionTemplate.VARIABLE_EXPENSE
		);
		
		assertThatThrownBy(() -> financeCommand.createAccount(member1PID, createdLedgerPIDMember1, failAccountCreateRequest))
		.isInstanceOf(AccountException.class)
		.hasMessageContaining(AccountErrorCode.ACCOUNT_TYPE_MISMATH.getMessage());
	}
	
	@Test
	@DisplayName("계정 항목 수정 실패_지원하지 않는 계정 옵션")
	void updateAccount_TypeMismatch() {
		PublicId createdLedgerPIDMember1 = createLedger(member1PID, ledgerCreateRequest);
		
		AccountCreateRequest accountCreateRequest = new AccountCreateRequest(
				AccountType.ASSET,
				"Account",
				"Memo",
				AccountOptionTemplate.BANK_ACCOUNT
		);
		
		PublicId createdAccountPID = new PublicId(
				financeCommand.createAccount(member1PID, createdLedgerPIDMember1, accountCreateRequest).accountPublicId()
		);
		
		em.flush();
		em.clear();
		
		Account account = accountFinder.findAccount(
				ledgerFinder.findByLedger(createdLedgerPIDMember1),
				createdAccountPID
		);
		
		AccountUpdateRequest failAccountUpdateRequest = new AccountUpdateRequest(
				"Account",
				"Memo",
				AccountOptionTemplate.EQUITY
		);
		
		em.flush();
		em.clear();
		
		assertThatThrownBy(() -> financeCommand.updateAccount(
				member1PID,
				createdLedgerPIDMember1,
				account.getAccountPublicId(),
				failAccountUpdateRequest
		))
		.isInstanceOf(AccountException.class)
		.hasMessageContaining(AccountErrorCode.ACCOUNT_TYPE_MISMATH.getMessage());
	}
	
	// NEXT: Account Deactivate Fail Test
	
	private PublicId createLedger(PublicId memberPublicId, LedgerCreateRequest ledgerCreateRequest) {
		PublicId createdLedgerPID = new PublicId(financeCommand.createLedger(memberPublicId, ledgerCreateRequest).ledgerPublicId());
		
		em.flush();
		em.clear();
		
		return createdLedgerPID;
	}
	
}
