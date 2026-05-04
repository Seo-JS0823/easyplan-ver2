package com.easyplan.member.application.provided;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.MemberRole;
import com.easyplan.member.domain.MemberStatus;

public record MemberPrincipal(Long memberId, PublicId publicId, MemberRole role, MemberStatus status, List<SimpleGrantedAuthority> authorities) {
	public static MemberPrincipal of(Member member) {
		List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
		
		return new MemberPrincipal(member.getId(), member.getMemberPublicId(), member.getRole(), member.getStatus(), authorities);
	}
}
