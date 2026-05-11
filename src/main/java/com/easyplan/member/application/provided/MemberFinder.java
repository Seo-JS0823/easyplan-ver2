package com.easyplan.member.application.provided;

import java.util.Optional;

import com.easyplan._shared.domain.Email;
import com.easyplan._shared.domain.PublicId;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.Nickname;

// 멤버 조회 인터페이스
public interface MemberFinder {
	
	// 외부 도메인에서 서비스 사용 가능한 멤버를 조회해야할 때 사용
	MemberSummary findActiveMember(PublicId memberPublicId);
	
	MemberSummary findByPublicIdSummary(PublicId memberPublicId);
	
	Member findById(Long id);
	
	Member findByPublicId(PublicId memberPublicId);
	
	Optional<Member> findOptionalByEmail(Email email);
	
	void checkDuplicateEmail(Email email);
	
	void checkDuplicateNickname(Nickname nickname);
}
