package com.easyplan.finance.domain.budget.request;

import java.time.YearMonth;

public record BudgetCreateRequest(YearMonth period, Long limitAmount) {

}
