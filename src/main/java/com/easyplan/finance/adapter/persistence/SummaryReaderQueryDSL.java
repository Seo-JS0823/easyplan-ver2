package com.easyplan.finance.adapter.persistence;

import java.time.YearMonth;

import org.springframework.stereotype.Repository;

import com.easyplan._shared.annotation.TraceTime;
import com.easyplan.finance.application.required.query.SummaryReader;
import com.easyplan.finance.application.usecase.response.query.AssetSummary;
import com.easyplan.finance.application.usecase.response.query.MonthlyCashSummary;
import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.account.QAccount;
import com.easyplan.finance.domain.journal.QEntryLine;
import com.easyplan.finance.domain.journal.QJournal;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SummaryReaderQueryDSL implements SummaryReader {

	private final JPAQueryFactory qf;
	
	@Override
	@TraceTime
	public AssetSummary currentAssetSummary(Long ledgerId) {
		QJournal journal = QJournal.journal;
		QEntryLine entry = QEntryLine.entryLine;
		QAccount account = QAccount.account;
		
		NumberExpression<Long> totalAsset = new CaseBuilder()
				.when(account.accountType.eq(AccountType.ASSET)
						.and(entry.side.eq(EntrySide.DEBIT)))
				.then(entry.amount.amount)
				.when(account.accountType.eq(AccountType.ASSET)
						.and(entry.side.eq(EntrySide.CREDIT)))
				.then(entry.amount.amount.negate())
				.otherwise(0L)
				.sum();
		
		NumberExpression<Long> totalLiabilities = new CaseBuilder()
				.when(account.accountType.eq(AccountType.LIABILITIES)
						.and(entry.side.eq(EntrySide.DEBIT)))
				.then(entry.amount.amount.negate())
				.when(account.accountType.eq(AccountType.LIABILITIES)
						.and(entry.side.eq(EntrySide.CREDIT)))
				.then(entry.amount.amount)
				.otherwise(0L)
				.sum();
		
		Tuple result = qf
				.select(totalAsset, totalLiabilities)
				.from(journal)
				.join(entry).on(entry.journal.id.eq(journal.id))
				.join(account).on(account.id.eq(entry.account.id))
				.where(
						journal.ledger.id.eq(ledgerId),
						account.accountType.in(AccountType.ASSET, AccountType.LIABILITIES)
				)
				.fetchOne();
		
		long asset = result == null || result.get(totalAsset) == null
				? 0L
				: result.get(totalAsset);
		
		long liabilities = result == null || result.get(totalLiabilities) == null
				? 0L
				: result.get(totalLiabilities);
		
		long netWorth = asset - liabilities;
		
		return new AssetSummary(asset, liabilities, netWorth);
	}

	@Override
	public MonthlyCashSummary monthlyCashSummary(Long ledgerId, YearMonth month) {
		// TODO Auto-generated method stub
		return null;
	}

}
