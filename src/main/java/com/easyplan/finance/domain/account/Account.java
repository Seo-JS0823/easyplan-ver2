package com.easyplan.finance.domain.account;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.easyplan._shared.domain.BaseEntity;
import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.account.exception.AccountErrorCode;
import com.easyplan.finance.domain.account.exception.AccountException;
import com.easyplan.finance.domain.ledger.Ledger;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ledger_id", nullable = false, updatable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Ledger ledger;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false, updatable = false)
	private Category category;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "option_id", nullable = false)
	private AccountOption option;
	
	@Embedded
	@AttributeOverride(
			name = "publicId",
			column = @Column(name = "account_public_id", nullable = false, unique = true, updatable = false)
	)
	private PublicId accountPublicId;
	
	@Column(name = "account_name", nullable = false)
	private String accountName;
	
	@Column(name = "account_description")
	private String accountDescription;
	
	@Column(name = "account_type", nullable = false, updatable = false, length = 15)
	@Enumerated(EnumType.STRING)
	private AccountType accountType;
	
	@Column(name = "account_status", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private AccountStatus status;
	
	public static Account create(Ledger ledger, Category category, AccountOption option, String accountName, String accountDescription) {
		Account account = new Account();
		
		account.ledger = ledger;
		account.category = category;
		account.option = option;
		
		account.accountName = accountName;
		account.accountDescription = accountDescription;
		
		account.accountPublicId = PublicId.create();
		account.status = AccountStatus.ACTIVE;
		account.accountType = category.getAccountType();
		
		return account;
	}
	
	public void changeInfo(String accountName, String accountDescription, AccountOption option) {
		validateActive();
		
		this.accountName = accountName;
		this.accountDescription = accountDescription;
		this.option = option;
	}
	
	public void deactivate() {
		this.status = AccountStatus.DEACTIVATE;
	}
	
	public void validateActive() {
		if(this.status != AccountStatus.ACTIVE) {
			throw new AccountException(AccountErrorCode.ACCOUNT_DEACTIVATE);
		}
	}
	
}
