package com.easyplan.finance.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.annotation.TraceTime;
import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.LedgerFinder;
import com.easyplan.finance.application.required.query.SummaryReader;
import com.easyplan.finance.application.usecase.response.query.AssetSummary;
import com.easyplan.finance.domain.ledger.Ledger;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinanceQuery {
	private final LedgerFinder ledgerFinder;
	
	private final SummaryReader summaryReader;
	
	@TraceTime
	public AssetSummary getNetWorthSummary(PublicId memberPublicId, PublicId ledgerPublicId) {
		Ledger ledger = ledgerFinder.findByLedgerOwner(memberPublicId, ledgerPublicId);
		
		return summaryReader.currentAssetSummary(ledger.getId());
	}
	
}
