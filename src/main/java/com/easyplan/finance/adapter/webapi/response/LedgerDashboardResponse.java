package com.easyplan.finance.adapter.webapi.response;

import java.util.List;

import com.easyplan.finance.application.usecase.response.query.BudgetSummary.PeriodAccountBudgetSummary;
import com.easyplan.finance.application.usecase.response.query.LedgerAssetSummary;
import com.easyplan.finance.application.usecase.response.query.MonthlyAssetSummary;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LedgerDashboardResponse {
	private final LedgerAssetSummary assets;
	
	private final MonthlyAssetSummary monthly;
	
	private final List<PeriodAccountBudgetSummary> budget;
}
