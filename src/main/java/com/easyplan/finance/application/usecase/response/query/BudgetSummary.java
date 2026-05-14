package com.easyplan.finance.application.usecase.response.query;

public class BudgetSummary {
	public record PeriodAccountBudgetSummary(
			long budgetId,
			String accountName,
			long limitAmount,
			long totalExpense,
			double ratio
	) {
		public static PeriodAccountBudgetSummary of(long id, String accountName, long limitAmount, long totalExpense) {
			double ratio = limitAmount > 0
					? Math.round(((double) totalExpense / limitAmount * 100) * 10) / 10.0
					: 0.0;
			
			return new PeriodAccountBudgetSummary(id, accountName, limitAmount, totalExpense, ratio);
		}
	}
}
