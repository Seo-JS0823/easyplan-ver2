package com.easyplan._shared.response;

public record GlobalResponse<T>(
		boolean success,
		String message,
		T data,
		ErrorResponse error) {
	
	public static <T> GlobalResponse<T> ok(String message, T data) {
		return new GlobalResponse<>(true, message, data, null);
	}
	
	public static GlobalResponse<Void> ok(String message) {
    return new GlobalResponse<>(true, message, null, null);
	}
	
	public static GlobalResponse<Void> fail(String code, String message) {
		return new GlobalResponse<>(false, null, null, new ErrorResponse(code, message));
	}
	
	public record ErrorResponse(String code, String message) {}
}
