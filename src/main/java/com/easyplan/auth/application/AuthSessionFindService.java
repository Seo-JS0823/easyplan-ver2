package com.easyplan.auth.application;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan.auth.application.provided.AuthSessionFinder;
import com.easyplan.auth.application.required.AuthSessionRepository;
import com.easyplan.auth.domain.AuthSession;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthSessionFindService implements AuthSessionFinder {

	private final AuthSessionRepository authRepo;
	
	@Override
	public Optional<AuthSession> findByMemberId(Long memberId) {
		return authRepo.findByMemberId(memberId);
	}
	
}
