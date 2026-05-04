package com.easyplan.member.application;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.easyplan._shared.domain.Email;
import com.easyplan._shared.domain.PublicId;
import com.easyplan._shared.time.UTC;
import com.easyplan.member.application.provided.MemberFinder;
import com.easyplan.member.application.provided.MemberPolicy;
import com.easyplan.member.application.provided.MemberSummary;
import com.easyplan.member.domain.Nickname;
import com.easyplan.member.domain.exception.MemberException;
import com.easyplan.member.domain.exception.MemberExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberPolicyService implements MemberPolicy {

	private final MemberFinder memberFinder;
	
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
		memberFinder.checkDuplicateEmail(new Email(email));
	}

	@Override
	public void checkDuplicateNickname(String nickname) {
		memberFinder.checkDuplicateNickname(new Nickname(nickname));
	}

	@Override
	public MemberSummary canUseService(PublicId memberPublicId) {
		MemberSummary member = memberFinder.findByPublicIdSummary(memberPublicId);
		
		member.canUseService();
		
		return member;
	}

}
