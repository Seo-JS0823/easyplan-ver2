package com.easyplan.member.adapter.webapi;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyplan._shared.domain.PublicId;
import com.easyplan._shared.response.GlobalResponse;
import com.easyplan.member.application.provided.MemberCommand;
import com.easyplan.member.domain.request.MemberUpdateRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members/me")
@RequiredArgsConstructor
public class MemberUpdateController {
	private final MemberCommand memberCommand;
	
	// 닉네임 변경 API
	@PatchMapping("/nickname/{publicId}")
	public GlobalResponse<Void> memberNicknameUpdateAPI(
			@RequestBody MemberUpdateRequest.NicknameUpdate nicknameUpdate,
			@PathVariable String publicId) {
		
		PublicId pid = new PublicId(publicId);
		
		memberCommand.changeNickname(pid, nicknameUpdate);
		
		return GlobalResponse.ok("닉네임이 변경되었습니다.");
	}
	
	// 프로필 상세 수정 API
	@PutMapping("/profile/{publicId}")
	public GlobalResponse<Void> memberProfileUpdateAPI(
			@RequestBody MemberUpdateRequest.ProfileDetail profileUpdate,
			@PathVariable String publicId) {
		
		PublicId pid = new PublicId(publicId);
		
		memberCommand.changeProfileDetail(pid, profileUpdate);
		
		return GlobalResponse.ok("프로필 설정이 변경되었습니다.");
	}
	
	// 알림 설정 수정 API
	@PutMapping("/setting/{publicId}")
	public GlobalResponse<Void> memberSettingUpdateAPI(
			@RequestBody MemberUpdateRequest.NotificationSetting notificationUpdate,
			@PathVariable String publicId) {
		
		PublicId pid = new PublicId(publicId);
		
		memberCommand.changeNotificationSetting(pid, notificationUpdate);
		
		return GlobalResponse.ok("알림 설정 정보가 변경되었습니다.");
	}
}
