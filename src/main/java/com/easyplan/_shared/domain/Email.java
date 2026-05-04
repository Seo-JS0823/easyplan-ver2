package com.easyplan._shared.domain;

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
public class Email {
	private static final Pattern EMAIL_PATTERN =
			Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
	
	private String address;
	
	public Email(String address) {
		if (address == null || address.isBlank()) {
			throw new IllegalArgumentException("이메일은 필수입니다.");
		}
		
		address = address.trim().toLowerCase();
		
		if(!EMAIL_PATTERN.matcher(address).matches()) {
			throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다: " + address);
		}
		
		this.address = address;
	}
	
	public String address() {
		return address;
	}
}
