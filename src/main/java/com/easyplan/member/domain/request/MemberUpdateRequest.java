package com.easyplan.member.domain.request;

import java.time.LocalDate;

import com.easyplan.member.domain.ProfileVisibility;

public class MemberUpdateRequest {
	public record ProfileDetail(String introduction, LocalDate birthdate, ProfileVisibility visibility) {}
	
	public record NotificationSetting(boolean pushNotification, boolean emailNotification) {}
	
	public record NicknameUpdate(String nickname) {}
	
	public record PasswordUpdate(String rawPassword, String newPassword) {}
}
