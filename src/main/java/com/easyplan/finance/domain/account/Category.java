package com.easyplan.finance.domain.account;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.easyplan._shared.domain.BaseEntity;
import com.easyplan.finance.domain.EntrySide;
import com.easyplan.finance.domain.ledger.Ledger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ledger_id", nullable = false, updatable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Ledger ledger;
	
	@Column(name = "account_type", nullable = false, updatable = false, length = 15)
	private AccountType accountType;
	
	public static Category create(Ledger ledger, AccountType accountType) {
		Category category = new Category();
		
		category.ledger = ledger;
		category.accountType = accountType;
		
		return category;
	}
	
	public static List<Category> createDefault(Ledger ledger) {
		AccountType[] types = AccountType.values();
		
		List<Category> categories = new ArrayList<>();
		for(AccountType type : types) {
			categories.add(Category.create(ledger, type));
		}
		
		if(categories.size() != types.length) {
			throw new RuntimeException("기본 카테고리 생성 중 개수 불일치 오류가 발생했습니다.");
		}
		
		return categories;
	}
	
	public EntrySide getEntrySide() {
		return this.accountType.getSide();
	}
	
	public String getCategoryName() {
		return this.accountType.getCategoryName();
	}
}
