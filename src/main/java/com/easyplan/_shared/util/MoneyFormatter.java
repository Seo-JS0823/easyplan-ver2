package com.easyplan._shared.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.easyplan.finance.domain.journal.Money;

public class MoneyFormatter {
	public static String moneyFormat(BigDecimal amount) {
		return new DecimalFormat("#,###").format(amount);
	}
	
	public static String moneyFormat(Money amount) {
		return String.format("%,d", amount.getAmount());
	}
}
