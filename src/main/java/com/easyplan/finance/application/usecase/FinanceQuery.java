package com.easyplan.finance.application.usecase;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.annotation.TraceTime;
import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.LedgerFinder;
import com.easyplan.finance.application.required.query.SummaryReader;
import com.easyplan.finance.application.usecase.response.query.LedgerAssetSummary;
import com.easyplan.finance.application.usecase.response.query.MonthlyAssetSummary;
import com.easyplan.finance.domain.ledger.Ledger;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinanceQuery {
	private final LedgerFinder ledgerFinder;
	
	private final SummaryReader summaryReader;
	
	@TraceTime
	public LedgerAssetSummary getNetWorthSummary(PublicId memberPublicId, PublicId ledgerPublicId) {
		Ledger ledger = ledgerFinder.findByLedgerOwner(memberPublicId, ledgerPublicId);
		
		return summaryReader.currentAssetSummary(ledger.getId());
	}
	
	@TraceTime
	public MonthlyAssetSummary getMonthlyCashSummary(PublicId memberPublicId, PublicId ledgerPublicId, YearMonth month) {
		Ledger ledger = ledgerFinder.findByLedgerOwner(memberPublicId, ledgerPublicId);
		
		LocalDate startDate = ledger.fiscalStartDate(month);
		LocalDate endDate = ledger.fiscalEndDate(month);
		
		return summaryReader.monthlyCashSummary(ledger.getId(), startDate, endDate);
	}
	
}
