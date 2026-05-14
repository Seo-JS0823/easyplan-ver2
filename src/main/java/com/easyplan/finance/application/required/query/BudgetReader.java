package com.easyplan.finance.application.required.query;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.easyplan.finance.application.usecase.response.query.BudgetSummary.PeriodAccountBudgetSummary;

public interface BudgetReader {
	PeriodAccountBudgetSummary summarizeAccountBudgetSummary
		(Long ledgerId, Long accountId, YearMonth period, LocalDate startDate, LocalDate endDate);
	
	List<PeriodAccountBudgetSummary> summarizeMultiAccountBudgetSummary
		(Long ledgerId, YearMonth period, LocalDate startDate, LocalDate endDate);
}
