package com.easyplan._shared.util;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.easyplan._shared.time.UTC;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieProvider {

	public void addCookie(CookieProp cookie, String value, Instant expiresAt, HttpServletResponse response) {
		response.addHeader(HttpHeaders.SET_COOKIE, createCookie(cookie, value, expiresAt));
	}
	
	public void removeCookie(CookieProp cookie, HttpServletResponse response) {
		response.addHeader(HttpHeaders.SET_COOKIE, createCookie(cookie, "", 0));
	}
	
	public String getCookieValue(CookieProp prop, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if(cookies == null) return null;
		
		for(Cookie c : cookies) {
			if(c.getName().equals(prop.getName())) {
				return c.getValue();
			}
		}
		
		return null;
	}
	
	private String createCookie(CookieProp cookie, String value, Instant expiresAt) {
		long maxAge = Math.max(Duration.between(UTC.nowSecond(), expiresAt).toSeconds(), 0);
		
		return createCookie(cookie, value, maxAge);
	}
	
	private String createCookie(CookieProp cookie, String value, long maxAge) {
		
		ResponseCookie responseCookie = ResponseCookie.from(cookie.getName(), value)
				.path("/")
				.httpOnly(cookie.isHttpOnly())
				.secure(true)
				.maxAge(maxAge)
				.sameSite("Strict")
				.build();
		
		return responseCookie.toString();
	}
}
