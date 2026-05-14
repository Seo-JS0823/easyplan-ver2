package com.easyplan.finance.adapter.persistence;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.easyplan._global.persistence.QueryDslUtil;
import com.easyplan._shared.annotation.TraceTime;
import com.easyplan.finance.application.required.query.BudgetReader;
import com.easyplan.finance.application.usecase.response.query.BudgetSummary.PeriodAccountBudgetSummary;
import com.easyplan.finance.domain.budget.QBudget;
import com.easyplan.finance.domain.journal.QEntryLine;
import com.easyplan.finance.domain.journal.QJournal;
import com.easyplan.finance.domain.journal.TransactionType;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BudgetReaderQueryDSL implements BudgetReader {
	
	private final JPAQueryFactory qf;

	@Override
	@TraceTime
	public PeriodAccountBudgetSummary summarizeAccountBudgetSummary(
			Long ledgerId,
			Long accountId,
			YearMonth period,
			LocalDate startDate,
			LocalDate endDate) {
		
		QBudget budget = QBudget.budget;
		QJournal journal = QJournal.journal;
		QEntryLine entry = QEntryLine.entryLine;
		
		JPQLQuery<Long> totalExpense = JPAExpressions
				.select(journal.amount.amount.sum().coalesce(0L))
				.from(journal)
				.join(journal.entries, entry)
				.where(
						journal.ledger.id.eq(ledgerId),
						journal.transactionType.eq(TransactionType.EXPENSE),
						journal.transactionDate.between(startDate, endDate),
						entry.account.id.eq(accountId)
				);
		
		Tuple result = qf
				.select(
						budget.id,
						budget.limitAmount.amount,
						budget.account.accountName,
						totalExpense
				)
				.from(budget)
				.where(
						budget.ledger.id.eq(ledgerId),
						budget.account.id.eq(accountId),
						budget.period.eq(period)
				)
				.fetchOne();
		
		long id = QueryDslUtil.getLongOrZero(result, budget.id);
		
		long limitAmount = QueryDslUtil.getLongOrZero(result, budget.limitAmount.amount);
		
		long expense = QueryDslUtil.getLongOrZero(result, totalExpense);
		
		String accountName = QueryDslUtil.getStringOrNull(result, budget.account.accountName);
		
		return PeriodAccountBudgetSummary.of(id, accountName, limitAmount, expense); 
	}

	@Override
	@TraceTime
	public List<PeriodAccountBudgetSummary> summarizeMultiAccountBudgetSummary(
			Long ledgerId,
			YearMonth period,
			LocalDate startDate, LocalDate endDate) {
		
		QBudget budget = QBudget.budget;
		QJournal journal = QJournal.journal;
		QEntryLine entry = QEntryLine.entryLine;
		
		JPQLQuery<Long> totalExpense = JPAExpressions
				.select(journal.amount.amount.sum().coalesce(0L))
				.from(journal)
				.join(journal.entries, entry)
				.where(
						journal.ledger.id.eq(ledgerId),
						journal.transactionType.eq(TransactionType.EXPENSE),
						journal.transactionDate.between(startDate, endDate),
						entry.account.id.eq(budget.account.id)
				);
		
		List<Tuple> results = qf
				.select(
						budget.id,
						budget.limitAmount.amount,
						budget.account.accountName,
						totalExpense
				)
				.from(budget)
				.where(
						budget.ledger.id.eq(ledgerId),
						budget.period.eq(period)
				)
				.fetch();
		
		List<PeriodAccountBudgetSummary> summarize = results.stream()
				.map(result -> {
					long id = QueryDslUtil.getLongOrZero(result, budget.id);
					long amount = QueryDslUtil.getLongOrZero(result, budget.limitAmount.amount);
					long expense = QueryDslUtil.getLongOrZero(result, totalExpense);
					String accountName = QueryDslUtil.getStringOrNull(result, budget.account.accountName);
					
					return PeriodAccountBudgetSummary.of(id, accountName, amount, expense);
				})
				.toList();
		
		return summarize;
	}
	
	
}
