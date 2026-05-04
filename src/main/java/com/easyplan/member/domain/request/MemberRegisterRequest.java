package com.easyplan.member.domain.request;

import java.time.LocalDate;

public record MemberRegisterRequest(
		String email,
		String password,
		String nickname,
		String introduction,
		LocalDate birthdate,
		String zoneId,
		boolean pushNotification,
		boolean emailNotification) {

}
