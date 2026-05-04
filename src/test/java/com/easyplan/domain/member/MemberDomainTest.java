package com.easyplan.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.easyplan._shared.util.NonNull;
import com.easyplan.fixture.MemberFix;
import com.easyplan.member.application.provided.MemberPolicy;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.MemberRole;
import com.easyplan.member.domain.MemberStatus;
import com.easyplan.member.domain.Nickname;
import com.easyplan.member.domain.PasswordEncoder;
import com.easyplan.member.domain.ProfileAddress;
import com.easyplan.member.domain.ProfileVisibility;
import com.easyplan.member.domain.request.MemberRegisterRequest;
import com.easyplan.member.domain.request.MemberUpdateRequest.NotificationSetting;
import com.easyplan.member.domain.request.MemberUpdateRequest.ProfileDetail;

public class MemberDomainTest {
	
	PasswordEncoder passwordEncoder = new PasswordEncoder() {
		
		private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		@Override
		public String encode(String password) {
			return encoder.encode(password);
		}

		@Override
		public boolean matches(String password, String passwordHash) {
			return encoder.matches(password, passwordHash);
		}
		
	};
	
	Member member;
	MemberRegisterRequest request;
	MemberPolicy policy;
	
	@BeforeEach
	void setUp() {
		request = MemberFix.memberRegisterRequest();
		policy = MemberFix.createMemberPolicy();
		member = Member.register(request, passwordEncoder, policy);
	}
	
	
	@Test
	@DisplayName("회원 생성")
	void memberCreate() {
		assertAll(
				() -> assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING),
				() -> assertThat(member.getRole()).isEqualTo(MemberRole.PENDING),
				
				() -> assertThat(member.getEmail().address()).isEqualTo("junit@test.com"),
				() -> assertThat(member.getNickname().nickname()).isEqualTo("개발자"),
				() -> assertThat(member.getLastLoginAt()).isNull(),
				
				() -> assertThat(member.getProfile().getImageUrl()).isNull(),
				() -> assertThat(member.getProfile().getVisibility()).isEqualTo(ProfileVisibility.PUBLIC),
				() -> assertThat(member.getProfile().getIntroduction()).isEqualTo("Easyplan 개발자"),
				
				() -> assertThat(member.getSetting().isPushNotification()).isTrue(),
				() -> assertThat(member.getSetting().isEmailNotification()).isTrue(),
				() -> assertThat(member.getSetting().isFriendRequest()).isTrue(),
				() -> assertThat(member.getSetting().getZoneId().text()).isEqualTo("Asia/Seoul")
		);
	}
	
	@Test
	@DisplayName("회원 변경")
	void memberUpdate() {
		member.activate();
		assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
		
		Nickname newNickname = new Nickname("easyplan2");
		member.changeNickname(newNickname, policy);
		assertThat(member.getNickname()).isEqualTo(newNickname);
		assertThat(member.getProfile().getProfile()).isEqualTo(new ProfileAddress(newNickname.nickname()));
		
		String newPassword = "easyplanPassword01@";
		member.changePassword(request.password(), newPassword, passwordEncoder);
		assertThat(member.verifyPassword(newPassword, passwordEncoder)).isTrue();
		
		String newImageUrl = "C:USER:DRIVER:EASYPLAN";
		member.changeImageUrl(newImageUrl);
		assertThat(member.getProfile().getImageUrl()).isEqualTo(newImageUrl);
		
		ProfileDetail detail = new ProfileDetail("Easyplan Introduction", LocalDate.of(1998, 1, 1), ProfileVisibility.PRIVATE);
		member.changeProfileDetail(detail);
		assertAll(
				() -> assertThat(member.getProfile().getBirthdate()).isEqualTo(LocalDate.of(1998, 1, 1)),
				() -> assertThat(member.getProfile().getIntroduction()).isEqualTo("Easyplan Introduction"),
				() -> assertThat(member.getProfile().getVisibility()).isEqualTo(ProfileVisibility.PRIVATE)				
		);
		
		NotificationSetting setting = new NotificationSetting(false, false);
		member.changeNotificationSetting(setting);
		assertAll(
				() -> assertThat(member.getSetting().isEmailNotification()).isFalse(),
				() -> assertThat(member.getSetting().isPushNotification()).isFalse(),
				() -> assertThat(member.getSetting().isFriendRequest()).isTrue()
		);
	}
	
	@Test
	@DisplayName("회원 변경 실패케이스")
	void memberUpdateFail() {
		assertThat(member.getStatus().equals(MemberStatus.ACTIVE)).isFalse();
		
		Nickname newNickname = new Nickname("easyplan2");
		assertThatThrownBy(
				() -> member.changeNickname(newNickname, policy)
		).isInstanceOf(IllegalStateException.class);
		assertThat(member.getNickname().equals(newNickname)).isFalse();
		assertThat(member.getProfile().getProfile().equals(new ProfileAddress(newNickname.nickname()))).isFalse();
		
		String newPassword = "easyplanPassword01@";
		assertThatThrownBy(
				() -> member.changePassword(request.password(), newPassword, passwordEncoder)
		).isInstanceOf(IllegalStateException.class);
		assertThat(member.verifyPassword(newPassword, passwordEncoder)).isFalse();
		
		String newImageUrl = "C:USER:DRIVER:EASYPLAN";
		assertThatThrownBy(
				() -> member.changeImageUrl(newImageUrl)
		).isInstanceOf(IllegalStateException.class);
		assertThat(NonNull.eq(member.getProfile().getImageUrl(),newImageUrl)).isFalse();
		
		ProfileDetail detail = new ProfileDetail("Easyplan Introduction", LocalDate.of(1998, 1, 1), ProfileVisibility.PRIVATE);
		assertThatThrownBy(
				() -> member.changeProfileDetail(detail)
		).isInstanceOf(IllegalStateException.class);
		assertAll(
				() -> assertThat(member.getProfile().getBirthdate().equals(LocalDate.of(1998, 1, 1))).isFalse(),
				() -> assertThat(NonNull.eq(member.getProfile().getIntroduction(),"Easyplan Introduction")).isFalse(),
				() -> assertThat(member.getProfile().getVisibility().equals(ProfileVisibility.PRIVATE)).isFalse()
		);
		
		NotificationSetting setting = new NotificationSetting(false, false);
		assertThatThrownBy(
				() -> member.changeNotificationSetting(setting)
		).isInstanceOf(IllegalStateException.class);
		assertAll(
				() -> assertThat(member.getSetting().isEmailNotification() == false).isFalse(),
				() -> assertThat(member.getSetting().isPushNotification() == false).isFalse(),
				() -> assertThat(member.getSetting().isFriendRequest() == true).isTrue()
		);
	}
	
	@Test
	@DisplayName("회원 삭제")
	void memberDelete() {
		member.activate();
		
		assertThat(member.getDeletionDate()).isNull();
		
		member.deactivate(policy);
		
		assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
		assertThat(member.getDeletionDate()).isNotNull();
	}
	
}
