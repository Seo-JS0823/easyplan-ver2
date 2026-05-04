package com.easyplan.member.domain;

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
public class PasswordHash {
	private static final Pattern PASSWORD_HASH_PATTERN = Pattern.compile("^\\$2[ayb]\\$.{56}$");
	
	private String passwordHash;
	
	public PasswordHash(String passwordHash) {
		if(passwordHash == null || passwordHash.isBlank()) {
			throw new IllegalArgumentException("비밀번호를 입력해주세요.");
		}
		
		if(!PASSWORD_HASH_PATTERN.matcher(passwordHash).matches()) {
			throw new IllegalArgumentException("지원하지 않는 암호화 형식입니다.");
		}
		
		this.passwordHash = passwordHash;
	}
	
	public String passwordHash() {
		return passwordHash;
	}
}
