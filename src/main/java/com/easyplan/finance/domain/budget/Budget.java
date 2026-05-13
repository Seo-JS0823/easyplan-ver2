package com.easyplan.finance.domain.budget;

import java.time.YearMonth;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.easyplan._global.persistence.YearMonthConverter;
import com.easyplan._shared.domain.BaseEntity;
import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.account.AccountType;
import com.easyplan.finance.domain.budget.exception.BudgetErrorCode;
import com.easyplan.finance.domain.budget.exception.BudgetException;
import com.easyplan.finance.domain.budget.request.BudgetCreateRequest;
import com.easyplan.finance.domain.journal.Money;
import com.easyplan.finance.domain.ledger.Ledger;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
		name = "budget",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uk_budget_account_period",
						columnNames = {"account_id", "period"}
				)
		}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Budget extends BaseEntity {
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ledger_id", nullable = false, updatable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Ledger ledger;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false, updatable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Account account;
	
	@Column(name = "period", nullable = false, updatable = false)
	@Convert(converter = YearMonthConverter.class)
	private YearMonth period;
	
	@Embedded
	@AttributeOverride(
			name = "amount",
			column = @Column(name = "limit_amount", nullable = false)
	)
	private Money limitAmount;
	
	public static Budget create(Account account, BudgetCreateRequest budgetCreate) {
		// 예산은 0이하로 설정할 수 없다.
		if(budgetCreate.limitAmount() <= 0) {
			throw new BudgetException(BudgetErrorCode.INVALID_BUDGET_LIMIT_AMOUNT);
		}
		
		// 계정 객체에 Active 상태냐고 물어본다.
		account.validateActive();
		
		// 예산은 EXPENSE 계정에만 설정할 수 있다.
		if(account.getAccountType() != AccountType.EXPENSE) {
			throw new BudgetException(BudgetErrorCode.INVALID_BUDGET_TARGET);
		}
		
		Budget budget = new Budget();
		
		budget.ledger = account.getLedger();
		budget.account = account;
		budget.period = budgetCreate.period();
		budget.limitAmount = new Money(budgetCreate.limitAmount());
		
		return budget;
	}
	
	public void changeLimitAmount(Long limitAmount) {
		if(limitAmount <= 0) {
			throw new BudgetException(BudgetErrorCode.INVALID_BUDGET_LIMIT_AMOUNT);
		}
		
		this.limitAmount = new Money(limitAmount);
	}
	
	public String getAccountName() {
		return this.account.getAccountName();
	}
	
	public Long getAmount() {
		return this.limitAmount.getAmount();
	}
	
}
