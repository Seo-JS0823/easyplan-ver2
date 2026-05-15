package com.easyplan.finance.application.required.query;

import java.time.LocalDate;
import java.util.List;

import com.easyplan.finance.application.usecase.FinanceQuery.FiscalPeriod;
import com.easyplan.finance.application.usecase.response.query.LedgerAssetSummary;
import com.easyplan.finance.application.usecase.response.query.MonthlyAssetSummary;
import com.easyplan.finance.application.usecase.response.query.MonthlyTrendResponse.MonthlyTrendElement;

/*
 * 1. 현재 자산 상황 (순자산, 총자산, 총부채)
 *      - 메서드 이름 : currentAssetSummary
 *      - 반환 DTO    : LedgerAssetSummary
 * 2. 한 회계월 기준 총 수입 / 총 지출
 *      - 메서드 이름 : monthlyCashSummary
 *      - 반환 DTO    : MonthlyCashSummary
 * 3. 기간 손익 차트
 * 			- 메서드 이름 : monthlyNetWorthTrend
 * 			- 반환 DTO		: List<MonthlyTrendElement>
 */
public interface NetWorthReader {
	
	/**
	 * 순자산, 총자산, 총부채 집계
	 * 
	 * 집계 대상 = 가계부 ASSET, LIABILITIES 계정의 모든 거래 내역
	 */
	LedgerAssetSummary currentAssetSummary(Long ledgerId);
	
	/**
	 * 한 회계월의 총 수입, 총 지출 집계
	 * 
	 * 집계 대상 = 회계월 시작날짜와 마감날짜의 모든 수입, 지출 거래유형의 거래 내역
	 */
	MonthlyAssetSummary monthlyCashSummary(Long ledgerId, LocalDate startDate, LocalDate endDate);
	
	/**
	 * 기간 손익 차트에 필요한 집계 데이터로
	 * 
	 * 회계월을 기준으로 월별 마감날짜를 기준으로 해당 시점의
	 * 순자산, 총 지출, 총 수입 집계
	 */
	List<MonthlyTrendElement> monthlyNetWorthTrend(Long ledgerId, List<FiscalPeriod> periods);
}
