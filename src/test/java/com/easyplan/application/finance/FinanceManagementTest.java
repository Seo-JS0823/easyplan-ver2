package com.easyplan.application.finance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.YearMonth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.BudgetFinder;
import com.easyplan.finance.application.usecase.FinanceCommand;
import com.easyplan.finance.application.usecase.FinanceManagementCommand;
import com.easyplan.finance.domain.budget.Budget;
import com.easyplan.finance.domain.budget.exception.BudgetErrorCode;
import com.easyplan.finance.domain.budget.exception.BudgetException;
import com.easyplan.finance.domain.budget.request.BudgetCreateRequest;
import com.easyplan.finance.domain.budget.request.BudgetUpdateRequest;
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
	@DisplayName("예산 설정")
	void createBudget() {
		// 준비
		BudgetCreateRequest given = new BudgetCreateRequest(
				YearMonth.of(2026, 5),
				1_000_000L
		);
		
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
		BudgetCreateRequest budgetCreate = new BudgetCreateRequest(
				YearMonth.of(2026, 5),
				1_000_000L
		);
		
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
		BudgetCreateRequest given = new BudgetCreateRequest(
				YearMonth.of(2026, 5),
				1_000_000L
		);
		
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
