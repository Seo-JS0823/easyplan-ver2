package com.easyplan._global.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.easyplan._shared.util.CookieProp;
import com.easyplan._shared.util.CookieProvider;
import com.easyplan.auth.application.required.TokenProvider;
import com.easyplan.auth.domain.TokenClaims;
import com.easyplan.member.application.provided.MemberFinder;
import com.easyplan.member.domain.Member;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	private final TokenProvider tokenProvider;
	
	private final MemberFinder memberFinder;
	
	private final CookieProvider cookieProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		authenticate(request);
		
		filterChain.doFilter(request, response);
	}
	
	private void authenticate(HttpServletRequest request) {
		String accessToken = cookieProvider.getCookieValue(CookieProp.ACCESS, request);
		if(accessToken == null || accessToken.isBlank()) {
			return;
		}
		
		TokenClaims claims = extractClaims(accessToken);
		if(claims == null) {
			return;
		}
		
		Member member = findMember(claims);
		if(member == null) {
			return;
		}
		
		MemberDetails memberDetails = new MemberDetails(member);
		if(!memberDetails.isEnabled()) {
			return;
		}
		
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				memberDetails,
				null,
				memberDetails.getAuthorities()
		);
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	private TokenClaims extractClaims(String accessToken) {
		try {
			return tokenProvider.extractTokenClaims(accessToken);
		} catch(RuntimeException e) {
			return null;
		}
	}
	
	private Member findMember(TokenClaims claims) {
		System.out.println("JWTFILTER: MEMBER_PUBLIC_ID: " + claims.publicId().publicId());
		
		try {
			Member member =  memberFinder.findByPublicId(claims.publicId());
			
			System.out.println("MEMBER_ID:" + member.getId());
			
			return member;
		} catch(RuntimeException e) {
			return null;
		}
	}
}
