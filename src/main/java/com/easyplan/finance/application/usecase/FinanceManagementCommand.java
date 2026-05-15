package com.easyplan.finance.application.usecase;

import java.time.YearMonth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.annotation.TraceTime;
import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.AccountFinder;
import com.easyplan.finance.application.provided.BudgetCommand;
import com.easyplan.finance.application.usecase.response.command.BudgetResponse.BudgetCreateResponse;
import com.easyplan.finance.application.usecase.response.command.BudgetResponse.BudgetUpdateResponse;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.budget.Budget;
import com.easyplan.finance.domain.budget.request.BudgetCreateRequest;
import com.easyplan.finance.domain.budget.request.BudgetUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FinanceManagementCommand {
	private final AccountFinder accountFinder;
	
	private final BudgetCommand budgetCommand;
	
	// 예산 생성
	@TraceTime
	public BudgetCreateResponse createBudget(Long ownerId, PublicId ledgerPublicId, PublicId accountPublicId, BudgetCreateRequest budgetCreate) {
		Account account = findAccount(ownerId, ledgerPublicId, accountPublicId);
		
		Budget budget = budgetCommand.createBudget(account, budgetCreate);
		
		return BudgetCreateResponse.of(budget);
	}
	
	// 예산 수정
	@TraceTime
	public BudgetUpdateResponse updateBudget(Long ownerId, PublicId ledgerPublicId, PublicId accountPublicId, YearMonth period, BudgetUpdateRequest budgetUpdate) {
		Account account = findAccount(ownerId, ledgerPublicId, accountPublicId);
		
		Budget budget = budgetCommand.updateBudget(account, period, budgetUpdate);
		
		return BudgetUpdateResponse.of(budget);
	}
	
	// 예산 삭제
	@TraceTime
	public void deleteBudget(Long ownerId, PublicId ledgerPublicId, PublicId accountPublicId, YearMonth period) {
		Account account = findAccount(ownerId, ledgerPublicId, accountPublicId);
		
		budgetCommand.deleteBudget(account, period);
	}
	
	private Account findAccount(Long ownerId, PublicId ledgerPublicId, PublicId accountPublicId) {
		return accountFinder.findActiveAccountOwner(ownerId, ledgerPublicId, accountPublicId);
	}
	
}
