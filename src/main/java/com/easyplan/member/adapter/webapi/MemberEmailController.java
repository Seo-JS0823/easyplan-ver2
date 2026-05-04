package com.easyplan.member.adapter.webapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.easyplan._shared.response.GlobalResponse;
import com.easyplan.member.application.provided.MemberCommand;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberEmailController {
	
	private final MemberCommand memberCommand;
	
	@GetMapping("/verify")
	public GlobalResponse<Void> emailVerify(@RequestParam("token") String emailToken) {
		if(!memberCommand.emailVerify(emailToken)) {
			return GlobalResponse.fail("EMAIL_VERIFY_FAIL", "이메일 인증이 실패하였습니다.");
		}
		
		return GlobalResponse.ok("이메일 인증에 성공하였습니다.");
	}
}
