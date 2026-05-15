package com.easyplan.application.finance;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.YearMonth;
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
import com.easyplan.finance.application.usecase.response.query.MonthlyTrendResponse.MonthlyTrendElement;
import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.application.provided.AccountFinder;
import com.easyplan.finance.application.provided.LedgerFinder;
import com.easyplan.finance.application.usecase.FinanceCommand;
import com.easyplan.finance.application.usecase.FinanceQuery;
import com.easyplan.finance.application.usecase.response.query.LedgerAssetSummary;
import com.easyplan.finance.application.usecase.response.query.MonthlyAssetSummary;
import com.easyplan.finance.domain.journal.TransactionType;
import com.easyplan.finance.domain.journal.request.JournalRequest.JournalCreateRequest;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.LedgerType;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;
import com.easyplan.finance.domain.ledger.request.LedgerUpdateRequest.LedgerFiscalUpdate;
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
		
		ledgerPID = new PublicId(financeCommand.createLedger(member.getId(), ledgerCreateRequest).ledgerPublicId());
		
		em.flush();
		em.clear();
		
		ledger = ledgerFinder.findByLedgerOwner(member.getId(), ledgerPID);
		
		assPID = new PublicId(financeCommand.createAccount(member.getId(), ledgerPID, FinanceFix.assetAccountCreateRequest()).accountPublicId());
		incPID = new PublicId(financeCommand.createAccount(member.getId(), ledgerPID, FinanceFix.incomeAccountCreateRequest()).accountPublicId());
		expPID = new PublicId(financeCommand.createAccount(member.getId(), ledgerPID, FinanceFix.expenseAccountCreateRequest()).accountPublicId());
		liaPID = new PublicId(financeCommand.createAccount(member.getId(), ledgerPID, FinanceFix.liabilitiesAccountCreateRequest()).accountPublicId());
		
		em.flush();
		em.clear();
	}
	
	@Test
	@DisplayName("createJournalExpense Test")
	void createJournalExpenseTest() {
		for(int i = 0; i < 10; i++) {
			// 1회 amount: 100,000 
			createJournalIncome();
			createJournalExpenseFromAsset();
			createJournalExpenseFromLiabilities();
		}
		
		em.flush();
		em.clear();
		
		LedgerAssetSummary result = financeQuery.getNetWorthSummary(member.getId(), ledgerPID);
		
		assertThat(result.totalAsset()).isEqualTo(0L);
		assertThat(result.totalLiabilities()).isEqualTo(1000000L);
		assertThat(result.netWorth()).isEqualTo(-1000000L);
		
		System.out.println(result);
		
		// ms 측정
		financeQuery.getNetWorthSummary(member.getId(), ledgerPID);
	}
	
	@Test
	@DisplayName("monthlyCashSummary Test")
	void monthlyCashSummary() {
		for(int i = 0; i < 10; i++) {
			// 1회 amount: 100,000
			// transactionDate : 2026-04-20
			createJournalIncome();									// 총 수입이 100만원
			createJournalExpenseFromAsset();				// 자산계정의 소비 내역이 100만원
			createJournalExpenseFromLiabilities();	// 부채계정의 소비 내역이 200만원
		}
		
		em.flush();
		em.clear();
		
		MonthlyAssetSummary result = financeQuery.getMonthlyCashSummary(member.getId(), ledgerPID, YearMonth.of(2026, 4));
		
		assertThat(result.monthlyTotalExpense()).isEqualTo(2000000L);
		assertThat(result.monthlyTotalIncome()).isEqualTo(1000000L);
	}
	
	@Test
	@DisplayName("기간 손익")
	void monthlyNetWorthTrend() {
		financeCommand.updateLedgerFiscal(member.getId(), ledgerPID, new LedgerFiscalUpdate(10));
		
		// 준비
		financeCommand.updateLedgerFiscal(member.getId(), ledgerPID, new LedgerFiscalUpdate(10));
		
		em.flush();
		em.clear();
		
		createJournal(
				TransactionType.INCOME,
				LocalDate.of(2026, 4, 20),
				1_000_000L,
				assPID,
				incPID
		);
		createJournal(
				TransactionType.EXPENSE,
				LocalDate.of(2026, 5, 9),
				200_000L,
				expPID,
				assPID
		);
		createJournal(
				TransactionType.INCOME,
				LocalDate.of(2026, 5, 10),
				500_000L,
				assPID,
				incPID
		);
		createJournal(
				TransactionType.EXPENSE,
				LocalDate.of(2026, 6, 1),
				300_000L,
				expPID,
				liaPID
		);
		
		em.flush();
		em.clear();
		
		// 실행
		List<MonthlyTrendElement> result = financeQuery.getMonthlyNetWorthTrend(member.getId(), ledgerPID, YearMonth.of(2026, 5), 2);
		
		// 결과
		assertThat(result).hasSize(2);
		
		MonthlyTrendElement april = result.get(0);
		assertThat(april.period()).isEqualTo(YearMonth.of(2026, 4));
		assertThat(april.netWorth()).isEqualTo(800_000L);
		assertThat(april.totalIncome()).isEqualTo(1_000_000L);
		assertThat(april.totalExpense()).isEqualTo(200_000L);
		assertThat(april.profitRate()).isEqualTo(80L);
		
		MonthlyTrendElement may = result.get(1);
		assertThat(may.period()).isEqualTo(YearMonth.of(2026, 5));
		assertThat(may.netWorth()).isEqualTo(1_000_000L);
		assertThat(may.totalIncome()).isEqualTo(500_000L);
		assertThat(may.totalExpense()).isEqualTo(300_000L);
		assertThat(may.profitRate()).isEqualTo(40L);
	}
	
	
	
	private void createJournalExpenseFromLiabilities() {
		JournalCreateRequest request = FinanceFix.journalCreate(TransactionType.EXPENSE,
				accountFinder.findAccount(ledger, expPID),
				accountFinder.findAccount(ledger, liaPID)
		);
		
		financeCommand.createJournal(member.getId(), ledgerPID, request);
	}
	
	private void createJournalExpenseFromAsset() {
		JournalCreateRequest request = FinanceFix.journalCreate(TransactionType.EXPENSE,
				accountFinder.findAccount(ledger, expPID),
				accountFinder.findAccount(ledger, assPID)
		);
		
		financeCommand.createJournal(member.getId(), ledgerPID, request);
	}
	
	private void createJournalIncome() {
		JournalCreateRequest request = FinanceFix.journalCreate(TransactionType.INCOME,
				accountFinder.findAccount(ledger, assPID),
				accountFinder.findAccount(ledger, incPID)
		);
		
		financeCommand.createJournal(member.getId(), ledgerPID, request);
	}
	
	private void createJournal(TransactionType type, LocalDate date, Long amount, PublicId debitPID, PublicId creditPID) {
		JournalCreateRequest request = new JournalCreateRequest(
				date,
				amount,
				"memo",
				type,
				Map.of(
						EntrySide.DEBIT, debitPID.publicId(),
						EntrySide.CREDIT, creditPID.publicId()
				)
		);
		
		financeCommand.createJournal(member.getId(), ledgerPID, request);
	}
	
	
}
