package com.easyplan.application.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.Email;
import com.easyplan.auth.application.provided.AuthSessionCommand;
import com.easyplan.auth.application.provided.AuthSessionFinder;
import com.easyplan.auth.application.provided.LoginResult;
import com.easyplan.auth.domain.request.LoginRequest;
import com.easyplan.fixture.MemberFix;
import com.easyplan.member.application.provided.MemberCommand;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.request.MemberRegisterRequest;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class AuthApplicationTest {
	
	@Autowired
	private EntityManager em;
	
	@Autowired
	private AuthSessionCommand authCommand;
	
	@Autowired
	private AuthSessionFinder authFinder;
	
	@Autowired
	private MemberCommand memberCommand;
	
	Member member;
	String password;
	@BeforeEach
	void setUp() {
		MemberRegisterRequest request = MemberFix.memberRegisterRequest();
		member = memberCommand.register(request);
		member = memberCommand.activate(member.getMemberPublicId());
		password = MemberFix.memberRegisterRequest().password();
	}
	
	@Test
	@DisplayName("로그인 테스트")
	void login() {
		Email email = member.getEmail();
		LoginRequest request = new LoginRequest(email.address(), password);
		
		LoginResult result = authCommand.login(request);
		
		em.flush();
		em.clear();
		
		assertThat(result.publicId()).isEqualTo(member.getMemberPublicId().publicId());
		assertThat(result.accessToken()).isNotNull();
		assertThat(result.refreshToken().length()).isEqualTo(64);
	}
	
	@Test
	@DisplayName("refresh token 재발급 테스트")
	void reissue() {
		LoginRequest request = new LoginRequest(member.getEmail().address(), password);
		LoginResult loginResult = authCommand.login(request);
		
		var result = authCommand.reissue(loginResult.refreshToken());
		
		assertThat(result.accessToken()).isNotNull();
		assertThat(result.refreshToken()).isNotEqualTo(loginResult.refreshToken());
		assertThat(result.refreshToken().length()).isEqualTo(64);
	}
	
	@Test
	@DisplayName("로그아웃 테스트")
	void logout() {
		LoginRequest request = new LoginRequest(member.getEmail().address(), password);
		LoginResult loginResult = authCommand.login(request);
		
		authCommand.logout(loginResult.refreshToken());
		
		assertThat(authFinder.findByMemberId(member.getId())).isEmpty();
	}
}
