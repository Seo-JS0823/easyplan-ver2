package com.easyplan.finance.domain.account;

import com.easyplan._global.infra.jpa.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_options")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountOption extends BaseEntity {

	@Column(name = "option_code", nullable = false, updatable = false)
	private String optionCode;
	
	@Column(name = "option_name", nullable = false, length = 15, updatable = false)
	private String optionName;
	
	@Column(name = "account_type", nullable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	private AccountType accountType;
	
	public static AccountOption create(String optionCode, String optionName, AccountType accountType) {
		AccountOption accountOption = new AccountOption();
		
		accountOption.optionCode = optionCode;
		accountOption.optionName = optionName;
		accountOption.accountType = accountType;
		
		return accountOption;
	}
}