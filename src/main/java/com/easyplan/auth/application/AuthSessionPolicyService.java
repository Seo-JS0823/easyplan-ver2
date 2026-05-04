package com.easyplan.auth.application;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.easyplan._shared.time.UTC;
import com.easyplan.auth.domain.policy.AuthSessionPolicy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthSessionPolicyService implements AuthSessionPolicy {
	
	@Value("${jwt.times.refresh}")
	private long refreshTokenTime;
	
	@Value("${jwt.times.access}")
	private long accessTokenTime;
	
	@Override
	public Instant refreshExpiresAt() {
		return UTC.nowSecond().plusMillis(refreshTokenTime);
	}

	@Override
	public Instant accessExpiresAt() {
		return UTC.nowSecond().plusMillis(accessTokenTime);
	}

	@Override
	public long getAccessTokenTime() {
		return accessTokenTime;
	}

	@Override
	public long getRefreshTokenTime() {
		return refreshTokenTime;
	}
}
