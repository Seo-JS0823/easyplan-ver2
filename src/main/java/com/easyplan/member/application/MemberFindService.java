package com.easyplan.member.application;

import java.util.Optional;

import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.Email;
import com.easyplan._shared.domain.PublicId;
import com.easyplan.member.application.provided.MemberFinder;
import com.easyplan.member.application.provided.MemberPolicy;
import com.easyplan.member.application.provided.MemberSummary;
import com.easyplan.member.application.required.MemberRepository;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.Nickname;
import com.easyplan.member.domain.exception.MemberException;
import com.easyplan.member.domain.exception.MemberExceptionCode;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberFindService implements MemberFinder {
	
	@PersistenceContext
	private EntityManager em;
	
	private final MemberPolicy memberPolicy;
	
	private final MemberRepository repo;

	@Override
	public Member findById(Long id) {
		return repo.findById(id)
				.orElseThrow(() -> new MemberException(MemberExceptionCode.MEMBER_NOT_FOUND));
	}

	@Override
	public Member findByPublicId(PublicId memberPublicId) {
		/*
		return repo.findByPublicId(publicId)
				.orElseThrow(() -> new MemberException(MemberExceptionCode.MEMBER_NOT_FOUND));
		*/
		
		return em.unwrap(Session.class)
				.bySimpleNaturalId(Member.class)
				.loadOptional(memberPublicId)
				.orElseThrow(() -> new MemberException(MemberExceptionCode.MEMBER_NOT_FOUND));
	}
	
	@Override
	public Optional<Member> findOptionalByEmail(Email email) {
		return repo.findByEmail(email);
	}

	@Override
	public void checkDuplicateEmail(Email email) {
		if(repo.existsByEmail(email)) {
			throw new MemberException(MemberExceptionCode.DUPLICATE_EMAIL);
		}
	}

	@Override
	public void checkDuplicateNickname(Nickname nickname) {
		memberPolicy.validateNickname(nickname);
		
		if(repo.existsByNickname(nickname)) {
			throw new MemberException(MemberExceptionCode.DUPLICATE_NICKNAME);
		}
	}

	@Override
	public MemberSummary findByPublicIdSummary(PublicId publicId) {
		// return repo.findSummaryByPublicId(publicId);
		
		return new MemberSummaryImpl(findByPublicId(publicId));
	}

	@Override
	public MemberSummary findActiveMember(PublicId memberPublicId) {
		MemberSummary member = findByPublicIdSummary(memberPublicId);
		
		memberPolicy.canUseService(member);
		
		return member;
	}
	
}
