package com.easyplan.member.application.provided;

import java.util.Optional;

import com.easyplan._shared.domain.Email;
import com.easyplan._shared.domain.PublicId;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.Nickname;

// 멤버 조회 인터페이스
public interface MemberFinder {
	MemberSummary findByPublicIdSummary(PublicId publicId);
	
	Member findById(Long id);
	
	Member findByPublicId(PublicId publicId);
	
	Optional<Member> findOptionalByEmail(Email email);
	
	void checkDuplicateEmail(Email email);
	
	void checkDuplicateNickname(Nickname nickname);
}
