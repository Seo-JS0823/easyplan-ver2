package com.easyplan._global.persistence;

import java.time.YearMonth;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class YearMonthConverter implements AttributeConverter<YearMonth, Integer> {

	@Override
	public Integer convertToDatabaseColumn(YearMonth attribute) {
		if(attribute == null) return null;
		
		return attribute.getYear() * 100 + attribute.getMonthValue();
	}

	@Override
	public YearMonth convertToEntityAttribute(Integer dbData) {
		if(dbData == null) return null;
		
		int year = dbData / 100;
		int month = dbData % 100;
		
		return YearMonth.of(year, month);
	}

}
