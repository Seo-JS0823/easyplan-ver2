package com.easyplan.finance.application.service;

import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.AccountFinder;
import com.easyplan.finance.application.provided.BudgetFinder;
import com.easyplan.finance.application.required.repository.BudgetRepository;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.budget.Budget;
import com.easyplan.finance.domain.budget.exception.BudgetErrorCode;
import com.easyplan.finance.domain.budget.exception.BudgetException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetFinderService implements BudgetFinder {
	private final AccountFinder accountFinder;
	
	private final BudgetRepository budgetRepo;
	
	@Override
	public Budget findByBudget(Long ownerId, PublicId ledgerPublicId, PublicId accountPublicId, YearMonth period) {
		Account account = accountFinder.findActiveAccountOwner(ownerId, ledgerPublicId, accountPublicId);
		
		return budgetRepo.findByAccountAndPeriod(account, period)
				.orElseThrow(() -> new BudgetException(BudgetErrorCode.BUDGET_NOT_FOUND));
	}

	@Override
	public List<Budget> findBudgetByLedger(Long ownerId, PublicId ledgerPublicId, YearMonth period) {
		List<Account> accounts = accountFinder.findActiveAccountOwnerByLedger(ownerId, ledgerPublicId);
		
		return budgetRepo.findBudgetList(accounts, period);
	}

}
