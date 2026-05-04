package com.easyplan.auth.adapter.webapi;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyplan._shared.adapter.CookieProp;
import com.easyplan._shared.adapter.CookieProvider;
import com.easyplan._shared.response.GlobalResponse;
import com.easyplan.auth.application.provided.AuthSessionCommand;
import com.easyplan.auth.application.provided.LoginResult;
import com.easyplan.auth.application.provided.ReissueResult;
import com.easyplan.auth.domain.request.LoginRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthSessionCommand authCommand;
	
	private final CookieProvider cookieProvider;
	
	@PostMapping("/login")
	public GlobalResponse<Void> login(
			@RequestBody LoginRequest loginRequest,
			HttpServletResponse response) {
		LoginResult result = authCommand.login(loginRequest);
		
		cookieProvider.addCookie(CookieProp.ACCESS, result.accessToken(), result.accessExpiresAt(), response);
		cookieProvider.addCookie(CookieProp.REFRESH, result.refreshToken(), result.refreshExpiresAt(), response);
		
		return GlobalResponse.ok("로그인 성공");
	}
	
	@PostMapping("/reissue")
	public GlobalResponse<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = cookieProvider.getCookieValue(CookieProp.REFRESH, request);
		ReissueResult result = authCommand.reissue(refreshToken);
		
		cookieProvider.addCookie(CookieProp.ACCESS, result.accessToken(), result.accessExpiresAt(), response);
		cookieProvider.addCookie(CookieProp.REFRESH, result.refreshToken(), result.refreshExpiresAt(), response);
		
		return GlobalResponse.ok("토큰 재발급 성공");
	}
	
	@PostMapping("/logout")
	public GlobalResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = cookieProvider.getCookieValue(CookieProp.REFRESH, request);
		authCommand.logout(refreshToken);
		
		cookieProvider.removeCookie(CookieProp.ACCESS, response);
		cookieProvider.removeCookie(CookieProp.REFRESH, response);
		
		return GlobalResponse.ok("로그아웃 성공");
	}
}
