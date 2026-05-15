package com.easyplan.finance.application.usecase.response.query;

import java.time.YearMonth;
import java.util.List;

public record MonthlyTrendResponse(List<MonthlyTrendElement> item) {
	public record MonthlyTrendElement(
			YearMonth period,
			long netWorth,
			long totalIncome,
			long totalExpense,
			long profitRate
	) {}
}
