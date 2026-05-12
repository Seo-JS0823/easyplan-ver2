package com.easyplan.finance.application.usecase.response.command;

import com.easyplan.finance.domain.ledger.Ledger;
import com.easyplan.finance.domain.ledger.LedgerType;

public class LedgerResponse {
	public record LedgerCreateResponse(
			String ledgerPublicId,
			String ledgerName,
			String ledgerDescription,
			LedgerType ledgerType,
			int accountCount
	) {
		public static LedgerCreateResponse of(Ledger ledger, int accountCount) {
			return new LedgerCreateResponse(
					ledger.getLedgerPublicId().publicId(),
					ledger.getLedgerName(),
					ledger.getLedgerDescription(),
					ledger.getLedgerType(),
					accountCount
			);
		}
	}
	
	public record LedgerInfoUpdateResponse(
			String ledgerName,
			String ledgerDescription
	) {
		public static LedgerInfoUpdateResponse of(Ledger ledger) {
			return new LedgerInfoUpdateResponse(ledger.getLedgerName(), ledger.getLedgerDescription());
		}
	}
	
	public record LedgerFiscalUpdateResponse(
			int fiscalDay
	) {
		public static LedgerFiscalUpdateResponse of(Ledger ledger) {
			return new LedgerFiscalUpdateResponse(ledger.getFiscalDay().getDay());
		}
	}
	
}
