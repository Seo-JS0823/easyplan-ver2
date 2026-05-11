package com.easyplan.finance.domain.journal;

import com.easyplan._shared.domain.BaseEntity;
import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountType;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entry_line")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EntryLine extends BaseEntity {
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "journal_id", nullable = false, updatable = false)
	private Journal journal;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;
	
	@Column(name = "side", nullable = false, length = 10)
	private EntrySide side;
	
	@Embedded
	@AttributeOverride(
			name = "amount",
			column = @Column(name = "amount", nullable = false)
	)
	private Money amount;
	
	public static EntryLine create(Account account, Money amount, EntrySide side) {
		EntryLine line = new EntryLine();
		
		line.account = account;
		
		line.amount = amount;
		line.side = side;
		
		return line;
	}
	
	AccountType getAccountType() {
		return this.account.getCategory().getAccountType();
	}
	
	boolean isDebit() {
		return side == EntrySide.DEBIT;
	}
	
	boolean isCredit() {
		return side == EntrySide.CREDIT;
	}
	
	void linkJournal(Journal journal) {
		this.journal = journal;
	}
	
	void changeAmount(Money amount) {
		this.amount = amount;
	}
	
}
