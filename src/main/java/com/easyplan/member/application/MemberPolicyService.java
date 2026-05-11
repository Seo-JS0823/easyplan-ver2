package com.easyplan.member.application;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Component;

import com.easyplan._shared.time.UTC;
import com.easyplan.member.application.provided.MemberPolicy;
import com.easyplan.member.application.provided.MemberSummary;
import com.easyplan.member.domain.MemberRole;
import com.easyplan.member.domain.MemberStatus;
import com.easyplan.member.domain.Nickname;
import com.easyplan.member.domain.exception.MemberException;
import com.easyplan.member.domain.exception.MemberExceptionCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberPolicyService implements MemberPolicy {
	private static final List<String> FORBIDDEN_NICKNAMES = List.of("관리자", "admin");
	
	@Override
	public Instant deletionDate() {
		return UTC.nowSecond().plus(3, ChronoUnit.DAYS);
	}

	@Override
	public void validateNickname(Nickname nickname) {
		String name = nickname.nickname().toLowerCase();
		
		if(FORBIDDEN_NICKNAMES.contains(name)) {
			throw new MemberException(MemberExceptionCode.FORBIDDEN_NICKNAME);
		}
	}

	@Override
	public void canUseService(MemberSummary member) {
		if(member.getStatus() != MemberStatus.ACTIVE) {
			throw new MemberException(MemberExceptionCode.MEMBER_CANNOT_USE_SERVICE);
		}
		
		if(member.getRole() == MemberRole.PENDING) {
			throw new MemberException(MemberExceptionCode.MEMBER_CANNOT_USE_SERVICE_VERIFY_EMAIL);
		}
	}
	
}
