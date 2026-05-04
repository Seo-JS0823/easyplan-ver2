package com.easyplan.finance.domain.account;

import java.util.ArrayList;
import java.util.List;

import com.easyplan._global.infra.jpa.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountCategory extends BaseEntity {
	
	@Column(name = "ledger_id", nullable = false, updatable = false)
	private Long ledgerId;
	
	@Column(name = "category_name", nullable = false, updatable = false, length = 10)
	private String categoryName;
	
	@Column(name= "account_type", nullable = false, updatable = false, length = 15)
	private AccountType accountType;
	
	@Column(name = "entry_side", nullable = false, updatable = false, length= 10)
	private EntrySide entrySide;
	
	public static AccountCategory create(Long ledgerId, AccountType accountType) {
		AccountCategory accountCategory = new AccountCategory();
		
		accountCategory.ledgerId = ledgerId;
		accountCategory.accountType = accountType;
		accountCategory.categoryName = accountType.getTitle();
		accountCategory.entrySide = accountType.getEntrySide();
		
		return accountCategory;
	}
	
	public static List<AccountCategory> createDefault(Long ledgerId) {
		AccountType[] types = AccountType.values();
		
		List<AccountCategory> accountCategories = new ArrayList<>();
		for(AccountType type : types) {
			accountCategories.add(AccountCategory.create(ledgerId, type));
		}
		
		if(accountCategories.size() != types.length) {
			throw new RuntimeException("기본 카테고리 생성 중 개수 불일치 오류가 발생했습니다.");
		}
		
		return accountCategories;
	}
}
