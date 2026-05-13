package com.easyplan.finance.application.provided;

import java.time.YearMonth;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.budget.Budget;

public interface BudgetFinder {
	Budget findByBudget(Long ownerId, PublicId ledgerPublicId, PublicId accountPublicId, YearMonth period);
}
