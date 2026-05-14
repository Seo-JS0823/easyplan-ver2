package com.easyplan.finance.application.required.query;

import java.time.LocalDate;

import com.easyplan.finance.application.usecase.response.query.LedgerAssetSummary;
import com.easyplan.finance.application.usecase.response.query.MonthlyAssetSummary;

public interface NetWorthReader {
	/*
	 * 1. 현재 자산 상황 (순자산, 총자산, 총부채)
	 *      - 메서드 이름 : currentAssetSummary
	 *      - 반환 DTO    : AssetSummary
	 * 2. 회계시작일 기준 월별 수입 / 지출
	 *      - 메서드 이름 : monthlyCashSummary
	 *      - 반환 DTO    : MonthlyCashSummary
	 */
	
	LedgerAssetSummary currentAssetSummary(Long ledgerId);
	
	MonthlyAssetSummary monthlyCashSummary(Long ledgerId, LocalDate startDate, LocalDate endDate);
}
