package com.easyplan.finance.application.required.query;

import java.time.YearMonth;

import com.easyplan.finance.application.usecase.response.query.AssetSummary;
import com.easyplan.finance.application.usecase.response.query.MonthlyCashSummary;

public interface SummaryReader {
	/*
	 * 1. 현재 자산 상황 (순자산, 총자산, 총부채)
	 *      - 메서드 이름 : currentAssetSummary
	 *      - 반환 DTO    : AssetSummary
	 * 2. 회계시작일 기준 월별 수입 / 지출
	 *      - 메서드 이름 : monthlyCashSummary
	 *      - 반환 DTO    : MonthlyCashSummary
	 */
	
	AssetSummary currentAssetSummary(Long ledgerId);
	
	MonthlyCashSummary monthlyCashSummary(Long ledgerId, YearMonth month);
}
