package com.easyplan.auth.application;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.Email;
import com.easyplan._shared.time.UTC;
import com.easyplan.auth.application.provided.AuthSessionCommand;
import com.easyplan.auth.application.provided.AuthSessionFinder;
import com.easyplan.auth.application.provided.LoginResult;
import com.easyplan.auth.application.provided.ReissueResult;
import com.easyplan.auth.application.required.AuthSessionRepository;
import com.easyplan.auth.application.required.TokenProvider;
import com.easyplan.auth.domain.AuthSession;
import com.easyplan.auth.domain.RefreshTokenHash;
import com.easyplan.auth.domain.TokenPair;
import com.easyplan.auth.domain.exception.AuthException;
import com.easyplan.auth.domain.exception.AuthExceptionCode;
import com.easyplan.auth.domain.policy.AuthSessionPolicy;
import com.easyplan.auth.domain.request.LoginRequest;
import com.easyplan.member.application.provided.MemberFinder;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthSessionCommandService implements AuthSessionCommand {
	private final AuthSessionRepository authRepo;
	
	private final MemberFinder memberFinder;
	
	private final AuthSessionFinder authFinder;
	
	private final TokenProvider tokenProvider;
	
	private final PasswordEncoder encoder;
	
	private final AuthSessionPolicy authPolicy;

	@Override
	public LoginResult login(LoginRequest login) {
		Member member = memberFinder.findOptionalByEmail(new Email(login.email()))
				.orElseThrow(() -> new AuthException(AuthExceptionCode.LOGIN_FAIL));
		
		if(!member.verifyPassword(login.password(), encoder)) {
			throw new AuthException(AuthExceptionCode.LOGIN_FAIL);
		}
		
		member.isActive();
		
		Long memberId = member.getId();
		
		TokenPair tokens = tokenProvider.createTokenPair(member.getMemberPublicId(), member.getRole(), authPolicy.getAccessTokenTime());
		RefreshTokenHash newTokenHash = tokenProvider.hashToken(tokens.refresh());
		Instant newExpiresAt = authPolicy.refreshExpiresAt();
		
		Optional<AuthSession> optionalSession = authFinder.findByMemberId(memberId);
		
		AuthSession authSession;
		if(optionalSession.isPresent()) {
			authSession = optionalSession.get();
			authSession.rotation(newTokenHash, newExpiresAt);
		} else {
			authSession = AuthSession.create(memberId, newTokenHash, newExpiresAt);
		}
		
		AuthSession savedSession = authRepo.save(authSession);
		
		member.loginAtUpdate(UTC.nowSecond());
		
		return new LoginResult(
				member.getMemberPublicId().publicId(),
				tokens.access(),
				authPolicy.accessExpiresAt(),
				tokens.refresh(),
				savedSession.getExpiresAt()
		);
	}

	@Override
	public ReissueResult reissue(String refreshToken) {
		if(refreshToken == null || refreshToken.isBlank()) {
			throw new AuthException(AuthExceptionCode.REFRESH_TOKEN_EMPTY);
		}
		
		RefreshTokenHash oldTokenHash = tokenProvider.hashToken(refreshToken);
		AuthSession authSession = authRepo.findByTokenHash(oldTokenHash)
				.orElseThrow(() -> new AuthException(AuthExceptionCode.REFRESH_TOKEN_NOT_FOUND));
		
		if(!tokenProvider.validateRefreshToken(refreshToken, authSession.getTokenHash())) {
			authRepo.delete(authSession);
			
			throw new AuthException(AuthExceptionCode.REFRESH_TOKEN_MISMATCH);
		}
		
		if(authSession.isExpired(UTC.nowSecond())) {
			authRepo.delete(authSession);
			
			throw new AuthException(AuthExceptionCode.REFRESH_TOKEN_EXPIRED);
		}
		
		Member member = memberFinder.findById(authSession.getMemberId());
		member.isActive();
		
		TokenPair tokens = tokenProvider.createTokenPair(member.getMemberPublicId(), member.getRole(), authPolicy.getAccessTokenTime());
		RefreshTokenHash newTokenHash = tokenProvider.hashToken(tokens.refresh());
		Instant newExpiresAt = authPolicy.refreshExpiresAt();
		
		authSession.rotation(newTokenHash, newExpiresAt);
		
		return new ReissueResult(
				tokens.access(),
				authPolicy.accessExpiresAt(),
				tokens.refresh(),
				newExpiresAt
		);
	}

	@Override
	public void logout(String refreshToken) {
		if(refreshToken == null || refreshToken.isBlank()) {
			return;
		}
		
		RefreshTokenHash tokenHash = tokenProvider.hashToken(refreshToken);
		authRepo.findByTokenHash(tokenHash)
				.ifPresent(authRepo::delete);
	}
}
