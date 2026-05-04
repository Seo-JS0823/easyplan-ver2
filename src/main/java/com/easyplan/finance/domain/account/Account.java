package com.easyplan.finance.domain.account;

import com.easyplan._global.infra.jpa.BaseEntity;
import com.easyplan._shared.domain.PublicId;
import com.easyplan.finance.domain.account.exception.AccountException;
import com.easyplan.finance.domain.account.exception.AccountExceptionCode;
import com.easyplan.finance.domain.account.request.AccountCreateRequest;
import com.easyplan.finance.domain.account.request.AccountUpdateRequest.AccountInfoUpdate;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
		name = "account",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uk_category_id_account_name",
						columnNames = {"category_id", "account_name"}
				)
		}
)
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
	
	public static Account create(Long ledgerId, Long categoryId, Long optionId, String accountName, String accountDescription) {
		Account account = new Account();
		
		account.accountPublicId = PublicId.create();
		account.ledgerId = ledgerId;
		account.categoryId = categoryId;
		account.optionId = optionId;
		account.accountName = accountName;
		account.accountDescription = accountDescription;
		
		account.status = AccountStatus.ACTIVE;
		
		return account;
	}
	
	public static Account create(Long ledgerId, Long categoryId, Long optionId, AccountCreateRequest accountCreate) {
		Account account = new Account();
		
		account.accountPublicId = PublicId.create();
		account.ledgerId = ledgerId;
		account.categoryId = categoryId;
		account.optionId = optionId;
		account.accountName = accountCreate.accountName();
		account.accountDescription = accountCreate.accountDescription();
		
		account.status = AccountStatus.ACTIVE;
		
		return account;
	}
	
	public void changeAccountInfo(Long optionId, AccountInfoUpdate accountInfo) {
		isActive();
		
		this.accountName = accountInfo.accountName();
		this.accountDescription = accountInfo.accountDescription();
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
