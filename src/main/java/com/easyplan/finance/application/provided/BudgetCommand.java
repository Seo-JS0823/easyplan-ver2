package com.easyplan.finance.application.provided;

import java.time.YearMonth;

import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.budget.Budget;
import com.easyplan.finance.domain.budget.request.BudgetCreateRequest;
import com.easyplan.finance.domain.budget.request.BudgetUpdateRequest;

public interface BudgetCommand {
	
	// 예산 생성
	Budget createBudget(Account account, BudgetCreateRequest budgetCreate);
	
	// 예산 수정
	Budget updateBudget(Account account, YearMonth period, BudgetUpdateRequest budgetUpdate);
	
	// 예산 삭제
	void deleteBudget(Account account, YearMonth period);
}
