package com.easyplan._shared.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
	public static String dateFormat(LocalDate date) {
		return DateTimeFormatter.ofPattern("yyyy년 MM월 dd일").format(date);
	}
}
