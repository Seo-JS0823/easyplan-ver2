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
public class ProfileAddress {
	private static final Pattern PROFILE_ADDRESS_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]{2,10}$");
	
	private String nickname;
	
	public ProfileAddress(String nickname) {
		if(nickname == null || !PROFILE_ADDRESS_PATTERN.matcher(nickname).matches()) {
			throw new IllegalArgumentException("프로필 주소 형식이 올바르지 않습니다: " + nickname);
		}
		
		this.nickname = nickname;
	}
	
	public String nickname() {
		return nickname;
	}
	
	public String url() {
		return "@" + nickname;
	}
}
