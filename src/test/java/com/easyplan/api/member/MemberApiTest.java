package com.easyplan.api.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.util.CookieProp;
import com.easyplan.auth.domain.request.LoginRequest;
import com.easyplan.fixture.FakeEmailSender;
import com.easyplan.fixture.MemberFix;
import com.easyplan.fixture.TestEmailConfig;
import com.easyplan.member.application.provided.MemberCommand;
import com.easyplan.member.application.provided.MemberFinder;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.MemberStatus;
import com.easyplan.member.domain.Nickname;
import com.easyplan.member.domain.PasswordEncoder;
import com.easyplan.member.domain.ProfileVisibility;
import com.easyplan.member.domain.request.MemberAccountRequest;
import com.easyplan.member.domain.request.MemberAccountRequest.MemberDeactivate;
import com.easyplan.member.domain.request.MemberRegisterRequest;
import com.easyplan.member.domain.request.MemberUpdateRequest;
import com.easyplan.member.domain.request.MemberUpdateRequest.NicknameUpdate;
import com.easyplan.member.domain.request.MemberUpdateRequest.NotificationSetting;
import com.easyplan.member.domain.request.MemberUpdateRequest.PasswordUpdate;
import com.easyplan.member.domain.request.MemberUpdateRequest.ProfileDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.Cookie;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestEmailConfig.class)
public class MemberApiTest {

	@Autowired
	private MockMvc mockMvc;
	
	private final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private MemberCommand command;
	
	@Autowired
	private FakeEmailSender emailSender;
	
	@Autowired
	private MemberFinder finder;
	
	Member member;
	MemberRegisterRequest request;
	Cookie accessCookie;
	
	@BeforeEach
	void setUp() throws Exception {
		request = MemberFix.memberRegisterRequest();
		member = command.register(request);
		
		command.activate(member.getMemberPublicId());
		accessCookie = loginAccessCookie();
	}

	private Cookie loginAccessCookie() throws Exception {
		LoginRequest loginRequest = new LoginRequest(request.email(), request.password());

		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isOk())
			.andReturn();

		return loginResult.getResponse().getCookie(CookieProp.ACCESS.getName());
	}
	
	@Test
  @DisplayName("회원가입 API")
  void register() throws Exception {
		System.out.println("EmailSender Bean: " + emailSender.getClass());
		
    MemberRegisterRequest request = MemberFix.memberRegisterRequest2();

    mockMvc
    	.perform(post("/api/members")
    			.with(csrf())
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(request))
    	)
    	.andExpect(status().isCreated())
    	.andExpect(jsonPath("$.success").value(true))
    	.andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."));
    
    String token = emailSender.getLastToken();
    
    mockMvc.perform(get("/api/members/verify")
        .param("token", token))
    .andDo(print())
    .andExpect(status().isOk());
  }
	
	@Test
	@DisplayName("이메일 중복 체크 API")
	void checkEmail() throws Exception {
		mockMvc
			.perform(get("/api/members/check-email")
					.with(csrf())
					.param("email", "testtest@test.com")
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("사용 가능한 이메일입니다."));
	}
	
	@Test
	@DisplayName("닉네임 중복 체크 API")
	void checkNickname() throws Exception {
		mockMvc
			.perform(get("/api/members/check-nickname")
					.with(csrf())
					.param("nickname", "개발좌")
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("사용 가능한 닉네임입니다."));
	}
	
	@Test
	@DisplayName("닉네임 변경 API")
	void changeNickname() throws Exception {
		MemberUpdateRequest.NicknameUpdate request = new NicknameUpdate("Easyplan");
		
		mockMvc
			.perform(patch("/api/members/me/nickname/{publicId}", member.getMemberPublicId().publicId())
			.with(csrf())
			.cookie(accessCookie)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.success").value(true))
		.andExpect(jsonPath("$.message").value("닉네임이 변경되었습니다."));
		
		Member entity = finder.findByPublicId(member.getMemberPublicId());
		assertThat(entity.getStatus()).isEqualTo(MemberStatus.ACTIVE);
		assertThat(entity.getNickname()).isEqualTo(new Nickname("Easyplan"));
	}
	
	@Test
	@DisplayName("프로필 상세 수정 API")
	void changeProfile() throws Exception {
		String introduction = "Introduction";
		LocalDate birthdate = LocalDate.of(2000, 1, 1);
		MemberUpdateRequest.ProfileDetail request = new ProfileDetail(introduction, birthdate, ProfileVisibility.PRIVATE);
		
		mockMvc
			.perform(put("/api/members/me/profile/{publicId}", member.getMemberPublicId().publicId())
			.with(csrf())
			.cookie(accessCookie)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.success").value(true))
		.andExpect(jsonPath("$.message").value("프로필 설정이 변경되었습니다."));
		
		Member target = finder.findByPublicId(member.getMemberPublicId());
		
		assertThat(target.getProfile().getIntroduction()).isEqualTo(introduction);
		assertThat(target.getProfile().getBirthdate()).isEqualTo(birthdate);
		assertThat(target.getProfile().getVisibility()).isEqualTo(ProfileVisibility.PRIVATE);
	}
	
	@Test
	@DisplayName("알림 설정 수정 API")
	void changeNotifiationSetting() throws Exception {
		MemberUpdateRequest.NotificationSetting request = new NotificationSetting(false, false);
		
		mockMvc.perform(put("/api/members/me/setting/{publicId}", member.getMemberPublicId().publicId())
				.with(csrf())
				.cookie(accessCookie)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.success").value(true))
		.andExpect(jsonPath("$.message").value("알림 설정 정보가 변경되었습니다."));
		
		Member target = finder.findByPublicId(member.getMemberPublicId());
		
		assertThat(target.getSetting().isEmailNotification()).isFalse();
		assertThat(target.getSetting().isPushNotification()).isFalse();
	}
	
	@Test
	@DisplayName("비밀번호 변경 API")
	void changePassword() throws Exception {
		MemberUpdateRequest.PasswordUpdate request = new PasswordUpdate(this.request.password(), "easyplan1234@");
		
		mockMvc.perform(patch("/api/members/me/password/{publicId}", member.getMemberPublicId().publicId())
				.with(csrf())
				.cookie(accessCookie)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.success").value(true))
		.andExpect(jsonPath("$.message").value("비밀번호가 정상적으로 변경되었습니다."));
		
		Member target = finder.findByPublicId(member.getMemberPublicId());
		
		assertThat(target.verifyPassword("easyplan1234@", passwordEncoder)).isTrue();
	}
	
	@Test
	@DisplayName("계정 탈퇴 API")
	void deactivate() throws Exception {
		MemberAccountRequest.MemberDeactivate request = new MemberDeactivate(this.request.password());
		
		mockMvc.perform(post("/api/members/me/deactivate/{publicId}", member.getMemberPublicId().publicId())
				.with(csrf())
				.cookie(accessCookie)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.success").value(true))
		.andExpect(jsonPath("$.message").value("계정 탈퇴가 성공적으로 진행되었습니다."));
		
		Member target = finder.findByPublicId(member.getMemberPublicId());
		
		assertThat(target.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
		assertThat(target.getDeletionDate()).isNotNull();
	}
}
