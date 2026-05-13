package com.easyplan.finance.domain.journal;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {
	private Long amount;
	
	public Money(Long amount) {
		if(amount <= 0) {
			throw new IllegalArgumentException("금액은 0보다 커야합니다.");
		}
		
		this.amount = amount;
	}
}
