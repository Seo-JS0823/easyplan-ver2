package com.easyplan.member.adapter.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.easyplan.member.domain.PasswordEncoder;

@Component
public class SecurePasswordEncoder implements PasswordEncoder {

	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@Override
	public String encode(String password) {
		return encoder.encode(password);
	}

	@Override
	public boolean matches(String password, String passwordHash) {
		return encoder.matches(password, passwordHash);
	}

}
