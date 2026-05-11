package com.easyplan.finance.domain.journal.request;

import java.time.LocalDate;
import java.util.Map;

import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.domain.journal.TransactionType;

public class JournalRequest {
	public record JournalCreateRequest(
			LocalDate transactionDate,
			Long amount,
			String memo,
			TransactionType transactionType,
			Map<EntrySide, String> entries
	) {}
	
	public record JournalUpdateRequest(
			LocalDate transactionDate,
			Long amount,
			String memo,
			Map<EntrySide, String> entries
	) {}
	
	
}
