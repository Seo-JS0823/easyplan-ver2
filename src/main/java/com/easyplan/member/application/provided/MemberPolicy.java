package com.easyplan.member.application.provided;

import java.time.Instant;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.member.domain.Nickname;

public interface MemberPolicy {
	Instant deletionDate();
	
	void validateNickname(Nickname nickname);
	
	void checkDuplicateEmail(String email);
	
	void checkDuplicateNickname(String nickname);
	
	MemberSummary canUseService(PublicId memberPublicId);
}
