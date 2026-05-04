package com.easyplan.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.easyplan._shared.time.UTC;
import com.easyplan.auth.domain.AuthSession;
import com.easyplan.auth.domain.RefreshTokenHash;

public class AuthDomainTest {
	
	@Test
	@DisplayName("인증 객체 생성 테스트")
	void login() {
		Long memberId = 1L;
		RefreshTokenHash tokenHash = new RefreshTokenHash("AE1ZoPVUWAgYSePLIGlivahphjm+FZA5rNbhPdDJCvs=");
		Instant expiresAt = UTC.nowSecond().plus(3, ChronoUnit.DAYS);
		
		AuthSession authSession = AuthSession.create(memberId, tokenHash, expiresAt);
		
		assertThat(authSession.getMemberId()).isEqualTo(memberId);
		assertThat(authSession.getTokenHash()).isEqualTo(tokenHash);
		assertThat(authSession.getExpiresAt()).isEqualTo(expiresAt);
	}
}
