package com.easyplan.member.application;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.easyplan._shared.domain.Email;
import com.easyplan._shared.time.UTC;
import com.easyplan.member.application.provided.MemberPolicy;
import com.easyplan.member.application.required.MemberRepository;
import com.easyplan.member.domain.Nickname;
import com.easyplan.member.domain.exception.MemberException;
import com.easyplan.member.domain.exception.MemberExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberPolicyService implements MemberPolicy {

	private final MemberRepository repo;
	
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
	public void checkDuplicateEmail(String email) {
		if(repo.existsByEmail(new Email(email))) {
			throw new MemberException(MemberExceptionCode.DUPLICATE_EMAIL);
		}
	}

	@Override
	public void checkDuplicateNickname(String nickname) {
		if(repo.existsByNickname(new Nickname(nickname))) {
			throw new MemberException(MemberExceptionCode.DUPLICATE_NICKNAME);
		}
	}

}
