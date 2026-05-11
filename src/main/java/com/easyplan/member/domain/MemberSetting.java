package com.easyplan.member.domain;

import com.easyplan._shared.domain.BaseEntity;
import com.easyplan.member.domain.converter.TimeZoneIdConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_setting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSetting extends BaseEntity {
	
	@Column(name = "zone_id", nullable = false)
	@Convert(converter = TimeZoneIdConverter.class)
	private TimeZoneId zoneId;
	
	@Column(name = "push_notification", nullable = false)
	private boolean pushNotification;
	
	@Column(name = "email_notification", nullable = false)
	private boolean emailNotification;
	
	@Column(name = "friend_request", nullable = false)
	private boolean friendRequest;
	
	static MemberSetting create(String zoneId, boolean pushNotification, boolean emailNotification) {
		MemberSetting setting = new MemberSetting();
		
		setting.zoneId = TimeZoneId.of(zoneId.trim());
		setting.pushNotification = pushNotification;
		setting.emailNotification = emailNotification;
		setting.friendRequest = true;
		
		return setting;
	}
	
	void changeNotification(boolean pushNotification, boolean emailNotification) {
		this.pushNotification = pushNotification;
		this.emailNotification = emailNotification;
	}
}
