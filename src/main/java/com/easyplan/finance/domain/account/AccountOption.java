package com.easyplan.finance.domain.account;

import com.easyplan._shared.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountOption extends BaseEntity {
	
	@Column(name = "option_code", nullable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	private AccountOptionTemplate option;
	
	public static AccountOption create(AccountOptionTemplate option) {
		AccountOption accountOption = new AccountOption();
		
		accountOption.option = option;
		
		return accountOption;
	}
	
	public String getOptionName() {
		return this.option.getOptionName();
	}
	
	public AccountType getAccountType() {
		return this.option.getAccountType();
	}
}
