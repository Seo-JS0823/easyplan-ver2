package com.easyplan.fixture;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.test.context.TestComponent;

import com.easyplan._shared.time.UTC;
import com.easyplan.member.application.provided.MemberPolicy;
import com.easyplan.member.application.required.MemberRepository;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.Nickname;
import com.easyplan.member.domain.PasswordEncoder;
import com.easyplan.member.domain.exception.MemberException;
import com.easyplan.member.domain.exception.MemberExceptionCode;
import com.easyplan.member.domain.request.MemberRegisterRequest;

import lombok.RequiredArgsConstructor;

@TestComponent
@RequiredArgsConstructor
public class MemberFix {
	
	private final MemberRepository memberRepo;
	
	private final PasswordEncoder encoder;
	
	private final MemberPolicy memberPolicy;
	
	public Member createActiveMember() {
		Member member = Member.register(memberRegisterRequest(), encoder, memberPolicy);
		
		return memberRepo.save(member);
	}
	
	public static String getActiveMemberPassword() {
		return memberRegisterRequest().password();
	}
	
	public static MemberRegisterRequest memberRegisterRequest() {
		return new MemberRegisterRequest(
				"junit@test.com",						// Email
				"password01@", 							// Password
				"개발자",										// Nickname	
				"Easyplan 개발자",					// Introduction
				LocalDate.of(1990, 12, 1),	// Birthdate
				"Asia/Seoul",								// ZoneId
				true,												// pushNotification
				true												// emailNotification
		);
	}
	
	public static MemberRegisterRequest memberRegisterRequest2() {
		return new MemberRegisterRequest(
				"easyplan@naver.com",
				"easyplan01@",
				"개발좌",
				"Easyplan 개발자 여러분 일하세요.",
				LocalDate.of(2000, 3, 4),
				"Asia/Seoul",
				true,
				true
		);
	}
	
	public static MemberPolicy createMemberPolicy() {
		return new MemberPolicy() {
			@Override
			public Instant deletionDate() {
				return UTC.nowSecond().plus(3, ChronoUnit.DAYS);
			}

			@Override
			public void validateNickname(Nickname nickname) {
				String name = nickname.nickname();
				
				if(name.equals("easyplan")) {
					throw new MemberException(MemberExceptionCode.FORBIDDEN_NICKNAME);
				}
			}

			@Override
			public void checkDuplicateEmail(String email) {
				if(email.equals("easyplan@test.com")) {
					throw new MemberException(MemberExceptionCode.DUPLICATE_EMAIL);
				}
			}

			@Override
			public void checkDuplicateNickname(String nickname) {
				if(nickname.equals("Easyplan")) {
					throw new MemberException(MemberExceptionCode.DUPLICATE_NICKNAME);
				}
			}
		};
	}
}
