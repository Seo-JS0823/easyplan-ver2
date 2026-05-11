package com.easyplan._global.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MoneyFormatter {
	public static String moneyFormat(BigDecimal amount) {
		return new DecimalFormat("#,###").format(amount);
	}
}
