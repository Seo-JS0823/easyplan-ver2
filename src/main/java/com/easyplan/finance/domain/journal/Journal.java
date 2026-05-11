package com.easyplan.finance.domain.journal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.easyplan._shared.domain.BaseEntity;
import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.journal.exception.JournalErrorCode;
import com.easyplan.finance.domain.journal.exception.JournalException;
import com.easyplan.finance.domain.journal.request.JournalRequest.JournalCreateRequest;
import com.easyplan.finance.domain.journal.request.JournalRequest.JournalUpdateRequest;
import com.easyplan.finance.domain.ledger.Ledger;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "journal")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Journal extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ledger_id", nullable = false, updatable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Ledger ledger;
	
	@Embedded
	@AttributeOverride(
			name = "amount",
			column = @Column(name = "amount", nullable = false)
	)
	private Money amount;
	
	@Column(name = "transaction_date", nullable = false)
	private LocalDate transactionDate;
	
	@Column(name = "memo", length = 100)
	private String memo;
	
	@Column(name = "transaction_type", nullable = false, length = 15)
	private TransactionType transactionType;
	
	@OneToMany(mappedBy = "journal", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EntryLine> entries = new ArrayList<>(2);
	
	public static Journal create(Ledger ledger, JournalCreateRequest journalCreate) {
		Journal journal = new Journal();
		
		journal.ledger = ledger;
		
		journal.amount = new Money(journalCreate.amount());
		journal.transactionDate = journalCreate.transactionDate();
		journal.memo = journalCreate.memo();
		journal.transactionType = journalCreate.transactionType();
		
		return journal;
	}
	
	public EntryLine getEntryLine(EntrySide side) {
		switch (side) {
			case CREDIT : return this.entries.stream()
						.filter(line -> line.isCredit())
						.findFirst()
						.orElseThrow(() -> new JournalException(JournalErrorCode.JOURNAL_SYSTEM_ERROR));
			
			case DEBIT : return this.entries.stream()
						.filter(line -> line.isDebit())
						.findFirst()
						.orElseThrow(() -> new JournalException(JournalErrorCode.JOURNAL_SYSTEM_ERROR));
			
			default : throw new JournalException(JournalErrorCode.JOURNAL_SYSTEM_ERROR);
		}
	}
	
	public void addEntryLine(EntryLine line) {
		validateEntryLine();
		
		this.entries.add(line);
		line.linkJournal(this);
	}
	
	public void changeEntryLineWithAmount(List<EntryLine> entries, JournalUpdateRequest journalUpdate) {
		validateEntryLineSave();
		
		this.entries.clear();
		
		for (EntryLine line : entries) {
			addEntryLine(line);
		}
		
		changeAmount(journalUpdate.amount());
	}
	
	public void validateSavable() {
		validateEntryLineSave();
		validateTransactionType();
	}
	
	private void changeAmount(Long amount) {
		this.amount = new Money(amount);
		
		for (EntryLine line : entries) {
			line.changeAmount(this.amount);
		}
		
		validateEntryLineSave();
	}
	
	private void validateEntryLine() {
		if(this.entries.size() >= 2) {
			throw new JournalException(JournalErrorCode.JOURNAL_SYSTEM_ERROR);
		}
	}
	
	private void validateEntryLineSave() {
		if(this.entries.size() != 2) {
			throw new JournalException(JournalErrorCode.JOURNAL_SYSTEM_ERROR);
		}
		
		int debitCount = 0;
		int creditCount = 0;
		
		long debitAmount = 0L;
		long creditAmount = 0L;
		
		for (EntryLine line : entries) {
			if(line.isDebit()) {
				debitCount++;
				debitAmount = line.getAmount().getAmount();
			}
			
			if(line.isCredit()) {
				creditCount++;
				creditAmount = line.getAmount().getAmount();
			}
		}
		
		if(debitCount != 1 || creditCount != 1) {
			throw new JournalException(JournalErrorCode.JOURNAL_SYSTEM_ERROR);
		}
		
		if(debitAmount != creditAmount) {
			throw new JournalException(JournalErrorCode.JOURNAL_SYSTEM_ERROR);
		}
		
		if(this.amount.getAmount() != debitAmount) {
			throw new JournalException(JournalErrorCode.JOURNAL_SYSTEM_ERROR);
		}
	}
	
	private void validateTransactionType() {
		AccountType debit = getEntryLine(EntrySide.DEBIT).getAccountType();
		AccountType credit = getEntryLine(EntrySide.CREDIT).getAccountType();
		
		if(!this.transactionType.isValidPlacement(debit, credit)) {
			throw new JournalException(JournalErrorCode.INVALID_ENTRY_PAIR);
		}
	}
	
}
