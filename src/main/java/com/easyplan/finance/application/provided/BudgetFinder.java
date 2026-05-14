package com.easyplan.finance.application.provided;

import java.time.YearMonth;
import java.util.List;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.budget.Budget;

public interface BudgetFinder {
	Budget findByBudget(Long ownerId, PublicId ledgerPublicId, PublicId accountPublicId, YearMonth period);
	
	List<Budget> findBudgetByLedger(Long ownerId, PublicId ledgerPublicId, YearMonth period);
}
