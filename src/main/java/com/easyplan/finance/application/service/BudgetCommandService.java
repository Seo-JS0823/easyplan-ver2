package com.easyplan.finance.application.service;

import java.time.YearMonth;

import org.springframework.stereotype.Service;

import com.easyplan.finance.application.provided.BudgetCommand;
import com.easyplan.finance.application.required.repository.BudgetRepository;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.budget.Budget;
import com.easyplan.finance.domain.budget.exception.BudgetErrorCode;
import com.easyplan.finance.domain.budget.exception.BudgetException;
import com.easyplan.finance.domain.budget.request.BudgetCreateRequest;
import com.easyplan.finance.domain.budget.request.BudgetUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BudgetCommandService implements BudgetCommand {
	private final BudgetRepository budgetRepo;

	// 예산 설정
	@Override
	public Budget createBudget(Account account, BudgetCreateRequest budgetCreate) {
		Budget budget = Budget.create(account, budgetCreate);
		
		return budgetRepo.save(budget);
	}

	// 예산 수정
	@Override
	public Budget updateBudget(Account account, YearMonth period, BudgetUpdateRequest budgetUpdate) {
		Budget budget = budgetRepo.findByAccountAndPeriod(account, period)
				.orElseThrow(() -> new BudgetException(BudgetErrorCode.BUDGET_NOT_FOUND));
		
		budget.changeLimitAmount(budgetUpdate.limitAmount());
		
		return budgetRepo.save(budget);
	}

	// 예산 삭제
	@Override
	public void deleteBudget(Account account, YearMonth period) {
		Budget budget = budgetRepo.findByAccountAndPeriod(account, period)
				.orElseThrow(() -> new BudgetException(BudgetErrorCode.BUDGET_NOT_FOUND));
		
		budgetRepo.delete(budget);
	}
	
}
