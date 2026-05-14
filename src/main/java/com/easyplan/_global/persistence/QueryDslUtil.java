package com.easyplan._global.persistence;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;

public final class QueryDslUtil {
	private QueryDslUtil() {}
	
	public static long getLongOrZero(Tuple tuple, Expression<Long> expression) {
		if (tuple == null) {
			return 0L;
		}
		
		Long value = tuple.get(expression);
		return value == null ? 0L : value;
	}
	
	public static String getStringOrNull(Tuple tuple, Expression<String> expression) {
		if (tuple == null) {
			return null;
		}
		
		String value = tuple.get(expression);
		return value == null ? null : value;
	}
}
