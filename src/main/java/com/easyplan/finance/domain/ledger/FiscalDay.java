package com.easyplan.finance.domain.ledger;

import java.time.LocalDate;
import java.time.YearMonth;

import com.easyplan.finance.domain.ledger.exception.LedgerErrorCode;
import com.easyplan.finance.domain.ledger.exception.LedgerException;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FiscalDay {
	private int day;
	
	public FiscalDay(int day) {
		if(day < 1 || day > 31) {
			throw new LedgerException(LedgerErrorCode.FISCAL_OVER_DAY);
		}
		
		this.day = day;
	}
	
	public LocalDate resolveStartDate(YearMonth month) {
		return month.atDay(Math.min(day, month.lengthOfMonth()));
	}
	
	public LocalDate resolveEndDate(YearMonth month) {
		YearMonth nextMonth = month.plusMonths(1);
		LocalDate nextMonthStartDate = resolveStartDate(nextMonth);
		return nextMonthStartDate.minusDays(1);
	}
	
}
