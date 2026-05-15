package com.easyplan.finance.application.usecase;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.LongStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.annotation.TraceTime;
import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.LedgerFinder;
import com.easyplan.finance.application.required.query.NetWorthReader;
import com.easyplan.finance.application.usecase.response.query.LedgerAssetSummary;
import com.easyplan.finance.application.usecase.response.query.MonthlyAssetSummary;
import com.easyplan.finance.application.usecase.response.query.MonthlyTrendResponse.MonthlyTrendElement;
import com.easyplan.finance.domain.ledger.Ledger;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinanceQuery {
	private final LedgerFinder ledgerFinder;
	
	private final NetWorthReader netWorthReader;
	
	// 순자산, 총 자산, 총 지출
	@TraceTime
	public LedgerAssetSummary getNetWorthSummary(Long ownerId, PublicId ledgerPublicId) {
		Ledger ledger = ledgerFinder.findByLedgerOwner(ownerId, ledgerPublicId);
		
		return netWorthReader.currentAssetSummary(ledger.getId());
	}
	
	// 월 총 수입, 총 지출
	@TraceTime
	public MonthlyAssetSummary getMonthlyCashSummary(Long ownerId, PublicId ledgerPublicId, YearMonth period) {
		Ledger ledger = ledgerFinder.findByLedgerOwner(ownerId, ledgerPublicId);
		
		LocalDate startDate = ledger.fiscalStartDate(period);
		LocalDate endDate = ledger.fiscalEndDate(period);
		
		return netWorthReader.monthlyCashSummary(ledger.getId(), startDate, endDate);
	}
	
	// 월별 순자산, 총자산, 총지출
	@TraceTime
	public List<MonthlyTrendElement> getMonthlyNetWorthTrend(Long ownerId, PublicId ledgerPublicId, YearMonth period, int count) {
		Ledger ledger = ledgerFinder.findByLedgerOwner(ownerId, ledgerPublicId);
		
		YearMonth startPeriod = period.minusMonths(count - 1);
		
		List<FiscalPeriod> periods = LongStream.range(0, count)
				.mapToObj(i -> startPeriod.plusMonths(i))
				.map(ym -> new FiscalPeriod(
						ym,
						ledger.fiscalStartDate(ym),
						ledger.fiscalEndDate(ym)
				))
				.toList();
		
		return netWorthReader.monthlyNetWorthTrend(ledger.getId(), periods);
	}
	
	
	public static record FiscalPeriod(YearMonth period, LocalDate startDate, LocalDate endDate) {
		public boolean contains(LocalDate date) {
			return !date.isBefore(startDate) && !date.isAfter(endDate);
		}
	}
}
