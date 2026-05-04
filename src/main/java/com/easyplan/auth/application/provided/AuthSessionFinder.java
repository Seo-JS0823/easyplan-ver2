package com.easyplan.auth.application.provided;

import java.util.Optional;

import com.easyplan.auth.domain.AuthSession;

public interface AuthSessionFinder {
	Optional<AuthSession> findByMemberId(Long memberId);
}
