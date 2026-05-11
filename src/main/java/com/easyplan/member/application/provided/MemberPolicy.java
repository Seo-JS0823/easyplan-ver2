package com.easyplan.member.application.provided;

import java.time.Instant;

import com.easyplan.member.domain.Nickname;

public interface MemberPolicy {
	Instant deletionDate();
	
	void validateNickname(Nickname nickname);
	
	void canUseService(MemberSummary member);
}
