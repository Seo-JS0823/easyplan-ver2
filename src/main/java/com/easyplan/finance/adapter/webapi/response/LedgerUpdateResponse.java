package com.easyplan.finance.adapter.webapi.response;

import com.easyplan.finance.domain.ledger.Ledger;

public class LedgerUpdateResponse {
	public record LedgerInfoResponse(String ledgerName, String description) {
		public static LedgerInfoResponse of(Ledger ledger) {
			return new LedgerInfoResponse(ledger.getName(), ledger.getDescription());
		}
	}
}
