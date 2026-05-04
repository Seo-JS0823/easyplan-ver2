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
public class Nickname {
	private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]{2,10}$");
	
	private String nickname;
	
	public Nickname(String nickname) {
		if(nickname == null || nickname.isBlank()) {
			throw new IllegalArgumentException("닉네임은 필수로 입력해야 합니다.");
		}
		
		if(!NICKNAME_PATTERN.matcher(nickname).matches()) {
			throw new IllegalArgumentException("닉네임은 2~10자의 한글, 영문, 숫자만 허용합니다.");
		}
		
		this.nickname = nickname;
	}
	
	public String nickname() {
		return nickname;
	}
}
