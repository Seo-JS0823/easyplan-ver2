package com.easyplan.member.domain.converter;

import com.easyplan.member.domain.TimeZoneId;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TimeZoneIdConverter implements AttributeConverter<TimeZoneId, String> {

	@Override
	public String convertToDatabaseColumn(TimeZoneId attribute) {
		return attribute == null ? null : attribute.text();
	}

	@Override
	public TimeZoneId convertToEntityAttribute(String dbData) {
		return dbData == null ? null : TimeZoneId.of(dbData);
	}

}
