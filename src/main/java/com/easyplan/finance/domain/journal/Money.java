package com.easyplan.finance.domain.journal;

import com.easyplan.finance.domain.journal.exception.JournalErrorCode;
import com.easyplan.finance.domain.journal.exception.JournalException;

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
			throw new JournalException(JournalErrorCode.AMOUNT_ZERO_ERROR);
		}
		
		this.amount = amount;
	}
}
