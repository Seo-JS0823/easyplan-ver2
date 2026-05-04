package com.easyplan.api.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.adapter.CookieProp;
import com.easyplan.auth.domain.request.LoginRequest;
import com.easyplan.fixture.MemberFix;
import com.easyplan.member.application.provided.MemberCommand;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.request.MemberRegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthApiTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private MemberCommand memberCommand;
	
	Member member;
	MemberRegisterRequest request;
	LoginRequest loginRequest;
	
	private final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	
	@BeforeEach
	void setUp() {
		request = MemberFix.memberRegisterRequest();
		member = memberCommand.register(request);
		
		memberCommand.activate(member.getMemberPublicId());
		
		loginRequest = new LoginRequest(this.request.email(), this.request.password());
	}
	
	@Test
	@DisplayName("로그인 API")
	void login() throws Exception {
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.success").value(true))
		.andExpect(jsonPath("$.message").value("로그인 성공"))
		.andExpect(cookie().exists(CookieProp.ACCESS.getName()))
		.andExpect(cookie().exists(CookieProp.REFRESH.getName()))
		.andExpect(cookie().httpOnly(CookieProp.ACCESS.getName(), true))
		.andExpect(cookie().httpOnly(CookieProp.REFRESH.getName(), true));
	}
	
	@Test
	@DisplayName("토큰 재발급 API")
	void reissue() throws Exception {
		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
		.andExpect(status().isOk())
		.andReturn();
		
		Cookie refreshCookie = loginResult.getResponse().getCookie(CookieProp.REFRESH.getName());
		
		mockMvc.perform(post("/api/auth/reissue")
				.cookie(refreshCookie))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.success").value(true))
		.andExpect(jsonPath("$.message").value("토큰 재발급 성공"))
		.andExpect(cookie().exists(CookieProp.ACCESS.getName()))
		.andExpect(cookie().exists(CookieProp.REFRESH.getName()));
	}
	
	@Test
	@DisplayName("로그아웃 API")
	void logout() throws Exception {
		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
		.andExpect(status().isOk())
		.andReturn();
		
		Cookie refreshCookie = loginResult.getResponse().getCookie(CookieProp.REFRESH.getName());
		
		mockMvc.perform(post("/api/auth/logout")
				.cookie(refreshCookie))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.success").value(true))
		.andExpect(jsonPath("$.message").value("로그아웃 성공"))
		.andExpect(cookie().maxAge(CookieProp.ACCESS.getName(), 0))
		.andExpect(cookie().maxAge(CookieProp.REFRESH.getName(), 0));
	}
}
