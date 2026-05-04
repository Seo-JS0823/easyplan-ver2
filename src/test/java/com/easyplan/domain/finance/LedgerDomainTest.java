package com.easyplan.domain.finance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.easyplan.finance.domain.account.AccountBasicTemplate;
import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.LedgerType;
import com.easyplan.finance.domain.ledger.request.LedgerCreateRequest;

public class LedgerDomainTest {
	
	public static LedgerCreateRequest createLedgerRequest() {
		LedgerCreateRequest request = new LedgerCreateRequest(
				LedgerType.PERSONAL,
				"테스트 가계부",
				"테스트를 위한 가계부 입니다.",
				List.of(AccountBasicTemplate.values())
		);
		
		return request;
	}
	
	@Test
	@DisplayName("가계부 생성")
	void ledgerCreate() {
		Ledger ledger = Ledger.create(1L, createLedgerRequest());
		
		assertThat(ledger.getOwnerId()).isEqualTo(1L);
		assertThat(ledger.getType()).isEqualTo(LedgerType.PERSONAL);
		
	}
	
}
