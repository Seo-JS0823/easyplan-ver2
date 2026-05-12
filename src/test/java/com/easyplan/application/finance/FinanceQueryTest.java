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
import com.easyplan.finance.application.provided.AccountFinder;
import com.easyplan.finance.application.provided.LedgerFinder;
import com.easyplan.finance.application.usecase.FinanceCommand;
import com.easyplan.finance.application.usecase.FinanceQuery;
import com.easyplan.finance.application.usecase.response.query.AssetSummary;
import com.easyplan.finance.domain.journal.TransactionType;
import com.easyplan.finance.domain.journal.request.JournalRequest.JournalCreateRequest;
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
public class FinanceQueryTest {
	
	@Autowired
	EntityManager em;
	
	@Autowired
	FinanceQuery financeQuery;
	
	@Autowired
	FinanceCommand financeCommand;
	
	@Autowired
	MemberCommand memberCommand;
	
	@Autowired
	LedgerFinder ledgerFinder;
	
	@Autowired
	AccountFinder accountFinder;
	
	Member member;
	
	PublicId memberPID;
	
	MemberRegisterRequest memberRegisterRequest;
	
	Ledger ledger;
	
	PublicId ledgerPID;
	
	PublicId assPID;
	
	PublicId incPID;
	
	PublicId expPID;
	
	PublicId liaPID;
	
	@BeforeEach
	void setUp() {
		memberRegisterRequest = MemberFix.memberRegisterRequest();
		member = memberCommand.register(memberRegisterRequest);
		
		member.activate();
		
		memberPID = member.getMemberPublicId();
		
		em.flush();
		em.clear();
		
		LedgerCreateRequest ledgerCreateRequest = new LedgerCreateRequest(
				LedgerType.PERSONAL,
				"가계부 이름",
				"가계부 설명",
				List.of()
		);
		
		ledgerPID = new PublicId(financeCommand.createLedger(memberPID, ledgerCreateRequest).ledgerPublicId());
		
		em.flush();
		em.clear();
		
		ledger = ledgerFinder.findByLedgerOwner(memberPID, ledgerPID);
		
		assPID = new PublicId(financeCommand.createAccount(memberPID, ledgerPID, FinanceFix.assetAccountCreateRequest()).accountPublicId());
		incPID = new PublicId(financeCommand.createAccount(memberPID, ledgerPID, FinanceFix.incomeAccountCreateRequest()).accountPublicId());
		expPID = new PublicId(financeCommand.createAccount(memberPID, ledgerPID, FinanceFix.expenseAccountCreateRequest()).accountPublicId());
		liaPID = new PublicId(financeCommand.createAccount(memberPID, ledgerPID, FinanceFix.liabilitiesAccountCreateRequest()).accountPublicId());
		
		em.flush();
		em.clear();
	}
	
	@Test
	@DisplayName("createJournalExpense Test")
	void createJournalExpenseTest() {
		for(int i = 0; i < 1000; i++) {
			// amount: 100,000,000
			createJournalIncome();
		}
		
		for(int i = 0; i < 700; i++) {
			// amount: 70,000,000
			createJournalExpenseFromAsset();
		}
		
		for(int i = 0; i < 100; i++) {
			// amount: 10,000,000
			createJournalExpenseFromLiabilities();
		}
		
		em.flush();
		em.clear();
		
		AssetSummary result = financeQuery.getNetWorthSummary(memberPID, ledgerPID);
		
		assertThat(result.totalAsset()).isEqualTo(30000000L);
		assertThat(result.totalLiabilities()).isEqualTo(10000000L);
		assertThat(result.netWorth()).isEqualTo(20000000L);
		
		System.out.println(result);
		
		// ms 측정
		financeQuery.getNetWorthSummary(memberPID, ledgerPID);
	}
	
	private void createJournalExpenseFromLiabilities() {
		JournalCreateRequest request = FinanceFix.journalCreate(TransactionType.EXPENSE,
				accountFinder.findAccount(ledger, expPID),
				accountFinder.findAccount(ledger, liaPID)
				);
		
		financeCommand.createJournal(memberPID, ledgerPID, request);
	}
	
	private void createJournalExpenseFromAsset() {
		JournalCreateRequest request = FinanceFix.journalCreate(TransactionType.EXPENSE,
				accountFinder.findAccount(ledger, expPID),
				accountFinder.findAccount(ledger, assPID)
		);
		
		financeCommand.createJournal(memberPID, ledgerPID, request);
	}
	
	private void createJournalIncome() {
		JournalCreateRequest request = FinanceFix.journalCreate(TransactionType.INCOME,
				accountFinder.findAccount(ledger, assPID),
				accountFinder.findAccount(ledger, incPID)
		);
		
		financeCommand.createJournal(memberPID, ledgerPID, request);
	}
	
	
}
