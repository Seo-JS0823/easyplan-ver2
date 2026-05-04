package com.easyplan._shared.util;

import java.util.Objects;

public class NonNull {
	public static <T> T require(T value, String message) {
		return Objects.requireNonNull(value, message);
	}
	
	public static boolean eq(Object a, Object b) {
		return Objects.equals(a, b); 
	}
}
