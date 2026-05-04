package com.easyplan.application.member;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.Email;
import com.easyplan.fixture.MemberFix;
import com.easyplan.member.application.provided.MemberCommand;
import com.easyplan.member.application.provided.MemberFinder;
import com.easyplan.member.application.provided.MemberPolicy;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.MemberRole;
import com.easyplan.member.domain.MemberStatus;
import com.easyplan.member.domain.Nickname;
import com.easyplan.member.domain.PasswordEncoder;
import com.easyplan.member.domain.ProfileAddress;
import com.easyplan.member.domain.ProfileVisibility;
import com.easyplan.member.domain.request.MemberAccountRequest.MemberDeactivate;
import com.easyplan.member.domain.request.MemberRegisterRequest;
import com.easyplan.member.domain.request.MemberUpdateRequest.NotificationSetting;
import com.easyplan.member.domain.request.MemberUpdateRequest.PasswordUpdate;
import com.easyplan.member.domain.request.MemberUpdateRequest.ProfileDetail;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class MemberApplicationTest {
	@Autowired
	EntityManager em;
	
	@Autowired
	private MemberPolicy policy;
	
	@Autowired
	private MemberCommand command;
	
	@Autowired
	private MemberFinder finder;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	Member member;
	MemberRegisterRequest request;
	
	@BeforeEach
	void setUp() {
		request = MemberFix.memberRegisterRequest();
		member = Member.register(request, passwordEncoder, policy);
	}
	
	@Test
	@DisplayName("JPA 멤버 생성")
	void memberRegister() {
		Member member = register(request);
		Long savedId = member.getId();
		
		em.flush();
		em.clear();
		
		Member foundMember = finder.findByPublicId(member.getMemberPublicId());
		
		assertThat(foundMember.getId()).isNotNull();
		assertThat(savedId == foundMember.getId()).isTrue();
		assertThat(foundMember.getProfile()).isNotNull();
		assertThat(foundMember.getSetting()).isNotNull();
		
		assertThat(foundMember.getStatus() == MemberStatus.PENDING).isTrue();
		assertThat(foundMember.getRole() == MemberRole.PENDING).isTrue();
		assertThat(foundMember.getLastLoginAt()).isNull();
		assertThat(foundMember.getMemberPublicId()).isNotNull();
		
		assertThat(foundMember.getEmail()).isEqualTo(new Email(request.email()));
		assertThat(foundMember.getNickname()).isEqualTo(new Nickname(request.nickname()));
		assertThat(foundMember.getPasswordHash().passwordHash()).isNotEqualTo(request.password());
		assertThat(foundMember.getCreatedAt()).isNotNull();
		assertThat(foundMember.getUpdatedAt()).isNotNull();
		
		assertThat(foundMember.getProfile().getBirthdate().equals(request.birthdate())).isTrue();
		assertThat(foundMember.getProfile().getIntroduction().equals(request.introduction())).isTrue();
		assertThat(foundMember.getProfile().getProfile().equals(new ProfileAddress(request.nickname()))).isTrue();
		
		assertThat(foundMember.getSetting().isEmailNotification()).isTrue();
		assertThat(foundMember.getSetting().isPushNotification()).isTrue();
		assertThat(foundMember.getSetting().isFriendRequest()).isTrue();		
	}
	
	@Test
	@DisplayName("멤버 Active 전환")
	void activate() {
		Member member = register(request);
		
		em.flush();
		em.clear();
		
		Member updateMember = command.activate(member.getMemberPublicId());
		
		assertThat(updateMember.getStatus() == MemberStatus.ACTIVE).isTrue();
	}
	
	@Test
	@DisplayName("멤버 deactive 전환")
	void deactivate() {
		Member member = setUpMember(request);
		
		em.flush();
		em.clear();
		
		Member updateMember = command.deactivate(member.getMemberPublicId(), new MemberDeactivate(request.password()));
		
		em.flush();
		em.clear();
		
		assertThat(updateMember.getStatus() == MemberStatus.DEACTIVATED).isTrue();
	}
	
	@Test
	@DisplayName("패스워드 변경")
	void changePassword() {
		String rawPassword = request.password();
		
		Member member = setUpMember(request);
		
		em.flush();
		em.clear();
		
		Member updatePasswordMember = command.changePassword(member.getMemberPublicId(), new PasswordUpdate(rawPassword, "easyplan01@"));
		
		assertThat(passwordEncoder.matches("easyplan01@", updatePasswordMember.getPasswordHash().passwordHash())).isTrue();
	}
	
	@Test
	@DisplayName("프로필 변경")
	void changeProfileDetail() {
		Member member = setUpMember(request);
		
		em.flush();
		em.clear();
		
		String introduction = "안녕하세요. 새로 취업하게된 관리자입니다. 각오하세요";
		LocalDate birthdate = LocalDate.of(2000, 10, 1);
		ProfileDetail profile = new ProfileDetail(introduction, birthdate, ProfileVisibility.PRIVATE);
		
		command.changeProfileDetail(member.getMemberPublicId(), profile);
		
		em.flush();
		em.clear();
		
		Member found = finder.findByPublicId(member.getMemberPublicId());
		
		assertThat(found.getProfile().getIntroduction()).isEqualTo(introduction);
		assertThat(found.getProfile().getBirthdate()).isEqualTo(birthdate);
		assertThat(found.getProfile().getVisibility()).isEqualTo(ProfileVisibility.PRIVATE);
	}
	
	@Test
	@DisplayName("알림 설정 변경")
	void changeNotificationSetting() {
		Member member = setUpMember(request);
		
		em.flush();
		em.clear();
		
		var noti = new NotificationSetting(false, false);
		
		command.changeNotificationSetting(member.getMemberPublicId(), noti);
		
		em.flush();
		em.clear();
		
		Member found = finder.findByPublicId(member.getMemberPublicId());
		
		assertThat(found.getSetting().isEmailNotification()).isFalse();
		assertThat(found.getSetting().isPushNotification()).isFalse();
	}
	
	private Member setUpMember(MemberRegisterRequest request) {
		Member member = register(request);
		
		em.flush();
		em.clear();
		
		Member found = find(member);
		return command.activate(found.getMemberPublicId());
	}
	
	private Member register(MemberRegisterRequest request) {
		return command.register(request);
	}
	
	private Member find(Member member) {
		return finder.findByPublicId(member.getMemberPublicId());
	}
	
}
