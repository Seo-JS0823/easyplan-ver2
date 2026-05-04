package com.easyplan.finance.domain.account;

import com.easyplan._global.infra.jpa.BaseEntity;
import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.account.exception.AccountException;
import com.easyplan.finance.domain.account.exception.AccountExceptionCode;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {

	@Embedded
	@AttributeOverride(
			name = "publicId",
			column = @Column(name = "account_public_id", nullable = false, unique = true, updatable = false, length = 40)
	)
	private PublicId accountPublicId;
	
	@Column(name = "ledger_id", nullable = false)
	private Long ledgerId;
	
	@Column(name = "category_id", nullable = false, updatable = false)
	private Long categoryId;
	
	@Column(name = "option_id", nullable = false)
	private Long optionId;
	
	@Column(name = "status", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private AccountStatus status;
	
	@Column(name = "account_name", nullable = false)
	private String accountName;
	
	@Column(name = "account_description")
	private String accountDescription;
	
	@Column(name = "payment", nullable = false)
	private boolean payment;
	
	public static Account create(Long ledgerId, Long categoryId, Long optionId, String accountName, String accountDescription, boolean payment) {
		Account account = new Account();
		
		account.accountPublicId = PublicId.create();
		account.ledgerId = ledgerId;
		account.categoryId = categoryId;
		account.optionId = optionId;
		account.accountName = accountName;
		account.accountDescription = accountDescription;
		account.payment = payment;
		
		account.status = AccountStatus.ACTIVE;
		
		return account;
	}
	
	public void changeAccountInfo(String accountName, String accountDescription, Long optionId) {
		isActive();
		
		this.accountName = accountName;
		this.accountDescription = accountDescription;
		this.optionId = optionId;
	}
	
	public void deactivate() {
		if(this.status == AccountStatus.DEACTIVATE) {
			return;
		}
		
		this.status = AccountStatus.DEACTIVATE;
	}
	
	public void reactivate() {
		if(this.status == AccountStatus.ACTIVE) {
			return;
		}
		
		this.status = AccountStatus.ACTIVE;
	}
	
	public void isActive() {
		if(this.status != AccountStatus.ACTIVE) {
			throw new AccountException(AccountExceptionCode.ACCOUNT_NOT_ACTIVE);
		}
	}
	
}
