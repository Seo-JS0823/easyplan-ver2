package com.easyplan.finance.application.usecase.response.command;

import com.easyplan.finance.domain.journal.Journal;

public class JournalResponse {
	public record JournalCreateResponse(Long journalId, String message) {
		public static JournalCreateResponse of(Journal journal) {
			return new JournalCreateResponse(journal.getId(), journal.getTransactionType().createdMessage(journal));
		}
	}
	
	public record JournalUpdateResponse(Long journalId, String message) {
		public static JournalUpdateResponse of(Journal journal) {
			return new JournalUpdateResponse(journal.getId(), journal.getTransactionType().updatedMessage(journal));
		}
	}
}