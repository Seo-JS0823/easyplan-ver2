package com.easyplan.finance.domain.ledger.request;

public class LedgerUpdateRequest {
	public record LedgerInfoUpdate(String name, String description) {}
	
	public record LedgerFiscalUpdate(Integer fiscalDay) {}
}
