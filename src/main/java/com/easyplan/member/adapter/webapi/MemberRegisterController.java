package com.easyplan.member.adapter.webapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.easyplan._shared.domain.Email;
import com.easyplan._shared.response.GlobalResponse;
import com.easyplan.member.application.provided.MemberCommand;
import com.easyplan.member.application.provided.MemberFinder;
import com.easyplan.member.domain.Nickname;
import com.easyplan.member.domain.request.MemberRegisterRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberRegisterController {
	private final MemberCommand memberCommand;
	
	private final MemberFinder memberFinder;
	
	// 회원가입 API
	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public GlobalResponse<Void> memberRegisterAPI(@RequestBody MemberRegisterRequest memberRegister) {
		memberCommand.register(memberRegister);
		
		return GlobalResponse.ok("회원가입이 완료되었습니다.");
	}
	
	// 이메일 중복 체크 API
	@GetMapping("/check-email")
	public GlobalResponse<Void> emailCheckAPI(@RequestParam String email) {
		memberFinder.checkDuplicateEmail(new Email(email));
		
		return GlobalResponse.ok("사용 가능한 이메일입니다.");
	}
	
	// 닉네임 중복 체크 API
	@GetMapping("/check-nickname")
	public GlobalResponse<Void> nicknameCheckAPI(@RequestParam String nickname) {
		memberFinder.checkDuplicateNickname(new Nickname(nickname));
		
		return GlobalResponse.ok("사용 가능한 닉네임입니다.");
	}
}
