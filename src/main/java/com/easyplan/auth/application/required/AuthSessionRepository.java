package com.easyplan.auth.application.required;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.easyplan.auth.domain.AuthSession;
import com.easyplan.auth.domain.RefreshTokenHash;

@Repository
public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {
	Optional<AuthSession> findByMemberId(Long memberId);
	
	Optional<AuthSession> findByTokenHash(RefreshTokenHash tokenHash);
}
