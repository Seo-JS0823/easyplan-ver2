package com.easyplan.member.application.required;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.easyplan._shared.domain.Email;
import com.easyplan._shared.domain.PublicId;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.MemberStatus;
import com.easyplan.member.domain.Nickname;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByMemberPublicId(PublicId publicId);
	
	@Query("""
			SELECT m FROM Member m
			WHERE
					m.status = :status AND
					m.deletionDate <= :deletionDeadline
	""")
	List<Member> findByDeactivatedMembers(MemberStatus status, Instant deletionDeadline);
	
	boolean existsByEmail(Email email);
	
	boolean existsByNickname(Nickname Nickname);

	Optional<Member> findByEmail(Email email);
}
