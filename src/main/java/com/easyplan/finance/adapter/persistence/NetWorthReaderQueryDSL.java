package com.easyplan.finance.adapter.persistence;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.easyplan._global.persistence.QueryDslUtil;
import com.easyplan._shared.annotation.TraceTime;
import com.easyplan.finance.application.required.query.NetWorthReader;
import com.easyplan.finance.application.usecase.FinanceQuery.FiscalPeriod;
import com.easyplan.finance.application.usecase.response.query.LedgerAssetSummary;
import com.easyplan.finance.application.usecase.response.query.MonthlyAssetSummary;
import com.easyplan.finance.application.usecase.response.query.MonthlyTrendResponse.MonthlyTrendElement;
import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.account.QAccount;
import com.easyplan.finance.domain.journal.QEntryLine;
import com.easyplan.finance.domain.journal.QJournal;
import com.easyplan.finance.domain.journal.TransactionType;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NetWorthReaderQueryDSL implements NetWorthReader {

	private final JPAQueryFactory qf;
	
	/**
	 * 호출 시점을 기준으로 ledgerId에 해당하는 가계부의
	 * 모든 자산/부채 계정의 거래 내역을 가져온다.
	 * 
	 * 자산 계정은 차변인 경우 증가, 대변인 경우 감소
	 * 부채 계정은 차변인 경우 감소, 대변인 경우 증가
	 * 
	 * 이후 계정별로 집계된 금액으로 순자산을 계산한다. asset - liabilities
	 */
	@Override
	@TraceTime
	public LedgerAssetSummary currentAssetSummary(Long ledgerId) {
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
		
		long asset = QueryDslUtil.getLongOrZero(result, totalAsset);
		
		long liabilities = QueryDslUtil.getLongOrZero(result, totalLiabilities);
		
		long netWorth = asset - liabilities;
		
		return new LedgerAssetSummary(asset, liabilities, netWorth);
	}
	
	/**
	 * 하나의 회계일에 해당하는 총 수입액과 총 지출액을 집계한다.
	 * 거래내역만 있으면 구할 수 있기 때문에 Journal을 조회 대상으로 잡는다.
	 * TransactionType을 조건삼아 case 문으로 DB에서 거래 유형별로 미리 집계한다.
	 * 
	 * 이 쿼리는 가계부를 많이 쓰는 사람이더라도 한 회계월에 많이 잡아봐야 200건 정도 이므로
	 * 복합 인덱스의 필요성은 낮음.
	 * 
	 * 대시보드 UI에 필요한 데이터에 포함되기 때문에 Cache-aside 전략을 사용할 수 있는 부분도 있어서
	 * 복합 인덱스는 걸지 않아도 좋을 것 같음.
	 */
	@Override
	@TraceTime
	public MonthlyAssetSummary monthlyCashSummary(Long ledgerId, LocalDate startDate, LocalDate endDate) {
		QJournal journal = QJournal.journal;
		
		NumberExpression<Long> monthlyTotalIncome = new CaseBuilder()
				.when(journal.transactionType.eq(TransactionType.INCOME))
				.then(journal.amount.amount)
				.otherwise(0L)
				.sum();
		
		NumberExpression<Long> monthlyTotalExpense = new CaseBuilder()
				.when(journal.transactionType.eq(TransactionType.EXPENSE))
				.then(journal.amount.amount)
				.otherwise(0L)
				.sum();
		
		Tuple result = qf
				.select(monthlyTotalIncome, monthlyTotalExpense)
				.from(journal)
				.where(
						journal.ledger.id.eq(ledgerId),
						journal.transactionDate.between(startDate, endDate)
				)
				.fetchOne();
		
		long income = QueryDslUtil.getLongOrZero(result, monthlyTotalIncome);
		
		long expense = QueryDslUtil.getLongOrZero(result, monthlyTotalExpense);
		
		return new MonthlyAssetSummary(income, expense);
	}

	/**
	 * 기간손익 차트를 만들 때 사용되는 쿼리문으로
	 * 이 메서드를 호출하는 호출부에서 몇 개월치 데이터를 뽑을지 미리 계산해서
	 * 회계월이 기준이 되는 리스트를 넘겨줌.
	 * 
	 * 이 쿼리문은 DB에서 groupBy를 사용할 수 없다. YearMonth를 기준으로 groupBy하면 회계월이 기준이 아닌
	 * 달력월이 기준이 되기 때문에.
	 * 
	 * 그래서 쿼리전략은 Income, Expense 거래내역을 나누지 않고 In으로 포함해서 전부다 가져온다.
	 * 가져온 후 거래타입을 기준으로 거래금액을 집계하는 흐름으로 진행된다.
	 */
	@Override
	@TraceTime
	public List<MonthlyTrendElement> monthlyNetWorthTrend(Long ledgerId, List<FiscalPeriod> periods) {
		if(periods.isEmpty()) {
			return List.of();
		}
		
		QJournal journal = QJournal.journal;
		QEntryLine entry = QEntryLine.entryLine;
		QAccount account = QAccount.account;
		
		// 집계할 전체 범위를 만들기 위한 LocalDate 범위
		LocalDate minStartDate = periods.get(0).startDate();
		LocalDate maxEndDate = periods.get(periods.size() -1).endDate();
		
		// 집계할 거래 내역을 전부다 들고온다.
		List<Tuple> rows = qf
				.select(
						journal.transactionDate,
						journal.transactionType,
						journal.amount.amount
				)
				.from(journal)
				.where(
						journal.ledger.id.eq(ledgerId),
						journal.transactionDate.between(minStartDate, maxEndDate),
						journal.transactionType.in(List.of(TransactionType.INCOME, TransactionType.EXPENSE))
				)
				.fetch();
		
		// 데이터 바운딩 준비
		Map<YearMonth, Long> incomeMap = new HashMap<>();
		Map<YearMonth, Long> expenseMap = new HashMap<>();
		
		// 조회된 Tuple 을 돌면서 TransactionType을 조건으로 각 Map에 바운딩
		for (Tuple row : rows) {
			LocalDate tDate = row.get(journal.transactionDate);
			TransactionType tType = row.get(journal.transactionType);
			Long amount = row.get(journal.amount.amount);
			
			FiscalPeriod matched = findPeriod(periods, tDate);
			YearMonth period = matched.period();
			
			if(tType == TransactionType.INCOME) {
				incomeMap.merge(period, amount, Long::sum);
			}
			
			if(tType == TransactionType.EXPENSE) {
				expenseMap.merge(period, amount, Long::sum);
			}
		}
		
		// 순자산을 계산하는데 위 쿼리 전략과 같이 한 번에 집계에 필요한 데이터를 한 번에 긁어옴
		List<Tuple> netWorthRows = qf
				.select(
						journal.transactionDate,
						account.accountType,
						entry.side,
						entry.amount.amount
				)
				.from(journal)
				.join(entry).on(entry.journal.id.eq(journal.id))
				.join(account).on(account.id.eq(entry.account.id))
				.where(
						journal.ledger.id.eq(ledgerId),
						journal.transactionDate.loe(maxEndDate),
						account.accountType.in(AccountType.ASSET, AccountType.LIABILITIES)
				)
				.fetch();
		
		// 각 월별 범위에 맞게 순자산 데이터를 바운딩해야하므로 위와 똑같이 생긴 Map을 하나 만듬
		long openingNetWorth = 0L;
		Map<YearMonth, Long> netWorthDeltaMap = new HashMap<>();
		
		// 계정이 차변에 있는지 대변에 있는지에 따라 연산방식이 달라서
		// AccountType, EntrySide 값과 비교해서 연산용 amount 데이터를 추출함
		// 이후 이 거래 내역이 전체 집계 범위의 첫 시작날짜의 이전인지 안전하게 검증한 후
		// 순자산 데이터에 바운딩하고 순자산 Map에 넣어줌
		for (Tuple row : netWorthRows) {
			LocalDate tDate = row.get(journal.transactionDate);
			AccountType accountType = row.get(account.accountType);
			EntrySide side = row.get(entry.side);
			Long amount = row.get(entry.amount.amount);
			
			long delta = netWorthDelta(accountType, side, amount);
			
			if(tDate.isBefore(minStartDate)) {
				openingNetWorth += delta;
				continue;
			}
			
			FiscalPeriod matched = findPeriod(periods, tDate);
			netWorthDeltaMap.merge(matched.period(), delta, Long::sum);
		}
		
		List<MonthlyTrendElement> result = new ArrayList<>();
		long runningNetWorth = openingNetWorth;
		
		for (FiscalPeriod period : periods) {
			runningNetWorth += netWorthDeltaMap.getOrDefault(period.period(), 0L);
			
			long income = incomeMap.getOrDefault(period.period(), 0L);
			long expense = expenseMap.getOrDefault(period.period(), 0L);
			long profitRate = income == 0
					? 0L
					: Math.round(((double) (income - expense) / income) * 100);
			
			result.add(new MonthlyTrendElement(
					period.period(),
					runningNetWorth,
					income,
					expense,
					profitRate
			));
		}
		
		return result;
	}
	
	private FiscalPeriod findPeriod(List<FiscalPeriod> periods, LocalDate transactionDate) {
		return periods.stream()
				.filter(period -> period.contains(transactionDate))
				.findFirst()
				.orElseThrow();
	}
	
	private long netWorthDelta(AccountType accountType, EntrySide side, Long amount) {
		if(accountType == AccountType.ASSET && side == EntrySide.DEBIT) {
			return amount;
		}
		if(accountType == AccountType.ASSET && side == EntrySide.CREDIT) {
			return -amount;
		}
		if(accountType == AccountType.LIABILITIES && side == EntrySide.DEBIT) {
			return amount;
		}
		if(accountType == AccountType.LIABILITIES && side == EntrySide.CREDIT) {
			return -amount;
		}
		
		return 0L;
	}
	
}
