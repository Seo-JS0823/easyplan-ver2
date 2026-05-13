package com.easyplan.finance.application.required.repository;

import java.time.YearMonth;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan.finance.domain.account.Account;
import com.easyplan.finance.domain.budget.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
	Optional<Budget> findByAccountAndPeriod(Account account, YearMonth period);
}
