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

import com.easyplan.finance.application.provided.account.AccountFinder;
import com.easyplan.finance.application.provided.ledger.LedgerCommand;
import com.easyplan.finance.application.provided.ledger.LedgerFinder;
import com.easyplan.finance.domain.account.AccountBasicTemplate;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.LedgerType;
import com.easyplan.finance.domain.ledger.exception.LedgerException;
import com.easyplan.finance.domain.ledger.exception.LedgerExceptionCode;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerInfoUpdate;
import com.easyplan.fixture.MemberFix;
import com.easyplan.member.application.provided.MemberCommand;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.exception.MemberException;
import com.easyplan.member.domain.exception.MemberExceptionCode;
import com.easyplan.member.domain.request.MemberAccountRequest.MemberDeactivate;
import com.easyplan.member.domain.request.MemberRegisterRequest;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class LedgerApplicationFailTest {

	@Autowired
	EntityManager em;
	
	@Autowired
	private MemberCommand memberCommand;
	
	@Autowired
	private LedgerCommand ledgerCommand;
	
	@Autowired
	private LedgerFinder ledgerFinder;
	
	@Autowired
	private AccountFinder accountFinder;
	
	Member member1;
	Member member2;
	MemberRegisterRequest request1;
	MemberRegisterRequest request2;
	LedgerCreateRequest ledgerCreateRequest;
	LedgerCreateRequest ledgerCreateRequest2;	
	int accountSize = 0;
	
	@BeforeEach
	void setUp() {
		request1 = MemberFix.memberRegisterRequest();
		request2 = MemberFix.memberRegisterRequest2();
		member1 = memberCommand.register(request1);
		member2 = memberCommand.register(request2);
		
		member1.activate();
		member2.activate();
		
		List<AccountBasicTemplate> accountTemplate = List.of(AccountBasicTemplate.values());
		accountSize = accountTemplate.size();
		
		ledgerCreateRequest = new LedgerCreateRequest(
				LedgerType.PERSONAL,
				"테스트 가계부",
				"테스트를 위한 가계부 생성",
				accountTemplate);
		
		ledgerCreateRequest2 = new LedgerCreateRequest(
				LedgerType.PERSONAL,
				"테스트 가계부2",
				"테스트를 위한 가계부 생성",
				accountTemplate);
	}
	
	@Test
	@DisplayName("가계부 이름, 설명 변경 다른 사용자 가계부 변경")
	void ledgerInfoUpdateFail_NotOwner() {
		Ledger memberLedger1 = createLedger1(ledgerCreateRequest);
		Ledger memberLedger2 = createLedger2(ledgerCreateRequest);
		
		String newName = "LedgerInfo";
		String newDescription = "LedgerInfoUpdateTest";
		
		LedgerInfoUpdate ledgerInfo = new LedgerInfoUpdate(newName, newDescription);
		
		assertThatThrownBy(() -> 
			ledgerCommand.updateLedgerInfo(member1.getMemberPublicId(), memberLedger2.getLedgerPublicId(), ledgerInfo)
		).isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerExceptionCode.LEDGER_NOT_FOUND.getMessage());
		
		assertThatThrownBy(() ->
			ledgerCommand.updateLedgerInfo(member2.getMemberPublicId(), memberLedger1.getLedgerPublicId(), ledgerInfo)
		)
		.isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerExceptionCode.LEDGER_NOT_FOUND.getMessage());
	}
	
	@Test
	@DisplayName("가계부 이름, 설명 변경 서비스 이용 불가 상태")
	void ledgerInfoUpdateFail_MemberCannotUseService() {
		Ledger memberLedger1 = createLedger1(ledgerCreateRequest);
		Ledger memberLedger2 = createLedger2(ledgerCreateRequest);
		
		memberDeactivate(member1, request1.password());
		memberDeactivate(member2, request2.password());
		
		String newName = "LedgerInfo";
		String newDescription = "LedgerInfoUpdateTest";
		
		LedgerInfoUpdate ledgerInfo = new LedgerInfoUpdate(newName, newDescription);
		
		assertThatThrownBy(() -> 
			ledgerCommand.updateLedgerInfo(member1.getMemberPublicId(), memberLedger1.getLedgerPublicId(), ledgerInfo)
		).isInstanceOf(MemberException.class)
		.hasMessageContaining(MemberExceptionCode.MEMBER_CANNOT_USE_SERVICE.getMessage());
		
		assertThatThrownBy(() -> 
			ledgerCommand.updateLedgerInfo(member2.getMemberPublicId(), memberLedger2.getLedgerPublicId(), ledgerInfo)
		).isInstanceOf(MemberException.class)
		.hasMessageContaining(MemberExceptionCode.MEMBER_CANNOT_USE_SERVICE.getMessage());
	}
	
	@Test
	@DisplayName("가계부 이름, 설명 변경 사용안함 상태의 가계부")
	void ledgerInfoUpdateFail_AlreadyLedger() {
		Ledger memberLedger1 = createLedger1(ledgerCreateRequest);
		Ledger memberLedger2 = createLedger2(ledgerCreateRequest);
		
		ledgerArchived(member1, memberLedger1);
		ledgerArchived(member2, memberLedger2);
		
		String newName = "LedgerInfo";
		String newDescription = "LedgerInfoUpdateTest";
		
		LedgerInfoUpdate ledgerInfo = new LedgerInfoUpdate(newName, newDescription);
		
		assertThatThrownBy(() -> 
			ledgerCommand.updateLedgerInfo(member1.getMemberPublicId(), memberLedger1.getLedgerPublicId(), ledgerInfo)
		).isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerExceptionCode.LEDGER_NOT_ACTIVE.getMessage());
		
		assertThatThrownBy(() -> 
			ledgerCommand.updateLedgerInfo(member2.getMemberPublicId(), memberLedger2.getLedgerPublicId(), ledgerInfo)
		).isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerExceptionCode.LEDGER_NOT_ACTIVE.getMessage());
	}
	
	@Test
	@DisplayName("가계부 이름, 설명 변경 동일 이름의 가계부가 존재")
	void ledgerInfoUpdateFail_DuplicateName() {
		Ledger memberLedger1 = createLedger1(ledgerCreateRequest);
		Ledger memberLedger2 = createLedger1(ledgerCreateRequest2);
		
		String newName = ledgerCreateRequest2.name();
		String newDescription = "LedgerInfoUpdateTest";
		
		LedgerInfoUpdate ledgerInfo = new LedgerInfoUpdate(newName, newDescription);
		
		String newName2 = ledgerCreateRequest.name();
		String newDescription2 = "LedgerInfoUpdateTestFailCase";
		
		LedgerInfoUpdate ledgerInfo2 =  new LedgerInfoUpdate(newName2, newDescription2);
		
		assertThatThrownBy(() -> 
			ledgerCommand.updateLedgerInfo(member1.getMemberPublicId(), memberLedger1.getLedgerPublicId(), ledgerInfo)
		).isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerExceptionCode.LEDGER_NAME_DUPLICATE.getMessage());
		
		assertThatThrownBy(() -> 
			ledgerCommand.updateLedgerInfo(member1.getMemberPublicId(), memberLedger2.getLedgerPublicId(), ledgerInfo2)
		).isInstanceOf(LedgerException.class)
		.hasMessageContaining(LedgerExceptionCode.LEDGER_NAME_DUPLICATE.getMessage());
	}
	
	private void ledgerArchived(Member member, Ledger ledger) {
		ledgerCommand.archived(member.getMemberPublicId(), ledger.getLedgerPublicId());
		
		em.flush();
		em.clear();
	}
	
	private void memberDeactivate(Member member, String currentPath) {
		memberCommand.deactivate(member.getMemberPublicId(), new MemberDeactivate(currentPath));
		
		em.flush();
		em.clear();
	}
	
	private Ledger createLedger1(LedgerCreateRequest ledgerCreate) {
		Ledger ledger =  ledgerCommand.createLedger(member1.getMemberPublicId(), ledgerCreate);
		em.flush();
		em.clear();
		
		return ledger;
	}
	
	private Ledger createLedger2(LedgerCreateRequest ledgerCreate) {
		Ledger ledger =  ledgerCommand.createLedger(member2.getMemberPublicId(), ledgerCreate);
		em.flush();
		em.clear();
		
		return ledger;
	}
}
