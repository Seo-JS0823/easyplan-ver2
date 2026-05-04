package com.easyplan.member.application.provided;

import java.time.Instant;

import com.easyplan._shared.domain.PublicId;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.request.MemberAccountRequest;
import com.easyplan.member.domain.request.MemberRegisterRequest;
import com.easyplan.member.domain.request.MemberUpdateRequest;

// 멤버 등록, 수정, 삭제 인터페이스
public interface MemberCommand {
	// 회원을 생성한다.
	Member register(MemberRegisterRequest memberRegister);
	
	Member activate(PublicId publicId);
	
	Member deactivate(PublicId publicId, MemberAccountRequest.MemberDeactivate memberDeactivate);
	
	Member recover(PublicId pulbicId, MemberAccountRequest.MemberRecover memberRecover);
	
	Member changeNickname(PublicId publicId, MemberUpdateRequest.NicknameUpdate nicknameUpdate);
	
	Member changePassword(PublicId publicId, MemberUpdateRequest.PasswordUpdate passwordUpdate);
	
	Member changeProfileDetail(PublicId publicId, MemberUpdateRequest.ProfileDetail profileDetail);
	
	Member changeNotificationSetting(PublicId publicId, MemberUpdateRequest.NotificationSetting notificationSetting);
	
	// 이메일 인증
	boolean emailVerify(String token);
	
	// Hard Delete
	void cleanUpDeactivatedMembers(Instant deletionDeadline);
}
