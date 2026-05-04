package com.easyplan.member.adapter.webapi;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyplan._shared.domain.PublicId;
import com.easyplan._shared.response.GlobalResponse;
import com.easyplan.member.application.provided.MemberCommand;
import com.easyplan.member.domain.request.MemberAccountRequest;
import com.easyplan.member.domain.request.MemberUpdateRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members/me")
@RequiredArgsConstructor
public class MemberAccountController {
	private final MemberCommand memberCommand;
	
	// 비밀번호 변경 API
	@PatchMapping("/password/{publicId}")
	public GlobalResponse<Void> memberPasswordUpdateAPI(
			@RequestBody MemberUpdateRequest.PasswordUpdate passwordUpdate,
			@PathVariable String publicId) {
		
		PublicId pid = new PublicId(publicId);
		
		memberCommand.changePassword(pid, passwordUpdate);
		
		return GlobalResponse.ok("비밀번호가 정상적으로 변경되었습니다.");
	}
	
	
	// 계정 탈퇴 API
	@PostMapping("/deactivate/{publicId}")
	public GlobalResponse<Void> memberDeactivateAPI(
			@RequestBody MemberAccountRequest.MemberDeactivate memberDeactivate,
			@PathVariable String publicId) {
		
		PublicId pid = new PublicId(publicId);
		
		memberCommand.deactivate(pid, memberDeactivate);
		
		return GlobalResponse.ok("계정 탈퇴가 성공적으로 진행되었습니다.");
	}
	
	// 계정 복구 API
	@PostMapping("/reactivate/{publicId}")
	public GlobalResponse<Void> memberReactivateAPI(
			@RequestBody MemberAccountRequest.MemberRecover memberRecover,
			@PathVariable String publicId) {
		
		PublicId pid = new PublicId(publicId);
		
		memberCommand.recover(pid, memberRecover);
		
		return GlobalResponse.ok("계정이 복구되었습니다.");
	}
}
