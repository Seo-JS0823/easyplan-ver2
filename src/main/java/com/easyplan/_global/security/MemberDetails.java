package com.easyplan._global.security;

import java.util.Collection;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.easyplan.member.application.provided.MemberPrincipal;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.MemberStatus;

public class MemberDetails implements UserDetails {

	private final MemberPrincipal member;
	
	public MemberDetails(Member member) {
		this.member = MemberPrincipal.of(member); 
	}
	
	public MemberPrincipal getMember() {
		return member;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return member.authorities();
	}

	@Override
	public @Nullable String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return member.publicId().publicId();
	}

	@Override
	public boolean isEnabled() {
		if(member.status() == MemberStatus.PENDING) {
			return false;
		}
		return true;
	}
	
}
