package com.easyplan.finance.adapter.webapi.response;

import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.LedgerType;

public record LedgerCreateResponse(String ledgerPublicId, String ledgerName, LedgerType type) {
	public static LedgerCreateResponse of(Ledger ledger) {
		return new LedgerCreateResponse(
				ledger.getLedgerPublicId().publicId(),
				ledger.getName(),
				ledger.getType()
		);
	}
}
