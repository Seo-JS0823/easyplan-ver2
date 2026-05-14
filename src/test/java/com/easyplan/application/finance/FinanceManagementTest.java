package com.easyplan.application.finance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.YearMonth;
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
import com.easyplan.finance.application.provided.BudgetFinder;
import com.easyplan.finance.application.usecase.FinanceCommand;
import com.easyplan.finance.application.usecase.FinanceManagementCommand;
import com.easyplan.finance.application.usecase.FinanceManagementQuery;
import com.easyplan.finance.application.usecase.response.query.BudgetSummary.PeriodAccountBudgetSummary;
import com.easyplan.finance.domain.budget.Budget;
import com.easyplan.finance.domain.budget.exception.BudgetErrorCode;
import com.easyplan.finance.domain.budget.exception.BudgetException;
import com.easyplan.finance.domain.budget.request.BudgetCreateRequest;
import com.easyplan.finance.domain.budget.request.BudgetUpdateRequest;
import com.easyplan.finance.domain.journal.TransactionType;
import com.easyplan.fixture.MemberFix;
import com.easyplan.member.application.provided.MemberCommand;
import com.easyplan.member.domain.Member;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class FinanceManagementTest {

	@Autowired
	EntityManager em;
	
	@Autowired
	FinanceCommand financeCommand;
	
	@Autowired
	MemberCommand memberCommand;
	
	@Autowired
	FinanceManagementCommand financeManager;
	
	@Autowired
	BudgetFinder budgetFinder;
	
	// 예산쿼리 긴급 테스트
	@Autowired
	AccountFinder accountFinder;
	@Autowired
	FinanceManagementQuery financeManagerQuery;
	
	Member member;
	
	PublicId memberPID;
	
	Long memberId;
	
	PublicId ledgerPID;
	
	PublicId assPID;
	
	PublicId incPID;
	
	PublicId expPID;
	
	PublicId liaPID;
	
	@BeforeEach
	void setUp() {
		member = memberCommand.register(MemberFix.memberRegisterRequest());
		
		member.activate();
		
		memberId = member.getId();
		memberPID = member.getMemberPublicId();
		
		em.flush();
		
		ledgerPID = new PublicId(financeCommand.createLedger(memberId, FinanceFix.ledgerCreateRequest()).ledgerPublicId());
		
		assPID = new PublicId(financeCommand.createAccount(memberId, ledgerPID, FinanceFix.assetAccountCreateRequest()).accountPublicId());
		incPID = new PublicId(financeCommand.createAccount(memberId, ledgerPID, FinanceFix.incomeAccountCreateRequest()).accountPublicId());
		expPID = new PublicId(financeCommand.createAccount(memberId, ledgerPID, FinanceFix.expenseAccountCreateRequest()).accountPublicId());
		liaPID = new PublicId(financeCommand.createAccount(memberId, ledgerPID, FinanceFix.liabilitiesAccountCreateRequest()).accountPublicId());
		
		em.clear();
	}
	
	@Test
	@DisplayName("가계부 지정된 달 예산 현황 전체 조회")
	void summarizePeriodBudgetSelect() {
		BudgetCreateRequest given = FinanceFix.budgetCreateRequest();
		
		PublicId exp1PID = new PublicId(financeCommand.createAccount(memberId, ledgerPID, FinanceFix.expenseAccountCreateRequest("지출계정 1번")).accountPublicId());
		PublicId exp2PID = new PublicId(financeCommand.createAccount(memberId, ledgerPID, FinanceFix.expenseAccountCreateRequest("지출계정 2번")).accountPublicId());
		PublicId exp3PID = new PublicId(financeCommand.createAccount(memberId, ledgerPID, FinanceFix.expenseAccountCreateRequest("지출계정 3번")).accountPublicId());
		PublicId exp4PID = new PublicId(financeCommand.createAccount(memberId, ledgerPID, FinanceFix.expenseAccountCreateRequest("지출계정 4번")).accountPublicId());
		PublicId exp5PID = new PublicId(financeCommand.createAccount(memberId, ledgerPID, FinanceFix.expenseAccountCreateRequest("지출계정 5번")).accountPublicId());
		
		em.flush();
		em.clear();
		
		List<PublicId> expPids = List.of(exp1PID, exp2PID, exp3PID, exp4PID, exp5PID);
		
		expPids.forEach((pid) -> financeManager.createBudget(memberId, ledgerPID, pid, given));
		
		em.flush();
		em.clear();
		
		expPids.forEach((pid) -> {
			financeCommand.createJournal(memberId, ledgerPID, FinanceFix.journalCreate(
					TransactionType.EXPENSE,
					accountFinder.findActiveAccountOwner(memberId, ledgerPID, pid),
					accountFinder.findActiveAccountOwner(memberId, ledgerPID, assPID)
			));
		});
		
		em.flush();
		em.clear();
		
		System.out.println("Summarize Multi Query ===");
		List<PeriodAccountBudgetSummary> summarize = financeManagerQuery.getMultiPeriodBudgetSummary(memberId, ledgerPID, YearMonth.of(2026, 4));
		
		summarize.forEach((sum) -> {
			assertThat(sum.limitAmount()).isEqualTo(given.limitAmount());
			assertThat(sum.totalExpense()).isEqualTo(100_000L);
		});
		
		System.out.println(summarize);
	}
	
	@Test
	@DisplayName("해당 가계부의 전체 계정 예산 조회")
	void allBudgetSelect() {
		// 준비
		BudgetCreateRequest given = FinanceFix.budgetCreateRequest();
		
		PublicId exp1PID = new PublicId(financeCommand.createAccount(memberId, ledgerPID, FinanceFix.expenseAccountCreateRequest("지출계정 1번")).accountPublicId());
		PublicId exp2PID = new PublicId(financeCommand.createAccount(memberId, ledgerPID, FinanceFix.expenseAccountCreateRequest("지출계정 2번")).accountPublicId());
		PublicId exp3PID = new PublicId(financeCommand.createAccount(memberId, ledgerPID, FinanceFix.expenseAccountCreateRequest("지출계정 3번")).accountPublicId());
		PublicId exp4PID = new PublicId(financeCommand.createAccount(memberId, ledgerPID, FinanceFix.expenseAccountCreateRequest("지출계정 4번")).accountPublicId());
		PublicId exp5PID = new PublicId(financeCommand.createAccount(memberId, ledgerPID, FinanceFix.expenseAccountCreateRequest("지출계정 5번")).accountPublicId());
		
		em.flush();
		em.clear();
		
		List<PublicId> expPids = List.of(exp1PID, exp2PID, exp3PID, exp4PID, exp5PID);
		
		expPids.forEach((pid) -> financeManager.createBudget(memberId, ledgerPID, pid, given));
		
		em.flush();
		em.clear();
		
		// 실행
		System.out.println("FindBudgetByLedger 실행 =====");
		List<Budget> budgets = budgetFinder.findBudgetByLedger(memberId, ledgerPID, given.period());
		
		// 결과
		assertThat(budgets).hasSize(5);
		
		budgets.forEach(
				(budget) -> { 
					assertThat(budget.getLedger().getLedgerPublicId()).isEqualTo(ledgerPID);
					assertThat(budget.getPeriod()).isEqualTo(given.period());
					assertThat(budget.getAmount()).isEqualTo(given.limitAmount());
				}
		);
	}
	
	@Test
	@DisplayName("예산 설정")
	void createBudget() {
		// 준비
		BudgetCreateRequest given = FinanceFix.budgetCreateRequest();
		
		// 실행
		financeManager.createBudget(memberId, ledgerPID, expPID, given);
		
		em.flush();
		em.clear();
		
		Budget result = budgetFinder.findByBudget(memberId, ledgerPID, expPID, given.period());
		
		// 결과
		assertThat(result.getLedger().getLedgerPublicId()).isEqualTo(ledgerPID);
		assertThat(result.getAccount().getAccountPublicId()).isEqualTo(expPID);
		assertThat(result.getPeriod()).isEqualTo(given.period());
		assertThat(result.getAmount()).isEqualTo(given.limitAmount());
		assertThat(result.getAccountName()).isEqualTo(FinanceFix.expenseAccountCreateRequest().accountName());
	}
	
	@Test
	@DisplayName("예산 수정")
	void updateBudget() {
		// 준비
		BudgetCreateRequest budgetCreate = FinanceFix.budgetCreateRequest();
		
		financeManager.createBudget(memberId, ledgerPID, expPID, budgetCreate);
		
		em.flush();
		em.clear();
		
		BudgetUpdateRequest given = new BudgetUpdateRequest(1_500_000L);
		
		// 실행
		financeManager.updateBudget(memberId, ledgerPID, expPID, budgetCreate.period(), given);
		
		em.flush();
		em.clear();
		
		Budget result = budgetFinder.findByBudget(memberId, ledgerPID, expPID, budgetCreate.period());
		
		// 결과
		assertThat(result.getLedger().getLedgerPublicId()).isEqualTo(ledgerPID);
		assertThat(result.getAccount().getAccountPublicId()).isEqualTo(expPID);
		assertThat(result.getPeriod()).isEqualTo(budgetCreate.period());
		assertThat(result.getAmount()).isEqualTo(given.limitAmount());
		assertThat(result.getAccountName()).isEqualTo(FinanceFix.expenseAccountCreateRequest().accountName());
	}
	
	@Test
	@DisplayName("예산 삭제")
	void deleteBudget() {
		// 준비
		BudgetCreateRequest given = FinanceFix.budgetCreateRequest();
		
		financeManager.createBudget(memberId, ledgerPID, expPID, given);
		
		em.flush();
		em.clear();
		
		// 실행
		financeManager.deleteBudget(memberId, ledgerPID, expPID, given.period());
		
		em.flush();
		em.clear();
		
		// 결과
		assertThatThrownBy(() -> budgetFinder.findByBudget(memberId, ledgerPID, expPID, given.period()))
		.isInstanceOf(BudgetException.class)
		.hasMessageContaining(BudgetErrorCode.BUDGET_NOT_FOUND.getMessage());
	}
}
