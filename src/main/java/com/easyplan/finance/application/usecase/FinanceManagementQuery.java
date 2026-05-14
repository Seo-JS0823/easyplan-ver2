package com.easyplan.finance.application.usecase;

import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.application.provided.AccountFinder;
import com.easyplan.finance.application.provided.LedgerFinder;
import com.easyplan.finance.application.required.query.BudgetReader;
import com.easyplan.finance.application.usecase.response.query.BudgetSummary.PeriodAccountBudgetSummary;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.ledger.Ledger;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinanceManagementQuery {
	private final AccountFinder accountFinder;
	
	private final LedgerFinder ledgerFinder;
	
	private final BudgetReader budgetReader;
	
	public PeriodAccountBudgetSummary getPeriodBudgetSummary(Long ownerId, PublicId ledgerPublicId ,PublicId accountPublicId, YearMonth period) {
		Account account = accountFinder.findActiveAccountOwner(ownerId, ledgerPublicId, accountPublicId);
		
		return budgetReader.summarizeAccountBudgetSummary(
				account.getLedger().getId(),
				account.getId(),
				period,
				account.getFiscalStartDate(period),
				account.getFiscalEndDate(period)
		);
	}
	
	public List<PeriodAccountBudgetSummary> getMultiPeriodBudgetSummary(Long ownerId, PublicId ledgerPublicId, YearMonth period) {
		Ledger ledger = ledgerFinder.findByLedgerOwner(ownerId, ledgerPublicId);
		
		return budgetReader.summarizeMultiAccountBudgetSummary(
				ownerId,
				period,
				ledger.fiscalStartDate(period),
				ledger.fiscalEndDate(period)
		);
	}
}
