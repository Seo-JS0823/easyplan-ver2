package com.easyplan.member.domain;

import java.time.ZoneId;

public record TimeZoneId(ZoneId zoneId) {
	public static TimeZoneId of(String value) {
		if(value == null || value.isBlank()) {
			throw new IllegalArgumentException("시간대 정보를 확인할 수 없습니다. 다시 시도해주세요.");
		}
		
		try {
			return new TimeZoneId(ZoneId.of(value));
		} catch(Exception e) {
			throw new IllegalArgumentException("시간대 정보를 확인할 수 없습니다. 다시 시도해주세요.");
		}
	}
	
	public String text() {
		return zoneId.getId();
	}
}
