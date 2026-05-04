package com.easyplan.auth.domain;

import java.time.Instant;

import com.easyplan._global.infra.jpa.BaseEntity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "auth_session")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true, exclude = {"tokenHash"})
public class AuthSession extends BaseEntity {
	
	// Member Id
	@Column(name = "member_id", nullable = false, unique = true)
	private Long memberId;
	
	// 리프레시 토큰 해시
	@Embedded
	@AttributeOverride(
			name = "tokenHash",
			column = @Column(name = "token_hash", nullable = false, unique = true, length = 100)
	)
	private RefreshTokenHash tokenHash;
	
	// 리프레시 토큰 만료 기간
	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;
	
	public static AuthSession create(Long memberId, RefreshTokenHash tokenHash, Instant expiresAt) {
		AuthSession authSession = new AuthSession();
		
		authSession.memberId = memberId;
		authSession.tokenHash = tokenHash;
		authSession.expiresAt = expiresAt;
		
		return authSession;
	}
	
	public void rotation(RefreshTokenHash tokenHash, Instant expiresAt) {
		this.tokenHash = tokenHash;
		this.expiresAt = expiresAt;
	}
	
	public boolean isExpired(Instant now) {
		return !expiresAt.isAfter(now);
	}
}
