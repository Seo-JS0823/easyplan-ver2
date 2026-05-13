package com.easyplan.finance.application.usecase.response.command;

import java.time.YearMonth;

import com.easyplan.finance.domain.budget.Budget;

public class BudgetResponse {
	public record BudgetCreateResponse(String accountName, YearMonth period, Long limitAmount) {
		public static BudgetCreateResponse of(Budget budget) {
			return new BudgetCreateResponse(
					budget.getAccountName(),
					budget.getPeriod(),
					budget.getAmount()
			);
		}
	}
	
	public record BudgetUpdateResponse(String accountName, Long limitAmount) {
		public static BudgetUpdateResponse of(Budget budget) {
			return new BudgetUpdateResponse(budget.getAccountName(), budget.getAmount());
		}
	}
}
