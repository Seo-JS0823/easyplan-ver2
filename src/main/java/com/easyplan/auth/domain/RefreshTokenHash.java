package com.easyplan.auth.domain;

import java.util.regex.Pattern;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenHash {
	private static final Pattern REFRESH_TOKEN_HASH_PATTERN = Pattern.compile("^[A-Za-z0-9+/]+=*$");
	
	private String tokenHash;
	
	public RefreshTokenHash(String tokenHash) {
		if(tokenHash == null || tokenHash.isBlank() || !REFRESH_TOKEN_HASH_PATTERN.matcher(tokenHash).matches()) {
			throw new IllegalArgumentException("유효하지 않은 refresh token hash입니다.");
		}
		
		this.tokenHash = tokenHash;
	}
	
	public String tokenHash() {
		return tokenHash;
	}
}
