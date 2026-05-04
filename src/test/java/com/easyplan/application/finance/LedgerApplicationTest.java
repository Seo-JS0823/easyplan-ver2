package com.easyplan.application.finance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.adapter.webapi.response.LedgerCreateResponse;
import com.easyplan.finance.application.provided.account.AccountFinder;
import com.easyplan.finance.application.provided.ledger.LedgerCommand;
import com.easyplan.finance.application.provided.ledger.LedgerFinder;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountBasicTemplate;
import com.easyplan.finance.domain.account.AccountCategory;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.LedgerStatus;
import com.easyplan.finance.domain.ledger.LedgerType;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerFiscalDayUpdate;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerInfoUpdate;
import com.easyplan.fixture.MemberFix;
import com.easyplan.member.application.provided.MemberCommand;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.request.MemberRegisterRequest;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class LedgerApplicationTest {
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
	
	Member member;
	MemberRegisterRequest request;
	LedgerCreateRequest ledgerCreateRequest;
	int accountSize = 0;
	
	@BeforeEach
	void setUp() {
		request = MemberFix.memberRegisterRequest();
		member = memberCommand.register(request);
		
		member.activate();
		
		List<AccountBasicTemplate> accountTemplate = List.of(AccountBasicTemplate.values());
		accountSize = accountTemplate.size();
		
		ledgerCreateRequest = new LedgerCreateRequest(
				LedgerType.PERSONAL,
				"테스트 가계부",
				"테스트를 위한 가계부 생성",
				accountTemplate);
	}
	
	@Test
	@DisplayName("가계부 생성")
	void ledgerCreate() {
		LedgerCreateResponse result = LedgerCreateResponse.of(ledgerCommand.createLedger(member.getMemberPublicId(), ledgerCreateRequest));
		
		assertThat(result.ledgerName()).isEqualTo(ledgerCreateRequest.name());
		assertThat(result.ledgerPublicId().equals(member.getMemberPublicId().publicId())).isFalse();
		
		em.flush();
		em.clear();
		
		Ledger createdLedger = ledgerFinder.findByLedgerPublicId(member.getMemberPublicId(), new PublicId(result.ledgerPublicId()));
		
		List<Account> createdAccounts = accountFinder.findByLedgerId(createdLedger.getId());
		assertThat(createdAccounts.size() == accountSize).isTrue();
		
		List<AccountCategory> createdCategories = accountFinder.findCategoryByLedgerId(createdLedger.getId());
		assertThat(createdCategories.size() == 5).isTrue();
	}
	
	@Test
	@DisplayName("가계부 이름, 설명 변경")
	void ledgerInfoUpdate() {
		Ledger ledger = createLedger();
		
		String newName = "LedgerInfo";
		String newDescription = "LedgerInfoUpdateTest";
		
		LedgerInfoUpdate ledgerInfo = new LedgerInfoUpdate(newName, newDescription);
		
		Ledger updated = ledgerCommand.updateLedgerInfo(member.getMemberPublicId(), ledger.getLedgerPublicId(), ledgerInfo);
		
		assertThat(updated.getName()).isEqualTo(newName);
		assertThat(updated.getDescription()).isEqualTo(newDescription);
	}
	
	@Test
	@DisplayName("가계부 사용안함 처리")
	void ledgerArchived() {
		Ledger ledger = createLedger();
		
		Ledger updated = ledgerCommand.archived(member.getMemberPublicId(), ledger.getLedgerPublicId());
		
		assertThat(updated.getStatus()).isEqualTo(LedgerStatus.ARCHIVED);
	}
	
	@Test
	@DisplayName("가계부 회계 시작일 변경")
	void ledgerFiscalDayUpdate() {
		Ledger ledger = createLedger();
		
		Integer fiscalDay = 10;
		LedgerUpdateRequest.LedgerFiscalDayUpdate fiscalDayUpdate = new LedgerFiscalDayUpdate(fiscalDay);
		
		Ledger updated = ledgerCommand.updateLedgerFiscalDay(member.getMemberPublicId(), ledger.getLedgerPublicId(), fiscalDayUpdate);
		
		assertThat(updated.getFiscalStartDay().getDay()).isEqualTo(fiscalDay);
	}
	
	@Test
	@DisplayName("가계부 활성화 처리")
	void LedgerReactivate() {
		Ledger ledger = createLedger();
		
		Ledger archived = ledgerCommand.archived(member.getMemberPublicId(), ledger.getLedgerPublicId());
		
		assertThat(archived.getStatus()).isEqualTo(LedgerStatus.ARCHIVED);
		
		em.flush();
		em.clear();
		
		Ledger updated = ledgerCommand.reactivate(member.getMemberPublicId(), ledger.getLedgerPublicId());
		
		assertThat(updated.getStatus()).isEqualTo(LedgerStatus.ACTIVE);
	}
	
	private Ledger createLedger() {
		Ledger ledger =  ledgerCommand.createLedger(member.getMemberPublicId(), ledgerCreateRequest);
		
		return ledger;
	}
	
}
