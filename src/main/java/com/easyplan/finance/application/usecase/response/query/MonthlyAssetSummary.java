package com.easyplan.finance.application.usecase.response.query;

// 한 회계월에 해당하는 총 지출, 총 수입 응답 DTO
public record MonthlyAssetSummary(long monthlyTotalIncome, long monthlyTotalExpense) {

}
