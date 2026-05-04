package com.easyplan.finance.domain.ledger;

import java.time.LocalDate;
import java.time.YearMonth;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FiscalStartDay {
	
  private int day;

  public FiscalStartDay(int day) {
    if (day < 1 || day > 31) {
      throw new IllegalArgumentException("회계 시작일은 1일부터 31일 사이여야 합니다.");
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
