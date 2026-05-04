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
import com.easyplan.finance.application.provided.account.AccountCommand;
import com.easyplan.finance.application.provided.ledger.LedgerCommand;
import com.easyplan.finance.application.required.AccountRepository;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountBasicTemplate;
import com.easyplan.finance.domain.account.AccountOptionTemplate;
import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.account.request.AccountCreateRequest;
import com.easyplan.finance.domain.account.request.AccountUpdateRequest.AccountInfoUpdate;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.LedgerType;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.fixture.MemberFix;
import com.easyplan.member.application.provided.MemberCommand;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.request.MemberRegisterRequest;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class AccountApplicationTest {
	
	@Autowired
	private EntityManager em;
	
	@Autowired
	private AccountCommand accountCommand;
	
	@Autowired
	private LedgerCommand ledgerCommand;
	
	@Autowired
	private MemberCommand memberCommand;
	
	@Autowired
	private AccountRepository accountRepo;
	
	Member member;
	Member member2;
	MemberRegisterRequest request;
	MemberRegisterRequest request2;
	LedgerCreateRequest ledgerCreateRequest;
	Ledger ledger;
	Ledger ledger2;
	int accountSize = 0;
	
	PublicId accountPublicId;
	@BeforeEach
	void setUp() {
		request = MemberFix.memberRegisterRequest();
		request2 = MemberFix.memberRegisterRequest2();
		
		member = memberCommand.register(request);
		member2 = memberCommand.register(request2);
		
		member.activate();
		member2.activate();
		
		List<AccountBasicTemplate> accountTemplate = List.of(AccountBasicTemplate.values());
		accountSize = accountTemplate.size();
		
		ledgerCreateRequest = new LedgerCreateRequest(
				LedgerType.PERSONAL,
				"테스트 가계부",
				"테스트를 위한 가계부 생성",
				accountTemplate);
		
		ledger = ledgerCommand.createLedger(member.getMemberPublicId(), ledgerCreateRequest);
		ledger2 = ledgerCommand.createLedger(member2.getMemberPublicId(), ledgerCreateRequest);
		
		em.flush();
		em.clear();
	}
	
	@Test
	@DisplayName("계정 생성")
	void accountCreate() {
		String accountName = "국민은행";
		String accountDescription = "월급받는 통장";
		
		AccountCreateRequest accountCreate = new AccountCreateRequest(
				ledger.getLedgerPublicId().publicId(),
				AccountType.ASSET,
				AccountOptionTemplate.BANK_ACCOUNT,
				accountName,
				accountDescription
		);
		
		Account account = accountCommand.createAccount(member.getMemberPublicId(), ledger.getLedgerPublicId(), accountCreate);
		
		assertThat(account.getAccountName()).isEqualTo(accountName);
		assertThat(account.getAccountDescription()).isEqualTo(accountDescription);
		assertThat(accountRepo.count()).isEqualTo((accountSize * 2) + 1);
		
		String accountName2 = "롯데캐시";
		String accountDescription2 = "롯데오지마라";
		
		AccountCreateRequest accountCreate2 = new AccountCreateRequest(
				ledger.getLedgerPublicId().publicId(),
				AccountType.ASSET,
				AccountOptionTemplate.BANK_ACCOUNT,
				accountName2,
				accountDescription2
		);
		
		Account account2 = accountCommand.createAccount(member2.getMemberPublicId(), ledger2.getLedgerPublicId(), accountCreate2);
		
		assertThat(account2.getAccountName()).isEqualTo(accountName2);
		assertThat(account2.getAccountDescription()).isEqualTo(accountDescription2);
		assertThat(accountRepo.count()).isEqualTo((accountSize * 2) + 2);
	}
	
	@Test
	@DisplayName("계정 정보 변경")
	void accountInfoUpdate() {
		String accountName = "국민은행";
		String accountDescription = "월급받는 통장";
		
		AccountCreateRequest accountCreate = new AccountCreateRequest(
				ledger.getLedgerPublicId().publicId(),
				AccountType.ASSET,
				AccountOptionTemplate.BANK_ACCOUNT,
				accountName,
				accountDescription
		);
		
		Account account = accountCommand.createAccount(member.getMemberPublicId(), ledger.getLedgerPublicId(), accountCreate);
		
		em.flush();
		em.clear();
		
		String updateName = "국민은행";
		String updateDescription = "월급받는 통장2";
		
		AccountInfoUpdate accountInfo = new AccountInfoUpdate(updateName, updateDescription, AccountOptionTemplate.BANK_ACCOUNT);
		
		accountCommand.updateAccountInfo(member.getMemberPublicId(), account.getAccountPublicId(), accountInfo);
		
		em.flush();
		em.clear();
		
		Account updated = accountRepo.findByAccountPublicId(account.getAccountPublicId());
		
		assertThat(updated.getAccountName()).isEqualTo(updateName);
		assertThat(updated.getAccountDescription()).isEqualTo(updateDescription);
	}
	
}
